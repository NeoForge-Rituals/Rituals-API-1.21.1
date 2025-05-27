package net.haremal.ritualsapi.api.debug

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.suggestion.SuggestionProvider
import net.haremal.ritualsapi.api.cults.CultMemberManager
import net.haremal.ritualsapi.api.cults.CultRegistry
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer

object CultCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("cult")
                .then(join())
                .then(leave())
                .then(info())
        )
    }

    private val SUGGEST_CULTS = SuggestionProvider<CommandSourceStack> { ctx, builder ->
        val availableCults = CultRegistry.all()
        for (cult in availableCults) {
            builder.suggest(cult.id.toString())
        }
        builder.buildFuture()
    }

    private fun join(): LiteralArgumentBuilder<CommandSourceStack?>? {
        return Commands.literal("join")
            .then(
                Commands.argument("cultId", ResourceLocationArgument.id())
                    .suggests(SUGGEST_CULTS)  // Add tab-completion here
                    .executes { ctx ->
                        val player = ctx.source.entity as? ServerPlayer ?: return@executes 0
                        val cultId = ResourceLocationArgument.getId(ctx, "cultId")

                        // Check if a cult exists
                        val cult = CultRegistry.get(cultId)
                        if (cult == null) {
                            player.sendSystemMessage(Component.literal("Cult '$cultId' does not exist."))
                            return@executes 1
                        }
                        // Join the cult
                        CultMemberManager.joinCult(player, cult)
                        cult.onJoin(player)
                        player.sendSystemMessage(Component.literal("You have joined the cult: ${cult.name}"))
                        1
                    }
            )
    }


    private fun leave(): LiteralArgumentBuilder<CommandSourceStack?>? {
        return Commands.literal("leave")
            .executes { ctx ->
                val player = ctx.source.entity as? ServerPlayer ?: return@executes 0
                CultMemberManager.leaveCult(player)
                player.sendSystemMessage(Component.literal("You have left your cult."))
                1
            }
    }

    private fun info(): LiteralArgumentBuilder<CommandSourceStack?>? {
        return Commands.literal("info")
            .executes { ctx ->
                val player = ctx.source.entity as? ServerPlayer ?: return@executes 0
                val cultId = CultMemberManager.getCult(player)
                if (cultId != null) {
                    player.sendSystemMessage(Component.literal("You are a member of the cult: $cultId"))
                } else {
                    player.sendSystemMessage(Component.literal("You are not part of any cult."))
                }
                1
            }
    }
}
