package net.haremal.ritualsapi.api.item

import net.haremal.ritualsapi.api.ModRegistries
import net.haremal.ritualsapi.api.ModRegistries.BloodDataStorage.bloodPixels
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.damagesource.DamageSources
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.phys.Vec3
import kotlin.math.floor

class RitualDaggerItem(properties: Properties) : SwordItem(Tiers.IRON, properties) {
    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack?> {
        player.hurt(level.damageSources().playerAttack(player), 4f)

        val blockState = level.getBlockState(player.blockPosition().below())
        val blockBelow = player.blockPosition().below()
        val isSolidFullBlock = blockState.isSolidRender(level, blockBelow) && blockState.isCollisionShapeFullBlock(level, blockBelow)
        if(isSolidFullBlock) playerPixelPosition(player)
        return InteractionResultHolder.success(player.getItemInHand(usedHand))
    }

    private fun playerPixelPosition(player: Player) {
        val exactPlayerPosition = player.getPosition(0f)
        val blockPosition = Vec3(floor(exactPlayerPosition.x), floor(exactPlayerPosition.y), floor(exactPlayerPosition.z))

        val xPixel = ((exactPlayerPosition.x-blockPosition.x)*16).toInt().coerceAtMost(15)
        val zPixel = ((exactPlayerPosition.z-blockPosition.z)*16).toInt().coerceAtMost(15)
        val pixelPosition = Vec3(xPixel.toDouble(), exactPlayerPosition.y, zPixel.toDouble())


        bloodPixels.add(
            ModRegistries.BloodDataStorage.BloodPixelData(
                blockPosition,
                pixelPosition,
                pickRandomBloodColor()
            )
        )
    }

    fun pickRandomBloodColor(): Triple<Int, Int, Int> {
        val bloodColors = listOf(
            Triple(126, 0, 0),    // fresh deep red
            Triple(178, 34, 34),  // bright red
            Triple(92, 0, 0),     // dark red
            Triple(75, 30, 11),   // brownish
            Triple(220, 20, 60),  // crimson
            Triple(139, 64, 0),   // rust brown
            Triple(106, 13, 173)  // purplish red
        )
        val bloodWeights = listOf(30, 40, 15, 5, 25, 5, 3)

        val totalWeight = bloodWeights.sum()
        val randomValue = (0 until totalWeight).random()
        var sum = 0
        for(i in bloodWeights.indices) {
            sum += bloodWeights[i]
            if(randomValue < sum) return bloodColors[i]
        }
        return bloodColors[0] // fallback
    }
}