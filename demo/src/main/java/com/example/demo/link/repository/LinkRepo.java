package com.example.demo.link.repository;

import com.example.demo.link.entity.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinkRepo extends JpaRepository<Link,Long> {
}
