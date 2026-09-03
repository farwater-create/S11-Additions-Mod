package net.spudacious5705.abovethecloudstweaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class GitCommands {

    static int pullKubeJs(CommandContext<CommandSourceStack> context) {
        runShCommand(context.getSource(), "./kubejs",
                new String[]{"git pull"},
                "§eRefreshing Git Kubejs...","[Git Clone] ");
        return 1;
    }

    static int resetKubeJs(CommandContext<CommandSourceStack> context) {
        runShCommand(context.getSource(), "./kubejs",
                new String[]{"rm -rf ./* ./.*", "git clone https://github.com/farwater-create/s11-kubejs"},
                "§eRefreshing Git Kubejs...","[Git Clone] ");
        return 1;
    }

    /*static int pullConfig(CommandContext<CommandSourceStack> context) {
        runShCommand(context.getSource(), "./config",
                new String[]{"git pull"},
                "§eRefreshing Git Kubejs...","[Git Clone] ");
        return 1;
    }

    static int resetConfig(CommandContext<CommandSourceStack> context) {
        runShCommand(context.getSource(), "./config",
                new String[]{"rm -rf ./* ./.*", "git clone https://github.com/farwater-create/s11-kubejs"},
                "§eRefreshing Git Kubejs...","[Git Clone] ");
        return 1;
    }*/

    private static void runShCommand(
            CommandSourceStack source, String dir, String[] commands,
            String startMsg, String headerMsg
        ) {
        source.sendSystemMessage(Component.literal(startMsg));
        CompletableFuture.runAsync(() -> {
            try {
                File workingDir = new File(dir);

                if (!workingDir.exists() || !workingDir.isDirectory()) {
                    source.sendSystemMessage(Component.literal("§c Error: Failed to locate Directory " + dir));
                    return;
                }

                ProcessBuilder builder = new ProcessBuilder();
                builder.directory(workingDir);

                for(String cmnd : commands){
                    builder.command("sh", "-c", cmnd);
                }

                builder.redirectErrorStream(true);
                Process process = builder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(headerMsg + line);
                        source.sendSystemMessage(Component.literal("§e#  " + line));
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    source.sendSystemMessage(Component.literal("§a Success!"));
                } else {
                    source.sendSystemMessage(Component.literal("§c Failed with exit code: " + exitCode));
                }

            } catch (Exception e) {
                source.sendSystemMessage(Component.literal("§c Exception occurred: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}
