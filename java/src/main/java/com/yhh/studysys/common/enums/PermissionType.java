package com.yhh.studysys.common.enums;

public enum PermissionType {

    Menu(1,"菜单权限"),

    BUTTON(2,"按钮权限"),

    ;

    private final int type;

    private final String desc;

    PermissionType(int type,String desc){
        this.type = type;
        this.desc = desc;
    }

    public int type(){
        return this.type;
    }

    public String desc(){
        return this.desc;
    }

}
