public class ActualizadorEstado extends Thread{
    private final PaginacionNRU paginacion;

    public ActualizadorEstado(PaginacionNRU paginacion) {
        this.paginacion = paginacion;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            paginacion.actualizarBits();
        }
    }


}
