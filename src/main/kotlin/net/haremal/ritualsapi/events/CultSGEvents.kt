package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.cults.CultMemberManager
import net.haremal.ritualsapi.network.SyncCultPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME)
object CultSGEvents {
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
}