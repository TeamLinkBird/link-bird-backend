package com.example.demo.link.repository;

import com.example.demo.link.entity.Folder;
import com.example.demo.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepo extends JpaRepository<Folder,Long> {

    List<Folder> findAllByUser(User user);
}
