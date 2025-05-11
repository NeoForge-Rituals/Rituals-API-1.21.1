package net.haremal.ritualsapi.network

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
}