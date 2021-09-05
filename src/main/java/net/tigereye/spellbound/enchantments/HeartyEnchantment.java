package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.UUID;

public class HeartyEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    private static final UUID HEARTY_ID = UUID.fromString("94e1b6fd-beb6-4163-9beb-904374c69857");

    public HeartyEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return (level*11)-10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    @Override
    public int getMaxLevel() {

        if(Spellbound.config.HEARTY_ENABLED) return 4;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            EntityAttributeModifier mod = new EntityAttributeModifier(HEARTY_ID, "SpellboundHeartyMaxHP",
                    (SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getItemsEquipped(),SBEnchantments.HEARTY)+
                            SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(),SBEnchantments.HEARTY))
                            * Spellbound.config.HEARTY_HEALTH_FACTOR_PER_LEVEL,EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            ReplaceAttributeModifier(att, mod);
            if(entity.getHealth() > entity.getMaxHealth()){
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    public void onTickAlways(LivingEntity entity){
        if(SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(),SBEnchantments.HEARTY) == 0){
            EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if(att != null){
                att.removeModifier(HEARTY_ID);
                if(entity.getHealth() > entity.getMaxHealth()){
                    entity.setHealth(entity.getMaxHealth());
                }
            }
        }
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return !(other instanceof ProtectionEnchantment) && super.canAccept(other);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }

    private static void ReplaceAttributeModifier(EntityAttributeInstance att, EntityAttributeModifier mod)
    {
        //removes any existing mod and replaces it with the updated one.
        att.removeModifier(mod);
        att.addPersistentModifier(mod);
    }
}
