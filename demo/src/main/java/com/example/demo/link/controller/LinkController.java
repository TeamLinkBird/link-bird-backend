package com.example.demo.link.controller;

import com.example.demo.link.dto.LinkDto;
import com.example.demo.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/links")
public class LinkController {

    private final LinkService linkService;

    @Operation(summary = "Get All Links", description = "모든 링크를 가져옵니다.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LinkDto>> getAllLinks(){
        List<LinkDto> links = linkService.findAll();
        return new ResponseEntity<List<LinkDto>>(links, HttpStatus.OK);
    }

    @Operation(summary = "Get Link", description = "링크를 가져옵니다.")
    @GetMapping(value = "/{linkCode}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LinkDto> getLink(@PathVariable("linkCode")long linkCode){
        return new ResponseEntity<LinkDto>(linkService.findByLinkCode(linkCode),HttpStatus.OK);
    }

    @Operation(summary = "Save Links", description = "링크를 저장합니다.")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LinkDto> save(@RequestBody LinkDto linkDto){
        return new ResponseEntity<LinkDto>(linkService.save(linkDto), HttpStatus.OK);
    }

    @Operation(summary = "Delete Links", description = "링크를 제거합니다.")
    @DeleteMapping(value = "/{linkCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteLink(@PathVariable("linkCode") long linkCode){
        linkService.delete(linkCode);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
