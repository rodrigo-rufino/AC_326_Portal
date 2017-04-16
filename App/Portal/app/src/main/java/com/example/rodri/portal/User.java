package com.example.rodri.portal;

import java.util.List;

/**
 * Created by rodri on 15-Apr-17.
 */

public class User {
    public String username;
    public String mac;
    public List<String> portals;

    public User(){
    }

    public User(String username, String mac){
        this.username = username;
        this.mac = mac;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public List<String> getPortals() {
        return portals;
    }

    public void setPortals(List<String> portals) {
        this.portals = portals;
    }

    @Override
    public String toString() {
        String s = getUsername() + " " + getMac() + " " + getPortals();
        return s;
    }
}

