package me.triggjo2.vAuth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/** 
 * vAuth is an offline server verification plugin to prevent people from logging in as admins and destroying the server.
 * @author J_Triggs (triggjo2)
 */

public class Main extends JavaPlugin{
	/**
	 * Should we write out all permissions to a file
	 */
	public boolean writePermissions = true;
	/**
	 * Should we let everyone register, regardless of permissions
	 */
	public boolean allowAllRegister = true;
	/**
	 * Should we check for updates?
	 */
	public boolean checkVersionb = true;
	/**
	 * Should we let everyone login, regardless of permissions
	 */
	public boolean allowAllLogin = true;
	/**
	 * Should we let everyone change their own passwords, regardless of permissions
	 */
	public boolean allowAllChangePassword = true;
	/**
	 * Should we require login/register, regardless of permissions
	 */
	public boolean requireLogin = true;
	/**
	 * Should we inform the player they can't move as they try
	 */
	public boolean movementErrorMessage = false;
	/**
	 * Where to teleport our players to when they login
	 */
	public String loginLocation = "0>>64>>0>>world";
	/**
	 * Should we teleport players to the set coords when they login
	 */
	public boolean loginTeleport = true;
	/**
	 * Are we logged in
	 */
	public ArrayList<Player> notLoggedIn = new ArrayList<Player>();
	/**
	 * Our Config controller
	 */
	public FileConfiguration Config;
	/**
	 * Our Config file's location
	 */
	public File ConfigFile;
	/**
	 * Our Passwords controller
	 */
	public FileConfiguration UserPass;
	
	public FileConfiguration UserIP;
	
	public FileConfiguration UserDate;
	/**
	 * Our Passwords file's location
	 */
	public File UserPassFile;
	
	public File UserIPFile;
	
	public File UserDateFile;
	/**
	 * Our Configuration for the converter
	 */
	FileConfiguration Convert;
	/**
	 * Our Converting File's location
	 */
	public File ConvertFile;
	/**
	 * Our Language Pack
	 */
	FileConfiguration Language;
	/**
	 * Our Language File's location
	 */
	public File LanguageFile;
	/**
	 * Our player's Location storage
	 */
	public HashMap<String, Location> playerLocation = new HashMap<String, Location>();
	/**
	 * The plugins Event Listener
	 */
	private final ServerListener listener = new ServerListener(this);
	/**
	 * Our op password
	 */
	public String opSecurePassword = "CH4NG3ME";
	/**
	 * Our register message
	 */
	public String registerMessage = "&cYou have not registered! Please use /register [New Password] [Confirm New Password] to play!";
	/**
	 * Our login message
	 */
	public String loginMessage = "&cYou are not logged in! Please use /login [Your Password] to play!";
	/**
	 * Our change password message
	 */
	public String changePasswordMessage = "&7Password successfully changed!";
	/**
	 * Our failed change password message
	 */
	public String failedChangePasswordMessage = "&cSomething went wrong! Were the passwords correct?";
	/**
	 * Our force change password message
	 */
	public String forceChangePasswordMessage = "&7User %player%'s password changed!";
	/**
	 * Our failed permissions message
	 */
	public String failedPermissionsCheckMessage = "&cYou do not have permission to issue this command!";
	/**
	 * Our you are not logged in alert
	 */
	public String youAreNotLoggedInMessage = "&cYou are not allowed to do that! Login / Register!";
	/**
	 * Our your command is too short
	 */
	public String commandIsTooShortMessage = "&cSyntax: %commandSyntax%!";
	/**
	 * Our your command is too long
	 */
	public String commandIsTooLongMessage = "&cSyntax: %commandSyntax%!";
	/**
	 * Our successful login message
	 */
	public String loginSuccessfulMessage = "&bSuccessfully Logged in!";
	/**
	 * Our failed login message
	 */
	public String failedLoginMessage = "&cWrong Password!";
	/**
	 * Our successful register message
	 */
	public String registerSuccessfulMessage = "&bSuccessfully Registered!";
	/**
	 * Our failed register message
	 */
	public String failedRegisterMessage = "&cSomething went wrong! Check to make sure your password's match!";
	/**
	 * Our player not online message
	 */
	public String playerNotOnlineMessage = "&cPlayer is not online";
	/**
	 * Our already logged in message
	 */
	public String alreadyLoggedInMessage = "&cYou are already logged in";
	/**
	 * Our not registered message
	 */
	public String notRegisteredMessage = "&cYou are not registered";
	/**
	 * Our already registered message
	 */
	public String alreadyRegisteredMessage = "&cYou are already registered";
	/**
	 * Our op message
	 */
	public String opMessage = "&bSuccessfully op'ed %player%!";
	/**
	 * Our deop message
	 */
	public String deOpMessage = "&7Successfully deop'ed %player%!";
	/**
	 * Our you are now op message
	 */
	public String youAreNowOpMessage = "&eYou are now op!";
	/**
	 * Our you are no longer op message
	 */
	public String youAreNoLongerOpMessage = "&eYou are no longer op!";
	/**
	 * Our failed to op message
	 */
	public String failedToOpMessage = "&cIncorrect Password!";
	/**
	 * Our failed to deop message
	 */
	public String failedToDeOpMessage = "&cIncorrect Password!";
	/**
	 * Our allowed characters list
	 */
	public String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_";
	/**
	 * Whether or not we are in debug mode
	 */
	public boolean debug = false;
	/**
	 * Reference to the messages class file
	 */

