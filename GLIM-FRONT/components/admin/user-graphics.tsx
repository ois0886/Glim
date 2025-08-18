/**
 * @file user-graphics.tsx
 * @description '글:림' 관리자 대시보드에서 사용자 인구통계학적 데이터를 시각적으로 보여주는 컴포넌트.
 *              총 사용자 수, 신규/활성 사용자 통계, 성별/연령/디바이스별 사용자 분포(Sunburst 차트)를 표시합니다.
 *              `app/page.tsx`에서 '사용자 통계' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @backend_note
 * - 현재 모든 데이터는 `/public/user-graphics.json` 파일에서 로드됩니다.
 * - **향후 백엔드 연동 시, `useEffect` 내의 데이터 로드 로직을 백엔드 API 호출로 변경해야 합니다.**
 *   특히, `transformDataForSunburst` 함수는 백엔드에서 제공하는 데이터 구조에 맞춰 수정이 필요할 수 있습니다.
 */
'use client' // 이 파일이 클라이언트 측에서 렌더링되어야 함을 Next.js에 알립니다.

// --- 라이브러리 임포트 ---
import { useState, useEffect } from "react"; // React 훅: 상태 관리 및 사이드 이펙트 처리
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"; // 카드 형태의 UI 컨테이너
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"; // 드롭다운 선택 메뉴
import { ResponsiveSunburst } from '@nivo/sunburst'; // Nivo 라이브러리의 Sunburst 차트 컴포넌트
import { Users, UserPlus, ArrowUp, ArrowDown } from "lucide-react"; // 아이콘 라이브러리 (사용자, 사용자 추가, 화살표 아이콘)

// --- 데이터 타입 정의 ---
// 분포 그룹 (성별, 연령, 디바이스 등)의 구조
interface DistributionGroup {
  group: string; // 그룹 이름 (예: "남성", "20대")
  value: number; // 해당 그룹의 값 (사용자 수)
  details?: { [key: string]: { [key: string]: number } }; // 하위 그룹의 상세 정보 (선택 사항)
}

// 사용자 통계 데이터의 전체 구조
interface GraphicsData {
  totalUsers: number; // 총 사용자 수
  newUserStats: { count: number; change: number }; // 신규 사용자 통계 (수, 변화율)
  activeUsers: { count: number; change: number }; // 활성 사용자 통계 (수, 변화율)
  distribution: {
    age: DistributionGroup[]; // 연령별 분포
    gender: DistributionGroup[]; // 성별 분포
    device: DistributionGroup[]; // 디바이스별 분포
  };
  monthlyGrowth: { month: string; users: number }[]; // 월별 사용자 증가 추이
}

// Nivo Sunburst 차트 데이터 형식으로 변환하는 함수
// Sunburst 차트는 계층적 데이터를 시각화하는 데 사용됩니다.
const transformDataForSunburst = (data: GraphicsData) => {
  if (!data) return { id: 'root', children: [] }; // 데이터가 없으면 빈 루트 반환

  // 성별 데이터를 기반으로 Sunburst 차트의 최상위 계층을 구성합니다.
  const genderChildren = data.distribution.gender.map(g => ({
    id: g.group, // 성별 그룹 (예: "남성", "여성")
    value: g.value, // 해당 성별의 사용자 수
    children: [ // 하위 계층 (연령)
      { id: `${g.group}_age_root`, children: Object.keys(g.details.age).map(ageGroup => ({
        id: ageGroup, // 연령 그룹 (예: "20대", "30대")
        value: g.details.age[ageGroup], // 해당 연령 그룹의 사용자 수
        children: [ // 하위 계층 (디바이스)
          { id: `${g.group}_${ageGroup}_device_root`, children: Object.keys(g.details.device).map(deviceGroup => ({
            id: deviceGroup, // 디바이스 그룹 (예: "모바일", "웹")
            value: g.details.device[deviceGroup] // 해당 디바이스 그룹의 사용자 수
          }))}
        ]
      }))}
    ]
  }));

  // 최종 Sunburst 차트 데이터 구조를 반환합니다.
  return {
    id: 'root', // 최상위 루트 노드
    children: genderChildren, // 성별별 하위 계층
  };
};

