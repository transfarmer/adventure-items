package transfarmer.soulboundarmory.item;

import com.google.common.collect.Multimap;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.common.SoulItemHelper;
import transfarmer.soulboundarmory.entity.EntitySoulboundSmallFireball;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;

public class ItemSoulboundStaff extends Item implements SoulboundWeapon {
    public ItemSoulboundStaff(final String name) {
        super();

        this.setRegistryName(Main.MOD_ID, name);
        this.setTranslationKey(String.format("%s.%s", Main.MOD_ID, name));
    }

    @Override
    @NotNull
    public ActionResult<ItemStack> onItemRightClick(@NotNull final World world, @NotNull final EntityPlayer player,
                                                    @NotNull final EnumHand hand) {
        if (!world.isRemote) {
            final EntitySmallFireball fireball = new EntitySoulboundSmallFireball(world, player);

            world.spawnEntity(fireball);
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @NotNull
    public Multimap<String, AttributeModifier> getAttributeModifiers(@NotNull final EntityEquipmentSlot slot,
                                                                     @NotNull final ItemStack itemStack) {
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(SoulItemHelper.ATTACK_SPEED_UUID, "generic.attackSpeed", 0, ADD), MAINHAND);
        itemStack.addAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(SoulItemHelper.ATTACK_DAMAGE_UUID, "generic.attackDamage", 0, ADD), MAINHAND);

        return itemStack.getAttributeModifiers(slot);
    }
}
