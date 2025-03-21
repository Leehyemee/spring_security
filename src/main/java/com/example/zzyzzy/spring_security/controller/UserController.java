package com.example.zzyzzy.spring_security.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class UserController {

    @GetMapping("/login")
    public String login() {
        return "views/login";
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
