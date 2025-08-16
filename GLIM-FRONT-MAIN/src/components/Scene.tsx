"use client";

import React, { Suspense, useRef, useState, useEffect, useMemo } from 'react';
import { Canvas, useFrame, useThree } from '@react-three/fiber';
import { OrbitControls } from '@react-three/drei';
import type { OrbitControls as OrbitControlsImpl } from 'three-stdlib';
import * as THREE from 'three';
// 아래 Content 타입은 사용자 프로젝트에 정의되어 있다고 가정합니다.
// 예: export interface Content { quoteId: number; quoteImage: string; }
import { Content } from '../types/api';
// 'gsap' 모듈을 찾지 못하는 오류를 해결하기 위해 CDN에서 직접 가져옵니다.
import { gsap } from 'gsap';

// --- Stardust 컴포넌트 ---
const Stardust = () => {
    const pointsRef = useRef<THREE.Points>(null!);

    const particles = useMemo(() => {
        const geometry = new THREE.BufferGeometry();
        const count = 20000;
        const positions = new Float32Array(count * 3);

        for (let i = 0; i < count * 3; i++) {
            positions[i] = (Math.random() - 0.5) * 200;
        }

        geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
        return geometry;
    }, []);

    useFrame((state, delta) => {
        if (pointsRef.current) {
            pointsRef.current.rotation.y += delta * 0.02;
        }
    });

    return (
        <points ref={pointsRef}>
            <primitive object={particles} attach="geometry" />
            <pointsMaterial
                attach="material"
                size={0.04}
                color="#ffffff"
                transparent
                opacity={0.6}
                sizeAttenuation
            />
        </points>
    );
};


// --- Book 컴포넌트 ---
const Book: React.FC<{ item: Content; onClick: (mesh: THREE.Mesh) => void }> = ({ item, onClick }) => {
    const meshRef = useRef<THREE.Mesh>(null!);
    // public/images/ 폴더에 이미지가 있다고 가정합니다.
    const imageUrl = `/images/${item.quoteImage}`;
    const texture = useMemo(() => new THREE.TextureLoader().load(imageUrl), [imageUrl]);

    const position = useMemo(() => {
        const radius = 12;
        const phi = Math.acos(2 * Math.random() - 1);
        const theta = Math.random() * 2 * Math.PI;
        const r = Math.cbrt(Math.random()) * radius;

        const x = r * Math.sin(phi) * Math.cos(theta);
        const y = r * Math.sin(phi) * Math.sin(theta);
        const z = r * Math.cos(phi);

        return [x, y, z];
    }, []);

    const rotation = useMemo(() => [
        Math.random() * Math.PI,
        Math.random() * Math.PI,
        Math.random() * Math.PI,
    ], []);

    return (
        <mesh
            ref={meshRef}
            position={position as [number, number, number]}
            rotation={rotation as [number, number, number]}
            onClick={(e) => {
                // 이벤트 버블링을 막아 배경 클릭으로 인식되지 않게 합니다.
                e.stopPropagation();
                onClick(meshRef.current);
            }}
        >
            <planeGeometry args={[1.2, 1.8]} />
            <meshBasicMaterial map={texture} side={THREE.FrontSide} transparent />
        </mesh>
    );
};


