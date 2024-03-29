package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface OnItemInteract {
    public static Event<OnItemInteract> EVENT = EventFactory.createArrayBacked(OnItemInteract.class, (listeners) -> (jugador, callbackInfoReturnable) -> {
        for(OnItemInteract listener : listeners){
            ActionResult resultado = listener.run(jugador, callbackInfoReturnable);

            if(resultado != ActionResult.PASS){
                return resultado;
            }
        }

        return ActionResult.PASS;
    });

    public ActionResult run(ServerPlayerEntity jugador, CallbackInfoReturnable<ActionResult> callbackInfoReturnable);
}
