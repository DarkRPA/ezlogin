package org.darkrpa.mods.ezlogin.ezlogin.eventos;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public interface OnBlockInteract {
    public static Event<OnBlockInteract> EVENT = EventFactory.createArrayBacked(OnBlockInteract.class, (listeners) -> (jugador, callbackInfo) -> {
        for(OnBlockInteract listener : listeners){
            ActionResult resultado = listener.run(jugador, callbackInfo);
            if(resultado != ActionResult.PASS){
                return resultado;
            }
        }

        return ActionResult.PASS;
    });

    public ActionResult run(ServerPlayerEntity jugador, CallbackInfoReturnable<ActionResult> callbackInfo);
}
