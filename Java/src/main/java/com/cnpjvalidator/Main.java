package com.cnpjvalidator;

public class Main {
 
    public static void main(String[] args) {
        // CNPJ numérico válido (Receita Federal — exemplo público)
        String raw = "11.222.333/0001-81";
 
        try {
            Cnpj cnpj = CnpjValidator.create(raw);
            System.out.println("Cnpj criado: " + cnpj.value());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}