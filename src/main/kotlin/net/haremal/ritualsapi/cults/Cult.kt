package net.haremal.ritualsapi.cults

import net.haremal.ritualsapi.debug.SyncDebugBoxesPacket
import net.haremal.ritualsapi.rituals.BloodStainEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.network.PacketDistributor
import java.awt.Color

abstract class Cult (
    val id: ResourceLocation,
    val name: MutableComponent,
    val description: String,
    val maincolor: Color,
    val seccolor: Color
) {
    companion object CultRegistry {
        val cults: MutableMap<ResourceLocation, Cult> = mutableMapOf()

        fun register(newCult: Cult) {
            require(cults.putIfAbsent(newCult.id, newCult) == null) {
                "Can't register a cult: ID '${newCult.id}' is already"
            }
        }
        fun get(id: ResourceLocation): Cult? = cults[id]
        fun all(): Collection<Cult> = cults.values
        fun init() {}
    }
    object Sigil {
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



    private var lastSyncedEnergy = -1
    var magicEnergy: Int = 0

    open fun onTick(world: ServerLevel) {
        world.players().forEach { player ->
            if (!player.level().isClientSide && CultMemberManager.getCult(player)?.id == id) {
                magicEnergy.takeIf { it < 100 }?.also { if (magicSourceEnergy(player)) magicEnergy++ }
                if (magicEnergy != lastSyncedEnergy) {
                    lastSyncedEnergy = magicEnergy
                    SyncEnergyPacket.syncToPlayer(player)
                }
            }
        }
    }

    abstract fun cultSigilGet(): Array<IntArray>
    open fun joinReason( player: ServerPlayer): Boolean = false
    open fun magicSourceEnergy(player: ServerPlayer): Boolean = false
    open fun cultFigureGet(): Entity? = null // TODO: LATER
}

