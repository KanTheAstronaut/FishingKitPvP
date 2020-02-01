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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

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
        BukkitRunnable r = new BukkitRunnable() {
            @Override
            public void run() {
                if (!cooldownp.isEmpty()) {
                    for(HashMap.Entry<UUID, Integer> e : cooldownp.entrySet()) {
                        cooldownp.replace(e.getKey(), e.getValue() - 1);
                    }
                }
            }
        };
        r.runTaskTimerAsynchronously(this, 0, 20);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll();
    }

    @EventHandler
    public void onFishingRod(PlayerFishEvent event) {
        if (event.getCaught() instanceof Player && event.getPlayer().getItemInHand().getType().equals(Material.FISHING_ROD)) {
            if (!event.getPlayer().hasPermission("fishingkitpvp.use")) {
                event.setCancelled(true);
                return;
            }
            if (event.getCaught().hasPermission("fishingkitpvp.exempt")) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot use that on that player!");
                event.setCancelled(true);
                return;
            }
            if (event.getPlayer().getLocation().distance(event.getCaught().getLocation()) > Config.getInt("range")) {
                event.getPlayer().sendMessage(ChatColor.RED + "That player is too far!");
                event.setCancelled(true);
                return;
            }
            if (cooldownp.containsKey(event.getPlayer().getUniqueId()) && cooldownp.get(event.getPlayer().getUniqueId()) <= 0)
                cooldownp.remove(event.getPlayer().getUniqueId());
            else if (cooldownp.containsKey(event.getPlayer().getUniqueId()) && cooldownp.get(event.getPlayer().getUniqueId()) > 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "You need to wait " + cooldownp.get(event.getPlayer().getUniqueId()) + " seconds before using that again!");
                event.setCancelled(true);
            }
            if (!cooldownp.containsKey(event.getPlayer().getUniqueId())) {
                if (!event.getPlayer().hasPermission("fishingkitpvp.nocooldown"))
                    cooldownp.put(event.getPlayer().getUniqueId(), Config.getInt("cooldown"));
                if (Config.getBoolean("victimmsg"))
                    event.getCaught().sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("victimmsgi")).replace("%player%", event.getPlayer().getName()));
                if (Config.getBoolean("attackermsg"))
                    event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', Config.getString("attackermsgi")).replace("%player%", event.getCaught().getName()));
                if (Config.getBoolean("drag"))
                    event.getCaught().setVelocity(new Vector(event.getCaught().getLocation().getX() - event.getPlayer().getLocation().getX(), event.getCaught().getLocation().getY() - event.getPlayer().getLocation().getY(), event.getCaught().getLocation().getZ() - event.getPlayer().getLocation().getZ()).normalize().multiply(-Config.getDouble("speed")));
                else {
                    Location l = event.getPlayer().getLocation();
                    l.setY(l.getY() + Config.getInt("height"));
                    event.getCaught().teleport(l);
                }
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
