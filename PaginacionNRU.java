import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaginacionNRU {
    private final int numMarcos;
    private final Map<Integer, Pagina> tablaPaginas = new HashMap<>(); 
    private final LinkedHashMap<Integer, Pagina> marcos = new LinkedHashMap<>();

    public PaginacionNRU(int numMarcos) {
        this.numMarcos = numMarcos;
    }

    public synchronized boolean procesarReferencia(int numeroPagina, boolean esEscritura) {

        if (marcos.containsKey(numeroPagina)) {
            Pagina pagina = marcos.get(numeroPagina);
            pagina.bitUso = 1;

            if (esEscritura) {
                pagina.bitModificacion = 1;
            }

            return true;

        } 
        else {  

            if (marcos.size() >= numMarcos) {
                reemplazarPagina();
            }

            Pagina nuevaPagina = new Pagina(numeroPagina, esEscritura);
            marcos.put(numeroPagina, nuevaPagina);
            tablaPaginas.put(numeroPagina, nuevaPagina);
            return false;
        }
    }
    
    private synchronized void reemplazarPagina() {

        List<Integer> clase0 = new ArrayList<>();
        List<Integer> clase1 = new ArrayList<>();
        List<Integer> clase2 = new ArrayList<>();
        List<Integer> clase3 = new ArrayList<>();

        for (Map.Entry<Integer, Pagina> entry : marcos.entrySet()) {

            Pagina pagina = entry.getValue();
            int numeroPagina = entry.getKey();

            if (pagina.bitUso == 0 && pagina.bitModificacion == 0) {
                clase0.add(numeroPagina);
            } 
            else if (pagina.bitUso == 0 && pagina.bitModificacion == 1) {
                clase1.add(numeroPagina);
            } 
            else if (pagina.bitUso == 1 && pagina.bitModificacion == 0) {
                clase2.add(numeroPagina);
            } 
            else {
                clase3.add(numeroPagina);
            }
        }

        if (!clase0.isEmpty()) {
            int paginaEliminar = clase0.get(0);
            marcos.remove(paginaEliminar);
            tablaPaginas.remove(paginaEliminar);
        } 
        else if (!clase1.isEmpty()) {
            int paginaEliminar = clase1.get(0);
            marcos.remove(paginaEliminar);
            tablaPaginas.remove(paginaEliminar);
        } 
        else if (!clase2.isEmpty()) {
            int paginaEliminar = clase2.get(0);
            marcos.remove(paginaEliminar);
            tablaPaginas.remove(paginaEliminar);
        } 
        else if (!clase3.isEmpty()) {
            int paginaEliminar = clase3.get(0);
            marcos.remove(paginaEliminar);
            tablaPaginas.remove(paginaEliminar);
        }
    }

    public synchronized void actualizarBits() {

        for (Pagina pagina : marcos.values()) {
            pagina.bitUso = 0;
        }
    }
}