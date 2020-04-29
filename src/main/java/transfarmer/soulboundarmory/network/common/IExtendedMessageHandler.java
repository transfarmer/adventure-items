package transfarmer.soulboundarmory.network.common;

import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public interface IExtendedMessageHandler<P extends IExtendedMessage> extends IMessageHandler<P, IExtendedMessage> {
}
