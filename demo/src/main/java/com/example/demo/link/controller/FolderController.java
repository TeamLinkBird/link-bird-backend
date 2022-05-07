package com.example.demo.link.controller;

import com.example.demo.link.dto.FolderDto;
import com.example.demo.link.service.FolderService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/folders")
@Tag(name = "Folder", description = "Folder 관련 APi")
public class FolderController {

    private final FolderService folderService;

    @Operation(tags = "Folder", summary = "유저의 모든 폴더를 가져옵니다.", description = "")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<FolderDto>> getAllFolders(HttpServletRequest request){
        String userId = (String)request.getAttribute("id");
        //todo userId null일경우 exception 처리하기
        List<FolderDto> folders = folderService.findAll(userId);
        return new ResponseEntity<List<FolderDto>>(folders, HttpStatus.OK);
    }

    @Operation(tags = "Folder", summary = "폴더 번호로 폴더를 조회합니다.", description = "")
    @GetMapping(value = "/{folderCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FolderDto> getFolder(@PathVariable("folderCode") Long folderCode){
        return new ResponseEntity<FolderDto>(folderService.findByFolderCode(folderCode), HttpStatus.OK);
    }

    @Operation(tags = "Folder", summary = "폴더를 저장합니다.", description = "")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FolderDto> save(HttpServletRequest request, @RequestBody FolderDto folderDto){
        String userid = (String) request.getAttribute("id");
        return new ResponseEntity<FolderDto>(folderService.save(folderDto,userid),HttpStatus.OK);
    }

    @Operation(tags = "Folder", summary = "폴더를 수정합니다.", description = "")
    @PutMapping(value = "/{folderCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FolderDto> updateFolder(@PathVariable("folderCode") Long folderCode, @RequestBody FolderDto folderDto){
        folderService.updateByFolderCode(folderCode, folderDto);
        return new ResponseEntity<FolderDto>(folderDto,HttpStatus.OK);
    }

    @Operation(tags = "Folder", summary = "폴더를 삭제합니다.", description = "")
    @DeleteMapping(value = "{folderCode}", produces =  {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteFolder(@PathVariable("folderCode") Long folderCode){
        folderService.deleteByFolderCode(folderCode);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
