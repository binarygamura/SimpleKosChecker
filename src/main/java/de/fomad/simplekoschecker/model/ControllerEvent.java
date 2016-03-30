package de.fomad.simplekoschecker.model;

/**
 * Simple Container representing the two types of events a controller
 * can ermit, which are START end RESULT. This datatype doesnt contain
 * any logic.
 * 
 * @author binary gamura
 */
public class ControllerEvent 
{
    
    public static enum Type{START, RESULT};
    
    /**
     * Type of the event.
     */
    private Type type;
    
    /**
     * Result from the CVA kos api or cache. This field
     * contains only data if the type is set to RESULT.
     */
    private CheckerThreadResult result;

    public ControllerEvent(Type type)
    {
	this(type, null);
    }
    
    public ControllerEvent(Type type, CheckerThreadResult result)
    {
	this.type = type;
	this.result = result;
    }
    
    public Type getType()
    {
	return type;
    }

    public void setType(Type type)
    {
	this.type = type;
    }

    public CheckerThreadResult getResult()
    {
	return result;
    }

    public void setResult(CheckerThreadResult result)
    {
	this.result = result;
    }
}
