package net.haremal.ritualsapi.api.cults

import net.haremal.ritualsapi.network.SyncCultPacket
import net.minecraft.client.player.LocalPlayer
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor

object CultMemberManager {
    private const val CULT_ID_KEY = "RitualsAPI.CultId"

    fun joinCult(player: ServerPlayer, cult: Cult) {
        player.persistentData.putString(CULT_ID_KEY, cult.id.toString())
        PacketDistributor.sendToPlayer(player, SyncCultPacket(cult.id))
    }

    fun leaveCult(player: ServerPlayer) {
        player.persistentData.remove(CULT_ID_KEY) // Optional: clear data on leave
        PacketDistributor.sendToPlayer(player, SyncCultPacket(ResourceLocation.parse("")))
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