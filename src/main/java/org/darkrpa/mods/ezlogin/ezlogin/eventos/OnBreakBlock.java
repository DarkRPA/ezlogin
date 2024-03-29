package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public interface OnBreakBlock {
    public static Event<OnBreakBlock> EVENT = EventFactory.createArrayBacked(OnBreakBlock.class, (listeners) -> (jugador, callback) -> {
        for(OnBreakBlock listener : listeners){
            ActionResult resultado = listener.run(jugador, callback);

            if(resultado != ActionResult.PASS){
                return resultado;
            }
        }

        return ActionResult.PASS;
    });

    public ActionResult run(ServerPlayerEntity jugador, CallbackInfo callback);
}
