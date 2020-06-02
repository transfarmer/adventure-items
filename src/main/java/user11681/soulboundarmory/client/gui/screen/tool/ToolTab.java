package user11681.soulboundarmory.client.gui.screen.tool;

import user11681.soulboundarmory.client.gui.screen.StorageTab;
import user11681.soulboundarmory.component.soulbound.item.tool.ToolStorage;

public interface ToolTab extends StorageTab {
    default ToolStorage<?> getToolStorage() {
        return (ToolStorage<?>) this.getStorage();
    }
}
