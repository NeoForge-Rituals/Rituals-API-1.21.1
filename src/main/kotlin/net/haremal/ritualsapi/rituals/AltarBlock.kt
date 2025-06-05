package net.haremal.ritualsapi.rituals

import net.haremal.ritualsapi.ModRegistries
import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.cults.CultFollowerEntity
import net.haremal.ritualsapi.cults.CultMemberManager
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
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
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.sqrt

class AltarBlock(properties: Properties) : Block(properties), EntityBlock {
    enum class AltarLevel(val requiredFollowersAmount: Int) { SIMPLE(1), MINOR(3), STANDARD(6), MAJOR(9), GRAND(12), APOCALYPTIC(15) }
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
        var ritualStarted = false
        var performingTime = 100

        val followerTargets = mutableMapOf<CultFollowerEntity, Triple<Double, Double, Double>>()
        var ritualActive: Boolean = false
        var ritual: Ritual? = null

        // TODO: CHANGE LATER TO ALTAR LEVELS STRUCTURES
        fun getAltarLevel(level: Level): AltarLevel {
            val blockBelow = level.getBlockState(worldPosition.below()).block
            return if (blockBelow == Blocks.REDSTONE_BLOCK) AltarLevel.STANDARD else AltarLevel.MINOR
        }

        fun tick() {
            val level = level ?: return
            if (level.isClientSide) return

            val cultId = Cult.Sigil.getMatchingSigil(level, worldPosition)?.key?: run { stopRitual(); return }
            if(ritualActive) { getFollowersToAltar(level, worldPosition, getAltarLevel(level).requiredFollowersAmount, cultId) ?: run { stopRitual(); return } }

            if(ritualStarted && ritual != null) {
                if(ritual!!.altarLevel != getAltarLevel(level) || ritual!!.requirementsAreMet(level, worldPosition).not() || ritual!!.cult.id != cultId) { stopRitual(); return }
                scareMobsAround(cultId, level)

                performingTime.takeIf { it > 0 }?.run { performingTime--; return }
                ritual!!.result(level, worldPosition); stopRitual(); return
            }

            ritual = findMatchingRitual(level, worldPosition, cultId) ?: run { stopRitual(); return }
            scareMobsAround(cultId, level); ritualActive=true
        }

        fun stopRitual(){
            followerTargets.keys.forEach { f ->
                if (f.isAlive) {
                    f.setNoAi(false)
                    f.navigation.stop()
                }
            }
            ritualActive=false
            followerTargets.clear()
            ritualStarted = false

            performingTime = 100
            ritual = null
        }

        fun findMatchingRitual(level: Level, pos: BlockPos, cultId: ResourceLocation): Ritual? {
            val centerX = pos.x + 0.5; val centerY = pos.y + 1.0; val centerZ = pos.z + 0.5
            val aabb = AABB(centerX - 0.3, centerY - 0.1, centerZ - 0.3, centerX + 0.3, centerY + 0.8, centerZ + 0.3)
            val sacrifice = level.getEntitiesOfClass(LivingEntity::class.java, aabb).firstOrNull { it.isAlive }

            val blockAltarLevel = getAltarLevel(level)
            return Ritual.all().firstOrNull { ritual ->
                ritual.altarLevel.ordinal <= blockAltarLevel.ordinal &&
                        ritual.requirementsAreMet(level, pos) &&
                        ritual.cult.id == cultId &&
                        ritual.sacrifice == sacrifice?.type
            }
        }

        fun scareMobsAround(cultId: ResourceLocation, level: Level) {
            val x = worldPosition.x + 0.5
            val y = worldPosition.y.toDouble()
            val z = worldPosition.z + 0.5
            val radius = 50.0

            val area = AABB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius)

            val allLiving = level.getEntitiesOfClass(LivingEntity::class.java, area)
                .filter { it !is Player && CultMemberManager.getCult(it)?.id != cultId}

