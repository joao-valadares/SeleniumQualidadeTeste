import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import io.github.bonigarcia.wdm.WebDriverManager;

// Test Case 1: Register User
public class RegistrarUsuarioTest {

	protected WebDriver driver;

	@BeforeEach
	public void createDriver() {
		// 1. Launch browser
		ChromeOptions options = new ChromeOptions();
		// bloqueia os anuncios do site, que abrem uma tela por cima e atrapalham os cliques
		options.addArguments("--host-resolver-rules=MAP *.googlesyndication.com 0.0.0.0, MAP *.doubleclick.net 0.0.0.0");
		driver = WebDriverManager.chromedriver().capabilities(options).create();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();

		// 2. Navigate to url
		driver.get("https://automationexercise.com/");
	}

	@AfterEach
	public void quitDriver() {
		driver.quit();
	}

	// Passos 3, 4 e 5: home visivel, clicar em 'Signup / Login' e ver 'New User Signup!'
	private void abrirTelaDeCadastro() {
		assertTrue(driver.findElement(By.cssSelector("div.logo img")).isDisplayed());
		assertEquals("Automation Exercise", driver.getTitle());

		driver.findElement(By.cssSelector("a[href='/login']")).click();

		assertEquals("New User Signup!", driver.findElement(By.cssSelector("div.signup-form h2")).getText());
	}

	// Passos 6 e 7: preencher nome e e-mail e clicar em 'Signup'
	private void preencherNomeEEmail(String nome, String email) {
		driver.findElement(By.cssSelector("input[data-qa='signup-name']")).sendKeys(nome);
		driver.findElement(By.cssSelector("input[data-qa='signup-email']")).sendKeys(email);
		driver.findElement(By.cssSelector("button[data-qa='signup-button']")).click();
	}

	private void escrever(String dataQa, String texto) {
		driver.findElement(By.cssSelector("[data-qa='" + dataQa + "']")).sendKeys(texto);
	}

	private void clicar(By localizador) {
		WebElement elemento = driver.findElement(localizador);
		// rola a pagina ate o elemento para ele nao ficar escondido
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", elemento);
		elemento.click();
	}

	// Fluxo completo do Test Case 1 (passos 1 a 18)
	private void registrarUsuario(String nome, String dia, String mes, String ano) {
		// e-mail diferente a cada execucao, senao o site diz que o e-mail ja existe
		String email = "aluno" + System.currentTimeMillis() + "@teste.com";

		abrirTelaDeCadastro();
		preencherNomeEEmail(nome, email);

		// 8. Verify that 'ENTER ACCOUNT INFORMATION' is visible
		WebElement titulo = driver.findElement(By.cssSelector("div.login-form h2 b"));
		assertTrue(titulo.isDisplayed());
		assertEquals("ENTER ACCOUNT INFORMATION", titulo.getText().toUpperCase());

		// 9. Fill details: Title, Name, Email, Password, Date of birth
		driver.findElement(By.id("id_gender1")).click();
		assertEquals(nome, driver.findElement(By.cssSelector("[data-qa='name']")).getAttribute("value"));
		assertEquals(email, driver.findElement(By.cssSelector("[data-qa='email']")).getAttribute("value"));
		escrever("password", "senha123");
		new Select(driver.findElement(By.cssSelector("[data-qa='days']"))).selectByVisibleText(dia);
		new Select(driver.findElement(By.cssSelector("[data-qa='months']"))).selectByVisibleText(mes);
		new Select(driver.findElement(By.cssSelector("[data-qa='years']"))).selectByVisibleText(ano);

		// 10 e 11. Checkboxes de newsletter e ofertas
		clicar(By.id("newsletter"));
		clicar(By.id("optin"));
		assertTrue(driver.findElement(By.id("newsletter")).isSelected());
		assertTrue(driver.findElement(By.id("optin")).isSelected());

		// 12. Fill details: First name, Last name, Company, Address, Address2, Country, State, City, Zipcode, Mobile Number
		escrever("first_name", nome);
		escrever("last_name", "Silva");
		escrever("company", "Faculdade");
		escrever("address", "Rua Teste, 100");
		escrever("address2", "Apto 1");
		new Select(driver.findElement(By.cssSelector("[data-qa='country']"))).selectByVisibleText("Canada");
		escrever("state", "Ontario");
		escrever("city", "Toronto");
		escrever("zipcode", "12345");
		escrever("mobile_number", "11999999999");

		// 13. Click 'Create Account button'
		clicar(By.cssSelector("[data-qa='create-account']"));

		// 14. Verify that 'ACCOUNT CREATED!' is visible
		assertEquals("ACCOUNT CREATED!",
				driver.findElement(By.cssSelector("[data-qa='account-created']")).getText().toUpperCase());

		// 15. Click 'Continue' button
		clicar(By.cssSelector("[data-qa='continue-button']"));

		// 16. Verify that 'Logged in as username' is visible
		WebElement logado = driver.findElement(By.xpath("//a[contains(., 'Logged in as')]"));
		assertTrue(logado.isDisplayed());
		assertEquals("Logged in as " + nome, logado.getText().trim());

		// 17. Click 'Delete Account' button
		clicar(By.cssSelector("a[href='/delete_account']"));

		// 18. Verify that 'ACCOUNT DELETED!' is visible and click 'Continue' button
		assertEquals("ACCOUNT DELETED!",
				driver.findElement(By.cssSelector("[data-qa='account-deleted']")).getText().toUpperCase());
		clicar(By.cssSelector("[data-qa='continue-button']"));
	}

