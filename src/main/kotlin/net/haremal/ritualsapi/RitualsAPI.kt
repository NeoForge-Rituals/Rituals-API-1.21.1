package net.haremal.ritualsapi

import com.mojang.logging.LogUtils
import net.haremal.ritualsapi.api.cults.CultMemberManager
import net.haremal.ritualsapi.api.cults.CultRegistry
import net.haremal.ritualsapi.debug.ExampleCult
import net.haremal.ritualsapi.debug.CultCommand
import net.haremal.ritualsapi.events.PlayerCultEvents
import net.haremal.ritualsapi.events.WorldTickEvents
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.registries.DeferredRegister

@Mod(RitualsAPI.MODID)
class RitualsAPI {
    companion object {
        const val MODID = "ritualsapi"
        val LOGGER = LogUtils.getLogger()
        val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)
        val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MODID)
        val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)

        @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
        object ClientModEvents {
            @SubscribeEvent
            fun onClientSetup(event: FMLClientSetupEvent?) {
                // Some client setup code
                LOGGER.info("HELLO FROM CLIENT SETUP")
                LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().user.name)
            }
        }
    }

    constructor(modEventBus: IEventBus, modContainer: ModContainer) {
        modEventBus.addListener(::onCommonSetup)
        modEventBus.addListener(::addCreative)
        NeoForge.EVENT_BUS.register(this)

        // VANILLA
        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)

        // API
        CultRegistry.init()

        // EVENTS
        NeoForge.EVENT_BUS.register(PlayerCultEvents)
        NeoForge.EVENT_BUS.register(WorldTickEvents)
    }

    private fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.info("RitualsAPI (common) setup!")

        CultRegistry.register(ExampleCult)
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        // Add items to the creative tab
    }

    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {

    }

    // DEBUG COMMANDS
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        CultCommand.register(event.dispatcher)
    }
}