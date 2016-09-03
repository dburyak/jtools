package dburyak.jtools;


/**
 * Project : jtools.<br/>
 * Entity that has zero or more named properties and those can be changed or removed.
 * <br/><b>Created on:</b> <i>12:26:45 AM May 9, 2016</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
public interface IConfigurable extends IConfigured {

    /**
     * Set property.
     * <br/><b>PRE-conditions:</b> non-empty name, non-null value
     * <br/><b>POST-conditions:</b> NONE
     * <br/><b>Side-effects:</b> UNKNOWN
     * <br/><b>Created on:</b> <i>4:25:10 AM Aug 19, 2016</i>
     * 
     * @param key
     *            property name to be set
     * @param value
     *            property value to be set
     * @return previous value or null if no previous value has been set
     */
    public String property(final String key, final String value);

    /**
     * Remove property.
     * <br/><b>PRE-conditions:</b> non-empty name
     * <br/><b>POST-conditions:</b> NONE
     * <br/><b>Side-effects:</b> UNKNOWN
     * <br/><b>Created on:</b> <i>3:13:17 AM Aug 20, 2016</i>
     * 
     * @param key
     *            name of the property to be removed
     * @return previous value or null if no such property defined
     */
    public String removeProperty(final String key);

}
