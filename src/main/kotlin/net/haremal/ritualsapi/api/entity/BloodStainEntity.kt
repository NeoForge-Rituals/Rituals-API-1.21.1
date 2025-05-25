package net.haremal.ritualsapi.api.entity

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.ModRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animation.AnimatableManager
import software.bernie.geckolib.util.GeckoLibUtil

class BloodStainEntity(type: EntityType<BloodStainEntity>, level: Level) : Entity(type, level), GeoAnimatable {
    private val cache = GeckoLibUtil.createInstanceCache(this)
    companion object {
        var timer = mutableMapOf<Player, Int>()
    }

    init { noPhysics = true }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {}
    override fun readAdditionalSaveData(compound: CompoundTag) {}
    override fun addAdditionalSaveData(compound: CompoundTag) {}

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar?) {}
    override fun getAnimatableInstanceCache(): AnimatableInstanceCache? = cache
    override fun getTick(`object`: Any?): Double = this.tickCount.toDouble()
}