package com.example.demo.link.repository;

import com.example.demo.link.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepo extends JpaRepository<Tag,Long> {

}
