package net.haremal.ritualsapi.debug

import net.haremal.ritualsapi.RitualsAPI
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

data class SyncDebugBoxesPacket(val boxesByPos: Map<BlockPos, List<AABB>>) : CustomPacketPayload {

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
                // Write how many positions
                buf.writeInt(packet.boxesByPos.size)
                for ((pos, boxes) in packet.boxesByPos) {
                    BLOCKPOS_CODEC.encode(buf, pos)
                    // Write how many boxes for this pos
                    buf.writeInt(boxes.size)
                    boxes.forEach { BOX_CODEC.encode(buf, it) }
                }
            },
            { buf ->
                val size = buf.readInt()
                val map = mutableMapOf<BlockPos, List<AABB>>()
                repeat(size) {
                    val pos = BLOCKPOS_CODEC.decode(buf)
                    val boxCount = buf.readInt()
                    val boxes = mutableListOf<AABB>()
                    repeat(boxCount) {
                        boxes.add(BOX_CODEC.decode(buf))
                    }
                    map[pos] = boxes
                }
                SyncDebugBoxesPacket(map)
            }
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    object DebugBoxesCache {
        var boxesByPos: Map<BlockPos, List<AABB>> = mapOf()
    }
}

object DebugBoxesHandlers {
    fun clientHandleDebugBoxes(data: SyncDebugBoxesPacket, context: IPayloadContext) {
        context.enqueueWork {
            val current = DebugBoxesCache.boxesByPos.toMutableMap()

            for ((pos, boxes) in data.boxesByPos) {
                current[pos] = (current[pos] ?: emptyList()) + boxes
            }

            DebugBoxesCache.boxesByPos = current
        }
    }
}