package org.darkrpa.mods.ezlogin.ezlogin.comandos;

import com.mojang.brigadier.Command;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;

public abstract  class CustomServerCommmand implements Command<ServerCommandSource> {
    public boolean verificarNoTerminal(ServerCommandSource fuente){
        boolean resultado = true;
        if(fuente.getEntity() == null){
            resultado = false;
            return resultado;
        }

        if(fuente.getEntity().getType() != EntityType.PLAYER){
            resultado = false;
        }

        return resultado;
    }
}
