package com.example.demo.link.controller;

import com.example.demo.link.dto.LinkDto;
import com.example.demo.link.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/links")
@Tag(name = "Link", description = "Link 관련 API")
public class LinkController {

    private final LinkService linkService;

    @Operation(tags = "Link", summary = "사용자의 모든 링크를 가져옵니다.", description = "")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<LinkDto>> getAllLinks(HttpServletRequest request){
        String userId = (String)request.getAttribute("id");
        List<LinkDto> links = linkService.findAllByUserId(userId);
        return new ResponseEntity<List<LinkDto>>(links, HttpStatus.OK);
    }

    @Operation(tags = "Link", summary = "링크번호로 링크를 가져옵니다.", description = "")
    @GetMapping(value = "/{linkCode}",produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LinkDto> getLink(@PathVariable("linkCode")long linkCode){
        return new ResponseEntity<LinkDto>(linkService.findByLinkCode(linkCode),HttpStatus.OK);
    }

    @Operation(tags = "Link", summary = "링크를 저장합니다.", description = "")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<LinkDto> save(@RequestBody LinkDto linkDto){
        return new ResponseEntity<LinkDto>(linkService.save(linkDto), HttpStatus.OK);
    }

    @Operation(tags = "Link", summary = "링크를 제거합니다.", description = "")
    @DeleteMapping(value = "/{linkCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteLink(@PathVariable("linkCode") long linkCode){
        linkService.delete(linkCode);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
