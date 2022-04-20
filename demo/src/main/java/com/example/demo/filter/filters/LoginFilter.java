package com.example.demo.filter.filters;

import com.example.demo.common.utility.JwtUtility;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Jwt 필터 생성");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
//        Claims claims = JwtUtility.parseJwtToken(authorizationHeader);
//        String parsedData = (String)claims.get("id");
//        log.info("parsedData : {}",parsedData);
//        if(!parsedData.equals("신동철")){ // 파싱된 값의 id 에 매칭되는 value 가 신동철이 아니라면 종료.
//            return;
//        }

        chain.doFilter(request, response); // 신동철이란 값이 제대로 나왔따면 진행
    }

    @Override
    public void destroy() {
        log.info("Jwt 필터 종료");
    }
}
