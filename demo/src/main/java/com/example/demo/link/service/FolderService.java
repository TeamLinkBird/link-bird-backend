package com.example.demo.link.service;

import com.example.demo.link.dto.FolderDto;
import com.example.demo.link.dto.LinkDto;
import com.example.demo.link.entity.Folder;
import com.example.demo.link.repository.FolderRepo;
import com.example.demo.login.entity.User;
import com.example.demo.login.repository.LoginRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FolderService {

    private final FolderRepo folderRepo;
    private final LoginRepo loginRepo;

    public List<FolderDto> findAll(String userId) {
        User user = loginRepo.findByUserId(userId);
        List<Folder> folders = folderRepo.findAllByUser(user);
        return folders.stream().map(folder ->
                        FolderDto.builder()
                                .code(folder.getFolderCode())
                                .userId(folder.getUser().getUserId())
                                .list(folder.getLinks()
                                        .stream()
                                        .map(link -> LinkDto.builder()
                                                .title(link.getTitle())
                                                .url(link.getUrl())
                                                .build()).collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    public FolderDto save(FolderDto folderDto, String userId){
        User user = loginRepo.findByUserId(userId);
        Folder folder = Folder.builder()
                .user(user)
                .folderOrder(folderDto.getOrder())
                .folderName(folderDto.getName())
                .build();
        folderRepo.save(folder);
        return folderDto;
    }
    
    public void update(FolderDto folderDto){
        Folder folder = Folder.builder()
                .folderOrder(folderDto.getOrder())
                .folderName(folderDto.getName())
                .build();
        // todo 업데이트로 바꾸기
        folderRepo.save(folder);
    }

    public void updateByFolderCode(Long folderCode, FolderDto folderDto) {
        Optional<Folder> folderOptional = folderRepo.findById(folderCode);
        if(folderOptional.isPresent()){
            Folder folder = Folder.builder()
                    .folderCode(folderCode)
                    .folderOrder(folderDto.getOrder())
                    .folderName(folderDto.getName())
                    .build();
            folderRepo.save(folder);
        }
    }

    public void deleteByFolderCode(Long folderCode) {
        folderRepo.deleteById(folderCode);
    }

    public FolderDto findByFolderCode(long folderCode) {
        Optional<Folder> folder = folderRepo.findById(folderCode);
        return  FolderDto.builder()
                .code(folder.get().getFolderCode())
                .name(folder.get().getFolderName())
                .list(folder.get().getLinks()
                        .stream()
                        .map(link -> LinkDto.builder()
                                .title(link.getTitle())
                                .url(link.getUrl())
                                .build()).collect(Collectors.toList()))
                .build();
    }
}
