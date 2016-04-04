package de.fomad.simplekoschecker.view;

import de.fomad.simplekoschecker.model.CVAResultNode;
import de.fomad.simplekoschecker.model.Constants;
import java.awt.Color;
import java.awt.Component;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 *
 * @author binary gamura
 */
public class KosResultList extends JList<CVAResultNode>
{
    private static final Logger logger = Logger.getLogger(KosResultList.class);
    
    private final KosResultListComparator comparator;
    
    public KosResultList()
    {
	setCellRenderer(new KosResultListRenderer());
	setModel(new KosResultListModel());
	comparator = new KosResultListComparator();
    }
    
    public void setData(final List<CVAResultNode> data)
    {
	Collections.sort(data, comparator);
	SwingUtilities.invokeLater(new Runnable()
	{
	    @Override
	    public void run()
	    {
		((KosResultListModel) getModel()).setData(data);
		logger.info("done setting list data!"+data.size());
		revalidate();
	    }
	});
    }
    
    private static String getTextLabelFor(CVAResultNode node)
    {
	StringBuilder builder = new StringBuilder(node.getLabel());
	if(node.getCorp() != null )
	{
	    builder.append(" (").append(node.getCorp().getLabel());
	    if(node.getCorp().getAlliance() != null)
	    {
		builder.append(" | ");
		builder.append(node.getCorp().getAlliance().getLabel());
	    }
	    builder.append(")");
	}
	return builder.toString();
    }
    
    private static Color getColorFor(CVAResultNode node)
    {
	Color color; // = Constants.Colors.noResultColor;
	if(node.computeKos())
	{
	    color = Constants.Colors.kosColor;
	}
	else
	{
	    color = Constants.Colors.notColor;
	}
	return color;
    }
    
    public static class KosResultListComparator implements Comparator<CVAResultNode>
    {

	private final Collator stringCollator = Collator.getInstance();
	
	@Override
	public int compare(CVAResultNode o1, CVAResultNode o2)
	{
	    int result;
	    boolean kosA = o1.computeKos();
	    boolean kosB = o2.computeKos();
	    if(kosA == kosB)
	    {
		result = stringCollator.compare(o1.getLabel(), o2.getLabel());
	    }
	    else
	    {
		if(kosA)
		{
		    result = -1;
		}
		else
		{
		    result = 1;
		}
	    }
	    return result;
	}
	
    }
    
    private static class KosResultListRenderer extends DefaultListCellRenderer
    {
	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
	    
	    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); 
	    CVAResultNode node = (CVAResultNode) value;
	    this.setText(getTextLabelFor(node));
	    this.setBackground(getColorFor(node));
	    return this;
	}
    }
    
    private static class KosResultListModel extends DefaultListModel<CVAResultNode>
    {
	public void setData(List<CVAResultNode> data)
	{
	    removeAllElements();
	    for(CVAResultNode node : data)
	    {
		addElement(node);
	    }
	    fireContentsChanged(this, 0, data.size());
	}
    }
}
