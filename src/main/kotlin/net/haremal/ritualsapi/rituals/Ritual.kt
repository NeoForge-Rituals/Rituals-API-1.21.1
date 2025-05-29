package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.cults.Cult
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

abstract class Ritual(
    val id: ResourceLocation,
    val name: MutableComponent,
    val description: String,
    val altarLevel: AltarLevel,
    val ritualtype: RitualType,
    val getcult: Cult,
    val getSacrifice: EntityType<*>

) {
    enum class RitualType { SUMMONING, EMPOWERMENT, CURSE, TRANSFORMATION }
    enum class AltarLevel { MINOR, STANDARD, MAJOR, GRAND, APOCALYPTIC }
    companion object RitualRegistry {
        val rituals: MutableMap<ResourceLocation, Ritual> = mutableMapOf()

        fun register(newRitual: Ritual) {
            require(rituals.putIfAbsent(newRitual.id, newRitual) == null) {
                "Can't register a cult: ID '${newRitual.id}' is already"
            }
        }
        fun get(id: ResourceLocation): Ritual? = rituals[id]
        fun all(): Collection<Ritual> = rituals.values
        fun init() {}
    }

    fun canPerform(altarLevel: AltarLevel, ritual: Ritual): Boolean = altarLevel.ordinal >= ritual.altarLevel.ordinal

    abstract fun requirements()
    abstract fun perform(level: Level, pos: BlockPos): Boolean
}


