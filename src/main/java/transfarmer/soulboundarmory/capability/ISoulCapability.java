package transfarmer.soulboundarmory.capability;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import transfarmer.soulboundarmory.data.IAttribute;
import transfarmer.soulboundarmory.data.IDatum;
import transfarmer.soulboundarmory.data.IEnchantment;
import transfarmer.soulboundarmory.data.IType;

import java.util.Map;

public interface ISoulCapability {
    void setStatistics(int[][] data, float[][] attributes, int[][] enchantments);

    int[][] getData();

    float[][] getAttributes();

    int[][] getEnchantments();

    void setData(int[][] data);

    void setAttributes(float[][] attributes);

    void setEnchantments(int[][] enchantments);

    IType getCurrentType();

    void setCurrentType(IType type);

    void setCurrentType(int index);

    IDatum getEnumXP();

    IDatum getEnumLevel();

    IDatum getEnumAttributePoints();

    IDatum getEnumEnchantmentPoints();

    IDatum getEnumSpentAttributePoints();

    IDatum getEnumSpentEnchantmentPoints();

    IDatum getEnumSkills();

    int getItemAmount();

    int getDatumAmount();

    int getAttributeAmount();

    int getEnchantmentAmount();

    int getDatum(IDatum datum, IType type);

    void setDatum(int amount, IDatum datum, IType type);

    boolean addDatum(int amount, IDatum datum, IType type);

    float getAttribute(IAttribute attribute, IType type);

    void setAttribute(float value, IAttribute attribute, IType type);

    void addAttribute(int amount, IAttribute attribute, IType toolType);

    void setData(int[] data, IType type);

    void setAttributes(float[] attributes, IType type);

    int getEnchantment(IEnchantment enchantment, IType type);

    void setEnchantments(int[] enchantments, IType type);

    void addEnchantment(int amount, IEnchantment enchantment, IType toolType);

    int getNextLevelXP(IType weaponType);

    int getCurrentTab();

    void setCurrentTab(int currentTab);

    int getBoundSlot();

    void bindSlot(int boundSlot);

    void unbindSlot();

    Map<IEnchantment, Integer> getEnchantments(IType type);

    AttributeModifier[] getAttributeModifiers(IType type);

    ItemStack getItemStack(IType type);

    ItemStack getItemStack(ItemStack itemStack);

    String[] getTooltip(IType type);
}
