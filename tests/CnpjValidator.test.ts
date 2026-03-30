import test from "node:test";
import assert from "node:assert/strict";
import { CnpjValidator } from "../CnpjValidator.ts";

const VALID_CNPJS = [
  "12345678000195",
  "12ABC34501AB77",
  "AB12CD34EF5602",
  "A1B2C3D4E5F668",
  "ZXCVBN1234QW16",
  "00000000000191",
];

test("isValid accepts valid alphanumeric CNPJ", () => {
  assert.equal(CnpjValidator.isValid("12.ABC.345/01AB-77"), true);
  assert.equal(CnpjValidator.isValid("12.abc.345/01ab-77"), true);
  assert.equal(CnpjValidator.isValid("12...ABC...345///01AB---77!!!"), true);
});

test("isValid rejects legacy sample and repeated sequences", () => {
  assert.equal(CnpjValidator.isValid("12.ABC.345/01AB-35"), false);
  assert.equal(CnpjValidator.isValid("00000000000000"), false);
  assert.equal(CnpjValidator.isValid("AAAAAAAAAAAAAA"), false);
});

test("isValid validates generated bases and rejects check-digit mutations", () => {
  for (const cnpj of VALID_CNPJS) {
    assert.equal(CnpjValidator.isValid(cnpj), true);

    const penultimate = Number(cnpj[12]);
    const nextPenultimate = Number.isNaN(penultimate)
      ? 0
      : (penultimate + 1) % 10;
    const mutatedPenultimate = `${cnpj.slice(0, 12)}${nextPenultimate}${cnpj[13] ?? "0"}`;
    assert.equal(CnpjValidator.isValid(mutatedPenultimate), false);

    const last = Number(cnpj.at(-1));
    const nextLast = Number.isNaN(last) ? 0 : (last + 1) % 10;
    const mutatedLast = `${cnpj.slice(0, 13)}${nextLast}`;
    assert.equal(CnpjValidator.isValid(mutatedLast), false);
  }
});

test("isValid rejects invalid length and non-string values", () => {
  const invalidLength = [
    "",
    "1",
    "1234567890123",
    "123456789012345",
    "11.222.333/0001",
    "11.222.333/0001-811",
  ];

  for (const value of invalidLength) {
    assert.equal(CnpjValidator.isValid(value), false);
  }

  const nonString: unknown[] = [
    null,
    undefined,
    11222333000181,
    {},
    [],
    true,
    false,
  ];
  for (const value of nonString) {
    assert.equal(CnpjValidator.isValid(value), false);
  }
});

test("create returns normalized value and throws for invalid", () => {
  assert.equal(CnpjValidator.create("12.abc.345/01ab-77"), "12ABC34501AB77");
  assert.throws(
    () => CnpjValidator.create("12.ABC.345/01AB-36"),
    /Invalid Alphanumeric CNPJ!/,
  );
  assert.throws(() => CnpjValidator.create(""), /Invalid Alphanumeric CNPJ!/);
});
