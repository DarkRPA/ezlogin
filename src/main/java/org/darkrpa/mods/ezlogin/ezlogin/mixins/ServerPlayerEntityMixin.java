package org.darkrpa.mods.ezlogin.ezlogin.mixins;

import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.OnChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void onChat(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, CallbackInfo ci){
        ServerPlayerEntity jugador = ((ServerPlayerEntity) (Object) this);

        ActionResult resultado = OnChat.EVENT.invoker().run(jugador, message, params);

        if(resultado == ActionResult.CONSUME){
            ci.cancel();
        }
    }
}
