// 파일 경로: src/components/Scene.tsx

'use client';

import React, { Suspense, useRef, useState, useEffect, useMemo } from 'react';
import { Canvas, useFrame, useThree } from '@react-three/fiber';
import { OrbitControls, useTexture } from '@react-three/drei';
import type { OrbitControls as OrbitControlsImpl } from 'three-stdlib';
import * as THREE from 'three';
import { a, useSpring } from '@react-spring/three';
import gsap from 'gsap';

// --- 데이터 타입 및 API 훅 ---
import { Content } from '../types/api';
import useApiData from '../hooks/useApiData';

// ------------------------------
// 별 배경 컴포넌트
// ------------------------------
const Stardust: React.FC = () => {
    const pointsRef = useRef<THREE.Points>(null);
    useFrame((_, delta) => {
        if (pointsRef.current) pointsRef.current.rotation.y += delta * 0.02;
    });

    const particles = useMemo(() => {
        const geometry = new THREE.BufferGeometry();
        const positions = new Float32Array(5000 * 3).map(() => (Math.random() - 0.5) * 100);
        geometry.setAttribute('position', new THREE.BufferAttribute(positions, 3));
        return geometry;
    }, []);

    return (
        <points ref={pointsRef} geometry={particles}>
            <pointsMaterial size={0.05} color="#ffffff" transparent opacity={0.6} />
        </points>
    );
};

// ------------------------------
// 개별 책(이미지) 컴포넌트
// ------------------------------
const Book: React.FC<{
    item: Content;
    position: [number, number, number];
    rotation: [number, number, number];
    onClick: () => void;
    isOutOfFocus: boolean;
}> = ({ item, position, rotation, onClick, isOutOfFocus }) => {
    const [hovered, setHovered] = useState(false);

    const imageUrl = useMemo(() => {
        const imageName = item.quoteImage?.split('/').pop() || item.quoteImage;
        if (!imageName) return '/images/books/placeholder.png';
        return `/images/books/${imageName}`;
    }, [item.quoteImage, item.quoteImage]);

    const texture = useTexture(imageUrl);
    const { gl } = useThree();

    // 텍스처 품질 및 좌우 반전 방지
    texture.wrapS = THREE.RepeatWrapping;
    texture.repeat.x = 1;

    // 색공간 (three r154+ / r153- 모두 안전)
    const anyTex = texture as any;
    const anyTHREE = THREE as any;
    if ('colorSpace' in anyTex && anyTHREE.SRGBColorSpace !== undefined) {
        anyTex.colorSpace = anyTHREE.SRGBColorSpace; // r154+
    } else if ('encoding' in anyTex && anyTHREE.sRGBEncoding !== undefined) {
        anyTex.encoding = anyTHREE.sRGBEncoding;     // r153-
    }
    // 품질
    texture.anisotropy = gl?.capabilities?.getMaxAnisotropy?.() ?? 1;

    const { scale, opacity } = useSpring({
        scale: hovered && !isOutOfFocus ? 1.2 : 1,
        opacity: isOutOfFocus ? 0.15 : 1,
        config: { tension: 300, friction: 20 },
    });

    useEffect(() => {
        document.body.style.cursor = hovered ? 'pointer' : 'auto';
        return () => {
            document.body.style.cursor = 'auto';
        };
    }, [hovered]);

    return (
        <a.mesh
            position={position}
            rotation={rotation}
            scale={scale}
            onClick={(e) => {
                e.stopPropagation();
                onClick();
            }}
            onPointerOver={(e) => {
                e.stopPropagation();
                setHovered(true);
            }}
            onPointerOut={() => setHovered(false)}
        >
            <boxGeometry args={[1, 1.5, 0.1]} />
            {/* 옆면/뒷면 색 */}
            <a.meshBasicMaterial attach="material-0" color="#f0e6d6" transparent opacity={opacity} />
            <a.meshBasicMaterial attach="material-1" color="#f0e6d6" transparent opacity={opacity} />
            <a.meshBasicMaterial attach="material-2" color="#f0e6d6" transparent opacity={opacity} />
            <a.meshBasicMaterial attach="material-3" color="#f0e6d6" transparent opacity={opacity} />
            {/* 앞/뒤 표지 텍스처 */}
            <a.meshBasicMaterial attach="material-4" map={texture} transparent opacity={opacity} toneMapped={false} />
            <a.meshBasicMaterial attach="material-5" map={texture} transparent opacity={opacity} toneMapped={false} />
        </a.mesh>
    );
};

