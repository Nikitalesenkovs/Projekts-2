import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CeļojumuProcessors {
    private static final String FAILA_NOSAUKUMS = "db.csv";
    private static final DateTimeFormatter DATUMA_FORMATĒTĀJS = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DecimalFormat CENAS_FORMATĒTĀJS = new DecimalFormat("#.##");

    public static void main(String[] args) {
        Scanner skeneris = new Scanner(System.in);

        while (true) {
            System.out.println("\n1. Apskatīt faila saturu");
            System.out.println("2. Pievienot jaunu ceļojumu");
            System.out.println("3. Izdzēst ceļojuma informāciju");
            System.out.println("4. Labot ceļojuma informāciju");
            System.out.println("5. Sakārtot ceļojumus pēc datuma");
            System.out.println("6. Atrast ceļojumus ar norādīto cenu");
            System.out.println("7. Aprēķināt ceļojumu vidējo cenu");
            System.out.println("8. Iziet");

            System.out.print("Ievadiet savu izvēli: ");
            String izvēle = skeneris.nextLine();

            switch (izvēle) {
                case "1":
                    apskatītFailaSaturu();
                    break;
                case "2":
                    pievienotCeļojumu();
                    break;
                case "3":
                    izdzēstCeļojumu();
                    break;
                case "4":
                    labotCeļojumu();
                    break;
                case "5":
                    sakārtotCeļojumusPēcDatuma();
                    break;
                case "6":
                    atrastCeļojumusArCenu();
                    break;
                case "7":
                    aprēķinātCeļojumuVidējoCenu();
                    break;
                case "8":
                    System.out.println("Izejam no programmas.");
                    skeneris.close();
                    return;
                default:
                    System.out.println("Nederīga izvēle. Lūdzu, ievadiet skaitli no 1 līdz 8.");
            }
        }
    }

    private static void apskatītFailaSaturu() {
        try (BufferedReader lasītājs = new BufferedReader(new FileReader(FAILA_NOSAUKUMS))) {
            String rinda;
            while ((rinda = lasītājs.readLine()) != null) {
                System.out.println(rinda);
            }
        } catch (IOException e) {
            System.out.println("Kļūda lasot failu: " + e.getMessage());
        }
    }

    private static void pievienotCeļojumu() {
        try (BufferedWriter rakstītājs = new BufferedWriter(new FileWriter(FAILA_NOSAUKUMS, true))) {
            Scanner skeneris = new Scanner(System.in);

            System.out.print("Ievadiet ceļojuma identifikatoru: ");
            String id = skeneris.nextLine();

            System.out.print("Ievadiet pilsētu: ");
            String pilsēta = skeneris.nextLine();

            System.out.print("Ievadiet datumu (dd/MM/gggg): ");
            String datumaStr = skeneris.nextLine();
            LocalDate datums = LocalDate.parse(datumaStr, DATUMA_FORMATĒTĀJS);

            System.out.print("Ievadiet dienu skaitu: ");
            int dienas = Integer.parseInt(skeneris.nextLine());

            System.out.print("Ievadiet cenu: ");
            double cena = Double.parseDouble(skeneris.nextLine());

            System.out.print("Ievadiet transporta veidu: ");
            String transports = skeneris.nextLine();

            rakstītājs.write(String.format("%s;%s;%s;%d;%.2f;%s%n", id, pilsēta, datums.format(DATUMA_FORMATĒTĀJS), dienas, cena, transports));
            System.out.println("Ceļojums veiksmīgi pievienots.");
        } catch (IOException e) {
            System.out.println("Kļūda rakstot failā: " + e.getMessage());
        }
    }

    private static void izdzēstCeļojumu() {
        Scanner skeneris = new Scanner(System.in);
        System.out.print("Ievadiet ceļojuma identifikatoru, ko izdzēst: ");
        String idIzdzēst = skeneris.nextLine();

        try {
            List<String> rindiņas = Files.lines(new File(FAILA_NOSAUKUMS).toPath())
                    .filter(rinda -> !rinda.startsWith(idIzdzēst + ";"))
                    .collect(Collectors.toList());

            Files.write(new File(FAILA_NOSAUKUMS).toPath(), rindiņas);
            System.out.println("Ceļojums veiksmīgi izdzēsts.");
        } catch (IOException e) {
            System.out.println("Kļūda dzēšot ceļojumu: " + e.getMessage());
        }
    }

    private static void labotCeļojumu() {
        Scanner skeneris = new Scanner(System.in);
        System.out.print("Ievadiet ceļojuma identifikatoru, ko labot: ");
        String idLabot = skeneris.nextLine();

        try {
            List<String> rindiņas = Files.lines(new File(FAILA_NOSAUKUMS).toPath())
                    .map(rinda -> {
                        if (rinda.startsWith(idLabot + ";")) {
                            String[] daļas = rinda.split(";");
                            System.out.print("Ievadiet jauno pilsētu: ");
                            String jaunāPilsēta = skeneris.nextLine();
                            System.out.print("Ievadiet jauno datumu (dd/MM/gggg): ");
                            String jaunaisDatumsStr = skeneris.nextLine();
                            LocalDate jaunaisDatums = LocalDate.parse(jaunaisDatumsStr, DATUMA_FORMATĒTĀJS);
                            System.out.print("Ievadiet jauno dienu skaitu: ");
                            int jaunāsDienas = Integer.parseInt(skeneris.nextLine());
                            System.out.print("Ievadiet jauno cenu: ");
                            double jaunāCena = Double.parseDouble(skeneris.nextLine());
                            System.out.print("Ievadiet jauno transporta veidu: ");
                            String jaunaisTransports = skeneris.nextLine();

                            return String.format("%s;%s;%s;%d;%.2f;%s", daļas[0], jaunāPilsēta, jaunaisDatums.format(DATUMA_FORMATĒTĀJS), jaunāsDienas, jaunāCena, jaunaisTransports);
                        }
                        return rinda;
                    })
                    .collect(Collectors.toList());

            Files.write(new File(FAILA_NOSAUKUMS).toPath(), rindiņas);
            System.out.println("Ceļojuma informācija veiksmīgi labota.");
        } catch (IOException e) {
            System.out.println("Kļūda labojot ceļojumu: " + e.getMessage());
        }
    }

    private static void sakārtotCeļojumusPēcDatuma() {
        try {
            List<String> rindiņas = Files.lines(new File(FAILA_NOSAUKUMS).toPath())
                    .sorted(Comparator.comparing(rinda -> LocalDate.parse(rinda.split(";")[2], DATUMA_FORMATĒTĀJS)))
                    .collect(Collectors.toList());

            Files.write(new File(FAILA_NOSAUKUMS).toPath(), rindiņas);
            System.out.println("Ceļojumi sakārtoti pēc datuma veiksmīgi.");
        } catch (IOException e) {
            System.out.println("Kļūda sakārtojot ceļojumus pēc datuma: " + e.getMessage());
        }
    }

    private static void atrastCeļojumusArCenu() {
        Scanner skeneris = new Scanner(System.in);
        System.out.print("Ievadiet maksimālo cenu: ");
        double maksimālāCena = Double.parseDouble(skeneris.nextLine());

        try (BufferedReader lasītājs = new BufferedReader(new FileReader(FAILA_NOSAUKUMS))) {
            String rinda;
            boolean atrasts = false;
            while ((rinda = lasītājs.readLine()) != null) {
                String[] daļas = rinda.split(";");
                if (Double.parseDouble(daļas[4]) <= maksimālāCena) {
                    System.out.println(rinda);
                    atrasts = true;
                }
            }
            if (!atrasts) {
                System.out.println("Nav atrasti ceļojumi ar norādīto cenu.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda lasot failu: " + e.getMessage());
        }
    }

    private static void aprēķinātCeļojumuVidējoCenu() {
        try (BufferedReader lasītājs = new BufferedReader(new FileReader(FAILA_NOSAUKUMS))) {
            String rinda;
            double kopsavilkums = 0;
            int skaits = 0;
            while ((rinda = lasītājs.readLine()) != null) {
                String[] daļas = rinda.split(";");
                kopsavilkums += Double.parseDouble(daļas[4]);
                skaits++;
            }
            if (skaits > 0) {
                double vidējā = kopsavilkums / skaits;
                System.out.println("Ceļojumu vidējā cena: " + CENAS_FORMATĒTĀJS.format(vidējā));
            } else {
                System.out.println("Nav atrasti ceļojumi, lai aprēķinātu vidējo cenu.");
            }
        } catch (IOException e) {
            System.out.println("Kļūda lasot failu: " + e.getMessage());
        }
    }
}
