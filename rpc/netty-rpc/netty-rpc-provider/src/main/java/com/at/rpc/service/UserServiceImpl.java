package com.at.rpc.service;

import com.at.rpc.api.IUserService;
import com.at.rpc.spring.annotation.GpRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

//@Service
@GpRemoteService
@Slf4j
public class UserServiceImpl implements IUserService {
    @Override
    public String saveUser(String name) {
        log.info("begin saveUser:"+name);
        return "Save User Success!";
    }
}
