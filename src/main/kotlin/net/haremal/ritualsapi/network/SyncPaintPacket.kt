package net.haremal.ritualsapi.network

import io.netty.buffer.ByteBuf
import net.haremal.ritualsapi.RitualsAPI
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor

data class SyncPaintPacket(val pos: BlockPos, val pixels: IntArray) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SyncPaintPacket>(
            ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "sync_paint_packet")
        )

        private val BLOCKPOS_CODEC: StreamCodec<ByteBuf, BlockPos> = StreamCodec.of(
            { buf, pos ->
                buf.writeInt(pos.x)
                buf.writeInt(pos.y)
                buf.writeInt(pos.z)
            },
            { buf -> BlockPos(buf.readInt(), buf.readInt(), buf.readInt()) }
        )

        private val INT_ARRAY_CODEC: StreamCodec<ByteBuf, IntArray> = StreamCodec.of(
            { buf, array ->
                buf.writeInt(array.size)
                for (v in array) buf.writeInt(v)
            },
            { buf ->
                val size = buf.readInt()
                IntArray(size) { buf.readInt() }
            }
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, SyncPaintPacket> = StreamCodec.composite(
            BLOCKPOS_CODEC, SyncPaintPacket::pos,
            INT_ARRAY_CODEC, SyncPaintPacket::pixels,
            ::SyncPaintPacket
        )

        fun syncToTrackingPlayers(level: ServerLevel, pos: BlockPos, pixels: IntArray) {
            val chunk = level.getChunkAt(pos)
            val players = level.chunkSource.chunkMap.getPlayers(chunk.pos, false)
            for (player in players) {
                PacketDistributor.sendToPlayer(player, SyncPaintPacket(pos, pixels))
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    object ClientPaintCache {
        val canvas: MutableMap<BlockPos, IntArray> = mutableMapOf()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SyncPaintPacket) return false
        return pos == other.pos && pixels.contentEquals(other.pixels)
    }

    override fun hashCode(): Int {
        var result = pos.hashCode()
        result = 31 * result + pixels.contentHashCode()
        return result
    }
}
