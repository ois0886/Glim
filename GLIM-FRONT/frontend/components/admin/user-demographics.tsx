/**
 * @file user-demographics.tsx
 * @description '글:림' 관리자 대시보드에서 사용자 인구통계학적 데이터를 시각적으로 보여주는 컴포넌트.
 *              연령, 성별, 디바이스별 분포 및 월별 사용자 증가 추이를 차트로 표시합니다.
 *              `app/page.tsx`에서 '사용자 통계' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @backend_note
 * - 현재 모든 데이터는 컴포넌트 내부에 하드코딩된 더미 데이터입니다.
 * - **향후 백엔드 연동 시, `useEffect` 훅을 사용하여 백엔드 API에서 실제 데이터를 로드하도록 수정해야 합니다.**
 */
"use client" // 이 파일이 클라이언트 측에서 렌더링되어야 함을 Next.js에 알립니다.

// --- 라이브러리 임포트 ---
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"; // 카드 형태의 UI 컨테이너
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"; // 드롭다운 선택 메뉴
import { Badge } from "@/components/ui/badge"; // 작은 태그/배지
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip } from "recharts"; // 차트 라이브러리

// --- 더미 데이터 정의 (현재는 하드코딩된 데이터 사용) ---
// 연령별 분포 데이터
const ageData = [
  { name: "10대", value: 15, count: 450 },
  { name: "20대", value: 35, count: 1050 },
  { name: "30대", value: 28, count: 840 },
  { name: "40대", value: 15, count: 450 },
  { name: "50대 이상", value: 7, count: 210 },
]

// 성별 분포 데이터
const genderData = [
  { name: "여성", value: 58, count: 1740 },
  { name: "남성", value: 42, count: 1260 },
]

// 월별 사용자 증가 데이터
const monthlyGrowth = [
  { month: "1월", newUsers: 320, totalUsers: 2100 },
  { month: "2월", newUsers: 450, totalUsers: 2550 },
  { month: "3월", newUsers: 380, totalUsers: 2930 },
  { month: "4월", newUsers: 520, totalUsers: 3450 },
]

// 디바이스별 접속 데이터
const deviceData = [
  { name: "모바일", value: 72, count: 2160 },
  { name: "데스크톱", value: 23, count: 690 },
  { name: "태블릿", value: 5, count: 150 },
]

// 차트 색상 팔레트
const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#8884D8"]

/**
 * @file user-demographics.tsx
 * @description 관리자 대시보드에서 서비스에 가입한 사용자들의 인구통계학적 데이터를 시각적으로 보여주는 컴포넌트입니다.
 *              사용자 연령, 성별, 디바이스별 분포 및 월별 사용자 증가 추이를 파이 차트와 바 차트로 표시합니다.
 *
 * @usage
 * 이 컴포넌트는 `app/page.tsx` 파일에서 관리자 대시보드의 '사용자 통계' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @component UserDemographics
 *
 * @structure
 * - `ageData`, `genderData`, `monthlyGrowth`, `deviceData`: 컴포넌트 내부에 하드코딩된 더미 데이터.
 * - `COLORS`: 차트 시각화를 위한 색상 팔레트.
 * - UI 구성:
 *   - 요약 통계 카드: 총 사용자, 신규 사용자, 활성 사용자, 평균 연령 등의 요약 정보를 표시합니다.
 *   - 연령 및 성별 분포 차트: `recharts` 라이브러리를 사용하여 파이 차트로 시각화합니다.
 *   - 월별 사용자 증가 차트: `recharts` 라이브러리를 사용하여 바 차트로 시각화합니다.
 *   - 디바이스별 접속 정보: 목록과 파이 차트로 표시합니다.
 *
 * @backend_interaction
 * - 현재 모든 데이터는 컴포넌트 내부에 하드코딩된 더미 데이터입니다.
 * - **향후 실제 백엔드 API 연동 시, 다음 부분을 수정해야 합니다:**
 *   - `useEffect` 훅을 사용하여 컴포넌트 마운트 시 백엔드 API(예: `/api/admin/analytics/users/demographics`)를 호출하여 실제 데이터를 가져와야 합니다.
 *   - 가져온 실제 데이터로 `ageData`, `genderData`, `monthlyGrowth`, `deviceData`와 같은 상태를 업데이트해야 합니다.
 *   - 백엔드 API의 응답 데이터 구조에 맞게 데이터 매핑 로직을 조정해야 합니다.
 *
 * @notes
 * - `recharts` 라이브러리를 사용하여 다양한 차트(파이 차트, 바 차트)를 구현합니다.
 * - `shadcn/ui`의 `Card`, `Select`, `Badge` 등의 컴포넌트를 활용하여 UI를 구성합니다.
 * - 현재는 정적인 데이터를 사용하므로, `useEffect` 훅이 사용되지 않습니다. 실제 데이터 연동 시 `useEffect`를 추가해야 합니다.
 */

