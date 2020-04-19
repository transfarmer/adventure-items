package transfarmer.soulboundarmory.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import transfarmer.soulboundarmory.capability.soulbound.IItemCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.client.i18n.Mappings;
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
        return String.format("%s\n%s", Mappings.COMMAND_USAGE_0, Mappings.COMMAND_USAGE_1);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            throw new WrongUsageException(this.getUsage(null));
        } else {
            final String commandType = args[0];

            if (COMMAND_TYPES.contains(commandType)) {
                final EntityPlayerMP player = args.length >= 4
                        ? server.getPlayerList().getPlayerByUsername(args[3])
                        : (EntityPlayerMP) sender;

                if (player != null) {
                    final IItemCapability capability = SoulItemHelper.getCapability(player, (Item) null);

                    if (capability != null) {
                        if (commandType.equals("reset")) {
                            capability.reset();
                        } else {
                            if (args.length < 3) {
                                throw new WrongUsageException(this.getUsage(null));
                            }

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

                        capability.sync();
                    } else {
                        throw new WrongUsageException(Mappings.COMMAND_NO_ITEM);
                    }
                }
            }
        }
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull final MinecraftServer server, @Nonnull final ICommandSender sender, final String[] args, @Nullable final BlockPos blockPos) {
        final boolean notReset = COMMAND_TYPES.contains(args[0]) && !args[0].equals("reset");
        List<String> result = new ArrayList<>();

        switch (args.length) {
            case 1:
                result = getListOfStringsMatchingLastWord(args, COMMAND_TYPES);
                break;
            case 2:
                if (notReset) {
                    result = getListOfStringsMatchingLastWord(args, OPERATIONS);
                }
                break;
            case 3:
                if (args[0].equals("reset")) {
                    result = getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
                }
                break;
            case 4:
                if (notReset) {
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
