package net.haremal.ritualsapi.examples

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.rituals.Ritual
import net.haremal.ritualsapi.rituals.Ritual.AltarLevel
import net.haremal.ritualsapi.rituals.Ritual.RitualType
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level


object ExampleRitual : Ritual(
    ResourceLocation.fromNamespaceAndPath(RitualsAPI.MODID, "example_ritual"),
    Component.literal("Example Ritual"),
    "Sets an nearby forest on fire",
    AltarLevel.GRAND,
    RitualType.SUMMONING,
    ExampleCult,
    EntityType.BLAZE
) {
    override fun requirements() {
        TODO("Not yet implemented")
    }

    override fun perform(level: Level, pos: BlockPos): Boolean {
        TODO("Not yet implemented")
    }
}