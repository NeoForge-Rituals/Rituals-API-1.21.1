package net.haremal.ritualsapi.api.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class BloodStainEntity(type: EntityType<BloodStainEntity>, level: Level) : Entity(type, level) {
    init {
        noPhysics = true  // no collision
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {}
    override fun readAdditionalSaveData(compound: CompoundTag) {}
    override fun addAdditionalSaveData(compound: CompoundTag) {}
}