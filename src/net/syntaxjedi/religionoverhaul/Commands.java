package net.syntaxjedi.religionoverhaul;

import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Commands implements CommandExecutor{
	private static final Logger log = Logger.getLogger("Minecraft");
	
	public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
		if(!(sender instanceof Player)){
			sender.sendMessage(ChatColor.RED + "This plugin does not have console support.");
			return true;
		}else{
			Player p = (Player) sender;
			if(command.getName().equalsIgnoreCase("religion")){
				if(args.length == 0){
					p.sendMessage(ChatColor.RED + "Use /religion help to see all the commands");
					return true;
				}else if(args.length == 1){
					switch(args[0]){
					case "help":
						p.sendMessage(ChatColor.GOLD + "/religion join <religion> : " + ChatColor.WHITE + "Join a religion."
								+ ChatColor.GOLD + "\n/religion create <name> description <description> : " + ChatColor.WHITE + "Found a new religion."
								+ ChatColor.GOLD + "\n/religion list : " + ChatColor.WHITE + "List all the religions.");
						return true;
					case "list":
						SQLHandler.getList(p);
						return true;
					case "create":
						p.sendMessage(ChatColor.RED + "Use /religion create <name> description <description> to create a new religion");
						return true;
					}
				}else if(args.length >= 2){
					String concatName = "";
					String concatDesc = "";
					switch(args[0]){
					case "join":
						concatName = "";
						for(int i = 1; i < args.length; i++){
							concatName = concatName + args[i] + " ";
						}
						p.sendMessage(ChatColor.GOLD + "You have chosen to join " + ChatColor.BLUE + concatName);
						SQLHandler.joinReligion(p, concatName, "follower");
						return true;
					case "create":
						concatName = "";
						Boolean desc = false;
						for(int i = 1; i<args.length; i++){
							if(args[i].equalsIgnoreCase("description")){
								desc = true;
								continue;
							}else if(desc == true){
								concatDesc = concatDesc + args[i] + " ";
							}else{
								concatName = concatName + args[i] + " ";
							}
							
						}
						TextComponent msg = new TextComponent("You created the religion ");
						TextComponent religion = new TextComponent(concatName);
						
						msg.setColor(net.md_5.bungee.api.ChatColor.GOLD);
						religion.setColor(net.md_5.bungee.api.ChatColor.BLUE);
						religion.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(concatDesc).create()));
						msg.addExtra(religion);
						p.spigot().sendMessage(msg);
						
						SQLHandler.createReligion(concatName, concatDesc, p);
						SQLHandler.joinReligion(p, concatName, "head");
						return true;
					case "followers":
						concatName = "";
						if(args.length == 2){
							SQLHandler.getFollowers(p, args[1]);
						}else{
							for(int i = 1; i<args.length; i++){
								concatName = concatName + args[i] + " ";
							}
							SQLHandler.getFollowers(p, concatName);
						}
						return true;
					}
					
				}
			}
		}
		return false;
	}
}