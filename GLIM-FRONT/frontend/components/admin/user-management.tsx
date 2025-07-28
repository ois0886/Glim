/**
 * @file user-management.tsx
 * @description '글:림' 관리자 대시보드에서 사용자 계정을 관리하는 컴포넌트.
 *              사용자 검색, 상태 필터링, 상세 정보 편집, 상태 변경 기능을 제공합니다.
 *              `app/page.tsx`에서 '사용자 관리' 섹션에 동적으로 로드되어 사용됩니다.
 *
 * @backend_note
 * - 현재 사용자 데이터는 `/public/users.json` 파일에서 로드됩니다.
 * - 사용자 정보 수정 및 상태 변경 기능은 `http://localhost:50850/api/v1/members/{memberId}` 엔드포인트를 호출합니다.
 * - **향후 백엔드 연동 시, 데이터 로드 (`useEffect` 내 `fetchUsers`) 및 수정/삭제 (`handleSaveEdit`, `handleStatusChangeClick`) 로직을
 *   백엔드 API 호출로 변경해야 합니다.**
 */
"use client" // 이 파일이 클라이언트 측에서 렌더링되어야 함을 Next.js에 알립니다.

// --- 라이브러리 임포트 ---
import { useState, useEffect } from "react" // React 훅: 상태 관리 및 사이드 이펙트 처리
import { Badge } from "@/components/ui/badge" // 작은 태그/배지
import { Button } from "@/components/ui/button" // 버튼 컴포넌트
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card" // 카드 형태의 UI 컨테이너
import { Input } from "@/components/ui/input" // 텍스트 입력 필드
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table" // 테이블 컴포넌트
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog" // 다이얼로그(모달) 컴포넌트
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select" // 드롭다운 선택 메뉴
import { Search, Edit, Trash2, User } from "lucide-react" // 아이콘 라이브러리 (검색, 편집, 삭제, 사용자 아이콘)

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
 * - `useEffect`: 컴포넌트가 마운트될 때 `/public/users.json` 파일에서 사용자 데이터를 비동기적으로 가져옵니다.
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
 * - 현재 사용자 데이터는 `/public/users.json` 파일에서 로드됩니다.
 * - 사용자 정보 수정 (`handleSaveEdit`): `http://localhost:50850/api/v1/members/{memberId}` 엔드포인트로 `PUT` 요청을 보냅니다.
 * - 사용자 상태 변경 (`handleStatusChangeClick`): `http://localhost:50850/api/v1/members/{memberId}/status` 엔드포인트로 `PATCH` 요청을 보냅니다.
 * - **향후 실제 백엔드 API 연동 시, 다음 부분을 수정해야 합니다:**
 *   - `useEffect` 내의 `fetchUsers` 함수: `/public/users.json` 대신 백엔드 API 엔드포인트(예: `/api/admin/users`)를 호출하여 실제 사용자 데이터를 가져와야 합니다.
 *   - `handleSaveEdit` 및 `handleStatusChangeClick` 함수: `localhost` 주소를 실제 백엔드 서버 주소로 변경하고, 필요한 인증 토큰 등을 추가해야 합니다.
 *   - 데이터 구조: 백엔드 API의 응답 데이터 구조에 맞게 `User` 인터페이스 및 데이터 매핑 로직을 조정해야 합니다.
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
        // `/users.json` 파일에서 사용자 데이터를 가져옵니다. (현재는 정적 JSON 파일)
        const response = await fetch('/users.json');
        if (!response.ok) {
          throw new Error('Failed to load users.json');
        }
        // JSON 데이터를 파싱하여 사용자 목록 상태에 저장합니다.
        const data = await response.json();
        setUsers(data);
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };

    fetchUsers(); // 사용자 데이터 가져오기 함수 호출
  }, []); // 빈 배열은 컴포넌트가 마운트될 때 한 번만 실행됨을 의미합니다.

  // 검색어와 상태 필터에 따라 사용자 목록을 필터링합니다.
  const filteredUsers = users.filter((user) => {
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
            // 백엔드 API로 PUT 요청을 보내 사용자 정보를 업데이트합니다.
            // 현재는 로컬 개발 서버 주소를 사용합니다.
            const response = await fetch(`http://localhost:50850/api/v1/members/${editedUser.memberId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    nickname: editedUser.nickname,
                    birthDate: editedUser.birthDate, // 문자열 형식으로 전송
                    gender: editedUser.gender,
                }),
            });

            if (!response.ok) {
                const errorBody = await response.text();
                throw new Error(`Failed to update user: ${errorBody}`);
            }

            const updatedUser = await response.json(); // 업데이트된 사용자 정보 응답 받기
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
    if (window.confirm("이 사용자의 상태를 변경하시겠습니까?")) { // 사용자에게 확인 메시지 표시
        try {
            console.log("Attempting to change status for user ID:", userId);
            // 백엔드 API로 PATCH 요청을 보내 사용자 상태를 변경합니다.
            const response = await fetch(`http://localhost:50850/api/v1/members/${userId}/status`, {
                method: 'PATCH',
            });

            if (!response.ok) {
                throw new Error('Failed to update user status');
            }
            const updatedUser = await response.json(); // 업데이트된 사용자 정보 응답 받기
            console.log("User status updated successfully:", updatedUser);
            // 사용자 목록에서 업데이트된 사용자 상태를 반영합니다.
            setUsers(users.map((user) => (user.memberId === updatedUser.memberId ? updatedUser : user)));

        } catch (error) {
            console.error("Failed to change user status:", error);
        }
    }
  };

  // 사용자 상태에 따라 다른 색상의 배지를 반환하는 헬퍼 함수
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
          {/* 사용자 목록 테이블 */}
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
              {/* 필터링된 사용자 목록을 매핑하여 테이블 행으로 렌더링 */}
              {filteredUsers.map((user) => (
                <TableRow key={user.memberId}><TableCell className="font-mono">{user.memberId}</TableCell><TableCell className="font-medium">{user.nickname}</TableCell><TableCell>{user.email}</TableCell><TableCell>{user.birthDate}</TableCell><TableCell>{getStatusBadge(user.status)}</TableCell><TableCell>{user.gender === "MALE" ? "남성" : "여성"}</TableCell><TableCell><div className="flex gap-2"><Button variant="outline" size="sm" onClick={() => handleEditClick(user)}><Edit className="w-4 h-4" /></Button><Button variant="outline" size="sm" onClick={() => handleStatusChangeClick(user.memberId)}><Trash2 className="w-4 h-4" /></Button></div></TableCell></TableRow>
              ))}
            </TableBody>
          </Table>
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