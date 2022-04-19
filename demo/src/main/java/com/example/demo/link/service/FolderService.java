package com.example.demo.link.service;

import com.example.demo.link.entity.Folder;
import com.example.demo.link.repository.FolderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepo folderRepo;

    public List<Folder> findAll(){
        List<Folder> folders = new ArrayList<>();
        folderRepo.findAll().forEach(e -> {
//            /if(e.getLinks() != null)
                folders.add(e);
        });
        return folders;
    }
}
