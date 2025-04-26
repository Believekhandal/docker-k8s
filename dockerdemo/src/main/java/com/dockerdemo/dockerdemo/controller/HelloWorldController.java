package com.dockerdemo.dockerdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/api/v1")
public class HelloWorldController {

    @GetMapping("/hello")
    public String sayHello() {
        log.info("Request received.....");
        return "home"; // It will look for home.html under src/main/resources/templates/
    }
}
