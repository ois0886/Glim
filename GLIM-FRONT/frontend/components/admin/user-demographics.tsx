"use client"

import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/admin/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/admin/ui/select"
import { Badge } from "@/components/admin/ui/badge"
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip } from "recharts"

// 기존 데이터 유지
const ageData = [
  { name: "10대", value: 15, count: 450 },
  { name: "20대", value: 35, count: 1050 },
  { name: "30대", value: 28, count: 840 },
  { name: "40대", value: 15, count: 450 },
  { name: "50대 이상", value: 7, count: 210 },
]

const genderData = [
  { name: "여성", value: 58, count: 1740 },
  { name: "남성", value: 42, count: 1260 },
]

const monthlyGrowth = [
  { month: "1월", newUsers: 320, totalUsers: 2100 },
  { month: "2월", newUsers: 450, totalUsers: 2550 },
  { month: "3월", newUsers: 380, totalUsers: 2930 },
  { month: "4월", newUsers: 520, totalUsers: 3450 },
]

const deviceData = [
  { name: "모바일", value: 72, count: 2160 },
  { name: "데스크톱", value: 23, count: 690 },
  { name: "태블릿", value: 5, count: 150 },
]

// 각 데이터셋별 색상 팔레트 정의
const COLORS = {
  gender: ["#FF69B4", "#87CEEB"], // 여성, 남성
  age: ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#8884D8"], // 10대, 20대, 30대, 40대, 50대 이상
  device: ["#8884d8", "#82ca9d", "#ffc658"], // 모바일, 데스크톱, 태블릿
}

// 커스텀 범례 컴포넌트
const CustomLegend = ({ title, data, colors }) => (
  <div className="mt-2">
    <h4 className="font-semibold text-sm mb-1">{title}</h4>
    <div className="flex flex-wrap gap-x-4 gap-y-1">
      {data.map((entry, index) => (
        <div key={`legend-${title}-${index}`} className="flex items-center text-xs">
          <span className="w-3 h-3 mr-1.5 rounded-full" style={{ backgroundColor: colors[index % colors.length] }} />
          <span>{entry.name}</span>
        </div>
      ))}
    </div>
  </div>
)

export function UserDemographics() {
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">사용자 통계</h2>
          <p className="text-muted-foreground">사용자 연령, 성별 및 기타 인구통계학적 데이터를 분석합니다.</p>
        </div>
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

      {/* 요약 통계 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
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
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">신규 사용자</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">520</div>
            <p className="text-xs text-muted-foreground">이번 달</p>
          </CardContent>
        </Card>
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

      {/* 통합 동심원 도넛 차트 */}
      <Card>
        <CardHeader>
          <CardTitle>사용자 분포 요약</CardTitle>
          <CardDescription>성별, 연령, 디바이스별 사용자 분포를 나타냅니다.</CardDescription>
        </CardHeader>
        <CardContent className="grid gap-6 md:grid-cols-2 items-center">
          <div className="w-full h-[350px]">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart margin={{ top: 0, right: 0, bottom: 0, left: 0 }}>
                <Tooltip formatter={(value) => [`${value}%`, "비율"]} />

                {/* 가장 안쪽 링: 성별 */}
                <Pie
                  data={genderData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  outerRadius={60}
                  fill="#8884d8"
                  paddingAngle={2}
                >
                  {genderData.map((entry, index) => (
                    <Cell key={`cell-gender-${index}`} fill={COLORS.gender[index % COLORS.gender.length]} />
                  ))}
                </Pie>

                {/* 가운데 링: 연령 */}
                <Pie
                  data={ageData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  innerRadius={70}
                  outerRadius={100}
                  fill="#82ca9d"
                  paddingAngle={2}
                >
                  {ageData.map((entry, index) => (
                    <Cell key={`cell-age-${index}`} fill={COLORS.age[index % COLORS.age.length]} />
                  ))}
                </Pie>

                {/* 가장 바깥쪽 링: 디바이스 */}
                <Pie
                  data={deviceData}
                  dataKey="value"
                  nameKey="name"
                  cx="50%"
                  cy="50%"
                  innerRadius={110}
                  outerRadius={140}
                  fill="#ffc658"
                  paddingAngle={2}
                >
                  {deviceData.map((entry, index) => (
                    <Cell key={`cell-device-${index}`} fill={COLORS.device[index % COLORS.device.length]} />
                  ))}
                </Pie>
              </PieChart>
            </ResponsiveContainer>
          </div>
          <div className="flex flex-col gap-4">
            <CustomLegend title="성별 분포" data={genderData} colors={COLORS.gender} />
            <CustomLegend title="연령별 분포" data={ageData} colors={COLORS.age} />
            <CustomLegend title="디바이스별 접속" data={deviceData} colors={COLORS.device} />
          </div>
        </CardContent>
      </Card>

      {/* 월별 사용자 증가 추이 */}
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
    </div>
  )
}