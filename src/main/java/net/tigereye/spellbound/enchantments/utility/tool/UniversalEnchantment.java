package net.tigereye.spellbound.enchantments.utility.tool;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class UniversalEnchantment extends SBEnchantment{
    public UniversalEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.universal.RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.universal.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.universal.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.universal.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.universal.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.universal.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.universal.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.universal.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.universal.IS_FOR_SALE;}
    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        if(!stack.getItem().isSuitableFor(block)){
            return miningSpeed*Spellbound.config.universal.OFF_TYPE_MINING_SPEED_FACTOR;
        }
        return miningSpeed;
    }
    @Override
    public boolean setItemSuitability(int level, ItemStack stack, BlockState state, Boolean suitability) {
        return true;
    }
}
