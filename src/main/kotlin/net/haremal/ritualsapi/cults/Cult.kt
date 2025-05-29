package net.haremal.ritualsapi.cults

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
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

    private var lastSyncedEnergy = -1
    var magicEnergy: Int = 0

    open fun onTick(world: ServerLevel) {
        world.players().forEach { player ->
            if (!player.level().isClientSide && CultMemberManager.getCult(player)?.id == id) {
                if(magicSourceEnergy(player)) magicEnergy.takeIf { it < 100 }?.let {  magicEnergy++ }
                if (magicEnergy != lastSyncedEnergy) {
                    lastSyncedEnergy = magicEnergy
                    SyncEnergyPacket.syncToPlayer(player)
                }
            }
        }
    }

    abstract fun cultSigilGet(): Array<IntArray>
    open fun cultFigureGet(): Entity? = null // TODO: LATER
    open fun joinReason( player: ServerPlayer): Boolean = false
    open fun magicSourceEnergy(player: ServerPlayer): Boolean = false
}

