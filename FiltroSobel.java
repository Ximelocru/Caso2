import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FiltroSobel {
    Imagen imagenIn;
    Imagen imagenOut;
    int TP;
    List<String> referencias = new ArrayList<>();
    
    int baseImagen, baseSobelX, baseSobelY, baseRta;
    int acumulativoImagen, acumulativoSobelX, acumulativoSobelY, acumulativoRta;

    

    public FiltroSobel(Imagen imagenEntrada, Imagen imagenSalida, int TP) {
        imagenIn = imagenEntrada;
        imagenOut = imagenSalida;
        this.TP = TP;
    }

    // Sobel Kernels para detección de bordes
    static final int[][] SOBEL_X = {
            { -1, 0, 1 },
            { -2, 0, 2 },
            { -1, 0, 1 }
    };

    static final int[][] SOBEL_Y = {
            { -1, -2, -1 },
            { 0, 0, 0 },
            { 1, 2, 1 }

    };

    /**
     * Método para para aplicar el filtro de Sobel a una imagen BMP
     * 
     * @pre la matriz imagenIn debe haber sido inicializada con una imagen
     * @pos la mstriz imagenOut fue modificada aplicando el filtro Sobel
     */
    public void applySobel() {
        baseImagen = 0;
        acumulativoImagen = baseImagen;

        baseSobelX = acumulativoImagen + (imagenIn.alto * imagenIn.ancho * 3);
        acumulativoSobelX = baseSobelX;

        baseSobelY = acumulativoSobelX + (3 * 3 * 4);
        acumulativoSobelY = baseSobelY;

        baseRta = acumulativoSobelY + (3 * 3 * 4);
        acumulativoRta = baseRta;
        // Recorrer la imagen aplicando los dos filtros de Sobel
        for (int i = 1; i < imagenIn.alto - 1; i++) {
            for (int j = 1; j < imagenIn.ancho - 1; j++) {
                int gradXRed = 0, gradXGreen = 0, gradXBlue = 0;
                int gradYRed = 0, gradYGreen = 0, gradYBlue = 0;

               

                // Aplicar las máscaras Sobel X y Y
                for (int ki = -1; ki <= 1; ki++) {
                    for (int kj = -1; kj <= 1; kj++) {

                        // Prints Imagen---------------------------------
                        int red = imagenIn.imagen[i + ki][j + kj][0];
                        int green = imagenIn.imagen[i + ki][j + kj][1];
                        int blue = imagenIn.imagen[i + ki][j + kj][2];
                        
                        // REVISAR: base y direccion
                    
                        
                        
                        //Direccion Imagen
                        int direccionImagen=acumulativoImagen+((i + ki)*imagenIn.ancho+(j + kj))*3;
                        
                        
                        nuevaReferencia("Imagen["+(i+ki)+"]"+"["+(j + kj)+"].r", direccionImagen, "R");// referencia ROJO
                        nuevaReferencia("Imagen["+(i+ki)+"]"+"["+(j + kj)+"].g", direccionImagen+1, "R");// referencia VERDE
                        nuevaReferencia("Imagen["+(i+ki)+"]"+"["+(j + kj)+"].b", direccionImagen+2, "R");// referencia AZUL
                        // ---------------------------------------------

                        // Print SOBEL_X-------------------------------
                        gradXRed += red * SOBEL_X[ki + 1][kj + 1];
                        gradXGreen += green * SOBEL_X[ki + 1][kj + 1];
                        gradXBlue += blue * SOBEL_X[ki + 1][kj + 1];

                        // REVISAR: base y direccion

                        

                        int direccionSobelX=acumulativoSobelX+((ki + 1)*3+(kj + 1))*4;
                        nuevaReferencia("SOBEL_X["+(ki+1)+"]"+"["+(kj+1)+"]", direccionSobelX, "R");
                        
                        // --------------------------------------------

                        // Print SOBEL_Y----------------------------------
                        gradYRed += red * SOBEL_Y[ki + 1][kj + 1];
                        gradYGreen += green * SOBEL_Y[ki + 1][kj + 1];
                        gradYBlue += blue * SOBEL_Y[ki + 1][kj + 1];
                        
                        
                        int direccionSobelY=acumulativoSobelY+((ki + 1)*3+(kj + 1))*4;

                        nuevaReferencia("SOBEL_Y["+(ki+1)+"]"+"["+(kj+1)+"]",direccionSobelY, "R");
                        
                        // ------------------------------------------------
                    }
                }

                // Calcular la magnitud del gradiente
                int red = Math.min(Math.max((int) Math.sqrt(gradXRed * gradXRed +
                        gradYRed * gradYRed), 0), 255);
                int green = Math.min(Math.max((int) Math.sqrt(gradXGreen * gradXGreen +
                        gradYGreen * gradYGreen), 0), 255);
                int blue = Math.min(Math.max((int) Math.sqrt(gradXBlue * gradXBlue +
                        gradYBlue * gradYBlue), 0), 255);

                // Print Respuesta------------------------------
                
               

                int direccionRta=acumulativoRta + ((i * imagenOut.ancho + j) * 3);
                nuevaReferencia("Rta["+(i)+"]"+"["+(j)+"].r", direccionRta, "W");//ROJO
                nuevaReferencia("Rta["+(i)+"]"+"["+(j)+"].g", direccionRta+1, "W");//VERDE
                nuevaReferencia("Rta["+(i)+"]"+"["+(j)+"].b", direccionRta+2, "W");//BLUE

                // --------------------------------------------

                // Crear el nuevo valor RGB
                imagenOut.imagen[i][j][0] = (byte) red;
                imagenOut.imagen[i][j][1] = (byte) green;
                imagenOut.imagen[i][j][2] = (byte) blue;
            }
        }
    }

    public void nuevaReferencia(String nombre, int direccion, String bitAccion) {
        int pagina = direccion / TP;
        int desplazamiento = direccion % TP;
        String res = nombre + "," + pagina + "," + desplazamiento + "," + bitAccion;

        referencias.add(res);
    }

    public void generarArchivoReferencias() {
        int NF = imagenOut.alto;
        int NC = imagenOut.ancho;
        int NR = referencias.size();
        int totalBytes = (imagenIn.alto * imagenIn.ancho * 3 * 2) + 72;
        int NP = (int) Math.ceil((double) totalBytes / TP);
        // int NP= (int) Math.ceil((double) totalBytes / TP);;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\cfvm0\\OneDrive\\Documents\\Caso2_InfraComp\\Caso2"))) {

            // TP
            writer.write("TP=" + TP);
            writer.newLine();

            // NF
            writer.write("NF=" + NF);
            writer.newLine();

            // NC
            writer.write("NC=" + NC);
            writer.newLine();

            // NR
            writer.write("NR=" + NR);
            writer.newLine();

            // NP
            writer.write("NP=" + NP);
            writer.newLine();

            for (String ref : referencias) {
                writer.write(ref);
                writer.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}