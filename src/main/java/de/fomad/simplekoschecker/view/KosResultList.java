package de.fomad.simplekoschecker.view;

import de.fomad.simplekoschecker.model.CVAResultNode;
import de.fomad.simplekoschecker.model.Constants;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 *
 * @author binary gamura
 */
public class KosResultList extends JList<CVAResultNode>
{
    public KosResultList()
    {
	setCellRenderer(new KosResultListRenderer());
	setModel(new KosResultListModel());
    }
    
    public void setData(CVAResultNode[] data)
    {
	((KosResultListModel) getModel()).setData(data);
    }
    
    private static String getTextLabelFor(CVAResultNode node)
    {
	StringBuilder builder = new StringBuilder(node.getLabel());
	builder.append(" (").append(node.getCorp().getLabel());
	if(node.getCorp().getAlliance() != null)
	{
	    builder.append(" | ");
	    builder.append(node.getCorp().getAlliance().getLabel());
	}
	builder.append(")");
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
	public void setData(CVAResultNode[] data)
	{
	    removeAllElements();
	    for(CVAResultNode node : data)
	    {
		addElement(node);
	    }
	    fireContentsChanged(this, 0, data.length);
	}
    }
}
