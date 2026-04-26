package com.cnpjvalidator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


@DisplayName("CnpjValidator")
class CnpjValidatorTest {

    // -------------------------------------------------------------------------
    // create() — CNPJs válidos
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("create() — CNPJs válidos")
    class CreateValid {

        @Test
        @DisplayName("aceita CNPJ numérico legado com máscara")
        void numericWithMask() {
            Cnpj cnpj = CnpjValidator.create("11.222.333/0001-81");
            assertEquals("11222333000181", cnpj.value());
        }

        @Test
        @DisplayName("aceita CNPJ numérico legado sem máscara")
        void numericWithoutMask() {
            Cnpj cnpj = CnpjValidator.create("11222333000181");
            assertEquals("11222333000181", cnpj.value());
        }

        @Test
        @DisplayName("aceita CNPJ alfanumérico com máscara")
        void alphanumericWithMask() {
            Cnpj cnpj = CnpjValidator.create("12.ABC.345/01AB-77");
            assertEquals("12ABC34501AB77", cnpj.value());
        }

        @Test
        @DisplayName("aceita CNPJ alfanumérico sem máscara")
        void alphanumericWithoutMask() {
            Cnpj cnpj = CnpjValidator.create("12ABC34501AB77");
            assertEquals("12ABC34501AB77", cnpj.value());
        }

        @Test
        @DisplayName("normaliza letras minúsculas para maiúsculas")
        void normalizesLowercase() {
            Cnpj cnpj = CnpjValidator.create("12.abc.345/01ab-77");
            assertEquals("12ABC34501AB77", cnpj.value());
        }

        @Test
        @DisplayName("retorna o valor correto via toString()")
        void toStringMatchesValue() {
            Cnpj cnpj = CnpjValidator.create("11.222.333/0001-81");
            assertEquals(cnpj.value(), cnpj.toString());
        }

        @Test
        @DisplayName("dois CNPJs com o mesmo valor são iguais (record equality)")
        void recordEquality() {
            Cnpj a = CnpjValidator.create("11.222.333/0001-81");
            Cnpj b = CnpjValidator.create("11222333000181");
            assertEquals(a, b);
        }
    }

    // -------------------------------------------------------------------------
    // create() — CNPJs inválidos devem lançar exceção
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("create() — CNPJs inválidos")
    class CreateInvalid {
 
        @ParameterizedTest(name = "sequência uniforme: \"{0}\"")
        @ValueSource(strings = {
            "00000000000000",      // zeros
            "11111111111111",      // uns — matematicamente passa no módulo 11, mas deve ser rejeitado
            "22222222222222",
            "99999999999999",
            "AAAAAAAAAAAAAA"       // letras uniformes
        })
        @DisplayName("lança exceção para sequências onde todos os 14 caracteres são iguais")
        void uniformSequences(String cnpj) {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create(cnpj)
            );
        }
 
        @Test
        @DisplayName("lança exceção para dígito verificador errado")
        void wrongCheckDigits() {
            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create("12.ABC.345/01AB-35")
            );
            assertTrue(ex.getMessage().contains("Invalid Alphanumeric CNPJ!"));
        }
 
        @Test
        @DisplayName("lança exceção para CNPJ alfanumérico com segundo dígito errado")
        void wrongSecondCheckDigit() {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create("12.ABC.345/01AB-78")
            );
        }
 
        @Test
        @DisplayName("lança exceção para CNPJ com comprimento menor que 14")
        void tooShort() {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create("1222333000181")
            );
        }
 
        @Test
        @DisplayName("lança exceção para CNPJ com comprimento maior que 14")
        void tooLong() {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create("112223330001810")
            );
        }
 
        @Test
        @DisplayName("lança exceção para CNPJ nulo")
        void nullInput() {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create(null)
            );
        }
 
        @ParameterizedTest(name = "entrada: \"{0}\"")
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("lança exceção para entradas em branco ou nulas")
        void blankOrNullInputs(String input) {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create(input)
            );
        }
 
        @ParameterizedTest(name = "CNPJ inválido: \"{0}\"")
        @ValueSource(strings = {
            "00.000.000/0000-00",  // todos zeros
            "11.111.111/1111-11",  // sequência uniforme numérica (todos '1')
            "AA.AAA.AAA/AAAA-AA",  // sequência uniforme alfanumérica (todos 'A') — dígitos verificadores não são numéricos
            "12.345.678/0001-99",  // dígitos verificadores inventados
            "ab.cde.fgh/ijkl-mn"   // completamente inválido
        })
        @DisplayName("lança exceção para CNPJs com formato ou dígitos incorretos")
        void invalidCnpjs(String cnpj) {
            assertThrows(
                IllegalArgumentException.class,
                () -> CnpjValidator.create(cnpj)
            );
        }
    }
 
    // -------------------------------------------------------------------------
    // Cnpj record — validação do próprio construtor
    // -------------------------------------------------------------------------
 
    @Nested
    @DisplayName("Cnpj record — construtor")
    class CnpjRecord {
 
        @Test
        @DisplayName("lança exceção ao construir Cnpj com valor nulo")
        void nullValue() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new Cnpj(null)
            );
        }
 
        @Test
        @DisplayName("lança exceção ao construir Cnpj com valor em branco")
        void blankValue() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new Cnpj("   ")
            );
        }
 
        @Test
        @DisplayName("value() retorna o valor passado ao construtor")
        void valueAccessor() {
            Cnpj cnpj = new Cnpj("12ABC34501AB77");
            assertEquals("12ABC34501AB77", cnpj.value());
        }
    }
 
    // -------------------------------------------------------------------------
    // Normalização da entrada
    // -------------------------------------------------------------------------
 
    @Nested
    @DisplayName("Normalização da entrada")
    class Normalization {
 
        @Test
        @DisplayName("remove pontos, barras e traços antes de validar")
        void removesMaskCharacters() {
            // Mesmo CNPJ com e sem máscara devem produzir o mesmo resultado
            Cnpj withMask    = CnpjValidator.create("11.222.333/0001-81");
            Cnpj withoutMask = CnpjValidator.create("11222333000181");
            assertEquals(withMask, withoutMask);
        }
 
        @Test
        @DisplayName("converte letras minúsculas para maiúsculas no value()")
        void uppercasesLetters() {
            Cnpj cnpj = CnpjValidator.create("12abc34501ab77");
            assertEquals("12ABC34501AB77", cnpj.value());
        }
 
        @Test
        @DisplayName("remove espaços extras na entrada")
        void removesSpaces() {
            // Espaços são caracteres não-alfanuméricos e devem ser removidos
            Cnpj cnpj = CnpjValidator.create("11 222 333 0001 81");
            assertEquals("11222333000181", cnpj.value());
        }
    }
}