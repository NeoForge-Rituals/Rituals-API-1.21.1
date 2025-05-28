package net.haremal.ritualsapi.cults

import com.mojang.blaze3d.systems.RenderSystem
import net.haremal.ritualsapi.RitualsAPI
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderGuiEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object CultEventsCG {
    private val CULT_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(RitualsAPI.Companion.MODID,"textures/gui/cult_bar.png")

    @SubscribeEvent
    fun onRenderGuiPost(event: RenderGuiEvent.Pre) {
        // Checking if a player exists
        val minecraft = Minecraft.getInstance()
        Minecraft.getInstance().player ?: return

        // Checking if the player is in a cult
        val cultId = SyncCultPacket.ClientCultCache.id ?: return
        CultRegistry.get(cultId) ?: return
        CultMemberManager.getCult() ?: return

        // Variables for rendering the bar
        val barLoad = maxOf(SyncEnergyPacket.ClientEnergyCache.energy, 5)
        val xPos = minecraft.window.guiScaledWidth - (minecraft.window.guiScaledWidth/20)
        val yPos = minecraft.window.guiScaledHeight / 15

        // Bind texture for rendering
        minecraft.textureManager.bindForSetup(CULT_BAR_TEXTURE)

        // Drawing the bar
        RenderSystem.enableBlend()
        event.guiGraphics.blit(
            CULT_BAR_TEXTURE,
            xPos, yPos, 0f, 0f,
            10, barLoad, 10, 100
        )
        RenderSystem.disableBlend()
    }
}