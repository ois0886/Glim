'use client'

import { useState, useEffect } from "react";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/admin/ui/card";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/admin/ui/select";
import { ResponsiveSunburst } from '@nivo/sunburst';
import { Users, UserPlus, ArrowUp, ArrowDown } from "lucide-react";

// 데이터 구조에 따른 타입 정의
interface DistributionGroup {
  group: string;
  value: number;
  details?: { [key: string]: { [key: string]: number } };
}

interface GraphicsData {
  totalUsers: number;
  newUserStats: { count: number; change: number };
  activeUsers: { count: number; change: number };
  distribution: {
    age: DistributionGroup[];
    gender: DistributionGroup[];
    device: DistributionGroup[];
  };
  monthlyGrowth: { month: string; users: number }[];
}

// Nivo Sunburst 차트 데이터 형식으로 변환하는 함수
const transformDataForSunburst = (data: GraphicsData) => {
  if (!data) return { id: 'root', children: [] };

  const genderChildren = data.distribution.gender.map(g => ({
    id: g.group,
    value: g.value,
    children: [
      { id: `${g.group}_age_root`, children: Object.keys(g.details.age).map(ageGroup => ({
        id: ageGroup,
        value: g.details.age[ageGroup],
        children: [
          { id: `${g.group}_${ageGroup}_device_root`, children: Object.keys(g.details.device).map(deviceGroup => ({
            id: deviceGroup,
            value: g.details.device[deviceGroup]
          }))}
        ]
      }))}
    ]
  }));

  return {
    id: 'root',
    children: genderChildren,
  };
};

export function UserGraphics() {
  const [data, setData] = useState<GraphicsData | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch('/user-graphics.json');
        if (!response.ok) throw new Error('Failed to fetch data');
        const jsonData = await response.json();
        setData(jsonData);
      } catch (error) {
        console.error("Error fetching user graphics data:", error);
      }
    };
    fetchData();
  }, []);

  if (!data) {
    return <div>Loading...</div>; // 또는 스켈레톤 UI
  }

  const sunburstData = transformDataForSunburst(data);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h2 className="text-2xl font-bold tracking-tight">사용자 통계</h2>
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

      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">총 사용자</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.totalUsers.toLocaleString()}</div>
            <p className="text-xs text-muted-foreground">시스템에 등록된 전체 사용자 수</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">신규 사용자 (30일)</CardTitle>
            <UserPlus className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">+{data.newUserStats.count}</div>
            <p className={`text-xs ${data.newUserStats.change > 0 ? 'text-green-500' : 'text-red-500'} flex items-center`}>
                {data.newUserStats.change > 0 ? <ArrowUp className="h-3 w-3 mr-1"/> : <ArrowDown className="h-3 w-3 mr-1"/>}
                {data.newUserStats.change}%
            </p>
          </CardContent>
        </Card>
         <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">활성 사용자 (30일)</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{data.activeUsers.count.toLocaleString()}</div>
             <p className={`text-xs ${data.activeUsers.change > 0 ? 'text-green-500' : 'text-red-500'} flex items-center`}>
                {data.activeUsers.change > 0 ? <ArrowUp className="h-3 w-3 mr-1"/> : <ArrowDown className="h-3 w-3 mr-1"/>}
                {data.activeUsers.change}%
            </p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>사용자 분포 요약 (성별 &gt; 연령 &gt; 디바이스)</CardTitle>
          <CardDescription>클릭하여 계층을 탐색하세요.</CardDescription>
        </CardHeader>
        <CardContent className="h-[500px]">
          <ResponsiveSunburst
            data={sunburstData}
            id="id"
            value="value"
            cornerRadius={2}
            borderWidth={1}
            borderColor={{ theme: 'background' }}
            colors={{ scheme: 'nivo' }}
            childColor={{ from: 'color', modifiers: [ [ 'brighter', 0.4 ] ] }}
            enableArcLabels={true}
            arcLabelsSkipAngle={10}
            arcLabelsTextColor={{ from: 'color', modifiers: [ [ 'darker', 1.4 ] ] }}
            tooltipFormat={value => `${value.toLocaleString()}명`}
          />
        </CardContent>
      </Card>

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