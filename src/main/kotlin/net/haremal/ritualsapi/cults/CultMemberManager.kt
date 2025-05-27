package net.haremal.ritualsapi.cults

import net.haremal.ritualsapi.network_to_remove.SyncCultPacket
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer

object CultMemberManager {
    private const val CULT_ID_KEY = "RitualsAPI.CultId"

    fun joinCult(player: ServerPlayer, cult: Cult) {
        player.persistentData.putString(CULT_ID_KEY, cult.id.toString())
        SyncCultPacket.syncToPlayer(player, cult)
    }

    fun leaveCult(player: ServerPlayer) {
        player.persistentData.remove(CULT_ID_KEY) // Optional: clear data on leave
        SyncCultPacket.syncToPlayer(player, null)
    }

    fun tickCult(level: ServerLevel) {
        CultRegistry.all().forEach { cult ->
            cult.onTick(level)
            level.players().forEach { player ->
                if (getCult(player) == null && cult.joinReason(player)) {
                    joinCult(player, cult)
                    cult.onJoin(player)
                    player.sendSystemMessage(Component.literal("You have joined the cult: ${cult.name.string}"))
                }
            }
        }
    }

    // SERVER SIDE ONLY
    fun getCult(player: ServerPlayer): Cult? {
        val idString = player.persistentData.getString(CULT_ID_KEY)
        if (idString.isNullOrBlank()) return null
        val id = ResourceLocation.tryParse(idString) ?: return null
        return CultRegistry.get(id)
    }

    // CLIENT SIDE ONLY
    fun getCult(): Cult? {
        val cultId = SyncCultPacket.ClientCultCache.id
        if (cultId == null) return null
        return CultRegistry.get(cultId)
    }
}