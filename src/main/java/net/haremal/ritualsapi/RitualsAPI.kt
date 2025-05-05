package net.haremal.ritualsapi

import com.mojang.logging.LogUtils
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.CreativeModeTab
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.event.server.ServerStartingEvent
import net.neoforged.neoforge.registries.DeferredRegister
import java.sql.DriverManager.println
import java.util.logging.Logger

@Mod(RitualsAPI.MODID)
class RitualsAPI(modEventBus: net.neoforged.bus.api.IEventBus, modContainer: ModContainer) {
    companion object {
        const val MODID = "ritualsapi"
        private val LOGGER: Logger = LogUtils.getLogger() as Logger

        val BLOCKS: DeferredRegister<*> = DeferredRegister.createBlocks(MODID)
        val ITEMS: DeferredRegister<*> = DeferredRegister.createItems(MODID)
        val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)
    }

    init {
        modEventBus.addListener(::onCommonSetup)
        modEventBus.addListener(::addCreative)

        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        CREATIVE_MODE_TABS.register(modEventBus)

        NeoForge.EVENT_BUS.register(this)
    }

    fun onCommonSetup(event: FMLCommonSetupEvent) {
        println("RitualsAPI (common) setup!")
    }

    private fun addCreative(event: BuildCreativeModeTabContentsEvent) {
        // Add items to creative tab here
    }


    @SubscribeEvent
    fun onServerStarting(event: ServerStartingEvent) {
        // Server starting logic here
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
    object ClientModEvents {
        @SubscribeEvent
        fun onClientSetup(event: FMLClientSetupEvent) {
            // Client setup logic here
        }
    }
}