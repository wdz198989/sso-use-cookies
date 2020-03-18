package com.sso.login.controller;

import com.sso.login.pojo.User;
import com.sso.login.utils.LoginCacheUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Controller
@RequestMapping("/login")
public class LoginController {
    private static Set<User> dbUsers;
    static {
        dbUsers=new HashSet<>();
        dbUsers.add(new User(0,"zhangsan","12345"));
        dbUsers.add(new User(1,"lisi","12345"));
        dbUsers.add(new User(2,"wangwu","12345"));
    }
    @PostMapping
    public String doLogin(User user, HttpSession session,
                          HttpServletResponse response){
        String target = (String)session.getAttribute("target");
        // 模拟从数据库中通过登陆用户名和密码去校验用户
        Optional<User> first = dbUsers.stream().filter(dbUser -> dbUser.getUsername().equals(user.getUsername()) &&
                dbUser.getPassword().equals(user.getPassword())).findFirst();
        // 判断用户是否登录
        if(first.isPresent()){
           //保存用户信息
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("TOKEN", token);
            cookie.setDomain("codeshop.com");
            response.addCookie(cookie);
            LoginCacheUtil.loginUser.put(token,first.get());
        }else{
            session.setAttribute("msg","用户名或者密码错误");
            return "login";
        }
        return "redirect:"+ target;
    }
    @GetMapping("info")
    @ResponseBody
    public ResponseEntity<User> getUserInfo(String token){
        if(!StringUtils.isEmpty(token)){
          User user= LoginCacheUtil.loginUser.get(token);
          return ResponseEntity.ok(user);
        }else{
            return  new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}


