package com.example.demo.link.service;

import com.example.demo.link.dto.TagDto;
import com.example.demo.link.entity.Tag;
import com.example.demo.link.repository.TagRepo;
import com.example.demo.login.entity.User;
import com.example.demo.login.repository.LoginRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepo tagRepo;
    private final LoginRepo loginRepo;

    public List<TagDto> findAllByUserId(String userId){
        User user = loginRepo.findByUserId(userId);
        List<Tag> tags = tagRepo.findAllByUser(user);
        return tags.stream().map(tag ->
                TagDto.builder()
                        .tagOrder(tag.getTagOrder())
                        .tagName(tag.getTagName())
                        .build())
                .collect(Collectors.toList());
    }

    public TagDto findByTagCode(long tagCode){
        Optional<Tag> tag = tagRepo.findById(tagCode);
        return TagDto.builder()
                .tagOrder(tag.get().getTagOrder())
                .tagName(tag.get().getTagName())
                .build();
    }

    public void updateByTagCode(long tagCode){
        // todo helper 만들어서 set 하고 업데이트하기
    }

    public void deleteByTagCode(long tagCode){
        tagRepo.deleteById(tagCode);
    }

}
