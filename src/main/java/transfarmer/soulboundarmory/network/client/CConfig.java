package transfarmer.soulboundarmory.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.Configuration;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CConfig implements IMessage {
    private boolean levelupNotifications;
    private boolean passiveXP;
    private boolean addToOffhand;
    private int initialWeaponXP;
    private int initialToolXP;
    private int levelsPerSkill;
    private int levelsPerEnchantment;
    private int preservationLevel;
    private int maxLevel;
    private float attackDamageMultiplier;
    private float difficultyMultiplier;
    private float bossMultiplier;
    private float hardcoreMultiplier;
    private float babyMultiplier;

    public CConfig() {
        this.levelupNotifications = Configuration.levelupNotifications;
        this.passiveXP = Configuration.passiveXP;
        this.addToOffhand = Configuration.addToOffhand;
        this.initialWeaponXP = Configuration.initialWeaponXP;
        this.initialToolXP = Configuration.initialToolXP;
        this.levelsPerSkill = Configuration.levelsPerSkill;
        this.levelsPerEnchantment = Configuration.levelsPerEnchantment;
        this.preservationLevel = Configuration.preservationLevel;
        this.maxLevel = Configuration.maxLevel;
        this.attackDamageMultiplier = Configuration.multipliers.attackDamageMultiplier;
        this.difficultyMultiplier = Configuration.multipliers.difficultyMultiplier;
        this.bossMultiplier = Configuration.multipliers.bossMultiplier;
        this.hardcoreMultiplier = Configuration.multipliers.hardcoreMultiplier;
        this.babyMultiplier = Configuration.multipliers.babyMultiplier;
    }

    @SideOnly(CLIENT)
    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.levelupNotifications = buffer.readBoolean();
        this.passiveXP = buffer.readBoolean();
        this.addToOffhand = buffer.readBoolean();
        this.initialWeaponXP = buffer.readInt();
        this.initialToolXP = buffer.readInt();
        this.levelsPerSkill = buffer.readInt();
        this.levelsPerEnchantment = buffer.readInt();
        this.preservationLevel = buffer.readInt();
        this.maxLevel = buffer.readInt();
        this.attackDamageMultiplier = buffer.readFloat();
        this.difficultyMultiplier = buffer.readFloat();
        this.bossMultiplier = buffer.readFloat();
        this.hardcoreMultiplier = buffer.readFloat();
        this.babyMultiplier = buffer.readFloat();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeBoolean(this.levelupNotifications);
        buffer.writeBoolean(this.passiveXP);
        buffer.writeBoolean(this.addToOffhand);
        buffer.writeInt(this.initialWeaponXP);
        buffer.writeInt(this.initialToolXP);
        buffer.writeInt(this.levelsPerSkill);
        buffer.writeInt(this.levelsPerEnchantment);
        buffer.writeInt(this.preservationLevel);
        buffer.writeInt(this.maxLevel);
        buffer.writeFloat(this.attackDamageMultiplier);
        buffer.writeFloat(this.difficultyMultiplier);
        buffer.writeFloat(this.bossMultiplier);
        buffer.writeFloat(this.hardcoreMultiplier);
        buffer.writeFloat(this.babyMultiplier);
    }

    public static final class Handler implements IMessageHandler<CConfig, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CConfig message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> {
                Configuration.levelupNotifications = message.levelupNotifications;
                Configuration.passiveXP = message.passiveXP;
                Configuration.addToOffhand = message.addToOffhand;
                Configuration.initialWeaponXP = message.initialWeaponXP;
                Configuration.initialToolXP = message.initialToolXP;
                Configuration.levelsPerSkill = message.levelsPerSkill;
                Configuration.levelsPerEnchantment = message.levelsPerEnchantment;
                Configuration.preservationLevel = message.preservationLevel;
                Configuration.maxLevel = message.maxLevel;
                Configuration.multipliers.attackDamageMultiplier = message.attackDamageMultiplier;
                Configuration.multipliers.difficultyMultiplier = message.difficultyMultiplier;
                Configuration.multipliers.bossMultiplier = message.bossMultiplier;
                Configuration.multipliers.hardcoreMultiplier = message.hardcoreMultiplier;
                Configuration.multipliers.babyMultiplier = message.babyMultiplier;
            });

            return null;
        }
    }
}
