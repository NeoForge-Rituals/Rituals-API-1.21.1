package net.haremal.ritualsapi.api.entity

import net.minecraft.util.TimeUtil
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.NeutralMob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

class CultFollowerEntity(type: EntityType<CultFollowerEntity>, level: Level) : PathfinderMob(type, level), NeutralMob {
    companion object { private val PERSISTENT_ANGER_TIME: UniformInt = TimeUtil.rangeOfSeconds(30, 60) }
    private var _remainingPersistentAngerTime = 0
    private var _persistentAngerTarget: UUID? = null


    override fun registerGoals() {
        goalSelector.addGoal(1, FloatGoal(this))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.5, true))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(5, RandomLookAroundGoal(this))
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
        targetSelector.addGoal(3, ResetUniversalAngerTargetGoal(this, false))

    }

    override fun getRemainingPersistentAngerTime(): Int = this._remainingPersistentAngerTime
    override fun setRemainingPersistentAngerTime(remainingPersistentAngerTime: Int) {
        this._remainingPersistentAngerTime = remainingPersistentAngerTime
    }

    override fun getPersistentAngerTarget(): UUID? = this._persistentAngerTarget
    override fun setPersistentAngerTarget(persistentAngerTarget: UUID?) {
        this._persistentAngerTarget = persistentAngerTarget
    }

    override fun startPersistentAngerTimer() {
        this._remainingPersistentAngerTime = PERSISTENT_ANGER_TIME.sample(this.random)
    }
}