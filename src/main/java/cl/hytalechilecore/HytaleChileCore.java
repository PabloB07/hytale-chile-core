package cl.hytalechilecore;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.CommandBase;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.entity.LivingEntityUseBlockEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

@SuppressWarnings({ "null", "removal" })
public class HytaleChileCore extends JavaPlugin {

    private static HytaleChileCore instance;

    // Track players who have already received tools (by username)
    private final Set<String> playersReceivedTools = new HashSet<>();

    // Track players who have already received door reward (by username)
    private final Set<String> playersReceivedDoorReward = new HashSet<>();

    public HytaleChileCore(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static HytaleChileCore get() {
        return instance;
    }

    @Override
    protected void setup() {
        instance = this;

        // Register commands
        getCommandRegistry().registerCommand(new ExampleCommand());

        // Register events
        registerEvents();

        getLogger().at(Level.INFO).log("HytaleChileCore setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("HytaleChileCore started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("HytaleChileCore shutting down!");
    }

    private void registerEvents() {
        // Listen for living entity block use events (doors, etc.)
        getEventRegistry().registerGlobal(
                LivingEntityUseBlockEvent.class,
                this::onLivingEntityUseBlock);
    }

    private void onLivingEntityUseBlock(LivingEntityUseBlockEvent event) {
        // Get block type
        String blockType = event.getBlockType();

        // Check if it's a door OPENING (not closing)
        // Block type contains "OpenDoor" when opening, "CloseDoor" when closing
        if (blockType != null && blockType.toLowerCase().contains("door") && blockType.contains("Open")) {
            // Get the entity that used the block
            Ref<EntityStore> entityRef = event.getRef();
            if (entityRef != null && entityRef.isValid()) {
                Store<EntityStore> store = entityRef.getStore();
                Player player = store.getComponent(entityRef, Player.getComponentType());

                if (player != null) {
                    String username = player.getPlayerRef().getUsername();

                    // Check if player already received the door reward
                    if (playersReceivedDoorReward.contains(username)) {
                        return;
                    }

                    // Mark player as having received the reward
                    playersReceivedDoorReward.add(username);

                    // Give the player a door item
                    Inventory inventory = player.getInventory();
                    ItemStack doorItem = new ItemStack("Furniture_Village_Door", 1);
                    inventory.getStorage().addItemStack(doorItem);
                    player.sendInventory();

                    // Send message to player
                    player.sendMessage(Message.translation("Abriste una puerta, test test test!"));
                }
            }
        }
    }

    // Main /example command collection
    class ExampleCommand extends AbstractCommandCollection {

        ExampleCommand() {
            super("welcome", "example.commands.desc");

            // Add subcommands
            this.addSubCommand(new InfoCommand());
            this.addSubCommand(new ToolsCommand());
        }

        // /example info - shows plugin information
        class InfoCommand extends CommandBase {

            InfoCommand() {
                super("info", "example.commands.info.desc");
            }

            @Override
            protected void executeSync(@Nonnull CommandContext context) {
                context.sendMessage(Message.translation(""));
                context.sendMessage(Message.translation("========== Hytale Chile Core =========="));
                context.sendMessage(Message.translation("Version: 1.0.0"));
                context.sendMessage(Message.translation("Made by: PabloB07"));
                context.sendMessage(Message.translation("GitHub: https://github.com/PabloB07/hytale-chile-core"));
                context.sendMessage(Message.translation("====================================="));
            }
        }

        // /example tools - gives stone tools (once per player)
        class ToolsCommand extends AbstractPlayerCommand {

            ToolsCommand() {
                super("tools", "example.commands.tools.desc");
            }

            @Override
            protected void execute(@Nonnull CommandContext context,
                    @Nonnull Store<EntityStore> store,
                    @Nonnull Ref<EntityStore> ref,
                    @Nonnull PlayerRef playerRef,
                    @Nonnull World world) {

                String username = playerRef.getUsername();

                // Check if player already received tools
                if (playersReceivedTools.contains(username)) {
                    context.sendMessage(Message.translation("Ya recibiste tu kit, disfrutalo!"));
                    return;
                }

                // Get the Player entity and inventory
                Player player = store.getComponent(ref, Player.getComponentType());
                if (player == null) {
                    context.sendMessage(Message.translation("Error: No se pudo acceder a los datos del jugador."));
                    return;
                }

                Inventory inventory = player.getInventory();

                // Create crude starter tools (valid Hytale items)
                List<ItemStack> tools = Arrays.asList(
                        new ItemStack("Tool_Pickaxe_Crude", 1),
                        new ItemStack("Tool_Hatchet_Crude", 1),
                        new ItemStack("Tool_Shovel_Crude", 1),
                        new ItemStack("Weapon_Axe_Crude", 1));

                // Add tools to inventory
                inventory.getStorage().addItemStacks(tools);

                // Send updated inventory to client
                player.sendInventory();

                // Mark player as having received tools
                playersReceivedTools.add(username);

                context.sendMessage(Message.translation("Recibiste tu kit de herramientas!"));
                context.sendMessage(Message.translation("- Pickaxe de madera"));
                context.sendMessage(Message.translation("- Machete de madera"));
                context.sendMessage(Message.translation("- Pala de madera"));
                context.sendMessage(Message.translation("- Hacha de madera"));

                getLogger().at(Level.INFO).log("Se le dio el kit a: %s", username);
            }
        }
    }
}
