package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class CageBlock(properties: Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? = CageBlockEntity(pos, state)
    override fun <T : BlockEntity?> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T?>? {
        return if (type == ModRegistries.CAGE_BLOCK_ENTITY.get()) {
            BlockEntityTicker { lvl, pos, state, entity -> (entity as? CageBlockEntity)?.tick() }
        } else null
    }

    class CageBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModRegistries.CAGE_BLOCK_ENTITY.get(), pos, state) {

        fun tick() {
            val pos = worldPosition.toVec3()
            val aabb = AABB(pos.x, pos.y, pos.z, pos.x + 1, pos.y + 2, pos.z + 1)
        
        }
    }
}