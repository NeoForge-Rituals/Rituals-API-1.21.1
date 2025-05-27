package net.haremal.ritualsapi.rituals

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animation.AnimatableManager
import software.bernie.geckolib.util.GeckoLibUtil

class BloodStainEntity(type: EntityType<BloodStainEntity>, level: Level) : Entity(type, level), GeoAnimatable {
    private val cache = GeckoLibUtil.createInstanceCache(this)
    var expireTime: Long = level.gameTime + 24000

    companion object {
        var bloodDrop = mutableMapOf<Player, BloodDropData>()
        data class BloodDropData(var timer: Int, var chance: Int)
    }

    init { noPhysics = true }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {}
    override fun readAdditionalSaveData(compound: CompoundTag) {}
    override fun addAdditionalSaveData(compound: CompoundTag) {}

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar?) {}
    override fun getAnimatableInstanceCache(): AnimatableInstanceCache? = cache
    override fun getTick(`object`: Any?): Double = this.tickCount.toDouble()

    override fun tick() {
        super.tick()
        if(expireTime - level().gameTime <= 0) this.remove(RemovalReason.DISCARDED)
    }
}