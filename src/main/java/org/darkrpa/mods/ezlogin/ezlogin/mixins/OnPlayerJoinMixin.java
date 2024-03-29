package org.darkrpa.mods.ezlogin.ezlogin.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.OnPlayerJoin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(PlayerManager.class)
public abstract class OnPlayerJoinMixin {
    @Inject(method = "onPlayerConnect", at = @At("TAIL"))
    private void onJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci){
        OnPlayerJoin.EVENT.invoker().run(player);
    }
}
