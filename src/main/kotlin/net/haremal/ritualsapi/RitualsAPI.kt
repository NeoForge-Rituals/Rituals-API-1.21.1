package net.haremal.ritualsapi

import net.haremal.ritualsapi.api.cults.CultRegistry
import net.haremal.ritualsapi.debug.ExampleCult
import net.haremal.ritualsapi.debug.CultCommand
import net.haremal.ritualsapi.api.ModRegistries.BLOCKS
import net.haremal.ritualsapi.api.ModRegistries.CREATIVE_MODE_TABS
import net.haremal.ritualsapi.api.ModRegistries.ENTITY_TYPES
import net.haremal.ritualsapi.api.ModRegistries.ITEMS
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent

@Mod(RitualsAPI.MODID)
class RitualsAPI {
    companion object {
        const val MODID = "ritualsapi"
    }

    constructor(modEventBus: IEventBus, modContainer: ModContainer) {
        modEventBus.addListener(::onCommonSetup)
        NeoForge.EVENT_BUS.register(this)

        // MOD
        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)
        ENTITY_TYPES.register(modEventBus)

        // API
        CultRegistry.init()
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        CultRegistry.register(ExampleCult)
    }

    // DEBUG COMMANDS
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        CultCommand.register(event.dispatcher)
    }
}