const SceneContent: React.FC<SceneContentProps> = ({ data }) => {
    const { camera } = useThree();
    const cameraGroupRef = useRef<THREE.Group>(null!);
    const controlsRef = useRef<OrbitControlsImpl>(null!);

    const [isZoomedIn, setIsZoomedIn] = useState(false);
    const [targetObject, setTargetObject] = useState<THREE.Mesh | null>(null);
    const originalRotation = useRef(new THREE.Quaternion());
    const defaultCameraPosition = useMemo(() => new THREE.Vector3(0, 0, 25), []);

    // 'react-hooks/exhaustive-deps' 경고를 해결하기 위해 의존성 배열에 defaultCameraPosition을 추가했습니다.
    useEffect(() => {
        camera.position.copy(defaultCameraPosition);
        if (cameraGroupRef.current) {
            cameraGroupRef.current.add(camera);
        }
    }, [camera, defaultCameraPosition]);

    useFrame(({ mouse: fiberMouse }) => {
        if (!isZoomedIn) {
            const parallaxX = fiberMouse.x * 0.5;
            const parallaxY = -fiberMouse.y * 0.5;
            if (cameraGroupRef.current) {
                cameraGroupRef.current.position.x += (parallaxX - cameraGroupRef.current.position.x) * 0.02;
                cameraGroupRef.current.position.y += (parallaxY - cameraGroupRef.current.position.y) * 0.02;
            }
        }
        
        if (controlsRef.current) {
            controlsRef.current.update();
        }
    });

    const handleBookClick = (mesh: THREE.Mesh | null) => {
        if (controlsRef.current) {
            gsap.killTweensOf(camera.position);
            gsap.killTweensOf(controlsRef.current.target);
        }
        if(targetObject) gsap.killTweensOf(targetObject.quaternion);

        if (mesh) { // Zoom in
            setIsZoomedIn(true);
            setTargetObject(mesh);
            if (controlsRef.current) controlsRef.current.enabled = false;

            const targetPosition = new THREE.Vector3();
            mesh.getWorldPosition(targetPosition);

            originalRotation.current.copy(mesh.quaternion);
            
            const cameraOffset = new THREE.Vector3(0, 0, 2.5);
            const cameraPosition = cameraOffset.applyMatrix4(mesh.matrixWorld);
            
            const targetRotation = new THREE.Quaternion();
            const tempCamera = new THREE.Object3D();
            tempCamera.position.copy(cameraPosition);
            tempCamera.lookAt(targetPosition);
            targetRotation.copy(tempCamera.quaternion);

            const flipRotation = new THREE.Quaternion().setFromAxisAngle(new THREE.Vector3(0, 1, 0), Math.PI);
            targetRotation.multiply(flipRotation);

            if (controlsRef.current) {
                gsap.to(controlsRef.current.target, { duration: 1.5, x: targetPosition.x, y: targetPosition.y, z: targetPosition.z, ease: 'power3.inOut' });
                gsap.to(camera.position, { duration: 1.5, x: cameraPosition.x, y: cameraPosition.y, z: cameraPosition.z, ease: 'power3.inOut', onComplete: () => { if (controlsRef.current) controlsRef.current.enabled = true; } });
            }
            gsap.to(mesh.quaternion, { duration: 1.5, x: targetRotation.x, y: targetRotation.y, z: targetRotation.z, w: targetRotation.w, ease: 'power3.inOut' });
        
        } else { // Zoom out
            if (isZoomedIn) {
                setIsZoomedIn(false);
                if (controlsRef.current) controlsRef.current.enabled = false;

                if (targetObject) {
                    gsap.to(targetObject.quaternion, { duration: 1.5, x: originalRotation.current.x, y: originalRotation.current.y, z: originalRotation.current.z, w: originalRotation.current.w, ease: 'power3.inOut' });
                }
                setTargetObject(null);

                if (controlsRef.current) {
                    gsap.to(controlsRef.current.target, { duration: 1.5, x: 0, y: 0, z: 0, ease: 'power3.inOut' });
                    gsap.to(camera.position, { duration: 1.5, x: defaultCameraPosition.x, y: defaultCameraPosition.y, z: defaultCameraPosition.z, ease: 'power3.inOut', onComplete: () => { if (controlsRef.current) controlsRef.current.enabled = true; } });
                }
            }
        }
    };

    const handleBackgroundClick = () => {
        handleBookClick(null);
    }

    return (
        <>
            <group ref={cameraGroupRef} />
            <color attach="background" args={['#000000']} />
            <fog attach="fog" args={['#000000', 10, 40]} />
            <ambientLight intensity={1.5} />
            
            <Stardust />

            <group>
                {data.map((item, i) => (
                    <Book key={item.quoteId || i} item={item} onClick={handleBookClick} />
                ))}
            </group>

            <OrbitControls ref={controlsRef} />
            
            {/* 배경 클릭을 감지하기 위한 투명한 평면 */}
            <mesh position={[0,0,-50]} onPointerDown={handleBackgroundClick}>
                <planeGeometry args={[500, 500]} />
                <meshBasicMaterial transparent opacity={0} />
            </mesh>
        </>
    );
};

interface SceneContentProps {
    data: Content[];
}

interface SceneProps {
    data: Content[];
}

const Scene: React.FC<SceneProps> = ({ data }) => {
    return (
        <Canvas camera={{ position: [0, 0, 25], fov: 75 }} flat>
            <Suspense fallback={null}>
                <SceneContent data={data} />
            </Suspense>
        </Canvas>
    );
};

export default Scene;