package user11681.soulboundarmory.network.common;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import user11681.soulboundarmory.component.soulbound.item.ItemStorage;
import user11681.usersmanual.entity.EntityUtil;

public class ExtendedPacketBuffer extends PacketByteBuf {
    public ExtendedPacketBuffer(final ItemStorage<?> component) {
        this();

        this.writeIdentifier(component.getType().getIdentifier());
    }

    public ExtendedPacketBuffer() {
        this(Unpooled.buffer());
    }

    public ExtendedPacketBuffer(final ByteBuf wrapped) {
        super(wrapped);
    }

    @Override
    public ExtendedPacketBuffer writeIdentifier(final Identifier identifier) {
        super.writeIdentifier(identifier);

        return this;
    }

    public String readString() {
        return super.readString(1 << 10);
    }

    @Override
    public ExtendedPacketBuffer writeString(final String string) {
        super.writeString(string);

        return this;
    }

    @Override
    @Nonnull
    public ExtendedPacketBuffer writeItemStack(@Nonnull final ItemStack itemStack) {
        return (ExtendedPacketBuffer) super.writeItemStack(itemStack);
    }

    @Override
    @Nonnull
    public ExtendedPacketBuffer writeUuid(@Nonnull final UUID id) {
        return (ExtendedPacketBuffer) super.writeUuid(id);
    }

    @Override
    @Nonnull
    public ExtendedPacketBuffer writeInt(final int value) {
        super.writeInt(value);

        return this;
    }

    public ExtendedPacketBuffer writeEntity(final Entity entity) {
        return this.writeUuid(entity.getUuid());
    }

    public Entity readEntity() {
        return EntityUtil.getEntity(this.readUuid());
    }

    public ExtendedPacketBuffer writePlayer(final Entity player) {
        return this.writeEntity(player);
    }

    public PlayerEntity readPlayer() {
        return (PlayerEntity) this.readEntity();
    }
}
