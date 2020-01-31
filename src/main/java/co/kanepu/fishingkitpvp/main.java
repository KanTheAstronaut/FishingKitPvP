package co.kanepu.fishingkitpvp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class main extends JavaPlugin implements Listener {
    private File ConfigFile;
    private FileConfiguration Config;
    private final HashMap<UUID, Integer> cooldownp = new HashMap<>();

    public void onEnable() {
        ConfigFile = new File(getDataFolder(), "config.yml");
        if (!ConfigFile.exists()) {
            ConfigFile.getParentFile().mkdirs();
            saveResource("config.yml", false);
        }

        Config= new YamlConfiguration();
        try {
            Config.load(ConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("Plugin made by KanTheAstronaut/Kanepu as requested by iEddie for monman11.com (if u r readin dis thomas plox gib me dev)");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();
    }

    @EventHandler
    public void onFishingRod(PlayerFishEvent event) {
        if (event.getCaught() instanceof Player && event.getPlayer().getItemInHand().getType().equals(Material.FISHING_ROD)) {
            if (event.getPlayer().hasPermission("fishingkitpvp.use")) {
                return;
            }
            if (event.getCaught().hasPermission("fishingkitpvp.exempt")) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot use that on that player!");
                return;
            }
            if (event.getPlayer().getLocation().distance(event.getCaught().getLocation()) > Config.getInt("range")) {
                event.getPlayer().sendMessage(ChatColor.RED + "That player is too far!");
                return;
            }
            long timeLeft = System.currentTimeMillis() - cooldownp.getOrDefault(event.getPlayer().getUniqueId(), 0);
            if (TimeUnit.MILLISECONDS.toSeconds(timeLeft) >= Config.getInt("cooldown")) {
                if (!event.getPlayer().hasPermission("fishingkitpvp.nocooldown")) {
                    if ((int) System.currentTimeMillis() < 1) {
                        cooldownp.remove(event.getPlayer().getUniqueId());
                    } else {
                        cooldownp.put(event.getPlayer().getUniqueId(), (int) System.currentTimeMillis());
                    }
                }
                if (Config.getBoolean("victimmsg")) {
                    event.getCaught().sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("victimmsgi")).replace("%player%", event.getPlayer().getName()));
                }
                if (Config.getBoolean("attackermsg")) {
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("attackermsgi")).replace("%player%", event.getCaught().getName()));
                }
                if (Config.getBoolean("drag")) {
                    event.getCaught().setVelocity(new Vector(event.getCaught().getLocation().getX() - event.getPlayer().getLocation().getX(), event.getCaught().getLocation().getY() - event.getPlayer().getLocation().getY(), event.getCaught().getLocation().getZ() - event.getPlayer().getLocation().getZ()).normalize().multiply(-Config.getDouble("speed")));
                } else {
                    Location l = event.getPlayer().getLocation();
                    l.setY(l.getY() + Config.getInt("height"));
                    event.getCaught().teleport(l);
                }
            } else {
                event.getPlayer().sendMessage(ChatColor.RED + "You need to wait %time% before using that again!".replace("%time%", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(timeLeft) - Config.getInt("cooldown"))));
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("fishingkitpvpreload")) {
            sender.sendMessage(ChatColor.GREEN + "The configuration file has been reloaded!");
            Config = YamlConfiguration.loadConfiguration(ConfigFile);
            return true;
        }
        return false;
    }
}
