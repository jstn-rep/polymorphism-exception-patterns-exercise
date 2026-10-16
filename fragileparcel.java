class FragileParcel extends BoxParcel {
    private final String handlingNote;

    FragileParcel(String tracking, String receiver, double weight, int length, int width, int height, String handlingNote)
            throws ParcelValidationException {
        super(tracking, receiver, weight, length, width, height, 30);
        String normalizedNote = handlingNote == null ? "" : handlingNote.trim();
        if (normalizedNote.isEmpty())
            throw new ParcelValidationException("Handling note cannot be empty.");
        this.handlingNote = normalizedNote;
    }

    String getType() { return "Fragile"; }
    String getHandlingNote() { return handlingNote; }
    double getBaseFee() { return super.getBaseFee() + 100; }
}
