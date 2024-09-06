package net.tigereye.spellbound.mob_effect.instance;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class OwnedStatusEffectInstance extends StatusEffectInstance{
    public Entity owner = null;
    public UUID ownerUUID = null;

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffect statusEffect) {
        super(statusEffect);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffect statusEffect, int duration) {
        super(statusEffect, duration);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffect statusEffect, int duration, int amplifier) {
        super(statusEffect, duration, amplifier);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean visible) {
        super(statusEffect, duration, amplifier, ambient, visible);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(UUID ownerUUID, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.ownerUUID = ownerUUID;
    }

    public OwnedStatusEffectInstance(StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        if(statusEffectInstance instanceof OwnedStatusEffectInstance){
            this.owner = ((OwnedStatusEffectInstance) statusEffectInstance).owner;
            if(owner != null) {
                this.ownerUUID = owner.getUuid();
            }
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.shouldShowParticles());
        tag.putBoolean("ShowIcon", this.shouldShowIcon());
        if(ownerUUID != null) {
            tag.putUuid("OwnerUUID", ownerUUID);
        }
        return tag;
    }

    public static OwnedStatusEffectInstance customFromNbt(StatusEffect type, NbtCompound tag) {
        int amplifier = tag.getByte("Amplifier");
        int duration = tag.getInt("Duration");
        boolean ambient = tag.getBoolean("Ambient");
        boolean showParticles = true;
        UUID ownerUUID = null;
        if (tag.contains("ShowParticles", 1)) {
            showParticles = tag.getBoolean("ShowParticles");
        }

        boolean showIcon = showParticles;
        if (tag.contains("ShowIcon", 1)) {
            showIcon = tag.getBoolean("ShowIcon");
        }

        if(tag.contains("OwnerUUID")){
            ownerUUID = tag.getUuid("OwnerUUID");
        }
        return new OwnedStatusEffectInstance(ownerUUID,type,duration,amplifier,ambient,showParticles,showIcon,null,Optional.empty());
    }

    public boolean fillMissingOwnerData(ServerWorld world){
        if(this.owner != null && this.ownerUUID != null){
            return true;
        }
        if(this.owner == null && this.ownerUUID == null){
            return false;
        }
        if(this.owner == null){
            if(world == null){
                return false;
            }
            this.owner = world.getEntity(this.ownerUUID);
            if(this.owner == null) {return false;}
        }
        if(this.ownerUUID == null){
            this.ownerUUID = this.owner.getUuid();
        }
        return true;
    }

    public boolean fillMissingOwnerData(Entity entity){
        World world = entity.getWorld();
        if(world instanceof ServerWorld sWorld){
            return fillMissingOwnerData(sWorld);
        }
        else{
            return fillMissingOwnerData((ServerWorld) null);
        }
    }
}
