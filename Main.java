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
                    simuladorPaginacion(reader);
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

            System.out.print("Ingrese el numero de marcos: ");
            Integer marcos = Integer.parseInt(reader.readLine().trim());
            
            //SimuladorNRU simulador= new SimuladorNRU(marcos, inputFilePath);

            //simulador.simularPaginacion();      
    }

    public static void simuladorPaginacion(BufferedReader reader)throws IOException{
        System.out.print("Ingrese la ruta del archivo BMP: ");
        String nombreArchivo = reader.readLine().trim();
        
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
        } catch (InterruptedException e) {
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
            // Saltar la cabecera
            String linea;
            while ((linea = br.readLine()) != null && !linea.startsWith("Imagen") && !linea.startsWith("SOBEL")) {
                // Salta todas las líneas hasta encontrar referencias válidas
            }
    
            // Procesar referencias
            do {
                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    int paginaVirtual = Integer.parseInt(partes[1].trim()); // Campo 2: Página virtual
                    boolean esEscritura = partes[3].trim().equals("W");     // Campo 4: Tipo de acción
                    referencias.add(new int[]{paginaVirtual, esEscritura ? 1 : 0});
                }
            } while ((linea = br.readLine()) != null);
        }
        return referencias;
    }
}
