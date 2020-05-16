package transfarmer.soulboundarmory;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import nerdhub.cardinal.components.api.event.ItemComponentCallback;
import nerdhub.cardinal.components.api.util.EntityComponents;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.EntityFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.Projectile;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import transfarmer.soulboundarmory.command.SoulboundArmoryCommand;
import transfarmer.soulboundarmory.component.config.ConfigComponent;
import transfarmer.soulboundarmory.component.config.IConfigComponent;
import transfarmer.soulboundarmory.component.entity.EntityData;
import transfarmer.soulboundarmory.component.entity.IEntityData;
import transfarmer.soulboundarmory.component.soulbound.item.DaggerComponent;
import transfarmer.soulboundarmory.component.soulbound.item.GreatswordComponent;
import transfarmer.soulboundarmory.component.soulbound.item.PickComponent;
import transfarmer.soulboundarmory.component.soulbound.item.StaffComponent;
import transfarmer.soulboundarmory.component.soulbound.item.SwordComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.IToolComponent;
import transfarmer.soulboundarmory.component.soulbound.tool.ToolComponent;
import transfarmer.soulboundarmory.component.soulbound.weapon.IWeaponComponent;
import transfarmer.soulboundarmory.component.soulbound.weapon.WeaponComponent;
import transfarmer.soulboundarmory.config.ClientConfig;
import transfarmer.soulboundarmory.config.MainConfig;
import transfarmer.soulboundarmory.enchantment.ImpactEnchantment;
import transfarmer.soulboundarmory.entity.ReachModifierEntity;
import transfarmer.soulboundarmory.entity.SoulboundDaggerEntity;
import transfarmer.soulboundarmory.entity.SoulboundFireballEntity;
import transfarmer.soulboundarmory.item.SoulboundDaggerItem;
import transfarmer.soulboundarmory.item.SoulboundGreatswordItem;
import transfarmer.soulboundarmory.item.SoulboundPickItem;
import transfarmer.soulboundarmory.item.SoulboundStaffItem;
import transfarmer.soulboundarmory.item.SoulboundSwordItem;
import transfarmer.soulboundarmory.network.C2S.C2SAttribute;
import transfarmer.soulboundarmory.network.C2S.C2SBindSlot;
import transfarmer.soulboundarmory.network.C2S.C2SConfig;
import transfarmer.soulboundarmory.network.C2S.C2SEnchant;
import transfarmer.soulboundarmory.network.C2S.C2SItemType;

import java.util.UUID;

import static transfarmer.soulboundarmory.network.Packets.C2S_ATTRIBUTE;
import static transfarmer.soulboundarmory.network.Packets.C2S_BIND_SLOT;
import static transfarmer.soulboundarmory.network.Packets.C2S_CONFIG;
import static transfarmer.soulboundarmory.network.Packets.C2S_ENCHANT;
import static transfarmer.soulboundarmory.network.Packets.C2S_ITEM_TYPE;

public class Main implements ModInitializer {
    public static final String MOD_ID = "soulboundarmory";
    public static final String MOD_NAME = "soulbound armory";
    public static final String VERSION = "3.0.0";

    public static final SoulboundDaggerItem SOULBOUND_DAGGER_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_dagger"), new SoulboundDaggerItem());
    public static final SoulboundSwordItem SOULBOUND_SWORD_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_sword"), new SoulboundSwordItem());
    public static final SoulboundGreatswordItem SOULBOUND_GREATSWORD_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_greatsword"), new SoulboundGreatswordItem());
    public static final SoulboundStaffItem SOULBOUND_STAFF_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_staff"), new SoulboundStaffItem());
    public static final SoulboundPickItem SOULBOUND_PICK_ITEM = Registry.register(Registry.ITEM, new Identifier(MOD_ID, "soulbound_pick"), new SoulboundPickItem());

