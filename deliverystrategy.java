interface DeliveryStrategy {
    double calculateFee(double baseFee, double chargeableWeight) throws ParcelValidationException;
}
