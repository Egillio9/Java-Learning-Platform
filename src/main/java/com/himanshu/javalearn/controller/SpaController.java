package com.himanshu.javalearn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Client-side routes of the React app; a refresh on any of them must still load index.html. */
@Controller
public class SpaController {

    @GetMapping({"/login", "/app", "/app/**", "/problems", "/problems/**", "/learn", "/learn/**", "/leaderboard", "/privacy"})
    public String forwardToApp() {
        return "forward:/index.html";
    }
}
