package de.fomad.simplekoschecker.model;

import java.awt.Color;

/**
 * @author binary gamura
 */
public class Constants
{
    public static class Common
    {
	public static final String configFileName = "config.properties";
    }
    
    public static class Colors
    {
	public static final Color kosColor = Color.RED;
	public static final Color notColor = Color.GREEN;
	public static final Color noResultColor = Color.LIGHT_GRAY;
    }

    public static class ConfigKeys
    {
	public static final String cacheTimeToLive = "cacheTTL";
	public static final String cvaApiUri = "cvaURI";
	public static final String fomadURI = "fomadURI";
	public static final String foregroundInterval = "foregroundInterval";
	public static final String keypressCooldown = "keypress_cooldown";
    }
}
