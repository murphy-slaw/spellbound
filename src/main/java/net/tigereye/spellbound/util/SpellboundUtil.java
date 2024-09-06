package net.tigereye.spellbound.util;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;

import java.util.List;

public class SpellboundUtil {

    public static void pushPullEntitiesPlayersInRange(double range, double strength, LivingEntity user){
        Vec3d position = user.getPos();
        List<Entity> entityList = user.getWorld().getNonSpectatingEntities(Entity.class,
                new Box(position.x+ range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (Entity target:
                entityList) {
            if(target != user && (target instanceof LivingEntity || target instanceof ItemEntity)
                    //&& !(target instanceof PlayerEntity)
                    && !(user.hasPassengerDeep(target) || target.hasPassengerDeep(user))
                    && !(target.hasVehicle())
            ) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                if (target instanceof LivingEntity) {
                    forceVec = forceVec.multiply(strength * Math.max(0, 1 - ((LivingEntity) target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                } else {
                    forceVec = forceVec.multiply(strength);
                }
                target.addVelocity(forceVec.x, target.isOnGround() ? 0 : forceVec.y, forceVec.z);
                target.velocityDirty = true;
            }
        }
        /*
        List<PlayerEntity> playerList = user.getWorld().getPlayers(TargetPredicate.DEFAULT,user,
                new Box(position.x+range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (LivingEntity target:
                playerList) {
            if(target != user) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                forceVec = forceVec.multiply(strength * Math.max(0, 1 - (target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                target.velocityModified = true;
            }
        }
        */
    }

    public static boolean isPositionObstructed(World world, BlockPos pos){
        boolean feetBlocked = world.getBlockState(pos).isOpaque();
        boolean headBlocked = world.getBlockState(pos.up()).isOpaque();
        return feetBlocked || headBlocked;
    }

    public static void psudeoExplosion(Entity source, boolean excludeSource, Vec3d position, float strength, float radius, float force, float fullDamageRadius){
        if(radius == 0){
            return;
        }
        List<LivingEntity> entityList = source.getWorld().getNonSpectatingEntities(LivingEntity.class,
                new Box(position.x+ radius,position.y+radius,position.z+radius,
                        position.x-radius,position.y-radius,position.z-radius));
        for (LivingEntity target:
                entityList) {
            if(target != source || !excludeSource) {
                Vec3d forceVec = target.getPos().subtract(position);
                float distance = (float) forceVec.length();
                if(distance < radius) {
                    float proximityRatio = 1;
                    if (distance > fullDamageRadius){
                        proximityRatio = 1f - ((distance - fullDamageRadius) / (radius-fullDamageRadius));
                    }
                    float exposure = Explosion.getExposure(position,target);
                    target.damage(source.getDamageSources().create(DamageTypes.EXPLOSION,source), strength * proximityRatio * exposure);

                    forceVec = forceVec.multiply(1,0,1).normalize().add(0,.1,0);
                    forceVec = forceVec.multiply(force * proximityRatio * exposure * Math.max(0, 1 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));

                    target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                    target.velocityModified = true;
                }
            }
        }

        if(Spellbound.config.DESTRUCTIVE_SHOCKWAVES && source.getWorld() instanceof ServerWorld){
            int blockRange = Math.round(radius)+2;
            float squaredRange = radius*radius;
            float squaredStrength = strength*strength;
            BlockPos lowerCorner = source.getBlockPos().add(1-blockRange,1-blockRange,1-blockRange);
            int size = (blockRange*2)-1;
            World world = source.getWorld();
            BlockPos target;
            BlockState targetBlock;
            Block block;
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
            Explosion dummyExplosion = new Explosion(world,source, source.getX(), source.getY(), source.getZ(), 0.1f,false, Explosion.DestructionType.DESTROY_WITH_DECAY);
            for(int y = 0; y < size; y++){
                if(lowerCorner.getY()+y >= 0){
                    for(int x = 0; x < size; x++){
                        for(int z = 0; z < size; z++){
                            target = lowerCorner.add(x,y,z);
                            double distanceFromEdge = radius-Math.sqrt(target.getSquaredDistance(source.getBlockPos()));
                            double squaredDistanceFromEdge = distanceFromEdge*distanceFromEdge;
                            targetBlock = world.getBlockState(target);
                            block = targetBlock.getBlock();
                            if(!targetBlock.isAir() &&
                                    distanceFromEdge > 0 &&
                                    targetBlock.getBlock().getBlastResistance() <= squaredStrength*squaredDistanceFromEdge/squaredRange)
                            {
                                BlockPos blockPos2 = target.toImmutable();
                                world.getProfiler().push("explosion_blocks");
                                if (block.shouldDropItemsOnExplosion(dummyExplosion) && world instanceof ServerWorld) {
                                    BlockEntity blockEntity = targetBlock.hasBlockEntity() ? world.getBlockEntity(target) : null;
                                    LootContextParameterSet.Builder builder = (new LootContextParameterSet.Builder((ServerWorld)world))
                                            .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(target))
                                            .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                                            .addOptional(LootContextParameters.BLOCK_ENTITY, blockEntity)
                                            .addOptional(LootContextParameters.THIS_ENTITY, source)
                                            .add(LootContextParameters.EXPLOSION_RADIUS, strength/radius);
                                    targetBlock.getDroppedStacks(builder).forEach((stack) -> tryMergeStack(objectArrayList, stack, blockPos2));
                                }

                                world.setBlockState(target, Blocks.AIR.getDefaultState(), 3);
                                block.onDestroyedByExplosion(world, target, dummyExplosion);
                                world.getProfiler().pop();
                            }
                        }
                    }
                }
            }

            for (Pair<ItemStack, BlockPos> itemStackBlockPosPair : objectArrayList) {
                Block.dropStack(world, itemStackBlockPosPair.getSecond(), itemStackBlockPosPair.getFirst());
            }
        }

        //draw explosion particles

        if (strength < 4.0f) {
            source.getWorld().addParticle(ParticleTypes.EXPLOSION, position.x, position.y, position.z, 1.0, 0.0, 0.0);
        } else {
            source.getWorld().addParticle(ParticleTypes.EXPLOSION_EMITTER, position.x, position.y, position.z, 1.0, 0.0, 0.0);
        }

        source.getEntityWorld().playSound(null, position.getX(), position.getY(), position.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS,
                (float) Math.min(4,Math.sqrt(strength)), (1.0F + (source.getWorld().random.nextFloat() - source.getWorld().random.nextFloat()) * 0.2F) * 0.7F);
    }

    public static void psudeoExplosion(Entity source, boolean excludeSource, Vec3d position, float strength, float range, float force){
        psudeoExplosion(source, excludeSource, position, strength, range, force, 0);
    }

    private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
        int i = stacks.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = stacks.get(j);
            ItemStack itemStack = pair.getFirst();
            if (ItemEntity.canMerge(itemStack, stack)) {
                ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
                stacks.set(j, Pair.of(itemStack2, pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        stacks.add(Pair.of(stack, pos));
    }

    public static void YandereViolence(LivingEntity entity){
        int itemCount = 0;
        for (ItemStack item:
                entity.getItemsEquipped()) {
            if(!item.isEmpty()) {
                itemCount++;
            }
        }
        int monogamy = SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getItemsEquipped(), SBEnchantments.MONOGAMOUS);
        int polygamy = SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getItemsEquipped(), SBEnchantments.POLYGAMOUS);
        int damage = 0;
        //monogamous violence
        if(monogamy + polygamy > 1 && monogamy > 0) {
            damage += monogamy * (monogamy + polygamy);
        }

        //polygamous violence
        if(monogamy + polygamy < itemCount) {
            damage += polygamy * (itemCount - (monogamy + polygamy));
        }

        entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.INFIDELITY),damage);
    }

    public static Enchantment.Rarity rarityLookup(int configValue){
        return switch (configValue) {
            case 1 -> Enchantment.Rarity.COMMON;
            case 2 -> Enchantment.Rarity.UNCOMMON;
            case 3 -> Enchantment.Rarity.RARE;
            default -> Enchantment.Rarity.VERY_RARE;
        };
    }
}
