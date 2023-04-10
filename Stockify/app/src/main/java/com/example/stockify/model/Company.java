package com.example.stockify.model;

public class Company {
    private String symbol;
    private String name;
    private Type type;

    public Company() {}

    public Company(String symbol, String name, Type type) {
        this.symbol = symbol;
        this.name = name;
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
