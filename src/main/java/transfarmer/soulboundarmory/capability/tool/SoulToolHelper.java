package transfarmer.soulboundarmory.capability.tool;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import transfarmer.soulboundarmory.item.IItemSoulTool;

import java.util.UUID;

import static net.minecraft.inventory.EntityEquipmentSlot.MAINHAND;

public class SoulToolHelper {
    public static final UUID REACH_DISTANCE_UUID = UUID.fromString("CD407CC4-2214-4ECA-B4B6-7DCEE2DABA33");
    private static boolean datumEquality;

    public static boolean isSoulTool(final ItemStack itemStack) {
        return itemStack.getItem() instanceof IItemSoulTool;
    }

    public static boolean isSoulToolEquipped(final EntityPlayer player) {
        return player.getHeldItemMainhand().getItem() instanceof IItemSoulTool
                || player.getHeldItemOffhand().getItem() instanceof IItemSoulTool;
    }

    public static boolean hasSoulTool(final EntityPlayer player) {
        for (final ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack.getItem() instanceof IItemSoulTool) return true;
        }

        return player.getHeldItemOffhand().getItem() instanceof IItemSoulTool;
    }

    public static boolean areDataEqual(ItemStack itemStack0, ItemStack itemStack1) {
        datumEquality = true;

        itemStack0.getAttributeModifiers(MAINHAND).forEach((String key, AttributeModifier modifier0) -> {
            if (!datumEquality) return;

            itemStack1.getAttributeModifiers(MAINHAND).get(key).forEach((AttributeModifier modifier1) -> {
                if (!datumEquality) return;

                if (!modifier0.getID().equals(modifier1.getID()) || modifier0.getAmount() != modifier1.getAmount()) {
                    datumEquality = false;
                }
            });
        });

        if (datumEquality) {
            final NBTTagList enchantmentTagList0 = itemStack0.getEnchantmentTagList();
            final NBTTagList enchantmentTagList1 = itemStack1.getEnchantmentTagList();
            final int listEntries = Math.max(enchantmentTagList0.tagCount(), enchantmentTagList1.tagCount());

            for (int i = 0; i < listEntries; i++) {
                final NBTTagCompound tagCompound0 = enchantmentTagList0.getCompoundTagAt(i);
                final NBTTagCompound tagCompound1 = enchantmentTagList1.getCompoundTagAt(i);

                if (tagCompound0.getSize() == tagCompound1.getSize()) {
                    final int compoundEntries = Math.max(tagCompound0.getSize(), tagCompound1.getSize());

                    for (int j = 0; j < compoundEntries; j++) {
                        for (final String key : tagCompound1.getKeySet()) {
                            if (tagCompound0.getShort(key) != tagCompound1.getShort(key)) {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            }
        }

        return datumEquality;
    }
}
