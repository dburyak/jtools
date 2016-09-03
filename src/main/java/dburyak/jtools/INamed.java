package dburyak.jtools;


/**
 * Project : jtools.<br/>
 * Named entity.
 * <br/><b>Created on:</b> <i>12:26:07 AM May 9, 2016</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
public interface INamed {

    /**
     * Get name of entity.
     * <br/><b>PRE-conditions:</b> NONE
     * <br/><b>POST-conditions:</b> non-empty result
     * <br/><b>Side-effects:</b> UNKNOWN
     * <br/><b>Created on:</b> <i>4:27:17 AM Aug 19, 2016</i>
     * 
     * @return name of this entity
     */
    public String name();

}
