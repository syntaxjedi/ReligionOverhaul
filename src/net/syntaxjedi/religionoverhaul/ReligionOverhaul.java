package net.syntaxjedi.religionoverhaul;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ReligionOverhaul extends JavaPlugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	
	@Override
	public void onEnable(){
		log.info("[ReligionOverhaul] Registering Commands");
		Commands commands = new Commands();
		this.getCommand("religion").setExecutor(commands);
		log.info("[ReligionOverhaul] Checking Files");
		this.saveDefaultConfig();
		log.info("[ReligionOverhaul] Trying To Connect To Database");
		
		try {
			SQLHandler.tryConnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onDisable(){
		
	}
}