export function UserDemographics() {
  // --- 컴포넌트 렌더링 ---
  return (
    <div className="space-y-6">
      {/* 페이지 헤더 섹션 */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">사용자 통계</h2>
          <p className="text-muted-foreground">사용자 연령, 성별 및 기타 인구통계학적 데이터를 분석합니다.</p>
        </div>
        {/* 기간 선택 드롭다운 (현재는 기능 없음) */}
        <Select defaultValue="all">
          <SelectTrigger className="w-[180px]">
            <SelectValue placeholder="기간 선택" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">전체 기간</SelectItem>
            <SelectItem value="month">이번 달</SelectItem>
            <SelectItem value="quarter">분기</SelectItem>
            <SelectItem value="year">올해</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* 요약 통계 카드 섹션 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        {/* 총 사용자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 사용자</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">3,000</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+12.5%</span> 지난 달 대비
            </p>
          </CardContent>
        </Card>
        {/* 신규 사용자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">신규 사용자</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">520</div>
            <p className="text-xs text-muted-foreground">이번 달</p>
          </CardContent>
        </Card>
        {/* 활성 사용자 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">활성 사용자</CardTitle>
            </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">2,340</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+8.1%</span> 지난 주 대비
            </p>
          </CardContent>
        </Card>
        {/* 평균 연령 카드 */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">평균 연령</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">28.5세</div>
            <p className="text-xs text-muted-foreground">전체 사용자 기준</p>
          </CardContent>
        </Card>
      </div>

      {/* 연령 및 성별 분포 차트 섹션 */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* 연령별 분포 파이 차트 */}
        <Card>
          <CardHeader>
            <CardTitle>연령별 분포</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-between mb-4">
              <ResponsiveContainer width="60%" height={200}>
                <PieChart>
                  <Pie
                    data={ageData} // 연령 데이터
                    cx="50%" // 차트 중앙 X 위치
                    cy="50%" // 차트 중앙 Y 위치
                    innerRadius={40} // 내부 원 반지름
                    outerRadius={80} // 외부 원 반지름
                    paddingAngle={5} // 각 조각 사이의 간격
                    dataKey="value" // 파이 차트의 값을 나타내는 데이터 키
                  >
                    {ageData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} /> // 각 조각의 색상
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => [`${value}%`, "비율"]} /> {/* 툴팁 포맷팅 */}
                </PieChart>
              </ResponsiveContainer>
              {/* 연령별 범례 */}
              <div className="space-y-2">
                {ageData.map((entry, index) => (
                  <div key={entry.name} className="flex items-center gap-2">
                    <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[index % COLORS.length] }} />
                    <span className="text-sm">{entry.name}</span>
                    <Badge variant="outline">{entry.value}%</Badge>
                  </div>
                ))}
              </div>
            </div>
          </CardContent>
        </Card>

        {/* 성별 분포 파이 차트 */}
        <Card>
          <CardHeader>
            <CardTitle>성별 분포</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="flex items-center justify-between mb-4">
              <ResponsiveContainer width="60%" height={200}>
                <PieChart>
                  <Pie
                    data={genderData} // 성별 데이터
                    cx="50%"
                    cy="50%"
                    innerRadius={40}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {genderData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => [`${value}%`, "비율"]} />
                </PieChart>
              </ResponsiveContainer>
              {/* 성별 범례 */}
              <div className="space-y-2">
                {genderData.map((entry, index) => (
                  <div key={entry.name} className="flex items-center gap-2">
                    <div className="w-3 h-3 rounded-full" style={{ backgroundColor: COLORS[index % COLORS.length] }} />
                    <span className="text-sm">{entry.name}</span>
                    <Badge variant="outline">{entry.value}%</Badge>
                  </div>
                ))}
              </div>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* 월별 성장 및 디바이스 분포 섹션 */}
      <div className="grid gap-4 md:grid-cols-2">
        {/* 월별 사용자 증가 바 차트 */}
        <Card>
          <CardHeader>
            <CardTitle>월별 사용자 증가</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={monthlyGrowth}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="newUsers" fill="#8884d8" name="신규 사용자" />
                <Bar dataKey="totalUsers" fill="#82ca9d" name="총 사용자" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        {/* 디바이스별 접속 정보 */}
        <Card>
          <CardHeader>
            <CardTitle>디바이스별 접속</CardTitle>
          </CardHeader>
          <CardContent>
            {/* 디바이스별 목록 */}
            <div className="space-y-4">
              {deviceData.map((device, index) => (
                <div key={device.name} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-4 h-4 rounded-full" style={{ backgroundColor: COLORS[index % COLORS.length] }} />
                    <span className="font-medium">{device.name}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <span className="text-sm text-muted-foreground">{device.count.toLocaleString()}명</span>
                    <Badge variant="outline">{device.value}%</Badge>
                  </div>
                </div>
              ))}
            </div>
            {/* 디바이스별 파이 차트 */}
            <div className="mt-6">
              <ResponsiveContainer width="100%" height={200}>
                <PieChart>
                  <Pie
                    data={deviceData}
                    cx="50%"
                    cy="50%"
                    innerRadius={40}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {deviceData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value) => [`${value}%`, "비율"]} />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}