package saucedemo;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.util.Collections;

public class BaseTest {
    // Убрали ключевое слово static. Теперь у каждого потока/теста будут свои изолированные объекты
    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeEach
    void setUp() {
        // 1. Инициализируем Playwright и Browser индивидуально для каждого теста
        playwright = Playwright.create();

        String browserParam = System.getProperty("chosen.browser", "chromium");
        String headlessParam = System.getProperty("chosen.headless", "true");

        if (System.getenv("CI") != null && System.getProperty("chosen.headless") == null) {
            headlessParam = "true";
        }
        boolean isHeadless = Boolean.parseBoolean(headlessParam);

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setArgs(Collections.singletonList("--no-sandbox")); // Важно для стабильности в параллели на CI

        if ("firefox".equalsIgnoreCase(browserParam)) {
            browser = playwright.firefox().launch(options);
        } else {
            browser = playwright.chromium().launch(options);
        }

        // 2. Создаем контекст и страницу в рамках текущего потока
        context = browser.newContext();
        page = context.newPage();

        // Задаем явный таймаут на действия внутри теста
        page.setDefaultTimeout(15000); // 15 секунд

        String usernameParam = System.getProperty("test.username", "standard_user");
        String passwordParam = System.getProperty("test.password", "secret_sauce");

        // Открываем страницу с ожиданием полной готовности сети
        page.navigate("https://saucedemo.com", new Page.NavigateOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));

        // Заполняем данные
        page.locator("[data-test='username']").fill(usernameParam);
        page.locator("[data-test='password']").fill(passwordParam);
        page.locator("[data-test='login-button']").click();

        try {
            // Ждем перехода на страницу каталога
            page.waitForURL("**/inventory.html", new Page.WaitForURLOptions().setTimeout(5000));
        } catch (PlaywrightException e) {
            System.err.println("ОШИБКА: Не удалось авторизоваться в потоке " + Thread.currentThread().getName() + "! Текущий URL: " + page.url());
            throw e;
        }
    }

    @AfterEach
    void tearDown() {
        // Закрываем все ресурсы текущего теста строго в обратном порядке
        if (page != null) page.close();
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}