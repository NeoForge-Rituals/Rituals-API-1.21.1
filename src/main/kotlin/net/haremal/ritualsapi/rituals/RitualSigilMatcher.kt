package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.debug.SyncDebugBoxesPacket
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.PacketDistributor

object RitualSigilMatcher {
    val SIGILS = mutableMapOf<ResourceLocation, MutableList<AABB>>()
    fun  matchesSigil(level: Level, center: BlockPos): Boolean {
        val boxesByCultId: Map<ResourceLocation, List<AABB>> = SIGILS.mapValues { (_, boxes) ->
            boxes.map { it.move(center.x + 0.5, center.y.toDouble(), center.z + 0.5) }
        }

        val packetData = mapOf(center to boxesByCultId)

        level.players().filterIsInstance<ServerPlayer>().forEach { player ->
            PacketDistributor.sendToPlayer(player, SyncDebugBoxesPacket(packetData))
        }

        val altarArea = AABB(-2.5, -0.2, -2.5, 2.5, 0.2, 2.5).move(center.bottomCenter)
        val bloodStains = level.getEntitiesOfClass(BloodStainEntity::class.java, altarArea)

        val aligned = boxesByCultId.any { (_, sigilBoxes) ->
            sigilBoxes.all { box -> bloodStains.any { it.boundingBox.intersects(box) } }
        }

        return aligned
    }


    fun makeSigil(cult: Cult, sigil: Array<IntArray>) {
        val gridSize = sigil.size
        val cellSize = 0.5
        val boxes = mutableListOf<AABB>()

        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                if (sigil[y][x] == 1) {
                    val minX = (x * cellSize) - (gridSize * cellSize / 2)
                    val minZ = (y * cellSize) - (gridSize * cellSize / 2)
                    val maxX = minX + cellSize
                    val maxZ = minZ + cellSize
                    boxes.add(AABB(minX, -0.2, minZ, maxX, 0.2, maxZ))
                }
            }
        }
        SIGILS[cult.id] = boxes
    }
}

