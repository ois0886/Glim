/**
 * @file visitor-analytics.tsx
 * @description '글:림' 관리자 대시보드에서 웹사이트 방문자 데이터를 시각적으로 표시하는 컴포넌트.
 *              실시간/오늘 방문자, 페이지뷰, 평균 체류시간 요약 및 방문자 추이 차트를 제공합니다.
 *              `app/page.tsx`에서 '방문자 분석' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @backend_note
 * - 현재 모든 데이터는 `/public/visitor-analytics.json` 파일에서 로드됩니다.
 * - **향후 백엔드 연동 시, `useEffect` 내의 데이터 로드 로직을 백엔드 API 호출로 변경해야 합니다.**
 */
"use client" // 이 파일이 클라이언트 측에서 렌더링되어야 함을 Next.js에 알립니다.

// --- 라이브러리 임포트 ---
import { useState, useEffect } from "react" // React 훅: 상태 관리 및 사이드 이펙트 처리
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card" // 카드 형태의 UI 컨테이너
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select" // 드롭다운 선택 메뉴
import { Badge } from "@/components/ui/badge" // 작은 태그/배지
import { TrendingUp, Users, Eye, Clock } from "lucide-react" // 아이콘 라이브러리 (추세, 사용자, 보기, 시계 아이콘)
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from "recharts" // 차트 라이브러리

// --- 데이터 타입 정의 ---
// 일별 방문자 데이터의 구조
interface DailyVisitorData {
  date: string; // 날짜 (예: "2023-07-20")
  visitors: number; // 방문자 수
  pageViews: number; // 페이지 뷰 수
}

// 주별 방문자 데이터의 구조
interface WeeklyVisitorData {
  week: string; // 주차 (예: "Week 29")
  visitors: number; // 방문자 수
  pageViews: number; // 페이지 뷰 수
}

// 상위 사용자 데이터의 구조
interface TopUserData {
  nickname: string; // 사용자 닉네임
  visits: number; // 방문 횟수
  lastVisit: string; // 마지막 방문일 (예: "2023-07-25")
}

// 상위 페이지 데이터의 구조
interface TopPageData {
  page: string; // 페이지 경로 (예: "/dashboard")
  visits: number; // 방문 횟수
  percentage: number; // 전체 방문 중 차지하는 비율
}

// 전체 방문자 분석 데이터의 구조
interface VisitorAnalyticsData {
  realtimeVisitors: number; // 실시간 방문자 수
  todayVisitors: number; // 오늘 방문자 수
  pageViews: number; // 총 페이지 뷰 수
  avgSessionDuration: string; // 평균 세션 지속 시간 (예: "00:05:30")
  dailyVisitors: DailyVisitorData[]; // 일별 방문자 데이터 배열
  weeklyVisitors: WeeklyVisitorData[]; // 주별 방문자 데이터 배열
  topUsers: TopUserData[]; // 상위 사용자 데이터 배열
  topPages: TopPageData[]; // 상위 페이지 데이터 배열
}

/**
 * @file visitor-analytics.tsx
 * @description 관리자 대시보드에서 웹사이트의 트래픽과 방문자 행동을 분석하여 보여주는 대시보드 컴포넌트입니다.
 *              실시간 방문자, 오늘 방문자, 페이지뷰, 평균 체류시간 등의 요약 정보와 함께,
 *              일별/주별 방문자 추이 차트, 가장 활성화된 사용자, 인기 페이지 목록을 표시합니다.
 *
 * @usage
 * 이 컴포넌트는 `app/page.tsx` 파일에서 관리자 대시보드의 '방문자 분석' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @component VisitorAnalytics
 *
 * @structure
 * - `useState`: 방문자 분석 데이터를 저장하는 상태 (`data`).
 * - `useEffect`: 컴포넌트가 마운트될 때 `/public/visitor-analytics.json` 파일에서 데이터를 비동기적으로 가져옵니다.
 * - UI 구성:
 *   - 요약 통계 카드: 실시간 방문자, 오늘 방문자, 페이지뷰, 평균 체류시간을 표시합니다.
 *   - 방문자 추이 차트: `recharts` 라이브러리를 사용하여 일별 라인 차트와 주별 바 차트를 렌더링합니다.
 *   - 활성 사용자 및 인기 페이지 목록: 상위 사용자 및 페이지 정보를 목록 형태로 표시합니다.
 *
 * @backend_interaction
 * - 현재 모든 방문자 분석 데이터는 `/public/visitor-analytics.json` 파일에서 로드됩니다.
 * - **향후 실제 백엔드 API 연동 시, 다음 부분을 수정해야 합니다:**
 *   - `useEffect` 내의 `fetchData` 함수: `/public/visitor-analytics.json` 대신 백엔드 API 엔드포인트(예: `/api/admin/analytics/visitors`)를 호출하여 실제 데이터를 가져와야 합니다.
 *   - 데이터 구조: 백엔드 API의 응답 데이터 구조에 맞게 `VisitorAnalyticsData` 인터페이스 및 데이터 매핑 로직을 조정해야 합니다.
 *
 * @notes
 * - `recharts` 라이브러리를 사용하여 데이터 시각화를 구현합니다.
 * - `lucide-react`에서 다양한 아이콘을 사용합니다.
 * - `shadcn/ui`의 `Card`, `Select`, `Badge` 등의 컴포넌트를 활용하여 UI를 구성합니다.
 */

