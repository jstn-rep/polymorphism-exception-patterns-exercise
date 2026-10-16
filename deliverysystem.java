import java.util.Scanner;
import java.util.NoSuchElementException;

public class deliverysystem {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final ParcelManager PARCELS = ParcelManager.getInstance();

    public static void main(String[] args) {
        boolean running = true;
        try {
            while (running) {
                System.out.println("\n=== Parcel Delivery System ===");
                System.out.println("1. Add Parcel");
                System.out.println("2. View All Parcels");
                System.out.println("3. Change Delivery Service");
                System.out.println("4. Mark as Delivered");
                System.out.println("5. Exit");
                int choice = readMenuChoice();

                switch (choice) {
                    case 1 -> addParcel();
                    case 2 -> viewParcels();
                    case 3 -> changeService();
                    case 4 -> markDelivered();
                    case 5 -> {
                        running = false;
                        System.out.println("Goodbye!");
                    }
                    default -> throw new IllegalStateException("Unexpected menu option");
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("\nInput ended. Goodbye!");
        }
    }

    private static void addParcel() {
        System.out.println("\nParcel type: 1. Document  2. Box  3. Fragile");
        int type = readInt("Choose parcel type: ", 1, 3);
        String tracking;
        while (true) {
            tracking = readLine("Tracking Number (ABC followed by 4 digits): ");
            try {
                Parcel.validateTrackingNumber(tracking);
                if (PARCELS.contains(tracking)) {
                    throw new ParcelValidationException("Tracking number already exists.");
                }
                break;
            } catch (ParcelValidationException ex) {
                System.out.println(ex.getMessage());
            }
        }

        String receiver = readNonEmpty("Receiver Name: ");
        double maxWeight = type == 1 ? 0.5 : type == 3 ? 30 : 50;
        double weight = readPositiveDouble("Weight in kg (maximum " + maxWeight + "): ", maxWeight);
        Parcel parcel;
        try {
            if (type == 1) {
                int pages = readInt("Number of Pages (1-100): ", 1, 100);
                parcel = new DocumentParcel(tracking, receiver, weight, pages);
            } else {
                int length = readPositiveInt("Length in cm: ");
                int width = readPositiveInt("Width in cm: ");
                int height = readPositiveInt("Height in cm: ");
                if (type == 2) {
                    parcel = new BoxParcel(tracking, receiver, weight, length, width, height);
                } else {
                    String note = readNonEmpty("Handling Note: ");
                    parcel = new FragileParcel(tracking, receiver, weight, length, width, height, note);
                }
            }
        } catch (ParcelValidationException ex) {
            System.out.println("Invalid parcel: " + ex.getMessage());
            System.out.println("Please start Add Parcel again with valid details.");
            return;
        }

        DeliveryService service = selectAllowedService(parcel);
        try {
            parcel.setDeliveryService(service);
            PARCELS.add(parcel);
            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n", parcel.getDeliveryFee());
        } catch (ParcelValidationException ex) {
            System.out.println("Invalid parcel: " + ex.getMessage());
        }
    }

    private static DeliveryService selectAllowedService(Parcel parcel) {
        while (true) {
            DeliveryService service = chooseService();
            try {
                service.calculateFee(parcel.getBaseFee(), parcel.getChargeableWeight());
                return service;
            } catch (ParcelValidationException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static DeliveryService chooseService() {
        System.out.println("Delivery service: 1. Standard  2. Express  3. Same Day");
        return DeliveryService.fromChoice(readInt("Choose delivery service: ", 1, 3));
    }

    private static void viewParcels() {
        if (PARCELS.isEmpty()) {
            System.out.println("No parcels found.");
            return;
        }
        System.out.printf("%-10s %-10s %-20s %12s %-12s %14s %-10s%n",
                "Tracking", "Type", "Receiver", "Chargeable kg", "Service", "Fee (Php)", "Status");
        System.out.println("-".repeat(96));
        for (Parcel parcel : PARCELS.getAll()) {
            System.out.printf("%-10s %-10s %-20s %12.2f %-12s %14.2f %-10s%n",
                    parcel.getTrackingNumber(), parcel.getType(), parcel.getReceiverName(),
                    parcel.getChargeableWeight(), parcel.getDeliveryService().displayName(), parcel.getDeliveryFee(),
                    parcel.getStatus().displayName());
        }
    }

    private static void changeService() {
        Parcel parcel = findParcel();
        if (parcel == null) return;
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            System.out.println("Parcel already delivered!");
            return;
        }

        DeliveryService newService = chooseService();
        try {
            newService.calculateFee(parcel.getBaseFee(), parcel.getChargeableWeight());
            parcel.setDeliveryService(newService);
            System.out.printf("Delivery service changed! New fee: Php %.2f%n", parcel.getDeliveryFee());
        } catch (ParcelValidationException ex) {
            // The service is only changed after eligibility succeeds, so the old service remains in place.
            System.out.println(ex.getMessage());
        }
    }

    private static void markDelivered() {
        Parcel parcel = findParcel();
        if (parcel == null) return;
        if (parcel.getStatus() == ParcelStatus.DELIVERED) {
            System.out.println("Parcel was already delivered.");
        } else {
            parcel.markDelivered();
            System.out.println("Parcel delivered successfully!");
        }
    }

    private static Parcel findParcel() {
        while (true) {
            String tracking = readLine("Tracking Number: ");
            try {
                Parcel.validateTrackingNumber(tracking);
            } catch (ParcelValidationException ex) {
                System.out.println("Parcel not found!");
                continue;
            }
            Parcel parcel = PARCELS.find(tracking);
            if (parcel == null) System.out.println("Parcel not found!");
            return parcel;
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            String input = readLine(prompt);
            try {
                if (!input.matches("[1-9][0-9]*")) throw new NumberFormatException();
                int value = Integer.parseInt(input);
                if (value < min || value > max) throw new NumberFormatException();
                return value;
            } catch (NumberFormatException ex) {
                System.out.printf("Enter a whole number from %d to %d without leading zeros.%n", min, max);
            }
        }
    }

    private static int readMenuChoice() {
        while (true) {
            String input = readLine("Choose an option (1-5): ");
            if (input.matches("[1-5]")) return input.charAt(0) - '0';
            System.out.println("Enter exactly one digit from 1 to 5.");
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            String input = readLine(prompt);
            try {
                if (!input.matches("[1-9][0-9]*")) throw new NumberFormatException();
                int value = Integer.parseInt(input);
                return value;
            } catch (NumberFormatException ex) {
                System.out.println("Enter a whole number greater than 0 without leading zeros.");
            }
        }
    }

    private static double readPositiveDouble(String prompt, double max) {
        while (true) {
            try {
                String input = readLine(prompt);
                if (!input.matches("[0-9]+(?:\\.[0-9]+)?")) throw new NumberFormatException();
                double value = Double.parseDouble(input);
                if (!Double.isFinite(value) || value <= 0 || value > max) throw new NumberFormatException();
                return value;
            } catch (NumberFormatException ex) {
                System.out.printf("Enter a decimal amount greater than 0 and no more than %.1f kg.%n", max);
            }
        }
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) return value;
            System.out.println("This value cannot be empty.");
        }
    }

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine().trim();
    }

}
