package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class AltarBlock(properties: Properties) : Block(properties), EntityBlock {
    // TODO: IMPLEMENT LOGIC TO ALTAR BLOCK
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return super.getShape(state, level, pos, context)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = AltarBlockEntity(pos, state)
    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? =
        BlockEntityTicker { lvl, pos, state, entity -> (entity as? AltarBlockEntity)?.tick() }

    class AltarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModRegistries.ALTAR_BLOCK_ENTITY.get(), pos, state) {
        fun tick() {
            println(RitualSigilMatcher.matchesSigil(this.level ?: return, this.worldPosition))
        }
    }
}