enum ParcelStatus {
    PENDING("Pending"), DELIVERED("Delivered");

    private final String displayName;
    ParcelStatus(String displayName) { this.displayName = displayName; }
    String displayName() { return displayName; }
}
