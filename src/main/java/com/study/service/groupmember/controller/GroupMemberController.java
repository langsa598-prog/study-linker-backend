package com.study.service.groupmember.controller;

import com.study.service.groupmember.dto.GroupMemberResponse;
import com.study.service.groupmember.dto.GroupMemberStatusUpdateRequest;
import com.study.service.groupmember.service.GroupMemberService;
import com.study.service.security.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/group-members")
public class GroupMemberController {

    private final GroupMemberService service;

    public GroupMemberController(GroupMemberService service) {
        this.service = service;
    }

    // ============================
    // PATCH /api/group-members/{memberId}
    // 멤버 상태 변경 (관리자만)
    // ============================
    @PatchMapping("/{memberId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long memberId,
            @RequestBody GroupMemberStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            boolean isAdmin = userDetails.isAdmin();
            GroupMemberResponse updated =
                    service.updateStatusAsAdmin(memberId, request.getStatus(), isAdmin);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ============================
    // DELETE /api/group-members/{memberId}
    // 멤버 삭제 (관리자만)
    // ============================
    @DeleteMapping("/{memberId}")
    public ResponseEntity<?> delete(
            @PathVariable Long memberId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        try {
            boolean isAdmin = userDetails.isAdmin();
            service.deleteByIdAsAdmin(memberId, isAdmin);
            return ResponseEntity.ok("멤버가 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}