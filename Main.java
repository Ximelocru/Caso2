import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        try (

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            boolean continuar = true;

            while (continuar) {
            
                System.out.println("Menu de Opciones: ");
                System.out.println("1. Generar Archivo de Referencias");
                System.out.println("2. Simulacion de Paginacion");
                System.out.println("3. Salir");

                String opcion = reader.readLine().trim();

                if (opcion.equals("1")) {
                    generarReferencias(reader);
                }

                if (opcion.equals("2")) {
                    simuladorPaginacion(reader);
                }

                if (opcion.equals("3")) {
                    System.out.println("Saliendo...");
                    continuar = false;
                }
            }
        } 
        catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public static void generarReferencias(BufferedReader reader) throws NumberFormatException, IOException {
        
        System.out.print("Ingrese el tamaño de página (en bytes): ");
        int TP = Integer.parseInt(reader.readLine().trim());
    
        System.out.print("Ingrese el nombre de la imagen (ejemplo: imagen.bmp). Recuerde que debe encontrarse dentro de la carpeta 'Imagenes': ");
        String nombreImagen = reader.readLine().trim();
        String inputFilePath = "Imagenes/" + nombreImagen;
    
        Imagen imagenIn = new Imagen(inputFilePath); 
        Imagen imagenOut = new Imagen(inputFilePath); 
    
        FiltroSobel fs = new FiltroSobel(imagenIn, imagenOut, TP);
        fs.applySobel();
        String nombreArchivo = nombreImagen.replace(".bmp", "");
        fs.generarArchivoReferencias(nombreArchivo);
    
        String outputFilePath = inputFilePath.replace(".bmp", "-salida.bmp");
        imagenOut.escribirImagen(outputFilePath);
        System.out.println("Imagen procesada y guardada en: " + outputFilePath);
    }

    public static void simuladorPaginacion(BufferedReader reader) throws IOException {
        System.out.print("Ingrese el nombre del archivo de referencias (ejemplo: referencias-32.txt). Recuerde que debe encontrarse dentro de la carpeta 'Referencias': ");
        String nombreArchivo = "Referencias/" + reader.readLine().trim();
        
        System.out.print("Ingrese el numero de marcos: ");
        Integer numMarcos = Integer.parseInt(reader.readLine().trim());

        List<int[]> referencias = leerReferencias(nombreArchivo);
        PaginacionNRU paginacion = new PaginacionNRU(numMarcos);

        GestorReferencias gestor = new GestorReferencias(paginacion, referencias);
        ActualizadorEstado actualizador = new ActualizadorEstado(paginacion);

        gestor.start();
        actualizador.start();

        try {
            gestor.join();
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Fallas de página: " + gestor.fallasPagina);
                System.out.println("Hits: " + gestor.hits);
                System.out.println("total: " + (gestor.hits+gestor.fallasPagina));
                
        actualizador.interrupt();
    }
    
    private static List<int[]> leerReferencias(String nombreArchivo) throws IOException {

        List<int[]> referencias = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;

            while ((linea = br.readLine()) != null && !linea.startsWith("Imagen") && !linea.startsWith("SOBEL")) {
            }
    
            do {
                String[] partes = linea.split(",");

                if (partes.length >= 4) {
                    int paginaVirtual = Integer.parseInt(partes[1].trim()); 
                    boolean esEscritura = partes[3].trim().equals("W");    
                    referencias.add(new int[]{paginaVirtual, esEscritura ? 1 : 0});
                }
            } 
            while ((linea = br.readLine()) != null);
        }
        return referencias;
    }
}