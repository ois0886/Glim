"use client"

import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Edit, Trash2 } from "lucide-react";

interface User {
  memberId: number;
  nickname: string;
  email: string;
  birthDate: string;
  status: "ACTIVE" | "INACTIVE";
  gender: "MALE" | "FEMALE";
}

interface UserTableProps {
  users: User[];
  onEditClick: (user: User) => void;
  onStatusChangeClick: (userId: number) => void;
}

// Force recompile - Gemini
export function UserTable({ users, onEditClick, onStatusChangeClick }: UserTableProps) {
  const getStatusBadge = (status: string) => {
    switch (status) {
      case "ACTIVE":
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            활성
          </Badge>
        );
      case "INACTIVE":
        return <Badge variant="destructive">비활성</Badge>;
      default:
        return <Badge variant="secondary">{status}</Badge>;
    }
  };

  return (
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
        {users.map((user) => (
          <TableRow key={user.memberId}>
            <TableCell className="font-mono">{user.memberId}</TableCell>
            <TableCell className="font-medium">{user.nickname}</TableCell>
            <TableCell>{user.email}</TableCell>
            <TableCell>{user.birthDate}</TableCell>
            <TableCell>{getStatusBadge(user.status)}</TableCell>
            <TableCell>{user.gender === "MALE" ? "남성" : "여성"}</TableCell>
            <TableCell>
              <div className="flex gap-2">
                <Button variant="outline" size="sm" onClick={() => onEditClick(user)}>
                  <Edit className="w-4 h-4" />
                </Button>
                <Button variant="outline" size="sm" onClick={() => onStatusChangeClick(user.memberId)}>
                  <Trash2 className="w-4 h-4" />
                </Button>
              </div>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
