package user11681.soulboundarmory.component;

import java.util.ArrayList;
import java.util.List;
import nerdhub.cardinal.components.api.ComponentRegistry;
import nerdhub.cardinal.components.api.ComponentType;
import nerdhub.cardinal.components.api.event.EntityComponentCallback;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import user11681.soulboundarmory.Main;
import user11681.soulboundarmory.component.config.ConfigComponent;
import user11681.soulboundarmory.component.config.IConfigComponent;
import user11681.soulboundarmory.component.entity.EntityData;
import user11681.soulboundarmory.component.entity.IEntityData;
import user11681.soulboundarmory.component.soulbound.player.SoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.ToolSoulboundComponent;
import user11681.soulboundarmory.component.soulbound.player.WeaponSoulboundComponent;

public class Components {
    public static final List<ComponentType<? extends SoulboundComponent>> SOULBOUND_COMPONENTS = new ArrayList<>();

    public static final ComponentType<IConfigComponent> CONFIG_COMPONENT = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(Main.MOD_ID, "config_component"), IConfigComponent.class);
    public static final ComponentType<IEntityData> ENTITY_DATA = ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(Main.MOD_ID, "entity_data"), IEntityData.class);
    public static final ComponentType<SoulboundComponent> WEAPON_COMPONENT = register(ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(Main.MOD_ID, "weapon_component"), SoulboundComponent.class));
    public static final ComponentType<SoulboundComponent> TOOL_COMPONENT = register(ComponentRegistry.INSTANCE.registerIfAbsent(new Identifier(Main.MOD_ID, "tool_component"), SoulboundComponent.class));

    public static <T extends ComponentType<? extends SoulboundComponent>> T register(final T componentType) {
        SOULBOUND_COMPONENTS.add(componentType);

        return componentType;
    }

    public static void attach() {
        CONFIG_COMPONENT.attach(EntityComponentCallback.event(PlayerEntity.class), ConfigComponent::new);
        ENTITY_DATA.attach(EntityComponentCallback.event(Entity.class), EntityData::new);
        WEAPON_COMPONENT.attach(EntityComponentCallback.event(PlayerEntity.class), WeaponSoulboundComponent::new);
        TOOL_COMPONENT.attach(EntityComponentCallback.event(PlayerEntity.class), ToolSoulboundComponent::new);
    }
}
