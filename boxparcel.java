class BoxParcel extends Parcel {
    private final int length;
    private final int width;
    private final int height;

    BoxParcel(String tracking, String receiver, double weight, int length, int width, int height)
            throws ParcelValidationException {
        this(tracking, receiver, weight, length, width, height, 50);
    }

    protected BoxParcel(String tracking, String receiver, double weight, int length, int width, int height,
                        double maxWeight) throws ParcelValidationException {
        super(tracking, receiver, weight, maxWeight);
        if (length <= 0 || width <= 0 || height <= 0)
            throw new ParcelValidationException("Length, width, and height must be greater than 0.");
        this.length = length;
        this.width = width;
        this.height = height;
    }

    String getType() { return "Box"; }
    int getLength() { return length; }
    int getWidth() { return width; }
    int getHeight() { return height; }
    double getVolumetricWeight() { return ((double) length * width * height) / 3500.0; }
    double getChargeableWeight() { return Math.ceil(Math.max(getActualWeight(), getVolumetricWeight())); }
    double getBaseFee() { return 40 * getChargeableWeight(); }
}
