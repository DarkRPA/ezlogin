package org.darkrpa.mods.ezlogin.ezlogin.fileTools;

import org.darkrpa.mods.ezlogin.ezlogin.Main;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Clase encargada de realizar todas las gestiones relacionadas con el archivo de las contraseñas y demás, tenemos que tener en cuenta
 * de que al ser un servidor no premium tal vez tengamos que basarnos en el nombre más que en el ID a la hora de almacenar las passwords
 */
public class Gestor {
    public static final String NOMBRE_FICHERO = "passwd.csv";
    public static final String NOMBRE_SOLO_ACEPTADOS = "aceptados.csv";
    public static final String NOMBRE_DIRECTORIO = "ezlogin";
    private File directorioDatos;
    private File archivoDatos;
    private File archivoObtenidos;
    private ArrayList<String> nombresAceptados = new ArrayList<>();

    private static Gestor instanciaActual;


    {
        boolean result = true;

        try{
            if(!this.getOrCreateDirectory() || !this.getOrCreateDataFile()){
                result = false;
            }
        }catch(IOException e){
            result = false;
        }

        if(!result){
            Main.disableMod("No se ha podido crear el directorio de datos o el fichero de datos");
        }
    }

    public static Gestor getInstance() throws IOException {
        if(Gestor.instanciaActual == null){
            Gestor.instanciaActual = new Gestor();
        }

        return Gestor.instanciaActual;
    }
    protected Gestor() throws IOException {
        //Hemos conseguido el directorio finalmente, resulta que el problema estaba no en esta parte del codigo, sino en la siguiente que ya está eliminada
        //De todas formas, Fabric no nos limita para nada en que código Java podemos ejecutar por lo que simplemente podemos, si ponemso bien la ruta,
        //crear nuestro archivo de datos.
        this.getOrCreateDirectory();
        this.getOrCreateDataFile();
        this.loadAceptados();
    }

    public boolean getOrCreateDirectory() throws IOException{
//        URI rutaActual = Main.getRutaLocal();
//        String rutaToString = rutaActual.getPath();
//        Path caminoFormateado = Paths.get(rutaToString.substring(1), Gestor.NOMBRE_DIRECTORIO);

        File directorio = new File("mods", Gestor.NOMBRE_DIRECTORIO);

        if(!directorio.exists()){
            boolean creado = directorio.mkdir();
            Main.LOGGER.warn("No existe el directorio de datos, creandolo");
            if(!creado){
                Main.LOGGER.error("No se ha podido crear el directorio de datos, deshabilitando mod");
                return false;
            }
        }

        this.directorioDatos = directorio;
        return true;
    }

    public boolean getOrCreateDataFile() throws IOException{
        if(!this.directorioDatos.exists()){
            return false;
            //No existe el directorio de datos por alguna razon, no debería llegar a este lugar
        }

        Path rutaFichero = Paths.get(this.directorioDatos.getPath(), Gestor.NOMBRE_FICHERO);
        Path ficheroAceptados = Paths.get(this.directorioDatos.getPath(), Gestor.NOMBRE_SOLO_ACEPTADOS);

        File fichero = new File(rutaFichero.toString());
        File ficheroAceptadosObtenido = new File(ficheroAceptados.toString());

        if(!fichero.exists()){
            Main.LOGGER.warn("No existe el fichero de datos, creandolo");
            boolean resultado = fichero.createNewFile();

            if(!resultado){
                Main.LOGGER.warn("No se ha podido crear el archivo de datos, deshabilitando mod");
                return false;
            }
        }

        if(!ficheroAceptadosObtenido.exists()){
            Main.LOGGER.warn("No existe el fichero de datos, creandolo");
            boolean resultado = ficheroAceptadosObtenido.createNewFile();

            if(!resultado){
                Main.LOGGER.warn("No se ha podido crear el archivo de datos, deshabilitando mod");
                return false;
            }
        }

        this.archivoDatos = fichero;
        this.archivoObtenidos = ficheroAceptadosObtenido;


        return true;
    }

    public void loadAceptados() throws IOException{
        BufferedReader lector = new BufferedReader(new FileReader(this.archivoObtenidos));
        String linea;

        while((linea = lector.readLine()) != null){
            this.nombresAceptados.add(linea);
        }

        lector.close();
    }

    public String getPlayerData(String id) throws IOException{
        BufferedReader lector = new BufferedReader(new FileReader(this.archivoDatos));
        String resultado = "";

        String linea;
        while((linea = lector.readLine()) != null){
            String[] separador = linea.split(";");
            if(!separador[0].equals(id)) continue;
            resultado = separador[1];
            break;
        }

        return resultado;
    }

    public boolean appendPlayerData(String id, String hashedPassword){
        String formato = String.format("%s;%s\n", id, hashedPassword);

        try{
            FileWriter escritor = new FileWriter(this.archivoDatos);
            escritor.append(formato);
            escritor.flush();

            escritor.close();
        }catch(IOException e){
            return false;
        }

        return true;
    }

    public boolean isAcepted(String nombre){
        return this.nombresAceptados.contains(nombre) || (this.nombresAceptados.isEmpty());
    }
}
