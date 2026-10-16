import java.util.LinkedHashMap;
import java.util.Map;

final class ParcelManager {
    private static final ParcelManager INSTANCE = new ParcelManager();
    private final Map<String, Parcel> parcels = new LinkedHashMap<>();

    private ParcelManager() { }
    static ParcelManager getInstance() { return INSTANCE; }

    void add(Parcel parcel) throws ParcelValidationException {
        if (parcel == null) throw new ParcelValidationException("Cannot add a null parcel.");
        if (parcel.getDeliveryService() == null)
            throw new ParcelValidationException("A delivery service must be assigned before adding a parcel.");
        if (parcels.containsKey(parcel.getTrackingNumber()))
            throw new ParcelValidationException("Tracking number already exists.");
        parcels.put(parcel.getTrackingNumber(), parcel);
    }

    boolean contains(String trackingNumber) { return parcels.containsKey(trackingNumber); }
    Parcel find(String trackingNumber) { return parcels.get(trackingNumber); }
    boolean isEmpty() { return parcels.isEmpty(); }
    Iterable<Parcel> getAll() { return parcels.values(); }
}
