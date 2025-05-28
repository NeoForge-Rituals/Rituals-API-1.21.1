package net.haremal.ritualsapi.debug

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.haremal.ritualsapi.cults.Cult
import net.haremal.ritualsapi.cults.CultMemberManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import java.util.function.Supplier

object DebugCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("cult")
                .then(join())
                .then(leave())
                .then(info())
        )
    }

    private val SUGGEST_CULTS = SuggestionProvider<CommandSourceStack> { ctx, builder ->
        val availableCults = Cult.all()
        for (cult in availableCults) {
            builder.suggest(cult.id.toString())
        }
        builder.buildFuture()
    }

    private fun join(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("join")
            .then(
                Commands.argument("target", EntityArgument.entity()) // exactly one entity (player or non-player)
                    .then(
                        Commands.argument("cultId", ResourceLocationArgument.id())
                            .suggests(SUGGEST_CULTS)
                            .executes { ctx ->
                                val entity = EntityArgument.getEntity(ctx, "target")
                                val cultId = ResourceLocationArgument.getId(ctx, "cultId")
                                val cult = Cult.get(cultId)
                                if (cult == null) {
                                    ctx.source.sendFailure(Component.literal("Cult '$cultId' does not exist."))
                                    return@executes 1
                                }
                                CultMemberManager.joinCult(entity as LivingEntity, cult)
                                ctx.source.sendSystemMessage(Component.literal("${entity.name} joined the cult: ${cult.name}"))
                                1
                            }
                    )
            )
    }

    private fun leave(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("leave")
            .then(
                Commands.argument("target", EntityArgument.player()) // exactly one player
                    .executes { ctx ->
                        val player = EntityArgument.getPlayer(ctx, "target")
                        CultMemberManager.leaveCult(player)
                        ctx.source.sendSystemMessage(Component.literal("${player.name} has left the cult."))
                        1
                    }
            )
    }

    private fun info(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("info")
            .then(
                Commands.argument("target", EntityArgument.entities()) // allow any entities now
                    .executes { ctx ->
                        val targets = EntityArgument.getEntities(ctx, "target")
                        for (entity in targets) {
                            val cult = CultMemberManager.getCult(entity as LivingEntity)
                            if (cult != null) {
                                ctx.source.sendSystemMessage(Component.literal("${entity.name} is a member of the cult: ${cult.name}"))
                            } else {
                                ctx.source.sendSystemMessage(Component.literal("${entity.name} is not part of any cult."))
                            }
                        }
                        1
                    }
            )
    }

}
