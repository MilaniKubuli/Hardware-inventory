package hardware.hardwareinventory.models;

import java.math.BigDecimal;

public class CartItem {
    private int cartId;
    private int userId;
    private Items item;
    private int quantity;

    public CartItem(int cartId, int userId, Items item, int quantity) {
        this.cartId = cartId;
        this.userId = userId;
        this.item = item;
        this.quantity = quantity;
    }

    public int getCartId() {
        return cartId;
    }

    public int getUserId() {
        return userId;
    }

    public Items getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getLineTotal() {
        return item.getPrice().multiply(new BigDecimal(quantity));
    }
}
