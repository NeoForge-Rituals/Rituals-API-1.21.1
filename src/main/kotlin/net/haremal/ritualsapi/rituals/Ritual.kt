package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.debug.SyncDebugBoxesPacket
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.PacketDistributor

abstract class Ritual(
    val id: ResourceLocation,
    val name: MutableComponent,
    val description: String,
    val altarLevel: AltarBlock.AltarLevel,
    val ritualtype: AltarBlock.RitualType,
    val cult: Cult,
    val sacrifice: EntityType<*>
) {
    companion object RitualRegistry {
        val rituals: MutableMap<ResourceLocation, Ritual> = mutableMapOf()

        fun register(newRitual: Ritual) {
            require(rituals.putIfAbsent(newRitual.id, newRitual) == null) {
                "Can't register a cult: ID '${newRitual.id}' is already registered"
            }
        }
        fun get(id: ResourceLocation): Ritual? = rituals[id]
        fun all(): Collection<Ritual> = rituals.values
        fun init() {}
    }

    object RitualSigilMatcher {
        val SIGILS = mutableMapOf<ResourceLocation, MutableList<AABB>>()
        fun getMatchingSigil(level: Level, center: BlockPos): Map.Entry<ResourceLocation, List<AABB?>>? {
            val movedSigils = SIGILS.mapValues { (_, boxes) ->
                boxes.map { it.move(center.x + 0.5, center.y.toDouble(), center.z + 0.5) }
            }

            val packetData = mapOf(center to movedSigils)
            level.players().filterIsInstance<ServerPlayer>().forEach { player ->
                PacketDistributor.sendToPlayer(player, SyncDebugBoxesPacket(packetData))
            }

            val altarArea = AABB(-2.5, -0.2, -2.5, 2.5, 0.2, 2.5).move(center.bottomCenter)
            val bloodStains = level.getEntitiesOfClass(BloodStainEntity::class.java, altarArea)

            return movedSigils.entries.firstOrNull { (_, sigilBoxes) ->
                sigilBoxes.all { box -> bloodStains.any { it.boundingBox.intersects(box) } }
            }
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

    abstract fun requirementsAreMet(level: Level, pos: BlockPos): Boolean
    abstract fun perform(level: Level, pos: BlockPos)
}


