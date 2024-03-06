package com.at.rpc.controller;

import com.at.rpc.api.IUserService;
import com.at.rpc.spring.annotation.GpRemoteReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GpRemoteReference
    private IUserService userService;

    @GetMapping("/test")
    public String test(){
        String s = userService.saveUser("Mic");
        System.out.println("success res = " + s);
        return s;
    }
}