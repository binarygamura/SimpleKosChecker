package de.fomad.simplekoschecker.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author binary gamura
 */
public class CVAResultNodeCache 
{
    private final Map<String, CVAResultNode> cache;
    
    private final long ttl;
    
    public CVAResultNodeCache(long ttl)
    {
	cache = new HashMap<>();
	this.ttl = ttl;
    }
    
    public void evict()
    {
	Map.Entry<String,CVAResultNode> entry;
	long limit = System.currentTimeMillis() - ttl;
	for(Iterator<Map.Entry<String,CVAResultNode>> iterator = cache.entrySet().iterator(); iterator.hasNext(); )
	{
	    entry = iterator.next();
	    if(entry.getValue().getLastChecked() < limit)
	    {
		iterator.remove();
	    }
	}
    }
    
    public boolean hasEntry(String playername)
    {
//	CVAResultNode node = cache.get(playername);
//	return (node != null && node.getLastChecked() > (System.currentTimeMillis() - ttl));	
	return false;
    }
    
    public CVAResultNode getByPlayername(String playername)
    {
	return cache.get(playername);
    }
    
    public void clear()
    {
	cache.clear();
    }
    
    public void add(CVAResultNode result)
    {
	if(result.getType() == CVAResultNode.Type.pilot)
	{
	    cache.put(result.getLabel(), result);
	}
    }
}
