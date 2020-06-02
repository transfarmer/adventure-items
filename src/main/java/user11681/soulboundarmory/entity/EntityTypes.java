package user11681.soulboundarmory.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import user11681.soulboundarmory.Main;

public enum EntityTypes {
    ;
    public static final EntityType<SoulboundDaggerEntity> SOULBOUND_DAGGER_ENTITY = Registry
            .register(Registry.ENTITY_TYPE, new Identifier(Main.MOD_ID, "dagger"), FabricEntityTypeBuilder
                    .create(EntityCategory.MISC, (EntityType.EntityFactory<SoulboundDaggerEntity>) SoulboundDaggerEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
    public static final EntityType<SoulboundFireballEntity> SOULBOUND_FIREBALL_ENTITY = Registry
            .register(Registry.ENTITY_TYPE, new Identifier(Main.MOD_ID, "fireball"), FabricEntityTypeBuilder
                    .create(EntityCategory.MISC, (EntityType.EntityFactory<SoulboundFireballEntity>) SoulboundFireballEntity::new)
                    .dimensions(EntityDimensions.fixed(1, 1)).build());
}
