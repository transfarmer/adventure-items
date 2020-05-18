package transfarmer.soulboundarmory.command;

/*
public class SoulboundArmoryCommand implements Command<ServerCommandSource> {
    private static final List<String> COMMAND_TYPES = CollectionUtil.arrayList("xp", "level", "reset");
    private static final List<String> OPERATIONS = CollectionUtil.arrayList("add", "set");

    public static void register(final CommandDispatcher<ServerCommandSource> dispatcher, final boolean dedicated) {
        final LiteralCommandNode<ServerCommandSource> sbaCommandNode = dispatcher.register(
                (literal(Main.MOD_NAME).requires((final ServerCommandSource source) -> source.hasPermissionLevel(2)))
                        .then(literal("add").then(argument(""))));

        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
                (literal("experience").requires((source) -> source.hasPermissionLevel(2)))
                        .then(literal("add").then(argument("targets", players()).then((argument("amount", integer()).executes((context) ->
                                executeAdd(context.getSource(), getPlayers(context, "targets"), getInteger(context, "amount"), ExperienceCommand.Component.POINTS))).then(literal("points").executes((context) ->
                                executeAdd(context.getSource(), getPlayers(context, "targets"), getInteger(context, "amount"), ExperienceCommand.Component.POINTS))).then(literal("levels").executes((context) ->
                                executeAdd(context.getSource(), getPlayers(context, "targets"), getInteger(context, "amount"), ExperienceCommand.Component.LEVELS))))))
                        .then(literal("set").
                                then(argument("targets", players()).then(argument("amount", integer(0)).executes((context) ->
                                        executeSet(context.getSource(), getPlayers(context, "targets"), getInteger(context, "amount"), ExperienceCommand.Component.POINTS)).then(literal("points").executes((context) ->
                                        executeSet(context.getSource(), getPlayers(context, "targets"), getInteger(context, "amount"), ExperienceCommand.Component.POINTS))).then(literal("levels").executes((context) ->
                                        executeSet(context.getSource(), getPlayers(context, "targets"), getInteger(context, "amount"), ExperienceCommand.Component.LEVELS)))))).then(literal("query").
                        then(argument("targets", player()).then(literal("points").executes((context) ->
                                executeQuery(context.getSource(), getPlayer(context, "targets"), ExperienceCommand.Component.POINTS))).then(literal("levels").executes((context) ->
                                executeQuery(context.getSource(), getPlayer(context, "targets"), ExperienceCommand.Component.LEVELS))))));

        dispatcher.register((literal("xp").requires((source) -> {
            return source.hasPermissionLevel(2);
        })).redirect(literalCommandNode));
    }

    @Override
    @Nonnull
    public String getName() {
        return "sba";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    @Nonnull
    public String getUsage(@Nullable final ICommandSender sender) {
        return "command.soulboundarmory.clientUsage0";
    }

    @ParametersAreNonnullByDefault
    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender,
                        final String[] args) {
    }

    private void sendUsage(final ICommandSender sender, final boolean noItem) {
        if (noItem) {
            this.sendError(sender, "command.soulboundarmory.noItem", false);
        } else {
            if (sender instanceof PlayerEntity) {
                this.sendError(sender, "command.soulboundarmory.clientUsage0", true);
                this.sendError(sender, "command.soulboundarmory.clientUsage1", true);
            } else {
                this.sendError(sender, "command.soulboundarmory.serverUsage0", true);
                this.sendError(sender, "command.soulboundarmory.serverUsage1", true);
            }
        }
    }

    private void sendError(final ICommandSender sender, final String key, final boolean usage) {
        final ITextComponent message = (usage
                ? new TextComponentTranslation("commands.generic.usage", new TextComponentTranslation(key).getFormattedText())
                : new TextComponentTranslation(key));

        message.style().setColor(TextFormatting.RED);
        sender.sendMessage(message);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender,
                                          final String @Nonnull [] args, @Nullable final BlockPos blockPos) {
        final boolean valid = COMMAND_TYPES.contains(args[0]);
        final boolean reset = args[0].equals("reset");
        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1:
                result = getListOfStringsMatchingLastWord(args, COMMAND_TYPES);

                break;
            case 2:
                if (valid && !reset) {
                    result = getListOfStringsMatchingLastWord(args, OPERATIONS);
                } else {
                    result = getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                }

                break;
            case 3:
                if (!reset) {
                    sender.sendMessage(new TextComponentString("amount"));
                }

                break;
            case 4:
                if (valid && !reset) {
                    result = getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                }

                break;
        }

        return result;
    }

    @Override
    public boolean isUsernameIndex(@Nonnull final String[] args, final int index) {
        return index == 2 || index == 3;
    }

    @Override
    public int run(final CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (args.length == 0) {
            this.sendUsage(sender, false);
        } else {
            final String commandType = args[0];

            if (COMMAND_TYPES.contains(commandType)) {
                final PlayerEntityMP player = args.length == 2 || args.length == 4
                        ? server.getPlayerList().getPlayerByUsername(args[3])
                        : (PlayerEntityMP) sender;

                if (player != null) {
                    final ISoulboundItemComponent component = SoulboundItemUtil.getFirstComponent(player, (Item) null);

                    if (component == null) {
                        this.sendUsage(sender, true);
                    } else {
                        if (commandType.equals("reset")) {
                            component.reset(component.getItemType());
                        } else {
                            if (args.length < 3) {
                                this.sendUsage(sender, false);
                            } else {
                                final String operation = args[1];
                                final StatisticType datum;

                                if (commandType.equals("xp")) {
                                    datum = XP;
                                } else {
                                    datum = LEVEL;
                                }

                                if (operation.equals("add") || operation.equals("set")) {
                                    final int amount = Integer.parseInt(args[2]);

                                    if (operation.equals("add")) {
                                        component.addDatum(component.getItemType(), datum, amount);
                                    } else {
                                        component.setDatum(component.getItemType(), datum, amount);
                                    }
                                }
                            }
                        }

                        component.sync();
                    }
                }
            }
        }
    }
}
*/
