package transfarmer.soulboundarmory.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;
import transfarmer.soulboundarmory.capability.tool.ISoulTool;
import transfarmer.soulboundarmory.capability.weapon.ISoulWeapon;
import transfarmer.soulboundarmory.network.client.tool.CToolDatum;
import transfarmer.soulboundarmory.network.client.weapon.CWeaponDatum;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommandSoulboundArmory extends CommandBase {
    @Override
    public String getName() {
        return "sba";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "commands.soulboundarmory.usage";
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException(this.getUsage(null));
        } else {
            final String commandType = args[0];

            if (commandType.equals("xp")) {
                final EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                final ISoulCapability capability = SoulItemHelper.getCapability(player, null);
                final int amount = Integer.parseInt(args[1]);

                if (capability instanceof ISoulWeapon) {
                    Main.CHANNEL.sendTo(new CWeaponDatum(amount, capability.getEnumXP(), capability.getCurrentType()), (EntityPlayerMP) sender.getCommandSenderEntity());
                    capability.addDatum(amount, capability.getEnumXP(), capability.getCurrentType());
                } else if (capability instanceof ISoulTool) {
                    Main.CHANNEL.sendTo(new CToolDatum(amount, capability.getEnumXP(), capability.getCurrentType()), (EntityPlayerMP) sender.getCommandSenderEntity());
                    capability.addDatum(amount, capability.getEnumXP(), capability.getCurrentType());
                }
            }
        }
    }

    @Override
    public List<String> getTabCompletions(final MinecraftServer server, final ICommandSender sender, final String[] args, @Nullable BlockPos blockPos) {
        final List<String> entries = new ArrayList<>();

        switch (args.length) {
            case 1:
                entries.add("xp");
        }

        return entries;
    }
}
