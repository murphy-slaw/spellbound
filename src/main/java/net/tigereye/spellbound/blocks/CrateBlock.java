package net.tigereye.spellbound.blocks;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CrateBlock extends BlockWithEntity {
    public CrateBlock(Settings settings) {
        super(settings);
    }


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(null,pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrateBlockEntity crateBlockEntity && !EnchantmentHelper.hasSilkTouch(player.getMainHandStack())) {
            crateBlockEntity.spawnLoot(world, pos, player);
        }
        super.onBreak(world, pos, state, player);
    }
}
