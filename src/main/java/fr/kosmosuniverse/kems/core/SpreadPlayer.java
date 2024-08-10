package fr.kosmosuniverse.kems.core;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KosmosUniverse
 */
public class SpreadPlayer {
	/**
	 * Private SpreadPlayer constructor
	 *
	 * @throws IllegalStateException
	 */
	private SpreadPlayer() {
		throw new IllegalStateException("Utility class");
	}
	
	/**
	 * Spreads all players to locations that forms circle centered on the sender player
	 * 
	 * @param sender	The player that is calling for a spread
	 */
    public static void spreadPlayers(Player sender) {
		 if (Config.getInstance().getConfigValues().getSpreadDistance() < 0) {
            sender.sendMessage(ChatColor.RED + " Spread Distance too short");
            return ;
        }

		 int playerAmount = PlayersList.getInstance().getPlayerQuantity();

		double angle = 360.0 / playerAmount;
        long radius = radiusCalc(angle, Config.getInstance().getConfigValues().getSpreadDistance());
        
        radius = Math.max(radius, Config.getInstance().getConfigValues().getSpreadRadius());
        
        List<Location> locations = getSpreadLocations(radius, angle, playerAmount, sender.getLocation());
        
        PlayersList.getInstance().spread(locations);
        
        locations.clear();
    }
    
    /**
     * Calculates the minimum radius for a specific distance between two points
     * 
     * @param angle		The angle between two points
     * @param distance	The distance between two points
     * 
     * @return the minimum radius
     */
    private static long radiusCalc(double angle, double distance) {
    	double cos = Math.cos(Math.toRadians(angle));
    	double sin = Math.sin(Math.toRadians(angle));
    	double tmp = cos - 1;
    	
    	tmp = Math.pow(tmp, 2);
    	tmp = tmp + sin;
    	tmp = Math.sqrt(tmp);
    	tmp = distance / tmp;
    	
    	return Math.round(tmp);
    }
    
    /**
     * Gets the spread locations list
     * 
     * @param radius	The distance between center and every players
     * @param angleInc	The angle between two players
     * @param size		The amount of players (or locations to calculate)
     * @param center	The center of the circle as Location
     * 
     * @return the list of the future players locations
     */
    private static List<Location> getSpreadLocations(long radius, double angleInc, int size, Location center) {
    	List<Location> locations = new ArrayList<>();
    	
    	double angle = 0;
    	double x;
    	double z;
    	
    	for (int cnt = 0; cnt < size; cnt++) {
    		x = radius * Math.cos(Math.toRadians(angle));
    		z = radius * Math.sin(Math.toRadians(angle));
    		
    		locations.add(new Location(center.getWorld(), x + center.getX(), 0, z + center.getZ()));
    		
    		angle+=angleInc;
    	}
    	
    	return locations;
    }
}