	Message msg = new Message(this);
	/**
	 * Plugin Logger (log.info("Message to log");)
	 */
	final Logger log = Logger.getLogger("Minecraft");

	/** Plugin enable */
	@Override
	public void onEnable()
	{
		loginLocation = "0>>64>>0>>world";
		ConfigFile = new File(this.getDataFolder(), "config.yml");
		UserPassFile = new File(this.getDataFolder(), "passwords.yml");
		UserIPFile = new File(this.getDataFolder(), "ips.yml");
		UserDateFile = new File(this.getDataFolder(), "last-login.yml");
		ConvertFile = new File(this.getDataFolder(), "converting.yml");
		Convert = YamlConfiguration.loadConfiguration(ConvertFile);
		Config = YamlConfiguration.loadConfiguration(ConfigFile);
		UserPass = YamlConfiguration.loadConfiguration(UserPassFile);
		UserIP = YamlConfiguration.loadConfiguration(UserIPFile);
		UserDate = YamlConfiguration.loadConfiguration(UserDateFile);
		Bukkit.getServer().getPluginManager().registerEvents(listener, this);
		config();
		log.info("[vAuth] v" + getDescription().getVersion() + " Loaded config and user passwords!");
		writePermissions();
	}

	/** Plugin disable */
	@Override
	public void onDisable()
	{
		for(Player p : Bukkit.getOnlinePlayers())
		{
			if(notLoggedIn.contains(p))
			{
				listener.teleport(p);
			}
		}
		try {
			Config.save(ConfigFile);
		} catch (IOException e) {if(debug) e.printStackTrace();}
		try {
			UserPass.save(UserPassFile);
			UserIP.save(UserIPFile);
			UserDate.save(UserDateFile);
		} catch (IOException e) {if(debug) e.printStackTrace();}
		log.info("[vAuth] Shutting down, saved config and user passwords!");
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		try
		{
			if(commandLabel.equalsIgnoreCase("login") || commandLabel.equalsIgnoreCase("vlogin"))
			{
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
					msg.send(player, "Console can't login!");
					return true;
				}
				if(hasPermission(player, "vauth.login") || allowAllLogin || requireLogin)
				{
					if(!(notLoggedIn.contains(player)))
					{
						msg.send(player, alreadyLoggedInMessage);
						return true;
					}
					if(UserPass.get(player.getUniqueId().toString()) == null)
					{
						msg.send(player, notRegisteredMessage);
						return true;
					}
					if(args.length == 0)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Your Password]"));
						return true;
					}else if(args.length == 1) {
						if(loginCheck(player, args[0]))
						{
							msg.send(player, loginSuccessfulMessage);
//							if(notLoggedIn.contains(player))
//							{
//								notLoggedIn.remove(player);
//							}
							listener.teleport(player);
							
							return true;
						}
						msg.send(player, failedLoginMessage);
						return true;
					}else {
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Your Password]"));
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("register") || commandLabel.equalsIgnoreCase("vregister")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
					msg.send(player, "Console can't register!");
					return true;
				}
				if(hasPermission(player, "vauth.register") || allowAllRegister || requireLogin)
				{
					if(!(UserPass.get(player.getUniqueId().toString()) == null))
					{
						msg.send(player, alreadyRegisteredMessage);
						return true;
					}
					if(args.length == 0)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [New Password] [Confirm New Password]"));
						return true;
					}else if(args.length == 2) {
						if(register(player, args[0], args[1]))
						{
							msg.send(player, registerSuccessfulMessage);
//							if(notLoggedIn.contains(player))
//							{
//								notLoggedIn.remove(player);
//							}
							listener.teleport(player);
							return true;
						}
						msg.send(player, failedRegisterMessage);
						return true;
					}else {
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [New Password] [Confirm New Password]"));
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("changepassword") || commandLabel.equalsIgnoreCase("vchangepassword")|| commandLabel.equalsIgnoreCase("changepw")|| commandLabel.equalsIgnoreCase("vchangepw")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
					msg.send(player, "Console can't change its password!");
					return true;
				}
				if(hasPermission(player, "vauth.changepassword") || allowAllChangePassword)
				{
					if(args.length <= 2)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Old Password] [New Password] [Confirm New Password]"));
						return true;
					}else if(args.length == 3) {
						if(loginCheck(player, args[0]))
						{
							if(register(player, args[1], args[2]))
							{
								msg.send(player, changePasswordMessage);
								return true;
							}
							msg.send(player, failedChangePasswordMessage);
							return true;
						}
						msg.send(player, failedChangePasswordMessage);
						return true;
					}else {
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Old Password] [New Password] [Confirm New Password]"));
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("forcepassword") || commandLabel.equalsIgnoreCase("vforcepassword") || commandLabel.equalsIgnoreCase("forcepw") || commandLabel.equalsIgnoreCase("vforcepw")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
				}
				if(hasPermission(player, "vauth.admin.forcepasswordchange") || hasPermission(player, "vauth.admin.*"))
				{
					if(args.length <= 2)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Playername] [New Password] [Confirm New Password]"));
						return true;
					}else if(args.length == 3) {
						Player user = Bukkit.getPlayer(args[0]);
						if(user == null)
						{
							msg.send(player, playerNotOnlineMessage);
							return true;
						}
						if(register(user, args[1], args[2]))
						{
							msg.send(player, forceChangePasswordMessage.replaceAll("%player%", user.getName()));
							return true;
						}
						msg.send(player, failedRegisterMessage);
						return true;
					}else {
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Playername] [New Password] [Confirm New Password]"));
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("vauth")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
				}
				if(hasPermission(player, "vauth.admin.vauth") || hasPermission(player, "vauth.admin.*"))
				{
					if(args.length == 0)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Argument]"));
						msg.send(player, ChatColor.RED + "Accessible arguments: reload, setlogin, player");
						return true;
					}else if(args.length > 2){
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Argument]"));
						msg.send(player, ChatColor.RED + "Accessible arguments: reload, setlogin, player");
						return true;
					}else {
						if(args[0].equalsIgnoreCase("reload"))
						{
							try {
								Config.load(ConfigFile);
							} catch (FileNotFoundException e2) {
								if(debug) e2.printStackTrace();
							} catch (IOException e2) {
								if(debug) e2.printStackTrace();
							} catch (InvalidConfigurationException e2) {
								if(debug) e2.printStackTrace();
							}
							try {
								UserPass.save(UserPassFile);
								UserIP.save(UserIPFile);
								UserDate.save(UserDateFile);
							} catch (IOException e1) {if(debug) e1.printStackTrace();}
							config();
							try {
								Config.save(ConfigFile);
							} catch (IOException e) {if(debug) e.printStackTrace();}
							msg.send(player, ChatColor.GREEN + "Reloaded vAuth Config!");
							return true;
						}else if(args[0].equalsIgnoreCase("setlogin")) {
							loginLocation = player.getLocation().getX() + ">>" + player.getLocation().getY() + ">>" + player.getLocation().getZ() + ">>" + player.getLocation().getWorld().getName();
							Config.set("Advanced-Use.login-location", loginLocation);
							try {
								Config.save(ConfigFile);
							} catch (IOException e) {if(debug) e.printStackTrace();}
							msg.send(player, ChatColor.YELLOW + "Coords set: X: " + player.getLocation().getBlockX() + " Y: " + player.getLocation().getBlockY() + " Z: " + player.getLocation().getBlockZ() + " World: " + player.getLocation().getWorld().getName());
							return true;
						}else if(args[0].equalsIgnoreCase("player")) {
							if(args.length < 2)
							{
								msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " player [Playername]"));
								return true;
							}else if(args.length == 2) {
								Player user = Bukkit.getPlayer(args[0]);
								if(user == null)
								{
									msg.send(player, playerNotOnlineMessage);
									return true;
								}
								msg.send(user, loginSuccessfulMessage);
								msg.send(player, loginSuccessfulMessage + ": " + user.getName());
								if(notLoggedIn.contains(user))
								{
									notLoggedIn.remove(user);
								}
								listener.teleport(user);
								return true;
							}else {
								msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " player [Playername]"));
								return true;
							}
						}
						msg.send(player, ChatColor.RED + "Accessible arguments: reload, setlogin, player");
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("op")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
				}
				if(hasPermission(player, "vauth.admin.secureop") || hasPermission(player, "vauth.admin.*"))
				{
					if(args.length <= 1)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Playername] [Password]"));
						return true;
					}else if(args.length == 2) {
						OfflinePlayer user = null;
						try {
							user = Bukkit.getOfflinePlayer(args[0]);
						} catch (Exception e) {
							if(debug) e.printStackTrace();
							return true;
						}
						if(args[1].equals(opSecurePassword))
						{
							user.setOp(true);
							msg.announce(player, opMessage.replaceAll("%player%", user.getName()));
							try {msg.send((Player)user, youAreNowOpMessage);}
							catch(Exception e){}
							return true;
						}
						msg.send(player, failedToOpMessage);
						return true;
					}else {
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Playername] [Password]"));
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("deop")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
				}
				if(hasPermission(player, "vauth.admin.securedeop") || hasPermission(player, "vauth.admin.*"))
				{
					if(args.length <= 1)
					{
						msg.send(player, commandIsTooShortMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Playername] [Password]"));
						return true;
					}else if(args.length == 2) {
						OfflinePlayer user = null;
						try {
							user = Bukkit.getOfflinePlayer(args[0]);
						} catch (Exception e) {
							if(debug) e.printStackTrace();
							return true;
						}
						if(args[1].equals(opSecurePassword))
						{
							user.setOp(false);
							msg.announce(player, deOpMessage.replaceAll("%player%", user.getName()));
							try {msg.send((Player)user, youAreNoLongerOpMessage);}
							catch(Exception e){}
							return true;
						}
						msg.send(player, failedToDeOpMessage);
						return true;
					}else {
						msg.send(player, commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel + " [Playername] [Password]"));
						return true;
					}
				}
				msg.send(player, failedPermissionsCheckMessage);
				return true;
			}else if(commandLabel.equalsIgnoreCase("ops") || commandLabel.equalsIgnoreCase("vops")) {
				Player player = null;
				if(sender instanceof Player)
				{
					player = (Player) sender;
				}else {
					player = null;
				}
				if(hasPermission(player, "vauth.admin.ops"))
				{
					if(args.length == 0)
					{
						String fileops = "ops.json";
						String ops = null;
						try
						{
							BufferedReader bufferedreader = new BufferedReader(new FileReader(fileops));
							LineNumberReader lnr = new LineNumberReader(new FileReader(new File(fileops)));
							lnr.skip(Long.MAX_VALUE);
							int lastLine = (lnr.getLineNumber() / 5) - 2;
							ops = ChatColor.GOLD + "Ops(" + (lastLine) + "): " + ChatColor.GREEN;
							String s;
							for(; (s = bufferedreader.readLine()) != null;)
							{
								if(s.contains("\"name\": "))
								{
									s = s.replaceAll("\"", "");
									s = s.replaceAll(" ", "");
									ops += s.replaceAll("name: ", "") + ", ";
								}
							}
							msg.send(player, ops.substring(0, ops.length() - 2));
							bufferedreader.close();
							lnr.close();
							return true;
						} catch(IOException e){if(debug) e.printStackTrace();}	
					}else {
						msg.send(player,commandIsTooLongMessage.replaceAll("%commandSyntax%", "/" + commandLabel));
					}
				}else {
					msg.send(player, ChatColor.RED + "You do not have access to this command");
					return true;
				}
			}
			return true;
		}catch (NumberFormatException e){
			if(debug) e.printStackTrace();
			return true;
		}
	}

	public boolean loginCheck(Player player, String password)
	{
		String decryptedPassword = null;
		try
		{
			return comparePasswordToHash(password, UserPass.getString(player.getUniqueId().toString()));
		}catch (Exception e) {
			decryptedPassword = Base64Coder.decodeString(UserPass.getString(player.getUniqueId().toString()));
		}
		if(password.equals(decryptedPassword))
		{
			register(player, decryptedPassword, decryptedPassword);
			return true;
		}
		return false;
	}

	public boolean register(Player player, String password, String confirmedPassword)
	{
		String encryptedText = null;
		if(password.equals(confirmedPassword))
		{
			encryptedText = hashAndEncode(password);
			if(encryptedText == null)
			{
				msg.send(player, ChatColor.RED + "Failed to encrypt password! Disabling plugin!");
				this.setEnabled(false);
				return true;
			}
			UserPass.set(player.getUniqueId().toString(), encryptedText);
			UserIP.set(player.getUniqueId().toString(), player.getAddress().getHostName().toString());
			UserDate.set(player.getUniqueId().toString(), new Date());
			try
			{
				UserPass.save(UserPassFile);
				UserIP.save(UserIPFile);
				UserDate.save(UserDateFile);
			}catch (IOException e) {if(debug) e.printStackTrace();}
			return true;
		}
		return false;
	}

	//Happy you guys? //jtgans put this in the comments of the dev page
	private final int SALT_SIZE = 20;
	private byte[] generateSalt() {
		SecureRandom rng = new SecureRandom();
		byte[] salt = new byte[SALT_SIZE];

		rng.nextBytes(salt);

		return salt;
	}

	private static byte[] hashWithSalt(String password, byte[] salt) {
		MessageDigest hash;

		try {
			hash = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		hash.update(password.getBytes());
		hash.update(salt);
		return hash.digest();
	}

	public String hashAndEncode(String password) {
		byte[] salt = generateSalt();
		byte[] saltedHash = hashWithSalt(password, salt);

		byte[] combinedHash = new byte[salt.length + saltedHash.length];
		System.arraycopy(salt, 0, combinedHash, 0, salt.length);
		System.arraycopy(saltedHash, 0, combinedHash, salt.length, saltedHash.length);
		return new String(Base64Coder.encode(combinedHash));
	}

	public boolean comparePasswordToHash(String password, String encodedHash) {
		byte[] combinedHash = Base64Coder.decode(encodedHash);
		byte[] salt = Arrays.copyOfRange(combinedHash, 0, SALT_SIZE);
		byte[] saltedHash = Arrays.copyOfRange(combinedHash, SALT_SIZE, combinedHash.length);

		byte[] testHash = hashWithSalt(password, salt);

		return Arrays.equals(saltedHash, testHash);
	}
	//End of that snippet

	public void config()
	{
		try
		{
			if(Config.get("Security.allow-all-register") == null)
			{
				Config.set("Security.allow-all-register", allowAllRegister);
			}else {
				allowAllRegister = Config.getBoolean("Security.allow-all-register");
			}
			if(Config.get("Security.allow-all-login") == null)
			{
				Config.set("Security.allow-all-login", allowAllLogin);
			}else {
				allowAllLogin = Config.getBoolean("Security.allow-all-login");
			}
			if(Config.get("Security.allow-all-changepassword") == null)
			{
				Config.set("Security.allow-all-changepassword", allowAllChangePassword);
			}else {
				allowAllChangePassword = Config.getBoolean("Security.allow-all-changepassword");
			}
			if(Config.get("Security.require-login") == null)
			{
				Config.set("Security.require-login", requireLogin);
			}else {
				requireLogin = Config.getBoolean("Security.require-login");
			}
			if(Config.get("Security.allowed-characters") == null)
			{
				Config.set("Security.allowed-characters", allowedCharacters);
			}else {
				allowedCharacters = Config.getString("Security.allowed-characters");
			}
			boolean encrypt = true;
			if(Config.get("Security.encrypt-op-password") == null)
			{
				Config.set("Security.encrypt-op-password", encrypt);
			}else {
				encrypt = Config.getBoolean("Security.encrypt-op-password");
			}
			if(Config.get("Security.op-secure-password") == null)
			{
				Config.set("Security.op-secure-password", opSecurePassword);
			}else {
				if(!encrypt)
				{
					try {
						opSecurePassword = Base64Coder.decodeString(Config.getString("Security.op-secure-password"));
					} catch (Exception e) {if(debug) e.printStackTrace();}
				}else {
					opSecurePassword = Config.getString("Security.op-secure-password");
					Config.set("Security.op-secure-password", Base64Coder.encodeString(opSecurePassword));
				}
				Config.set("Security.encrypt-op-password", false);
				if(debug) log.info("Op Secure Password: " + opSecurePassword);
			}
			if(Config.get("Messages.show-movementerror-message") == null)
			{
				Config.set("Messages.show-movementerror-message", movementErrorMessage);
			}else {
				movementErrorMessage = Config.getBoolean("Messages.show-movementerror-message");
			}
			if(Config.get("Messages.language-file") == null)
			{
				Config.set("Messages.language-file", "english.yml");
			}else {
				language();
			}
			if(Config.get("Server.teleport-at-login") == null)
			{
				Config.set("Server.teleport-at-login", loginTeleport);
			}else {
				loginTeleport = Config.getBoolean("Server.teleport-at-login");
			}
			if(Config.get("Advanced-Use.login-location") == null)
			{
				Config.set("Advanced-Use.login-location", loginLocation);
			}else {
				loginLocation = Config.getString("Advanced-Use.login-location");
			}
			if(Config.get("Advanced-Use.debug") == null)
			{
				Config.set("Advanced-Use.debug", debug);
			}else {
				debug = Config.getBoolean("Advanced-Use.debug");
				if(debug) log.info("[vAuth] Debug mode ON!");
			}
			if(Config.get("Advanced-Use.convert-to-id") == null)
			{
				Config.set("Advanced-Use.convert-to-id", false);
				try
				{
					convertToIds();
				}catch (Exception e){if(debug) e.printStackTrace();}
			}else if(Config.getBoolean("Advanced-Use.convert-to-id")){
				try
				{
					convertToIds();
				}catch (Exception e){if(debug) e.printStackTrace();}
				Config.set("Advanced-Use.convert-to-id", false);
			}
			try
			{
				Config.save(ConfigFile);
			}catch (IOException e){if(debug) e.printStackTrace();}
		}catch (Exception e){if(debug) e.printStackTrace();}
	}
	
	public void language(String language) throws IOException
	{
		InputStream stream = Main.class.getResourceAsStream(language + ".yml");
	    if (stream == null) {
	        log.info("[vAuth] Failed to create " + language + " language file");
	        return;
	    }
	    OutputStream resStreamOut = null;
	    int readBytes;
	    byte[] buffer = new byte[4096];
	    try {
	        resStreamOut = new FileOutputStream(new File(this.getDataFolder(), language + ".yml"));
	        while ((readBytes = stream.read(buffer)) > 0) {
	            resStreamOut.write(buffer, 0, readBytes);
	        }
	    } catch (IOException e1) {
	        e1.printStackTrace();
	    } finally {
	        stream.close();
	        resStreamOut.close();
	    }
	}
	
	public void language() throws IOException
	{
		LanguageFile = new File(this.getDataFolder(), Config.getString("Messages.language-file"));
		if(!checkLanguageFiles("english.yml")) language("english");
		if(!checkLanguageFiles("german.yml")) language("german");
		if(!checkLanguageFiles("dutch.yml")) language("dutch");
		if(!checkLanguageFiles("french.yml")) language("french");
		if(!checkLanguageFiles("polish.yml")) language("polish");
		Language = YamlConfiguration.loadConfiguration(LanguageFile);
		if(LanguageFile.exists())
		{
			registerMessage = msg.getColors(Language.getString("Message.register-message"));
			loginMessage = msg.getColors(Language.getString("Message.login-message"));
			changePasswordMessage = msg.getColors(Language.getString("Message.changepassword-message"));
			failedChangePasswordMessage = msg.getColors(Language.getString("Message.failed-changepassword-message"));
			forceChangePasswordMessage = msg.getColors(Language.getString("Message.force-changepassword-message"));
			failedPermissionsCheckMessage = msg.getColors(Language.getString("Message.failed-permissioncheck-message"));
			youAreNotLoggedInMessage = msg.getColors(Language.getString("Message.you-are-not-loggedin-message"));
			commandIsTooShortMessage = msg.getColors(Language.getString("Message.command-tooshort-message"));
			commandIsTooLongMessage = msg.getColors(Language.getString("Message.command-toolong-message"));
			loginSuccessfulMessage = msg.getColors(Language.getString("Message.login-successful-message"));
			failedLoginMessage = msg.getColors(Language.getString("Message.failed-login-message"));
			registerSuccessfulMessage = msg.getColors(Language.getString("Message.register-successful-message"));
			failedRegisterMessage = msg.getColors(Language.getString("Message.failed-register-message"));
			playerNotOnlineMessage = msg.getColors(Language.getString("Message.player-notonline-message"));
			alreadyLoggedInMessage = msg.getColors(Language.getString("Message.already-loggedin-message"));
			notRegisteredMessage = msg.getColors(Language.getString("Message.not-registered-message"));
			alreadyRegisteredMessage = msg.getColors(Language.getString("Message.already-registered-message"));
			youAreNowOpMessage = msg.getColors(Language.getString("Message.you-are-now-op-message"));
			opMessage = msg.getColors(Language.getString("Message.inform-everyone-of-op-message"));
			failedToOpMessage = msg.getColors(Language.getString("Message.failed-to-op-message"));
			youAreNoLongerOpMessage = msg.getColors(Language.getString("Message.you-are-nolonger-op-message"));
			deOpMessage = msg.getColors(Language.getString("Message.inform-everyone-of-deop-message"));
			failedToDeOpMessage = msg.getColors(Language.getString("Message.failed-to-deop-message"));
		}
	}

	public boolean checkLanguageFiles(String language)
	{
		File file = new File(this.getDataFolder(), language);
		//System.out.println(file);
		if(file.exists())
		{
			return true;
		}
		return false;
	}

	@SuppressWarnings("deprecation")
	public void convertToIds() throws IOException
	{
		String fileName = "plugins" + File.separator + "vAuth" + File.separator + "passwords.yml";
		File file = new File(fileName);
		UserPass.save(file + ".backup");
		int currentline = 1;
		FileInputStream fs;
		fs = new FileInputStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(fs));
		LineNumberReader lnr = new LineNumberReader(new FileReader(file));
		lnr.skip(Long.MAX_VALUE);
		int lastLine = lnr.getLineNumber();
		for (int i = 0; i < currentline; i++) {
			currentline = currentline + 1;
			String split = br.readLine();
			String[] split1 = null;
			try
			{
				split1 = split.split(": ");
				String id = Bukkit.getOfflinePlayer(split1[0]).getUniqueId().toString();
				log.info("[vAuth] Converting from name to id: " + id);
				Convert.set(id.toLowerCase(), split1[1]);
			}catch (Exception e){if(debug) e.printStackTrace();}
			if(currentline == lastLine + 1)
			{
				Convert.save(ConvertFile);
				BufferedWriter bufferedwriter;
				try {
					bufferedwriter = new BufferedWriter(new FileWriter(file));
					bufferedwriter.write((new StringBuilder()).append("").toString());
					bufferedwriter.close();
				} catch (IOException e3) {if(debug) e3.printStackTrace();}
				Convert.save(UserPassFile);
				UserPass = YamlConfiguration.loadConfiguration(UserPassFile);
				break;
			}
		}
		br.close();
		lnr.close();
	}

