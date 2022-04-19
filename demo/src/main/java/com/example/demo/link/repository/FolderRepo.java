package com.example.demo.link.repository;

import com.example.demo.link.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepo extends JpaRepository<Folder,Long> {

    public List<Folder> findByFolderCode(Long folderCode);

}
