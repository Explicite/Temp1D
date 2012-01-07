import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jan Paw
 */
public class Temp1D {

    private double Rmin, Rmax, AlfaAir, TempBegin, t1, t2, Tau1, Tau2, C, Ro,
            K, TauMax, dTau, Tau, dR, a, Alfa, dTmax, dT, Rp, TpTau, TempAir;
    private double[] E, W, N1, N2, r, P, TempTau, vrtxTemp, vrtxCoordX, aC, aD,
            aE, aB;
    private double[][] H;
    private int nh, ne, Np, nTime;
    private Solver noweZadanieMES;

    /**
     * Konstruktor klasy {@link Temp1D}.
     *
     * @param Rmin      -> promień minimalny prętu [m]
     * @param Rmax      -> promień maksymalny prętu [m]
     * @param AlfaAir   -> konwekcyjny współczynnik wymiany ciepła [W/m^2*K]
     * @param TempBegin -> temperatura początkowa [K]
     * @param t1        -> pierwszy przystanek temperatury [K]
     * @param t2        -> drugi przystanek temperatury [K]
     * @param Tau1      -> długość trwania pierwszego przystanku [s]
     * @param Tau2      -> długość twania drugiego przystanku [s]
     * @param C         -> wsp. ciepła właściwego [J/kg*K]
     * @param Ro        -> gęstość materiału [kg/m3]
     * @param K         -> wsp. przewodzenia ciepła [W/m*K]
     * @author Jan Paw
     */
    public Temp1D(double Rmin, double Rmax, double AlfaAir, double TempBegin,
                  double t1, double t2, double C, double Ro, double K, double Tau1,
                  double Tau2) {

        // Zapisanie zmiennych pobranych z piku i przekazanych przez konstruktor
        this.Rmin = Rmin;
        this.Rmax = Rmax;
        this.AlfaAir = AlfaAir;
        this.TempBegin = TempBegin;
        this.t1 = t1;
        this.t2 = t2;
        this.Tau1 = Tau1;
        this.Tau2 = Tau2;
        this.C = C;
        this.Ro = Ro;
        this.K = K;

        E = new double[2]; // wagi
        W = new double[2]; // pkt calkowania w ukladzie lokalnym
        N1 = new double[2]; // funkcje ksztalt
        N2 = new double[2]; // funkcje ksztalt
        r = new double[2]; // wsp pkt w ukladzie gobalnym
        H = new double[2][2]; // macierz sztywności układu lokalnego
        P = new double[2];
        TempTau = new double[2];
        TempAir = 0.0;
        nh = 51;// ilość wezłów
        ne = nh - 1;// ilość elementów
        Np = 2;// ilość pkt całkowania
        a = this.K / (this.C * this.Ro);
        W[0] = 1;
        W[1] = 1;
        E[0] = -0.5773502692;
        E[1] = 0.5773502692;
        N1[0] = 0.5 * (1 - E[0]);
        N1[1] = 0.5 * (1 - E[0]);
        N2[0] = 0.5 * (1 + E[0]);
        N2[1] = 0.5 * (1 + E[0]);
        dR = (this.Rmax - this.Rmin) / ne; // skok wsp dla pkt
        dTau = (dR * dR) / (0.5 * a);
        TauMax = this.Tau1 + this.Tau2;

        nTime = (int) ((TauMax / dTau) + 1); // ilość iteracji
        dTau = TauMax / nTime; // skok czasowy

        dTmax = 0.0;
        Tau = 0.0;
        vrtxTemp = new double[nh]; // temperatury węzłów
        vrtxCoordX = new double[nh]; // wspólrzędne węzłów
        aC = new double[nh]; // poddiagonalna macierzy sztywności
        aD = new double[nh]; // diagonalna macierzy sztywności
        aE = new double[nh]; // naddiagonalna macierzy sztywności
        aB = new double[nh]; // wektor obciążeń
    }

