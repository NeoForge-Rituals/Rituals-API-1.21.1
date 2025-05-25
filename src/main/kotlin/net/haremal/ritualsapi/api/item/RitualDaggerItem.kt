package net.haremal.ritualsapi.api.item

import net.haremal.ritualsapi.api.ModRegistries
import net.haremal.ritualsapi.api.entity.BloodStainEntity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.level.Level

class RitualDaggerItem(properties: Properties) : SwordItem(Tiers.IRON, properties) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        BloodStainEntity.timer.put(player, 200)
        player.hurt(level.damageSources().playerAttack(player), 4f)
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }
}