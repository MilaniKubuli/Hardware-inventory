package hardware.hardwareinventory.models;

public class LoginResponse {
    private boolean success;
    private String message;
    private int userId;
    private String username;
    private String role;

    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public LoginResponse(boolean success, String message, User user) {
        this.success = success;
        this.message = message;

        if (user != null) {
            this.userId = user.getUserId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String toJson() {
        return "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + safe(message) + "\","
                + "\"userId\":" + userId + ","
                + "\"username\":\"" + safe(username) + "\","
                + "\"role\":\"" + safe(role) + "\""
                + "}";
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}