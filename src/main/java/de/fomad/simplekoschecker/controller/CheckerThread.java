package de.fomad.simplekoschecker.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fomad.simplekoschecker.model.CVAResult;
import de.fomad.simplekoschecker.model.CheckerThreadResult;
import java.net.URI;
import java.util.Observable;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author binary gamura
 */
public class CheckerThread extends Observable implements Runnable
{
    private final CloseableHttpClient httpClient;

    private final String[] candidates;
    
    private final String[] allCandidates;

    private final URI cvaApiURI;

    private static final Logger logger = Logger.getLogger(CheckerThread.class);
    
    private final Gson gson;

    public CheckerThread(CloseableHttpClient httpClient, String[] allCandidates, String[] candidates, URI cvaApiURI)
    {
	gson = new GsonBuilder().create();
	this.httpClient = httpClient;
	this.allCandidates = allCandidates;
	this.candidates = candidates;
	this.cvaApiURI = cvaApiURI;
    }

    /**
     * simple function to turn an array of strings into a comma
     * seperated string containing the array elements. no
     * need to include another dependency, if not even 10 lines 
     * of code can do the job.
     * 
     * @param input
     * @return 
     */
    private String implode(String[] input)
    {
	StringBuilder builder = new StringBuilder();
	if (input.length > 0)
	{
	    for (int i = 0; i < input.length - 1; i++)
	    {
		builder.append(input[i]).append(",");
	    }
	    builder.append(input[input.length - 1]);
	}
	return builder.toString();
    }

    @Override
    public void run()
    {
	String body = null;
	CheckerThreadResult result = new CheckerThreadResult();
	try
	{
	    URIBuilder builder = new URIBuilder(cvaApiURI);
	    builder.addParameter("c", "json")
		    .addParameter("icon", "32")
		    .addParameter("type", "multi")
		    .addParameter("max", "128")
		    .addParameter("offset", "0")
		    .addParameter("q", implode(candidates));
	    URI uri = builder.build();
	    HttpGet request = new HttpGet(uri);
	    CloseableHttpResponse response = httpClient.execute(request);
	    if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
	    {
		body = EntityUtils.toString(response.getEntity());
		CVAResult cvaResult = gson.fromJson(body, CVAResult.class);
		result.setResults(cvaResult.getResults());
	    }
	    else
	    {
		throw new CheckerThreadException("unable to query CVA api! status code was "+response.getStatusLine().getStatusCode()+".");
	    }
	}
	catch (Exception ex)
	{
	    logger.warn("error while accessing CVA kos api.", ex);
	    result.setException(ex);
	}
	finally
	{
	    setChanged();
	    notifyObservers(result);
	}
    }
}
