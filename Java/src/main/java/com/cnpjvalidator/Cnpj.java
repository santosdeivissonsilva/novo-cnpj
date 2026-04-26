package com.cnpjvalidator;
public record Cnpj(String value) {
    public Cnpj {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CNPJ value must not be null or blank.");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}
