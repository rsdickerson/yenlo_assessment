package org.lab.sms.domain;

public class Location extends CommonBean<Location> {

    @Key
    private String code;
    @Required
    private String name;

    public Location(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
