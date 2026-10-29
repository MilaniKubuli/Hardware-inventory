package hardware.hardwareinventory.database;

import hardware.hardwareinventory.models.CartItem;
import hardware.hardwareinventory.models.Items;
import hardware.hardwareinventory.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public void addItem(int userId, int itemId, int quantity) throws SQLException {
        String sql = "INSERT INTO cart (user_id, item_id, quantity) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setInt(2, itemId);
            statement.setInt(3, quantity);
            statement.executeUpdate();
        }
    }

    public void removeItem(int userId, int itemId) throws SQLException {
        String selectSql = "SELECT SUM(quantity) AS total_quantity FROM cart WHERE user_id = ? AND item_id = ?";
        String deleteSql = "DELETE FROM cart WHERE user_id = ? AND item_id = ?";
        String updateStockSql = "UPDATE items SET quantity = quantity + ?, available = TRUE WHERE item_id = ?";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);

            int removedQuantity = 0;

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setInt(1, userId);
                selectStatement.setInt(2, itemId);

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        removedQuantity = resultSet.getInt("total_quantity");
                    }
                }
            }

            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setInt(1, userId);
                deleteStatement.setInt(2, itemId);
                deleteStatement.executeUpdate();
            }

            if (removedQuantity > 0) {
                try (PreparedStatement updateStatement = connection.prepareStatement(updateStockSql)) {
                    updateStatement.setInt(1, removedQuantity);
                    updateStatement.setInt(2, itemId);
                    updateStatement.executeUpdate();
                }
            }

            connection.commit();
        }
    }

    public List<CartItem> findByUser(int userId) throws SQLException {
        String sql = "SELECT MIN(c.cart_id) AS cart_id, c.user_id, i.item_id, i.name, i.color, "
                + "i.description, i.price, i.quantity AS stock_quantity, i.available, "
                + "SUM(c.quantity) AS cart_quantity "
                + "FROM cart c INNER JOIN items i ON c.item_id = i.item_id "
                + "WHERE c.user_id = ? "
                + "GROUP BY c.user_id, i.item_id, i.name, i.color, i.description, i.price, i.quantity, i.available "
                + "ORDER BY i.item_id";

        List<CartItem> cartItems = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Items item = new Items(
                            resultSet.getInt("item_id"),
                            resultSet.getString("name"),
                            resultSet.getString("color"),
                            resultSet.getString("description"),
                            resultSet.getBigDecimal("price"),
                            resultSet.getInt("stock_quantity"),
                            resultSet.getBoolean("available"));

                    cartItems.add(new CartItem(
                            resultSet.getInt("cart_id"),
                            resultSet.getInt("user_id"),
                            item,
                            resultSet.getInt("cart_quantity")));
                }
            }
        }

        return cartItems;
    }
}