package net.haremal.ritualsapi.cults

import net.minecraft.util.TimeUtil
import net.minecraft.util.valueproviders.UniformInt
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.NeutralMob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import java.util.UUID

class CultFollowerEntity(type: EntityType<CultFollowerEntity>, level: Level) : PathfinderMob(type, level), NeutralMob {
    companion object {
        private val PERSISTENT_ANGER_TIME: UniformInt = TimeUtil.rangeOfSeconds(30, 60)
    }

    private var remainingPersistentAngerTime = 0
    private var persistentAngerTarget: UUID? = null

    override fun registerGoals() {
        goalSelector.addGoal(1, FloatGoal(this))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.5, true))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(5, RandomLookAroundGoal(this))

        targetSelector.addGoal(1, CultAwareHurtByTargetGoal(this))
        targetSelector.addGoal(2, ResetUniversalAngerTargetGoal(this, false)) // Stop anger after timer
    }

    override fun getRemainingPersistentAngerTime(): Int = remainingPersistentAngerTime
    override fun setRemainingPersistentAngerTime(value: Int) {
        remainingPersistentAngerTime = value
    }

    override fun getPersistentAngerTarget(): UUID? = persistentAngerTarget
    override fun setPersistentAngerTarget(target: UUID?) {
        persistentAngerTarget = target
    }

    override fun startPersistentAngerTimer() {
        remainingPersistentAngerTime = PERSISTENT_ANGER_TIME.sample(random)
    }

    override fun tick() {
        super.tick()

        if (CultMemberManager.getCult(this)?.id == null) {
            CultMemberManager.joinCult(this, Cult.all().random())
        }

        val currentTarget = this.target
        if (currentTarget is LivingEntity) {
            val myCultId = CultMemberManager.getCult(this)?.id
            val targetCultId = CultMemberManager.getCult(currentTarget)?.id

            if (myCultId != null && targetCultId != null && myCultId == targetCultId) {
                this.target = null
                this.persistentAngerTarget = null
                this.remainingPersistentAngerTime = 0

                this.targetSelector.removeAllGoals { true }
                this.goalSelector.removeAllGoals { true }
                this.registerGoals()
            }
        }
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val attacker = source.entity

        val result = super.hurt(source, amount)
        if (result && attacker is LivingEntity) {
            callNearbyFollowers(attacker)
            persistentAngerTarget = attacker.uuid
            startPersistentAngerTimer()
            target = attacker
        }
        return result
    }

    private fun callNearbyFollowers(attacker: LivingEntity) {
        if (level() == null) return

        val radius = 20.0
        val aabb = AABB(
            blockPosition().x - radius, blockPosition().y - radius, blockPosition().z - radius,
            blockPosition().x + radius, blockPosition().y + radius, blockPosition().z + radius
        )

        val myCult = CultMemberManager.getCult(this)?.id ?: return

        val sameCultFollowers = level().getEntitiesOfClass(
            CultFollowerEntity::class.java,
            aabb
        ) { follower ->
            follower != this && CultMemberManager.getCult(follower)?.id == myCult
        }

        sameCultFollowers.forEach { follower ->
            follower.persistentAngerTarget = attacker.uuid
            follower.startPersistentAngerTimer()
            follower.target = attacker
        }
    }

    class CultAwareHurtByTargetGoal(mob: PathfinderMob) : HurtByTargetGoal(mob) {
        override fun canUse(): Boolean {
            val attacker = mob.lastHurtByMob ?: return false
            val attackerCult = CultMemberManager.getCult(attacker)?.id
            val myCult = CultMemberManager.getCult(mob)?.id
            return (attackerCult == null || attackerCult != myCult) && super.canUse()
        }
    }

}