/**
 * @file user-graphics.tsx
 * @description 관리자 대시보드에서 서비스에 가입한 사용자들의 인구통계학적 데이터를 시각적으로 보여주는 컴포넌트입니다.
 *              총 사용자 수, 신규/활성 사용자 통계, 성별/연령/디바이스별 사용자 분포(Sunburst 차트),
 *              월별 사용자 증가 추이(현재는 비활성화)를 표시합니다.
 *
 * @usage
 * 이 컴포넌트는 `app/page.tsx` 파일에서 관리자 대시보드의 '사용자 통계' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @component UserGraphics
 *
 * @structure
 * - `useState`: 사용자 통계 데이터를 저장하는 상태 (`data`).
 * - `useEffect`: 컴포넌트가 마운트될 때 `/public/user-graphics.json` 파일에서 데이터를 비동기적으로 가져옵니다.
 * - `transformDataForSunburst`: 가져온 데이터를 Nivo Sunburst 차트 형식에 맞게 변환하는 함수.
 * - UI 구성:
 *   - 요약 통계 카드: 총 사용자, 신규 사용자, 활성 사용자 수를 표시합니다.
 *   - 사용자 분포 Sunburst 차트: 성별, 연령, 디바이스별 사용자 분포를 계층적으로 시각화합니다.
 *   - 월별 사용자 증가 추이 (현재는 비활성화된 주석 처리된 BarChart).
 *
 * @backend_interaction
 * - 현재 모든 사용자 통계 데이터는 `/public/user-graphics.json` 파일에서 로드됩니다.
 * - **향후 실제 백엔드 API 연동 시, 다음 부분을 수정해야 합니다:**
 *   - `useEffect` 내의 `fetchData` 함수: `/public/user-graphics.json` 대신 백엔드 API 엔드포인트(예: `/api/admin/analytics/users/demographics`)를 호출하여 실제 데이터를 가져와야 합니다.
 *   - 데이터 구조: 백엔드 API의 응답 데이터 구조에 맞게 `GraphicsData` 인터페이스 및 `transformDataForSunburst` 함수 내의 데이터 매핑 로직을 조정해야 합니다. 특히, Sunburst 차트의 계층 구조에 맞게 백엔드에서 데이터를 제공하는 것이 중요합니다.
 *
 * @notes
 * - `Nivo` 라이브러리의 `ResponsiveSunburst` 컴포넌트를 사용하여 계층적 데이터를 시각화합니다.
 * - `lucide-react`에서 다양한 아이콘을 사용합니다.
 * - `shadcn/ui`의 `Card`, `Select` 등의 컴포넌트를 활용하여 UI를 구성합니다.
 * - 월별 사용자 증가 추이 차트는 현재 주석 처리되어 있으며, 필요시 `recharts` 라이브러리를 사용하여 구현할 수 있습니다.
 */

