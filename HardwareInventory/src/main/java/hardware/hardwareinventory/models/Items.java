package hardware.hardwareinventory.models;

import java.math.BigDecimal;

public class Items {
    private int itemId;
    private String name;
    private String color;
    private String description;
    private BigDecimal price;
    private int quantity;
    private boolean available;

    public Items() {
    }

    public Items(int itemId, String name, String color, String description,
            BigDecimal price, int quantity, boolean available) {
        this.itemId = itemId;
        this.name = name;
        this.color = color;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.available = available;
    }

    public int getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public String getAvailabilityText() {
        if (available && quantity > 0) {
            return "Yes";
        }
        return "No";
    }

    public String toJson() {
        return "{"
                + "\"itemId\":" + itemId + ","
                + "\"name\":\"" + name + "\","
                + "\"color\":\"" + color + "\","
                + "\"description\":\"" + description + "\","
                + "\"price\":" + price + ","
                + "\"quantity\":" + quantity + ","
                + "\"available\":" + available
                + "}";
    }
}
