/**
 * @file user-management.tsx
 * @description '글:림' 관리자 대시보드에서 사용자 계정을 관리하는 컴포넌트.
 *              사용자 검색, 상태 필터링, 상세 정보 편집, 상태 변경 기능을 제공합니다.
 *              `app/page.tsx`에서 '사용자 관리' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @backend_note
 * - 사용자 데이터는 백엔드 API (`/api/v1/admin/members`)에서 로드됩니다.
 * - 사용자 정보 수정 및 상태 변경 기능은 `/api/v1/members/{memberId}` 엔드포인트를 호출합니다.
 */
"use client" // 이 파일이 클라이언트 측에서 렌더링되어야 함을 Next.js에 알립니다.

// --- 라이브러리 임포트 ---
import { useState, useEffect } from "react" // React 훅: 상태 관리 및 사이드 이펙트 처리
import axiosInstance from "@/lib/axiosInstance"; // axios 인스턴스 임포트
import { Badge } from "@/components/ui/badge" // 작은 태그/배지
import { Button } from "@/components/ui/button" // 버튼 컴포넌트
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card" // 카드 형태의 UI 컨테이너
import { Input } from "@/components/ui/input" // 텍스트 입력 필드
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog" // 다이얼로그(모달) 컴포넌트
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select" // 드롭다운 선택 메뉴
import { Search, Edit, Trash2, User } from "lucide-react" // 아이콘 라이브러리 (검색, 편집, 삭제, 사용자 아이콘)
import { UserTable } from "@/components/admin/user-table"; // UserTable 컴포넌트 임포트

// --- 데이터 타입 정의 ---
// 사용자 데이터의 구조
interface User {
  memberId: number; // 사용자 고유 ID
  nickname: string; // 사용자 닉네임
  email: string; // 사용자 이메일
  birthDate: string; // 생년월일 (문자열 형식)
  status: "ACTIVE" | "INACTIVE"; // 사용자 상태 (활성 또는 비활성)
  gender: "MALE" | "FEMALE"; // 성별 (남성 또는 여성)
}

/**
 * @file user-management.tsx
 * @description 관리자 대시보드에서 서비스에 가입한 모든 사용자의 목록을 보고 관리하는 페이지입니다.
 *              사용자 검색, 상태 필터링, 상세 정보 편집, 상태 변경 등의 기능을 제공합니다.
 *
 * @usage
 * 이 컴포넌트는 `app/page.tsx` 파일에서 관리자 대시보드의 '사용자 관리' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @component UserManagement
 *
 * @structure
 * - `useState`: 검색어, 상태 필터, 사용자 목록, 편집 다이얼로그 상태, 편집 중인 사용자 정보 등을 관리하는 상태.
 * - `useEffect`: 컴포넌트가 마운트될 때 백엔드 API에서 사용자 데이터를 비동기적으로 가져옵니다.
 * - `filteredUsers`: 검색어와 상태 필터에 따라 사용자 목록을 필터링하는 계산된 속성.
 * - `handleEditClick`: 사용자 편집 다이얼로그를 열고 편집할 사용자 정보를 설정합니다.
 * - `handleSaveEdit`: 편집된 사용자 정보를 백엔드 API를 통해 저장합니다.
 * - `handleStatusChangeClick`: 사용자의 상태를 변경하는 함수 (활성/비활성).
 * - `getStatusBadge`: 사용자 상태에 따라 다른 색상의 배지를 반환하는 헬퍼 함수.
 * - UI 구성:
 *   - 검색 입력 필드 및 상태 필터 드롭다운.
 *   - 사용자 목록을 표시하는 테이블.
 *   - 각 사용자 항목에 대한 편집 및 상태 변경 버튼.
 *   - 사용자 상세 정보 편집을 위한 다이얼로그(모달).
 *
 * @backend_interaction
 * - 사용자 데이터 로드 (`useEffect` 내 `fetchUsers`): `/api/v1/admin/members`를 호출하여 사용자 데이터를 가져옵니다.
 * - 사용자 정보 수정 (`handleSaveEdit`): `/api/v1/members/{memberId}` 엔드포인트로 `PUT` 요청을 보냅니다.
 * - 사용자 상태 변경 (`handleStatusChangeClick`): `/api/v1/members/{memberId}/status` 엔드포인트로 `PATCH` 요청을 보냅니다.
 * - 데이터 구조: 백엔드 API의 응답 데이터 구조에 맞게 `User` 인터페이스 및 데이터 매핑 로직을 조정해야 합니다.
 *
 * @notes
 * - `shadcn/ui`의 `Card`, `Input`, `Select`, `Table`, `Dialog`, `Badge`, `Button` 등의 컴포넌트를 활용하여 UI를 구성합니다.
 * - `lucide-react`에서 다양한 아이콘을 사용합니다.
 * - `useEffect`를 사용하여 컴포넌트 마운트 시 데이터를 한 번만 가져오도록 설정합니다.
 */

