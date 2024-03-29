package org.darkrpa.mods.ezlogin.ezlogin.mixins;

import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.OnBlockInteract;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.OnBreakBlock;
import org.darkrpa.mods.ezlogin.ezlogin.eventos.OnItemInteract;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Final
    protected ServerPlayerEntity player;

    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"), cancellable = true)
    public void onBlockBreak(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci){
        OnBreakBlock.EVENT.invoker().run(this.player, ci);
    }

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    public void onBlockInteract(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir){
        OnBlockInteract.EVENT.invoker().run(player, cir);
    }

    @Inject(method = "interactItem", at = @At("HEAD"), cancellable = true)
    public void onItemInteract(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        OnItemInteract.EVENT.invoker().run(player, cir);
    }
}
