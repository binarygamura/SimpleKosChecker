package de.fomad.simplekoschecker.controller;

import de.fomad.simplekoschecker.model.Constants;
import java.awt.Toolkit;
import java.util.Observable;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 *
 * @author binary gamura
 */
public class KeyLogger extends Observable implements NativeKeyListener
{
    private static final Logger logger = Logger.getLogger(KeyLogger.class);

    private int sequenceCounter;
    
    private long lastKeypress;
    
    private final long cooldown;
    
    static KeyLogger instance = null;
    
    private KeyLogger(long cooldown)
    {
	sequenceCounter = 0;
	this.cooldown = cooldown;
    }
    
    @Override
    public void nativeKeyPressed(NativeKeyEvent nke)
    {

    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nke)
    {
	if((nke.getKeyCode() & NativeKeyEvent.CTRL_MASK) == NativeKeyEvent.CTRL_MASK && (nke.getKeyCode() & NativeKeyEvent.VC_C) == NativeKeyEvent.VC_C)
	{
	    if(sequenceCounter == 0)
	    {
		sequenceCounter = 1;
		lastKeypress = System.currentTimeMillis();
	    }
	    else if(sequenceCounter == 1)
	    {
//		if(System.currentTimeMillis() - lastKeypress <= cooldown)
//		{
		    setChanged();
		    notifyObservers();
//		}
		sequenceCounter = 0;
	    }
	}
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nke)
    {
	
    }
    
    public static void dispose()
    {
	try
	{
	    GlobalScreen.unregisterNativeHook();
	}
	catch(Exception ex)
	{
	    logger.error("error while unregistering native keyboard hook!", ex);
	}
    }
    
    public static void init(Properties config) throws NativeHookException
    {
	instance = new KeyLogger(Long.parseLong(config.getProperty(Constants.ConfigKeys.keypressCooldown)));
	GlobalScreen.setEventDispatcher(new SwingDispatchService());
	GlobalScreen.registerNativeHook();
	GlobalScreen.addNativeKeyListener(instance);
    }
}
