package transfarmer.soulboundarmory.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import transfarmer.soulboundarmory.capability.soulbound.ISoulCapability;
import transfarmer.soulboundarmory.capability.soulbound.SoulItemHelper;
import transfarmer.soulboundarmory.statistics.SoulDatum;
import transfarmer.soulboundarmory.util.ListUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;

public class CommandSoulboundArmory extends CommandBase {
    private static final List<String> COMMAND_TYPES = ListUtils.arrayList("xp", "level", "reset");
    private static final List<String> OPERATIONS = ListUtils.arrayList("add", "set");

    @Override
    public String getName() {
        return "sba";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @ParametersAreNonnullByDefault
    @Override
    public String getUsage(final ICommandSender sender) {
        return "command.soulboundarmory.usage";
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
                    final ISoulCapability capability = SoulItemHelper.getCapability(player, (Item) null);

                    if (commandType.equals("reset")) {
                        capability.init();
                    } else {
                        if (args.length < 3) {
                            throw new WrongUsageException(this.getUsage(null));
                        }

                        final String operation = args[1];
                        final SoulDatum datum;

                        if (commandType.equals("xp")) {
                            datum = DATA.xp;
                        } else {
                            datum = DATA.level;
                        }

                        if (operation.equals("add") || operation.equals("set")) {
                            final int amount = Integer.parseInt(args[2]);

                            if (operation.equals("add")) {
                                capability.addDatum(amount, datum, capability.getCurrentType());
                            } else {
                                capability.setDatum(amount, datum, capability.getCurrentType());
                            }
                        }
                    }

                    capability.sync();
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(final MinecraftServer server, final ICommandSender sender, final String[] args, @Nullable final BlockPos blockPos) {
        boolean notReset = COMMAND_TYPES.contains(args[0]) && !args[0].equals("reset");

        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, COMMAND_TYPES);
            case 2:
                return notReset ? getListOfStringsMatchingLastWord(args, OPERATIONS) : null;
            case 3:
                return args[0].equals("reset") ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : null;
            case 4:
                return notReset ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : null;
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public boolean isUsernameIndex(final String[] args, final int index) {
        return index == 1;
    }
}
