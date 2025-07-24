"use client"

import { useState, useEffect } from "react"
import { Badge } from "@/components/admin/ui/badge"
import { Button } from "@/components/admin/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/admin/ui/card"
import { Input } from "@/components/admin/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/admin/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/admin/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/admin/ui/select"
import { Search, Edit, Trash2, User } from "lucide-react"

interface User {
  memberId: number;
  nickname: string;
  email: string;
  birthDate: string;
  status: "ACTIVE" | "INACTIVE";
  gender: "MALE" | "FEMALE";
}

export function UserManagement() {
  const [searchTerm, setSearchTerm] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [users, setUsers] = useState<User[]>([])
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [editedUser, setEditedUser] = useState<User | null>(null)

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetch('/users.json');
        if (!response.ok) {
          throw new Error('Failed to load users.json');
        }
        const data = await response.json();
        setUsers(data);
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };

    fetchUsers();
  }, []);

  const filteredUsers = users.filter((user) => {
    const matchesSearch =
      user.nickname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase())
    
    if (statusFilter === 'all') {
        return matchesSearch;
    }
    if (statusFilter === 'active') {
        return matchesSearch && user.status === 'ACTIVE';
    }
    if (statusFilter === 'inactive') {
        return matchesSearch && user.status === 'INACTIVE';
    }
    return matchesSearch;
  });

  const handleEditClick = (user: User) => {
    setEditedUser({ ...user })
    setIsEditDialogOpen(true)
  }

  const handleSaveEdit = async () => {
    if (editedUser) {
        try {
            console.log("Sending user data:", editedUser);
            const response = await fetch(`http://localhost:50850/api/v1/members/${editedUser.memberId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    nickname: editedUser.nickname,
                    birthDate: editedUser.birthDate, // Send as string
                    gender: editedUser.gender,
                }),
            });

            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(`Failed to update user: ${errorBody}`);
            }

            const updatedUser = await response.json();
            console.log("User updated successfully:", updatedUser);
            setUsers(users.map((user) => (user.memberId === updatedUser.memberId ? updatedUser : user)));
            setIsEditDialogOpen(false);
            setEditedUser(null);
        } catch (error) {
            console.error("Failed to save user:", error);
        }
    }
  }

  const handleStatusChangeClick = async (userId: number) => {
    if (window.confirm("이 사용자의 상태를 변경하시겠습니까?")) {
        try {
            console.log("Attempting to change status for user ID:", userId);
            const response = await fetch(`http://localhost:50850/api/v1/members/${userId}/status`, {
                method: 'PATCH',
            });

            if (!response.ok) {
                throw new Error('Failed to update user status');
            }
            const updatedUser = await response.json();
            console.log("User status updated successfully:", updatedUser);
            setUsers(users.map((user) => (user.memberId === updatedUser.memberId ? updatedUser : user)));

        } catch (error) {
            console.error("Failed to change user status:", error);
        }
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            활성
          </Badge>
        )
      case "INACTIVE":
        return <Badge variant="destructive">비활성</Badge>
      default:
        return <Badge variant="secondary">{status}</Badge>
    }
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
                <SelectItem value="inactive">비활성</SelectItem>
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
                <TableHead>생년월일</TableHead>
                <TableHead>상태</TableHead>
                <TableHead>성별</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredUsers.map((user) => (
                <TableRow key={user.memberId}>
                  <TableCell className="font-mono">{user.memberId}</TableCell>
                  <TableCell className="font-medium">{user.nickname}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.birthDate}</TableCell>
                  <TableCell>{getStatusBadge(user.status)}</TableCell>
                  <TableCell>{user.gender === "MALE" ? "남성" : "여성"}</TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      <Button variant="outline" size="sm" onClick={() => handleEditClick(user)}>
                        <Edit className="w-4 h-4" />
                      </Button>
                      <Button variant="outline" size="sm" onClick={() => handleStatusChangeClick(user.memberId)}>
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
                <p className="text-sm font-medium">이메일 (수정 불가)</p>
                <Input
                  value={editedUser.email}
                  disabled
                />
              </div>
              <div>
                <p className="text-sm font-medium">상태</p>
                <Select
                  value={editedUser.status}
                  onValueChange={(value) => setEditedUser({ ...editedUser, status: value as "ACTIVE" | "INACTIVE" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="상태 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="ACTIVE">활성</SelectItem>
                    <SelectItem value="INACTIVE">비활성</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <p className="text-sm font-medium">성별</p>
                <Select
                  value={editedUser.gender}
                  onValueChange={(value) => setEditedUser({ ...editedUser, gender: value as "MALE" | "FEMALE" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="성별 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="MALE">남성</SelectItem>
                    <SelectItem value="FEMALE">여성</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <p className="text-sm font-medium">생일</p>
                <Input
                  type="date"
                  value={editedUser.birthDate}
                  onChange={(e) => setEditedUser({ ...editedUser, birthDate: e.target.value })}
                />
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
