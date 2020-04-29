package transfarmer.soulboundarmory.network.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.util.EntityUtil;

import java.io.IOException;
import java.util.UUID;

public class ExtendedPacketBuffer extends PacketBuffer {
    public ExtendedPacketBuffer(final ByteBuf wrapped) {
        super(wrapped);
    }

    public ExtendedPacketBuffer() {
        this(Unpooled.buffer());
    }

    public String readString() {
        return super.readString(1 << 10);
    }

    @Override
    public NBTTagCompound readCompoundTag() {
        try {
            return super.readCompoundTag();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    @NotNull
    public ExtendedPacketBuffer writeItemStack(@NotNull final ItemStack itemStack) {
        return (ExtendedPacketBuffer) super.writeItemStack(itemStack);
    }

    @Override
    @NotNull
    public ExtendedPacketBuffer writeUniqueId(@NotNull final UUID id) {
        return (ExtendedPacketBuffer) super.writeUniqueId(id);
    }

    @Override
    @NotNull
    public ExtendedPacketBuffer writeInt(final int value) {
        super.writeInt(value);

        return this;
    }

    public ExtendedPacketBuffer writeEntity(final Entity entity) {
        return this.writeUniqueId(entity.getUniqueID());
    }

    public Entity readEntity() {
        return EntityUtil.getEntity(this.readUniqueId());
    }

    public ExtendedPacketBuffer writePlayer(final Entity player) {
        return this.writeEntity(player);
    }

    public EntityPlayer readPlayer() {
        return (EntityPlayer) this.readEntity();
    }
}
