package net.haremal.ritualsapi.events

import net.haremal.ritualsapi.RitualsAPI
import net.haremal.ritualsapi.api.ModRegistries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent

@EventBusSubscriber(modid = RitualsAPI.Companion.MODID, bus = EventBusSubscriber.Bus.MOD)
object EntitySMEvents {
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