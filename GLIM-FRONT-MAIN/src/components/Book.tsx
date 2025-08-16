import React, { useState, useRef } from 'react';
import { useLoader, useFrame } from '@react-three/fiber';
import * as THREE from 'three';
import { TextureLoader } from 'three';
import { Content } from '../types/api';

// Define the component's props interface
interface BookProps {
  position: THREE.Vector3;
  item: Content;
  onClick: (item: Content, mesh: THREE.Mesh) => void;
  imageUrl: string;
}

const Book: React.FC<BookProps> = ({ position, item, onClick, imageUrl }) => {
  // TextureLoader는 한 번만 생성하는 것이 좋습니다.
  const texture = useLoader(TextureLoader, imageUrl);

  // Type the mesh ref
  const meshRef = useRef<THREE.Mesh>(null!);

  const [isHovered, setIsHovered] = useState<boolean>(false);

  // Smoothly animate scale on hover
  useFrame(() => {
    if (meshRef.current) {
      meshRef.current.scale.lerp(
        new THREE.Vector3(1, 1, 1).multiplyScalar(isHovered ? 1.2 : 1),
        0.1
      );
    }
  });

  return (
    <mesh
      ref={meshRef}
      position={position}
      onPointerOver={(e) => {
        e.stopPropagation();
        setIsHovered(true);
      }}
      onPointerOut={() => setIsHovered(false)}
      onClick={() => onClick(item, meshRef.current)}
    >
      <planeGeometry args={[1.2, 1.8]} />
      <meshBasicMaterial map={texture} side={THREE.DoubleSide} transparent opacity={0.9} />
    </mesh>
  );
}

export default Book;