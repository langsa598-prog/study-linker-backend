package com.study.service.repository;

import com.study.service.domain.groupMember.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {}