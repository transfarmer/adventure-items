package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponXP implements IMessage {
    private int xp;

    public ClientWeaponXP() {}

    public ClientWeaponXP(int xp) {
        this.xp = xp;
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        this.xp = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.writeInt(this.xp);
    }

    public static final class Handler implements IMessageHandler<ClientWeaponXP, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientWeaponXP message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                ISoulWeapon instance = player.getCapability(CAPABILITY, null);

                instance.addXP(message.xp);
            });

            return null;
        }
    }
}
