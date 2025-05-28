package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.haremal.ritualsapi.RitualsAPI
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.GAME)
object BloodEventsSG {
    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent.Post) {
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
    fun onUseRitualDaggerItem(event: LivingEntityUseItemEvent.Tick){
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
}