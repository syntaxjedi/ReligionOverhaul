package net.syntaxjedi.religionoverhaul;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileHandler {
	private static final Logger log = Logger.getLogger("Minecraft");
	/*
	private static File relPath = new File("./plugins/religion/");
	private static File relFile = new File(relPath, "religion.yml");
	private static FileConfiguration relConf;
	
	public static void createFile(){
		relConf = YamlConfiguration.loadConfiguration(relFile);
		if(!relPath.exists()){
			relPath.mkdir();
		}
		if(!relFile.exists()){
			try{
				relFile.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
			relConf.set("religions.test1.deity", "Deity Name");
			relConf.set("religions.test1.desc", "This is a test description");
			saveFile();
		}
		
	}
	
	public static void saveFile(){
		try{
			relConf.save(relFile);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	*/
}
