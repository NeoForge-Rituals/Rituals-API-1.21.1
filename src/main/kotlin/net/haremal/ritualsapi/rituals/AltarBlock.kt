package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.haremal.ritualsapi.rituals.Ritual.RitualSigilMatcher.getMatchingSigil
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class AltarBlock(properties: Properties) : Block(properties), EntityBlock {
    enum class RitualType { SUMMONING, EMPOWERMENT, CURSE, TRANSFORMATION }
    enum class AltarLevel { MINOR, STANDARD, MAJOR, GRAND, APOCALYPTIC }
    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return super.getShape(state, level, pos, context)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = AltarBlockEntity(pos, state)
    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (type == ModRegistries.ALTAR_BLOCK_ENTITY.get()) {
            BlockEntityTicker { lvl, pos, state, entity -> (entity as? AltarBlockEntity)?.tick() }
        } else null
    }

    class AltarBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(ModRegistries.ALTAR_BLOCK_ENTITY.get(), pos, state) {
        var isPerfoming = false
        var perfomingTime = 100
        var sacrificedType: EntityType<*>? = null
        var cultId: ResourceLocation? = null  // Add this

        override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
            super.saveAdditional(tag, registries)
            cultId?.let { tag.putString("CultId", it.toString()) }
            sacrificedType?.let { tag.putString("SacrificedType", it.toString()) }
            tag.putBoolean("IsPerforming", isPerfoming)
            tag.putInt("PerformingTime", perfomingTime)
        }

        override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
            super.loadAdditional(tag, registries)
            cultId = tag.getString("CultId").takeIf { it.isNotEmpty() }?.let { ResourceLocation.parse(it) }
            sacrificedType = tag.getString("SacrificedType").takeIf { it.isNotEmpty() }?.let { id ->
                // You need to get the EntityType by its ResourceLocation registry name:
                EntityType.byString(id).orElse(null)
            }
            isPerfoming = tag.getBoolean("IsPerforming")
            perfomingTime = tag.getInt("PerformingTime")

        }

        // TODO: CHANGE LATER TO ALTAR LEVELS STRUCTURES
        fun getAltarLevel(level: Level, altarPos: BlockPos): AltarLevel {
            val blockBelowPos = altarPos.below()
            val blockBelow = level.getBlockState(blockBelowPos).block
            return if (blockBelow == Blocks.REDSTONE_BLOCK) AltarLevel.STANDARD else AltarLevel.MINOR
        }

        fun tick() {
            val level = level ?: return

            val sigil = getMatchingSigil(level, worldPosition)
            if (sigil == null) { stopRitual(); return }
            if (cultId == null) { cultId = sigil.key; setChanged() }

            // TODO: CHANGE LATER FOR ALTAR ACTIVATION
            level.addParticle(
                ParticleTypes.SOUL_FIRE_FLAME,
                worldPosition.x + 0.5 + level.random.nextGaussian() * 0.1,
                worldPosition.y + 1.7,
                worldPosition.z + 0.5 + level.random.nextGaussian() * 0.1,
                0.0, 0.01, 0.0
            )

            val sacrifice = getEntityStandingOnAltar(level, worldPosition)
            val sacrificeType = sacrifice?.type ?: sacrificedType
            if (sacrificeType == null) { stopRitual(); return }

            val ritual = findMatchingRitual(level, worldPosition, sacrificeType, cultId!!)
            if (!isPerfoming || ritual == null) { stopRitual(); return }

            if(perfomingTime>0) { perfomingTime--; setChanged(); return}
            ritual.perform(level, worldPosition); stopRitual()
        }


        fun stopRitual(){
            isPerfoming = false
            perfomingTime = 100
            sacrificedType = null
            setChanged()
        }

        fun getEntityStandingOnAltar(level: Level, pos: BlockPos): LivingEntity? {
            val centerX = pos.x + 0.5
            val centerY = pos.y + 1.0  // One block above
            val centerZ = pos.z + 0.5

            val aabb = AABB(centerX - 0.3, centerY - 0.1, centerZ - 0.3, centerX + 0.3, centerY + 0.8, centerZ + 0.3)
            return level.getEntitiesOfClass(LivingEntity::class.java, aabb)
                .firstOrNull { it.isAlive }
        }

        fun findMatchingRitual(
            level: Level,
            pos: BlockPos,
            target: EntityType<*>?,
            cultId: ResourceLocation
        ): Ritual? {
            val blockAltarLevel = getAltarLevel(level, pos)
            return Ritual.all().firstOrNull { ritual ->
                ritual.altarLevel.ordinal <= blockAltarLevel.ordinal &&
                        ritual.requirementsAreMet(level, pos) &&
                        ritual.cult.id == cultId &&
                        ritual.sacrifice == target
            }
        }
    }
}

// TODO: MAKE DAGGER KILL SACRIFICE ONLY IF EVERY SINGLE REQUIREMENT IS MET (IFS IN RITUAL DAGGER ITEM)
// TODO: IF ALTAR BLOCK CAN PERFORM THEN ASSIGN FOLLOWERS TO GO THERE (CULT FOR THE SPECIFIC RITUAL, NUMBER FOR SPECIFIC LEVEL)
// TODO: ASSIGNED FOLLOWERS GO TO THE ALTAR, EVERY OTHER ENTITY RUNS AWAY FROM THE ALTAR
// TODO: ADD PARALYSING INJECTION
