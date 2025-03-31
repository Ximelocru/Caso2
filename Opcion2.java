import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Opcion2 {
    private int numMarcos;
    private int TP;
    private Map<Integer, Integer> tablaPaginas = new HashMap<>(); // Tabla de páginas (mapa página -> marco)
    private Map<Integer, String> memoriaRAM = new LinkedHashMap<>(); // Simulación de RAM
    private Map<Integer, Integer> bitsReferencia = new HashMap<>(); // Bit R para NRU
    private Map<Integer, Integer> bitsModificado = new HashMap<>(); // Bit M para NRU

    private int hits = 0; // Número de hits
    private int misses = 0; // Número de misses
    private String archivoReferencias; // Ruta del archivo de referencias

    public Opcion2(int numMarcos, int TP) throws IOException {
        this.numMarcos = numMarcos;
        this.archivoReferencias = archivoReferencias;
        
        // Leer TP desde metadatos del archivo de referencias
        BufferedReader br = new BufferedReader(new FileReader(archivoReferencias));
        Map<String, Integer> metadatos = leerMetadatos(br);
        this.TP = metadatos.get("TP"); // Extrae el tamaño de página desde el archivo
        br.close();
    }
    
    // Leer encabezados TP, NF, NC, NR, NP del archivo de referencias
    private Map<String, Integer> leerMetadatos(BufferedReader br) throws IOException {
        Map<String, Integer> metadatos = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            String linea = br.readLine();
            String[] partes = linea.split("=");
            metadatos.put(partes[0].trim(), Integer.parseInt(partes[1].trim()));
        }
        return metadatos;
    }
    
    
}
