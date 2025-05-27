package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.network_to_remove.SyncDebugBoxesPacket
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.PacketDistributor

object RitualSigilMatcher {
    val SIGILS = mutableListOf<AABB>()
    fun matchesSigil(level: Level, center: BlockPos): Boolean {
        // PLACE BOXES OF THE SIGIL ON THE ALTAR ON THE SERVER AND THE CLIENT
        val shiftedSigils = SIGILS.map { it.move(center.x + 0.5, center.y.toDouble(), center.z + 0.5) }
        level.players().filterIsInstance<ServerPlayer>().forEach { player ->
            PacketDistributor.sendToPlayer(player, SyncDebugBoxesPacket(mapOf(center to shiftedSigils)))
        }

        // CHECK IF ALL BOXES ALIGN WITH BLOOD STAIN ENTITIES
        val altarArea = AABB(-2.5, -0.2, -2.5, 2.5, 0.2, 2.5).move(center.bottomCenter)
        val bloodStains = level.getEntitiesOfClass(BloodStainEntity::class.java, altarArea)
        val aligned = shiftedSigils.all { sigilBox -> bloodStains.any { it.boundingBox.intersects(sigilBox) } }

        return aligned
    }

    fun makeSigil(sigil: Array<IntArray>) {
        val gridSize = sigil.size
        val cellSize = 0.5
        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                if (sigil[y][x] == 1) {
                    val minX = (x * cellSize) - (gridSize * cellSize / 2)
                    val minZ = (y * cellSize) - (gridSize * cellSize / 2)
                    val maxX = minX + cellSize
                    val maxZ = minZ + cellSize

                    val box = AABB(minX, -0.2, minZ, maxX, 0.2, maxZ)
                    SIGILS.add(box)
                }
            }
        }
    }
}

