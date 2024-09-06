package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SBStatusEffectHelper {



    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBStatusEffectHelper.forEachStatusEffect((instance,effectsToAdd,effectsToRemove) -> {
            if(instance.getEffectType() instanceof SBStatusEffect) {
                mutableFloat.setValue(((SBStatusEffect)(instance.getEffectType())).onPreArmorDefense(instance, source, defender, mutableFloat.floatValue(), effectsToAdd, effectsToRemove));
            }
        }, defender.getStatusEffects(), defender);
        return mutableFloat.floatValue();
    }

    public static void onDeath(DamageSource source, LivingEntity defender){
        SBStatusEffectHelper.forEachStatusEffect((instance,effectsToAdd,effectsToRemove) -> {
            if(instance.getEffectType() instanceof SBStatusEffect) {
                ((SBStatusEffect)(instance.getEffectType())).onDeath(instance, source, defender, effectsToAdd, effectsToRemove);
            }
        }, defender.getStatusEffects(), defender);
    }

    private static void forEachStatusEffect(SBStatusEffectHelper.Consumer consumer, Collection<StatusEffectInstance> effects, LivingEntity entity) {
        List<StatusEffectInstance> effectsToAdd = new ArrayList<>();
        List<StatusEffect> effectsToRemove = new ArrayList<>();
        for (StatusEffectInstance effect:
             effects) {
            consumer.accept(effect,effectsToAdd,effectsToRemove);
        }
        for (StatusEffect effect:
                effectsToRemove) {
            entity.removeStatusEffect(effect);
        }
        for (StatusEffectInstance effect:
                effectsToAdd) {
            entity.addStatusEffect(effect);
        }
    }

    @FunctionalInterface
    interface Consumer {
        void accept(StatusEffectInstance instance, List<StatusEffectInstance> effectsToAdd, List<StatusEffect> effectsToRemove);
    }
}
