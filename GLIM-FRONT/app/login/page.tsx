// /app/login/page.tsx (최종 추천 코드)

"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { login } from "@/lib/api/auth"; // ✅ 역할 분리: login 함수는 API 호출과 토큰 저장만 책임집니다.

// ✅ UI/UX 개선을 위한 컴포넌트 임포트
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useToast } from "@/components/ui/use-toast"; // ✅ 사용자 피드백을 위한 Toast 임포트

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false); // ✅ [추가] 로딩 상태 관리
  const router = useRouter();
  const { toast } = useToast(); // ✅ [추가] Toast 기능 사용

  // ✅ [수정] form의 onSubmit 이벤트로 처리하여 접근성 향상 (Enter 키로 로그인 가능)
  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault(); // form의 기본 새로고침 동작 방지
    setIsLoading(true); // 로딩 시작

    try {
      // ✅ [수정] login 함수를 호출하기만 하면 됩니다. 토큰 저장은 login 함수가 알아서 처리합니다.
      await login({ email, password });

      // ✅ [추가] 로그인 성공 시 사용자에게 피드백 제공
      toast({
        title: "✅ 로그인 성공",
        description: "관리자 대시보드로 이동합니다.",
      });

      // 대시보드 메인 페이지로 이동
      router.push("/"); 

    } catch (error: any) {
      console.error("Login failed on page:", error);

      // ✅ [추가] 로그인 실패 시 사용자에게 명확한 에러 메시지 표시
      let description = "아이디 또는 비밀번호를 확인해주세요.";
      if (error.response?.status === 500) {
        description = "서버에 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
      }
      toast({
        title: "❌ 로그인 실패",
        description: description,
        variant: "destructive",
      });
    } finally {
      setIsLoading(false); // 로딩 종료 (성공/실패 모든 경우에)
    }
  };

  return (
    // ✅ [수정] form 태그로 감싸고 onSubmit 이벤트 핸들러 연결
    <form onSubmit={handleLogin}>
      <div className="flex items-center justify-center min-h-screen bg-gray-100">
        <Card className="w-full max-w-sm">
          <CardHeader>
            <CardTitle className="text-2xl">관리자 로그인</CardTitle>
            <CardDescription>
              이메일과 비밀번호를 입력하여 로그인하세요.
            </CardDescription>
          </CardHeader>
          <CardContent className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="email">이메일</Label>
              <Input
                id="email"
                type="email"
                placeholder="admin@example.com"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                disabled={isLoading} // ✅ 로딩 중 입력 비활성화
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="password">비밀번호</Label>
              <Input
                id="password"
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isLoading} // ✅ 로딩 중 입력 비활성화
              />
            </div>
            {/* ✅ [수정] 로딩 상태에 따라 버튼 텍스트 변경 및 비활성화 처리 */}
            <Button type="submit" className="w-full" disabled={isLoading}>
              {isLoading ? "로그인 중..." : "로그인"}
            </Button>
          </CardContent>
        </Card>
      </div>
    </form>
  );
}