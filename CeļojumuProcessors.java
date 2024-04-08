import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TripProcessor {
    private static final String FILE_NAME = "db.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        createFileIfNotExists();

        while (true) {
            System.out.println("\nCeļojumu aģentūras sistēma:");
            System.out.println("1. Apskatīt ceļojumus");
            System.out.println("2. Pievienot jaunu ceļojumu");
            System.out.println("3. Izdzēst ceļojumu");
            System.out.println("4. Labot ceļojuma informāciju");
            System.out.println("5. Sakārtot ceļojumus pēc datumiem");
            System.out.println("6. Atrast ceļojumus pēc cenas");
            System.out.println("7. Aprēķināt ceļojumu vidējo cenu");
            System.out.println("8. Iziet");

            System.out.print("Ievadiet izvēli: ");
            String choice = SCANNER.nextLine();

            switch (choice) {
                case "1":
                    showTrips();
                    break;
                case "2":
                    addTrip();
                    break;
                case "3":
                    deleteTrip();
                    break;
                case "4":
                    editTrip();
                    break;
                case "5":
                    sortTripsByDate();
                    break;
                case "6":
                    findTripsByPrice();
                    break;
                case "7":
                    calculateAveragePrice();
                    break;
                case "8":
                    System.out.println("Programma tiek izbeigta.");
                    SCANNER.close();
                    return;
                default:
                    System.out.println("Nepareiza izvēle. Lūdzu, ievadiet skaitli no 1 līdz 8.");
            }
        }
    }

    private static void createFileIfNotExists() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās izveidot datņu failu: " + e.getMessage());
        }
    }

    private static void showTrips() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās nolasīt datņu failu: " + e.getMessage());
        }
    }

    private static void addTrip() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            System.out.print("Ievadiet ceļojuma informāciju (identifikators;pilsēta;dd/MM/yyyy;dienas;cena;transporta veids): ");
            String tripInfo = SCANNER.nextLine();
            writer.write(tripInfo + "\n");
            System.out.println("Ceļojums veiksmīgi pievienots.");
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās pievienot ceļojumu: " + e.getMessage());
        }
    }

    private static void deleteTrip() {
        try {
            List<String> trips = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    trips.add(line);
                }
            }
            System.out.print("Ievadiet ceļojuma identifikatoru, ko izdzēst: ");
            String tripIdToDelete = SCANNER.nextLine();
            boolean found = false;
            for (int i = 0; i < trips.size(); i++) {
                if (trips.get(i).startsWith(tripIdToDelete + ";")) {
                    trips.remove(i);
                    found = true;
                    break;
                }
            }
            if (found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                    for (String trip : trips) {
                        writer.write(trip + "\n");
                    }
                    System.out.println("Ceļojums veiksmīgi izdzēsts.");
                }
            } else {
                System.out.println("Ceļojums ar norādīto identifikatoru nav atrasts.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās dzēst ceļojumu: " + e.getMessage());
        }
    }

    private static void editTrip() {
        try {
            List<String> trips = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    trips.add(line);
                }
            }
            System.out.print("Ievadiet ceļojuma identifikatoru, ko labot: ");
            String tripIdToEdit = SCANNER.nextLine();
            boolean found = false;
            for (int i = 0; i < trips.size(); i++) {
                if (trips.get(i).startsWith(tripIdToEdit + ";")) {
                    System.out.print("Ievadiet jauno ceļojuma informāciju (pilsēta;dd/MM/yyyy;dienas;cena;transporta veids): ");
                    String newTripInfo = SCANNER.nextLine();
                    trips.set(i, tripIdToEdit + ";" + newTripInfo);
                    found = true;
                    break;
                }
            }
            if (found) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                    for (String trip : trips) {
                        writer.write(trip + "\n");
                    }
                    System.out.println("Ceļojuma informācija veiksmīgi labota.");
                }
            } else {
                System.out.println("Ceļojums ar norādīto identifikatoru nav atrasts.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās labot ceļojuma informāciju: " + e.getMessage());
        }
    }

    private static void sortTripsByDate() {
        try {
            List<String> trips = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    trips.add(line);
                }
            }
            Collections.sort(trips, Comparator.comparing(trip -> LocalDate.parse(trip.split(";")[2], DATE_FORMATTER)));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (String trip : trips) {
                    writer.write(trip + "\n");
                }
                System.out.println("Ceļojumi sakārtoti pēc datuma veiksmīgi.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās sakārtot ceļojumus pēc datuma: " + e.getMessage());
        }
    }

    private static void findTripsByPrice() {
        System.out.print("Ievadiet maksimālo ceļojuma cenu: ");
        double maxPrice = Double.parseDouble(SCANNER.nextLine());
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] tripParts = line.split(";");
                double price = Double.parseDouble(tripParts[4]);
                if (price <= maxPrice) {
                    System.out.println(line);
                    found = true;
                }
            }
            if (!found) {
                System.out.println("Nav atrasti ceļojumi ar norādīto cenu.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās atrast ceļojumus pēc cenas: " + e.getMessage());
        }
    }

    private static void calculateAveragePrice() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            double total = 0;
            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] tripParts = line.split(";");
                double price = Double.parseDouble(tripParts[4]);
                total += price;
                count++;
            }
            if (count > 0) {
                double average = total / count;
                System.out.println("Vidējā ceļojuma cena: " + String.format("%.2f", average));
            } else {
                System.out.println("Nav atrasti ceļojumi, lai aprēķinātu vidējo cenu.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda, neizdevās aprēķināt vidējo ceļojuma cenu: " + e.getMessage());
        }
    }
}
