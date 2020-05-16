package transfarmer.soulboundarmory.skill;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import transfarmer.farmerlib.collection.CollectionUtil;
import transfarmer.soulboundarmory.client.gui.screen.common.ExtendedScreen;

import java.util.Arrays;
import java.util.List;

public abstract class Skill {
    protected final Identifier identifier;
    protected final List<Skill> dependencies;
    protected int maxLevel;

    public Skill(final Identifier identifier, final Skill... dependencies) {
        this(identifier, 0, dependencies);
    }

    public Skill(final Identifier identifier, final int maxLevel, final Skill... dependencies) {
        this.identifier = identifier;
        this.maxLevel = maxLevel;
        this.dependencies = CollectionUtil.arrayList(dependencies);
    }

    public List<Skill> getDependencies() {
        return this.dependencies;
    }

    public abstract int getCost(final boolean learned, final int level);

    @Environment(EnvType.CLIENT)
    public String getName() {
        final String name = I18n.translate(String.format("skill.%s.%s.name", this.identifier.getNamespace(), this.identifier.getPath()));

        return name.replaceFirst("[A-Za-z]", String.valueOf(name.charAt(0)).toUpperCase());
    }

    @Environment(EnvType.CLIENT)
    public List<String> getTooltip() {
        return Arrays.asList(I18n.translate(String.format("skill.%s.%s.desc", this.identifier.getNamespace(), this.identifier.getPath())).split("\\\\n"));
    }

    public boolean hasDependencies() {
        return !this.getDependencies().isEmpty();
    }

    public final int getTier() {
        int tier = this.hasDependencies() ? 1 : 0;

        for (final Skill dependency : this.getDependencies()) {
            final int previous = dependency.getTier();

            if (previous > 0) {
                tier++;
            }
        }

        return tier;
    }

    @Environment(EnvType.CLIENT)
    public void render(final ExtendedScreen screen, final int level, final int x, final int y, final int blitOffset) {
        ExtendedScreen.TEXTURE_MANAGER.bindTexture(Skills.getDefaultTextureLocation(this));
        screen.withZ(blitOffset, () -> screen.blit(x, y, 0, 0, 16, 16));
    }
}
