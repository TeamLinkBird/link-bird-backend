package com.example.demo.link.service;

import com.example.demo.link.dao.MemberDao;
import com.example.demo.link.dto.MemberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    private MemberDao memberDao;

    public List<MemberDto> findAll(){
        List<MemberDto> members = new ArrayList<>();
        memberDao.findAll().forEach(e -> members.add(e));
        return members;
    }

    public Optional<MemberDto> findById(Long mbrNo){
        Optional<MemberDto> member = memberDao.findById(mbrNo);
        return member;
    }

    public void deleteById(Long mbrNo)
    {
        memberDao.deleteById(mbrNo);
    }

    public MemberDto save(MemberDto member)
    {
        memberDao.save(member); return member;
    }
    public void updateById(Long mbrNo, MemberDto member)
    {
        Optional<MemberDto> e = memberDao.findById(mbrNo);
        if (e.isPresent()) { e.get().setMbrNo(member.getMbrNo());
            e.get().setId(member.getId());
            e.get().setName(member.getName());
            memberDao.save(member); }
    }

}
