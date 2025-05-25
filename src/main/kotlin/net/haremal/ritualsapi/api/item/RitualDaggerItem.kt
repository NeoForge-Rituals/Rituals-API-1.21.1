package net.haremal.ritualsapi.api.item

import net.haremal.ritualsapi.api.entity.BloodStainEntity
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.level.Level

class RitualDaggerItem(properties: Properties) : SwordItem(Tiers.IRON, properties) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (player.cooldowns.isOnCooldown(this)) return InteractionResultHolder.pass(player.getItemInHand(usedHand))

        player.cooldowns.addCooldown(this, 20)

        player.addEffect(MobEffectInstance(MobEffects.CONFUSION, 200, 0, false, false, false))
        player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4, false, false, false))

        BloodStainEntity.bloodDrop.put(player, BloodStainEntity.Companion.BloodDropData(200, 0))
        player.hurt(level.damageSources().generic(), 4f)
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }
}