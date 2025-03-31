import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SimuladorNRU {

    private int numMarcos; // Número de marcos de página en RAM
    private int TP; // Tamaño de página
    private Map<Integer, Integer> tablaPaginas = new HashMap<>(); // Tabla de páginas (mapa página -> marco)
    private Map<Integer, String> memoriaRAM = new LinkedHashMap<>(); // Simulación de RAM
    private Map<Integer, Integer> bitsReferencia = new HashMap<>(); // Bit R para NRU
    private Map<Integer, Integer> bitsModificado = new HashMap<>(); // Bit M para NRU

    private int hits = 0; // Número de hits
    private int misses = 0; // Número de misses
    private String archivoReferencias; // Ruta del archivo de referencias

    // Nuevo constructor: recibe numMarcos y archivo de referencias
    public SimuladorNRU(int numMarcos, String archivoReferencias) throws IOException {
        this.numMarcos = numMarcos;
        this.archivoReferencias = archivoReferencias;

        // Leer TP desde metadatos del archivo de referencias
        BufferedReader br = new BufferedReader(new FileReader(archivoReferencias));
        Map<String, Integer> metadatos = leerMetadatos(br);
        this.TP = metadatos.get("TP"); // Extrae el tamaño de página desde el archivo
        br.close();
    }

    // Simular paginación con NRU
    public void simularPaginacion() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(archivoReferencias));
        String linea;

        // Saltar metadatos
        for (int i = 0; i < 5; i++) br.readLine();

        // Leer cada referencia
        while ((linea = br.readLine()) != null) {
            procesarReferencia(linea);
        }
        br.close();

        // Mostrar resultados finales
        mostrarResultados();
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

    // Procesar cada referencia del archivo
    private void procesarReferencia(String referencia) {
        String[] partes = referencia.split(",");
        int paginaVirtual = Integer.parseInt(partes[1].trim()); // Página virtual
        int desplazamiento = Integer.parseInt(partes[2].trim()); // Offset
        char accion = partes[3].trim().charAt(0); // Acción (R o W)

        if (tablaPaginas.containsKey(paginaVirtual)) {
            // Página ya está en RAM (HIT)
            hits++;
            actualizarBits(paginaVirtual, accion);
        } else {
            // Fallo de página (MISS)
            misses++;
            manejarFalloPagina(paginaVirtual, accion);
        }
    }

    // Actualizar bits de referencia y modificado si es necesario
    private void actualizarBits(int pagina, char accion) {
        bitsReferencia.put(pagina, 1); // Página referenciada
        if (accion == 'W') {
            bitsModificado.put(pagina, 1); // Página modificada si es escritura
        }
    }

    // Manejar fallo de página usando NRU
    private void manejarFalloPagina(int paginaVirtual, char accion) {
        if (memoriaRAM.size() < numMarcos) {
            // Hay espacio disponible en RAM
            cargarPaginaEnRAM(paginaVirtual, accion);
        } else {
            // No hay espacio -> aplicar NRU para reemplazo
            int paginaReemplazo = aplicarNRU();
            reemplazarPagina(paginaReemplazo, paginaVirtual, accion);
        }
    }

    // Cargar página en RAM
    private void cargarPaginaEnRAM(int paginaVirtual, char accion) {
        int marcoDisponible = memoriaRAM.size();
        memoriaRAM.put(paginaVirtual, "Pagina " + paginaVirtual);
        tablaPaginas.put(paginaVirtual, marcoDisponible);
        bitsReferencia.put(paginaVirtual, 1);
        bitsModificado.put(paginaVirtual, accion == 'W' ? 1 : 0);
    }

    // Reemplazar página usando NRU
    private void reemplazarPagina(int paginaReemplazo, int nuevaPagina, char accion) {
        memoriaRAM.remove(paginaReemplazo);
        tablaPaginas.remove(paginaReemplazo);
        bitsReferencia.remove(paginaReemplazo);
        bitsModificado.remove(paginaReemplazo);

        cargarPaginaEnRAM(nuevaPagina, accion);
    }

    // Algoritmo NRU para seleccionar página a reemplazar
    private int aplicarNRU() {
        List<Integer> clase0 = new ArrayList<>();
        List<Integer> clase1 = new ArrayList<>();
        List<Integer> clase2 = new ArrayList<>();
        List<Integer> clase3 = new ArrayList<>();

        // Clasificar páginas
        for (int pagina : memoriaRAM.keySet()) {
            int R = bitsReferencia.getOrDefault(pagina, 0);
            int M = bitsModificado.getOrDefault(pagina, 0);

            if (R == 0 && M == 0) clase0.add(pagina);
            else if (R == 0 && M == 1) clase1.add(pagina);
            else if (R == 1 && M == 0) clase2.add(pagina);
            else clase3.add(pagina);
        }

        // Seleccionar página de menor clase
        if (!clase0.isEmpty()) return clase0.get(0);
        if (!clase1.isEmpty()) return clase1.get(0);
        if (!clase2.isEmpty()) return clase2.get(0);
        return clase3.get(0);
    }

    // Mostrar resultados finales
    private void mostrarResultados() {
        System.out.println("\n📊 Resultados de la simulación:");
        System.out.println("✅ Número de Hits: " + hits);
        System.out.println("❌ Número de Fallas de página: " + misses);

        long tiempoHits = hits * 50; // 50 ns por hit
        long tiempoMisses = misses * 10_000_000; // 10 ms por fallo
        long tiempoTotal = tiempoHits + tiempoMisses;

        System.out.println("⏱️ Tiempo total de accesos: " + tiempoTotal + " ns");
        System.out.println("⏱️ Tiempo solo con hits: " + tiempoHits + " ns");
        System.out.println("⏱️ Tiempo solo con fallas: " + tiempoMisses + " ns");
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Pedir datos al usuario
            System.out.print("Ingrese el número de marcos de página: ");
            int numMarcos = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Ingrese la ruta del archivo de referencias: ");
            String archivoReferencias = scanner.nextLine().trim();

            // Crear simulador con número de marcos y archivo de referencias
            SimuladorNRU simulador = new SimuladorNRU(numMarcos, archivoReferencias);

            // Simular paginación usando NRU
            simulador.simularPaginacion();
        } catch (IOException e) {
            System.err.println("❌ Error al leer el archivo: " + e.getMessage());
        }
    }
}
