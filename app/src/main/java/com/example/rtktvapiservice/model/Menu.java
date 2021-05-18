package com.example.rtktvapiservice.model;

import java.util.ArrayList;

public class Menu {
    private String title= "";
    private ArrayList<SubMenu> subMenus = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<SubMenu> getSubMenus() {
        return subMenus;
    }

    public void setSubMenus(ArrayList<SubMenu> subMenus) {
        this.subMenus = subMenus;
    }
}
