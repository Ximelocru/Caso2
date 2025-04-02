import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FiltroSobel {
    int TP;
    Imagen imagenIn;
    Imagen imagenOut;
    List<String> referencias = new ArrayList<>();
    int baseImagen, baseSobelX, baseSobelY, baseRta;
    int acumulativoImagen, acumulativoSobelX, acumulativoSobelY, acumulativoRta;

    public FiltroSobel(Imagen imagenEntrada, Imagen imagenSalida, int TP) {
        this.TP = TP;
        this.imagenIn = imagenEntrada;
        this.imagenOut = imagenSalida;
    }

    public static final int[][] SOBEL_X = {
            { -1, 0, 1 },
            { -2, 0, 2 },
            { -1, 0, 1 }
    };

    public static final int[][] SOBEL_Y = {
            { -1, -2, -1 },
            { 0, 0, 0 },
            { 1, 2, 1 }

    };

    public void applySobel() {
        baseImagen = 0;
        acumulativoImagen = baseImagen;

        baseSobelX = acumulativoImagen + (imagenIn.alto * imagenIn.ancho * 3);
        acumulativoSobelX = baseSobelX;

        baseSobelY = acumulativoSobelX + (3 * 3 * 4);
        acumulativoSobelY = baseSobelY;

        baseRta = acumulativoSobelY + (3 * 3 * 4);
        acumulativoRta = baseRta;

        for (int i = 1; i < imagenIn.alto - 1; i++) {
            for (int j = 1; j < imagenIn.ancho - 1; j++) {
                int gradXRed = 0, gradXGreen = 0, gradXBlue = 0;
                int gradYRed = 0, gradYGreen = 0, gradYBlue = 0;

                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {

                        int red = imagenIn.imagen[i + ki][j + kj][0];
                        int green = imagenIn.imagen[i + ki][j + kj][1];
                        int blue = imagenIn.imagen[i + ki][j + kj][2];
                        
                        int direccionImagen = acumulativoImagen + ((i + ki) * imagenIn.ancho + (j + kj)) * 3;
                        nuevaReferencia("Imagen["+(i+ki)+"]"+"["+(j + kj)+"].r", direccionImagen, "R");
                        nuevaReferencia("Imagen["+(i+ki)+"]"+"["+(j + kj)+"].g", direccionImagen+1, "R");
                        nuevaReferencia("Imagen["+(i+ki)+"]"+"["+(j + kj)+"].b", direccionImagen+2, "R");

                        gradXRed += red * SOBEL_X[ki + 1][kj + 1];
                        gradXGreen += green * SOBEL_X[ki + 1][kj + 1];
                        gradXBlue += blue * SOBEL_X[ki + 1][kj + 1];

                        int direccionSobelX = acumulativoSobelX + ((ki + 1) * 3 + (kj + 1)) * 4;
                        nuevaReferencia("SOBEL_X["+(ki+1)+"]"+"["+(kj+1)+"]", direccionSobelX, "R");
                        nuevaReferencia("SOBEL_X["+(ki+1)+"]"+"["+(kj+1)+"]", direccionSobelX, "R");
                        nuevaReferencia("SOBEL_X["+(ki+1)+"]"+"["+(kj+1)+"]", direccionSobelX, "R");

                        gradYRed += red * SOBEL_Y[ki + 1][kj + 1];
                        gradYGreen += green * SOBEL_Y[ki + 1][kj + 1];
                        gradYBlue += blue * SOBEL_Y[ki + 1][kj + 1];
                        
                        int direccionSobelY = acumulativoSobelY + ((ki + 1) * 3 + (kj + 1)) * 4;
                        nuevaReferencia("SOBEL_Y["+(ki+1)+"]"+"["+(kj+1)+"]",direccionSobelY, "R");
                        nuevaReferencia("SOBEL_Y["+(ki+1)+"]"+"["+(kj+1)+"]",direccionSobelY, "R");
                        nuevaReferencia("SOBEL_Y["+(ki+1)+"]"+"["+(kj+1)+"]",direccionSobelY, "R");
                    }
                }

                int red = Math.min(Math.max((int) Math.sqrt(gradXRed * gradXRed + gradYRed * gradYRed), 0), 255);
                int green = Math.min(Math.max((int) Math.sqrt(gradXGreen * gradXGreen + gradYGreen * gradYGreen), 0), 255);
                int blue = Math.min(Math.max((int) Math.sqrt(gradXBlue * gradXBlue + gradYBlue * gradYBlue), 0), 255);

                imagenOut.imagen[i][j][0] = (byte) red;
                imagenOut.imagen[i][j][1] = (byte) green;
                imagenOut.imagen[i][j][2] = (byte) blue;

                int direccionRta = acumulativoRta + ((i * imagenOut.ancho + j) * 3);
                nuevaReferencia("Rta["+(i)+"]"+"["+(j)+"].r", direccionRta, "W");
                nuevaReferencia("Rta["+(i)+"]"+"["+(j)+"].g", direccionRta+1, "W");
                nuevaReferencia("Rta["+(i)+"]"+"["+(j)+"].b", direccionRta+2, "W");
            }
        }
    }

    public void nuevaReferencia(String nombre, int direccion, String bitAccion) {
        int pagina = direccion / TP;
        int desplazamiento = direccion % TP;
        String ref = nombre + "," + pagina + "," + desplazamiento + "," + bitAccion;
        referencias.add(ref);
    }

    public void generarArchivoReferencias() {
        int NF = imagenOut.alto;
        int NC = imagenOut.ancho;
        int NR = referencias.size();
        int totalBytes = (imagenIn.alto * imagenIn.ancho * 3 * 2) + 72;
        int NP = (int) Math.ceil((double) totalBytes / TP);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Referencias/referencias-" + TP + ".txt"))) {

            writer.write("TP=" + TP);
            writer.newLine();

            writer.write("NF=" + NF);
            writer.newLine();

            writer.write("NC=" + NC);
            writer.newLine();

            writer.write("NR=" + NR);
            writer.newLine();

            writer.write("NP=" + NP);
            writer.newLine();

            for (String ref : referencias) {
                writer.write(ref);
                writer.newLine();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Archivo de referencias generado y guardado en: Referencias/referencias-" + TP + ".txt");
    }
}