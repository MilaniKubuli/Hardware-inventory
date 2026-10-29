package hardware.hardwareinventory.models;

public class RegistrationResponse {
    private final boolean success;
    private final String message;
    private final int userId;

    public RegistrationResponse(boolean success, String message, int userId) {
        this.success = success;
        this.message = message;
        this.userId = userId;
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

    public String toJson() {
        return "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + safe(message) + "\","
                + "\"userId\":" + userId
                + "}";
    }

    private String safe(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}