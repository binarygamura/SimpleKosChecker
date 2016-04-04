package de.fomad.simplekoschecker.view;

import de.fomad.simplekoschecker.controller.Controller;
import de.fomad.simplekoschecker.model.CheckerThreadResult;
import de.fomad.simplekoschecker.model.Constants;
import de.fomad.simplekoschecker.model.ControllerEvent;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.LogManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.log4j.Logger;
import org.jnativehook.GlobalScreen;

/**
 * @author binary gamura
 */
public class GUI extends JFrame implements Observer
{

    private static final Logger logger = Logger.getLogger(GUI.class);

    private transient Controller controller;

    /**
     * this thread keeps the gui always on top of other applications.
     */
    private transient Thread foregroundCheckerThread;

    /**
     * configuration of this application.
     */
    private Properties properties;

    private JProgressBar progressBar;

    private KosResultList list;

    private GUI()
    {
	super();
	setType(JFrame.Type.UTILITY);
    }

    private GUI initializeCore() throws IOException
    {
	properties = new Properties();
	try (InputStream propertyStream = GUI.class.getResourceAsStream(Constants.Common.configFileName))
	{
	    properties.load(propertyStream);
	    controller = new Controller(properties);
	    controller.addObserver(this);
	    controller.init();

	    //add implementation version from manifest to the properties.
	    //the following code comes from http://stackoverflow.com/a/1273432 	    
	    String version = "unknown";
	    Class clazz = GUI.class;
	    String className = clazz.getSimpleName() + ".class";
	    String classPath = clazz.getResource(className).toString();
	    if (classPath.startsWith("jar"))
	    {
		// Class not from JAR
		String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1)
			+ "/META-INF/MANIFEST.MF";
		Manifest manifest = new Manifest(new URL(manifestPath).openStream());
		Attributes attr = manifest.getMainAttributes();
		version = attr.getValue("Implementation-Version");
	    }
	    setTitle("Simple KOS checker (ver.:"+version+")");
	    properties.setProperty(Constants.ConfigKeys.currentVersion, version);
	    
	    return this;
	}
    }

    private GUI createContent()
    {
	setLayout(new BorderLayout());

	JButton infoButton = new JButton("info");
	infoButton.addActionListener(new ActionListener()
	{
	    @Override
	    public void actionPerformed(ActionEvent e)
	    {
		try
		{
		    Desktop.getDesktop().browse(new URI(properties.getProperty(Constants.ConfigKeys.fomadURI)));
		}
		catch (URISyntaxException ex)
		{
		    logger.warn("unable to open link to homepage! the uri seems invalid.", ex);
		}
		catch (IOException ex)
		{
		    logger.warn("unable to open link to homepage!", ex);
		}
	    }
	});
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new GridLayout(1, 0));
	buttonPanel.add(infoButton);

	list = new KosResultList();

	progressBar = new JProgressBar();
	progressBar.setStringPainted(true);

	add(progressBar, BorderLayout.NORTH);
	add(new JScrollPane(list), BorderLayout.CENTER);
	add(infoButton, BorderLayout.SOUTH);
	return this;
    }

    private void exitOperation()
    {
	SwingUtilities.invokeLater(new Runnable()
	{
	    @Override
	    public void run()
	    {
		dispose();
		controller.dispose();
		foregroundCheckerThread.interrupt();
	    }
	});
    }

    private void initAndShow() throws IOException
    {

	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	foregroundCheckerThread = new Thread(new ForegroundCheckerThread(this, Long.parseLong(properties.getProperty(Constants.ConfigKeys.foregroundInterval))));
	addWindowListener(new WindowAdapter()
	{
	    @Override
	    public void windowClosing(WindowEvent e)
	    {
		exitOperation();
	    }
	});
	setMinimumSize(new Dimension(200, 300));
	setFocusableWindowState(false);
	setFocusable(false);
	setAlwaysOnTop(true);
	validate();
	pack();
	setLocationRelativeTo(null);
	setVisible(true);

	foregroundCheckerThread.start();
    }

    public static void main(String[] args)
    {
	try
	{
	    //disable logging of JNativeHook
	    //see http://stackoverflow.com/questions/26565236/jnativehook-how-do-you-keep-from-printing-everything-that-happens
	    LogManager.getLogManager().reset();
	    java.util.logging.Logger jnativeLogger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
	    jnativeLogger.setLevel(Level.WARNING);

	    SwingUtilities.invokeLater(new Runnable()
	    {
		@Override
		public void run()
		{
		    try
		    {
			try
			{
			    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex)
			{
			    logger.warn("unable to set look and feel.", ex);
			}
			new GUI().initializeCore().createContent().initAndShow();
		    }
		    catch (IOException ex)
		    {
			logger.fatal("unable to load properties!", ex);
		    }
		}
	    });
	}
	catch (Exception ex)
	{
	    logger.error("unable to register native hooks!", ex);
	}
    }

    @Override
    public void update(Observable o, Object arg)
    {
	ControllerEvent result = (ControllerEvent) arg;
	switch (result.getType())
	{
	    case RESULT:
		progressBar.setIndeterminate(false);

		CheckerThreadResult response = result.getResult();
		if (response.hadError())
		{
		    progressBar.setString(response.getException().getMessage());
		}
		else
		{
		    list.setData(response.getResults());
		}
		logger.info("done! " + response.getResults().size());
		progressBar.setString("done! " + response.getResults().size());
		break;
	    case START:
		progressBar.setIndeterminate(true);
		progressBar.setString("progressing request...");
		break;
	}
    }
}
