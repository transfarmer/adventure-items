package user11681.soulboundarmory.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import user11681.soulboundarmory.client.i18n.Mappings;
import user11681.soulboundarmory.component.Components;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponentBase;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.component.statistics.StatisticType;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.command.arguments.EntityArgumentType.players;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static user11681.soulboundarmory.command.StaticConstantArgumentType.allConstants;

public class SoulboundArmoryCommand {
    protected static final DynamicCommandExceptionType NO_ITEM_EXCEPTION = new DynamicCommandExceptionType((final Object player) -> new TranslatableText(Mappings.COMMAND_NO_ITEM.getKey(), ((PlayerEntity) player).getName()));

    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicated) {
        dispatcher.register((literal("sba").requires((final ServerCommandSource source) -> source.hasPermissionLevel(2)))
                .then(literal("add")
                        .then(argument("statistic", allConstants(StatisticType.class))
                                .then(argument("value", integer()).executes((final CommandContext<ServerCommandSource> context) -> add(context, false))
                                        .then(argument("targets", players()).executes((final CommandContext<ServerCommandSource> context) -> add(context, true))))))
                .then(literal("set")
                        .then(argument("statistic", allConstants(StatisticType.class))
                                .then(argument("value", integer()).executes(SoulboundArmoryCommand::set)
                                        .then(argument("targets", players()).executes(SoulboundArmoryCommand::set)))))
                .then(literal("reset")
                        .then(argument("category", allConstants(Category.class, Category.class))
                                .then(argument("targets", players())).executes(SoulboundArmoryCommand::reset)))
        );
    }

    protected static int add(final CommandContext<ServerCommandSource> context, final boolean hasPlayerArgument) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players;

        if (hasPlayerArgument) {
            players = EntityArgumentType.getPlayers(context, "targets");
        } else {
            players = Collections.singleton(context.getSource().getPlayer());
        }

        for (final PlayerEntity player : players) {
            getStorage(player).incrementStatistic(context.getArgument("statistic", StatisticType.class), IntegerArgumentType.getInteger(context, "value"));
        }

        return 0;
    }

    protected static int set(final CommandContext<ServerCommandSource> context) {
        return 0;
    }

    protected static int reset(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        final Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(context, "targets");

        for (final PlayerEntity player : players) {
            for (final SoulboundComponentBase component : Components.getComponents(player)) {
            }
        }

        return players.size();
    }

    protected static ItemStorage<?> getStorage(final PlayerEntity player) throws CommandSyntaxException {
        for (final SoulboundComponentBase component : Components.getComponents(player)) {
            final ItemStorage<?> storage = component.getHeldItemStorage();

            if (storage != null) {
                return storage;
            }
        }

        throw NO_ITEM_EXCEPTION.create(player);
    }
}
