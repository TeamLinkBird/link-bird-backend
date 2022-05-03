package com.example.demo.link.controller;

import com.example.demo.link.dto.TagDto;
import com.example.demo.link.service.TagService;
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
@RequestMapping("/tags")
@RequiredArgsConstructor
@Tag(name = "Tag", description = "Tag 관련 API")
public class TagController {

    private final TagService tagService;

    @Operation(tags = "Tag", summary = "사용자의 모든 태그를 검색합니다.", description = "")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TagDto>> getAllTags(HttpServletRequest request){
        String userId = (String) request.getAttribute("id");
        List<TagDto> tags = tagService.findAllByUserId(userId);
        return new ResponseEntity<List<TagDto>>(tags, HttpStatus.OK);
    }

    @Operation(tags = "Tag", summary = "태그를 검색합니다.", description = "태그이름으로 검색어 추천할 때 사용")
    @GetMapping(value = "/{tagName}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<TagDto>> getTag(@PathVariable("tagName") String tagName, HttpServletRequest request){
        String userId = (String) request.getAttribute("id");
        List<TagDto> tags = tagService.findAllByUserAndTagNameStartingWith(userId, tagName);
        return new ResponseEntity<List<TagDto>>(tags,HttpStatus.OK);
    }

    @Operation(tags = "Tag", summary = "태그를 제거합니다.", description = "")
    @DeleteMapping(value = "/{tagCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteTag(@PathVariable("tagCode")Long tagCode){
        tagService.deleteByTagCode(tagCode);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
