package net.haremal.ritualsapi.api.debug

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.rituals.RitualSigilMatcher
import net.haremal.ritualsapi.api.rituals.RitualSigilMatcher.makeSigil
import net.haremal.ritualsapi.events.DebugCGEvents
import net.haremal.ritualsapi.network.SyncDebugBoxesPacket
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.UUID

class DebugWand(properties: Properties) : Item(properties) {
    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (!level.isClientSide || entity !is Player) return

        val uuid = entity.uuid
        val hasWand = isSelected && (entity.mainHandItem === stack || entity.offhandItem === stack)

        if (hasWand) {
            DebugWandTracker.playersWithDebug.add(uuid)
        } else {
            DebugWandTracker.playersWithDebug.remove(uuid)
        }

    }

    object DebugWandTracker {
        val playersWithDebug = mutableSetOf<UUID>()

        fun isDebugging(player: Player): Boolean {
            return player.uuid in playersWithDebug
        }
    }
}