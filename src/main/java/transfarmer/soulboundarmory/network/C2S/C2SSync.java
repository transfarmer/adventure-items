package transfarmer.soulboundarmory.network.C2S;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;

public class C2SSync implements IExtendedMessage {
    private String component;
    private CompoundTag tag;

    public C2SSync() {
    }

    public C2SSync(final IComponentType component, final CompoundTag tag) {
        this.component = component.toString();
        this.tag = tag;
    }

    public void fromBytes(final ExtendedPacketBuffer buffer) {
        this.component = buffer.readString();
        this.tag = buffer.readCompoundTag();
    }

    public void toBytes(final ExtendedPacketBuffer buffer) {
        buffer.writeString(this.component);
        buffer.writeCompoundTag(this.tag);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SSync> {
        @Override
        public IExtendedMessage onMessage(final C2SSync message, final MessageContext context) {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                final ISoulboundComponent component = context.getServerHandler().player.getComponent(IComponentType.get(component).getComponent(), null);

                if (tag.hasKey("tab")) {
                    component.setCurrentTab(tag.getInt("tab"));
                }

                if (component instanceof IWeaponComponent && tag.hasKey("spell")) {
                    ((IWeaponComponent) component).setSpell((tag.getInt("spell")));
                }
            });

            return null;
        }
    }
}
