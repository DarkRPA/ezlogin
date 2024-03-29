package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface OnPlayerJoin {
    public final static Event<OnPlayerJoin> EVENT = EventFactory.createArrayBacked(OnPlayerJoin.class, (listeners) -> (jugador) -> {
        ActionResult resultado = ActionResult.PASS;
        for(OnPlayerJoin listener : listeners){

            listener.run(jugador);
        }
        return resultado;
    });

    public ActionResult run(ServerPlayerEntity jugador);
}
