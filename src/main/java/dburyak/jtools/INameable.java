package dburyak.jtools;


/**
 * Project : jtools.<br/>
 * Entity that can be given name to.
 * <br/><b>Created on:</b> <i>4:41:26 AM Aug 20, 2016</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
public interface INameable extends INamed {

    /**
     * Set name.
     * <br/><b>PRE-conditions:</b> non-empty name
     * <br/><b>POST-conditions:</b> NONE
     * <br/><b>Side-effects:</b> UNKNOWN
     * <br/><b>Created on:</b> <i>4:42:10 AM Aug 20, 2016</i>
     * 
     * @param name
     *            name to be set
     * @return previous name or null if no name has been specified yet
     */
    public String name(final String name);

}
