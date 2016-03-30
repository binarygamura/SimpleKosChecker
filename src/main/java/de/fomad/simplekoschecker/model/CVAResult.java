package de.fomad.simplekoschecker.model;

/**
 *
 * @author binary gamura
 */
public class CVAResult 
{
    private int total;
    
    private int code;
    
    private String message;
    
    private CVAResultNode[] results;

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

    public CVAResultNode[] getResults()
    {
	return results;
    }

    public void setResults(CVAResultNode[] results)
    {
	this.results = results;
    }
}
