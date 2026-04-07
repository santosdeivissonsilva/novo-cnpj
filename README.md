# novo-cnpj — Validador de CNPJ Alfanumérico em TypeScript

Biblioteca **zero dependências** para validar e tipar o novo **CNPJ Alfanumérico** da Receita Federal, implementado em TypeScript puro com suporte ao formato legado (numérico) e ao novo formato alfanumérico previsto para entrar em vigor em **julho de 2026**.

---

## O que é o novo CNPJ Alfanumérico?

O **CNPJ (Cadastro Nacional da Pessoa Jurídica)** é o número de identificação das empresas e organizações junto à Receita Federal do Brasil. Até hoje ele é composto apenas por **14 dígitos numéricos** no formato `XX.XXX.XXX/XXXX-DD`.

Com o crescimento contínuo do número de empresas abertas no Brasil, a capacidade do formato puramente numérico estava chegando ao limite. Para resolver isso, a Receita Federal publicou a **Instrução Normativa RFB nº 2.229, de 15 de outubro de 2024**, criando o **CNPJ Alfanumérico**: um novo formato que combina letras maiúsculas (A–Z) e números (0–9) nas 12 primeiras posições.

> **Importante:** os CNPJs numéricos já existentes **não serão alterados**. O novo formato será atribuído apenas às **novas inscrições a partir de julho de 2026**.

### Cronograma oficial

| Data                  | Evento                                                  |
| --------------------- | ------------------------------------------------------- |
| 15 de outubro de 2024 | Publicação da Instrução Normativa RFB nº 2.229          |
| 25 de outubro de 2024 | Entrada em vigor da Instrução Normativa                 |
| Julho de 2026         | Início da emissão de CNPJs no novo formato alfanumérico |

---

## Estrutura do CNPJ Alfanumérico

Um CNPJ tem sempre **14 caracteres** divididos em três partes:

```
X X . X X X . X X X / X X X X - D D
└───────────────────┘ └──────┘ └──┘
    Raiz (8 chars)    Ordem   Dígitos
                     (4 chars) Verificadores
```

| Parte                     | Posições | Conteúdo                                       | Exemplo    |
| ------------------------- | -------- | ---------------------------------------------- | ---------- |
| **Raiz**                  | 1–8      | Identifica a empresa (pode ter letras)         | `12ABC345` |
| **Ordem**                 | 9–12     | Identifica o estabelecimento (pode ter letras) | `01AB`     |
| **Dígitos Verificadores** | 13–14    | Sempre numéricos (0–9)                         | `77`       |

No novo formato, as posições de 1 a 12 aceitam os caracteres `A–Z` e `0–9`. As posições 13 e 14 (dígitos verificadores) **continuam sendo sempre numéricas**.

A máscara de exibição é: `XX.XXX.XXX/XXXX-DD`

---

## Como os Dígitos Verificadores são calculados

O algoritmo segue o padrão **módulo 11** definido pela Receita Federal, adaptado para caracteres alfanuméricos.

### Conversão de caracteres

Cada caractere da base é convertido para um valor numérico subtraindo 48 do seu código ASCII:

- Dígitos `0–9` → valores `0` a `9`
- Letras maiúsculas `A–Z` → valores `17` a `42`
  - `'A'` = 65 − 48 = **17**
  - `'B'` = 66 − 48 = **18**
  - `'Z'` = 90 − 48 = **42**

### Cálculo do 1º dígito verificador

1. Tome os **12 primeiros caracteres** (Raiz + Ordem)
2. Multiplique cada caractere pelo seu peso, da esquerda para a direita:

   | Posição  | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   | 9   | 10  | 11  | 12  |
   | -------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
   | **Peso** | 5   | 4   | 3   | 2   | 9   | 8   | 7   | 6   | 5   | 4   | 3   | 2   |

3. Some todos os resultados
4. Calcule o resto da divisão por 11
5. Se o resto for `0` ou `1` → dígito = `0`; caso contrário → dígito = `11 − resto`

### Cálculo do 2º dígito verificador

1. Utilize os **12 primeiros caracteres + o 1º dígito verificador** (total de 13 caracteres)
2. Multiplique cada caractere pelo seu peso:

   | Posição  | 1   | 2   | 3   | 4   | 5   | 6   | 7   | 8   | 9   | 10  | 11  | 12  | 13  |
   | -------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
   | **Peso** | 6   | 5   | 4   | 3   | 2   | 9   | 8   | 7   | 6   | 5   | 4   | 3   | 2   |

3. Some todos os resultados
4. Calcule o resto da divisão por 11
5. Mesma regra: se o resto for `0` ou `1` → dígito = `0`; caso contrário → dígito = `11 − resto`

---

## Instalação

