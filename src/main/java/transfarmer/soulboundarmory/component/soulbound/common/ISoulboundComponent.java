package transfarmer.soulboundarmory.component.soulbound.common;

import nerdhub.cardinal.components.api.util.sync.EntitySyncedComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.statistics.IItem;

import java.util.List;

public interface ISoulboundComponent extends EntitySyncedComponent {
    IItem getItemType(int index);

    IItem getItemType(ItemStack itemStack);

    IItem getItemType(Item item);

    IItem getItemType(String item);

    IItem getItemType();

    void setItemType(IItem type);

    void setItemType(int index);

    Item getItem();

    Item getItem(IItem item);

    int getIndex(IItem item);

    int getIndex();

    int getCurrentTab();

    void setCurrentTab(int currentTab);

    int getBoundSlot();

    void bindSlot(int boundSlot);

    void unbindSlot();

    void refresh();

    void openGUI();

    void openGUI(int tab);

    List<ScreenTab> getTabs();

    boolean hasSoulboundItem();

    ItemStack getEquippedItemStack();

    void onTick();

    CompoundTag toClientTag();

    void sync();
}
