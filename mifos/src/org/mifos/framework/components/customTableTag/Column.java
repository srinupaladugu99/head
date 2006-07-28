package org.mifos.framework.components.customTableTag;

import java.util.List;
import java.util.Locale;
import java.lang.reflect.*;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.mifos.application.configuration.business.ConfigurationIntf;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.framework.exceptions.TableTagParseException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.LabelTagUtils;

public class Column {

	private String label = null;

	private String value = null;

	private String valueType = null;

	private String columnType = null;

	private ColumnDetails columnDetails = null;

	private LinkDetails linkDetails = null;

	private ConfigurationIntf labelConfig = MifosConfiguration.getInstance();

	public void setLinkDetails(LinkDetails linkDetails) {
		this.linkDetails = linkDetails;
	}

	public LinkDetails getLinkDetails() {
		return linkDetails;
	}

	public void setColumnDetials(ColumnDetails columnDetails) {
		this.columnDetails = columnDetails;
	}

	public ColumnDetails getColumnDetails() {
		return columnDetails;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	public String getValueType() {
		return valueType;
	}

	public String getColumnType() {
		return columnType;
	}

	public void getColumnHeader(StringBuilder tableInfo,
			PageContext pageContext, String bundle) throws JspException {
		tableInfo.append("<td ");
		tableInfo
				.append(" width=\"" + getColumnDetails().getColWidth() + "%\"");
		tableInfo.append(" align=\"" + getColumnDetails().getAlign() + "\" ");
		tableInfo.append(">");
		if (getLabel().replaceAll("", "").equals("") || getLabel() == null)
			tableInfo.append("&nbsp;");
		else
			tableInfo.append("<b>"
					+ getLabelText(pageContext, getLabel(), bundle) + "</b>");
		tableInfo.append("</td>");
	}

	public void generateTableColumn(StringBuilder tableInfo, Object obj,
			Locale locale, Locale prefferedLocale, Locale mfiLocale)
			throws TableTagParseException {
		tableInfo.append("<td class=\"" + getColumnDetails().getRowStyle()
				+ "\"  ");

		tableInfo.append(" align=\"" + getColumnDetails().getAlign() + "\" ");
		tableInfo.append("> ");

		if (getValueType().equalsIgnoreCase(TableTagConstants.METHOD)) {
			if (getColumnType().equalsIgnoreCase(TableTagConstants.TEXT)) {
				getTableColumn(tableInfo, obj, locale, prefferedLocale,
						mfiLocale);
			} else {
				// Generate Link On Column
				getTableColumnWithLink(tableInfo, obj, locale, prefferedLocale,
						mfiLocale);
			}
		} else {
			if (getColumnType().equalsIgnoreCase(TableTagConstants.TEXT)) {
				// ColumnType should be link
				throw new TableTagParseException();
			}

			getLinkWithoutName(tableInfo, obj);
			tableInfo.append("");
		}

		tableInfo.append("</td>");

	}

	public void getTableColumn(StringBuilder tableInfo, Object obj,
			Locale locale, Locale prefferedLocale, Locale mfiLocale)
			throws TableTagParseException {
		Method[] methods = obj.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equalsIgnoreCase("get".concat(getValue()))) {
				try {
					tableInfo.append(methods[i].invoke(obj, new Object[] {}));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new TableTagParseException();
				} catch (InvocationTargetException ex) {
					ex.printStackTrace();
					throw new TableTagParseException();
				}
			}
			if (methods[i].getName().equalsIgnoreCase("setLocale")
					&& locale != null) {
				try {
					Object[] argumentLocale = new Object[] { locale };
					methods[i].invoke(obj, argumentLocale);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new TableTagParseException();
				} catch (InvocationTargetException ex) {
					ex.printStackTrace();
					throw new TableTagParseException();
				}
			}
			if (methods[i].getName().equalsIgnoreCase("setMfiLocale")
					&& mfiLocale != null) {
				try {
					Object[] argumentLocale = new Object[] { mfiLocale };
					methods[i].invoke(obj, argumentLocale);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					throw new TableTagParseException();
				} catch (InvocationTargetException ex) {
					ex.printStackTrace();
					throw new TableTagParseException();
				}
			}
		}
	}

	public void getTableColumnWithLink(StringBuilder tableInfo, Object obj,
			Locale locale, Locale prefferedLocale, Locale mfiLocale)
			throws TableTagParseException {
		tableInfo.append("<a ");
		linkDetails.generateLink(tableInfo, obj);
		tableInfo.append(" >");
		getTableColumn(tableInfo, obj, locale, prefferedLocale, mfiLocale);
		tableInfo.append("</a>");
	}

	public void getLinkWithoutName(StringBuilder tableInfo, Object obj)
			throws TableTagParseException {
		tableInfo.append("<a ");
		linkDetails.generateLink(tableInfo, obj);
		tableInfo.append(" >");
		tableInfo.append(getValue());
		tableInfo.append("</a>");
	}

	private String getLabelText(PageContext pageContext, String key,
			String bundle) throws JspException {

		UserContext userContext = (UserContext) pageContext.getSession()
				.getAttribute(Constants.USER_CONTEXT_KEY);
		LabelTagUtils labelTagUtils = LabelTagUtils.getInstance();
		String labelText = null;
		try {
			labelText = labelConfig.getLabel(key, userContext
					.getPereferedLocale());
		} catch (ConfigurationException e) {
			// ignore

		}
		
			if (labelText == null)try {
				labelText = labelTagUtils.getLabel(pageContext, bundle,
						userContext.getPereferedLocale(), key, null);
		} catch (Exception e) {
		}
		
			if (labelText == null)try {
				char[] charArray = bundle.toCharArray();
				charArray[0] = Character.toUpperCase(charArray[0]);
				bundle = new String(charArray);
				labelText = labelTagUtils.getLabel(pageContext, bundle,
						userContext.getPereferedLocale(), key, null);
			
		} catch (Exception e) {
			labelText = key;

		}
		return labelText;
	}
}
