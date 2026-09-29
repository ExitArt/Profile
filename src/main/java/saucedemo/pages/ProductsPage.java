package saucedemo.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.ArrayList;
import java.util.List;

public class ProductsPage {
    private final Page page;

    // Все локаторы страницы теперь собраны в одном месте
    private final Locator addToCartButtons;
    private final Locator removeFromCartButtons;
    private final Locator productNames;
    private final Locator productImages;
    private final Locator inventoryItems; // Новый локатор для карточек

    public ProductsPage(Page page) {
        this.page = page;
        this.addToCartButtons = page.locator(".btn_primary.btn_inventory");
        this.removeFromCartButtons = page.locator(".btn_secondary.btn_inventory");
        this.productNames = page.locator(".inventory_item_name");
        this.productImages = page.locator(".inventory_item_img img");

        // Инициализируем локатор карточек товаров
        this.inventoryItems = page.locator(".inventory_item[data-test='inventory-item']");
    }

    // Возвращаем локатор карточек для проверок внутри ассертов
    public Locator getInventoryItemsLocator() {
        return this.inventoryItems;
    }

    // Получаем текущее количество карточек на странице
    public int getInventoryItemsCount() {
        return this.inventoryItems.count();
    }

    // --- Остальные методы страницы (остаются без изменений) ---
    public Locator getProductNamesLocator() { return this.productNames; }
    public Locator getProductImagesLocator() { return this.productImages; }
    public List<String> getAllProductNames() { return productNames.allTextContents(); }

    public List<String> getAllProductImagesSources() {
        int imgCount = productImages.count();
        List<String> sources = new ArrayList<>();
        for (int i = 0; i < imgCount; i++) {
            sources.add(productImages.nth(i).getAttribute("src"));
        }
        return sources;
    }

    public void addAllProductsToCart() {
        while (addToCartButtons.first().isVisible()) { addToCartButtons.first().click(); }
    }

    public List<Locator> getAllRemoveButtons() { return removeFromCartButtons.all(); }
    public int getAddToCartButtonsCount() { return addToCartButtons.count(); }
}