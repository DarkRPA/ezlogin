package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.darkrpa.mods.ezlogin.ezlogin.fileTools.Jugador;

public interface LoginEventCallback {
    static Event<LoginEventCallback> EVENT = EventFactory.createArrayBacked(LoginEventCallback.class, (listeners) -> (jugador, jugadorServidor) -> {
        ActionResult resultado = ActionResult.PASS;

        for(LoginEventCallback listener : listeners){
            resultado = listener.interact(jugador, jugadorServidor);
        }

        return resultado;
    });

    public ActionResult interact(Jugador jugador, ServerPlayerEntity jugadorServidor);
}
