package net.haremal.ritualsapi.examples

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.cults.Cult
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import java.awt.Color

object Example2Cult : Cult(
    ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "stone_guys"),
    Component.literal("Stone Guys"),
    "Uga buga uga buga",
    Color(0xFFFF00), Color(0xB26562)
) {
    override fun joinReason(player: ServerPlayer): Boolean {
        return player.position().y > 300
    }

    override fun magicSourceEnergy(player: ServerPlayer): Boolean {
        return player.level().isNight
    }

    override fun cultSigilGet(): Array<IntArray> = arrayOf(
        intArrayOf(0, 0, 0, 1, 1, 1, 1, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(1, 0, 0, 1, 0, 0, 1, 0, 0, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 0, 0, 0, 0, 0, 0, 0, 0, 1),
        intArrayOf(1, 0, 0, 1, 0, 0, 1, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0),
        intArrayOf(0, 0, 0, 1, 1, 1, 1, 0, 0, 0)
    )
}