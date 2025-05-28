package net.haremal.ritualsapi

import net.haremal.ritualsapi.debug.DebugCommands
import net.haremal.ritualsapi.ModRegistries.BLOCKS
import net.haremal.ritualsapi.ModRegistries.BLOCK_ENTITY_TYPES
import net.haremal.ritualsapi.ModRegistries.BLOCK_ITEMS
import net.haremal.ritualsapi.ModRegistries.CREATIVE_MODE_TABS
import net.haremal.ritualsapi.ModRegistries.ENTITY_TYPES
import net.haremal.ritualsapi.ModRegistries.ITEMS
import net.haremal.ritualsapi.cults.Cult
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
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
        Cult.init()
    }

    // DEBUG COMMANDS
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        DebugCommands.register(event.dispatcher)
    }
}