package net.haremal.ritualsapi.cults

import net.haremal.ritualsapi.RitualsAPI
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME)
object CultEventsSG {
    @SubscribeEvent
    fun onServerTick(event: LevelTickEvent.Post) {
        val level = event.level
        if (level is ServerLevel) CultMemberManager.tickCult(level)
    }
    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            val cult = CultMemberManager.getCult(player)
            if (cult != null) {
                SyncCultPacket.syncToPlayer(player, cult)
            }
        }
    }

    @SubscribeEvent
    fun onClone(e: PlayerEvent.Clone) {
        if (!e.isWasDeath) return
        CultMemberManager.getCult(e.original)?.let {
            CultMemberManager.joinCult(e.entity as ServerPlayer, it)
        }
    }
}