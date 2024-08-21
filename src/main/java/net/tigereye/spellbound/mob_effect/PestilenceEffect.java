package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tigereye.spellbound.registration.SBStatusEffects.PESTILENCE;

public class PestilenceEffect extends SBStatusEffect implements CustomDataStatusEffect{

    public PestilenceEffect(){
        super(StatusEffectCategory.HARMFUL, 0x194212);
    }

    @Override
    public boolean isInstant() {
        return false;
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY >> amplifier;
        if (i > 1) {
            return duration % i == Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY_OFFSET; //offset with poison to reduce I-frame collision
        }
        return true;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            //first, check if the status is owned by the victim. If so, they are immune.
            LivingEntity owner = null;
            StatusEffectInstance instance = entity.getStatusEffect(PESTILENCE);
            if(instance instanceof OwnedStatusEffectInstance si && fillMissingPestilenceData(si,entity)) {
                if(si.owner == entity) return;
                else owner = si.owner;
            }

            //tally up the levels of negative effects on the target
            AtomicInteger effectLevels = new AtomicInteger();
            Collection<StatusEffectInstance> effects = entity.getStatusEffects();
            effects.forEach(effect -> {
                if (effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL && effect.getEffectType() != PESTILENCE) {
                    effectLevels.addAndGet(Math.min(effect.getAmplifier(), Spellbound.config.pestilence.MAX_DAMAGE_LEVELS_PER_EFFECT - 1) + 1);
                }
            });

            //do damage based on the negitive effect count.
            if(owner != null) {
                entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.PESTILENCE,owner),
                        Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
            }
            else{
                entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.PESTILENCE),
                        Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
            }
        }
    }

    public boolean fillMissingPestilenceData(OwnedStatusEffectInstance pestilenceInstance, LivingEntity entity){
        if(pestilenceInstance.owner == null){
            if(pestilenceInstance.ownerUUID == null) {
                return false;
            }
            else{
                ServerWorld world;
                if(entity.getWorld() instanceof ServerWorld){
                    world = (ServerWorld)entity.getWorld();
                }
                else{
                    return false;
                }
                Entity owner = world.getEntity(pestilenceInstance.ownerUUID);
                if(owner instanceof LivingEntity lEntity) {
                    pestilenceInstance.owner = lEntity;
                }
                if(pestilenceInstance.owner == null) {return false;}
            }
        }
        if(pestilenceInstance.ownerUUID == null){
            pestilenceInstance.ownerUUID = pestilenceInstance.owner.getUuid();
        }
        return true;
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return OwnedStatusEffectInstance.customFromNbt(SBStatusEffects.PESTILENCE,tag);
    }
}
