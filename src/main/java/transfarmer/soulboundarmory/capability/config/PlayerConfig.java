package transfarmer.soulboundarmory.capability.config;

public class PlayerConfig implements IPlayerConfig {
    private boolean addToOffhand;

    @Override
    public boolean getAddToOffhand() {
        return this.addToOffhand;
    }

    @Override
    public void setAddToOffhand(final boolean addToOffhand) {
        this.addToOffhand = addToOffhand;
    }
}
