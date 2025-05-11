package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.cults.CultRegistry
import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.LevelTickEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME)
object SGameTickEvents {
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