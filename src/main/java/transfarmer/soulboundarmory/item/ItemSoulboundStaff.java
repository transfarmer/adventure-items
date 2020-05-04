package transfarmer.soulboundarmory.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import transfarmer.soulboundarmory.Main;
import transfarmer.soulboundarmory.capability.soulbound.weapon.IWeaponCapability;
import transfarmer.soulboundarmory.capability.soulbound.weapon.WeaponProvider;
import transfarmer.soulboundarmory.entity.EntitySoulboundSmallFireball;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;
import static net.minecraftforge.common.util.Constants.AttributeModifierOperation.ADD;

public class ItemSoulboundStaff extends ItemStaff implements SoulboundWeapon {
    public ItemSoulboundStaff(final String name) {
        super();

        this.setRegistryName(Main.MOD_ID, name);
        this.setTranslationKey(String.format("%s.%s", Main.MOD_ID, name));
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean onLeftClickEntity(@NotNull final ItemStack stack, @NotNull final EntityPlayer player, @NotNull final Entity entity) {
        return true;
    }

    @Override
    @NotNull
    public ActionResult<ItemStack> onItemRightClick(@NotNull final World world, @NotNull final EntityPlayer player,
                                                    @NotNull final EnumHand hand) {
        if (!world.isRemote) {
            final IWeaponCapability capability = WeaponProvider.get(player);

            if (capability.getFireballCooldown() <= 0) {
                world.spawnEntity(new EntitySoulboundSmallFireball(world, player, capability.getSpell()));

                if (!player.isCreative()) {
                    capability.resetFireballCooldown();
                }
            }
        }

        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @NotNull
    public Multimap<String, AttributeModifier> getAttributeModifiers(@NotNull final EntityEquipmentSlot slot,
                                                                     @NotNull final ItemStack itemStack) {
        final Multimap<String, AttributeModifier> modifiers = HashMultimap.create();

        if (slot == MAINHAND) {
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ItemSoulbound.ATTACK_SPEED_MODIFIER, "generic.attackSpeed", 0, ADD));
            modifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ItemSoulbound.ATTACK_DAMAGE_MODIFIER, "generic.attackDamage", 0, ADD));
        }

        return modifiers;
    }
}
