package de.fomad.simplekoschecker.controller;

import java.awt.Toolkit;
import java.util.Observable;
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
    
    private KeyLogger()
    {
	sequenceCounter = 0;
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
	    sequenceCounter = 1;
	    System.out.println("!!! WEEEEE !!!");
	}
	else if(nke.getKeyCode() == NativeKeyEvent.VC_D)
	{
	    System.out.println("!!! HEEEEE !!!");
	    if(sequenceCounter == 1)
	    {
		System.out.println("WINNNERS!!!");
		
		setChanged();
		notifyObservers();
	    }
	}
	else 
	{
	    sequenceCounter = 0;
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
    
    public static final KeyLogger instance = new KeyLogger();
    
    public static void init() throws NativeHookException
    {
	GlobalScreen.setEventDispatcher(new SwingDispatchService());
	GlobalScreen.registerNativeHook();
	GlobalScreen.addNativeKeyListener(instance);
    }
}
