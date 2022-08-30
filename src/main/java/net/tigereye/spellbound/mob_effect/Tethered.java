package net.tigereye.spellbound.mob_effect;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class Tethered extends SBStatusEffect implements CustomDataStatusEffect{
    public Tethered(){
        super(StatusEffectCategory.HARMFUL, 0xaaaaaa);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //Spellbound.LOGGER.info("Dragging Target");
        StatusEffectInstance temp = entity.getStatusEffect(SBStatusEffects.TETHERED);
        if(temp instanceof TetheredInstance){
            TetheredInstance ti = (TetheredInstance)temp;
            //attempt to get any missing data. Remove the debuff if if fails because we can't use it.
            if(!fillMissingTetheredData(ti,entity)){
                entity.removeStatusEffect(SBStatusEffects.TETHERED);
                return;
            }
            //if the anchor has been removed from the world, remove the tether
            if(ti.anchor.isRemoved()){
                entity.removeStatusEffect(SBStatusEffects.TETHERED);
                return;
            }
            //otherwise, drag them in if they are past the leash
            entity.fallDistance = 0;

            Vec3d pullVector = new Vec3d(ti.anchor.getX() - entity.getX(), ti.anchor.getY() - entity.getY(), ti.anchor.getZ() - entity.getZ());

            if(!(entity.world.isClient)) {
                if (pullVector.length() >= Spellbound.config.TETHER_LEASH_LENGTH) {
                    entity.setPos(entity.getX(), entity.getY() + pullVector.y * 0.015D * Spellbound.config.TETHER_ATTRACTION_FACTOR, entity.getZ());

                    double d = 0.05D * Spellbound.config.TETHER_ATTRACTION_FACTOR;
                    Vec3d impulseVector = pullVector.multiply(d).subtract(entity.getVelocity().multiply(0.15D));
                    entity.addVelocity(impulseVector.x, impulseVector.y, impulseVector.z);
                    entity.velocityModified = true;
                }
            }
            //draw particals between entity and anchor

            int sparks = entity.getRandom().nextInt(Math.min(20,Math.max(3,(int)(pullVector.lengthSquared()/5))));
            Vec3d basePosition = entity.getPos();
            for (int i = 0; i < sparks; i++) {
                Vec3d modifiedPosition = basePosition.add(pullVector.multiply(entity.getRandom().nextFloat()));
                float driftX = (entity.getRandom().nextFloat() - .5f) * .15f;
                float driftY = (entity.getRandom().nextFloat() - .5f) * .15f;
                float driftZ = (entity.getRandom().nextFloat() - .5f) * .15f;
                entity.world.addParticle(ParticleTypes.ELECTRIC_SPARK,
                        modifiedPosition.x+driftX, modifiedPosition.y+driftY, modifiedPosition.z+driftZ,
                        0, 0, 0);
            }
        }
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return TetheredInstance.customFromNbt(tag);
    }

    public boolean fillMissingTetheredData(TetheredInstance tetheredInstance, LivingEntity entity){
        if(tetheredInstance.anchor == null){
            if(tetheredInstance.tetherUUID == null) {
                return false;
            }
            else{
                ServerWorld world;
                if(entity.world instanceof ServerWorld){
                    world = (ServerWorld)entity.world;
                }
                else{
                    return false;
                }
                tetheredInstance.anchor = world.getEntity(tetheredInstance.tetherUUID);
                if(tetheredInstance.anchor == null) {return false;}
            }
        }
        if(tetheredInstance.tetherUUID == null){
            tetheredInstance.tetherUUID = tetheredInstance.anchor.getUuid();
        }
        return true;
    }
}
