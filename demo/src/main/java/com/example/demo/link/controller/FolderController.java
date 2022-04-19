package com.example.demo.link.controller;

import com.example.demo.link.entity.Folder;
import com.example.demo.link.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Operation(summary = "All Folders", description = "Get All Folders")
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<List<Folder>> getAllFolders(){
        List<Folder> folders = folderService.findAll();
        return new ResponseEntity<List<Folder>>(folders, HttpStatus.OK);
    }
}