//	/**
//	 * Check for Updates
//	 */
//	public void checkVersion() throws Exception
//	{
//		log.info("[vAuth] Checking Version Number... Is disabled.");
//		if(checkVersionb) return;
//		try
//		{
//			URL url = new URL("http://darkrivers.no-ip.info/Plugin/version-vAuth.txt");
//			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
//			String str;
//			while ((str = in.readLine()) != null) {
//				if(!str.equals(getDescription().getVersion()))
//				{
//					log.info("[vAuth] Your version is either out of date or custom build!");
//				}else {
//					log.info("[vAuth] You have the latest version!");
//				}
//			}
//			in.close();
//		} catch (MalformedURLException e) {
//			if(debug)
//			{
//				e.printStackTrace();
//			} else {
//				log.info("[vAuth] Could not check for update!");
//			}
//		}catch(IOException e){
//			if(debug)
//			{
//				e.printStackTrace();
//			} else {
//				log.info("[vAuth] Could not check for update!");
//			}
//		}
//	}

	/**
	 * Check's for Permission
	 */
	public boolean hasPermission(Player player, String permission)
	{
		if(player == null) return true;
		if(player.hasPermission(permission)) return true;
		if(player.hasPermission("vauth.*")) return true;
		if(player.isOp()) return true;
		return false;
	}

	/**
	 * Writes out all plugin permissions to permissions.txt in data folder
	 */
	public void writePermissions()
	{
		File file = new File(this.getDataFolder(), "permissions.txt");
		try
		{
			BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(file));
			bufferedwriter.write((new StringBuilder()).append("PERMISSION NODE : DESCRIPTION : ASSOCIATED COMMAND").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.* : Access to ALL of vAuth : <No specific command>").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.login : Access to login and play on server if login is not required yet they're registered : /login").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.register : Access to register on the server and further protect your account : /register").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.changepassword : Access to change your login password : /changepassword").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.admin.forcepasswordchange : Access to force a password change on a user : /forcepassword").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.admin.vauth : Access to reload, setlogin, and other vAuth features : /vauth").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.admin.secureop : Access to op a player : /op").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.admin.securedeop : Access to deop a player : /deop").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.admin.ops : Access to list all ops registered in ops.txt : /ops").append("\r\n").toString());
			bufferedwriter.write((new StringBuilder()).append("vauth.admin.* : Access to ALL vauth.admin permissions : <No specific command>").append("\r\n").toString());
			bufferedwriter.close();
		}catch(Exception e){if(debug) e.printStackTrace();}
	}
}