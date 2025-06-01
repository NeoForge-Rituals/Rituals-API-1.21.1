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
    abstract fun requirementsAreMet(level: Level, pos: BlockPos): Boolean
    abstract fun result(level: Level, pos: BlockPos)
}


