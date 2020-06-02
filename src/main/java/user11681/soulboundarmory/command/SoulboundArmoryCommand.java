package user11681.soulboundarmory.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.arguments.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SoulboundArmoryCommand {
    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicated) {
        dispatcher.register((literal("sba").requires((final ServerCommandSource source) -> source.hasPermissionLevel(2)))
                .then(literal("add")
                        .then(literal("xp")
                                .then(argument("value", integer()).executes(SoulboundArmoryCommand::add))
                                .then(argument("targets", players()).executes(SoulboundArmoryCommand::add)))
                        .then(literal("level")
                                .then(argument("value", integer()).executes(SoulboundArmoryCommand::add))
                                .then(argument("targets", players()).executes(SoulboundArmoryCommand::add))))
                .then(literal("set")
                        .then(literal("xp")
                                .then(argument("value", integer()).executes(SoulboundArmoryCommand::set))
                                .then(argument("targets", players()).executes(SoulboundArmoryCommand::set)))
                        .then(literal("level")
                                .then(argument("value", integer()).executes(SoulboundArmoryCommand::set))
                                .then(argument("targets", players()).executes(SoulboundArmoryCommand::set))))
                .then(literal("reset").executes(SoulboundArmoryCommand::reset))
        );
    }

    protected static int reset(final CommandContext<ServerCommandSource> context) {
        return 0;
    }

    protected static int set(final CommandContext<ServerCommandSource> context) {
        return 0;
    }

    protected static int add(final CommandContext<ServerCommandSource> context) {
        return 0;
    }
}
