package com.admond.community.controller;

import com.admond.community.dto.QuestionDTO;
import com.admond.community.mapper.UserMapper;
import com.admond.community.model.User;
import com.admond.community.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IndexController {
    @Resource
    private UserMapper userMapper;

    @Resource
    private QuestionService questionService;

    @GetMapping("/")
    public  String index(HttpServletRequest request,
                         Model model,
                         @RequestParam(name = "page",defaultValue = "1") Integer page,
                         @RequestParam(name = "size",defaultValue = "5") Integer size){
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length != 0) {
            for(Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    User user = userMapper.findByToken(token);
                    if (user != null) {
                        System.out.println("user not null");
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
            System.out.println("cookies not null");
        }
        List<QuestionDTO> questionList = questionService.list(page,size);
        model.addAttribute("questions",questionList);
        return "index";
    }
}
