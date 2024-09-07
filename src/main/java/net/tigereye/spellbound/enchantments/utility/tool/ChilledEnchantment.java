package net.tigereye.spellbound.enchantments.utility.tool;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.Chilled.ChilledManager;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class ChilledEnchantment extends SBEnchantment{
    public ChilledEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.chilled.RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.chilled.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.chilled.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.chilled.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.chilled.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.chilled.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.chilled.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.chilled.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.chilled.IS_FOR_SALE;}

    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient()) {
            return;
        }
        chillBlock(world, pos.up());
        chillBlock(world, pos.down());
        chillBlock(world, pos.east());
        chillBlock(world, pos.west());
        chillBlock(world, pos.north());
        chillBlock(world, pos.south());
    }

    public void chillBlock(World world, BlockPos pos){
        BlockState block = world.getBlockState(pos);
        Identifier resultID = ChilledManager.getResult(block);
        if(resultID != null) {
            Block result = Registries.BLOCK.get(resultID);
            if(result != null){
                world.setBlockState(pos,result.getDefaultState());
            }
        }
    }
}
