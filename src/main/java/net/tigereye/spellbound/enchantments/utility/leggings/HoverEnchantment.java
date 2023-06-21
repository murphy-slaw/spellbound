package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.mixins.ClientPlayerEntityMixin;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SpellboundUtil;

public class HoverEnchantment extends SBEnchantment {

    public HoverEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.HOVER_RARITY), EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.HOVER_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.HOVER_POWER_PER_RANK * level) - Spellbound.config.HOVER_BASE_POWER;
        if(level > Spellbound.config.HOVER_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.HOVER_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.HOVER_HARD_CAP;
        else return 0;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if the user has landed since phasing, reset
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.hasMidairJumped() && (entity.isOnGround() || entity.isClimbing() || entity.isSwimming() || entity.isTouchingWater())) {
                player.setHasMidairJumped(false);
            }
        }
    }

    @Override
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){

        if(entity.isSwimming()
        || entity.isTouchingWater()
        || stack != entity.getEquippedStack(EquipmentSlot.LEGS)){
            return;
        }
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.hasMidairJumped()) {
                entity.removeStatusEffect(SBStatusEffects.HOVERING);
                return;
            }
            player.setHasMidairJumped(true);
            NetworkingUtil.sendStatusEffectRequestPacket(
                    Spellbound.config.HOVER_DURATION_BASE + Spellbound.config.HOVER_DURATION_PER_LEVEL * level, 0,
                    SBStatusEffects.HOVERING);
        }
    }
}
