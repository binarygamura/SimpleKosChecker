package de.fomad.simplekoschecker.controller;

import de.fomad.simplekoschecker.model.CVAResultNode;
import de.fomad.simplekoschecker.model.CVAResultNodeCache;
import de.fomad.simplekoschecker.model.CheckerThreadResult;
import de.fomad.simplekoschecker.model.Constants;
import de.fomad.simplekoschecker.model.ControllerEvent;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.jnativehook.NativeHookException;

/**
 * this controller encapsulates the whole functionality of the application.
 * it is responsable to propagate events and thus the control flow within the application.
 * 
 * the controller listens for input by the keylogger, takes copied data from the
 * system clipboards and starts a background thread to retain the kos data
 * from the cva api.
 * 
 * @author binary gamura
 */
public class Controller extends Observable implements Observer 
{
    private static final Logger logger = Logger.getLogger(Controller.class);
    
    /**
     * reusable HttpClient. 
     */
    private CloseableHttpClient httpClient;
    /**
     * in order to not freeze the whole application, the task of querying the
     * cva api will be done in the background with a thread from this pool.
     * please note, that the number of threads from this pool is limited by 1.
     * so only one thread at a time can query the cva api.
     */
    private final ExecutorService threadPool;
    
    /**
     * configuration of the application.
     */
    private final Properties properties;
    
    /**
     * to minimize the number of http requests, this cache can keep query results
     * from the cva api for a specified amount of time.
     */
    private final CVAResultNodeCache cache;
    
    public Controller(Properties properties)
    {
	//allow only one thread at a time to access the cva api.
	threadPool = Executors.newSingleThreadExecutor();
	cache = new CVAResultNodeCache(Long.valueOf(properties.getProperty(Constants.ConfigKeys.cacheTimeToLive)));
	this.properties = properties;
    }
    
    /**
     * start the keylogger and initialize crucial parts of this application ie. the http client.
     */
    public void init()
    {
	httpClient = HttpClients.custom().setConnectionManager(new PoolingHttpClientConnectionManager()).build();
	
	try
	{
	    KeyLogger.init();
	    KeyLogger.instance.addObserver(this);
	}
	catch (NativeHookException ex)
	{
	    //TODO: dont catch this exception? it is critical!
	    logger.fatal("critical error while initializing key logger!", ex);
	}
    }

    /**
     * safely shut down all critical components of the application.
     */
    public void dispose()
    {
	KeyLogger.dispose();
	threadPool.shutdown();
    }
    
    @Override
    public void update(Observable o, Object arg)
    {
	if(o instanceof KeyLogger)
	{
	    try
	    {
		if(threadPool instanceof ThreadPoolExecutor)
		{
		    if(((ThreadPoolExecutor) threadPool).getActiveCount() == 1)
		    {
			logger.debug("skinng request. request already running!");
			return;
		    }
		}
		String temp;
		//get the data from the system clipboard as a single string.
		String data = (String) Toolkit.getDefaultToolkit()
			    .getSystemClipboard().getData(DataFlavor.stringFlavor);
		ArrayList<String> candidates = new ArrayList<>();
		ArrayList<String> allCandidates = new ArrayList<>();
		//assume the player names are seperated by newlines.
		for(String token : data.split("\n"))
		{
		    temp = token.trim();
		    //do we have valid cache entries for the player in question?
		    if(!cache.hasEntry(token))
		    {
			candidates.add(temp);
		    }		    
		    allCandidates.add(temp);
		}
		String[] finalCandidates = candidates.toArray(new String[candidates.size()]);
		CheckerThread checkProcess = new CheckerThread(
			httpClient, 
			allCandidates.toArray(new String[allCandidates.size()]), 
			finalCandidates, 
			new URI(properties.getProperty(Constants.ConfigKeys.cvaApiUri)));
		checkProcess.addObserver(this);
		threadPool.submit(checkProcess);
		setChanged();
		notifyObservers(new ControllerEvent(ControllerEvent.Type.START));

	    }
	    catch(URISyntaxException | HeadlessException | UnsupportedFlavorException | IOException ex)
	    {
		logger.warn("unable to extract data from clipboard.", ex);
	    }
	}
	else if(o instanceof CheckerThread)
	{
	    CheckerThreadResult result = (CheckerThreadResult) arg;
	    if(!result.hadError())
	    {
		for(CVAResultNode node : result.getResults())
		{
		    cache.add(node);
		}
	    }
	    setChanged();
	    notifyObservers(new ControllerEvent(ControllerEvent.Type.RESULT, result));
	}
    }
}
