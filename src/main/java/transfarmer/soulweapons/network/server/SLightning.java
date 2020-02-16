package transfarmer.soulweapons.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import transfarmer.soulweapons.capability.ISoulWeapon;
import transfarmer.soulweapons.entity.EntitySoulLightningBolt;
import transfarmer.soulweapons.network.client.CLightning;

import static transfarmer.soulweapons.capability.SoulWeaponProvider.CAPABILITY;
import static transfarmer.soulweapons.data.SoulWeaponDatum.SKILLS;
import static transfarmer.soulweapons.data.SoulWeaponType.SWORD;

public class SLightning implements IMessage {
    private Vec3d hitVec;

    public SLightning() {
    }

    public SLightning(final RayTraceResult result) {
        if (result != null) {
            this.hitVec = result.hitVec;
        }
    }

    @Override
    public void fromBytes(final ByteBuf buffer) {
        if (buffer.isReadable()) {
            this.hitVec = new Vec3d(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        if (this.hitVec != null) {
            buffer.writeDouble(this.hitVec.x);
            buffer.writeDouble(this.hitVec.y);
            buffer.writeDouble(this.hitVec.z);
        }
    }

    public final static class Handler implements IMessageHandler<SLightning, IMessage> {
        IMessage packet = null;

        @Override
        public IMessage onMessage(final SLightning message, final MessageContext context) {
            final EntityPlayer player = context.getServerHandler().player;
            final ISoulWeapon capability = player.getCapability(CAPABILITY, null);

            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                if (message.hitVec != null && capability.getDatum(SKILLS, SWORD) >= 1 && capability.getLightningCooldown() <= 0) {
                    player.world.addWeatherEffect(new EntitySoulLightningBolt(player.world, message.hitVec, player));
                    capability.resetLightningCooldown();

                    this.packet = new CLightning(message.hitVec);
                }
            });

            return this.packet;
        }
    }
}
