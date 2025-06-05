package net.haremal.ritualsapi.examples

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.rituals.AltarBlock
import net.haremal.ritualsapi.rituals.Ritual
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

object ExampleRitual : Ritual(
    ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "example_ritual"),
    Component.literal("Example Ritual"),
    "Set raining",
    AltarBlock.AltarLevel.STANDARD,
    ExampleCult,
    EntityType.BLAZE
) {
    override fun requirementsAreMet(level: Level, pos: BlockPos): Boolean {
        return level.isNight
    }

    override fun result(level: Level, pos: BlockPos) {
        level.setRainLevel(1f)
    }
}