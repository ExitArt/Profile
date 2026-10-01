package tests;

import org.junit.jupiter.api.Test;
import saucedemo.BaseTest;
import saucedemo.pages.ProductsPage; // Импортируем Page Object
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class InventoryTest extends BaseTest {

    @Test
    public void shouldHaveItemsInStore() {
        // Инициализируем страницу продуктов
        ProductsPage productsPage = new ProductsPage(page);

        // Получаем количество товаров через метод класса страницы
        int itemsCount = productsPage.getInventoryItemsCount();

        // Используем умный ассерт Playwright без хардкода локатора
        assertThat(productsPage.getInventoryItemsLocator()).hasCount(itemsCount);

        System.out.println("Products at list: " + itemsCount);
    }
}