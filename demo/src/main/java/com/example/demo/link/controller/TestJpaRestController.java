package com.example.demo.link.controller;

import com.example.demo.link.dto.MemberDto;
import com.example.demo.link.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("memberTest")
public class TestJpaRestController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MemberService memberService;

    @Operation(summary = "tet summary", description = "test description")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST")
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<MemberDto>> getAllMembers(){
        List<MemberDto> member = memberService.findAll();
        return new ResponseEntity<List<MemberDto>>(member, HttpStatus.OK);
    }

    @GetMapping(value = "/{mbrNo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MemberDto> getMember(@PathVariable("mbrNo") Long mbrNo){
        Optional<MemberDto> member = memberService.findById(mbrNo);
        return new ResponseEntity<MemberDto>(member.get(),HttpStatus.OK);
    }

    @DeleteMapping(value = "/{mbrNo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteMember(@PathVariable("mbrNo") Long mbrNo){
        memberService.deleteById(mbrNo);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "/{mbrNo}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MemberDto> updateMember(@PathVariable("mbrNo") Long mbrNo, MemberDto member){
        memberService.updateById(mbrNo,member);
        return new ResponseEntity<MemberDto>(member,HttpStatus.OK);
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<MemberDto> save(@RequestBody MemberDto member){
        return new ResponseEntity<MemberDto>(memberService.save(member), HttpStatus.OK);
    }
}
