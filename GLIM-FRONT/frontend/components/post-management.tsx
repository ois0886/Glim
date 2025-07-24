"use client"

import { useState, useEffect } from "react"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Search, Edit, EyeOff, Trash2, Heart } from "lucide-react"

interface Post {
  id: string;
  content: string;
  author: string;
  date: string;
  likes: number;
  status: "visible" | "hidden";
  book: string;
  bookAuthor: string;
}

export function PostManagement() {
  const [searchTerm, setSearchTerm] = useState("")
  const [statusFilter, setStatusFilter] = useState("all")
  const [posts, setPosts] = useState<Post[]>([]);
  const [selectedPost, setSelectedPost] = useState<Post | null>(null)
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false)
  const [editedPost, setEditedPost] = useState<Post | null>(null)

  useEffect(() => {
    const fetchPosts = async () => {
      try {
        const response = await fetch("/posts.json");
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data: Post[] = await response.json();
        setPosts(data);
      } catch (error) {
        console.error("Failed to fetch posts:", error);
      }
    };

    fetchPosts();
  }, []);

  const handleEditClick = (post: Post) => {
    setEditedPost({ ...post });
    setIsEditDialogOpen(true);
  };

  const handleSaveEdit = () => {
    if (editedPost) {
      setPosts(posts.map((post) => (post.id === editedPost.id ? editedPost : post)));
      setIsEditDialogOpen(false);
      setEditedPost(null);
    }
  };

  const filteredPosts = posts.filter((post) => {
    const matchesSearch =
      post.content.toLowerCase().includes(searchTerm.toLowerCase()) ||
      post.author.toLowerCase().includes(searchTerm.toLowerCase()) ||
      post.book.toLowerCase().includes(searchTerm.toLowerCase())
    const matchesStatus = statusFilter === "all" || post.status === statusFilter
    return matchesSearch && matchesStatus
  })

  const getStatusBadge = (status: string) => {
    switch (status) {
      case "visible":
        return (
          <Badge variant="default" className="bg-green-100 text-green-800">
            공개
          </Badge>
        )
      case "hidden":
        return <Badge variant="secondary">숨김</Badge>
      default:
        return <Badge variant="secondary">{status}</Badge>
    }
  }

  const truncateContent = (content: string, maxLength = 80) => {
    return content.length > maxLength ? content.substring(0, maxLength) + "..." : content
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold tracking-tight">게시물 관리</h2>
        <p className="text-muted-foreground">플랫폼의 모든 게시물과 인용구를 관리합니다.</p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>게시물 목록</CardTitle>
          <div className="flex gap-4">
            <div className="relative flex-1">
              <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
              <Input
                placeholder="내용, 작성자, 책 제목으로 검색..."
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
                <SelectItem value="visible">공개</SelectItem>
                <SelectItem value="hidden">숨김</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>게시물 ID</TableHead>
                <TableHead>내용 미리보기</TableHead>
                <TableHead>작성자</TableHead>
                <TableHead>책 정보</TableHead>
                <TableHead>작성일</TableHead>
                <TableHead>좋아요</TableHead>
                <TableHead>상태</TableHead>
                <TableHead>작업</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredPosts.map((post) => (
                <TableRow key={post.id}>
                  <TableCell className="font-mono">{post.id}</TableCell>
                  <TableCell className="max-w-xs">
                    <p className="text-sm">{truncateContent(post.content)}</p>
                  </TableCell>
                  <TableCell className="font-medium">{post.author}</TableCell>
                  <TableCell>
                    <div className="font-bold text-foreground">{post.book}</div>
                    <div className="text-xs">{post.bookAuthor}</div>
                  </TableCell>
                  <TableCell>{post.date}</TableCell>
                  <TableCell>
                    <div className="flex items-center gap-1">
                      <Heart className="w-4 h-4 text-red-500" />
                      {post.likes}
                    </div>
                  </TableCell>
                  <TableCell>{getStatusBadge(post.status)}</TableCell>
                  <TableCell>
                    <div className="flex gap-2">
                      
                      <Button variant="outline" size="sm" onClick={() => handleEditClick(post)}>
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

      {/* Edit Post Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-2xl">
          <DialogHeader>
            <DialogTitle>게시물 편집</DialogTitle>
          </DialogHeader>
          {editedPost && (
            <div className="space-y-4">
              <div>
                <p className="text-sm font-medium">게시물 ID</p>
                <Input value={editedPost.id} disabled />
              </div>
              <div>
                <p className="text-sm font-medium">내용</p>
                <Input
                  value={editedPost.content}
                  onChange={(e) => setEditedPost({ ...editedPost, content: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">작성자</p>
                <Input
                  value={editedPost.author}
                  onChange={(e) => setEditedPost({ ...editedPost, author: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">작성일</p>
                <Input
                  type="date"
                  value={editedPost.date}
                  onChange={(e) => setEditedPost({ ...editedPost, date: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">좋아요</p>
                <Input
                  type="number"
                  value={editedPost.likes}
                  onChange={(e) => setEditedPost({ ...editedPost, likes: parseInt(e.target.value) })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">상태</p>
                <Select
                  value={editedPost.status}
                  onValueChange={(value) => setEditedPost({ ...editedPost, status: value as "visible" | "hidden" })}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="상태 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="visible">공개</SelectItem>
                    <SelectItem value="hidden">숨김</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div>
                <p className="text-sm font-medium">책 제목</p>
                <Input
                  value={editedPost.book}
                  onChange={(e) => setEditedPost({ ...editedPost, book: e.target.value })}
                />
              </div>
              <div>
                <p className="text-sm font-medium">책 저자</p>
                <Input
                  value={editedPost.bookAuthor}
                  onChange={(e) => setEditedPost({ ...editedPost, bookAuthor: e.target.value })}
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
