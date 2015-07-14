package dburyak.apptools;


/**
 * Project : jtools.<br/>
 * Enumeration of assertion cases. Internally holds string constatns. <br/>
 * <b>Created on:</b> <i>12:55:51 AM Aug 25, 2014</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 1.2
 */
public enum AssertConst {
        
        /**
         * Null argument passed into method. <br/>
         * <b>Created on:</b> <i>1:18:55 AM Nov 22, 2014</i>
         */
        ASRT_NULL_ARG("null argument"), //$NON-NLS-1$
        
        /**
         * Trying to return null result. <br/>
         * <b>Created on:</b> <i>1:20:33 AM Aug 25, 2014</i>
         */
        ASRT_NULL_RESULT("null result"), //$NON-NLS-1$
        
        /**
         * Invalid argument passed. Parameters invariant violation. <br/>
         * <b>Created on:</b> <i>1:20:42 AM Aug 25, 2014</i>
         */
        ASRT_INVALID_ARG("invalid argument"), //$NON-NLS-1$
        
        /**
         * Trying to return invalid result. Result invariant violation. <br/>
         * <b>Created on:</b> <i>1:20:50 AM Aug 25, 2014</i>
         */
        ASRT_INVALID_RESULT("invalid result"), //$NON-NLS-1$
        
        /**
         * Invalid intermediate result. <br/>
         * <b>Created on:</b> <i>1:20:56 AM Aug 25, 2014</i>
         */
        ASRT_INVALID_VALUE("invalid value"), //$NON-NLS-1$
        
        /**
         * <code>Null</code> intermediate result. <br/>
         * <b>Created on:</b> <i>1:22:12 AM Aug 25, 2014</i>
         */
        ASRT_NULL_VALUE("null value"); //$NON-NLS-1$
    
    
    /**
     * Message to be used in {@link Object#toString()} for enumeration constant. <br/>
     * <b>Created on:</b> <i>1:27:10 AM Nov 22, 2014</i>
     */
    private final String message;
    
    
    /**
     * Constructor for class : [jtools] dburyak.apptools.AssertConst.<br/>
     * <br/>
     * <b>Created on:</b> <i>1:28:58 AM Nov 22, 2014</i>
     * 
     * @param message
     *            to be printed for constant
     */
    private AssertConst(final String message) {
        this.message = message;
    }
    
    /**
     * Gives the text representation for assert constant. <br/>
     * <b>PRE-conditions:</b> NONE<br/>
     * <b>POST-conditions:</b> NONE<br/>
     * <b>Side-effects:</b> NONE<br/>
     * <b>Created on:</b> <i>1:28:30 AM Nov 22, 2014</i>
     * 
     * @see java.lang.Enum#toString()
     * @return text message for this type of assertion
     */
    @Override
    public String toString() {
        return message;
    }
}
