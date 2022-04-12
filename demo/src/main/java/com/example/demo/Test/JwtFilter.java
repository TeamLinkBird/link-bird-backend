package com.example.demo.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@Slf4j
public class JwtFilter implements Filter {

    public static String makeJwtToken() {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // (1)
                .setIssuer("fresh") // (2)
                .setIssuedAt(now) // (3)
                .setExpiration(new Date(now.getTime() + Duration.ofMinutes(30).toMillis())) // 만료 시간은 30분
                .claim("id", "신동철") // 비공개 클레임
                //.claim("email", "ajufresh@gmail.com") // // 비공개 클레임
                .signWith(SignatureAlgorithm.HS256, "secret") // 서명부분  deprecated 되어 추후 변경 필요 있다.
                .compact();
    }

    public static Claims parseJwtToken(String authorizationHeader) {
        validationAuthorizationHeader(authorizationHeader); // (1)
        String token = extractToken(authorizationHeader); // (2)

        return Jwts.parser()
                .setSigningKey("secret") // (3)
                .parseClaimsJws(token) // (4)
                .getBody();
    }


    private static void validationAuthorizationHeader(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException();
        }
    }

    private static String extractToken(String authorizationHeader) {
        return authorizationHeader.substring("Bearer ".length());
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("Jwt 필터 생성");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authorizationHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);
        Claims claims = parseJwtToken(authorizationHeader);
        String parsedData = (String)claims.get("id");
        log.info("parsedData : {}",parsedData);
        if(!parsedData.equals("신동철")){ // 파싱된 값의 id 에 매칭되는 value 가 신동철이 아니라면 종료.
            return;
        }

        chain.doFilter(request, response); // 신동철이란 값이 제대로 나왔따면 진행
    }

    @Override
    public void destroy() {
        log.info("Jwt 필터 종료");
    }
}
