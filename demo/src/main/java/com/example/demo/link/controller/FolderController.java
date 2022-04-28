package com.example.demo.link.controller;

import com.example.demo.filter.filters.baseFilter;
import com.example.demo.link.dto.FolderDto;
import com.example.demo.link.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
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
public class FolderController {

    private final FolderService folderService;

    @Operation(summary = "Get All Folders", description = "모든 폴더를 조회합니다.")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<FolderDto>> getAllFolders(HttpServletRequest request){
        String userId = (String)request.getAttribute("id");
        //todo userId null일경우 exception 처리하기
        List<FolderDto> folders = folderService.findAll(userId);
        return new ResponseEntity<List<FolderDto>>(folders, HttpStatus.OK);
    }

    @Operation(summary = "Get Folder", description = "폴더 번호로 폴더를 조회합니다.")
    @GetMapping(value = "/{folderCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FolderDto> getFolder(@PathVariable("folderCode") Long folderCode){
        return new ResponseEntity<FolderDto>(folderService.findByFolderCode(folderCode), HttpStatus.OK);
    }

    @Operation(summary = "Save Folder", description = "폴더를 저장합니다.")
    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FolderDto> save(@RequestBody FolderDto folderDto){
        return new ResponseEntity<FolderDto>(folderService.save(folderDto),HttpStatus.OK);
    }

    @Operation(summary = "Update Folder", description = "폴더를 수정합니다.")
    @PutMapping(value = "/{folderCode}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<FolderDto> updateFolder(@PathVariable("folderCode") Long folderCode, @RequestBody FolderDto folderDto){
        folderService.updateByFolderCode(folderCode, folderDto);
        return new ResponseEntity<FolderDto>(folderDto,HttpStatus.OK);
    }

    @Operation(summary = "Delete Folder", description = "폴더를 삭제합니다.")
    @DeleteMapping(value = "{folderCode}", produces =  {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> deleteFolder(@PathVariable("folderCode") Long folderCode){
        folderService.deleteByFolderCode(folderCode);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
