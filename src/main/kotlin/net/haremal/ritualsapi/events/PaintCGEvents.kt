package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.ModRegistries
import net.haremal.ritualsapi.api.ModRegistries.BloodDataStorage.bloodPixels
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LevelRenderer
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.AABB
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object PaintCGEvents {
    @SubscribeEvent
    fun onRenderWorld(event: RenderLevelStageEvent) {
        if (event.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return

        val cam = event.camera
        val poseStack = event.poseStack

        poseStack.pushPose()
        poseStack.translate(-cam.position.x, -cam.position.y, -cam.position.z)

        val buffer = Minecraft.getInstance().renderBuffers().bufferSource()
        val builder = buffer.getBuffer(RenderType.debugFilledBox())

        val height = 0.002
        val width = 0.125

        for (bloodPixel in bloodPixels) {
            val blockPos = bloodPixel.blockPos
            val pixelPos = bloodPixel.pixelPos
            val bloodColor = bloodPixel.color

            val xPixel = pixelPos.x.div(16)+blockPos.x
            val zPixel = pixelPos.z.div(16)+blockPos.z

            val widthXBotReducerCalc = if (pixelPos.x.toInt() == 0) 2 else if (pixelPos.x.toInt() == 1) 1 else 0
            val widthXTopReducerCalc = if (pixelPos.x.toInt() > 14) 1 else 0
            val widthZBotReducerCalc = if (pixelPos.z.toInt() == 0) 2 else if (pixelPos.z.toInt() == 1) 1 else 0
            val widthZTopReducerCalc = if (pixelPos.z.toInt() > 14) 1 else 0

            val widthXBotReducer = widthXBotReducerCalc.toDouble() * width/2
            val widthXTopReducer = widthXTopReducerCalc.toDouble() * width/2
            val widthZBotReducer = widthZBotReducerCalc.toDouble() * width/2
            val widthZTopReducer = widthZTopReducerCalc.toDouble() * width/2

            val red = bloodColor.first / 255f
            val green = bloodColor.second / 255f
            val blue = bloodColor.third / 255f

            LevelRenderer.addChainedFilledBoxVertices(
                poseStack, builder,
                xPixel - (width - widthXBotReducer), blockPos.y + 0.001, zPixel - (width - widthZBotReducer),
                xPixel + (width - widthXTopReducer), blockPos.y + 0.001 + height, zPixel + (width - widthZTopReducer),
                red, green, blue, 1f
            )
        }

        poseStack.popPose()
        buffer.endBatch()
    }
}