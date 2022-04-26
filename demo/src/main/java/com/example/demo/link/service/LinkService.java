package com.example.demo.link.service;

import com.example.demo.common.LinkCrawler;
import com.example.demo.link.dto.LinkDto;
import com.example.demo.link.entity.Link;
import com.example.demo.link.repository.FolderRepo;
import com.example.demo.link.repository.LinkRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LinkService {

    private final LinkRepo linkRepo;
    private final FolderRepo folderRepo;
    private final LinkCrawler linkCrawler;


    public List<LinkDto> findAll() {
        List<Link> links = linkRepo.findAll();
        return links.stream().map(link ->
                LinkDto.builder()
                        .url(link.getUrl())
                        .title(link.getTitle())
                        .memo(link.getMemo())
                        .isRead(link.isRead())
                        .build())
                .collect(Collectors.toList());
    }

    public LinkDto findByLinkCode(long linkCode){
        Optional<Link> link = linkRepo.findById(linkCode);
        return LinkDto.builder()
                .url(link.get().getUrl())
                .title(link.get().getTitle())
                .memo(link.get().getMemo())
                .isRead(link.get().isRead())
                .build();
    }

    public LinkDto save(LinkDto linkDto) {
        linkRepo.save(
            Link.builder()
                    .folder(folderRepo.getById(linkDto.getFolderCode()))
                    .linkOrder(linkDto.getLinkOrder())
                    .url(linkDto.getUrl())
                    .memo(linkDto.getMemo())
                    .isRead(linkDto.isRead())
                    .title(linkCrawler.getTitle(linkDto.getUrl()))
                    .build()
        );
        return linkDto;
    }

    public void update(LinkDto linkDto){
        // todo helper 만들기 혹은 setter 만들기
    }

    public void delete(long linkCode){
        linkRepo.deleteById(linkCode);
    }
}
