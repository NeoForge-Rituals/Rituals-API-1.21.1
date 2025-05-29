package net.haremal.ritualsapi.examples

import net.haremal.ritualsapi.cults.Cult
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import java.awt.Color

object ExampleCult : Cult(
    ResourceLocation.tryParse("ritualsapi:obsidian_dawn") ?: error("Invalid ResourceLocation"),
    Component.literal("Obsidian Dawn"),
    "Devoted to void rituals",
    Color(0x000020), Color(0x626262)
) {
    override fun joinReason(player: ServerPlayer): Boolean {
        return player.inventory.contains(Items.STONE.defaultInstance)
    }

    override fun magicSourceEnergy(player: ServerPlayer): Boolean {
        val inv = player.inventory
        val obsidianStack: ItemStack? = inv.items.find { it.item == Items.OBSIDIAN }
        if (obsidianStack == null) return false
        obsidianStack.shrink(1)
        return true
    }

    override fun cultSigilGet(): Array<IntArray> = arrayOf(
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 1)
    )
}