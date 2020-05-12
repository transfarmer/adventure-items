package transfarmer.soulboundarmory.network.common;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public interface IExtendedMessageHandler<P extends IExtendedMessage> extends IMessageHandler<P, IExtendedMessage> {
    @Override
    IExtendedMessage onMessage(P message, MessageContext context);
}
