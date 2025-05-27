package net.haremal.ritualsapi.api.registries

import net.haremal.ritualsapi.api.ModRegistries
import net.haremal.ritualsapi.api.rituals.RitualSigilMatcher.matchesSigil
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
    // TODO: PUT EVERY EVENT, PACKET OR ANYTHING TO WHAT THEY BELONG TO (CLEAN THE CODE AFTER COMMIT)
    // TODO: IMPLEMENT LOGIC
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return super.getShape(state, level, pos, context)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = AltarBlockEntity(pos, state)
    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? = BlockEntityTicker { lvl, pos, state, entity -> (entity as? AltarBlockEntity)?.tick() }

    class AltarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModRegistries.ALTAR_BLOCK_ENTITY.get(), pos, state) {
        fun tick() {
            println(matchesSigil(this.level ?: return, this.worldPosition))
        }
    }
}