    public void count() throws IOException {
        String curDir = System.getProperty("user.dir");
        FileWriter fr = new FileWriter(
                curDir + "/OutputData.txt");
        String dane;
        dane = "***********************************************************";
        fr.write(dane + "\r\n");
        dane = cut(" " + "Czas") + "   " + cut("Środek") + "  "
                + cut("Powierzchnia") + "  " + cut("dT");
        fr.write(dane + "\r\n" + "\r\n");
        double x = 0.0;
        for (int i = 0; i < nh; i++) {
            vrtxCoordX[i] = x;
            vrtxTemp[i] = TempBegin;
            x += dR;
        }
        dTmax = 0.0;
        Tau = 0.0;

        for (int iTime = 0; iTime < nTime; iTime++) {

            for (int i = 0; i < nh; i++) {
                aC[i] = 0.0;
                aD[i] = 0.0;
                aE[i] = 0.0;
                aB[i] = 0.0;
            }

            TempAir = t1;
            if (Tau >= Tau1)
                TempAir = t2;

            for (int ie = 0; ie < ne; ie++) {
                r[0] = vrtxCoordX[ie];
                r[1] = vrtxCoordX[ie + 1];
                TempTau[0] = vrtxTemp[ie];
                TempTau[1] = vrtxTemp[ie + 1];
                dR = r[1] - r[0];

                Alfa = 0.0;
                if (ie == ne - 1)
                    Alfa = AlfaAir;

                P[0] = 0.0;
                P[1] = 0.0;
                H[0][0] = 0.0;
                H[0][1] = 0.0;
                H[1][1] = 0.0;
                H[1][0] = 0.0;

                for (int ip = 0; ip < Np; ip++) {

                    Rp = N1[ip] * r[0] + N2[ip] * r[1];
                    TpTau = N1[ip] * TempTau[0] + N2[ip] * TempTau[1];
                    H[0][0] += K * Rp * W[ip] / dR + C * Ro * dR * Rp * W[ip]
                            * N1[ip] * N1[ip] / dTau;
                    H[0][1] += -K * Rp * W[ip] / dR + C * Ro * dR * Rp * W[ip]
                            * N1[ip] * N2[ip] / dTau;
                    H[1][0] = H[0][1];
                    H[1][1] += K * Rp * W[ip] / dR + C * Ro * dR * Rp * W[ip]
                            * N2[ip] * N2[ip] / dTau + 2 * Alfa * Rmax;
                    P[0] += C * Ro * dR * TpTau * Rp * W[ip] * N1[ip] / dTau;
                    P[1] += C * Ro * dR * TpTau * Rp * W[ip] * N2[ip] / dTau
                            + 2 * Alfa * Rmax * TempAir;
                }

                aD[ie] += H[0][0];
                aD[ie + 1] += H[1][1];
                aE[ie] += H[0][1];
                aC[ie + 1] += H[1][0];
                aB[ie] += P[0];
                aB[ie + 1] += P[1];
            }

            noweZadanieMES = new Solver(aB, aC, aD, aE);
            vrtxTemp = noweZadanieMES.solve();

            dT = Math.abs(vrtxTemp[0] - vrtxTemp[nh - 1]);
            if (dT > dTmax)
                dTmax = dT;
            dane = " " + cut(Double.toString(Tau)) + "  "
                    + cut(Double.toString(vrtxTemp[0])) + "  "
                    + cut(Double.toString(vrtxTemp[nh - 1])) + "  "
                    + cut(Double.toString(dT));
            System.out.println(dane);

            fr.write(dane + "\r\n");
            Tau += dTau;

        }
        fr.close();
    }

    public String cut(String string) {
        String newString = string;
        int howLong = string.length();
        if (howLong > 13) {
            newString = string.substring(0, 13);
        } else {
            for (int i = 0; i < 13 - howLong; i++) {
                newString = newString + " ";
            }
        }
        return newString;
    }
}
