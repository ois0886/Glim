"use client"

import { useEffect, useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { Bar, BarChart, CartesianGrid, Pie, PieChart, XAxis, YAxis, Tooltip, Legend, Cell } from "recharts"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Users, MapPin, BarChart3 } from "lucide-react"

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
      <Card className="lg:col-span-2">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <BarChart3 className="h-5 w-5" />
            연령대별 사용자 분포
          </CardTitle>
          <CardDescription>서비스를 이용하는 사용자들의 연령대 분포입니다.</CardDescription>
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
                  formatter={(value) => `${value.toLocaleString()}명`}
                  labelFormatter={(label) => `${label}세`}
                />}
              />
              <Bar dataKey="count" fill="hsl(var(--primary))" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ChartContainer>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="h-5 w-5" />
            성별 분포
          </CardTitle>
          <CardDescription>사용자들의 성별 비율을 나타냅니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <ChartContainer config={{}} className="h-[300px] w-full">
            <PieChart>
              <Tooltip
                cursor={{ fill: 'hsl(var(--muted))' }}
                content={<ChartTooltipContent
                  formatter={(value, name, props) => {
                    const total = genderData.reduce((acc, cur) => acc + cur.value, 0);
                    return [`${value.toLocaleString()}명 (${((Number(value) / total) * 100).toFixed(1)}%)`, name];
                  }}
                  nameKey="name"
                />}
              />
              <Pie data={genderData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} label>
                {genderData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Legend />
            </PieChart>
          </ChartContainer>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Users className="h-5 w-5" />
            접속 기기 분포
          </CardTitle>
          <CardDescription>사용자들이 주로 접속하는 기기 유형입니다.</CardDescription>
        </CardHeader>
        <CardContent>
          <ChartContainer config={{}} className="h-[300px] w-full">
            <PieChart>
              <Tooltip
                cursor={{ fill: 'hsl(var(--muted))' }}
                content={<ChartTooltipContent
                  formatter={(value, name, props) => {
                    const total = deviceData.reduce((acc, cur) => acc + cur.value, 0);
                    return [`${value.toLocaleString()}명 (${((Number(value) / total) * 100).toFixed(1)}%)`, name];
                  }}
                  nameKey="name"
                />}
              />
              <Pie data={deviceData} dataKey="value" nameKey="name" cx="50%" cy="50%" outerRadius={100} label>
                {deviceData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Legend />
            </PieChart>
          </ChartContainer>
        </CardContent>
      </Card>

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
