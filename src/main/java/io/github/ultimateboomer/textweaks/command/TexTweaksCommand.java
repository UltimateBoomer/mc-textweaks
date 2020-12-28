package io.github.ultimateboomer.textweaks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class TexTweaksCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("textweaks")
                .executes((context -> execute(context.getSource())));
        dispatcher.register(builder);
    }

    private static int execute(ServerCommandSource source) {
        MinecraftClient mc = MinecraftClient.getInstance();
        source.sendFeedback(new TranslatableText("textweaks.command.atlas"), false);
        mc.getBakedModelManager().atlasManager.atlases.forEach((id, texture) -> {
            int mipLevel = texture.sprites.values().toArray(new Sprite[0])[0].images.length - 1;
            source.sendFeedback(new LiteralText(String.format("Atlas: %s", id.toString())), false);
            source.sendFeedback(new LiteralText(String.format(" - Mipmap Level: %s", mipLevel)), false);
        });

        return 1;
    }
}