export function UserManagement() {
  // 검색어 입력 필드의 값을 관리하는 상태
  const [searchTerm, setSearchTerm] = useState("")
  // 사용자 상태 필터링을 위한 선택 값을 관리하는 상태 ("all", "active", "inactive")
  const [statusFilter, setStatusFilter] = useState("all")
  // 사용자 목록 데이터를 저장하는 상태
  const [users, setUsers] = useState<User[]>([])
  // 사용자 편집 다이얼로그(모달)의 열림/닫힘 상태를 관리하는 상태
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  // 현재 편집 중인 사용자 정보를 저장하는 상태
  const [editedUser, setEditedUser] = useState<User | null>(null)

  // 컴포넌트가 처음 화면에 나타날 때 (마운트될 때) 사용자 데이터를 가져옵니다.
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await axiosInstance.get('/api/v1/members');
        console.log("API Response (raw):", response);
        console.log("API Response (data):", response.data);
        console.log("API Response (data.data):", response.data.data);
        setUsers(response.data || []); // 데이터가 null이나 undefined일 경우 빈 배열로 설정
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };

    fetchUsers(); // 사용자 데이터 가져오기 함수 호출
  }, []); // 빈 배열은 컴포넌트가 마운트될 때 한 번만 실행됨을 의미합니다.

  // 검색어와 상태 필터에 따라 사용자 목록을 필터링합니다.
  const filteredUsers = users.filter((user) => {
    console.log("Filtering user:", user);
    // 닉네임 또는 이메일에 검색어가 포함되는지 확인
    const matchesSearch =
      user.nickname.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase())
    
    // 상태 필터에 따라 추가 필터링
    if (statusFilter === 'all') {
        return matchesSearch; // 모든 상태 표시
    }
    if (statusFilter === 'active') {
        return matchesSearch && user.status === 'ACTIVE'; // 활성 사용자만 표시
    }
    if (statusFilter === 'inactive') {
        return matchesSearch && user.status === 'INACTIVE'; // 비활성 사용자만 표시
    }
    return matchesSearch; // 기본적으로 검색어만 일치하는 경우
  });

  // 사용자 편집 버튼 클릭 시 호출되는 함수
  const handleEditClick = (user: User) => {
    setEditedUser({ ...user }) // 편집할 사용자 정보를 상태에 복사하여 설정
    setIsEditDialogOpen(true) // 편집 다이얼로그 열기
  }

  // 사용자 정보 저장 버튼 클릭 시 호출되는 함수
  const handleSaveEdit = async () => {
    if (editedUser) { // 편집 중인 사용자 정보가 있을 경우
        try {
            console.log("Sending user data:", editedUser);
            const birthDateArray = editedUser.birthDate.split('-').map(Number);
            birthDateArray.push(0, 0); // 시, 분 추가

            // 백엔드 API로 PUT 요청을 보내 사용자 정보를 업데이트합니다.
            const response = await axiosInstance.put(`/api/v1/admin/members/${editedUser.memberId}`, {
                nickname: editedUser.nickname,
                birthDate: birthDateArray, // 숫자 배열 형식으로 전송
                gender: editedUser.gender,
            });

            const updatedUser = response.data; // 업데이트된 사용자 정보 응답 받기
            console.log("User updated successfully:", updatedUser);
            // 사용자 목록에서 업데이트된 사용자 정보를 반영합니다.
            setUsers(users.map((user) => (user.memberId === updatedUser.memberId ? updatedUser : user)));
            setIsEditDialogOpen(false); // 다이얼로그 닫기
            setEditedUser(null); // 편집 중인 사용자 정보 초기화
        } catch (error) {
            console.error("Failed to save user:", error);
        }
    }
  }

  // 사용자 상태 변경 버튼 클릭 시 호출되는 함수
  const handleStatusChangeClick = async (userId: number) => {
    if (window.confirm("이 사용자를 삭제하시겠습니까?")) { // 사용자에게 확인 메시지 표시
        try {
            console.log("Attempting to change status for user ID:", userId);
            // 백엔드 API로 PATCH 요청을 보내 사용자 상태를 변경합니다.
            const response = await axiosInstance.patch(`/api/v1/members/${userId}/status`);

            const updatedUser = response.data; // 업데이트된 사용자 정보 응답 받기
            console.log("User status updated successfully:", updatedUser);
            // 사용자 목록에서 업데이트된 사용자 상태를 반영합니다.
            setUsers(users.map((user) => (user.memberId === updatedUser.memberId ? updatedUser : user)));

        } catch (error) {
            console.error("Failed to change user status:", error);
        }
    }
  };

  // 사용자 상태에 따라 다른 색상의 배지를 반환하는 헬퍼 함수
  // 이 함수는 이제 UserTable 컴포넌트 내부로 이동했습니다.
  // const getStatusBadge = (status: string) => {
  //   switch (status) {
  //     case "ACTIVE":
  //       return (
  //         <Badge variant="default" className="bg-green-100 text-green-800">
  //           활성
  //         </Badge>
  //       )
  //     case "INACTIVE":
  //       return <Badge variant="destructive">비활성</Badge>
  //     default:
  //       return <Badge variant="secondary">{status}</Badge>
  //   }
  // }

  // --- 컴포넌트 렌더링 ---
  return (
    <div className="space-y-6">
      {/* 페이지 제목 및 설명 */}
      <div>
        <h2 className="text-2xl font-bold tracking-tight">사용자 관리</h2>
        <p className="text-muted-foreground">플랫폼 사용자를 관리하고 모니터링합니다.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>사용자 목록</CardTitle>
          <div className="flex gap-4">
            {/* 검색 입력 필드 */}
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="닉네임 또는 이메일로 검색..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-8"
              />
            </div>
            {/* 상태 필터 드롭다운 */}
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
          <UserTable
            users={filteredUsers}
            onEditClick={handleEditClick}
            onStatusChangeClick={handleStatusChangeClick}
          />
        </CardContent>
      </Card>

      {/* 사용자 편집 다이얼로그 (모달) */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-md">
          <DialogHeader>
            <DialogTitle>사용자 편집</DialogTitle>
          </DialogHeader>
          {editedUser && ( // 편집 중인 사용자 정보가 있을 때만 내용 표시
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
                  disabled // 이메일은 수정 불가능하도록 비활성화
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