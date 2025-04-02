import java.util.HashMap;
import java.util.LinkedHashMap;
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
        Integer candidatoClase1 = null;
        Integer candidatoClase2 = null;
        Integer candidatoClase3 = null;
    
        for (Map.Entry<Integer, Pagina> entry : marcos.entrySet()) {
            int numeroPagina = entry.getKey();
            Pagina pagina = entry.getValue();
    
            int bitUso = pagina.bitUso;
            int bitMod = pagina.bitModificacion;
    
            if (bitUso == 0 && bitMod == 0) {
                marcos.remove(numeroPagina);
                tablaPaginas.remove(numeroPagina);
                return;
            } 
            else if (bitUso == 0 && bitMod == 1 && candidatoClase1 == null) {
                candidatoClase1 = numeroPagina;
            } 
            else if (bitUso == 1 && bitMod == 0 && candidatoClase2 == null) {
                candidatoClase2 = numeroPagina;
            } 
            else if (bitUso == 1 && bitMod == 1 && candidatoClase3 == null) {
                candidatoClase3 = numeroPagina;
            }
        }
    
        if (candidatoClase1 != null) {
            marcos.remove(candidatoClase1);
            tablaPaginas.remove(candidatoClase1);
        } 
        else if (candidatoClase2 != null) {
            marcos.remove(candidatoClase2);
            tablaPaginas.remove(candidatoClase2);
        } 
        else if (candidatoClase3 != null) {
            marcos.remove(candidatoClase3);
            tablaPaginas.remove(candidatoClase3);
        }
    }
    
    public synchronized void actualizarBits() {

        for (Pagina pagina : marcos.values()) {
            pagina.bitUso = 0;
        }
    }
}