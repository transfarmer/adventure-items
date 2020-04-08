package transfarmer.soulboundarmory.statistics.v2.statistics;


import java.util.HashMap;
import java.util.Map;

public class SoulboundStatistics {
    private final Map<String, Map<String, Map<String, Statistic>>> statistics;

    public SoulboundStatistics(final String[] itemNames, final String[] statisticTypeNames, final String[][] statisticNames, final double[][] min) {
        this.statistics = new HashMap<>(itemNames.length, 1);

        for (final String itemName : itemNames) {
            final Map<String, Map<String, Statistic>> statisticTypes = new HashMap<>(statisticTypeNames.length, 1);

            statistics.put(itemName, statisticTypes);

            for (int j = 0; j < statisticTypeNames.length; j++) {
                final Map<String, Statistic> statisticSubtypes = new HashMap<>();

                statisticTypes.put(statisticTypeNames[j], statisticSubtypes);

                final String[] names = statisticNames[j];
                final double[] subtypeMin = min[j];

                for (int k = 0; k < statisticNames.length; k++) {
                    statisticSubtypes.put(names[k], new Statistic(names[k], statisticTypeNames[j], subtypeMin[k]));
                }
            }
        }
    }

    public Map<String, Map<String, Statistic>> getItem(final String name) {
        return this.statistics.get(name);
    }

    public Statistic get(final String itemName, final String statisticName) {
        final Map<String, Map<String, Statistic>> item = this.getItem(itemName);
        Statistic statistic = null;

        for (final Map<String, Statistic> subtype : item.values()) {
            statistic = subtype.get(statisticName);

            if (statistic != null) {
                break;
            }
        }

        return statistic;
    }

    public void set(final String item, final String type, final String name, final Number value) {
        if (value instanceof Statistic) {
            this.statistics.get(item).get(type).put(name, (Statistic) value);
        } else {
            this.statistics.get(item).get(type).get(name).set(value);
        }
    }

    public void set(final String itemName, final Map<String, Map<String, Statistic>> itemMap) {
        this.statistics.put(itemName, itemMap);
    }

    public void add(final String item, final String statisticName, final Number value) {
        final Map<String, Map<String, Statistic>> itemMap = this.statistics.get(item);
        final Statistic statistic = this.get(item, statisticName);

        itemMap.get(statistic.getType()).put(statisticName, statistic.add(value));
    }

    public Map<String, Map<String, Map<String, Statistic>>> get() {
        return this.statistics;
    }
}
