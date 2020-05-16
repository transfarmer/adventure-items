package transfarmer.soulboundarmory.component.soulbound.item;

public interface ISwordComponent {
    int getLightningCooldown();

    void setLightningCooldown(int ticks);

    void resetLightningCooldown();
}
