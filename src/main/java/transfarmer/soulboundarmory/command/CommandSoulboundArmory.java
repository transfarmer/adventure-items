package transfarmer.soulboundarmory.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import transfarmer.soulboundarmory.capability.ISoulCapability;
import transfarmer.soulboundarmory.capability.SoulItemHelper;

public class CommandSoulboundArmory extends CommandBase {
    @Override
    public String getName() {
        return "sba";
    }

    @Override
    public String getUsage(final ICommandSender sender) {
        return "commands.soulboundarmory.usage";
    }

    @Override
    public void execute(final MinecraftServer server, final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 3) {
            throw new WrongUsageException(this.getUsage(null));
        } else {
            final String commandType = args[2];

            if (commandType.equals("xp")) {
                final EntityPlayer player = (EntityPlayer) sender.getCommandSenderEntity();
                final Item item = player.getHeldItemMainhand().getItem();
                final ISoulCapability capability = SoulItemHelper.getCapability(player, item);
                final int amount = Integer.parseInt(args[3]);

                if (capability != null) {
                    capability.addDatum(amount, capability.getEnumXP(), capability.getCurrentType());
                }
            }
        }
    }
}
