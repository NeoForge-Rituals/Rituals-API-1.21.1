package net.haremal.ritualsapi.cults

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity

object CultMemberManager {
    private const val CULT_ID_KEY = "RitualsAPI.CultId"

    fun joinCult(entity: LivingEntity, cult: Cult) {
        entity.persistentData.putString(CULT_ID_KEY, cult.id.toString())
        if(entity is ServerPlayer) SyncCultPacket.syncToPlayer(entity, cult)
    }

    fun leaveCult(entity: ServerPlayer) {
        entity.persistentData.remove(CULT_ID_KEY)
        SyncCultPacket.syncToPlayer(entity, null)
    }

    fun tickCult(level: ServerLevel) {
        Cult.all().forEach { cult ->
            cult.onTick(level)
            level.players().forEach { player ->
                if (getCult(player) == null && cult.joinReason(player)) {
                    joinCult(player, cult)
                    player.sendSystemMessage(Component.literal("You have joined the cult: ${cult.name.string}"))
                }
            }
        }
    }

    // SERVER SIDE ONLY
    fun getCult(entity: LivingEntity): Cult? {
        val idString = entity.persistentData.getString(CULT_ID_KEY)
        if (idString.isNullOrBlank()) return null
        val id = ResourceLocation.tryParse(idString) ?: return null
        return Cult.get(id)
    }

    // CLIENT SIDE ONLY
    fun getCult(): Cult? {
        val cultId = SyncCultPacket.ClientCultCache.id
        if (cultId == null) return null
        return Cult.get(cultId)
    }
}