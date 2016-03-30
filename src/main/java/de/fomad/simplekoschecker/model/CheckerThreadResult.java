/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fomad.simplekoschecker.model;

import java.util.List;


/**
 *
 * @author binary gamura
 */
public class CheckerThreadResult 
{
    private Exception exception;
    
    private List<CVAResultNode> results;
    
    public boolean hadError()
    {
	return exception != null;
    }

    public Exception getException()
    {
	return exception;
    }

    public void setException(Exception exception)
    {
	this.exception = exception;
    }

    public List<CVAResultNode> getResults()
    {
	return results;
    }

    public void setResults(List<CVAResultNode> results)
    {
	this.results = results;
    }
    
    public CVAResultNode getResultFor(String label)
    {
	CVAResultNode match = null;
	for(CVAResultNode node : results)
	{
	    if(node.getLabel().equalsIgnoreCase(label))
	    {
		match = node;
		break;
	    }
	}
	return match;
    }
    
//    public boolean hasEntryFor(String playername)
//    {
//	boolean hasEntry = false;
//	for(CVAResultNode node : results)
//	{
//	    if(node.getLabel().equalsIgnoreCase(playername))
//	    {
//		hasEntry = true;
//		break;
//	    }
//	}
//	return hasEntry;
//    }
    
}
