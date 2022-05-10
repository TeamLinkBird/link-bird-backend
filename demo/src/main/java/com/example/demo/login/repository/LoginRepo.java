package com.example.demo.login.repository;

import com.example.demo.link.entity.Folder;
import com.example.demo.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginRepo extends JpaRepository<User,String> {

    public User findByUserId(String userId);

    public User findByRefreshToken(String refresh_Token);

}
