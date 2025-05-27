package net.haremal.ritualsapi.cults

import net.haremal.ritualsapi.ModRegistries
import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.debug.ExampleCult
import net.haremal.ritualsapi.rituals.RitualSigilMatcher.makeSigil
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.MOD)

object CultEventsSM {
    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            CultRegistry.cults.forEach { makeSigil(it.value.cultSigilGet()) }
        }

        // EXAMPLE
        CultRegistry.register(ExampleCult)
    }

    @SubscribeEvent
    fun onEntityAttributes(event: EntityAttributeCreationEvent) {
        event.put(
            ModRegistries.CULT_FOLLOWER_ENTITY.get() as EntityType<out LivingEntity?>,
            Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .build()
        )
    }
}