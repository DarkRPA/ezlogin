package org.darkrpa.mods.ezlogin.ezlogin.fileTools;

import com.google.common.hash.Hashing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Jugador {
    private static List<Jugador> jugadores = new ArrayList<>();
    private String id;
    private String hashedPassword;
    private double x;
    private double y;
    private double z;
    private boolean exists = false;
    private boolean loggedIn = false;
    private Gestor gestor;

    {
        this.gestor = Gestor.getInstance();
    }

    public static Jugador getInstance(String id) throws IOException{
        Jugador encontrado = null;

        for(Jugador jugador : Jugador.jugadores){
            if(jugador.id.equals(id)){
                encontrado = jugador;
                break;
            }
        }

        if(encontrado != null){
            return encontrado;
        }

        encontrado = new Jugador(id);
        Jugador.jugadores.add(encontrado);

        return encontrado;
    }

    private Jugador(String id) throws IOException{

        this.id = id;
        if(this.exists()){
            this.hashedPassword = this.gestor.getPlayerData(this.id);
            this.exists = true;
        }
    }

    public String hashPassword(String plainPassword){
        return Hashing.sha256().hashString(plainPassword, StandardCharsets.UTF_8).toString();
    }

    public boolean exists() throws IOException {
        boolean resultado = false;
        String passwordGuardada = this.gestor.getPlayerData(this.id);
        resultado = !passwordGuardada.isEmpty();

        return resultado;
    }

    public boolean passwordValid(String receivedPassword) throws  IOException{
        if(!this.exists)  return false;

        String hashing = this.hashPassword(receivedPassword);

        return this.hashedPassword.equals(hashing);
    }
    public boolean register(String password) throws IOException{
        if(this.exists()) return false;

        this.hashedPassword = this.hashPassword(password);

        return  this.gestor.appendPlayerData(this.id, this.hashedPassword);
    }

    public boolean isLoggedIn(){
        return this.loggedIn;
    }

    public void login(){
        this.loggedIn = true;
    }

    public void logout(){
        this.loggedIn = false;
    }

    public double[] getUltimaPosicion(){
        return new double[]{this.x, this.y, this.z};
    }

    public void setUltimaPosicion(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static void removeJugador(String id){
        Iterator<Jugador> jugadores = Jugador.jugadores.iterator();

        while(jugadores.hasNext()){
            Jugador jugadorActual = jugadores.next();
            if(jugadorActual.id.equals(id)){
                jugadores.remove();
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jugador jugador = (Jugador) o;
        return Objects.equals(id, jugador.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
