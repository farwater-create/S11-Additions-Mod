package net.spudacious5705.abovethecloudstweaks;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class GitPullCommand {

    public static void runGitPull(CommandSourceStack source) {
        source.sendSystemMessage(Component.literal("§eStarting git pull..."));
        CompletableFuture.runAsync(() -> {
            try {
                File workingDir = new File("./kubejs");

                if (!workingDir.exists() || !workingDir.isDirectory()) {
                    source.sendSystemMessage(Component.literal("§cError: Failed to locate kubejs Directory"));
                    return;
                }

                ProcessBuilder builder = new ProcessBuilder();
                builder.directory(workingDir);


                builder.command("sh", "-c", "git pull origin main");


                builder.redirectErrorStream(true);
                Process process = builder.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[Git Pull] " + line);
                        source.sendSystemMessage(Component.literal("§e#  " + line));
                    }
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    source.sendSystemMessage(Component.literal("§aGit pull completed successfully!"));
                } else {
                    source.sendSystemMessage(Component.literal("§cGit pull failed with exit code: " + exitCode));
                }

            } catch (Exception e) {
                source.sendSystemMessage(Component.literal("§cException occurred: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }
}
