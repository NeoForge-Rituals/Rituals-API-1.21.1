package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.api.cults.CultRegistry
import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.tick.LevelTickEvent

object WorldTickEvents {
    @SubscribeEvent
    fun onServerTick(event: LevelTickEvent.Post) {
        val level = event.level
        if (level is ServerLevel) {
            for (cult in CultRegistry.all()) {
                cult.onTick(level)
            }
        }
    }
}