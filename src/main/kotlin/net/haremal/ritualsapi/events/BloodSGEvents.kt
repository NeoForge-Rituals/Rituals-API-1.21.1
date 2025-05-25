package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.ModRegistries
import net.haremal.ritualsapi.api.entity.BloodStainEntity
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.ServerTickEvent
import kotlin.collections.forEach
import kotlin.concurrent.timer

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME)
object BloodSGEvents {
    // TODO: ONLY WHEN CROUCHING AND ONLY ON THE BLOCK BELOW THE PLAYER (IF THERE IS)

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
        BloodStainEntity.timer.entries.removeIf { (_, time) -> time <= 0 }
        BloodStainEntity.timer.forEach { (player, time) ->
            BloodStainEntity.timer[player] = time - 1

            val pos = player.position()
            val box = AABB(
                pos.x - 0.2, pos.y - 0.2, pos.z - 0.2,
                pos.x + 0.2, pos.y + 0.2, pos.z + 0.2
            )
            val nearby = player.level().getEntitiesOfClass(BloodStainEntity::class.java, box)

            if (nearby.isEmpty()) {
                val dimKey: ResourceKey<Level> = player.level().dimension()  // the player’s current dimension key
                val serverLevel: ServerLevel = event.server.getLevel(dimKey)?: return
                val stainType = ModRegistries.BLOOD_STAIN.get() ?: return@forEach
                println("Spawning blood stain in ${dimKey.location()}")

                stainType.create(serverLevel).let { stain ->
                    stain?.setPos(player.position())
                    if (stain != null) serverLevel.addFreshEntity(stain)
                }
            }
        }
    }
}