	@Test
	public void testaRegistrarUsuario() {
		registrarUsuario("Joao", "10", "May", "2000");
	}

	// Classes validas e valores limite da data de nascimento (o site so aceita de 1900 ate 2021)
	// e do tamanho do nome.
	@ParameterizedTest
	@CsvSource({
		"A, 1, January, 1900",                        // limite inferior: nome com 1 letra e menor data
		"Maria, 31, December, 2021",                  // limite superior: maior data possivel
		"José Antônio, 29, February, 2000",           // nome com acento e espaco / 29 de fevereiro de ano bissexto
		"Nome Bem Comprido Para Testar O Campo, 15, July, 1985" // nome longo
	})
	public void testaRegistrarUsuarioComDadosDiferentes(String nome, String dia, String mes, String ano) {
		registrarUsuario(nome, dia, mes, ano);
	}

	// Valores fora do limite da data de nascimento nao existem nos combos
	@Test
	public void testaAnosForaDoLimite() {
		abrirTelaDeCadastro();
		preencherNomeEEmail("Joao", "aluno" + System.currentTimeMillis() + "@teste.com");

		String anos = driver.findElement(By.cssSelector("[data-qa='years']")).getText();
		assertTrue(anos.contains("1900"));
		assertTrue(anos.contains("2021"));
		assertFalse(anos.contains("1899"));
		assertFalse(anos.contains("2022"));
	}

	// Classes invalidas: o navegador nao deixa enviar o formulario de cadastro
	@ParameterizedTest
	@CsvSource({
		"'', aluno@teste.com",     // nome vazio
		"Joao, ''",                // e-mail vazio
		"Joao, emailsemarroba.com" // e-mail sem @
	})
	public void testaCadastroComCamposInvalidos(String nome, String email) {
		abrirTelaDeCadastro();
		preencherNomeEEmail(nome, email);

		assertTrue(driver.getCurrentUrl().contains("/login"));
		WebElement campoNome = driver.findElement(By.cssSelector("input[data-qa='signup-name']"));
		WebElement campoEmail = driver.findElement(By.cssSelector("input[data-qa='signup-email']"));
		String mensagem = campoNome.getAttribute("validationMessage") + campoEmail.getAttribute("validationMessage");
		assertFalse(mensagem.isEmpty());
	}

	// Classe invalida: e-mail que ja foi cadastrado
	@Test
	public void testaCadastroComEmailJaExistente() {
		abrirTelaDeCadastro();
		// conta fixa do proprio site de exemplo; se ainda nao existir, o teste falha avisando
		preencherNomeEEmail("Joao", "admin@admin.com");

		assertEquals("Email Address already exist!", driver.findElement(By.cssSelector("div.signup-form p")).getText());
	}
}
