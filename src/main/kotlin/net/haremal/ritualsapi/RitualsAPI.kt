package net.haremal.ritualsapi

import com.mojang.logging.LogUtils
import net.haremal.ritualsapi.api.cults.CultRegistry
import net.haremal.ritualsapi.debug.ExampleCult
import net.haremal.ritualsapi.debug.CultCommand
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import net.neoforged.neoforge.registries.DeferredRegister

@Mod(RitualsAPI.MODID)
class RitualsAPI {
    companion object {
        const val MODID = "ritualsapi"
        val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)
        val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MODID)
        val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)
    }

    constructor(modEventBus: IEventBus, modContainer: ModContainer) {
        modEventBus.addListener(::onCommonSetup)
        NeoForge.EVENT_BUS.register(this)

        // VANILLA
        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)

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