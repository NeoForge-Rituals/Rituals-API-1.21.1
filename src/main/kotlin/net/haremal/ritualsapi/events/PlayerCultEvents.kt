package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.api.cults.CultMemberManager
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent

object PlayerCultEvents {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as? ServerPlayer ?: return
        // Load cult from persistent data
        CultMemberManager.getCult(player)?.let {
            CultMemberManager.joinCult(player, it)
        }
    }
}