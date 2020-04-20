package transfarmer.soulboundarmory.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;
import transfarmer.soulboundarmory.util.CollectionUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.LEVEL;
import static transfarmer.soulboundarmory.statistics.base.enumeration.StatisticType.XP;

public class CommandSoulboundArmory extends CommandBase {
    private static final List<String> COMMAND_TYPES = CollectionUtil.arrayList("xp", "level", "reset");
    private static final List<String> OPERATIONS = CollectionUtil.arrayList("add", "set");

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
        if (args.length == 0) {
            this.sendUsage(sender, false);
        } else {
            final String commandType = args[0];

            if (COMMAND_TYPES.contains(commandType)) {
                final EntityPlayerMP player = args.length == 2 || args.length == 4
                        ? server.getPlayerList().getPlayerByUsername(args[3])
                        : (EntityPlayerMP) sender;

                if (player != null) {
                    final IItemCapability capability = SoulItemHelper.getFirstCapability(player, (Item) null);

                    if (capability == null) {
                        this.sendUsage(sender, true);
                    } else {
                        if (commandType.equals("reset")) {
                            capability.reset();
                        } else {
                            if (args.length < 3) {
                                this.sendUsage(sender, false);
                            } else {
                                final String operation = args[1];
                                final IStatistic datum;

                                if (commandType.equals("xp")) {
                                    datum = XP;
                                } else {
                                    datum = LEVEL;
                                }

                                if (operation.equals("add") || operation.equals("set")) {
                                    final int amount = Integer.parseInt(args[2]);

                                    if (operation.equals("add")) {
                                        capability.addDatum(capability.getItemType(), datum, amount);
                                    } else {
                                        capability.setDatum(capability.getItemType(), datum, amount);
                                    }
                                }
                            }
                        }

                        capability.sync();
                    }
                }
            }
        }
    }

    private void sendUsage(final ICommandSender sender, final boolean noItem) {
        if (noItem) {
            this.sendError(sender, "command.soulboundarmory.noItem", false);
        } else {
            if (sender instanceof EntityPlayer) {
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

        message.getStyle().setColor(TextFormatting.RED);
        sender.sendMessage(message);
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender,
                                          final String @NotNull [] args, @Nullable final BlockPos blockPos) {
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
}
