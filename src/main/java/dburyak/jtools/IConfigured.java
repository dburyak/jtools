package dburyak.jtools;


/**
 * Project : jtools.<br/>
 * Configured entity that has zero or more properties. Property values are read-only.<br/>
 * Should be used by immutable classes.
 * <br/><b>Created on:</b> <i>3:06:42 AM Aug 20, 2016</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
public interface IConfigured {

    /**
     * Get property.
     * <br/><b>PRE-conditions:</b> non-empty name
     * <br/><b>POST-conditions:</b> NONE
     * <br/><b>Side-effects:</b> UNKNOWN
     * <br/><b>Created on:</b> <i>4:22:33 AM Aug 19, 2016</i>
     * 
     * @param key
     *            property name to be retrieved
     * @return value of the property or null if no such property configured
     */
    public String property(final String key);

}
