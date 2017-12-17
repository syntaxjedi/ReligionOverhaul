package net.syntaxjedi.religionoverhaul;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class SQLHandler {
	
	private static final Logger log = Logger.getLogger("Minecraft");
	
	private static Connection connection;
	private static String host, database, username, password;
	private static int port;
	private static JavaPlugin plugin = JavaPlugin.getProvidingPlugin(ReligionOverhaul.class);
	
	/*
	private static ReligionOverhaul plugin;
	public SQLHandler(ReligionOverhaul instance){
		SQLHandler.plugin = instance;
	}
	*/
	public static void tryConnect() throws SQLException{
		
		host = plugin.getConfig().getString("database.host");
		port = plugin.getConfig().getInt("database.port");
		database = plugin.getConfig().getString("database.databaseName");
		username = plugin.getConfig().getString("database.username");
		password = plugin.getConfig().getString("database.password");
		
		
		/*
		host = "localhost";
		port = 3306;
		database = "test";
		username = "root";
		password = "Pa$$768874";
		*/
		BukkitRunnable r = new BukkitRunnable(){
			@Override
			public void run(){
				try{
					openConnection();
					findTable();
				}catch(ClassNotFoundException e){
					e.printStackTrace();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		r.runTaskAsynchronously(plugin);
	}
	
	public static void searchDB(){
		BukkitRunnable s = new BukkitRunnable(){
			@Override
			public void run(){
				
				Statement statement = null;
				try {
					statement = connection.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				ResultSet result = null;
				try {
					result = statement.executeQuery("SELECT * FROM players WHERE username = \"syntaxjedi\";");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				try {
					while(result.next()){
						String rel = null;
						try {
							rel = result.getString("religion");
						} catch (SQLException e) {
							e.printStackTrace();
						}
						log.info("Religion: " + rel);
					}
					result.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		s.runTaskAsynchronously(plugin);
	}
	
	public static void findTable(){
		BukkitRunnable f = new BukkitRunnable(){
			@Override
			public void run(){
				Statement statement = null;
				ResultSet aff = null;
				ResultSet rel = null;
				DatabaseMetaData dbm = null;
				
				
				try {
					statement = connection.createStatement();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				try {
					dbm = connection.getMetaData();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				try {
					aff = dbm.getTables(null, null, "affiliations", null);
					if(!aff.next()){
						log.info("[ReligionOverhaul] Creating Affiliations Table");
						statement.executeUpdate("CREATE TABLE `test`.`affiliations`(`UUID` VARCHAR(40) NOT NULL, "
								+ "`username` VARCHAR(20) NULL, "
								+ "`religion` VARCHAR(30) NULL, "
								+ "`position` VARCHAR(15) NULL, "
								+ "PRIMARY KEY (`UUID`));");
					}else{
						log.info("[ReligionOverhaul] Affiliations Table Already Exists");
					}
					aff.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				try{
					rel = dbm.getTables(null, null, "religions", null);
					if(!rel.next()){
						log.info("[ReligionOverhaul] Creating Religions Table");
						statement.executeUpdate("CREATE TABLE `test`.`religions`(`key` INT NOT NULL AUTO_INCREMENT, "
								+ "`name` VARCHAR(30) NULL,"
								+ "`location` VARCHAR(15) NULL,"
								+ "`description` VARCHAR(250) NULL,"
								+ "`head_uuid` VARCHAR(40) NULL,"
								+ "`head_name` VARCHAR(20) NULL,"
								+ "PRIMARY KEY (`key`));");
					}else{
						log.info("[ReligionOverhaul] Religions Table Already Exists");
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		f.runTaskAsynchronously(plugin);
	}
	
	public static void getList(Player p){
		Map<Integer, Map<Integer, String>> relList = new HashMap<Integer, Map<Integer, String>>();
		Map<Integer, String> relInfo = new HashMap<Integer, String>();
		BukkitRunnable l = new BukkitRunnable(){
			@Override
			public void run(){
				Statement statement = null;
				ResultSet result = null;
				try{
					statement = connection.createStatement();
					result = statement.executeQuery("SELECT `key`, `name`, `description` FROM test.religions");
					while(result.next()){
						/*
						relInfo.put(1, result.getString(2));
						relInfo.put(2, result.getString(3));
						relList.put(result.getInt(1), relInfo);
						*/
						TextComponent message = new TextComponent(result.getString(2));
						message.setColor(net.md_5.bungee.api.ChatColor.BLUE);
						message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(result.getString(3)).create()));
						p.spigot().sendMessage(message);
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		l.runTaskAsynchronously(plugin);
	}
	
	public static void joinReligion(Player p, String religion, String position){
		BukkitRunnable j = new BukkitRunnable(){
			@Override
			public void run(){
				Statement statement = null;
				ResultSet result = null;
				String query = "INSERT INTO affiliations (UUID, username, religion, position) VALUES (?, ?, ?, ?)";
				String uuid = p.getUniqueId().toString();
				String name = p.getDisplayName().toString();
				
				try{
					statement = connection.createStatement();
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				try{
					PreparedStatement pStmt = connection.prepareStatement(query);
					pStmt.setString(1, uuid);
					pStmt.setString(2, name);
					pStmt.setString(3, religion);
					pStmt.setString(4, position);
					result = statement.executeQuery("SELECT * FROM affiliations WHERE UUID = \"" + uuid + "\";");
					if(!result.next()){
						pStmt.execute();
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		j.runTaskAsynchronously(plugin);
	}
	
	public static void createReligion(String religion, String description, Player p){
		BukkitRunnable c = new BukkitRunnable(){
			@Override
			public void run(){
				String query = "INSERT INTO religions (name, description, head_uuid, head_name) VALUES (?, ?, ?, ?);";
				String uuid = p.getUniqueId().toString();
				String name = p.getName();
				try{
					PreparedStatement pStmt = connection.prepareStatement(query);
					pStmt.setString(1, religion);
					pStmt.setString(2, description);
					pStmt.setString(3, uuid);
					pStmt.setString(4, name);
					pStmt.execute();
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		c.runTaskAsynchronously(plugin);
	}
	
	public static void getFollowers(Player p, String religion){
		BukkitRunnable f = new BukkitRunnable(){
			public void run(){
				Statement statement = null;
				ResultSet result = null;
				int i = 0;
				String followCount = "";
				String head = "";
				
				try{
					statement = connection.createStatement();
				}catch(SQLException e){
					e.printStackTrace();
				}
				
				try{
					result = statement.executeQuery("SELECT username, position FROM affiliations WHERE religion = \"" + religion + "\";");
					while(result.next()){
						i++;
						if(result.getString(2).equals("head")){
							head = "Head: " +  result.getString(1) + "\n";
						}
					}
					followCount = "Followers: " + i;
					TextComponent message = new TextComponent(religion);
					TextComponent followers = new TextComponent(followCount);
					message.setColor(net.md_5.bungee.api.ChatColor.BLUE);
					followers.setColor(net.md_5.bungee.api.ChatColor.GREEN);
					message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(head).color(net.md_5.bungee.api.ChatColor.GOLD).append(followCount).color(net.md_5.bungee.api.ChatColor.GREEN).create()));
					p.spigot().sendMessage(message);
					
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
		};
		f.runTaskAsynchronously(plugin);
	}
	
	public static void openConnection() throws SQLException, ClassNotFoundException{
		if(connection != null && !connection.isClosed()){
			return;
		}
		
		synchronized(plugin){
			if(connection != null && !connection.isClosed()){
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", username, password);
		}
	}
}
