package net.tigereye.spellbound.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {
    @Inject(at = @At(value="RETURN"), method = "getMiningSpeedMultiplier", cancellable = true)
    public void spellboundGetMiningSpeedMultiplierMixin(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(SBEnchantmentHelper.getBaseMiningSpeed(((MiningToolItem)(Object)this),stack, state, cir.getReturnValue()));
    }
}