    public static final ComponentType<IConfigComponent> CONFIG = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "config_component"), IConfigComponent.class).attach(EntityComponentCallback.event(PlayerEntity.class), ConfigComponent::new);

    public static final ComponentType<IEntityData> ENTITY_DATA = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "entity_data"), IEntityData.class).attach(EntityComponentCallback.event(Entity.class), (final Entity entity) ->
            (entity instanceof LivingEntity || entity instanceof Projectile)
                    && !(entity instanceof ReachModifierEntity || entity instanceof SoulboundDaggerEntity)
                    ? new EntityData(entity) : null
    );
    public static final ComponentType<IToolComponent> TOOLS = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "tools"), IToolComponent.class).attach(EntityComponentCallback.event(PlayerEntity.class), ToolComponent::new);
    public static final ComponentType<IWeaponComponent> WEAPONS = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "weapons"), IWeaponComponent.class).attach(EntityComponentCallback.event(PlayerEntity.class), WeaponComponent::new);
    public static final ComponentType<DaggerComponent> DAGGER_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "item"), DaggerComponent.class).attach(ItemComponentCallback.event(SOULBOUND_DAGGER_ITEM), DaggerComponent::new);
    public static final ComponentType<SwordComponent> SWORD_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "item"), SwordComponent.class).attach(ItemComponentCallback.event(SOULBOUND_SWORD_ITEM), SwordComponent::new);
    public static final ComponentType<GreatswordComponent> GREATSWORD_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "item"), GreatswordComponent.class).attach(ItemComponentCallback.event(SOULBOUND_GREATSWORD_ITEM), GreatswordComponent::new);
    public static final ComponentType<StaffComponent> STAFF_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "item"), StaffComponent.class).attach(ItemComponentCallback.event(SOULBOUND_STAFF_ITEM), StaffComponent::new);
    public static final ComponentType<PickComponent> PICK_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(MOD_ID, "item"), PickComponent.class).attach(ItemComponentCallback.event(SOULBOUND_PICK_ITEM), PickComponent::new);

    public static final EntityType<SoulboundDaggerEntity> SOULBOUND_DAGGER_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "dagger"), FabricEntityTypeBuilder.create(EntityCategory.MISC, (EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new).dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<SoulboundFireballEntity> SOULBOUND_FIREBALL_ENTITY = Registry.register(Registry.ENTITY_TYPE, new Identifier(MOD_ID, "fireball"), FabricEntityTypeBuilder.create(EntityCategory.MISC, (EntityFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new).dimensions(EntityDimensions.fixed(1, 1)).build());

    public static final Enchantment IMPACT = Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "impact"), new ImpactEnchantment());

    public static final UUID ATTACK_RANGE_MODIFIER_UUID = UUID.fromString("F136C871-E55A-4DB5-A8FE-8EA49D9B5B81");
    public static final UUID REACH_MODIFIER_UUID = UUID.fromString("2D4AA65A-4A15-4C46-9F6B-D3898AEC42B6");

    public static final ServerSidePacketRegistry PACKET_REGISTRY = ServerSidePacketRegistry.INSTANCE;

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public void onInitialize() {
        EntityComponents.setRespawnCopyStrategy(CONFIG, RespawnCopyStrategy.ALWAYS_COPY);
        EntityComponents.setRespawnCopyStrategy(TOOLS, RespawnCopyStrategy.ALWAYS_COPY);

        PACKET_REGISTRY.register(C2S_ATTRIBUTE, new C2SAttribute());
        PACKET_REGISTRY.register(C2S_BIND_SLOT, new C2SBindSlot());
        PACKET_REGISTRY.register(C2S_CONFIG, new C2SConfig());
        PACKET_REGISTRY.register(C2S_ENCHANT, new C2SEnchant());
        PACKET_REGISTRY.register(C2S_ITEM_TYPE, new C2SItemType());

        CommandRegistrationCallback.EVENT.register(SoulboundArmoryCommand::register);

        AutoConfig.register(MainConfig.class, Toml4jConfigSerializer::new);
        AutoConfig.register(ClientConfig.class, Toml4jConfigSerializer::new);

        MainConfig.instance().load();
        MainConfig.instance().update();
        MainConfig.instance().save();
    }
}
