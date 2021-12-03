package harmonised.explosiont.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import harmonised.explosiont.events.WorldTickHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class ExplosiontCommand
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("forceHealAll")
                .requires(player -> { return player.hasPermission(2); })
                .executes(ExplosiontCommand::forceHealAll));
    }

    private static int forceHealAll(CommandContext<CommandSourceStack> context)
    {
        try
        {
            context.getSource().getPlayerOrException().displayClientMessage(new TranslatableComponent("All loaded chunks have been healed"), false);
        }
        catch(CommandSyntaxException e)
        {
            //not player, it's fine
        }

        WorldTickHandler.forceAllHeal();
        return 1;
    }
}