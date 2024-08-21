package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PestilenceEnchantment  extends SBEnchantment {

    public PestilenceEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.pestilence.RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.pestilence.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.pestilence.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.pestilence.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.pestilence.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.pestilence.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.pestilence.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.pestilence.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.pestilence.IS_FOR_SALE;}
    @Override
    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(defender.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
            return amount;
        }
        if(source.getAttacker() == null){
            return amount;
        }
        //generate a list of harmful effects
        Collection<StatusEffectInstance> effects = defender.getStatusEffects();
        List<StatusEffectInstance> hostileEffects = new ArrayList<>();
        effects.forEach(effect -> {
            if(effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL && !effect.getEffectType().isInstant()){
                hostileEffects.add(effect);
            }
        });
        //create a stink cloud containing those effects and Pestilence
                AreaEffectCloudEntity stank = new AreaEffectCloudEntity(defender.getWorld(),defender.getX(),defender.getY(),defender.getZ());
                AtomicInteger longestDuration = new AtomicInteger(1);
                int levels = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.PESTILENCE,defender);
                stank.setOwner(defender);
                stank.setDuration(Spellbound.config.pestilence.DURATION_PER_LEVEL* levels);
                stank.setRadiusOnUse(0);
                stank.setRadius((float)Math.sqrt(1+(levels * Spellbound.config.pestilence.RADIUS_SQUARED_PER_LEVEL)));
                hostileEffects.forEach(effect -> {
                    int duration = (int) Math.min(effect.getDuration() * Spellbound.config.pestilence.STATUS_DURATION_FACTOR, Spellbound.config.pestilence.MAX_STATUS_DURATION);
                    stank.addEffect(new StatusEffectInstance(effect.getEffectType(),duration,effect.getAmplifier()));
                    if(duration > longestDuration.get()){
                        longestDuration.set(duration);
                    }
                });
                OwnedStatusEffectInstance osei = new OwnedStatusEffectInstance(defender, SBStatusEffects.PESTILENCE,
                        Math.max(Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY,(longestDuration.get() - (longestDuration.get() % Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY))
                                + Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY_OFFSET));
                stank.addEffect(osei);
                defender.getWorld().spawnEntity(stank);
            //}
        //}
        return amount;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() == Items.BOOK
                || super.isAcceptableItem(stack);
    }
}
