abstract class Parcel {
    private final String trackingNumber;
    private final String receiverName;
    private final double actualWeight;
    private DeliveryService deliveryService;
    private ParcelStatus status = ParcelStatus.PENDING;

    Parcel(String trackingNumber, String receiverName, double actualWeight, double maxWeight)
            throws ParcelValidationException {
        validateTrackingNumber(trackingNumber);
        String normalizedName = receiverName == null ? "" : receiverName.trim();
        if (normalizedName.isEmpty())
            throw new ParcelValidationException("Receiver name cannot be empty.");
        if (!Double.isFinite(maxWeight) || maxWeight <= 0)
            throw new IllegalArgumentException("Maximum weight must be finite and greater than 0.");
        if (!Double.isFinite(actualWeight) || actualWeight <= 0)
            throw new ParcelValidationException("Weight must be greater than 0.");
        if (actualWeight > maxWeight)
            throw new ParcelValidationException("Maximum weight for this parcel type is " + maxWeight + " kg.");
        this.trackingNumber = trackingNumber;
        this.receiverName = normalizedName;
        this.actualWeight = actualWeight;
    }

    static void validateTrackingNumber(String trackingNumber) throws ParcelValidationException {
        if (trackingNumber == null || !trackingNumber.matches("ABC\\d{4}"))
            throw new ParcelValidationException("Tracking number must be ABC followed by 4 digits (for example, ABC0001).");
    }

    abstract String getType();
    abstract double getChargeableWeight();
    abstract double getBaseFee();

    String getTrackingNumber() { return trackingNumber; }
    String getReceiverName() { return receiverName; }
    double getActualWeight() { return actualWeight; }
    ParcelStatus getStatus() { return status; }
    DeliveryService getDeliveryService() { return deliveryService; }
    void setDeliveryService(DeliveryService service) throws ParcelValidationException {
        if (service == null) throw new ParcelValidationException("A delivery service must be selected.");
        service.calculateFee(getBaseFee(), getChargeableWeight());
        this.deliveryService = service;
    }
    void markDelivered() { status = ParcelStatus.DELIVERED; }

    double getDeliveryFee() {
        if (deliveryService == null)
            throw new IllegalStateException("A delivery service has not been selected.");
        try {
            return deliveryService.calculateFee(getBaseFee(), getChargeableWeight());
        } catch (ParcelValidationException ex) {
            throw new IllegalStateException("Stored delivery service is not eligible.", ex);
        }
    }
}
