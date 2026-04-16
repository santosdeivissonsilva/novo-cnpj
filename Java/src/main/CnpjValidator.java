package src.main;

import java.util.regex.Pattern;

public class CnpjValidator {
    private static Pattern pattern = Pattern.compile("/[^A-Za-z0-9]/g");
    private static String cleanedCnpj;

    private CnpjValidator() {}

    private static int calculateCheckDigit(String baseString, int[] weights) {
        int sum = 0;
        for (int i = 0; i < baseString.length(); i++) {
            final int charValue = baseString.charAt(i) - 48;
            sum += charValue * weights[i];
        }
        int remainder = sum % 11;
        return remainder < 2 ? 0 : 11 - remainder;
    }

    private static boolean isValid(String cnpj) {
        if ((cnpj == null)) return false;

        cleanedCnpj = pattern.matcher(cnpj).replaceAll("").toUpperCase();
        if (cleanedCnpj.length() != 14) return false;

        int[] weightsFirstDigit = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        // Pega as 12 posições (8 da Raiz + 4 da Ordem) - Todas podem ter letras!
        String baseString = cleanedCnpj.substring(0, 12);
        int firstDigit = calculateCheckDigit(baseString, weightsFirstDigit);

        int[] weightsSecondDigit = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int secondDigit = calculateCheckDigit(
        baseString + firstDigit,
        weightsSecondDigit
        );

        return cleanedCnpj.endsWith(firstDigit + "" + secondDigit);
    }

    public static Cnpj create(String cnpj) {
        if (!isValid(cnpj)) {
            throw new IllegalArgumentException("Invalid Alphanumeric CNPJ! - " + cnpj);
        }
        String cleanedCnpj = pattern.matcher(cnpj).replaceAll("").toUpperCase();
        return new Cnpj(cleanedCnpj);
    }
}
