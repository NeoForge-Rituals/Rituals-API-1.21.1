package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.cults.CultFollowerEntity
import net.haremal.ritualsapi.cults.CultMemberManager
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
    enum class AltarLevel(val followersNumber: Int) { SIMPLE(1), MINOR(3), STANDARD(6), MAJOR(9), GRAND(12), APOCALYPTIC(15) }
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
        var shouldPerform = false
        var perfomingTime = 100
        var sacrificedType: EntityType<*>? = null
        var cultId: ResourceLocation? = null  // Add this

        override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
            super.saveAdditional(tag, registries)
            cultId?.let { tag.putString("CultId", it.toString()) }
            sacrificedType?.let { tag.putString("SacrificedType", it.toString()) }
            tag.putBoolean("IsPerforming", shouldPerform)
            tag.putInt("PerformingTime", perfomingTime)
        }

        override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
            super.loadAdditional(tag, registries)
            cultId = tag.getString("CultId").takeIf { it.isNotEmpty() }?.let { ResourceLocation.parse(it) }
            sacrificedType = tag.getString("SacrificedType").takeIf { it.isNotEmpty() }?.let { id ->
                // You need to get the EntityType by its ResourceLocation registry name:
                EntityType.byString(id).orElse(null)
            }
            shouldPerform = tag.getBoolean("IsPerforming")
            perfomingTime = tag.getInt("PerformingTime")

        }

        // TODO: CHANGE LATER TO ALTAR LEVELS STRUCTURES
        fun getAltarLevel(level: Level): AltarLevel {
            val blockBelow = level.getBlockState(worldPosition.below()).block
            return if (blockBelow == Blocks.REDSTONE_BLOCK) AltarLevel.STANDARD else AltarLevel.MINOR
        }

        fun tick() {
            val level = level ?: return

            // WHAT CULT?
            val sigil = Cult.Sigil.getMatchingSigil(level, worldPosition) ?: run { stopRitual(); return }
            cultId?: run { cultId = sigil.key; setChanged() }

            // TODO: CHANGE THAT LATER
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, worldPosition.x + 0.5 + level.random.nextGaussian() * 0.1, worldPosition.y + 1.7, worldPosition.z + 0.5 + level.random.nextGaussian() * 0.1, 0.0, 0.01, 0.0)

            println(getAltarLevel(level)) //TODO

            // WHAT RITUAL?
            getEntityStandingOnAltar(level, worldPosition)?.let { sacrificedType = it.type; setChanged() }
            val ritual = findMatchingRitual(level, worldPosition, sacrificedType, cultId!!) ?: run { stopRitual(); return }

            // FOLLOWERS HERE?
            val assignedFollowers = getFollowersAround(level, worldPosition, getAltarLevel(level).followersNumber).ifEmpty { null } ?: run { stopRitual(); return }
            positionFollowersInCircle(worldPosition, assignedFollowers)

            // DAGGER HIT?
            shouldPerform.takeIf { it } ?: run { stopRitual(); return }
            // RITUAL PERFORMANCE
            perfomingTime.takeIf { it > 0 }?.run { perfomingTime--; setChanged(); return }
            // RITUAL END
            ritual.result(level, worldPosition); stopRitual()
        }


        fun stopRitual(){
            shouldPerform = false
            perfomingTime = 100
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
            val blockAltarLevel = getAltarLevel(level)
            return Ritual.all().firstOrNull { ritual ->
                ritual.altarLevel.ordinal <= blockAltarLevel.ordinal &&
                        ritual.requirementsAreMet(level, pos) &&
                        ritual.cult.id == cultId &&
                        ritual.sacrifice == target
            }
        }

        fun getFollowersAround(level: Level, pos: BlockPos, followersNumber: Int): List<CultFollowerEntity> {
            val centerX = pos.x + 0.5
            val centerY = pos.y.toDouble()
            val centerZ = pos.z + 0.5

            val radius = 50

            val aabb = AABB(centerX - radius, centerY - radius, centerZ - radius, centerX + radius, centerY + radius, centerZ + radius)
            return level.getEntitiesOfClass(CultFollowerEntity::class.java, aabb)
                .filter  { it.isAlive; CultMemberManager.getCult(it)?.id == cultId }
                .sortedBy { it.distanceToSqr(pos.x + 0.5, pos.y + 0.5, pos.z + 0.5) }
                .take(followersNumber-1)
        }

        // TODO CACHE THE ENTITIES (OR STH) SO THEY NOT TELEPORT :C
        fun positionFollowersInCircle(center: BlockPos, followers: List<LivingEntity>) {
            val closeRadius = 1.0  // distance from center
            val angleCloseStep = (2 * Math.PI) / 3
            val closeFollowers = followers.take(2)
            closeFollowers.forEachIndexed { index, follower ->
                val angle = angleCloseStep * index
                val x = center.x + 0.5 + closeRadius * kotlin.math.cos(angle)
                val z = center.z + 0.5 + closeRadius * kotlin.math.sin(angle)
                val y = center.y.toDouble() // adjust for surface level if needed
                follower.teleportTo(x, y, z)
            }

            if(followers.count()<3) return

            val farRadius = 5.0  // distance from center
            val angleFarStep = (2 * Math.PI) / (followers.size - 2)
            val farFollowers = followers - closeFollowers
            farFollowers.forEachIndexed { index, follower ->
                val angle = angleFarStep * index
                val x = center.x + 0.5 + farRadius * kotlin.math.cos(angle)
                val z = center.z + 0.5 + farRadius * kotlin.math.sin(angle)
                val y = center.y.toDouble() // adjust for surface level if needed
                follower.teleportTo(x, y, z)
            }

        }
    }
}

// TODO: ASSIGNED FOLLOWERS GO TO THE ALTAR, EVERY OTHER ENTITY RUNS AWAY FROM THE ALTAR
// TODO: ADD PARALYSING INJECTION
