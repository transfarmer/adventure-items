package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.Main;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ServerWeaponLevelup implements IMessage {
    public ServerWeaponLevelup() {}

    @Override
    public void fromBytes(ByteBuf buffer) {}

    @Override
    public void toBytes(ByteBuf buffer) {}

    public static class Handler implements IMessageHandler<ServerWeaponLevelup, IMessage> {
        public IMessage onMessage(ServerWeaponLevelup message, MessageContext context) {
            EntityPlayerMP player = context.getServerHandler().player;
            ISoulWeapon instance = player.getCapability(CAPABILITY, null);

            if (player.experienceLevel > instance.getLevel()) {
                instance.addLevel();
                player.addExperienceLevel(-instance.getLevel());
                Main.CHANNEL.sendTo(new ClientWeaponLevelup(), player);
            }

            return null;
        }
    }
}
