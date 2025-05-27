package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.cults.CultRegistry
import net.haremal.ritualsapi.api.debug.ExampleCult
import net.haremal.ritualsapi.api.rituals.RitualSigilMatcher.makeSigil
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.MOD)

object CultSMEvents {
    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            CultRegistry.cults.forEach { makeSigil(it.value.cultSigilGet())}
        }

        // EXAMPLE
        CultRegistry.register(ExampleCult)
    }

}