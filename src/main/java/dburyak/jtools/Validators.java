package dburyak.jtools;


import java.nio.file.Files;
import java.nio.file.Path;


/**
 * Project : jtools.<br/>
 * Utility class.<br/>
 * Standard set of validation methods. <br/>
 * <b>Created on:</b> <i>2:13:08 AM Jul 22, 2015</i>
 *
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.2
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

    /**
     * Validates that string is not empty. Null string is treated as empty. <br/>
     * <b>PRE-conditions:</b> NONE <br/>
     * <b>POST-conditions:</b> NONE <br/>
     * <b>Side-effects:</b> NONE <br/>
     * <b>Created on:</b> <i>12:23:00 AM Jul 23, 2015</i>
     *
     * @param str
     *            string to be tested
     * @return true if string is not empty
     * @throws IllegalArgumentException
     *             if string is either null or empty
     */
    public static final boolean nonEmpty(final String str) {
        if ((str == null) || str.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return true;
    }

    /**
     * Validates that given path exists.
     * <br/><b>PRE-conditions:</b> NONE
     * <br/><b>POST-conditions:</b> NONE
     * <br/><b>Side-effects:</b> NONE
     * <br/><b>Created on:</b> <i>4:57:37 AM Aug 20, 2015</i>
     * 
     * @param filePath
     *            path to be validated
     * @return true if given path exists
     * @throws IllegalArgumentException
     *             if given file doesn't exist
     */
    public static final boolean exists(final Path filePath) {
        if ((filePath == null) || Files.exists(filePath)) {
            throw new IllegalArgumentException();
        }
        return true;
    }

}
