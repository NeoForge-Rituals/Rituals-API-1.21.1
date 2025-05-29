package net.haremal.ritualsapi.debug

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.debug.SyncDebugBoxesPacket.DebugBoxesCache
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.handling.IPayloadContext
import kotlin.collections.iterator
import kotlin.collections.plus

data class SyncDebugBoxesPacket(val boxesByPos: Map<BlockPos, Map<ResourceLocation, List<AABB>>>) : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SyncDebugBoxesPacket>(
            ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "debug_boxes")
        )

        private val BLOCKPOS_CODEC = StreamCodec.of<FriendlyByteBuf, BlockPos>(
            { buf, pos ->
                buf.writeInt(pos.x)
                buf.writeInt(pos.y)
                buf.writeInt(pos.z)
            },
            { buf ->
                BlockPos(buf.readInt(), buf.readInt(), buf.readInt())
            }
        )

        private val CULT_ID_CODEC = StreamCodec.of<FriendlyByteBuf, ResourceLocation>(
            { buf, id -> buf.writeResourceLocation(id) },
            { buf -> buf.readResourceLocation() }
        )

        private val BOX_CODEC = StreamCodec.of<FriendlyByteBuf, AABB>(
            { buf, box ->
                buf.writeDouble(box.minX)
                buf.writeDouble(box.minY)
                buf.writeDouble(box.minZ)
                buf.writeDouble(box.maxX)
                buf.writeDouble(box.maxY)
                buf.writeDouble(box.maxZ)
            },
            { buf ->
                AABB(
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble()
                )
            }
        )

        val STREAM_CODEC: StreamCodec<FriendlyByteBuf, SyncDebugBoxesPacket> = StreamCodec.of(
            { buf, packet ->
                buf.writeInt(packet.boxesByPos.size)
                for ((pos, cultMap) in packet.boxesByPos) {
                    BLOCKPOS_CODEC.encode(buf, pos)
                    buf.writeInt(cultMap.size)
                    for ((cultId, boxes) in cultMap) {
                        CULT_ID_CODEC.encode(buf, cultId)
                        buf.writeInt(boxes.size)
                        boxes.forEach { BOX_CODEC.encode(buf, it) }
                    }
                }
            },
            { buf ->
                val outerSize = buf.readInt()
                val result = mutableMapOf<BlockPos, Map<ResourceLocation, List<AABB>>>()
                repeat(outerSize) {
                    val pos = BLOCKPOS_CODEC.decode(buf)
                    val cultMapSize = buf.readInt()
                    val cultMap = mutableMapOf<ResourceLocation, List<AABB>>()
                    repeat(cultMapSize) {
                        val cultId = CULT_ID_CODEC.decode(buf)
                        val boxCount = buf.readInt()
                        val boxes = MutableList(boxCount) { BOX_CODEC.decode(buf) }
                        cultMap[cultId] = boxes
                    }
                    result[pos] = cultMap
                }
                SyncDebugBoxesPacket(result)
            }
        )

    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    object DebugBoxesCache {
        var boxesByPos: Map<BlockPos, Map<ResourceLocation, List<AABB>>> = mapOf()
    }
}

object DebugBoxesHandlers {
    fun clientHandleDebugBoxes(data: SyncDebugBoxesPacket, context: IPayloadContext) {
        context.enqueueWork {
            DebugBoxesCache.boxesByPos += data.boxesByPos
        }
    }
}