export function VisitorAnalytics() {
  // 방문자 분석 데이터를 저장하는 상태. 초기값은 null입니다.
  const [data, setData] = useState<VisitorAnalyticsData | null>(null);

  // 컴포넌트가 처음 화면에 나타날 때 (마운트될 때) 데이터를 가져옵니다.
  useEffect(() => {
    const fetchData = async () => {
      try {
        // `/visitor-analytics.json` 파일에서 데이터를 가져옵니다. (현재는 정적 JSON 파일)
        const response = await fetch("/visitor-analytics.json");
        if (!response.ok) {
          // 응답이 성공적이지 않으면 에러를 발생시킵니다.
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        // JSON 데이터를 파싱하여 상태에 저장합니다.
        const jsonData: VisitorAnalyticsData = await response.json();
        setData(jsonData);
      } catch (error) {
        // 데이터 가져오기 실패 시 콘솔에 에러를 기록합니다.
        console.error("Failed to fetch visitor analytics data:", error);
      }
    };

    fetchData(); // 데이터 가져오기 함수 호출
  }, []); // 빈 배열은 컴포넌트가 마운트될 때 한 번만 실행됨을 의미합니다.

  // 데이터가 로드되지 않았다면 로딩 메시지를 표시합니다.
  if (!data) {
    return <div>Loading analytics data...</div>;
  }

  // 데이터가 로드되면 실제 UI를 렌더링합니다.
  return (
    <div className="space-y-6">
      {/* 페이지 헤더 섹션 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">방문자 분석</h2>
          <p className="text-muted-foreground">실시간 및 과거 방문자 데이터를 확인합니다.</p>
        </div>
        {/* 기간 선택 드롭다운 (현재는 기능 없음) */}
        <Select defaultValue="daily">
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="기간 선택" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="daily">일별</SelectItem>
            <SelectItem value="weekly">주별</SelectItem>
            <SelectItem value="monthly">월별</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 실시간 통계 카드 섹션 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {/* 실시간 방문자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">실시간 방문자</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.realtimeVisitors}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+12%</span> 지난 시간 대비
            </p>
          </CardContent>
        </Card>
        {/* 오늘 방문자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">오늘 방문자</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.todayVisitors}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+8.2%</span> 어제 대비
            </p>
          </CardContent>
        </Card>
        {/* 페이지뷰 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">페이지뷰</CardTitle>
            <Eye className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.pageViews}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+15.3%</span> 어제 대비
            </p>
          </CardContent>
        </Card>
        {/* 평균 체류시간 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">평균 체류시간</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.avgSessionDuration}</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+2.1%</span> 어제 대비
            </p>
          </CardContent>
        </Card>
      </div>

      {/* 방문자 추이 차트 섹션 */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* 일별 방문자 추이 라인 차트 */}
        <Card>
          <CardHeader>
            <CardTitle>일별 방문자 추이</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={data.dailyVisitors}>
                <CartesianGrid strokeDasharray="3 3" /> {/* 그리드 라인 */}
                <XAxis dataKey="date" /> {/* X축 (날짜) */}
                <YAxis /> {/* Y축 (방문자/페이지뷰 수) */}
                <Tooltip /> {/* 마우스 오버 시 정보 표시 */}
                <Line type="monotone" dataKey="visitors" stroke="#8884d8" strokeWidth={2} name="방문자" /> {/* 방문자 라인 */}
                <Line type="monotone" dataKey="pageViews" stroke="#82ca9d" strokeWidth={2} name="페이지뷰" /> {/* 페이지뷰 라인 */}
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 주별 방문자 통계 바 차트 */}
        <Card>
          <CardHeader>
            <CardTitle>주별 방문자 통계</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={data.weeklyVisitors}>
                <CartesianGrid strokeDasharray="3 3" /> {/* 그리드 라인 */}
                <XAxis dataKey="week" /> {/* X축 (주차) */}
                <YAxis /> {/* Y축 (방문자 수) */}
                <Tooltip /> {/* 마우스 오버 시 정보 표시 */}
                <Bar dataKey="visitors" fill="#8884d8" name="방문자" /> {/* 방문자 바 */}
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* 활성 사용자 및 인기 페이지 섹션 */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* 가장 활성화된 사용자 목록 */}
        <Card>
          <CardHeader>
            <CardTitle>가장 활성화된 사용자</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {data.topUsers.map((user, index) => (
                <div key={user.nickname} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    {/* 순위 배지 */}
                    <Badge variant="outline" className="w-6 h-6 rounded-full p-0 flex items-center justify-center">
                      {index + 1}
                    </Badge>
                    <div>
                      <p className="font-medium">{user.nickname}</p>
                      <p className="text-sm text-muted-foreground">{user.lastVisit}</p>
                    </div>
                  </div>
                  <Badge variant="secondary">{user.visits}회</Badge> {/* 방문 횟수 */}
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* 인기 페이지 목록 */}
        <Card>
          <CardHeader>
            <CardTitle>인기 페이지</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {data.topPages.map((page, index) => (
                <div key={page.page} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    {/* 순위 배지 */}
                    <Badge variant="outline" className="w-6 h-6 rounded-full p-0 flex items-center justify-center">
                      {index + 1}
                    </Badge>
                    <div>
                      <p className="font-medium">{page.page}</p>
                      <p className="text-sm text-muted-foreground">{page.visits.toLocaleString()}회 방문</p>
                    </div>
                  </div>
                  <Badge variant="secondary">{page.percentage}%</Badge> {/* 비율 */}
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}