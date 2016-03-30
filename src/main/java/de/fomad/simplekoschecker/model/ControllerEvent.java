package de.fomad.simplekoschecker.model;

/**
 *
 * @author binary gamura
 */
public class ControllerEvent 
{
    public static enum Type{START, RESULT};
    
    private Type type;
    
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
