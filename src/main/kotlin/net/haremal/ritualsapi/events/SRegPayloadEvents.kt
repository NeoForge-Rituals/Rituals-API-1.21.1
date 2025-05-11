package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.network.PayloadHandlers.clientHandleCultPacket
import net.haremal.ritualsapi.network.PayloadHandlers.clientHandleEnergyPacket
import net.haremal.ritualsapi.network.SyncCultPacket
import net.haremal.ritualsapi.network.SyncEnergyPacket
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.registration.HandlerThread


@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.MOD)
object SRegPayloadEvents {
    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1").executesOn(HandlerThread.NETWORK)
        registrar.playBidirectional(
            SyncEnergyPacket.TYPE,
            SyncEnergyPacket.STREAM_CODEC,
            DirectionalPayloadHandler(
                ::clientHandleEnergyPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncCultPacket.TYPE,
            SyncCultPacket.STREAM_CODEC,
            DirectionalPayloadHandler(
                ::clientHandleCultPacket
            ) { _, _ -> }
        )
    }
}