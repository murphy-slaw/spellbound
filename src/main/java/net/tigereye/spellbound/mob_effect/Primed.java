package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;

public class Primed extends SBStatusEffect implements CustomDataStatusEffect{

    public Primed(){
        super(StatusEffectCategory.NEUTRAL, 0x194212);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            StatusEffectInstance temp = entity.getStatusEffect(SBStatusEffects.PRIMED);
            Entity owner = null;
            if(temp instanceof OwnedStatusEffectInstance ti) {
                ti.fillMissingOwnerData(entity);
                owner = ti.owner;
            }
            float range = (amplifier+2)*Spellbound.config.priming.SHOCKWAVE_RADIUS_SCALE;
            SpellboundUtil.psudeoExplosion(owner != null ? owner : entity
                    , Spellbound.config.priming.SAFE_FOR_USER
                    , entity.getPos()
                    , ((amplifier*amplifier)+1)*Spellbound.config.priming.SHOCKWAVE_DAMAGE_SCALE
                    ,(amplifier+2)*Spellbound.config.priming.SHOCKWAVE_RADIUS_SCALE
                    ,(amplifier+1)*Spellbound.config.priming.SHOCKWAVE_FORCE_SCALE
                    ,range * Spellbound.config.priming.SHOCKWAVE_FULL_DAMAGE_RADIUS
            );
        }
    }

    public void onDeath(StatusEffectInstance instance, DamageSource source, LivingEntity defender, List<StatusEffectInstance> effectsToAdd, List<StatusEffect> effectsToRemove) {
        applyUpdateEffect(defender,instance.getAmplifier()+1);
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return OwnedStatusEffectInstance.customFromNbt(this, tag);
    }
}