// ------------------------------
// 3D 씬의 핵심 로직
// ------------------------------
const SceneContent: React.FC<{ data: Content[] }> = ({ data }) => {
    // 전환 관리용 ref
    useEffect(() => {
        const heroContent = document.querySelector('.hero-content');
        if (heroContent) {
            gsap.set(heroContent, { opacity: 1, pointerEvents: 'all' });
            gsap.to(heroContent, { duration: 1.0, opacity: 0, pointerEvents: 'none', delay: 3.0 });
        }
    }, []);

    const isTweeningRef = useRef(false);
    const activeTlRef = useRef<gsap.core.Timeline | null>(null);

    // 클릭 연타 방지
    const lastClickAtRef = useRef(0);
    const CLICK_GAP = 180; // ms

    const { camera, size } = useThree();
    const controlsRef = useRef<OrbitControlsImpl | null>(null);
    const booksGroupRef = useRef<THREE.Group | null>(null);
    const [focusedIndex, setFocusedIndex] = useState<number | null>(null);

    // 초기 카메라 포지션 (줌아웃 복귀점)
    const initialCameraPosition = useMemo(() => new THREE.Vector3(0, 0, 10), []);

    // 클릭 핸들러(디바운스 적용)
    const handleBookClick = (index: number) => {
        const now = performance.now();
        if (now - lastClickAtRef.current < CLICK_GAP) return;
        lastClickAtRef.current = now;
        setFocusedIndex((prev) => (prev === index ? null : index));
    };

    const handleBackgroundClick = () => {
        const now = performance.now();
        if (now - lastClickAtRef.current < CLICK_GAP) return;
        lastClickAtRef.current = now;
        setFocusedIndex(null);
    };

    // 구체 표면에 고르게 배치 (사진이 적을수록 반지름을 줄여 밀도를 높임)
    const bookTransforms = useMemo(() => {
        const dynamicRadius = data.length < 50 ? 7 : 10;
        return data.map((item, index) => {
            const phi = Math.acos(-1 + (2 * index) / data.length);
            const theta = Math.sqrt(data.length * Math.PI) * phi;
            const posVec = new THREE.Vector3().setFromSpherical(
                new THREE.Spherical(dynamicRadius + (Math.random() - 0.5) * 8, phi, theta)
            );
            const tempObject = new THREE.Object3D();
            tempObject.position.copy(posVec);
            tempObject.lookAt(0, 0, 0);
            return {
                id: `${item.quoteId ?? 'item'}-${index}`,
                position: posVec.toArray() as [number, number, number],
                rotation: [tempObject.rotation.x, tempObject.rotation.y, tempObject.rotation.z] as [
                    number,
                    number,
                    number
                ],
            };
        });
    }, [data]);

    // 포커스 이동/복귀 애니메이션 (항상 "타임라인 1개"만 운용)
    useEffect(() => {
        const controls = controlsRef.current;
        const group = booksGroupRef.current;
        if (!controls || !group) return;

        // 이전 타임라인/트윈 제거
        if (activeTlRef.current) {
            activeTlRef.current.kill();
            activeTlRef.current = null;
        }
        gsap.killTweensOf([camera.position, controls.target]);

        controls.enabled = false;
        isTweeningRef.current = true;

        // 목표 지점 계산
        let camTarget = initialCameraPosition.clone();
        let lookTarget = new THREE.Vector3(0, 0, 0);

        if (focusedIndex !== null && group.children[focusedIndex]) {
            const focusedObject = group.children[focusedIndex];
            const bookWorld = new THREE.Vector3();
            focusedObject.getWorldPosition(bookWorld);

            // 책(1 x 1.5)을 화면에 꽉 차게 맞추기 위한 거리 계산
            const planeHeight = 1.5;
            const planeWidth = 1;
            const vfov = THREE.MathUtils.degToRad(camera.fov);
            const aspect = size.width / size.height;
            const hfov = 2 * Math.atan(Math.tan(vfov / 2) * aspect);

            const distForH = (planeHeight / 2) / Math.tan(vfov / 2);
            const distForW = (planeWidth / 2) / Math.tan(hfov / 2);
            const distance = Math.min(distForH, distForW);

            const offset = bookWorld.clone().sub(lookTarget).normalize().multiplyScalar(distance);
            camTarget = bookWorld.clone().add(offset);
            lookTarget = bookWorld.clone();
        }

        // 타임라인 1개만 생성 (새 전환 오면 위에서 즉시 교체)
        const tl = gsap.timeline({
            defaults: { duration: 1.6, ease: 'expo.inOut', overwrite: 'auto' },
            smoothChildTiming: true,
            onUpdate: () => {
                controls.update();
            },
            onComplete: () => {
                controls.enabled = true;
                isTweeningRef.current = false;
                activeTlRef.current = null;
            },
        });

        tl.to(camera.position, { x: camTarget.x, y: camTarget.y, z: camTarget.z }, 0).to(
            controls.target,
            { x: lookTarget.x, y: lookTarget.y, z: lookTarget.z },
            0
        );

        activeTlRef.current = tl;
    }, [focusedIndex, camera, size, initialCameraPosition]);

    // 회전(포커스 없을 때만 느리게 배경 회전)
    useFrame((_, delta) => {
        if (focusedIndex === null && booksGroupRef.current) {
            booksGroupRef.current.rotation.y += delta * 0.01;
        }
    });

    return (
        <>
            <color attach="background" args={['#000000']} />
            <fog attach="fog" args={['#000000', 15, 40]} />
            <ambientLight intensity={1.5} />
            <Stardust />

            <group ref={booksGroupRef}>
                {data.map((item, i) => (
                    <Book
                        key={bookTransforms[i].id}
                        item={item}
                        onClick={() => handleBookClick(i)}
                        position={bookTransforms[i].position}
                        rotation={bookTransforms[i].rotation}
                        isOutOfFocus={focusedIndex !== null && focusedIndex !== i}
                    />
                ))}
            </group>

            <OrbitControls
                ref={controlsRef}
                enableDamping
                dampingFactor={0.04}
                enablePan={false}
                minDistance={1.0}
                maxDistance={30}
            />

            {/* 배경 클릭 캡처용 투명 판 */}
            <mesh position={[0, 0, -5]} onClick={handleBackgroundClick} renderOrder={-1}>
                <planeGeometry args={[200, 200]} />
                <meshBasicMaterial visible={false} />
            </mesh>
        </>
    );
};

