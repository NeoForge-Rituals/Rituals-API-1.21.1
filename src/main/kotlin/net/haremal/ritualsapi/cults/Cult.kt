package net.haremal.ritualsapi.cults

import net.haremal.ritualsapi.network_to_remove.SyncEnergyPacket
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import java.awt.Color

abstract class Cult (
    val id: ResourceLocation,
    val name: MutableComponent,
    val description: String,
    val maincolor: Color,
    val seccolor: Color
) {
    private var lastSyncedEnergy = -1
    var magicEnergy: Int = 0

    // Default
    abstract fun onJoin(player: ServerPlayer)
    open fun onTick(world: ServerLevel) {
        world.players().forEach { player ->
            if (!player.level().isClientSide && CultMemberManager.getCult(player)?.id == id) {
                magicSourceEnergy(player)
                if (magicEnergy != lastSyncedEnergy) {
                    lastSyncedEnergy = magicEnergy
                    SyncEnergyPacket.syncToPlayer(player)
                }
            }
        }
    }

    // Variables
    abstract fun cultSigilGet(): Array<IntArray>
    open fun joinReason( player: ServerPlayer): Boolean = false
    open fun magicSourceEnergy(player: ServerPlayer) { callWithEnergy(player){true}}
    protected fun callWithEnergy(player: ServerPlayer, logic: () -> Boolean) { magicEnergy.takeIf { it < 100 }?.let { if (logic()) magicEnergy++ } }
}