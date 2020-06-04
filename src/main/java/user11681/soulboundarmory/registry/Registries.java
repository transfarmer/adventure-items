package user11681.soulboundarmory.registry;

import user11681.soulboundarmory.component.soulbound.item.StorageType;
import user11681.soulboundarmory.component.statistics.Category;
import user11681.soulboundarmory.component.statistics.StatisticType;
import user11681.soulboundarmory.network.common.Packet;
import user11681.soulboundarmory.skill.Skill;
import user11681.usersmanual.registry.ModRegistry;

public class Registries {
    public static final ModRegistry<StorageType<?>> STORAGE_TYPE = new ModRegistry<>();
    public static final ModRegistry<Category> CATEGORY = new ModRegistry<>();
    public static final ModRegistry<StatisticType> STATISTIC = new ModRegistry<>();
    public static final ModRegistry<Skill> SKILL = new ModRegistry<>();
    public static final ModRegistry<Packet> PACKET = new ModRegistry<>();
}
