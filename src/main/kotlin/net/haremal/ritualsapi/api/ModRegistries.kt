package net.haremal.ritualsapi.api

import com.google.common.base.Supplier
import net.haremal.ritualsapi.RitualsAPI.Companion.MODID
import net.haremal.ritualsapi.api.entity.CultFollowerEntity
import net.haremal.ritualsapi.api.item.RitualDaggerItem
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.DeferredSpawnEggItem
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister


object ModRegistries {
    val ENTITY_TYPES: DeferredRegister<EntityType<*>?> = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID)
    val CULT_FOLLOWER: DeferredHolder<EntityType<*>?, EntityType<CultFollowerEntity>?> = ENTITY_TYPES.register("cult_follower", Supplier<EntityType<CultFollowerEntity>> {
        EntityType.Builder.of(::CultFollowerEntity, MobCategory.MONSTER)
            .sized(0.6f, 1.95f)
            .build("cult_follower")
    })

    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MODID)
    val RITUAL_DAGGER: DeferredItem<RitualDaggerItem?> = ITEMS.register("ritual_dagger", Supplier {
        RitualDaggerItem(
            Item.Properties()
        )
    })
    val CULT_FOLLOWER_SPAWN_EGG: DeferredItem<DeferredSpawnEggItem> = ITEMS.register("cult_follower_spawn_egg", Supplier<DeferredSpawnEggItem> {
        DeferredSpawnEggItem(
            CULT_FOLLOWER,
            0x444444, // base color
            0x880000, // spot color
            Item.Properties()
        )
    })

    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)
    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)


    object BloodDataStorage {
        val bloodPixels = mutableListOf<BloodPixelData>()
        data class BloodPixelData (
            val blockPos: Vec3,
            val pixelPos: Vec3,
            val color: Triple<Int, Int, Int>
        )
    }
}



