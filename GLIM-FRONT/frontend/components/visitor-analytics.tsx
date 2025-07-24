"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Badge } from "@/components/ui/badge"
import { TrendingUp, Users, Eye, Clock } from "lucide-react"
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from "recharts"



interface DailyVisitorData {
  date: string;
  visitors: number;
  pageViews: number;
}

interface WeeklyVisitorData {
  week: string;
  visitors: number;
  pageViews: number;
}

interface TopUserData {
  nickname: string;
  visits: number;
  lastVisit: string;
}

interface TopPageData {
  page: string;
  visits: number;
  percentage: number;
}

interface VisitorAnalyticsData {
  dailyVisitors: DailyVisitorData[];
  weeklyVisitors: WeeklyVisitorData[];
  topUsers: TopUserData[];
  topPages: TopPageData[];
}

export function VisitorAnalytics() {
  const [data, setData] = useState<VisitorAnalyticsData | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("/visitor-analytics.json");
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const jsonData: VisitorAnalyticsData = await response.json();
        setData(jsonData);
      } catch (error) {
        console.error("Failed to fetch visitor analytics data:", error);
      }
    };

    fetchData();
  }, []);

  if (!data) {
    return <div>Loading analytics data...</div>;
  }
  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold tracking-tight">방문자 분석</h2>
          <p className="text-muted-foreground">실시간 및 과거 방문자 데이터를 확인합니다.</p>
        </div>
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

      {/* 실시간 통계 카드 */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">실시간 방문자</CardTitle>
            <Users className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">234</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+12%</span> 지난 시간 대비
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">오늘 방문자</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">1,890</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+8.2%</span> 어제 대비
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">페이지뷰</CardTitle>
            <Eye className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">5,200</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+15.3%</span> 어제 대비
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">평균 체류시간</CardTitle>
            <Clock className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">4m 32s</div>
            <p className="text-xs text-muted-foreground">
              <span className="text-green-600">+2.1%</span> 어제 대비
            </p>
          </CardContent>
        </Card>
      </div>

      {/* 방문자 추이 차트 */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>일별 방문자 추이</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <LineChart data={data.dailyVisitors}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="date" />
                <YAxis />
                <Tooltip />
                <Line type="monotone" dataKey="visitors" stroke="#8884d8" strokeWidth={2} name="방문자" />
                <Line type="monotone" dataKey="pageViews" stroke="#82ca9d" strokeWidth={2} name="페이지뷰" />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>주별 방문자 통계</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={300}>
              <BarChart data={data.weeklyVisitors}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="week" />
                <YAxis />
                <Tooltip />
                <Bar dataKey="visitors" fill="#8884d8" name="방문자" />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>
      </div>

      {/* 활성 사용자 및 인기 페이지 */}
      <div className="grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>가장 활성화된 사용자</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {data.topUsers.map((user, index) => (
                <div key={user.nickname} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Badge variant="outline" className="w-6 h-6 rounded-full p-0 flex items-center justify-center">
                      {index + 1}
                    </Badge>
                    <div>
                      <p className="font-medium">{user.nickname}</p>
                      <p className="text-sm text-muted-foreground">{user.lastVisit}</p>
                    </div>
                  </div>
                  <Badge variant="secondary">{user.visits}회</Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>인기 페이지</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {data.topPages.map((page, index) => (
                <div key={page.page} className="flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <Badge variant="outline" className="w-6 h-6 rounded-full p-0 flex items-center justify-center">
                      {index + 1}
                    </Badge>
                    <div>
                      <p className="font-medium">{page.page}</p>
                      <p className="text-sm text-muted-foreground">{page.visits.toLocaleString()}회 방문</p>
                    </div>
                  </div>
                  <Badge variant="secondary">{page.percentage}%</Badge>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
