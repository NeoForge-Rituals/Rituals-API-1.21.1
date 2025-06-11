package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.haremal.ritualsapi.RitualsAPI
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.item.ShovelItem
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.living.LivingEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.tick.EntityTickEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import java.util.UUID
import kotlin.uuid.toKotlinUuid

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.GAME)
object RitualEventsSG {
    @SubscribeEvent
    fun bloodDropping(event: ServerTickEvent.Post) {
        BloodStainEntity.Companion.bloodDrop.entries.removeIf { (_, data) -> data.timer <= 0 }
        BloodStainEntity.Companion.bloodDrop.forEach { (player, data) ->
            data.timer = data.timer - 1
            val ticks = data.chance + (1..10).random()
            if (ticks < 40) { data.chance = ticks; return@forEach } else data.chance = 0

            val posUnder = BlockPos.containing(player.position().x, player.position().y - 0.01, player.position().z)
            val stateUnder = player.level().getBlockState(posUnder)

            val shape = stateUnder.getCollisionShape(player.level(), posUnder)
            val isFullBlock = if (shape.isEmpty) false else shape.bounds() == AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

            val playerPos = player.position().takeIf { !stateUnder.isAir && isFullBlock} ?: return@forEach
            val playerBox = AABB(
                playerPos.x - 0.05, playerPos.y - 0.1, playerPos.z - 0.05,
                playerPos.x + 0.05, playerPos.y + 0.1, playerPos.z + 0.05
            )

            val bloodStains = player.level().getEntitiesOfClass(BloodStainEntity::class.java, playerBox)
            var nearby = false
            for(stain in bloodStains){
                val stainPos = stain.position()
                val box = AABB(
                    stainPos.x - 0.05, stainPos.y - 0.1, stainPos.z - 0.05,
                    stainPos.x + 0.05, stainPos.y + 0.1, stainPos.z + 0.05
                )
                if (playerBox.intersects(box)) {
                    nearby = true
                    break
                }
            }

            if (!nearby) {
                val dimKey: ResourceKey<Level> = player.level().dimension()  // the player’s current dimension key
                val serverLevel: ServerLevel = event.server.getLevel(dimKey)?: return@forEach
                val stainType = ModRegistries.BLOOD_STAIN_ENTITY.get() ?: return@forEach
                stainType.create(serverLevel).let { stain ->
                    stain?.setPos(player.position())
                    if (stain != null) serverLevel.addFreshEntity(stain)
                }
            }
        }
    }

    @SubscribeEvent
    fun useRitualDaggerItem(event: LivingEntityUseItemEvent.Tick){
        val player = event.entity
        if (event.item.item == Items.BRUSH && player is Player && !player.level().isClientSide) {
            val hit = player.pick(5.0, 0f, false)
            val pos = hit.location
            val range = 0.1
            val box = AABB(
                pos.x - range, pos.y, pos.z - range,
                pos.x + range, pos.y + range, pos.z + range
            )
            val entities = player.level().getEntitiesOfClass(BloodStainEntity::class.java, box)
            entities.firstOrNull()?.discard()
        }
    }

    @SubscribeEvent
    fun knockOutWithAShovel(event: LivingDamageEvent.Pre) {
        val target = event.entity
        val attacker = event.source.entity

        if (!target.level().isClientSide && attacker is Player && target is LivingEntity) {
            val weapon = attacker.mainHandItem.item
            if (weapon is ShovelItem) {
                val behindDistance = 1.5
                val targetPos = target.position()
                val look = target.lookAngle.normalize()

                // Calculate box behind the target
                val behindPos = targetPos.subtract(look.scale(behindDistance))
                println(behindPos)
                val box = AABB(behindPos.x - 1.5, behindPos.y - 1.0, behindPos.z - 1.5,
                    behindPos.x + 1.5, behindPos.y + 1.0, behindPos.z + 1.5)

                if (box.contains(attacker.position())) {
                    target.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 10)) // placeholder
                    target.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 200))
                    target.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 200))
                }
            }
        }
    }
}

