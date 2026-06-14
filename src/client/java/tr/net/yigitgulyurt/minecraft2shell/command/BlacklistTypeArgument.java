package tr.net.yigitgulyurt.minecraft2shell.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlacklistTypeArgument implements ArgumentType<String> {
    private static final Collection<String> EXAMPLES = Arrays.asList("specific", "wildcard", "regex");

    public static BlacklistTypeArgument blacklistType() {
        return new BlacklistTypeArgument();
    }

    public static String getBlacklistType(CommandContext<FabricClientCommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readUnquotedString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (String example : EXAMPLES) {
            if (example.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(example);
            }
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
