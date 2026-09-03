package net.spudacious5705.abovethecloudstweaks;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.WorldTransferSettings;

import java.util.Set;

@EventBusSubscriber(modid = Abovethecloudstweaks.MODID)
public class ATCTCommands {
    private static final Set<ResourceKey<Level>> DISALLOWED_DIMENSIONS = Set.of(
            Level.OVERWORLD,
            Level.NETHER,
            Level.END,
            WorldTransferSettings.LevelDDU
    );

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("exit_dimension")
                        .executes(TeleportBackToSpawn)
        );

        event.getDispatcher().register(
                Commands.literal("spawn")
                        .executes(ToHub)
        );

        event.getDispatcher().register(
        Commands.literal("git")
                .requires(source -> source.hasPermission(4)) // Require OP level 4

                .then(Commands.literal("pull")
                        .then(Commands.literal("kubejs")
                                .executes(GitCommands::pullKubeJs))
                        /*.then(Commands.literal("config")
                                .executes(GitCommands::pullConfig))*/
                )

                .then(Commands.literal("hardReset")
                        .then(Commands.literal("kubejs")
                                .executes(GitCommands::resetKubeJs))
                        /*.then(Commands.literal("config")
                                .executes(GitCommands::resetConfig))*/
                )
        );

    }

    private static final Command<CommandSourceStack> TeleportBackToSpawn = new Command<>() {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSourceStack source = context.getSource();

            if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("This command can only be used by a player."));
                return 0;
            }

            ResourceKey<Level> currentDimension = player.serverLevel().dimension();
            if (DISALLOWED_DIMENSIONS.contains(currentDimension)) {
                source.sendFailure(Component.literal("You can only use this command in cataclysm dimensions."));
                return 0;
            }

            ResourceKey<Level> respawnDimension = player.getRespawnDimension();
            ServerLevel targetLevel = player.server.getLevel(respawnDimension);
            if (targetLevel == null) {
                source.sendFailure(Component.literal("Your respawn dimension is unavailable."));
                return 0;
            }

            BlockPos respawnPos = player.getRespawnPosition();
            if (respawnPos == null) {
                respawnPos = targetLevel.getSharedSpawnPos();
            }

            player.teleportTo(
                    targetLevel,
                    respawnPos.getX() + 0.5D,
                    respawnPos.getY() + 0.1D,
                    respawnPos.getZ() + 0.5D,
                    player.getYRot(),
                    player.getXRot()
            );

            source.sendSuccess(() -> Component.literal("Leaving dimension..."), true);
            return 1;
        }
    };

    private static final Command<CommandSourceStack> ToHub = new Command<>() {
        @Override
        public int run(CommandContext<CommandSourceStack> context) {
            CommandSourceStack source = context.getSource();

            if (!(context.getSource().getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("This command can only be used by a player."));
                return 0;
            }

            ResourceKey<Level> respawnDimension = Level.OVERWORLD;
            ServerLevel targetLevel = player.server.getLevel(respawnDimension);

            if (targetLevel == null) {
                source.sendFailure(Component.literal("Error"));
                return 0;
            }
            player.teleportTo(
                    targetLevel,
                    0, 185, -10,
                    player.getYRot(),
                    player.getXRot()
            );

            source.sendSuccess(() -> Component.literal("Welcome back!"), true);
            return 1;
        }
    };
}
