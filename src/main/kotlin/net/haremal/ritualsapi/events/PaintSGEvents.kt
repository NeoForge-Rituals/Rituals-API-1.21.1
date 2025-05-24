package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.LevelTickEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME)
object PaintSGEvents {

    // TODO: ADD BLOOD SPAWNING

    @SubscribeEvent
    fun onServerTick(event: LevelTickEvent.Post) {

    }
}