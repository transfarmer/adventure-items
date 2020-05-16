package transfarmer.soulboundarmory.network.C2S;

import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulboundarmory.component.soulbound.common.ISoulboundComponent;
import transfarmer.soulboundarmory.network.common.ComponentPacket;
import transfarmer.soulboundarmory.network.common.ExtendedPacketBuffer;
import transfarmer.soulboundarmory.statistics.IItem;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulboundarmory.statistics.Category.ENCHANTMENT;

public class C2SReset extends ComponentPacket {
    private String item;
    private String category;

    public C2SReset() {
    }

    public C2SReset(final IComponentType component) {
        super(component);
    }

    public C2SReset(final IComponentType component, final IItem item) {
        this(component);

        this.item = item.toString();
    }

    public C2SReset(final IComponentType component, final IItem item, final Category category) {
        this(component, item);

        this.category = category.toString();
    }

    @Override
    public void fromBytes(final ExtendedPacketBuffer buffer) {
        super.fromBytes(buffer);

        this.item = buffer.readString();
        this.category = buffer.readString();
    }

    @Override
    @Environment(CLIENT)
    public void toBytes(final ExtendedPacketBuffer buffer) {
        super.toBytes(buffer);

        buffer.writeString(this.item);
        buffer.writeString(this.category);
    }

    public static final class Handler implements IExtendedMessageHandler<C2SReset> {
        @Override
        public IExtendedMessage onMessage(final C2SReset message, final MessageContext context) {
            final ISoulboundComponent component = context.getServerHandler().player.getComponent(componentType, null);

            if (item != null) {
                final IItem item = IItem.get(item);

                if (item != null) {
                    if (category != null) {
                        final Category category = Category.valueOf(category);

                        if (category == ENCHANTMENT) {
                            component.resetEnchantments(item);
                        } else if (category != null) {
                            component.reset(item, category);
                        }
                    } else {
                        component.reset(item);
                    }
                }
            } else {
                component.reset();
            }

            component.sync();
            component.refresh();

            return null;
        }
    }
}
