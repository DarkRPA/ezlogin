package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface OnChat {
    public static final Event<OnChat> EVENT = EventFactory.createArrayBacked(OnChat.class, (listeners) -> (jugador, mensaje, parametros) -> {
        for(OnChat listener : listeners){
            ActionResult resultado = listener.run(jugador, mensaje, parametros);

            if(resultado != ActionResult.PASS){
                return resultado;
            }
        }

        return ActionResult.PASS;
    });

    public ActionResult run(ServerPlayerEntity jugador, SentMessage mensaje, MessageType.Parameters parametros);
}
