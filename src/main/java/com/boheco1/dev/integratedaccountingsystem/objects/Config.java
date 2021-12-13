package com.boheco1.dev.integratedaccountingsystem.objects;

public class Config {
    private String key;
    private String value;
    private String parse;

    public Config(String key, String value, String parse) {
        this.key = key;
        this.value = value;
        this.parse = parse;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParse() {
        return parse;
    }

    public void setParse(String parse) {
        this.parse = parse;
    }

    public Object getParsed() {
        if(this.parse.equals("Integer")) {
            return Integer.parseInt(this.value);
        }else if(this.parse.equals("Double")) {
            return Double.parseDouble(this.value);
        }else if(this.parse.equals("Float")) {
            return Float.parseFloat(this.value);
        }else {
            return this.value;
        }
    }
}
