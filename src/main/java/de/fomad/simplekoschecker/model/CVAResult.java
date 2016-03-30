package de.fomad.simplekoschecker.model;

import java.util.List;

/**
 *
 * @author binary gamura
 */
public class CVAResult 
{
    private int total;
    
    private int code;
    
    private String message;
    
    private List<CVAResultNode> results;

    public int getTotal()
    {
	return total;
    }

    public void setTotal(int total)
    {
	this.total = total;
    }

    public int getCode()
    {
	return code;
    }

    public void setCode(int code)
    {
	this.code = code;
    }

    public String getMessage()
    {
	return message;
    }

    public void setMessage(String message)
    {
	this.message = message;
    }

    public List<CVAResultNode> getResults()
    {
	return results;
    }

    public void setResults(List<CVAResultNode> results)
    {
	this.results = results;
    }
}
