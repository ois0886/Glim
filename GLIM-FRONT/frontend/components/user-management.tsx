"use client"

import { useState } from "react"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Search, Edit, Ban, Trash2, Shield, User } from "lucide-react"

const sampleUsers = [
  {
    id: "U001",
    nickname: "책벌레김씨",
    email: "bookworm@email.com",
    joinDate: "2024-01-15",
    status: "active",
    userType: "normal",
    recentActivity: "2시간 전 로그인",
    totalPosts: 45,
    gender: "male",
    birthday: "1990-05-10",
  },
  {
    id: "U002",
    nickname: "문학소녀",
    email: "literature@email.com",
    joinDate: "2024-02-20",
    status: "active",
    userType: "admin",
    recentActivity: "30분 전 로그인",
    totalPosts: 12,
    gender: "female",
    birthday: "1992-11-22",
  },
  {
    id: "U003",
    nickname: "시인의마음",
    email: "poet@email.com",
    joinDate: "2024-03-10",
    status: "banned",
    userType: "normal",
    recentActivity: "3일 전 로그인",
    totalPosts: 23,
    gender: "male",
    birthday: "1988-01-01",
  },
  {
    id: "U004",
    nickname: "독서광",
    email: "reader@email.com",
    joinDate: "2024-01-05",
    status: "active",
    userType: "normal",
    recentActivity: "1일 전 로그인",
    totalPosts: 67,
    gender: "female",
    birthday: "1995-07-18",
  },
  {
    id: "U005",
    nickname: "글귀수집가",
    email: "collector@email.com",
    joinDate: "2024-02-28",
    status: "active",
    userType: "normal",
    recentActivity: "5시간 전 로그인",
    totalPosts: 89,
    gender: "male",
    birthday: "1985-03-30",
  },
]

interface User {
  id: string;
  nickname: string;
  email: string;
  joinDate: string;
  status: "active" | "banned";
  userType: "normal" | "admin";
  recentActivity: string;
  totalPosts: number;
  gender: "male" | "female" | "other";
  birthday: string;
}

export function UserManagement() {
  const [searchTerm, setSearchTerm] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [users, setUsers] = useState<User[]>(sampleUsers) // sampleUsers를 useState로 관리
  const [selectedUser, setSelectedUser] = useState<User | null>(null)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [editedUser, setEditedUser] = useState<User | null>(null)

  const filteredUsers = users.filter((user) => {
    const matchesSearch =
      user.nickname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesStatus = statusFilter === "all" || user.status === statusFilter
    return matchesSearch && matchesStatus
  })

  const handleEditClick = (user: (typeof sampleUsers)[0]) => {
    setEditedUser({ ...user })
    setIsEditDialogOpen(true)
  }

  const handleSaveEdit = () => {
    if (editedUser) {
      setUsers(users.map((user) => (user.id === editedUser.id ? editedUser : user)))
      setIsEditDialogOpen(false)
      setEditedUser(null)
    }
  }

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "active":
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            활성
          </Badge>
        )
      case "banned":
        return <Badge variant="destructive">차단됨</Badge>
      default:
        return <Badge variant="secondary">{status}</Badge>
    }
  }

  const getUserTypeBadge = (userType: string) => {
    return userType === "admin" ? (
      <Badge variant="outline" className="bg-blue-50 text-blue-700">
        <Shield className="w-3 h-3 mr-1" />
        관리자
      </Badge>
    ) : (
      <Badge variant="outline">
        <User className="w-3 h-3 mr-1" />
        일반
      </Badge>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">사용자 관리</h2>
        <p className="text-muted-foreground">플랫폼 사용자를 관리하고 모니터링합니다.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>사용자 목록</CardTitle>
          <div className="flex gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="닉네임 또는 이메일로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            <Select value={statusFilter} onValueChange={setStatusFilter}>
              <SelectTrigger className="w-[180px]">
                <SelectValue placeholder="상태 필터" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">전체</SelectItem>
                <SelectItem value="active">활성</SelectItem>
                <SelectItem value="banned">차단됨</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>사용자 ID</TableHead>
                <TableHead>닉네임</TableHead>
                <TableHead>이메일</TableHead>
                <TableHead>가입일</TableHead>
                <TableHead>상태</TableHead>
                <TableHead>유형</TableHead>
                <TableHead>성별</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredUsers.map((user) => (
                <TableRow key={user.id}>
                  <TableCell className="font-mono">{user.id}</TableCell>
                  <TableCell className="font-medium">{user.nickname}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.joinDate}</TableCell>
                  <TableCell>{getStatusBadge(user.status)}</TableCell>
                  <TableCell>{getUserTypeBadge(user.userType)}</TableCell>
                  <TableCell>{user.gender === "male" ? "남성" : user.gender === "female" ? "여성" : "기타"}</TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      
                      <Button variant="outline" size="sm" onClick={() => handleEditClick(user)}>
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button variant="outline" size="sm">
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Edit User Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>사용자 편집</DialogTitle>
          </DialogHeader>
          {editedUser && (
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium">닉네임</p>
                <Input
                  value={editedUser.nickname}
                  onChange={(e) => setEditedUser({ ...editedUser, nickname: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">이메일</p>
                <Input
                  value={editedUser.email}
                  onChange={(e) => setEditedUser({ ...editedUser, email: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">상태</p>
                <Select
                  value={editedUser.status}
                  onValueChange={(value) => setEditedUser({ ...editedUser, status: value as "active" | "banned" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="상태 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="active">활성</SelectItem>
                    <SelectItem value="banned">차단됨</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <p className="text-sm font-medium">유형</p>
                <Select
                  value={editedUser.userType}
                  onValueChange={(value) => setEditedUser({ ...editedUser, userType: value as "normal" | "admin" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="유형 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="normal">일반</SelectItem>
                    <SelectItem value="admin">관리자</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <p className="text-sm font-medium">성별</p>
                <Select
                  value={editedUser.gender}
                  onValueChange={(value) => setEditedUser({ ...editedUser, gender: value as "male" | "female" | "other" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="성별 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="male">남성</SelectItem>
                    <SelectItem value="female">여성</SelectItem>
                    <SelectItem value="other">기타</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <p className="text-sm font-medium">생일</p>
                <Input
                  type="date"
                  value={editedUser.birthday}
                  onChange={(e) => setEditedUser({ ...editedUser, birthday: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">총 게시물</p>
                <Input value={editedUser.totalPosts} disabled />
              </div>
              <div>
                <p className="text-sm font-medium">최근 활동</p>
                <Input value={editedUser.recentActivity} disabled />
              </div>
              <div className="flex justify-end gap-2">
                <Button variant="outline" onClick={() => setIsEditDialogOpen(false)}>
                  취소
                </Button>
                <Button onClick={handleSaveEdit}>저장</Button>
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>
    </div>
  )
}
