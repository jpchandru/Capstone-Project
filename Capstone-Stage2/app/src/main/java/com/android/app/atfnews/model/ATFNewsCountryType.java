package com.android.app.atfnews.model;

public enum ATFNewsCountryType {

    US("us", "USA"),

    UK("gb", "UK"),

    AU("au", "Australia"),

    IN("in", "INDIA"),

    UNKNOWN("unknown", "UNKNOWN");

    private final String source;

    private final String type;

    public String getSource() {
        return source;
    }

    public String getType() {
        return type;
    }

    ATFNewsCountryType(String source, String type) {
        this.source = source;
        this.type = type;
    }

    public static ATFNewsCountryType resolve(String source) {
        for (ATFNewsCountryType countryType : ATFNewsCountryType.values()) {
            if (countryType.getSource().equals(source)) {
                return countryType;
            }
        }
        return ATFNewsCountryType.UNKNOWN;
    }
}
