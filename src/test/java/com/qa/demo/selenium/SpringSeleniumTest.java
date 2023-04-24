package com.qa.demo.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT) // loads the context (list of beans)
@Sql(scripts = { "classpath:cat-schema.sql", "classpath:cat-data.sql" })
public class SpringSeleniumTest {

  private WebDriver driver;

  @LocalServerPort
  private int port; // find the random port that the app is running on - will inject into the class the port its running on

  private WebDriverWait wait;

  @BeforeEach
  void init() {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--remote-allow-origins=*");
    this.driver = new ChromeDriver(options);

    this.driver.manage().window().maximize();

    // wait up to 3 seconds before doing anything - if it has to (not loaded yet)
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(3));
  }

  @Test
  void testTitle() {
    this.driver.get("http://localhost:" + port);
    WebElement title =
      this.driver.findElement(By.cssSelector("body > header > h1"));

    assertEquals("CATS", title.getText());
  }

  @Test
  void testCreate() {
    this.driver.get("http://localhost:" + port);

    // type name
    WebElement nameInput =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(By.cssSelector("#catName"))
        );
    nameInput.sendKeys("David Meowie");

    // type length
    WebElement lengthInput =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(By.cssSelector("#catLength"))
        );
    lengthInput.sendKeys("22");

    // check has whiskers
    WebElement whiskersCheck =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#catWhiskers")
          )
        );
    whiskersCheck.click();

    // check evil
    WebElement evilCheck =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(By.cssSelector("#catEvil"))
        );
    evilCheck.click();

    // click submit
    WebElement submitButton =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#catForm > div.mt-3 > button.btn.btn-success")
          )
        );
    submitButton.click();

    // find the created card
    WebElement createdCard =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#output > div:nth-child(2) > div > div")
          )
        );

    assertTrue(createdCard.getText().contains("David Meowie")); // check the text 'David Meowie' is in the card
    assertTrue(createdCard.getText().contains("Length: 22"));
    assertTrue(createdCard.getText().contains("Whiskers: true"));
    assertTrue(createdCard.getText().contains("Evil: true"));
  }

  @Test
  void testGetAll() throws InterruptedException {
    this.driver.get("http://localhost:" + port);

    WebElement card =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#output > div > div")
          )
        );

    assertTrue(card.getText().contains("Mr Bigglesworth")); // check the text Mr.Bigglesworth is in the card
  }

  @Test
  void testUpdate() {
    this.driver.get("http://localhost:" + port);

    // click update button on card
    WebElement updateButton =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#output > div > div > div > button:nth-child(5)")
          )
        );
    updateButton.click();

    // change name
    WebElement updateNameInput =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#updateForm > #catName")
          )
        );
    updateNameInput.clear();
    updateNameInput.sendKeys("Catrick Swayze");

    // change length
    WebElement updateLengthInput =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#updateForm > #catLength")
          )
        );
    updateLengthInput.clear();
    updateLengthInput.sendKeys("12");

    // change hasWhiskers
    WebElement updateHasWhiskers =
      this.driver.findElement(
          By.cssSelector("#updateForm > div:nth-child(5) > #catWhiskers")
        );
    updateHasWhiskers.click();

    // change evil
    WebElement updateEvil =
      this.driver.findElement(
          By.cssSelector("#updateForm > div:nth-child(6) > #catEvil")
        );
    updateEvil.click();

    // click submit
    WebElement submitButton =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#updateForm > div.mt-3 > button.btn.btn-success")
          )
        );
    submitButton.click();

    // click exit modal
    WebElement exitButton =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#myModal > div > div > div.modal-header > button")
          )
        );
    exitButton.click();

    // find the updated card
    WebElement updatedCard =
      this.wait.until(
          ExpectedConditions.elementToBeClickable(
            By.cssSelector("#output > div > div > div")
          )
        );

    assertTrue(updatedCard.getText().contains("Catrick Swayze")); // check the text 'Catrick Swayze' is in the card
    assertTrue(updatedCard.getText().contains("Length: 12"));
    assertTrue(updatedCard.getText().contains("Whiskers: false"));
    assertTrue(updatedCard.getText().contains("Evil: false"));
  }

  @AfterEach
  void tearDown() {
    //this.driver.close();
  }
}