O código é um único arquivo TypeScript sem dependências externas. Basta copiar `CnpjValidator.ts` para o seu projeto.

Os testes utilizam o módulo nativo `node:test` do Node.js (disponível a partir da versão 18).

---

## Como usar

### Verificar se um CNPJ é válido — `isValid`

```typescript
import { CnpjValidator } from "./CnpjValidator";

// CNPJs numéricos legados continuam válidos
CnpjValidator.isValid("00.000.000/0001-91"); // true

// Novo formato alfanumérico (com máscara)
CnpjValidator.isValid("12.ABC.345/01AB-77"); // true

// Aceita letras minúsculas e caracteres extras na entrada — normaliza automaticamente
CnpjValidator.isValid("12.abc.345/01ab-77"); // true

// Retorna false para CNPJs inválidos
CnpjValidator.isValid("12.ABC.345/01AB-35"); // false (dígitos verificadores errados)
CnpjValidator.isValid("00000000000000"); // false (sequência uniforme)
CnpjValidator.isValid(null); // false (não é string)
```

### Criar um CNPJ tipado e normalizado — `create`

```typescript
import { CnpjValidator, Cnpj } from "./CnpjValidator";

// Retorna o CNPJ normalizado (uppercase, sem máscara) com tipo seguro
const cnpj: Cnpj = CnpjValidator.create("12.abc.345/01ab-77");
console.log(cnpj); // "12ABC34501AB77"

// Lança um erro se o CNPJ for inválido
CnpjValidator.create("12.ABC.345/01AB-36"); // Erro: "Invalid Alphanumeric CNPJ!"
```

---

## API

### `CnpjValidator.isValid(cnpj: unknown): boolean`

Verifica se um valor é um CNPJ válido (numérico legado ou alfanumérico).

- Aceita qualquer tipo (`unknown`) — retorna `false` para não-strings
- Ignora automaticamente caracteres que não sejam letras ou números (pontos, barras, traços, espaços etc.)
- Converte letras minúsculas para maiúsculas antes de validar
- Rejeita sequências em que todos os 14 caracteres são iguais (ex: `AAAAAAAAAAAAAA`)
- Retorna `true` apenas se os dois dígitos verificadores baterem com o algoritmo da Receita Federal

### `CnpjValidator.create(cnpj: string): Cnpj`

Cria um valor do tipo `Cnpj` (string com Branded Type) a partir de uma string.

- Retorna o CNPJ **normalizado**: somente letras maiúsculas e números, sem máscara
- Lança `Error("Invalid Alphanumeric CNPJ!")` se o CNPJ não for válido

### Tipo `Cnpj`

```typescript
export type Cnpj = string & { readonly __brand: unique symbol };
```

Um [Branded Type](https://www.typescriptlang.org/docs/handbook/2/template-literal-types.html) que garante em tempo de compilação que a string passou pela validação. Isso evita passar strings não validadas onde um CNPJ é esperado.

```typescript
function processarEmpresa(cnpj: Cnpj) {
  /* ... */
}

processarEmpresa("12ABC34501AB77"); // Erro de tipo em tempo de compilação!
processarEmpresa(CnpjValidator.create("...")); // OK
```

---

## Rodando os testes

O projeto não requer nenhum build prévio. Escolha o runtime de sua preferência:

**Node.js** (v22+)

```bash
node --test --experimental-strip-types tests/CnpjValidator.test.ts
```

**Deno** (v1.40+)

```bash
deno test --allow-read tests/CnpjValidator.test.ts
```

**Bun** (v1.0+)

```bash
bun test tests/CnpjValidator.test.ts
```

Os testes cobrem:

- Validação de CNPJs alfanuméricos com máscara e variações de caixa
- Rejeição de dígitos verificadores mutados
- Rejeição de sequências uniformes e comprimentos inválidos
- Rejeição de tipos não-string (`null`, `undefined`, números, arrays etc.)
- Normalização e erro no método `create`

---

## Autores

Desenvolvido por **Gabriel Froes** e **Vanessa Weber** do [Código Fonte TV](https://www.youtube.com/@codigofontetv).

Confira o vídeo explicativo no canal:

[![Código Fonte TV — Novo CNPJ Alfanumérico](https://img.youtube.com/vi/PrlLdgwxpdo/hqdefault.jpg)](https://www.youtube.com/watch?v=PrlLdgwxpdo)

---

## Referências oficiais

- [CNPJ Alfanumérico — Receita Federal](https://www.gov.br/receitafederal/pt-br/acesso-a-informacao/acoes-e-programas/programas-e-atividades/cnpj-alfanumerico)
- [Instrução Normativa RFB nº 2.229/2024](https://www.gov.br/receitafederal/pt-br/acesso-a-informacao/legislacao)
