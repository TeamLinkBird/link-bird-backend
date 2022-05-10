package com.example.demo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Data
@NoArgsConstructor
@Component
public class LinkCrawler {

    public String getTitle(String url){
        Document document = getDocument(url);
        return searchTag(document, "title");
    }

    private Document getDocument(String url) {
        Document doc = null;
        try{
            doc = Jsoup.connect(url).get();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return doc;
    }

    private String searchTag(Document document, String tag){
        Elements elements = document.select(tag);
        String title = null;

        for(Element e : elements) {
            if(0 < e.text().length())
                title = e.text();
        }
        return title;
    }
}
