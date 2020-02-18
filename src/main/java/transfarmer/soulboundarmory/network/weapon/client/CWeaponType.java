package transfarmer.soulboundarmory.network.weapon.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.data.weapon.SoulWeaponType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider.CAPABILITY;

public class CWeaponType implements IMessage {
    private int slot;
    private int weaponIndex;

    public CWeaponType() {}

    public CWeaponType(final int slot, final SoulWeaponType weaponType) {
        this.slot = slot;
        this.weaponIndex = weaponType.index;
    }

    public void fromBytes(final ByteBuf buffer) {
        this.slot = buffer.readInt();
        this.weaponIndex = buffer.readInt();
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.slot);
        buffer.writeInt(this.weaponIndex);
    }

    public static final class Handler implements IMessageHandler<CWeaponType, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CWeaponType message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer player = Minecraft.getMinecraft().player;

                player.getCapability(CAPABILITY, null).setCurrentType(message.weaponIndex);
                player.inventory.setInventorySlotContents(message.slot,
                    new ItemStack(player.getCapability(CAPABILITY, null).getCurrentType().item));
            });

            return null;
        }
    }
}
