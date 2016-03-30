/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.fomad.simplekoschecker.model;

/**
 *
 * @author binary gamura
 */
public class CheckerThreadResult 
{
    private Exception exception;
    
    private CVAResultNode[] results;
    
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

    public CVAResultNode[] getResults()
    {
	return results;
    }

    public void setResults(CVAResultNode[] results)
    {
	this.results = results;
    }
    
}
