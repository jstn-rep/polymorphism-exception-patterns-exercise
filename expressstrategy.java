class ExpressStrategy implements DeliveryStrategy {
    public double calculateFee(double baseFee, double chargeableWeight) { return baseFee * 1.5; }
}
