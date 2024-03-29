package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

public interface OnPlayerLeave {
    public static final Event<OnPlayerLeave> EVENT = EventFactory.createArrayBacked(OnPlayerLeave.class, (listeners) -> (jugador) -> {
        for(OnPlayerLeave listener : listeners){
            listener.run(jugador);
        }
    });

    public void run(ServerPlayerEntity jugador);
}
