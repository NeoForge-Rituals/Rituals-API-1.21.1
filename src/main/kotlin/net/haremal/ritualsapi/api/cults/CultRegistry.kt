package net.haremal.ritualsapi.api.cults

import net.minecraft.resources.ResourceLocation

object CultRegistry {
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

