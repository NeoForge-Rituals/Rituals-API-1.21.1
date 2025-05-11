package net.haremal.ritualsapi.debug

import net.haremal.ritualsapi.api.cults.Cult
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.awt.Color

object ExampleCult : Cult(
    ResourceLocation.tryParse("ritualsapi:obsidian_dawn") ?: error("Invalid ResourceLocation"),
    Component.literal("Obsidian Dawn"),
    "Devoted to void rituals",
    Color(0x000000), // Black color as example
    "Stone Amount",
    listOf(ResourceLocation.tryParse("ritualsapi:spawns/obsidian_ritualist") ?: error("Invalid ResourceLocation"))
) {

    override fun onJoin(player: ServerPlayer) {
        player.sendSystemMessage(Component.literal("The void accepts you."))
    }


    override fun joinReason(player: ServerPlayer): Boolean {
        return player.inventory.contains(Items.STONE.defaultInstance)
    }

    override fun magicSourceEnergy(player: ServerPlayer): Int {
        return player.inventory.items.getOrNull(0)?.count ?: 0
    }
}