package transfarmer.soulweapons.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulweapons.entity.EntitySoulLightningBolt;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;
import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;

public class CLightning implements IMessage {
    private Vec3d hitVec;

    public CLightning() {
    }

    public CLightning(final Vec3d hitVec) {
        this.hitVec = hitVec;
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.hitVec = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeDouble(this.hitVec.x);
        buffer.writeDouble(this.hitVec.y);
        buffer.writeDouble(this.hitVec.z);
    }

    public final static class Handler implements IMessageHandler<CLightning, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CLightning message, final MessageContext context) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                final EntityPlayer player = Minecraft.getMinecraft().player;

                player.world.addWeatherEffect(new EntitySoulLightningBolt(player.world, message.hitVec, player));
                player.getCapability(CAPABILITY, null).resetLightningCooldown();
            });

            return null;
        }
    }
}
