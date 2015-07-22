package dburyak.jtools;


/**
 * Project : jtools.<br/>
 * Utility class.<br/>
 * Standard set of validation methods. <br/>
 * <b>Created on:</b> <i>2:13:08 AM Jul 22, 2015</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
public final class Validators {
    
    /**
     * Constructor for class : [jtools] dburyak.jtools.Validators.<br/>
     * Constructor is private to prevent instantiation of this utility class. <br/>
     * <b>PRE-conditions:</b> NONE <br/>
     * <b>POST-conditions:</b> NONE <br/>
     * <b>Side-effects:</b> NONE <br/>
     * <b>Created on:</b> <i>2:14:58 AM Jul 22, 2015</i>
     */
    private Validators() {
        throw new AssertionError("Utility class, not supposed to be instantiated"); //$NON-NLS-1$
    }
    
    /**
     * Validates that argument is not null. <br/>
     * <b>PRE-conditions:</b> NONE <br/>
     * <b>POST-conditions:</b> NONE <br/>
     * <b>Side-effects:</b> NONE <br/>
     * <b>Created on:</b> <i>2:16:57 AM Jul 22, 2015</i>
     * 
     * @param obj
     *            object to be checked
     * @return true if object is not null
     * @throws IllegalArgumentException
     *             if object is null
     */
    public static final boolean nonNull(final Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException();
        }
        return true;
    }
    
}
