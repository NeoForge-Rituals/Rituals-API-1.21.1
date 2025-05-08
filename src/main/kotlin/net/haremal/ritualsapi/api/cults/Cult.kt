package net.haremal.ritualsapi.api.cults

import net.haremal.ritualsapi.api.cults.CultMemberManager.getCult
import net.haremal.ritualsapi.api.cults.CultMemberManager.joinCult
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import java.awt.Color

abstract class Cult (
    val id: ResourceLocation,
    val name: MutableComponent,
    val description: String,
    val color: Color,
    //TODO: MAGIC SOURCE LIKE IN AVATAR AANG. EMPOWERMENT SYSTEM
    val magicSource: String,
    val followersSpawns: List<ResourceLocation> = emptyList()
) {
    abstract  fun onJoin(player: ServerPlayer)
    open fun onTick(world: ServerLevel) {
        world.players().forEach { player ->
            joinThisCult(player)
        }
    }
    open fun joinReason(player: ServerPlayer): Boolean = false
    private fun joinThisCult(player: ServerPlayer) {
        if (getCult(player) == null && joinReason(player)) {
            joinCult(player, this)
            onJoin(player)
            player.sendSystemMessage(Component.literal("You have joined the cult: ${name.string}"))
        }
    }
}