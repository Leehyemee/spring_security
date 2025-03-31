package com.example.zzyzzy.spring_security.controller;

import com.example.zzyzzy.spring_security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {

    private  final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login() {
        return "views/login";
    }

    @PostMapping("/login")
    public String loginok(@RequestParam String userid,
                          @RequestParam String userpwd, HttpServletResponse res) {

        log.info(">> /member/login 호출!");

        try {
            // 스프링 시큐리티에서 제공하는 인증처리 매니저로 인증 처리
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userid, userpwd)
            );

            // 인증이 완료되면 jwt 토큰 생성
            final String jwt = jwtTokenProvider.generateToken(userid);

            // JWT 토큰을 쿠키에 저장
            Cookie cookie = new Cookie("jwt", jwt);
            cookie.setHttpOnly(true); // 토큰은 header를 통해서만 서버로 전송가능
            cookie.setMaxAge(60 * 30); // 유효시간 30분
            cookie.setPath("/");
            res.addCookie(cookie);

            return "redirect:/member/myinfo";

        } catch (BadCredentialsException ex) {
            log.info("잘못된 아이디나 비밀번호를 입력하셨습니다!!");
            return "redirect:/member/login";
        }
    }



    @GetMapping("/myinfo")
    public String myinfo(Authentication authentication, Model model) {
        String returnUrl = "redirect:/member/login";

        // 로그인 인증이 성공했다면
        if(authentication != null && authentication.isAuthenticated()) {
            // 인증 완료된 사용자 정보를 가져옴
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("user", userDetails);
            returnUrl = "views/myinfo";
        }
        //return returnUrl;
        return "views/myinfo";
    }
}
