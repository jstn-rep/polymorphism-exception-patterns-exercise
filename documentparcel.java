class DocumentParcel extends Parcel {
    private final int numberOfPages;

    DocumentParcel(String tracking, String receiver, double weight, int numberOfPages)
            throws ParcelValidationException {
        super(tracking, receiver, weight, 0.5);
        if (numberOfPages < 1 || numberOfPages > 100)
            throw new ParcelValidationException("Number of pages must be from 1 to 100.");
        this.numberOfPages = numberOfPages;
    }

    String getType() { return "Document"; }
    int getNumberOfPages() { return numberOfPages; }
    double getChargeableWeight() { return getActualWeight(); }
    double getBaseFee() { return 50; }
}
