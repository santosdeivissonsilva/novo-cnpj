// O "Branded Type": Garante que essa string passou pela nossa validação
export type Cnpj = string & { readonly __brand: unique symbol };

export class CnpjValidator {
  private static calculateCheckDigit(
    baseString: string,
    weights: number[],
  ): number {
    let sum = 0;
    for (let i = 0; i < baseString.length; i++) {
      const charValue = baseString.charCodeAt(i) - 48;
      sum += charValue * weights[i];
    }
    const remainder = sum % 11;
    return remainder < 2 ? 0 : 11 - remainder;
  }

  public static isValid(cnpj: unknown): boolean {
    if (typeof cnpj !== "string") return false;

    const cleanedCnpj = cnpj.replace(/[^A-Za-z0-9]/g, "").toUpperCase();
    if (cleanedCnpj.length !== 14) return false;
    if (/^([A-Z0-9])\1{13}$/.test(cleanedCnpj)) return false;

    const weightsFirstDigit = [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    // Pega as 12 posições (8 da Raiz + 4 da Ordem) - Todas podem ter letras!
    const baseString = cleanedCnpj.substring(0, 12);
    const firstDigit = this.calculateCheckDigit(baseString, weightsFirstDigit);

    const weightsSecondDigit = [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
    const secondDigit = this.calculateCheckDigit(
      baseString + firstDigit,
      weightsSecondDigit,
    );

    return cleanedCnpj.endsWith(`${firstDigit}${secondDigit}`);
  }

  public static create(cnpj: string): Cnpj {
    if (!this.isValid(cnpj)) {
      throw new Error("Invalid Alphanumeric CNPJ!");
    }
    return cnpj.replace(/[^A-Za-z0-9]/g, "").toUpperCase() as Cnpj;
  }
}
