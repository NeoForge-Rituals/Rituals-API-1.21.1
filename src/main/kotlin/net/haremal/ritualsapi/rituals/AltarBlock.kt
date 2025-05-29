package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class AltarBlock(properties: Properties) : Block(properties), EntityBlock {
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return super.getShape(state, level, pos, context)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = AltarBlockEntity(pos, state)
    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? =
        BlockEntityTicker { lvl, pos, state, entity -> (entity as? AltarBlockEntity)?.tick() }

    class AltarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModRegistries.ALTAR_BLOCK_ENTITY.get(), pos, state) {
        fun tick() {
            if(RitualSigilMatcher.matchesSigil(level ?: return, worldPosition)){
                level!!.addParticle(ParticleTypes.FLAME, worldPosition.x + 0.5, worldPosition.y + 1.7, worldPosition.z + 0.5, 0.0, 0.0, 0.0)

                val radius = 0.5
                val detectArea = AABB(
                    worldPosition.center.x - radius, worldPosition.center.y + 1 - radius, worldPosition.center.z - radius,
                    worldPosition.center.x + radius, worldPosition.center.y + 1 + radius, worldPosition.center.z + radius
                )
                val tConditions = TargetingConditions.forNonCombat().range(radius)
                val nearestEntity = level!!.getNearestEntity(LivingEntity::class.java, tConditions, null, worldPosition.center.x, worldPosition.center.y, worldPosition.center.z, detectArea)

                // TODO: IF ENTITY IS SETUP, CHECK THE REQUIREMENTS OF ALL RITUALS TILL MATCHES
                // TODO: IF REQUIREMENTS ARE MET THEN ASSIGN FOLLOWERS OF THE SAME CULT AS SIGIL
                // TODO: ASSIGNED FOLLOWERS GO TO THE ALTAR, EVERY OTHER ENTITY RUNS AWAY FROM THE ALTAR
                (nearestEntity as? Mob)?.navigation?.stop() // TODO: REPLACE THIS WITH PARALYSING INJECTION
            }
        }
    }
}