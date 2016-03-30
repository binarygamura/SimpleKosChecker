package de.fomad.simplekoschecker.model;

/**
 *
 * @author binary gamura
 */
public class CVAResultNode {
    
    public static enum Type {pilot, alliance, corp};
    
    private long lastChecked;
    
    private Type type;
    
    private long id;
    
    private String label;
    
    private String icon;
    
    private boolean kos;
    
    private long eveid;
    
    private CVAResultNode corp;
    
    private boolean npc;
    
    private String ticker;
    
    private CVAResultNode alliance;
    
    public CVAResultNode()
    {
	lastChecked = System.currentTimeMillis();
    }

    public Type getType()
    {
	return type;
    }

    public void setType(Type type)
    {
	this.type = type;
    }

    public long getId()
    {
	return id;
    }

    public void setId(long id)
    {
	this.id = id;
    }

    public long getLastChecked()
    {
	return lastChecked;
    }

    public void setLastChecked(long lastChecked)
    {
	this.lastChecked = lastChecked;
    }
    

    public String getLabel()
    {
	return label;
    }

    public void setLabel(String label)
    {
	this.label = label;
    }

    public String getIcon()
    {
	return icon;
    }

    public void setIcon(String icon)
    {
	this.icon = icon;
    }

    public boolean isKos()
    {
	return kos;
    }

    public void setKos(boolean kos)
    {
	this.kos = kos;
    }

    public long getEveid()
    {
	return eveid;
    }

    public void setEveid(long eveid)
    {
	this.eveid = eveid;
    }

    public CVAResultNode getCorp()
    {
	return corp;
    }

    public void setCorp(CVAResultNode corp)
    {
	this.corp = corp;
    }

    public boolean isNpc()
    {
	return npc;
    }

    public void setNpc(boolean npc)
    {
	this.npc = npc;
    }

    public String getTicker()
    {
	return ticker;
    }

    public void setTicker(String ticker)
    {
	this.ticker = ticker;
    }

    public CVAResultNode getAlliance()
    {
	return alliance;
    }

    public void setAlliance(CVAResultNode alliance)
    {
	this.alliance = alliance;
    }
    
    public boolean computeKos()
    {
	return kos ||  (corp != null && corp.kos) || (corp.alliance != null && corp.alliance.kos);
    }
}
