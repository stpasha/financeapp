package net.microfin.financeapp.util;

public enum Currency {
    USD("Доллар"),
    RUB("Рубль"),
    EUR("Евро");


    private final String name;

    Currency(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
