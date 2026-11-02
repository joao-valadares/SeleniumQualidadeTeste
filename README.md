# Testes E2E com Selenium - automationexercise.com

Atividade de Qualidade e Teste de Software: testes end-to-end com Selenium + JUnit 5 para o site
https://automationexercise.com/

- `LoginTest.java` - **Test Case 3**: Login User with incorrect email and password
- `RegistrarUsuarioTest.java` - **Test Case 1**: Register User

## Como rodar

Precisa de Java 8+ e do Google Chrome instalado (o driver é baixado pelo WebDriverManager).

```bash
mvnw.cmd test
```

(ou `mvn test` se tiver o Maven instalado; também dá para rodar pelo Eclipse com "Run As > JUnit Test")

## Casos de teste (classes de equivalência e valor limite)

### Login com e-mail incorreto (Test Case 3)

| Entrada (e-mail / senha) | Classe | Resultado esperado |
|---|---|---|
| `email.incorreto@teste.com` / `senha123` | Fluxo do Test Case 3 | "Your email or password is incorrect!" |
| `naoexiste@teste.com` / `senha123` | Válida: e-mail com formato correto, não cadastrado | "Your email or password is incorrect!" |
| `a@b.co` / `senha123` | Valor limite: e-mail bem curto | "Your email or password is incorrect!" |
| `NAOEXISTE@TESTE.COM` / `senha123` | Válida: e-mail em maiúsculas | "Your email or password is incorrect!" |
| `naoexiste@teste.com` / `a` | Valor limite: senha com 1 caractere | "Your email or password is incorrect!" |
| `naoexiste@teste.com` / senha com 50+ caracteres | Válida: senha longa | "Your email or password is incorrect!" |
| `emailsemarroba.com` / `senha123` | Inválida: e-mail sem @ | Navegador bloqueia o envio |
| `teste@` / `senha123` | Inválida: e-mail sem domínio | Navegador bloqueia o envio |
| vazio / `senha123` | Inválida: e-mail vazio | Navegador bloqueia o envio |
| `naoexiste@teste.com` / vazio | Inválida: senha vazia | Navegador bloqueia o envio |

### Registrar usuário (Test Case 1)

O combo de ano de nascimento do site vai de **1900 a 2021**, então esses são os limites da data.

| Entrada (nome / data de nascimento) | Classe | Resultado esperado |
|---|---|---|
| `Joao` / 10 May 2000 | Fluxo do Test Case 1 | Conta criada, logado e excluída |
| `A` / 1 January 1900 | Valor limite inferior (nome com 1 letra, menor data) | Conta criada, logado e excluída |
| `Maria` / 31 December 2021 | Valor limite superior (maior data) | Conta criada, logado e excluída |
| `José Antônio` / 29 February 2000 | Válida: nome com acento e espaço, ano bissexto | Conta criada, logado e excluída |
| Nome longo / 15 July 1985 | Válida: nome comprido | Conta criada, logado e excluída |
| Anos 1899 e 2022 | Fora do limite | Não existem no combo |
| nome vazio / `aluno@teste.com` | Inválida: nome vazio | Navegador bloqueia o envio |
| `Joao` / e-mail vazio | Inválida: e-mail vazio | Navegador bloqueia o envio |
| `Joao` / `emailsemarroba.com` | Inválida: e-mail sem @ | Navegador bloqueia o envio |
| `Joao` / `admin@admin.com` | Inválida: e-mail já cadastrado | "Email Address already exist!" |

Cada cadastro usa um e-mail novo (`aluno<timestamp>@teste.com`) e a conta é excluída no final (passos 17 e 18),
para não sujar o site.

## Resultado

```
Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
```
