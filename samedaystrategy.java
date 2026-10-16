class SameDayStrategy implements DeliveryStrategy {
    public double calculateFee(double baseFee, double chargeableWeight) throws ParcelValidationException {
        if (chargeableWeight > 10) {
            throw new ParcelValidationException("Same Day delivery is only for parcels up to 10 kg!");
        }
        return (baseFee * 2) + 100;
    }
}
