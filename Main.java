import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    
    public static void main(String[] args) {
        try (

                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            boolean continuar = true;

            while (continuar) {
                
                // Menu
                System.out.println("Menu de Opciones: ");
                System.out.println("1. Generar Archivo de referencias");
                System.out.println("2. Simulacion de Paginacion ");
                System.out.println("3. Salir");

                String opcion = reader.readLine().trim();

                //Opcion 1
                if (opcion.equals("1")) {
                    generarReferencias(reader);
                }
                //Opcion 2
                if (opcion.equals("2")) {
                    simulacion(reader);
                }
                //Salir
                if (opcion.equals("1")) {
                    System.out.println("Saliendo...");
                    continuar = false;
                }

            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void generarReferencias(BufferedReader reader) throws NumberFormatException, IOException {
        
        System.out.print("Ingrese el tamaño de página (en bytes): ");//TP
        int TP = Integer.parseInt(reader.readLine().trim());
    
        
        System.out.print("Ingrese la ruta del archivo BMP: ");
        String inputFilePath = reader.readLine().trim();
    
        
        Imagen imagenIn = new Imagen(inputFilePath);   // Imagen original
        Imagen imagenOut = new Imagen(inputFilePath);  // Imagen copia imagen original sobre la que se hace cambios de colores
    
        // Crear filtro Sobel y aplicarlo
        FiltroSobel fs = new FiltroSobel(imagenIn, imagenOut,TP);
        fs.applySobel();

        fs.generarArchivoReferencias();
    
        // Guardar la imagen procesada en un archivo diferente
        String outputFilePath = inputFilePath.replace(".bmp", "_sal.bmp");
        imagenOut.escribirImagen(outputFilePath);
        System.out.println("Imagen procesada y guardada en: " + outputFilePath);
    
        
    }
    

    public static void simulacion(BufferedReader reader) throws IOException{
            //toca pasarle el archivo de opcion 2
            
            System.out.print("Ingrese la ruta del archivo BMP: ");
            String inputFilePath = reader.readLine().trim();
            
            

            
            
    }
}
