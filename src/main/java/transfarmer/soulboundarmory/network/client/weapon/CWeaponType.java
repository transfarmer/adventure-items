package transfarmer.soulboundarmory.network.client.weapon;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.weapon.SoulWeaponProvider;
import transfarmer.soulboundarmory.statistics.SoulType;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CWeaponType implements IMessage {
    private int slot;
    private int weaponIndex;

    public CWeaponType() {}

    public CWeaponType(final int slot, final SoulType type) {
        this.slot = slot;
        this.weaponIndex = type.getIndex();
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

                SoulWeaponProvider.get(player).setCurrentType(message.weaponIndex);
                player.inventory.setInventorySlotContents(message.slot,
                    new ItemStack(SoulWeaponProvider.get(player).getCurrentType().getItem()));
            });

            return null;
        }
    }
}
