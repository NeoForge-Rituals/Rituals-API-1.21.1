package net.haremal.ritualsapi

import net.haremal.ritualsapi.cults.CultFollowerEntity
import net.haremal.ritualsapi.rituals.BloodStainEntity
import net.minecraft.client.model.VillagerModel
import net.minecraft.client.model.geom.ModelLayers
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.MobRenderer
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import software.bernie.geckolib.model.GeoModel
import software.bernie.geckolib.renderer.GeoEntityRenderer

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ModClientEvents {
    @SubscribeEvent
    fun onRegisterRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(ModRegistries.CULT_FOLLOWER_ENTITY.get()) { c -> CultFollowerRenderer(c) }
        event.registerEntityRenderer(ModRegistries.BLOOD_STAIN_ENTITY.get()) { c -> BloodStainsRenderer(c) }
    }

    class CultFollowerRenderer(context: EntityRendererProvider.Context) : MobRenderer<CultFollowerEntity, VillagerModel<CultFollowerEntity>>(context,
        VillagerModel(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f) {
        override fun getTextureLocation(entity: CultFollowerEntity): ResourceLocation = ResourceLocation.fromNamespaceAndPath(
            RitualsAPI.Companion.MODID, "textures/entity/cult_follower.png")
    }

    class BloodStainsRenderer(context: EntityRendererProvider.Context) : GeoEntityRenderer<BloodStainEntity>(context, BloodStainModel()) {
        override fun getTextureLocation(entity: BloodStainEntity): ResourceLocation = textures[entity.id % textures.size]
        val textures = listOf(
            ResourceLocation.fromNamespaceAndPath(RitualsAPI.Companion.MODID, "textures/entity/blood_stains/blood_stain_0.png")
        )
    }

    class BloodStainModel : GeoModel<BloodStainEntity>() {
        @Deprecated("Deprecated in Java")
        override fun getModelResource(entity: BloodStainEntity?): ResourceLocation = ResourceLocation.fromNamespaceAndPath(RitualsAPI.Companion.MODID, "geo/blood_stain.geo.json")
        @Deprecated("Deprecated in Java")
        override fun getTextureResource(entity: BloodStainEntity?): ResourceLocation = ResourceLocation.fromNamespaceAndPath(RitualsAPI.Companion.MODID, "textures/entity/blood_stains/blood_stain_1.png")
        override fun getAnimationResource(entity: BloodStainEntity?): ResourceLocation? = null
    }
}