package net.tigereye.spellbound.mixins;

import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FabricItem.class)
public interface FabricItemMixin {
    @Inject(at = @At("RETURN"), method = "isSuitableFor", cancellable = true)
    public default void spellboundFabricItemIsSuitableForMixin(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir){
        if(stack.hasEnchantments()){
            boolean suitability = cir.getReturnValue();
            suitability = SBEnchantmentHelper.setItemSuitability(stack, state, suitability);
            cir.setReturnValue(suitability);
        }
    }
}
