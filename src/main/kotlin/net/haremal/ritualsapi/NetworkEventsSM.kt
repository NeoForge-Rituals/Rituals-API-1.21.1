package net.haremal.ritualsapi

import net.haremal.ritualsapi.cults.CultHandlers
import net.haremal.ritualsapi.cults.EnergyHandlers
import net.haremal.ritualsapi.cults.SyncCultPacket
import net.haremal.ritualsapi.cults.SyncEnergyPacket
import net.haremal.ritualsapi.debug.DebugBoxesHandlers
import net.haremal.ritualsapi.debug.SyncDebugBoxesPacket
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.registration.HandlerThread

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.MOD)
object NetworkEventsSM {
    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1").executesOn(HandlerThread.NETWORK)
        registrar.playBidirectional(
            SyncEnergyPacket.Companion.TYPE,
            SyncEnergyPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                EnergyHandlers::clientHandleEnergyPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncCultPacket.Companion.TYPE,
            SyncCultPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                CultHandlers::clientHandleCultPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncDebugBoxesPacket.Companion.TYPE,
            SyncDebugBoxesPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                DebugBoxesHandlers::clientHandleDebugBoxes
            ) { _, _ -> }
        )
    }
}