// ------------------------------
// 최종 렌더링 컴포넌트
// ------------------------------
const Scene: React.FC = () => {
    const { data, loading, error } = useApiData();

    const displayData = useMemo(() => {
        if (!data || data.length === 0) return [];
        // 랜덤 셔플
        return [...data].sort(() => Math.random() - 0.5);
    }, [data]);

    const MessageOverlay: React.FC<{ color: string; children: React.ReactNode }> = ({
        color,
        children,
    }) => (
        <div
            style={{
                position: 'absolute',
                top: '50%',
                left: '50%',
                transform: 'translate(-50%, -50%)',
                color,
                textAlign: 'center',
                zIndex: 100,
            }}
        >
            {children}
        </div>
    );

    if (loading) return <MessageOverlay color="white">Loading Cosmos...</MessageOverlay>;
    if (error) return <MessageOverlay color="red">Error: {error.message}</MessageOverlay>;
    if (displayData.length === 0) return <MessageOverlay color="orange">표시할 데이터가 없습니다.</MessageOverlay>;

    return (
        <div style={{ position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh', background: '#000' }}>
            <Canvas camera={{ position: [0, 0, 18], fov: 75 }} flat>
                <Suspense fallback={null}>
                    <SceneContent data={displayData} />
                </Suspense>
            </Canvas>
        </div>
    );
};

export default Scene;
