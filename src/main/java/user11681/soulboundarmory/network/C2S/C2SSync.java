package user11681.soulboundarmory.network.C2S;

//public class C2SSync implements IExtendedMessage {
//    public static final class Handler implements IExtendedMessageHandler<C2SSync> {
//        @Override
//        public IExtendedMessage onMessage(final C2SSync message, final MessageContext context) {
//            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
//                final ISoulboundItemComponent component = context.getServerHandler().player.getComponent(IComponentType.get(component).getComponent(), null);
//
//                if (tag.hasKey("tab")) {
//                    component.setCurrentTab(tag.getInt("tab"));
//                }
//
//                if (component instanceof IWeaponComponent && tag.hasKey("spell")) {
//                    ((IWeaponComponent) component).setSpell((tag.getInt("spell")));
//                }
//            });
//
//            return null;
//        }
//    }
//}
