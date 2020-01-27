package transfarmer.soulweapons.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.gui.SoulWeaponMenu;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class ClientWeaponLevelup implements IMessage {
    public ClientWeaponLevelup() {}

    @Override
    public void fromBytes(ByteBuf buffer) {}

    @Override
    public void toBytes(ByteBuf buffer) {}

    public static final class Handler implements IMessageHandler<ClientWeaponLevelup, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(ClientWeaponLevelup message, MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                ISoulWeapon instance = player.getCapability(CAPABILITY, null);

                instance.addLevel();
                player.addExperienceLevel(-instance.getLevel());
                Minecraft.getMinecraft().displayGuiScreen(new SoulWeaponMenu("menu.soulweapons.attributes"));
            });

            return null;
        }
    }
}