export function UserGraphics() {
  // 사용자 통계 데이터를 저장하는 상태. 초기값은 null입니다.
  const [data, setData] = useState<GraphicsData | null>(null);

  // 컴포넌트가 처음 화면에 나타날 때 (마운트될 때) 데이터를 가져옵니다.
  useEffect(() => {
    const fetchData = async () => {
      try {
        // `/user-graphics.json` 파일에서 데이터를 가져옵니다. (현재는 정적 JSON 파일)
        const response = await fetch('/user-graphics.json');
        if (!response.ok) throw new Error('Failed to fetch data'); // 응답이 성공적이지 않으면 에러 발생
        const jsonData = await response.json(); // JSON 데이터를 파싱
        setData(jsonData); // 파싱된 데이터를 상태에 저장
      } catch (error) {
        console.error("Error fetching user graphics data:", error); // 에러 발생 시 콘솔에 기록
      }
    };
    fetchData(); // 데이터 가져오기 함수 호출
  }, []); // 빈 배열은 컴포넌트가 마운트될 때 한 번만 실행됨을 의미합니다.

  // 데이터가 로드되지 않았다면 로딩 메시지를 표시합니다.
  if (!data) {
    return <div>Loading...</div>; // 또는 더 나은 로딩 스켈레톤 UI를 표시할 수 있습니다.
  }

  // Sunburst 차트가 요구하는 형식으로 데이터를 변환합니다.
  const sunburstData = transformDataForSunburst(data);

  // --- 컴포넌트 렌더링 ---
  return (
    <div className="space-y-6">
      {/* 페이지 헤더 섹션 */}
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold tracking-tight">사용자 통계</h2>
        {/* 기간 선택 드롭다운 (현재는 기능 없음) */}
        <Select defaultValue="all-time">
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="기간 선택" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all-time">전체 기간</SelectItem>
            <SelectItem value="monthly">최근 30일</SelectItem>
            <SelectItem value="weekly">최근 7일</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 요약 통계 카드 섹션 */}
      <div className="grid gap-4 md:grid-cols-3">
        {/* 총 사용자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 사용자</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.totalUsers.toLocaleString()}</div> {/* 숫자 포맷팅 */}
            <p className="text-xs text-muted-foreground">시스템에 등록된 전체 사용자 수</p>
          </CardContent>
        </Card>
        {/* 신규 사용자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">신규 사용자 (30일)</CardTitle>
            <UserPlus className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">+{data.newUserStats.count}</div>
            {/* 변화율에 따라 색상 및 아이콘 변경 */}
            <p className={`text-xs ${data.newUserStats.change > 0 ? 'text-green-500' : 'text-red-500'} flex items-center`}>
                {data.newUserStats.change > 0 ? <ArrowUp className="h-3 w-3 mr-1"/> : <ArrowDown className="h-3 w-3 mr-1"/>}
                {data.newUserStats.change}%
            </p>
          </CardContent>
        </Card>
         {/* 활성 사용자 카드 */}
         <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">활성 사용자 (30일)</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.activeUsers.count.toLocaleString()}</div>
             {/* 변화율에 따라 색상 및 아이콘 변경 */}
             <p className={`text-xs ${data.activeUsers.change > 0 ? 'text-green-500' : 'text-red-500'} flex items-center`}>
                {data.activeUsers.change > 0 ? <ArrowUp className="h-3 w-3 mr-1"/> : <ArrowDown className="h-3 w-3 mr-1"/>}
                {data.activeUsers.change}%
            </p>
          </CardContent>
        </Card>
      </div>

      {/* 사용자 분포 Sunburst 차트 섹션 */}
      <Card>
        <CardHeader>
          <CardTitle>사용자 분포 요약 (성별 &gt; 연령 &gt; 디바이스)</CardTitle>
          <CardDescription>클릭하여 계층을 탐색하세요.</CardDescription>
        </CardHeader>
        <CardContent className="h-[500px]">
          {/* Nivo Sunburst 차트 */}
          <ResponsiveSunburst
            data={sunburstData} // 변환된 Sunburst 데이터
            id="id" // 노드의 고유 식별자 키
            value="value" // 노드의 값을 나타내는 키
            cornerRadius={2} // 모서리 둥글기
            borderWidth={1} // 테두리 두께
            borderColor={{ theme: 'background' }} // 테두리 색상
            colors={{ scheme: 'nivo' }} // 색상 스키마
            childColor={{ from: 'color', modifiers: [ [ 'brighter', 0.4 ] ] }} // 자식 노드 색상
            enableArcLabels={true} // 아크 라벨 활성화
            arcLabelsSkipAngle={10} // 라벨이 겹치지 않도록 건너뛸 각도
            arcLabelsTextColor={{ from: 'color', modifiers: [ [ 'darker', 1.4 ] ] }} // 라벨 텍스트 색상
            tooltipFormat={value => `${value.toLocaleString()}명`} // 툴팁 포맷팅
          />
        </CardContent>
      </Card>

      {/* 월별 사용자 증가 추이 섹션 (현재는 비활성화) */}
      <Card>
        <CardHeader>
          <CardTitle>월별 사용자 증가 추이</CardTitle>
        </CardHeader>
        <CardContent>
            {/* BarChart는 Recharts를 사용하므로, 필요시 Recharts import를 다시 추가해야 합니다. */}
            {/* 현재는 Nivo Sunburst에 집중하기 위해 BarChart는 주석 처리합니다. */}
            {/* <ResponsiveContainer width="100%" height={300}>
                <BarChart data={data.monthlyGrowth}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="month" />
                    <YAxis />
                    <Tooltip formatter={(value) => `${value.toLocaleString()}명`} />
                    <Bar dataKey="users" fill="#8884d8" name="월별 총 사용자" />
                </BarChart>
            </ResponsiveContainer> */}
            <p className="text-muted-foreground">월별 사용자 증가 추이 차트는 Nivo Sunburst 차트 구현을 위해 임시로 비활성화되었습니다.</p>
        </CardContent>
      </Card>
    </div>
  );
}
