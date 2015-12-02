package me.triggjo2.vAuth;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class ServerListener implements Listener {

	Main plugin;
	Message msg = new Message(this);
	private Scanner scanner;
	
	public ServerListener(Main instance)
	{
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		Player player = event.getPlayer();
		try
		{
			if(player.isOnline()) event.disallow(Result.KICK_OTHER, ChatColor.RED + "[vAuth] This player is already online");
			if(!vauthName(player)) event.disallow(Result.KICK_OTHER, ChatColor.RED + "Invalid Name");
		}catch (Exception e){if(plugin.debug) e.printStackTrace();}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		try
		{
			if(plugin.requireLogin || plugin.UserPass.get(player.getUniqueId().toString()) != null)
			{
				plugin.playerLocation.put(player.getName(), player.getLocation());
				plugin.log.info("[vAuth] Storing " + player.getName() + "'s location. Location is " + player.getLocation().toString());
			if(this.playerInFile(player) == false || this.getWeeksBetween(this.convertStringToDate((String) plugin.UserDate.get(player.getUniqueId().toString())))){
				if(plugin.UserPass.get(player.getUniqueId().toString()) == null)
				{
					// Register
					msg.send(player, plugin.registerMessage);
					plugin.notLoggedIn.add(player);
				}else
				{
					// Login
					msg.send(player, plugin.loginMessage);
					plugin.notLoggedIn.add(player);
				}
				
			}else{
				System.out.println("User has logged in");
			}
				if(plugin.loginTeleport) loginLocation(player);
			}
		}catch (NumberFormatException e){if(plugin.debug) e.printStackTrace();}
	}
	public Date convertStringToDate(String oldDate){
		System.out.println("The string date is: " + oldDate);
		DateFormat formatter ; 
		Date newDate = new Date(); 
		   formatter = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy");
		   try {
			newDate = formatter.parse(oldDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		   return newDate;
		
	}
	public boolean getWeeksBetween (Date a) {
		Date b = new Date();
	    if (b.before(a)) {
	        return false;
	    }
	    a = resetTime(a);
	    b = resetTime(b);

	    Calendar cal = new GregorianCalendar();
	    cal.setTime(a);
	    int weeks = 0;
	    while (cal.getTime().before(b)) {
	        // add another week
	        cal.add(Calendar.WEEK_OF_YEAR, 1);
	        weeks++;
	    }
	    if(weeks >= 1)
	    {
	    	System.out.println("User has been logged out for more than a week!");
	    	return true;
	    	
	    }
	    System.out.println("User has not been logged out for more than a week!");
	    return false;
	
	}

	public static Date resetTime (Date d) {
	    Calendar cal = new GregorianCalendar();
	    cal.setTime(d);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}

	public void loginLocation(Player player)
	{
		String[] toSplit = plugin.loginLocation.split(">>");
		Double X = Double.parseDouble(toSplit[0]);
		Double Y = Double.parseDouble(toSplit[1]);
		Double Z = Double.parseDouble(toSplit[2]);
		World world = Bukkit.getWorld(toSplit[3]);
		Location location = new Location(world, X, Y, Z);
		player.teleport(location, TeleportCause.PLUGIN);
		plugin.log.info("[vAuth] Teleporting " + player.getName() + " to login location " + location.toString());
	}
	public boolean playerInFile(Player player)
	
	{
		try {
		    scanner = new Scanner(plugin.UserIPFile);

		    //now read the file line by line...
		    int lineNum = 0;
		    while (scanner.hasNextLine()) {
		        String line = scanner.nextLine();
		        lineNum++;
		        if(line.contains(player.getUniqueId().toString()) || line.contains(player.getAddress().toString())) { 
		            System.out.println("User on" +lineNum+" has an IP that matches");
		            return true;
		        }
		    }
		} catch(FileNotFoundException e) { 
		    //handle this
			
		}
		return false;
	}
	public void teleport(Player player)
	{
//		String[] toSplit = plugin.loginLocation.split(">>");
//		Double X = Double.parseDouble(toSplit[0]);
//		Double Y = Double.parseDouble(toSplit[1]);
//		Double Z = Double.parseDouble(toSplit[2]);
//		World world = Bukkit.getWorld(toSplit[3]);
		try {
			if(!plugin.loginTeleport) return;
			Location location = plugin.playerLocation.get(player.getName());
			player.teleport(location, TeleportCause.PLUGIN);
			plugin.log.info("[vAuth] Teleporting " + player.getName() + " to their location " + location.toString());
			if(plugin.notLoggedIn.contains(player))
			{
				plugin.notLoggedIn.remove(player);
			}
		} catch (Exception e) {if(plugin.debug)	e.printStackTrace();}
	}

	public boolean vauthName(Player player)
	{
		String playerName = player.getName();
		if(playerName.equals("")) return false;
		if(playerName.length() >= 17) return false;
		for (int i = 0; i < playerName.length(); i++)
		{
			if(plugin.allowedCharacters.indexOf(playerName.charAt(i)) == -1)
			{
				return false;
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
//			String location1 = plugin.playerLocation.get(player.getName());
//			String[] toSplit = location1.split(">>");
//			Double X = Double.parseDouble(toSplit[0]);
//			Double Y = Double.parseDouble(toSplit[1]);
//			Double Z = Double.parseDouble(toSplit[2]);
//			World world = Bukkit.getWorld(toSplit[3]);
			Location location = plugin.playerLocation.get(player.getName());
			player.teleport(location, TeleportCause.PLUGIN);
			plugin.notLoggedIn.remove(player);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			msg.send(player, plugin.youAreNotLoggedInMessage);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerBreakBlocks(BlockBreakEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			msg.send(player, plugin.youAreNotLoggedInMessage);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPlaceBlocks(BlockPlaceEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			msg.send(player, plugin.youAreNotLoggedInMessage);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			if(plugin.movementErrorMessage)
			{
				msg.send(player, plugin.youAreNotLoggedInMessage);
			}
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			msg.send(player, plugin.youAreNotLoggedInMessage);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			msg.send(player, plugin.youAreNotLoggedInMessage);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		if(plugin.notLoggedIn.contains(player))
		{
			msg.send(player, plugin.youAreNotLoggedInMessage);
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event)
	{
		Entity entity = event.getEntity();
		Player player = null;
		try
		{
			if(entity instanceof Player)
			{
				player = (Player) event.getEntity();
				if(player.getGameMode() != GameMode.CREATIVE)
				{
					if(plugin.notLoggedIn.contains(player))
					{
						player.setFireTicks(0);
						player.setRemainingAir(player.getMaximumAir());
						event.setCancelled(true);
					}
				}
			}
		}catch (Exception e) {
			if(plugin.debug) e.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerIssueCommand(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String cmd = event.getMessage();
		if(!(cmd.contains("login") || cmd.contains("register")))
		{
			if(plugin.notLoggedIn.contains(player))
			{
				msg.send(player, plugin.youAreNotLoggedInMessage);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInventoryClick(InventoryClickEvent event)
	{
		HumanEntity p = event.getWhoClicked();
		Player player = Bukkit.getPlayer(p.getName());
		try
		{
			if(plugin.notLoggedIn.contains(player))
			{
				msg.send(player, plugin.youAreNotLoggedInMessage);
				event.setCancelled(true);
			}
		}catch (Exception e){if(plugin.debug) e.printStackTrace();}
	}
}
