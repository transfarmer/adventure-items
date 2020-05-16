package transfarmer.soulboundarmory.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderSystem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderFireball;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.SpriteAtlasTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.Identifier;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.jetbrains.annotations.Nonnull;
import transfarmer.soulboundarmory.entity.SoulboundFireballEntity;

public class RenderSoulboundFireball extends ProjectileEntityRenderer<SoulboundFireballEntity> {
}
