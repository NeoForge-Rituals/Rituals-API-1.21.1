package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.ModRegistries
import net.haremal.ritualsapi.api.entity.CultFollowerEntity
import net.minecraft.client.model.VillagerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object EntityCMEvents {
    @SubscribeEvent
    fun onRegisterRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(ModRegistries.CULT_FOLLOWER.get()) { c -> CultFollowerRenderer(c) }
    }

    class CultFollowerRenderer(context: EntityRendererProvider.Context) :
        MobRenderer<CultFollowerEntity, VillagerModel<CultFollowerEntity>>(
            context,
            VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)),
            0.5f
        ) {

        override fun getTextureLocation(entity: CultFollowerEntity): ResourceLocation {
            return ResourceLocation.fromNamespaceAndPath(RitualsAPI.Companion.MODID, "textures/entity/cult_follower.png")
        }
    }
}