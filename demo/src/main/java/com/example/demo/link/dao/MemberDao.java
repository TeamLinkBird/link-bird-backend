package com.example.demo.link.dao;

import com.example.demo.link.dto.MemberDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberDao extends JpaRepository<MemberDto, Long> {
    public List<MemberDto> findById(String id);

    public List<MemberDto> findByName(String id);

    public List<MemberDto> findByNameLike(String id);
}
