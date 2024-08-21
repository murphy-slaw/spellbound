package net.tigereye.spellbound.registration;

import net.tigereye.spellbound.data.ResurfacingItemsPersistentState;
import net.tigereye.spellbound.enchantments.fortune.SunkenTreasureEnchantment;

public class SBLootTableListeners {

    public static void register(){
        ResurfacingItemsPersistentState.registerResurfacingInChest();
        SunkenTreasureEnchantment.registerSunkenTreasureCrateFishing();

    }
}
