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
        this.levelupNotifications = MainConfig.instance().getLevelupNotifications();
        this.passiveXP = MainConfig.instance().getPassiveXP();
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
                MainConfig.instance().setLevelupNotifications(message.levelupNotifications);
                MainConfig.instance().setPassiveXP(message.passiveXP);
                MainConfig.instance().setAddToOffhand(message.addToOffhand);
                MainConfig.instance().setInitialWeaponXP(message.initialWeaponXP);
                MainConfig.instance().setInitialToolXP(message.initialToolXP);
                MainConfig.instance().setLevelsPerSkill(message.levelsPerSkill);
                MainConfig.instance().setLevelsPerEnchantment(message.levelsPerEnchantment);
                MainConfig.instance().setPreservationLevel(message.preservationLevel);
                MainConfig.instance().setMaxLevel(message.maxLevel);
                MainConfig.instance().setAttackDamageMultiplier(message.attackDamageMultiplier);
                MainConfig.instance().setDifficultyMultiplier(message.difficultyMultiplier);
                MainConfig.instance().setBossMultiplier(message.bossMultiplier);
                MainConfig.instance().setHardcoreMultiplier(message.hardcoreMultiplier);
                MainConfig.instance().setBabyMultiplier(message.babyMultiplier);
            });

            return null;
        }
    }
}
