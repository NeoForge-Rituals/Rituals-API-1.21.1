package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.network.PayloadHandlers
import net.haremal.ritualsapi.network.SyncCultPacket
import net.haremal.ritualsapi.network.SyncEnergyPacket
import net.haremal.ritualsapi.network.SyncPaintPacket
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.registration.HandlerThread

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.MOD)
object NetworkSMEvents {
    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1").executesOn(HandlerThread.NETWORK)
        registrar.playBidirectional(
            SyncEnergyPacket.Companion.TYPE,
            SyncEnergyPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                PayloadHandlers::clientHandleEnergyPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncCultPacket.Companion.TYPE,
            SyncCultPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                PayloadHandlers::clientHandleCultPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncPaintPacket.Companion.TYPE,
            SyncPaintPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                PayloadHandlers::clientHandlePaintPacket
            ) { _, _ -> }
        )
    }
}