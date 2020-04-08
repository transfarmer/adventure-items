package transfarmer.soulboundarmory.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.SideOnly;
import transfarmer.soulboundarmory.config.MainConfig;

import static net.minecraftforge.fml.relauncher.Side.CLIENT;

public class CConfig implements IMessage {
    private boolean levelupNotifications;
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
    private float passiveMultiplier;

    public CConfig() {
        this.levelupNotifications = MainConfig.instance().getLevelupNotifications();
        this.passiveMultiplier = MainConfig.instance().getPassiveMultiplier();
        this.addToOffhand = MainConfig.instance().getAddToOffhand();
        this.initialWeaponXP = MainConfig.instance().getInitialWeaponXP();
        this.initialToolXP = MainConfig.instance().getInitialToolXP();
        this.levelsPerSkill = MainConfig.instance().getLevelsPerSkill();
        this.levelsPerEnchantment = MainConfig.instance().getLevelsPerEnchantment();
        this.preservationLevel = MainConfig.instance().getPreservationLevel();
        this.maxLevel = MainConfig.instance().getMaxLevel();
        this.attackDamageMultiplier = MainConfig.instance().getAttackDamageMultiplier();
        this.difficultyMultiplier = MainConfig.instance().getDifficultyMultiplier();
        this.bossMultiplier = MainConfig.instance().getBossMultiplier();
        this.hardcoreMultiplier = MainConfig.instance().getHardcoreMultiplier();
        this.babyMultiplier = MainConfig.instance().getBabyMultiplier();
    }

    @SideOnly(CLIENT)
    @Override
    public void fromBytes(final ByteBuf buffer) {
        this.addToOffhand = buffer.readBoolean();
        this.levelupNotifications = buffer.readBoolean();
        this.initialToolXP = buffer.readInt();
        this.initialWeaponXP = buffer.readInt();
        this.levelsPerEnchantment = buffer.readInt();
        this.levelsPerSkill = buffer.readInt();
        this.maxLevel = buffer.readInt();
        this.preservationLevel = buffer.readInt();
        this.attackDamageMultiplier = buffer.readFloat();
        this.babyMultiplier = buffer.readFloat();
        this.bossMultiplier = buffer.readFloat();
        this.difficultyMultiplier = buffer.readFloat();
        this.hardcoreMultiplier = buffer.readFloat();
        this.passiveMultiplier = buffer.readFloat();
    }

    @Override
    public void toBytes(final ByteBuf buffer) {
        buffer.writeBoolean(this.addToOffhand);
        buffer.writeBoolean(this.levelupNotifications);
        buffer.writeInt(this.initialToolXP);
        buffer.writeInt(this.initialWeaponXP);
        buffer.writeInt(this.levelsPerEnchantment);
        buffer.writeInt(this.levelsPerSkill);
        buffer.writeInt(this.maxLevel);
        buffer.writeInt(this.preservationLevel);
        buffer.writeFloat(this.attackDamageMultiplier);
        buffer.writeFloat(this.babyMultiplier);
        buffer.writeFloat(this.bossMultiplier);
        buffer.writeFloat(this.difficultyMultiplier);
        buffer.writeFloat(this.hardcoreMultiplier);
        buffer.writeFloat(this.passiveMultiplier);
    }

    public static final class Handler implements IMessageHandler<CConfig, IMessage> {
        @SideOnly(CLIENT)
        @Override
        public IMessage onMessage(final CConfig message, final MessageContext context) {
            final Minecraft minecraft = Minecraft.getMinecraft();

            minecraft.addScheduledTask(() -> {
                MainConfig.instance().setAddToOffhand(message.addToOffhand);
                MainConfig.instance().setLevelupNotifications(message.levelupNotifications);
                MainConfig.instance().setInitialToolXP(message.initialToolXP);
                MainConfig.instance().setInitialWeaponXP(message.initialWeaponXP);
                MainConfig.instance().setLevelsPerEnchantment(message.levelsPerEnchantment);
                MainConfig.instance().setLevelsPerSkill(message.levelsPerSkill);
                MainConfig.instance().setMaxLevel(message.maxLevel);
                MainConfig.instance().setPreservationLevel(message.preservationLevel);
                MainConfig.instance().setAttackDamageMultiplier(message.attackDamageMultiplier);
                MainConfig.instance().setBabyMultiplier(message.babyMultiplier);
                MainConfig.instance().setBossMultiplier(message.bossMultiplier);
                MainConfig.instance().setDifficultyMultiplier(message.difficultyMultiplier);
                MainConfig.instance().setHardcoreMultiplier(message.hardcoreMultiplier);
                MainConfig.instance().setPassiveMultiplier(message.passiveMultiplier);
            });

            return null;
        }
    }
}
