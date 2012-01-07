/**
 * Author: Jan Paw
 * Date: 07.01.12
 * Time: 17:52
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    private double Rmin, Rmax, AlfaAir, TempBegin, t1, t2, Tau1, Tau2, C, Ro,
            K;

    public Main(String[] args) {

        if (args.length != 1)
            System.out.println("Proszę podać ścieżkę do pliku");
        else if (readConfig(args[0])) {
            readConfig(args[0]);
            Temp1D myTemp1D = new Temp1D(Rmin, Rmax, AlfaAir, TempBegin, t1,
                    t2, C, Ro, K, Tau1, Tau2);
            try {
                myTemp1D.count();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean readConfig(String filename) {
        File file = new File(filename);
        String s;
        String[] bits;
        if (!file.exists()) {
            System.out.println("Plik konfiguracyjny " + filename
                    + " nie istnieje");
            return false;
        }

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));

            Rmin = Double.parseDouble(readNextLine(in));
            Rmax = Double.parseDouble(readNextLine(in));
            AlfaAir = Double.parseDouble(readNextLine(in));
            TempBegin = Double.parseDouble(readNextLine(in));

            s = readNextLine(in);

            if (s == null) {
                System.out.println("Nie podano parametru");
                return false;
            }

            bits = s.split(",");
            t1 = Double.parseDouble(bits[0]);
            t2 = Double.parseDouble(bits[1]);

            C = Double.parseDouble(readNextLine(in));
            Ro = Double.parseDouble(readNextLine(in));
            K = Double.parseDouble(readNextLine(in));

            s = readNextLine(in);

            if (s == null) {
                System.out.println("Nie podano parametru");
                return false;
            }

            bits = s.split(",");
            Tau1 = Double.parseDouble(bits[0]);
            Tau2 = Double.parseDouble(bits[1]);

        } catch (Exception e) {
            System.out.println("Problem z otwarciem pliku konfiguracyjnego "
                    + filename);
            return false;
        }

        return true;
    }

    private String readNextLine(BufferedReader in) throws Exception {
        String str = null;
        boolean proceed;

        do {
            proceed = false;

            str = in.readLine();
            str = str.trim();

            if (str.startsWith("#") || str.matches(""))
                proceed = true;

        } while (proceed);

        return str;
    }

    public static void main(String[] args) {
        new Main(args);
    }
}