package org.darkrpa.mods.ezlogin.ezlogin.comandos;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.LoginEventCallback;
import org.darkrpa.mods.ezlogin.ezlogin.fileTools.Jugador;

import java.io.IOException;

/**
 * Clase encargada de representar al comando del login
 */
public class Login extends CustomServerCommmand implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource fuente = context.getSource();

        if(!super.verificarNoTerminal(fuente)){
            return -1;
        }

        Jugador jugador;

        try{
            jugador = Jugador.getInstance(fuente.getEntity().getName().getString());
            String password = StringArgumentType.getString(context, "password");
            if(!jugador.exists()){
                fuente.sendFeedback(Text.literal("Primero tienes que registrarte"), false);
                return -3;
            }

            if(!jugador.passwordValid(password)){
                fuente.sendFeedback(Text.literal("Contraseña incorrecta"), false);
                fuente.getPlayer().networkHandler.disconnect(Text.literal("Contraseña Incorrecta"));
                return 0;
            }
        }catch(IOException e) {
            return -2;
        }

        jugador.login();
        LoginEventCallback.EVENT.invoker().interact(jugador, fuente.getPlayer());
        fuente.sendFeedback(Text.literal("Bienvenido"), false);
        return 1;
    }
}
