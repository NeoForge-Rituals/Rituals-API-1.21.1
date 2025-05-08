package net.haremal.ritualsapi.api.cults

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

object CultMemberManager {
    private const val CULT_ID_KEY = "RitualsAPI.CultId"

    fun joinCult(player: ServerPlayer, cult: Cult) {
        player.persistentData.putString(CULT_ID_KEY, cult.id.toString())
    }

    fun leaveCult(player: ServerPlayer) {
        player.persistentData.remove(CULT_ID_KEY) // Optional: clear data on leave
    }

    fun getCult(player: ServerPlayer): Cult? {
        val idString = player.persistentData.getString(CULT_ID_KEY)
        if (idString.isNullOrBlank()) return null
        val id = ResourceLocation.tryParse(idString) ?: return null
        return CultRegistry.get(id)
    }
}