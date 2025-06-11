package net.haremal.ritualsapi.debug

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.cults.CultMemberManager
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import kotlin.collections.iterator

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object DebugEventsCG {
    @SubscribeEvent
    fun onRenderLevelStage(event: RenderLevelStageEvent) {
        val level = Minecraft.getInstance().level ?: return

        val cache = SyncDebugBoxesPacket.DebugBoxesCache.boxesByPos.toMutableMap()
        val iterator = cache.entries.iterator()
        while (iterator.hasNext()) {
            val (pos, _) = iterator.next()
            if (level.getBlockState(pos).isAir) {
                iterator.remove()
            }
        }
        SyncDebugBoxesPacket.DebugBoxesCache.boxesByPos = cache

        val player = Minecraft.getInstance().player
        if(player != null && DebugWand.DebugWandTracker.isDebugging(player)) {
            if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return

            val bufferSource = Minecraft.getInstance().renderBuffers().bufferSource()
            val pose = event.poseStack
            val buffer = bufferSource.getBuffer(RenderType.lines())

            val cameraPos = event.camera.position

            pose.pushPose()
            pose.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

            val playerCultId = CultMemberManager.getClientCult()?.id ?: return

            for ((altarPos, cultBoxesMap) in SyncDebugBoxesPacket.DebugBoxesCache.boxesByPos) {
                val boxes = cultBoxesMap[playerCultId] ?: continue
                for (box in boxes) {
                    LevelRenderer.renderLineBox(
                        pose, buffer, box,
                        1.0f, 1.0f, 1.0f, 1.0f
                    )
                }
            }


            pose.popPose()
            bufferSource.endBatch()
        }
    }
}