package transfarmer.soulboundarmory.component.soulbound.weapon;

import nerdhub.cardinal.components.api.ComponentType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundTag;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.farmerlib.nbt.NBTUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.EnchantmentTab;
import transfarmer.soulboundarmory.client.gui.screen.common.ScreenTab;
import transfarmer.soulboundarmory.client.gui.screen.common.SkillsTab;
import transfarmer.soulboundarmory.client.gui.screen.weapon.WeaponAttributesTab;
import transfarmer.soulboundarmory.client.gui.screen.weapon.WeaponSelectionTab;
import transfarmer.soulboundarmory.component.entity.IEntityData;
import transfarmer.soulboundarmory.component.soulbound.common.SoulboundBase;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.item.SoulboundItem;
import transfarmer.soulboundarmory.item.SoulboundWeaponItem;
import transfarmer.soulboundarmory.statistics.IItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static transfarmer.soulboundarmory.Main.SOULBOUND_GREATSWORD_ITEM;
import static transfarmer.soulboundarmory.Main.SOULBOUND_STAFF_ITEM;
import static transfarmer.soulboundarmory.Main.SOULBOUND_SWORD_ITEM;
import static transfarmer.soulboundarmory.Main.WEAPONS;
import static transfarmer.soulboundarmory.statistics.Item.GREATSWORD;
import static transfarmer.soulboundarmory.statistics.Item.STAFF;
import static transfarmer.soulboundarmory.statistics.Item.SWORD;
import static transfarmer.soulboundarmory.statistics.StatisticType.ATTACK_SPEED;

public class WeaponComponent extends SoulboundBase implements IWeaponComponent {
    public WeaponComponent(final PlayerEntity player) {
        super(player, new IItem[]{SWORD, GREATSWORD, STAFF}, new Item[]{SOULBOUND_SWORD_ITEM, SOULBOUND_GREATSWORD_ITEM, SOULBOUND_STAFF_ITEM});

        final List<IItem> itemTypes = this.itemTypes.keyList();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public List<ScreenTab> getTabs() {
        List<ScreenTab> tabs = new ArrayList<>();
        tabs = CollectionUtil.arrayList(new WeaponSelectionTab(tabs), new WeaponAttributesTab(tabs), new EnchantmentTab(WeaponProvider.WEAPONS, tabs), new SkillsTab(WeaponProvider.WEAPONS, tabs));

        return tabs;
    }

    @Override
    public Class<? extends SoulboundItem> getBaseItemClass() {
        return SoulboundWeaponItem.class;
    }

    @Override
    public void onTick() {
        super.onTick();

        if (!this.isClient) {
            if (this.getAttackCooldown() > 0) {
                this.attackCooldown--;
            }

            if (this.fireballCooldown > 0) {
                this.fireballCooldown--;
            }

            if (this.getItemType() != null && this.getLightningCooldown() > 0) {
                this.lightningCooldown--;
            }

            if (this.getLeapDuration() > 0) {
                if (--this.leapDuration == 0) {
                    this.resetLeapForce();
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundTag toTag(@Nonnull CompoundTag tag) {
        tag = super.toTag(tag);

        tag.putInt("slot", this.getBoundSlot());
        tag.putInt("attackCooldown", this.getAttackCooldown());
        tag.putInt("leapDuration", this.getLeapDuration());
        tag.putDouble("leapForce", this.getLeapForce());
        tag.putInt("lightningCooldown", this.getLightningCooldown());
        tag.putInt("spell", this.spell);

        return tag;
    }

    @Override
    public void fromTag(@Nonnull final CompoundTag tag) {
        super.fromTag(tag);

        this.bindSlot(tag.getInt("slot"));
        this.setLeapDuration(tag.getInt("leapDuration"));
        this.setLeapForce(tag.getDouble("leapForce"));
        this.setSpell(tag.getInt("spell"));
    }

    @Override
    public CompoundTag toClientTag() {
        final CompoundTag tag = super.toClientTag();

        tag.putInt("spell", this.spell);

        return tag;
    }

    @Nonnull
    @Override
    public ComponentType<?> getComponentType() {
        return WEAPONS;
    }
}
