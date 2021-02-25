package io.github.ultimateboomer.textweaks;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class TexTweaksMixinPlugin implements IMixinConfigPlugin {
    private final Set<String> optifineIncompatible = new ObjectOpenHashSet<>();

    public TexTweaksMixinPlugin() {
        optifineIncompatible.add("io.github.ultimateboomer.textweaks.mixin.RenderLayerMixin");
        optifineIncompatible.add("io.github.ultimateboomer.textweaks.mixin.MipmapHelperMixin");
    }

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (optifineIncompatible.contains(mixinClassName)
                && FabricLoader.getInstance().isModLoaded("optifabric")) {
            TexTweaks.LOGGER.info("{} disabled for OptiFine compatibility", mixinClassName);
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
