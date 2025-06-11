package net.haremal.ritualsapi.cults

import net.haremal.ritualsapi.RitualsAPI
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME)
object CultEventsSG {
    @SubscribeEvent
    fun tickCult(event: LevelTickEvent.Post) {
        val level = event.level
        if (level is ServerLevel) CultMemberManager.tickCult(level)
    }

    @SubscribeEvent
    fun saveCultForLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            val cult = CultMemberManager.getCult(player)
            if (cult != null) {
                SyncCultPacket.syncToPlayer(player, cult)
            }
        }
    }

    @SubscribeEvent
    fun saveCultForRespawn(e: PlayerEvent.Clone) {
        if (!e.isWasDeath) return
        CultMemberManager.getCult(e.original)?.let {
            CultMemberManager.joinCult(e.entity as ServerPlayer, it)
        }
    }

    @SubscribeEvent
    fun revengePlayerByFollowers(event: LivingDamageEvent.Post) {
        val entity = event.entity
        val attacker = event.source.entity ?: return
        if (entity is Player && attacker is LivingEntity) {
            val playerCult = CultMemberManager.getCult(entity)?.id ?: return
            val level = entity.level()
            val radius = 20.0
            val aabb = AABB(
                entity.x - radius, entity.y - radius, entity.z - radius,
                entity.x + radius, entity.y + radius, entity.z + radius
            )
            val followers = level.getEntitiesOfClass(CultFollowerEntity::class.java, aabb) {
                CultMemberManager.getCult(it)?.id == playerCult
            }
            followers.forEach { follower ->
                follower.target = attacker
                follower.persistentAngerTarget = attacker.uuid
                follower.startPersistentAngerTimer()
            }
        }
    }
}