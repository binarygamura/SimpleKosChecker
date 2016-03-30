package de.fomad.simplekoschecker.view;

import org.apache.log4j.Logger;

/**
 * keep the application alway on top of other applications. 
 * @author binary gamura
 */
class ForegroundCheckerThread implements Runnable
{

    private static final Logger logger = Logger.getLogger(ForegroundCheckerThread.class);

    private final GUI gui;

    private final long interval;

    ForegroundCheckerThread(GUI gui, long interval)
    {
	this.gui = gui;
	this.interval = interval;
    }

    @Override
    public void run()
    {
	try
	{
	    while (!Thread.currentThread().isInterrupted())
	    {

		if(gui.isEnabled())
		{
		    gui.setAlwaysOnTop(false);
		    gui.setAlwaysOnTop(true);
		    Thread.sleep(interval);
		}
	    }
	}
	catch (InterruptedException ex)
	{
	    logger.fatal("foreground thread was interrupted!", ex);
	}
    }
}
