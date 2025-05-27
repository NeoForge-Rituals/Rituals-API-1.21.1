package net.haremal.ritualsapi.network_to_remove

import net.neoforged.neoforge.network.handling.IPayloadContext

object PayloadHandlers {
    fun clientHandleEnergyPacket(data: SyncEnergyPacket, context: IPayloadContext) {
        context.enqueueWork {
            SyncEnergyPacket.ClientEnergyCache.energy = data.cultEnergy
        }
    }
    fun clientHandleCultPacket(data: SyncCultPacket, context: IPayloadContext) {
        context.enqueueWork {
            SyncCultPacket.ClientCultCache.id = data.cultId
        }
    }
    fun clientHandleDebugBoxes(data: SyncDebugBoxesPacket, context: IPayloadContext) {
        context.enqueueWork {
            val current = SyncDebugBoxesPacket.DebugBoxesCache.boxesByPos.toMutableMap()

            for ((pos, boxes) in data.boxesByPos) {
                current[pos] = (current[pos] ?: emptyList()) + boxes
            }

            SyncDebugBoxesPacket.DebugBoxesCache.boxesByPos = current
        }
    }

}