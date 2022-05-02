package com.example.demo.link.repository;

import com.example.demo.link.entity.Tag;
import com.example.demo.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepo extends JpaRepository<Tag,Long> {

    List<Tag> findAllByUser(User user);
}
