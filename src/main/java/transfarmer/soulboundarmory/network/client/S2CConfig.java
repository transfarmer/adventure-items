package transfarmer.soulboundarmory.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.config.MainConfig;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class S2CConfig implements IMessage {
    private int initialWeaponXP;
    private int initialToolXP;
    private int levelsPerSkill;
    private int levelsPerEnchantment;
    private int preservationLevel;
    private int maxLevel;

    private boolean levelupNotifications;
    private boolean addToOffhand;

    private float armorMultiplier;
    private float attackDamageMultiplier;
    private float difficultyMultiplier;
    private float bossMultiplier;
    private float hardcoreMultiplier;
    private float babyMultiplier;
    private float passiveMultiplier;

    public S2CConfig() {
        final MainConfig config = MainConfig.instance();

        this.initialWeaponXP = config.getInitialWeaponXP();
        this.initialToolXP = config.getInitialToolXP();
        this.levelsPerSkill = config.getLevelsPerSkill();
        this.levelsPerEnchantment = config.getLevelsPerEnchantment();
        this.preservationLevel = config.getPreservationLevel();
        this.maxLevel = config.getMaxLevel();

        this.levelupNotifications = config.getLevelupNotifications();
        this.addToOffhand = config.getAddToOffhand();

        this.armorMultiplier = config.getArmorMultiplier();
        this.attackDamageMultiplier = config.getAttackDamageMultiplier();
        this.babyMultiplier = config.getBabyMultiplier();
        this.bossMultiplier = config.getBossMultiplier();
        this.difficultyMultiplier = config.getDifficultyMultiplier();
        this.hardcoreMultiplier = config.getHardcoreMultiplier();
        this.passiveMultiplier = config.getPassiveMultiplier();
    }

    @SideOnly(CLIENT)
    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.initialToolXP = buffer.readInt();
        this.initialWeaponXP = buffer.readInt();
        this.levelsPerEnchantment = buffer.readInt();
        this.levelsPerSkill = buffer.readInt();
        this.maxLevel = buffer.readInt();
        this.preservationLevel = buffer.readInt();

        this.addToOffhand = buffer.readBoolean();
        this.levelupNotifications = buffer.readBoolean();

        this.armorMultiplier = buffer.readFloat();
        this.attackDamageMultiplier = buffer.readFloat();
        this.babyMultiplier = buffer.readFloat();
        this.bossMultiplier = buffer.readFloat();
        this.difficultyMultiplier = buffer.readFloat();
        this.hardcoreMultiplier = buffer.readFloat();
        this.passiveMultiplier = buffer.readFloat();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.initialToolXP);
        buffer.writeInt(this.initialWeaponXP);
        buffer.writeInt(this.levelsPerEnchantment);
        buffer.writeInt(this.levelsPerSkill);
        buffer.writeInt(this.maxLevel);
        buffer.writeInt(this.preservationLevel);

        buffer.writeBoolean(this.addToOffhand);
        buffer.writeBoolean(this.levelupNotifications);

        buffer.writeFloat(this.armorMultiplier);
        buffer.writeFloat(this.attackDamageMultiplier);
        buffer.writeFloat(this.babyMultiplier);
        buffer.writeFloat(this.bossMultiplier);
        buffer.writeFloat(this.difficultyMultiplier);
        buffer.writeFloat(this.hardcoreMultiplier);
        buffer.writeFloat(this.passiveMultiplier);
    }

    public static final class Handler implements IMessageHandler<S2CConfig, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final S2CConfig message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> {
                final MainConfig config = MainConfig.instance();

                config.setInitialToolXP(message.initialToolXP);
                config.setInitialWeaponXP(message.initialWeaponXP);
                config.setLevelsPerEnchantment(message.levelsPerEnchantment);
                config.setLevelsPerSkill(message.levelsPerSkill);
                config.setMaxLevel(message.maxLevel);
                config.setPreservationLevel(message.preservationLevel);

                config.setAddToOffhand(message.addToOffhand);
                config.setLevelupNotifications(message.levelupNotifications);

                config.setArmorMultiplier(message.armorMultiplier);
                config.setAttackDamageMultiplier(message.attackDamageMultiplier);
                config.setBabyMultiplier(message.babyMultiplier);
                config.setBossMultiplier(message.bossMultiplier);
                config.setDifficultyMultiplier(message.difficultyMultiplier);
                config.setHardcoreMultiplier(message.hardcoreMultiplier);
                config.setPassiveMultiplier(message.passiveMultiplier);
            });

            return null;
        }
    }
}
