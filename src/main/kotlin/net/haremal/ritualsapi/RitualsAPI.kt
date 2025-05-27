package net.haremal.ritualsapi

import net.haremal.ritualsapi.api.cults.CultRegistry
import net.haremal.ritualsapi.api.debug.ExampleCult
import net.haremal.ritualsapi.api.debug.CultCommand
import net.haremal.ritualsapi.api.ModRegistries.BLOCKS
import net.haremal.ritualsapi.api.ModRegistries.BLOCK_ENTITY_TYPES
import net.haremal.ritualsapi.api.ModRegistries.BLOCK_ITEMS
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
        NeoForge.EVENT_BUS.register(this)

        // DEFAULT
        ENTITY_TYPES.register(modEventBus)
        ITEMS.register(modEventBus)
        BLOCKS.register(modEventBus)
        BLOCK_ENTITY_TYPES.register(modEventBus)
        BLOCK_ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)

        // API
        CultRegistry.init()
    }

    // DEBUG COMMANDS
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        CultCommand.register(event.dispatcher)
    }
}