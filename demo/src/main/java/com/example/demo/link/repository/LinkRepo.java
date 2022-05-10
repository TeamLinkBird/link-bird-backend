package com.example.demo.link.repository;

import com.example.demo.link.entity.Link;
import com.example.demo.login.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepo extends JpaRepository<Link,Long> {
    List<Link> findAllByUser(User user);
}
