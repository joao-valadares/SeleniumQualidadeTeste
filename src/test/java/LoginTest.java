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
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;

import io.github.bonigarcia.wdm.WebDriverManager;

// Test Case 3: Login User with incorrect email and password
public class LoginTest {

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

	// Passos 3, 4 e 5: home visivel, clicar em 'Signup / Login' e ver 'Login to your account'
	private void abrirTelaDeLogin() {
		assertTrue(driver.findElement(By.cssSelector("div.logo img")).isDisplayed());
		assertEquals("Automation Exercise", driver.getTitle());

		driver.findElement(By.cssSelector("a[href='/login']")).click();

		assertEquals("Login to your account", driver.findElement(By.cssSelector("div.login-form h2")).getText());
	}

	// Passos 6 e 7: preencher e-mail e senha e clicar em 'login'
	private void fazerLogin(String email, String senha) {
		driver.findElement(By.cssSelector("input[data-qa='login-email']")).sendKeys(email);
		driver.findElement(By.cssSelector("input[data-qa='login-password']")).sendKeys(senha);
		driver.findElement(By.cssSelector("button[data-qa='login-button']")).click();
	}

	@Test
	public void testaLoginComEmailIncorreto() {
		abrirTelaDeLogin();
		fazerLogin("email.incorreto@teste.com", "senha123");

		// 8. Verify error 'Your email or password is incorrect!' is visible
		WebElement erro = driver.findElement(By.cssSelector("div.login-form p"));
		assertTrue(erro.isDisplayed());
		assertEquals("Your email or password is incorrect!", erro.getText());
	}

	// Classes validas de formato (o navegador deixa enviar), mas com credenciais incorretas:
	// todas devem mostrar a mensagem de erro.
	@ParameterizedTest
	@CsvSource({
		"naoexiste@teste.com, senha123",           // e-mail com formato valido mas nao cadastrado
		"a@b.co, senha123",                         // limite inferior: e-mail bem curto
		"NAOEXISTE@TESTE.COM, senha123",            // e-mail em letras maiusculas
		"naoexiste@teste.com, a",                   // limite inferior: senha com 1 caractere
		"naoexiste@teste.com, senhamuitograndesenhamuitograndesenhamuitogrande123" // senha longa
	})
	public void testaLoginComCredenciaisIncorretas(String email, String senha) {
		abrirTelaDeLogin();
		fazerLogin(email, senha);

		assertEquals("Your email or password is incorrect!",
				driver.findElement(By.cssSelector("div.login-form p")).getText());
	}

	// Classes invalidas: o proprio navegador bloqueia o envio (campos "required" e type="email"),
	// entao o usuario continua na tela de login sem a mensagem do site.
	@ParameterizedTest
	@CsvSource({
		"emailsemarroba.com, senha123", // e-mail sem @
		"teste@, senha123",             // e-mail sem dominio
		"'', senha123",                 // e-mail vazio
		"naoexiste@teste.com, ''"       // senha vazia
	})
	public void testaLoginComCamposInvalidos(String email, String senha) {
		abrirTelaDeLogin();
		fazerLogin(email, senha);

		assertTrue(driver.getCurrentUrl().contains("/login"));
		WebElement campoEmail = driver.findElement(By.cssSelector("input[data-qa='login-email']"));
		WebElement campoSenha = driver.findElement(By.cssSelector("input[data-qa='login-password']"));
		// validationMessage e a mensagem do navegador ("Preencha este campo", etc.)
		String mensagem = campoEmail.getAttribute("validationMessage") + campoSenha.getAttribute("validationMessage");
		assertFalse(mensagem.isEmpty());
	}
}
