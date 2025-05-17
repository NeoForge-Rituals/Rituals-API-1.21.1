package net.haremal.ritualsapi.mod.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CultFollowerEntity(type: EntityType<out PathfinderMob>, level: Level) : PathfinderMob(type, level) {
    override fun registerGoals() {
        goalSelector.addGoal(1, FloatGoal(this))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(4, RandomLookAroundGoal(this))
    }
}