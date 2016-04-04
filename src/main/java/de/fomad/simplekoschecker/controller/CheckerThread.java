package de.fomad.simplekoschecker.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fomad.simplekoschecker.model.CVAResult;
import de.fomad.simplekoschecker.model.CVAResultNode;
import de.fomad.simplekoschecker.model.CheckerThreadResult;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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

    private final List<String> candidates;
    
    private final List<String> allCandidates;

    private final URI cvaApiURI;

    private static final Logger logger = Logger.getLogger(CheckerThread.class);
    
    private final Gson gson;

    public CheckerThread(CloseableHttpClient httpClient, List<String> allCandidates, List<String> candidates, URI cvaApiURI)
    {
	gson = new GsonBuilder().create();
	this.httpClient = httpClient;
	this.allCandidates = allCandidates;
	this.candidates = candidates;
	this.cvaApiURI = cvaApiURI;
    }

    public List<String> getAllCandidates()
    {
	return allCandidates;
    }
    
    /**
     * simple function to turn a list of strings into a comma
     * seperated string containing the array elements. no
     * need to include another dependency, if not even 10 lines 
     * of code can do the job.
     * 
     * @param input
     * @return 
     */
    private String implode(List<String> input)
    {
	StringBuilder builder = new StringBuilder();
	int size = input.size();
	if (size > 0)
	{
	    for (int i = 0; i < size - 1; i++)
	    {
		builder.append(input.get(i)).append(",");
	    }
	    builder.append(input.get(size - 1));
	}
	return builder.toString();
    }

    @Override
    public void run()
    {
	CheckerThreadResult result = new CheckerThreadResult();
	try
	{
	    if(!candidates.isEmpty())
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
		    String body = EntityUtils.toString(response.getEntity());
		    CVAResult cvaResult = gson.fromJson(body, CVAResult.class);
		    //TODO: check cva response code.
		    result.setResults(cvaResult.getResults());
		}
		else
		{
		    throw new CheckerThreadException("unable to query CVA api! status code was "+response.getStatusLine().getStatusCode()+".");
		}
	    }
	    else
	    {
		result.setResults(new ArrayList<CVAResultNode>());
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
