package transfarmer.soulboundarmory.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.network.client.tool.CToolDatum;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponDatum;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static transfarmer.soulboundarmory.statistics.SoulDatum.DATA;

public class CommandSoulboundArmory extends CommandBase {
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
        if (args.length < 3) {
            throw new WrongUsageException(this.getUsage(null));
        } else {
            final String commandType = args[0];

            if (commandType.equals("xp")) {
                final EntityPlayerMP player = server.getPlayerList().getPlayerByUsername(args[1]);

                if (player != null) {
                    final ISoulCapability capability = SoulItemHelper.getCapability(player, (Item) null);
                    final int amount = Integer.parseInt(args[2]);

                    if (capability instanceof ISoulWeapon) {
                        Main.CHANNEL.sendTo(new CWeaponDatum(amount, DATA.xp, capability.getCurrentType()), player);
                        capability.addDatum(amount, DATA.xp, capability.getCurrentType());
                    } else if (capability instanceof ISoulCapability) {
                        Main.CHANNEL.sendTo(new CToolDatum(amount, DATA.xp, capability.getCurrentType()), player);
                        capability.addDatum(amount, DATA.xp, capability.getCurrentType());
                    }
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(final MinecraftServer server, final ICommandSender sender, final String[] args, @Nullable final BlockPos blockPos) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "xp");
            case 2:
                return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
            default:
                return new ArrayList<>();
        }
    }

    @Override
    public boolean isUsernameIndex(final String[] args, final int index) {
        return index == 1;
    }
}
