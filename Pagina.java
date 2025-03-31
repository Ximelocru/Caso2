public class Pagina {
    int numero;
    int bitUso; 
    int bitModificacion;

    public Pagina(int numero, boolean esEscritura) {
        this.numero = numero;
        this.bitUso = 1;
        this.bitModificacion = esEscritura ? 1 : 0;
    }
}