package com.at.netsupervisor.entity;

import java.io.Serializable;
import java.util.Objects;

/**
 * @create 2023-06-11
 */
public class InfoUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String name;

    private String address;

    public InfoUser() {
    }

    public InfoUser(String id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "InfoUser{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoUser infoUser = (InfoUser) o;
        return Objects.equals(id, infoUser.id) && Objects.equals(name, infoUser.name) && Objects.equals(address, infoUser.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, address);
    }
}
