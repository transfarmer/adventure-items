package transfarmer.soulboundarmory.network.C2S;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulboundCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.network.common.IExtendedMessage;
import transfarmer.soulboundarmory.network.common.IExtendedMessageHandler;
import transfarmer.soulboundarmory.statistics.base.iface.ICapabilityType;

public class C2SSync implements IExtendedMessage {
    private String capability;
    private NBTTagCompound tag;

    public C2SSync() {
    }

    public C2SSync(final ICapabilityType capability, final NBTTagCompound tag) {
        this.capability = capability.toString();
        this.tag = tag;
    }

    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.capability = buffer.readString();
        this.tag = buffer.readCompoundTag();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.capability);
        buffer.writeCompoundTag(this.tag);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SSync> {
        @Override
        public IExtendedMessage onMessage(final C2SSync message, final MessageContext context) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final SoulboundCapability capability = context.getServerHandler().player.getCapability(ICapabilityType.get(message.capability).getCapability(), null);

                if (message.tag.hasKey("tab")) {
                    capability.setCurrentTab(message.tag.getInteger("tab"));
                }

                if (capability instanceof IWeaponCapability && message.tag.hasKey("spell")) {
                    ((IWeaponCapability) capability).setSpell((message.tag.getInteger("spell")));
                }
            });

            return null;
        }
    }
}
