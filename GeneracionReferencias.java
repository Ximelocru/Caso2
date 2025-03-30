import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GeneracionReferencias {
Imagen imagenOut;
String nombreImagen;


//------------------------Atributos Creados nosotros---    
//DATOS:
int TP;

//-----------------------------------------------------
    public GeneracionReferencias(int TP, String nombreImagen, Imagen imagenOut){
        this.TP=TP;
        this.nombreImagen=nombreImagen;
        this.imagenOut=imagenOut;
    }
    
    public void generarArchivoReferencias(){
        int NF=imagenOut.alto;
        int NC=imagenOut.ancho;
        int NR=0;
        //int NP= (int) Math.ceil((double) totalBytes / TP);;
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreImagen))) { 
            
            //TP
            writer.write("TP=" + TP);
            writer.newLine();

            //NF
            writer.write("NF=" + NF);
            writer.newLine();

            //NC
            writer.write("NC=" + NC);
            writer.newLine();

            //NR
            writer.write("NR=" + NR);
            writer.newLine();

            //NP
            //writer.write("NP=" + NP);
            writer.newLine();

            for(int i=1;i<NF-1;i++){
                for(int j=1;j<NC-1;j++){

                }
            }


        } catch (IOException e) {
        e.printStackTrace();
    }
    }

}
