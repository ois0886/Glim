import React, { useState, useEffect } from 'react';
import { getCurations, ApiCuration } from './path/to/your/curation'; // 파일 경로에 맞게 수정

const CurationComponent = () => {
  const [curations, setCurations] = useState<ApiCuration[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchAndSortCurations = async () => {
      try {
        // 1. API를 통해 서버로부터 큐레이션 목록을 받습니다.
        const fetchedCurations = await getCurations();

        // 2. 클라이언트에서 순서를 변경합니다.
        // 예를 들어, curationItemId를 기준으로 내림차순(최신 항목이 위로)으로 정렬
        const sortedCurations = [...fetchedCurations].sort((a, b) => b.curationItemId - a.curationItemId);
        
        // 정렬된 결과를 state에 저장합니다.
        setCurations(sortedCurations);

      } catch (error) {
        console.error("큐레이션을 불러오는 데 실패했습니다:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchAndSortCurations();
  }, []); // 컴포넌트가 처음 마운트될 때 한 번만 실행

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  // 3. 정렬된 순서대로 화면에 렌더링합니다.
  return (
    <div>
      {curations.map(curation => (
        <div key={curation.curationItemId}>
          <h2>{curation.title}</h2>
          <p>{curation.description}</p>
        </div>
      ))}
    </div>
  );
};

export default CurationComponent;



// [수정됨] 특정 큐레이션 상세 조회
export const getCurationById = async (id: string): Promise<ApiCuration> => {
  // ... 현재 코드는 전체 목록을 받은 후 클라이언트에서 찾습니다 ...
  const allCurations = await getCurations();
  const curation = allCurations.find(c => c.curationItemId === parseInt(id, 10));
  // ...
};


// [개선안] 특정 큐레이션 상세 조회
export const getCurationById = async (id: string): Promise<ApiCuration> => {
  // 서버에서 직접 특정 ID의 큐레이션을 요청합니다.
  const response = await axiosInstance.get(`/api/v1/admin/curations/items/${id}`);
  return response.data;
};