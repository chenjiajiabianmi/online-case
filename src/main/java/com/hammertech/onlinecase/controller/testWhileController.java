package com.hammertech.onlinecase.controller;


import com.hammertech.onlinecase.service.TestWhileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping
@Controller
public class testWhileController {

    @Autowired
    private TestWhileService testWhileService;
    @GetMapping("/testWhile")
    public String testWhile(@RequestParam int size) {
        log.info("enter testWhile with param | size | {}", size);
        testWhileService.testWhile(size);
        return "test triggered !!!";
    }
}

