package org.darkrpa.mods.ezlogin.ezlogin.mixins;

import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.OnPlayerLeave;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class OnPlayerLeaveMixin {
    @Inject(method = "remove", at = @At("TAIL"))
    public void onLeave(ServerPlayerEntity player, CallbackInfo ci){
        OnPlayerLeave.EVENT.invoker().run(player);
    }
}
