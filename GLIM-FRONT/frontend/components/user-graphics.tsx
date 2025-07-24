"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { Bar, BarChart, CartesianGrid, Pie, PieChart, XAxis, YAxis, Tooltip, Cell, ResponsiveContainer } from "recharts"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Users, MapPin, BarChart3, TrendingUp } from "lucide-react"

// --- 데이터 인터페이스 (기존과 동일) ---
interface AgeData {
  age: string;
  count: number;
}

interface GenderData {
  name: string;
  value: number;
  color: string;
}

interface LocationData {
  rank: number;
  city: string;
  users: number;
  percentage: string;
}

interface DeviceData {
  name: string;
  value: number;
  color: string;
}

// --- 새로 추가된 동심원 차트 컴포넌트 ---
const ConcentricDonutChart = ({ genderData, ageData, deviceData }: {
  genderData: GenderData[],
  ageData: AgeData[],
  deviceData: DeviceData[],
}) => {
  // 연령대별 색상 (기존 데이터에 색상이 없으므로 정의)
  const AGE_COLORS = ["#8884d8", "#82ca9d", "#FFBB28", "#FF8042", "#A4DE6C", "#d0ed57", "#ffc658"];

  // 커스텀 범례 컴포넌트
  const CustomLegend = ({ title, data, colors }: { title: string, data: { name: string, value: number }[], colors: string[] }) => (
    <div className="flex flex-col items-center">
      <h4 className="text-sm font-semibold mb-2">{title}</h4>
      <div className="flex flex-wrap justify-center gap-x-4 gap-y-1 text-xs">
        {data.map((entry, index) => (
          <div key={`legend-${index}`} className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full" style={{ backgroundColor: colors[index % colors.length] }} />
            <span>{entry.name}</span>
          </div>
        ))}
      </div>
    </div>
  );
  
  // recharts 데이터 형식에 맞게 ageData의 key를 name/value로 통일
  const formattedAgeData = ageData.map(d => ({ name: d.age, value: d.count }));

  return (
    <Card className="lg:col-span-2">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <TrendingUp className="h-5 w-5" />
          통합 사용자 분포
        </CardTitle>
        <CardDescription>성별, 연령, 기기별 사용자 분포를 하나의 차트에서 보여줍니다.</CardDescription>
      </CardHeader>
      <CardContent>
        <ChartContainer config={{}} className="h-[350px] w-full">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart margin={{ top: 0, right: 0, bottom: 0, left: 0 }}>
              <Tooltip
                cursor={{ fill: 'hsl(var(--muted))' }}
                content={<ChartTooltipContent 
                    hideLabel
                    formatter={(value, name) => `${name}: ${Number(value).toLocaleString()}명`}
                />}
              />
              {/* 안쪽 원: 성별 */}
              <Pie
                data={genderData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={60}
                innerRadius={35}
              >
                {genderData.map((entry) => (
                  <Cell key={`cell-${entry.name}`} fill={entry.color} />
                ))}
              </Pie>
              {/* 중간 원: 연령 */}
              <Pie
                data={formattedAgeData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={95}
                innerRadius={70}
              >
                {formattedAgeData.map((entry, index) => (
                  <Cell key={`cell-${entry.name}`} fill={AGE_COLORS[index % AGE_COLORS.length]} />
                ))}
              </Pie>
              {/* 바깥 원: 디바이스 */}
              <Pie
                data={deviceData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                outerRadius={130}
                innerRadius={105}
              >
                {deviceData.map((entry) => (
                  <Cell key={`cell-${entry.name}`} fill={entry.color} />
                ))}
              </Pie>
            </PieChart>
          </ResponsiveContainer>
        </ChartContainer>
        
        {/* 커스텀 범례 영역 */}
        <div className="mt-6 space-y-4">
          <CustomLegend title="성별" data={genderData} colors={genderData.map(d => d.color)} />
          <CustomLegend title="연령대" data={formattedAgeData} colors={AGE_COLORS} />
          <CustomLegend title="접속 기기" data={deviceData} colors={deviceData.map(d => d.color)} />
        </div>
      </CardContent>
    </Card>
  );
};


export function UserDemographics() {
  const [ageData, setAgeData] = useState<AgeData[]>([]);
  const [genderData, setGenderData] = useState<GenderData[]>([]);
  const [locationData, setLocationData] = useState<LocationData[]>([]);
  const [deviceData, setDeviceData] = useState<DeviceData[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("/user-graphics.json");
        if (!response.ok) {
          throw new Error('사용자 통계 데이터를 불러오는데 실패했습니다.');
        }
        const data = await response.json();
        setAgeData(data.ageData);
        setGenderData(data.genderData);
        setLocationData(data.locationData);
        setDeviceData(data.deviceData);
      } catch (error) {
        console.error(error);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
      {/* 기존의 분리된 차트들을 새로운 통합 차트로 대체 */}
      <ConcentricDonutChart
        genderData={genderData}
        ageData={ageData}
        deviceData={deviceData}
      />
      
      {/* 연령대별 막대 차트는 시계열 분석 등 다른 목적이 있을 수 있으므로 유지 (제거도 가능) */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <BarChart3 className="h-5 w-5" />
            연령대별 사용자 수
          </CardTitle>
          <CardDescription>서비스를 이용하는 사용자들의 연령대별 절대 수치입니다.</CardDescription>
        </CardHeader>
        <CardContent> 
          <ChartContainer config={{}} className="h-[300px] w-full">
            <BarChart data={ageData} margin={{ top: 20, right: 20, left: -5, bottom: 5 }}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="age" tickLine={false} axisLine={false} />
              <YAxis />
              <Tooltip
                cursor={{ fill: 'hsl(var(--muted))' }}
                content={<ChartTooltipContent
                  formatter={(value) => `${Number(value).toLocaleString()}명`}
                />}
              />
              <Bar dataKey="count" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ChartContainer>
        </CardContent>
      </Card>

      {/* 지역별 사용자 순위는 그대로 유지 */}
      <Card className="lg:col-span-3">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <MapPin className="h-5 w-5" />
            지역별 사용자 순위
          </CardTitle>
          <CardDescription>사용자들이 가장 많이 접속하는 지역 순위입니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead className="w-[80px]">순위</TableHead>
                <TableHead>지역</TableHead>
                <TableHead className="text-right">사용자 수</TableHead>
                <TableHead className="text-right">비율</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {locationData.map((loc) => (
                <TableRow key={loc.rank}>
                  <TableCell className="font-medium">{loc.rank}</TableCell>
                  <TableCell>{loc.city}</TableCell>
                  <TableCell className="text-right">{loc.users.toLocaleString()}명</TableCell>
                  <TableCell className="text-right">{loc.percentage}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  )
}