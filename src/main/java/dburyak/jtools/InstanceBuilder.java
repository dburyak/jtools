package dburyak.jtools;


/**
 * Project : jtools.<br/>
 * Builds an instance of the specified type. Should be used as a better substitution for the telescoping constructor,
 * when there is a need to create an object and pass lots of parameters. <br/>
 * <b>Created on:</b> <i>2:10:36 AM May 18, 2015</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 * @param <T>
 *            type this builder can build instances of
 */
public interface InstanceBuilder<T> {
    
    /**
     * Build an instance of type T based on current state of this builder. <br/>
     * <b>PRE-conditions:</b> builder is in valid state (state doesn't violate the invariant of T)<br/>
     * <b>POST-conditions:</b> non-null result <br/>
     * <b>Side-effects:</b> none <br/>
     * <b>Created on:</b> <i>2:12:32 AM May 18, 2015</i>
     * 
     * @return a new instance of type T based on state ot this builder
     * @throws IllegalStateException
     *             if builder state violates invariant
     */
    public T build() throws IllegalStateException;
    
    /**
     * Indicates whether this builder is in valid state (doesn't violate the associated T invariant). <br/>
     * This method can be used to test correctness without trying to instantiate T object. With this style we are
     * avoiding working with {@link IllegalStateException} (can be used in assertions). <br/>
     * <b>PRE-conditions:</b> NONE <br/>
     * <b>POST-conditions:</b> NONE <br/>
     * <b>Side-effects:</b> NONE <br/>
     * <b>Created on:</b> <i>2:20:03 AM May 18, 2015</i>
     * 
     * @return true if this builder is in valid state and thus can build a new instance of T
     */
    public boolean isValid();
    
}
