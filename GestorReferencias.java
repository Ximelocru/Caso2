import java.util.List;

public class GestorReferencias extends Thread {
    private final PaginacionNRU paginacion;
    private final List<int[]> referencias;
    public int fallasPagina = 0;
    public int hits = 0;

    public GestorReferencias(PaginacionNRU paginacion, List<int[]> referencias) {
        this.paginacion = paginacion;
        this.referencias = referencias;
    }

    @Override
    public void run() {
        int i =1;
        for (int[] ref : referencias) {
            boolean hit= paginacion.procesarReferencia(ref[0], ref[1] == 1);
            if (hit) {
                hits++;
                //tiempoH+=25;}
            }
            if (!hit) {
                fallasPagina++;
                //tiempoH+=25;}
            }
            if (i % 10000 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            i++;
        }
    }


}
