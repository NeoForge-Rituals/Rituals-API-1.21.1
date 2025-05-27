package net.haremal.ritualsapi.network_to_remove

import io.netty.buffer.ByteBuf
import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.cults.CultMemberManager
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor

data class SyncEnergyPacket(val cultEnergy: Int): CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<SyncEnergyPacket> = CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "sync_energy_packet"));
        val STREAM_CODEC: StreamCodec<ByteBuf, SyncEnergyPacket> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            SyncEnergyPacket::cultEnergy,
            ::SyncEnergyPacket
        )

        fun syncToPlayer(player: ServerPlayer){
            PacketDistributor.sendToPlayer(player, SyncEnergyPacket(CultMemberManager.getCult(player)?.magicEnergy ?: 0))
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    object ClientEnergyCache {
        var energy: Int = 0
    }
}
