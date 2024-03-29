package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.darkrpa.mods.ezlogin.ezlogin.fileTools.Jugador;

public interface RegisterEventCallback {
    public static Event<RegisterEventCallback> EVENT = EventFactory.createArrayBacked(RegisterEventCallback.class, (listeners) -> (jugador, jugadorEnServidor) -> {
        ActionResult resultado = ActionResult.PASS;

        for(RegisterEventCallback listener : listeners){
            ActionResult ejecutado = listener.run(jugador, jugadorEnServidor);

            if(ejecutado != ActionResult.PASS){
                return ejecutado;
            }
        }

        return  resultado;
    }) ;

    public ActionResult run(Jugador jugador, ServerPlayerEntity jugadorEnServidor);
}
