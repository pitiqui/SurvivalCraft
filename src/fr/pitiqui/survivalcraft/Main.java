package fr.pitiqui.survivalcraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener
{	
	static String joinable = "startup";
	
	static boolean restart;
	static int mapsize;
	static int invictime;
	static boolean compass;
	static int endkicktime;
	
	public void onEnable()
	{
		setupConfig();
		
		ScoreboardManager.initScoreboard();
		
		GameManager.startCountdown();
		
		Bukkit.getPluginManager().registerEvents(this, this);
		Bukkit.getPluginManager().registerEvents(new PreEventManager(), this);
		Bukkit.getPluginManager().registerEvents(new ScoreboardManager(), this);
		Bukkit.getPluginManager().registerEvents(new GameManager(), this);
		Bukkit.getPluginManager().registerEvents(new InventoryManager(), this);
		
		WorldCreator wc = new WorldCreator("sg");
		wc.type(WorldType.NORMAL);
		wc.createWorld();
		
		WorldCreator wcNether = new WorldCreator("sg_nether");
		wcNether.environment(Environment.NETHER);
		wcNether.seed(wc.seed());
		wcNether.createWorld();
		
		Bukkit.getWorld("sg").setSpawnLocation(0, 70, 0);
		
		createBorders();
		
		joinable = "true";
		
		for(Player p : Bukkit.getOnlinePlayers())
		{
			p.setScoreboard(ScoreboardManager.sb);
		}
	}
	
	public void onDisable()
	{
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("sc"))
		{
			if(args.length == 0)
			{
				sender.sendMessage(ChatColor.GREEN + "SurvivalCraft v0.5, by pitiqui");
				
				return true;
			}
			
			else
			{
				if(args.length >= 1)
				{
					if(args[0].equalsIgnoreCase("start") && sender.isOp())
					{
						GameManager.startGame();
						sender.sendMessage("You have forced the start of the game !");
					}
					else if(args[0].equalsIgnoreCase("end") && sender.isOp())
					{
						GameManager.forceEndGame();
						sender.sendMessage("You have forced the end of the game !");
					}
					else if(args[0].equalsIgnoreCase("surrender")) {
						GameManager.forceQuit((Player) sender);
					}
					else if(args[0].equalsIgnoreCase("gui")) {
						InventoryManager.createMenu((Player) sender);
					}
					else if(args[0].equalsIgnoreCase("tp")) {
						if(args.length == 1) {
							sender.sendMessage("You have not precise to whom you want to teleport");
							return true;
						}
						
						if(getPlugin(Main.class).getServer().getPlayer(args[1]) != null) {
							if(((Player) sender).getGameMode().equals(GameMode.SPECTATOR)) {
								((Player) sender).teleport(Bukkit.getPlayer(args[1]).getLocation());
								sender.sendMessage("Teleported !");
							} else {
								sender.sendMessage("You're not spectator !");
							}
						} else {
							sender.sendMessage("The player doesn't exist");
						}
					}
					else {
						sender.sendMessage("Sorry, the command wasn't found");
					}
				}
			}
		}
		
		return true;
	}
	
	public boolean createBorders()
	{
		try {
			Integer halfMapSize = (int) Math.floor(mapsize/2);
			Integer wallHeight = 255;
			Material wallBlock = Material.BEDROCK;
			World w = Bukkit.getWorld("sg");
			
			Location spawn = w.getSpawnLocation();
			Integer limitXInf = spawn.add(-halfMapSize, 0, 0).getBlockX();
			
			spawn = w.getSpawnLocation();
			Integer limitXSup = spawn.add(halfMapSize, 0, 0).getBlockX();
			
			spawn = w.getSpawnLocation();
			Integer limitZInf = spawn.add(0, 0, -halfMapSize).getBlockZ();
			
			spawn = w.getSpawnLocation();
			Integer limitZSup = spawn.add(0, 0, halfMapSize).getBlockZ();
			
			for (Integer x = limitXInf; x <= limitXSup; x++) {
				w.getBlockAt(x, 1, limitZInf).setType(Material.BEDROCK);
				w.getBlockAt(x, 1, limitZSup).setType(Material.BEDROCK);
				for (Integer y = 2; y <= wallHeight; y++) {
					w.getBlockAt(x, y, limitZInf).setType(wallBlock);
					w.getBlockAt(x, y, limitZSup).setType(wallBlock);
				}
			} 
			
			for (Integer z = limitZInf; z <= limitZSup; z++) {
				w.getBlockAt(limitXInf, 1, z).setType(Material.BEDROCK);
				w.getBlockAt(limitXSup, 1, z).setType(Material.BEDROCK);
				for (Integer y = 2; y <= wallHeight; y++) {
					w.getBlockAt(limitXInf, y, z).setType(wallBlock);
					w.getBlockAt(limitXSup, y, z).setType(wallBlock);
				}
			}
			
			System.out.println("Borders gen finished");
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return false;
	}
	
	public void setupConfig()
	{
		this.getConfig().addDefault("restart", true);
		this.getConfig().addDefault("mapsize", 1000);
		this.getConfig().addDefault("invictime", 60);
		this.getConfig().addDefault("compass", true);
		this.getConfig().addDefault("endkicktime", 15);
		this.saveDefaultConfig();
		restart = getConfig().getBoolean("restart");
		mapsize = getConfig().getInt("mapsize");
		invictime = getConfig().getInt("invictime");
		compass = getConfig().getBoolean("compass");
		endkicktime = getConfig().getInt("endkicktime");
	}
}
