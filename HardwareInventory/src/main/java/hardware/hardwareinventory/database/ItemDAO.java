package hardware.hardwareinventory.database;

import hardware.hardwareinventory.models.Items;
import hardware.hardwareinventory.utils.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {

    public List<Items> findAll() throws SQLException {
        String sql = "SELECT item_id, name, color, description, price, quantity, available FROM items ORDER BY item_id";
        List<Items> items = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                items.add(readItem(resultSet));
            }
        }

        return items;
    }

    public List<Items> search(String searchText) throws SQLException {
        String sql = "SELECT item_id, name, color, description, price, quantity, available "
                + "FROM items WHERE name LIKE ? OR description LIKE ? OR color LIKE ? ORDER BY item_id";

        List<Items> items = new ArrayList<>();
        String keyword = "%" + searchText + "%";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, keyword);
            statement.setString(2, keyword);
            statement.setString(3, keyword);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    items.add(readItem(resultSet));
                }
            }
        }

        return items;
    }

    public boolean reduceQuantity(int itemId, int quantity) throws SQLException {
        String sql = "UPDATE items SET quantity = quantity - ?, "
                + "available = CASE WHEN quantity - ? > 0 THEN TRUE ELSE FALSE END "
                + "WHERE item_id = ? AND quantity >= ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, quantity);
            statement.setInt(2, quantity);
            statement.setInt(3, itemId);
            statement.setInt(4, quantity);

            return statement.executeUpdate() > 0;
        }
    }

    public void addItem(Items item) throws SQLException {
        String sql = "INSERT INTO items (item_id, name, color, description, price, quantity, available) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, item.getItemId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getColor());
            statement.setString(4, item.getDescription());
            statement.setBigDecimal(5, item.getPrice());
            statement.setInt(6, item.getQuantity());
            statement.setBoolean(7, item.isAvailable());

            statement.executeUpdate();
        }
    }

    private Items readItem(ResultSet resultSet) throws SQLException {
        return new Items(
                resultSet.getInt("item_id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("price"),
                resultSet.getInt("quantity"),
                resultSet.getBoolean("available"));
    }
    
    public void updateItem(Items item) throws SQLException {
    String sql = "UPDATE items SET name = ?, color = ?, description = ?, price = ?, quantity = ?, available = ? "
            + "WHERE item_id = ?";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setString(1, item.getName());
        statement.setString(2, item.getColor());
        statement.setString(3, item.getDescription());
        statement.setBigDecimal(4, item.getPrice());
        statement.setInt(5, item.getQuantity());
        statement.setBoolean(6, item.isAvailable());
        statement.setInt(7, item.getItemId());

        statement.executeUpdate();
    }
}

    public int countItems() throws SQLException {
    String sql = "SELECT COUNT(*) AS total FROM items";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            return resultSet.getInt("total");
        }
    }

    return 0;
}

    public int countLowStockItems() throws SQLException {
    String sql = "SELECT COUNT(*) AS total FROM items WHERE quantity <= 5";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            return resultSet.getInt("total");
        }
    }

    return 0;
}

    public int countOutOfStockItems() throws SQLException {
    String sql = "SELECT COUNT(*) AS total FROM items WHERE quantity = 0 OR available = FALSE";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            return resultSet.getInt("total");
        }
    }

    return 0;
    }

    public java.math.BigDecimal inventoryValue() throws SQLException {
    String sql = "SELECT SUM(price * quantity) AS total_value FROM items";

    try (Connection connection = DatabaseConnection.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql);
         ResultSet resultSet = statement.executeQuery()) {

        if (resultSet.next()) {
            java.math.BigDecimal value = resultSet.getBigDecimal("total_value");
            if (value != null) {
                return value;
            }
        }
    }

    return java.math.BigDecimal.ZERO;
    }
}