            allLiving.forEach { entity ->
                if (entity !is Mob) return@forEach // Skip non-navigating entities

                val entityPos = entity.position()
                val dx = entityPos.x - x
                val dy = entityPos.y - y
                val dz = entityPos.z - z
                val len = sqrt(dx * dx + dy * dy + dz * dz)
                if (len == 0.0) return@forEach

                val fleeX = entityPos.x + (dx / len) * 10
                val fleeY = entityPos.y
                val fleeZ = entityPos.z + (dz / len) * 10

                entity.navigation.moveTo(fleeX, fleeY, fleeZ, 2.0)
            }

        }

        fun getFollowersToAltar(level: Level, pos: BlockPos, requiredAmount: Int, cultId: ResourceLocation): List<CultFollowerEntity>? {
            val trueRequired = requiredAmount - 1
            val cx = pos.x + 0.5; val cy = pos.y.toDouble(); val cz = pos.z + 0.5
            if (followerTargets.isEmpty() ) {
                val searchAABB = AABB(cx - 50.0, cy - 50.0, cz - 50.0, cx + 50.0, cy + 50.0, cz + 50.0)
                val allFollowers = level.getEntitiesOfClass(CultFollowerEntity::class.java, searchAABB)
                    .filter { it.isAlive && CultMemberManager.getCult(it)?.id == cultId }
                    .sortedBy { it.distanceToSqr(cx, cy, cz) }
                if (allFollowers.size < trueRequired) return null
                val chosen = allFollowers.take(trueRequired)

                val closeRadius = 1.5
                val angleCloseStep = (2 * Math.PI) / 3.0
                val closeSpots = List(2) { i ->
                    val a = angleCloseStep * i
                    Triple(
                        cx + closeRadius * kotlin.math.cos(a),
                        cy,
                        cz + closeRadius * kotlin.math.sin(a)
                    )
                }

                val farCount = trueRequired - 2
                val farRadius = 5.0
                val angleFarStep = (2 * Math.PI) / farCount
                val farSpots = List(farCount) { i ->
                    val a = angleFarStep * i
                    Triple(
                        cx + farRadius * kotlin.math.cos(a),
                        cy,
                        cz + farRadius * kotlin.math.sin(a)
                    )
                }

                val allSpots = closeSpots + farSpots
                val assignedSet = mutableSetOf<CultFollowerEntity>()
                allSpots.forEach { (tx, ty, tz) ->
                    val nextFollower = chosen
                        .filter { it !in assignedSet }
                        .minByOrNull { it.distanceToSqr(tx, ty, tz) }
                        ?: return null
                    assignedSet += nextFollower
                    followerTargets[nextFollower] = Triple(tx, ty, tz)
                    nextFollower.navigation.moveTo(tx, ty, tz, 1.5)
                }
                return chosen.toList()
            }

            val iterator = followerTargets.entries.iterator()
            while (iterator.hasNext()) {
                val (follower, target) = iterator.next()
                if (!follower.isAlive) { stopRitual(); return null }
                val (tx, ty, tz) = target
                val distSq = follower.distanceToSqr(tx, ty, tz)

                if (distSq <= 3) {
                    // b) They’ve arrived: teleport exactly onto the spot and freeze (disable AI)
                    follower.teleportTo(tx, ty, tz)
                    follower.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3(pos.x + 0.5, pos.y + 1.0, pos.z + 0.5))
                    follower.setNoAi(true)
                    follower.navigation.stop()
                } else {
                    if (follower.navigation.isDone) {
                        follower.navigation.moveTo(tx, ty, tz, 1.5)
                    }
                }
            }
            return followerTargets.keys.toList()
        }

        fun areFollowersInPosition(tolerance: Double = 0.5): Boolean {
            followerTargets.forEach { (follower, targetPos) ->
                if (!follower.isAlive) return false
                val (tx, ty, tz) = targetPos
                val distSq = follower.distanceToSqr(tx, ty, tz)
                if (distSq > tolerance * tolerance) return false
            }
            return true
        }

        override fun setRemoved() {
            stopRitual()
            super.setRemoved()
        }
    }
}

// TODO: ADD [UNMOVING ENTITIES ITEM] FOR SETTING ENTITIES UP ON THE ALTAR