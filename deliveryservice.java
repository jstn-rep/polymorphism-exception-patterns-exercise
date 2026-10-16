enum DeliveryService {
    STANDARD(new StandardStrategy()),
    EXPRESS(new ExpressStrategy()),
    SAME_DAY(new SameDayStrategy());

    private final DeliveryStrategy strategy;
    DeliveryService(DeliveryStrategy strategy) { this.strategy = strategy; }

    String displayName() {
        return switch (this) {
            case STANDARD -> "Standard";
            case EXPRESS -> "Express";
            case SAME_DAY -> "Same Day";
        };
    }

    double calculateFee(double baseFee, double chargeableWeight) throws ParcelValidationException {
        if (!Double.isFinite(baseFee) || baseFee <= 0)
            throw new ParcelValidationException("Base fee must be a finite amount greater than 0.");
        if (!Double.isFinite(chargeableWeight) || chargeableWeight <= 0)
            throw new ParcelValidationException("Chargeable weight must be finite and greater than 0.");
        double fee = strategy.calculateFee(baseFee, chargeableWeight);
        if (!Double.isFinite(fee) || fee <= 0)
            throw new ParcelValidationException("Calculated delivery fee is invalid.");
        return fee;
    }

    static DeliveryService fromChoice(int choice) {
        return switch (choice) {
            case 1 -> STANDARD;
            case 2 -> EXPRESS;
            case 3 -> SAME_DAY;
            default -> throw new IllegalArgumentException("Invalid delivery service choice");
        };
    }
}
