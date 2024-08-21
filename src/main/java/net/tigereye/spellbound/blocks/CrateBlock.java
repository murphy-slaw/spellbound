package net.tigereye.spellbound.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CrateBlock extends BlockWithEntity {
    public CrateBlock(Settings settings) {
        super(settings);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(null,0,pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof CrateBlockEntity crateBlockEntity) {
            NbtCompound nbt = BlockItem.getBlockEntityNbt(itemStack);
            if(nbt != null){
                if(nbt.contains(CrateBlockEntity.LOOT_QUALITY_KEY)){
                    crateBlockEntity.setQuality(nbt.getInt(CrateBlockEntity.LOOT_QUALITY_KEY));
                }
                if(nbt.contains(CrateBlockEntity.LOOT_DIMENSION_KEY)){
                    crateBlockEntity.setDimension(new Identifier(nbt.getString(CrateBlockEntity.LOOT_DIMENSION_KEY)));
                }
            }
        }
    }
    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrateBlockEntity crateBlockEntity && !EnchantmentHelper.hasSilkTouch(player.getMainHandStack())) {
            crateBlockEntity.spawnLoot(world, pos, player);
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public void appendTooltip(ItemStack itemStack, BlockView world, List<Text> tooltip, TooltipContext tooltipContext) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(itemStack);
        if(nbt != null){
            tooltip.add(Text.translatable("crate.spellbound.quality"+nbt.getInt(CrateBlockEntity.LOOT_QUALITY_KEY)));
            if(nbt.contains(CrateBlockEntity.LOOT_DIMENSION_KEY)) {
                Identifier dimension = new Identifier(nbt.getString(CrateBlockEntity.LOOT_DIMENSION_KEY));
                tooltip.add(Text.translatable("crate.dimension."+dimension.toTranslationKey()));
            }
        }
    }
    //TODO: see fabricmc.net/wiki/tutorial:tooltip when updating to 1.20.5
}
