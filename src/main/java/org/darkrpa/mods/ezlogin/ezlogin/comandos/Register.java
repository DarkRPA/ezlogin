package org.darkrpa.mods.ezlogin.ezlogin.comandos;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.darkrpa.mods.ezlogin.ezlogin.Main;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.RegisterEventCallback;
import org.darkrpa.mods.ezlogin.ezlogin.fileTools.Jugador;

import java.io.IOException;

public class Register extends CustomServerCommmand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        //Primero de todos, debemos de comprobar que el jugador no esté registrado, por que si lo está no le dejaremos volver a registrarse

        ServerCommandSource fuente = context.getSource();

        if(!super.verificarNoTerminal(fuente)){
            return -1;
        }

        String nombreJugador = fuente.getEntity().getName().getString();
        Jugador jugador;
        try{
            jugador = Jugador.getInstance(nombreJugador);
            if(jugador.exists()) return -4;
            String password = StringArgumentType.getString(context, "password");
            String repeticion = StringArgumentType.getString(context, "repeticion");

            if(!password.equals(repeticion)){
                fuente.sendFeedback(Text.literal("Las contraseñas no son iguales"), false);
                return 0;
            }

            if(!jugador.register(password)){
                fuente.sendFeedback(Text.literal("Por algún motivo desconocido no se ha podido registrar"), false);
                return -5;
            }




        }catch(IOException e){
            Main.LOGGER.warn("Ha ocurrido un error con el jugador "+nombreJugador);
            return -3;
        }

        fuente.sendFeedback(Text.literal("Ha sido registrado con exito"), false);
        jugador.login();
        RegisterEventCallback.EVENT.invoker().run(jugador, fuente.getPlayer());
        return 0;
    }
}
