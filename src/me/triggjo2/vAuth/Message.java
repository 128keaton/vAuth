package me.triggjo2.vAuth;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Controls messages sent from the plugin to the player/console with a few other abilities
 */
public class Message {

	/**
	 * Reference to Main
	 */
	Main plugin;
	ServerListener listener;

	public Message(Main instance)
	{
		this.plugin = instance;
	}

	public Message(ServerListener serverListener)
	{
		this.listener = serverListener;
	}

	/**
	 * Sends messages from the plugin to either player(s) or the console
	 * @param player The player involved
	 * @param message The message to send to the player
	 */
	public void send(Player player, String message)
	{
		if(player == null)
		{
			plugin.log.info(message);
		}else {
			player.sendMessage(getColors(message));
		}
	}

	/**
	 * Sends messages from the plugin to the server
	 * @param player The player involved
	 * @param message The message to send to the player
	 */	
	public void announce(Player player, String message)
	{
		if(player == null)
		{
			Bukkit.getServer().broadcastMessage(getColors(message + ChatColor.YELLOW + " - Console"));
		}else {
			Bukkit.getServer().broadcastMessage(getColors(message + ChatColor.YELLOW + " - " + player.getName()));
		}
	}
	
	/**
	 * Make color
	 */
	public String getColors(String message)
	{
		message = message.replaceAll("&f", ChatColor.WHITE + "");
		message = message.replaceAll("&e", ChatColor.YELLOW + "");
		message = message.replaceAll("&b", ChatColor.AQUA + "");
		message = message.replaceAll("&0", ChatColor.BLACK + "");
		message = message.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
		message = message.replaceAll("&a", ChatColor.GREEN + "");
		message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
		message = message.replaceAll("&c", ChatColor.RED + "");
		message = message.replaceAll("&4", ChatColor.DARK_RED + "");
		message = message.replaceAll("&6", ChatColor.GOLD + "");
		message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
		message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
		message = message.replaceAll("&7", ChatColor.GRAY + "");
		message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
		message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
		message = message.replaceAll("&9", ChatColor.BLUE + "");
		return message;
	}
}
