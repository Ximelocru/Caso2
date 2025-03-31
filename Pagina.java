import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Pagina {

/*HashMap<Integer,ArrayList<Integer>> memAux= new HashMap<>();
HashMap<Integer,ArrayList<Integer>> memAux= new HashMap<>();
HashMap<Integer,ArrayList<Integer>> memAux= new HashMap<>();
HashMap<Integer,ArrayList<Integer>> memAux= new HashMap<>();
    public Pagina{
        

        // metodo que lea doc y indentifique el tipo y asi el peso

        //metodo intentar meter dato a ram
        
        //metodo para fallo de pagina 
            //seleccionar pagina a sacar (NRU) y moverla a mem Auxiliar
            //modifar tabla de paginas (quitar apuntadores y reemplazar la que va a entrar a ram)
            //Si la página no está en RAM, cargarla desde memoria auxiliar.

        //
    }*/ 
    int numero;
    int bitUso; // 0 o 1
    int bitModificacion; // 0 o 1

    public Pagina(int numero, boolean esEscritura) {
        this.numero = numero;
        this.bitUso = 1;
        this.bitModificacion = esEscritura ? 1 : 0;
    }

}
