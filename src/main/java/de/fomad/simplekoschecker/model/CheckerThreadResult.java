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
	if(results != null && !results.isEmpty())
	{
	    for(CVAResultNode node : results)
	    {
		if(node.getLabel().equalsIgnoreCase(label))
		{
		    match = node;
		    break;
		}
	    }
	}
	return match;
    }
    
    private int getNumberOfKosEntries()
    {
	int result = 0;
	for(CVAResultNode node : results)
	{
	    if(node.computeKos())
	    {
		result++;
	    }
	}
	return result;
    }
}
