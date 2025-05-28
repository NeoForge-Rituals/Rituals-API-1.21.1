package net.haremal.ritualsapi.cults

import net.minecraft.util.TimeUtil
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.NeutralMob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import java.util.UUID

class CultFollowerEntity(type: EntityType<CultFollowerEntity>, level: Level) : PathfinderMob(type, level), NeutralMob {
    companion object { private val PERSISTENT_ANGER_TIME: UniformInt = TimeUtil.rangeOfSeconds(30, 60) }
    private var remainingPersistentAngerTime = 0
    private var persistentAngerTarget: UUID? = null


    override fun registerGoals() {
        goalSelector.addGoal(1, FloatGoal(this))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.5, true))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(5, RandomLookAroundGoal(this))
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, ResetUniversalAngerTargetGoal(this, false))
    }

    override fun getRemainingPersistentAngerTime() = this.remainingPersistentAngerTime
    override fun setRemainingPersistentAngerTime(value: Int) {
        this.remainingPersistentAngerTime = value
    }

    override fun getPersistentAngerTarget(): UUID? = this.persistentAngerTarget
    override fun setPersistentAngerTarget(persistentAngerTarget: UUID?) {
        this.persistentAngerTarget = persistentAngerTarget
    }

    override fun startPersistentAngerTimer() {
        this.remainingPersistentAngerTime = PERSISTENT_ANGER_TIME.sample(this.random)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val result = super.hurt(source, amount)
        if (result && source.entity is LivingEntity) {
            val attacker = source.entity as LivingEntity
            callNearbyFollowers(attacker)
            this.persistentAngerTarget = attacker.uuid
            this.startPersistentAngerTimer()
            this.target = attacker
        }
        return result
    }

    private fun callNearbyFollowers(attacker: LivingEntity) {
        if (level() == null) return

        val radius = 20.0
        val aabb = AABB(
            this.blockPosition().x - radius, this.blockPosition().y - radius, this.blockPosition().z - radius,
            this.blockPosition().x + radius, this.blockPosition().y + radius, this.blockPosition().z + radius
        )

        level().getEntitiesOfClass(CultFollowerEntity::class.java, aabb) { it != this }.forEach {
            it.persistentAngerTarget = attacker.uuid
            it.startPersistentAngerTimer()
            it.target = attacker
        }
    }
}