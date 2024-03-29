package org.darkrpa.mods.ezlogin.ezlogin;

import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.darkrpa.mods.ezlogin.ezlogin.comandos.Login;
import org.darkrpa.mods.ezlogin.ezlogin.comandos.Register;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.*;
import org.darkrpa.mods.ezlogin.ezlogin.fileTools.Gestor;
import org.darkrpa.mods.ezlogin.ezlogin.fileTools.Jugador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Main implements ModInitializer {
    public static final String MOD_ID = "darkrpa_ezlogin";
    public static final long SLEEPING_TIME = 150;
    public static final long MESSAGE_INTERVAL = 1500;
    private static boolean modDisabled = false;
    public static final Logger LOGGER = LoggerFactory.getLogger("EZLogin");
    private static URI ruta;
    /*
     * Para poder tener a los jugadores controlados lo que vamos a hacer es que cuando un jugador entre al servidor vamos a iniciar un nuevo hilo para el
     * que tenga un listener de eventos
     *
     * 1 parametro = esta o no logueado
     * 2 parametro = hilo de bloqueo
     * 3 parametro = numero de intentos fallidos
     */

    static {
        try {
            Main.ruta = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        } catch (URISyntaxException e) {
            Main.LOGGER.error("No se ha podido conseguir la posición del JAR");
        }
    }

    @Override
    public void onInitialize() {
        try {
            Gestor.getInstance();
        } catch (IOException e) {
            Main.LOGGER.error("Error critico a la hora de establecer un gestor");
        }


        Register registro = new Register();
        Login login = new Login();

        if(!Main.modDisabled){
            CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment)->{
                dispatcher.register(CommandManager.literal("register")
                        .then(CommandManager.argument("password", StringArgumentType.word())
                                .then(CommandManager.argument("repeticion", StringArgumentType.word()).executes(registro))));
            });

            CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment)->{
                dispatcher.register(CommandManager.literal("login").then(CommandManager.argument("password", StringArgumentType.string()).executes(login)));
            });

            OnPlayerLeave.EVENT.register((jugador) -> {
                //El jugador ha salido, lo eliminamos de la lista
                try{
                    Jugador jugador1 = Jugador.getInstance(jugador.getName().getString());
                    //Esto no es que sea lo mas seguro pero bro es para un servidor de amigos, tampoco me va a venir la nasa
                    jugador1.login();
                    Jugador.removeJugador(jugador.getName().getString());
                }catch(Exception e){
                    Main.LOGGER.error(e.getMessage());
                }
            });

            OnPlayerJoin.EVENT.register((jugador -> {

                try{
                    Gestor gestor = Gestor.getInstance();
                    if(!gestor.isAcepted(jugador.getName().getString())){
                        jugador.networkHandler.disconnect(Text.literal("No estas en la whitelist"));
                    }

                    Main.startLoginRegisterThread(jugador);
                }catch(IOException e){
                    Main.LOGGER.error(e.getMessage());
                }
                return ActionResult.PASS;
            }));

            RegisterEventCallback.EVENT.register((jugador, jugadorServidor)-> {
                jugadorServidor.changeGameMode(GameMode.SURVIVAL);
                return ActionResult.CONSUME;
            });

            LoginEventCallback.EVENT.register(((jugador, jugadorServidor) -> {
                jugadorServidor.changeGameMode(GameMode.SURVIVAL);
                return ActionResult.CONSUME;
            }));

            OnChat.EVENT.register((jugador, mensaje, parametros) ->  {
                ActionResult resultado = ActionResult.PASS;

                try{
                    Jugador jugadorNuestro = Jugador.getInstance(jugador.getName().getString());
                    if(!jugadorNuestro.isLoggedIn()){
                        resultado = ActionResult.CONSUME;
                    }

                    String mensajeObtenido = mensaje.toString();

                    if(mensajeObtenido.startsWith("/register") || mensajeObtenido.startsWith("/login")){
                        resultado = ActionResult.PASS;
                    }
                }catch(IOException e){
                    Main.LOGGER.error(e.getMessage());
                }

                return resultado;
            });
        }
    }



    public static URI getRutaLocal(){
        return Main.ruta;
    }

    public static void disableMod(String causa){
        Main.modDisabled = true;
        Main.LOGGER.error(String.format("El mod ha sido deshabilitado por: %s", causa));
    }

    private static void startLoginRegisterThread(ServerPlayerEntity jugador) throws IOException{
        //Primero miramos si el jugador tiene o no datos guardados y luego esperemos a la orden de que se ha logueado o registrado

        String nombreJugador = jugador.getName().getString();
        Jugador jugadorEncontrado = Jugador.getInstance(nombreJugador);

        jugador.changeGameMode(GameMode.SPECTATOR);

        /*
         *  0 = Logueado
         *  1 = Hilo
         */

        Thread hiloAvisoLogin;
        Thread hiloPrincipal;

        Runnable avisoLogin = () -> {
            try{
                String mensaje = (jugadorEncontrado.exists())?"Para entrar, utilice el comando /login <password>":"No está registrado, registrese con /register <password> <password>";
                boolean loggedIn = jugadorEncontrado.isLoggedIn();

                while(!loggedIn){
                    Thread.sleep(Main.MESSAGE_INTERVAL);
                    loggedIn = jugadorEncontrado.isLoggedIn();

                    if(!loggedIn){
                        jugador.sendMessageToClient(Text.literal(mensaje), false);
                    }
                }

            }catch(Exception e){
                Main.LOGGER.warn(String.format("No se ha podido mostrar el mensaje repeticional a %s", jugador.getName().getString()));
            }
        };

        Runnable logicaTP = () -> {
            boolean logueado = false;
            Vec3d posicion = jugador.getPos();



            try{
                while(!logueado){
                    logueado = jugadorEncontrado.isLoggedIn();
                    //Main.LOGGER.warn("EEEEE");
                    Thread.sleep(Main.SLEEPING_TIME);
                    //Main.LOGGER.warn("BBBBBB");
                    jugador.teleport(posicion.x, posicion.y, posicion.z);
                    //Main.LOGGER.warn("CCCCCC");
                }
            }catch(Exception e){
                Main.LOGGER.error("Por alguna razon el hilo del logging ha fallado");
                //jugador.getWorld().disconnect();
            }
        };

        hiloPrincipal = new Thread(logicaTP);
        hiloAvisoLogin = new Thread(avisoLogin);

        hiloAvisoLogin.start();
        hiloPrincipal.start();

    }
}
