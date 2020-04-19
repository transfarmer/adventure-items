package transfarmer.soulboundarmory.network.client.weapon;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeapon;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.client.gui.SoulWeaponMenu;
import transfarmer.soulboundarmory.network.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.IExtendedMessage;
import transfarmer.soulboundarmory.network.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.IItem;
import transfarmer.soulboundarmory.statistics.base.iface.IStatistic;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.base.iface.IStatistic.get;

public class S2CWeaponSpentAttributePoints implements IExtendedMessage {
    private int amount;
    private String statistic;
    private String item;

    public S2CWeaponSpentAttributePoints() {}

    public S2CWeaponSpentAttributePoints(final IItem item, final IStatistic statistic, final int amount) {
        this.amount = amount;
        this.statistic = statistic.toString();
        this.item = item.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.amount = buffer.readInt();
        this.statistic = buffer.readString();
        this.item = buffer.readString();
    }

    @Override
    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeInt(this.amount);
        buffer.writeString(this.statistic);
        buffer.writeString(this.item);
    }

    public static final class Handler implements IExtendedMessageHandler<S2CWeaponSpentAttributePoints> {
        @SideOnly(CLIENT)
        @Override
        public IExtendedMessage onMessage(final S2CWeaponSpentAttributePoints message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();
            final IItem type = IItem.get(message.item);
            final IStatistic attribute = get(message.statistic);
            final IWeapon instance = WeaponProvider.get(minecraft.player);

            minecraft.addScheduledTask(() -> {
                instance.addAttribute(type, attribute, message.amount);
                minecraft.displayGuiScreen(new SoulWeaponMenu());
            });

            return null;
        }
    }
}
