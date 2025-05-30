package net.haremal.ritualsapi

import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.cults.CultHandlers
import net.haremal.ritualsapi.cults.EnergyHandlers
import net.haremal.ritualsapi.cults.SyncCultPacket
import net.haremal.ritualsapi.cults.SyncEnergyPacket
import net.haremal.ritualsapi.debug.DebugBoxesHandlers
import net.haremal.ritualsapi.debug.SyncDebugBoxesPacket
import net.haremal.ritualsapi.examples.Example2Cult
import net.haremal.ritualsapi.examples.ExampleCult
import net.haremal.ritualsapi.examples.ExampleRitual
import net.haremal.ritualsapi.rituals.Ritual
import net.haremal.ritualsapi.rituals.Ritual.RitualSigilMatcher.makeSigil
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
import net.neoforged.neoforge.network.registration.HandlerThread

@EventBusSubscriber(modid = RitualsAPI.MODID, bus = EventBusSubscriber.Bus.MOD)

object ModServerEvents {
    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            Cult.CultRegistry.cults.forEach { makeSigil(it.value, it.value.cultSigilGet()) }
        }

        // EXAMPLE
        Cult.register(ExampleCult)
        Cult.register(Example2Cult)
        Ritual.register(ExampleRitual)
    }

    @SubscribeEvent
    fun entityAttributes(event: EntityAttributeCreationEvent) {
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

    @SubscribeEvent
    fun packets(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("1").executesOn(HandlerThread.NETWORK)
        registrar.playBidirectional(
            SyncEnergyPacket.Companion.TYPE,
            SyncEnergyPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                EnergyHandlers::clientHandleEnergyPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncCultPacket.Companion.TYPE,
            SyncCultPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                CultHandlers::clientHandleCultPacket
            ) { _, _ -> }
        )
        registrar.playBidirectional(
            SyncDebugBoxesPacket.Companion.TYPE,
            SyncDebugBoxesPacket.Companion.STREAM_CODEC,
            DirectionalPayloadHandler(
                DebugBoxesHandlers::clientHandleDebugBoxes
            ) { _, _ -> }
        )
    }
}