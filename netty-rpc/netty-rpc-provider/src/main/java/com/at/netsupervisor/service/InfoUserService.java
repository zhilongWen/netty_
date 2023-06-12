package com.at.netsupervisor.service;

import com.at.netsupervisor.entity.InfoUser;

import java.util.List;
import java.util.Map;

/**
 * @create 2023-06-11
 */
public interface InfoUserService {

    /**
     * insert 一个 user 对象
     * @param infoUser
     * @return
     */
    List<InfoUser> insertInfoUser(InfoUser infoUser);

    /**
     * 通过 id 获取 user
     * @param id
     * @return
     */
    InfoUser getInfoUserById(String id);

    /**
     * 通过 id 删除 user
     * @param id
     */
    void deleteInfoUserById(String id);

    /**
     * 通过 id 获取 name
     * @param id
     * @return
     */
    String getNameById(String id);

    /**
     * 获取所有的用户信息
     * @return
     */
    Map<String, InfoUser> getAllUser();

}
