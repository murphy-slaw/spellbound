package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.blocks.CrateBlock;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import net.tigereye.spellbound.items.BagOfRocks;
import net.tigereye.spellbound.items.BagOfTrophies;

public class SBItems {

    public static final Item BAG_OF_ROCKS = new BagOfRocks(new Item.Settings());
    public static final Item BAG_OF_TROPHIES = new BagOfTrophies(new Item.Settings());
    public static final Item IRON_PEBBLE = new Item(new Item.Settings().maxCount(64));
    public static final Item COPPER_PEBBLE = new Item(new Item.Settings().maxCount(64));
    public static final Item GOLD_PEBBLE = new Item(new Item.Settings().maxCount(64));
    public static final Item DIAMOND_SHARD = new Item(new Item.Settings().maxCount(64));
    public static final Item EMERALD_SHARD = new Item(new Item.Settings().maxCount(64));
    //public static final Item ANCIENT_SHARD = new Item(new Item.Settings().maxCount(64));
    public static final Block CRATE = new CrateBlock(FabricBlockSettings.copyOf(Blocks.BARREL));

    public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier(Spellbound.MODID, "crate_block_entity"),
            BlockEntityType.Builder.create(CrateBlockEntity::new, CRATE).build(null)
    );
    public static void register() {
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "bag_of_rocks"), BAG_OF_ROCKS);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "bag_of_trophies"), BAG_OF_TROPHIES);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "iron_pebble"), IRON_PEBBLE);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "copper_pebble"), COPPER_PEBBLE);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "gold_pebble"), GOLD_PEBBLE);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "diamond_shard"), DIAMOND_SHARD);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "emerald_shard"), EMERALD_SHARD);
        //Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "ancient_shard"), ANCIENT_SHARD);
        Registry.register(Registries.BLOCK, new Identifier(Spellbound.MODID, "crate"), CRATE);
        Registry.register(Registries.ITEM, new Identifier(Spellbound.MODID, "crate"), new BlockItem(CRATE, new FabricItemSettings()));

        registerItemGroups();
    }

    private static void registerItemGroups(){
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(BAG_OF_ROCKS);
            entries.add(BAG_OF_TROPHIES);
            entries.add(IRON_PEBBLE);
            entries.add(COPPER_PEBBLE);
            entries.add(GOLD_PEBBLE);
            entries.add(DIAMOND_SHARD);
            entries.add(EMERALD_SHARD);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> entries.add(CRATE));
    }
}
