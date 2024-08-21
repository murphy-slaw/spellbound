package net.tigereye.spellbound.blocks.entity;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.spellbound.data.SunkenTreasure.SunkenTreasureManager;
import net.tigereye.spellbound.registration.SBItems;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends BlockEntity {
    public static final String LOOT_DIMENSION_KEY = "LootDimension";
    Identifier dimension;

    public CrateBlockEntity(Identifier dimension, BlockPos blockPos, BlockState blockState) {
        super(SBItems.CRATE_BLOCK_ENTITY, blockPos, blockState);
        this.dimension = dimension;
    }

    public CrateBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(null, blockPos, blockState);
    }

    public void spawnLoot(World world, BlockPos pos, @Nullable PlayerEntity player) {
        if (this.world != null && this.world.getServer() != null) {
            float luck = 0;
            LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld)this.world).add(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos));
            if(player != null){
                luck = player.getLuck();
                builder.luck(player.getLuck()).add(LootContextParameters.THIS_ENTITY, player);
            }
            int quality = SunkenTreasureManager.getWeightedRandomQuality(world.getRandom(),luck);
            Identifier lootTableId = SunkenTreasureManager.getWeightedRandomLootTableId(quality,this.world.getDimensionKey().getValue(),this.world.getRandom());
            LootTable lootTable = this.world.getServer().getLootManager().getLootTable(lootTableId);
            if (player instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, lootTableId);
            }
            lootTable.generateLoot(builder.build(LootContextTypes.CHEST), 0,
                    (itemStack) -> ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(LOOT_DIMENSION_KEY, NbtElement.STRING_TYPE)) {
            this.dimension = new Identifier(nbt.getString(LOOT_DIMENSION_KEY));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if(dimension != null) {
            nbt.putString(LOOT_DIMENSION_KEY, dimension.toString());
        }
    }
}
