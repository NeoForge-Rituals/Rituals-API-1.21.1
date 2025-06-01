package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.cults.CultMemberManager.getCult
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.level.Level

class RitualDaggerItem(properties: Properties) : SwordItem(Tiers.IRON, properties) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        if (player.cooldowns.isOnCooldown(this)) return InteractionResultHolder.pass(player.getItemInHand(usedHand))
        player.cooldowns.addCooldown(this, 20)

        player.addEffect(MobEffectInstance(MobEffects.CONFUSION, 250, 4, false, false, false))
        player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4, false, false, false))

        BloodStainEntity.bloodDrop.put(player, BloodStainEntity.Companion.BloodDropData(200, 0))
        player.hurt(level.damageSources().generic(), 4f)
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }

    override fun hurtEnemy(stack: ItemStack, target: LivingEntity, attacker: LivingEntity): Boolean {
        val altarBE = attacker.level().getBlockEntity(target.blockPosition().below()) as? AltarBlock.AltarBlockEntity?: return super.hurtEnemy(stack, target, attacker)
        if (isPerformable(altarBE, attacker, target)) {
            altarBE.shouldPerform = true
            altarBE.setChanged()
            target.hurt(attacker.level().damageSources().magic(), Float.MAX_VALUE)
        }

        return super.hurtEnemy(stack, target, attacker)
    }


    fun isPerformable(altar: AltarBlock.AltarBlockEntity, attacker: LivingEntity, target: LivingEntity): Boolean {
        Cult.Sigil.getMatchingSigil(attacker.level(), altar.blockPos)?: return false
        altar.findMatchingRitual(attacker.level(), altar.blockPos, altar.sacrificedType, altar.cultId?: return false)?: return false
        return altar.cultId == getCult(attacker)?.id
    }
}