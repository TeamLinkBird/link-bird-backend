package com.example.demo.link.service;

import com.example.demo.link.entity.Folder;
import com.example.demo.link.repository.FolderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepo folderRepo;

    public List<Folder> findAll(){
        List<Folder> folders = new ArrayList<>();
        folderRepo.findAll().forEach(e -> {
//            /if(e.getLinks() != null)
                folders.add(e);
        });
        return folders;
    }

}
