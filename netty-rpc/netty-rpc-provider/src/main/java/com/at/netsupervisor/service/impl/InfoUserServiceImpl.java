package com.at.netsupervisor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.at.netsupervisor.entity.InfoUser;
import com.at.netsupervisor.service.InfoUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @create 2023-06-11
 */
public class InfoUserServiceImpl implements InfoUserService {

    Logger logger = LoggerFactory.getLogger(InfoUserServiceImpl.class);

    //当做数据库，存储用户信息
    Map<String,InfoUser> infoUserMap = new ConcurrentHashMap<>(8);

    @Override
    public List<InfoUser> insertInfoUser(InfoUser infoUser) {
        logger.info("新增用户信息：{}", JSONObject.toJSONString(infoUser));
        infoUserMap.put(infoUser.getId(),infoUser);
        return null;
    }

    public List<InfoUser> getInfoUserList() {
        List<InfoUser> infoUserList = infoUserMap
                .entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        logger.info("返回用户信息：{}", JSON.toJSONString(infoUserList));

        return infoUserList;
    }

    @Override
    public InfoUser getInfoUserById(String id) {
        InfoUser infoUser = infoUserMap.get(id);
        logger.info("通过 id = {}, 查询到的用户信息：{}",id,infoUser);
        return infoUser;
    }

    @Override
    public void deleteInfoUserById(String id) {
        InfoUser infoUser = infoUserMap.remove(id);
        logger.info("删除的用户信息：{}", infoUser);
    }

    @Override
    public String getNameById(String id) {
        InfoUser infoUser = infoUserMap.get(id);
        String name = null;

        if (infoUser != null){
            name = infoUser.getName();
        }

        logger.info("通过 id = {}，获取到的 user name = {}",id,name);

        return name;
    }

    @Override
    public Map<String, InfoUser> getAllUser() {
        logger.info("查询到的所有用户信息：{}",JSONObject.toJSONString(infoUserMap));
        return infoUserMap;
    }
}
