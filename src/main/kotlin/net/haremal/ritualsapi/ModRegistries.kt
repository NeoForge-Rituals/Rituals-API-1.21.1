package net.haremal.ritualsapi

import com.google.common.base.Supplier
import net.haremal.ritualsapi.RitualsAPI.Companion.MODID
import net.haremal.ritualsapi.debug.DebugWand
import net.haremal.ritualsapi.rituals.AltarBlock
import net.haremal.ritualsapi.rituals.BloodStainEntity
import net.haremal.ritualsapi.cults.CultFollowerEntity
import net.haremal.ritualsapi.rituals.RitualDaggerItem
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import net.neoforged.neoforge.common.DeferredSpawnEggItem
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister


object ModRegistries {
    val ENTITY_TYPES: DeferredRegister<EntityType<*>?> = DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MODID)
        val CULT_FOLLOWER_ENTITY: DeferredHolder<EntityType<*>?, EntityType<CultFollowerEntity>?> = ENTITY_TYPES.register("cult_follower", Supplier<EntityType<CultFollowerEntity>> {
            EntityType.Builder.of(::CultFollowerEntity, MobCategory.MONSTER).sized(0.6f, 1.95f).build("cult_follower")
        })
        val BLOOD_STAIN_ENTITY: DeferredHolder<EntityType<*>?, EntityType<BloodStainEntity>?> = ENTITY_TYPES.register("blood_stain", Supplier {
            EntityType.Builder.of(::BloodStainEntity, MobCategory.MISC).sized(0.5f, 0.05f).clientTrackingRange(64).build("blood_stain")
        })

    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(MODID)
        val RITUAL_DAGGER_ITEM: DeferredItem<RitualDaggerItem?> = ITEMS.register("ritual_dagger", Supplier {
            RitualDaggerItem(Item.Properties())
        })
        val CULT_FOLLOWER_SPAWN_EGG: DeferredItem<DeferredSpawnEggItem> = ITEMS.register("cult_follower_spawn_egg", Supplier<DeferredSpawnEggItem> {
            DeferredSpawnEggItem(CULT_FOLLOWER_ENTITY, 0x444444, 0x880000, Item.Properties())
        })
        val DEBUG_WAND: DeferredItem<DebugWand?> = ITEMS.register("debug_wand", Supplier {
            DebugWand(Item.Properties())
        })

    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(MODID)
        val ALTAR_BLOCK: DeferredBlock<AltarBlock> = BLOCKS.register("altar", Supplier {
            AltarBlock(Properties.of().strength(3.0f).noOcclusion())
        })
    val BLOCK_ENTITY_TYPES: DeferredRegister<BlockEntityType<*>> = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID)
        val ALTAR_BLOCK_ENTITY: DeferredHolder<BlockEntityType<*>?, BlockEntityType<AltarBlock.AltarBlockEntity?>?> = BLOCK_ENTITY_TYPES.register("altar_block_entity", Supplier {
            BlockEntityType.Builder.of(AltarBlock::AltarBlockEntity, ALTAR_BLOCK.get()).build(null)
        })


    val BLOCK_ITEMS: DeferredRegister<Item?> = DeferredRegister.create(Registries.ITEM, MODID)
        val ALTAR_BLOCK_ITEM: DeferredHolder<Item?, BlockItem?> = BLOCK_ITEMS.register("altar", Supplier {
            BlockItem(ALTAR_BLOCK.get(), Item.Properties())
        })


    val CREATIVE_MODE_TABS: DeferredRegister<CreativeModeTab> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID)
    val RITUALS_API_TAB: DeferredHolder<CreativeModeTab?, CreativeModeTab?> = CREATIVE_MODE_TABS.register("rituals_api", Supplier {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.${MODID}.rituals_api")) // for lang
            .icon { ItemStack(ALTAR_BLOCK.get().asItem()) } // icon
            .displayItems { _: CreativeModeTab.ItemDisplayParameters, output: CreativeModeTab.Output ->
                output.accept(ALTAR_BLOCK.get().asItem())
                output.accept(RITUAL_DAGGER_ITEM.get())
                output.accept(CULT_FOLLOWER_SPAWN_EGG.get())
            }
            .build()
    })

}



