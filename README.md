# 📘 게시판 API

Spring Boot 기반으로 개발된 **게시판 API**입니다.  
백엔드만 구현 예정이며, Spring Boot를 학습하면서 기능을 차근차근 개발 중입니다.

---

## ⚙️ 기술 스택

- Java 17
- Spring Boot 3.5.3
- Spring Web / Spring Data JPA
- Lombok / Validation
- H2 Database (개발용)
- Postman (API 테스트용)

---

## 🛠️ 진행 상황

| 기능 항목 | 상태 | 비고 |
|-----------|------|------|
| 게시글 CRUD | ✅ | 기본 게시판 기능 |
| 댓글 CRUD | ✅ | 게시글에 종속된 구조 |
| 페이징 | ✅ | 게시글, 댓글 5개씩 페이징 |
| 좋아요 | 🔄️ | 미정 |
| 미정 | ⏳ | 미정 |

---

## 🔗 주요 API 명세

### 📄 게시글

| 메서드 | URI            | 설명             |
|--------|----------------|------------------|
| POST   | `/posts`        | 게시글 등록      |
| GET    | `/posts`        | 게시글 목록 조회 |
| GET    | `/posts/{id}`   | 게시글 단건 조회 |
| GET    | `/posts/paged`  | 게시글 페이징 조회 |
| PUT    | `/posts/{id}`   | 게시글 수정      |
| DELETE | `/posts/{id}`   | 게시글 삭제      |

---

### 💬 댓글

| 메서드 | URI                      | 설명                         |
|--------|--------------------------|------------------------------|
| POST   | `/comment/posts/{postId}` | 게시글에 댓글 등록           |
| GET    | `/comment/posts/{postId}` | 특정 게시글 댓글 전체 조회   |
| GET    | `/comment/{id}`          | 댓글 단건 조회               |
| GET    | `/comment/posts/{postId}/paged` | 특정 게시글 댓글 페이징 조회   |
| PUT    | `/comment/{id}`          | 댓글 수정                    |
| DELETE | `/comment/{id}`          | 댓글 삭제                    |

---
