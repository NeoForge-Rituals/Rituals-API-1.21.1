package net.haremal.ritualsapi.network_to_remove

import io.netty.buffer.ByteBuf
import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.cults.Cult
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import java.nio.charset.StandardCharsets

data class SyncCultPacket(val cultId: ResourceLocation?): CustomPacketPayload {
    companion object {
        val NO_CULT: ResourceLocation = ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "no_cult")
        val RESOURCE_LOCATION_CODEC: StreamCodec<ByteBuf, ResourceLocation> =
            StreamCodec.of(
                { buf, id -> buf.writeCharSequence(id.toString(), StandardCharsets.UTF_8) }, // Write using UTF-8
                { buf -> ResourceLocation.tryParse(buf.readCharSequence(buf.readableBytes(), StandardCharsets.UTF_8).toString()) ?: throw IllegalArgumentException("Invalid ResourceLocation") } // Read using UTF-8
            )

        val TYPE: CustomPacketPayload.Type<SyncCultPacket> = CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "sync_cult_packet"));
        val STREAM_CODEC: StreamCodec<ByteBuf, SyncCultPacket> = StreamCodec.composite(
            RESOURCE_LOCATION_CODEC,
            SyncCultPacket::cultId,
            ::SyncCultPacket
        )

        fun syncToPlayer(player: ServerPlayer, cult: Cult?){
            PacketDistributor.sendToPlayer(player, SyncCultPacket(cult?.id ?: NO_CULT))
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    object ClientCultCache {
        var id: ResourceLocation? = null
    }
}
