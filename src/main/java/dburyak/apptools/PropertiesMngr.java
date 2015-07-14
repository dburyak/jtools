package dburyak.apptools;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Project : jtools.<br/>
 * Singleton for managing application preferences. <br/>
 * <b>Created on:</b> <i>1:32:38 AM Nov 22, 2014</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
@ThreadSafe
public final class PropertiesMngr {
    
    /**
     * Project : jtools.<br/>
     * Available property keys for application. Designed for inheritance. <br/>
     * To define a set of properties keys, this interface should be implemented. This allows compile-time type safety. <br/>
     * Should be used with {@link PropertiesMngr} class. <br/>
     * <b>Created on:</b> <i>1:56:29 AM Nov 22, 2014</i>
     * 
     * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
     * @version 0.1
     */
    public static interface PropsKeys {
        
        /**
         * Get all properties keys supported for this application. <br/>
         * <b>PRE-conditions:</b> NONE <br/>
         * <b>POST-conditions:</b> not null result<br/>
         * <b>Side-effects:</b> NONE<br/>
         * <b>Created on:</b> <i>3:24:42 AM Nov 22, 2014</i>
         * 
         * @return collection of all supported properties keys
         */
        public Set<String> getSupportedKeys();
    }
    
    
    /**
     * Default system logger for this class. <br/>
     * <b>Created on:</b> <i>1:51:23 AM Nov 22, 2014</i>
     */
    private static final Logger LOGGER = LogManager.getFormatterLogger(PropertiesMngr.class);
    
    /**
     * Empty property, equivalent of null. <br/>
     * <b>Created on:</b> <i>3:53:17 AM Nov 23, 2014</i>
     */
    private static final String EMPTY_PROP = ""; //$NON-NLS-1$
    
    /**
     * {@link ReadWriteLock} lock used for access to {@link PropertiesMngr#propsInternal} data. <br/>
     * <b>Created on:</b> <i>2:09:21 AM Nov 22, 2014</i>
     */
    private static final ReadWriteLock DATA_LOCK = new ReentrantReadWriteLock();
    
    /**
     * Used for controlling access to {@link PropertiesMngr#DEFAULT_PROPS} object. <br/>
     * <b>Created on:</b> <i>2:50:00 AM Nov 22, 2014</i>
     */
    private static final ReadWriteLock DEFAULTS_LOCK = new ReentrantReadWriteLock();
    
    /**
     * Default properties. If there is a need of using default properties, then this value must be redefined by calling
     * {@link PropertiesMngr#getInstance(PropsKeys, Properties)}. <br/>
     * <b>Created on:</b> <i>2:16:19 AM Nov 22, 2014</i>
     */
    @GuardedBy("DEFAULTS_LOCK")
    private static Properties DEFAULT_PROPS = null;
    
    
    /**
     * Instance holder for singleton instance of this class. <br/>
     * <b>Created on:</b> <i>2:09:56 AM Nov 22, 2014</i>
     */
    private static final class InstanceHolder {
        
        /**
         * Singleton instance of enclosing class. <br/>
         * <b>Created on:</b> <i>2:23:44 AM Nov 22, 2014</i>
         */
        @SuppressWarnings("synthetic-access")
        private static final PropertiesMngr INSTANCE = new PropertiesMngr();
    }
    
    
    /**
     * Internal standard java properties object. All requests for managing properties are redirected to this object. <br/>
     * <b>Created on:</b> <i>1:48:23 AM Nov 22, 2014</i>
     */
    @GuardedBy("DATA_LOCK")
    private final Properties propsInternal;
    
    /**
     * Holds all supported property keys for application.<br/>
     * <b>Created on:</b> <i>3:50:31 AM Nov 22, 2014</i>
     */
    @GuardedBy("DATA_LOCK")
    private PropsKeys supportedPropsKeys;
    
    
    /**
     * Check if all properties a valid, i.e. all of property keys are valid. <br/>
     * <b>PRE-conditions:</b> not null args<br/>
     * <b>POST-conditions:</b> NONE<br/>
     * <b>Side-effects:</b> NONE<br/>
     * <b>Created on:</b> <i>2:45:42 AM Nov 23, 2014</i>
     * 
     * @param supportedKeys
     *            supported property keys for this application
     * @param props
     *            properties to validate
     * @return true if all properties keys are valid, false otherwise
     */
    private static final boolean validateProps(final PropsKeys supportedKeys, final Properties props) {
        LOGGER.entry(supportedKeys, props);
        assert (supportedKeys != null) : AssertConst.ASRT_NULL_ARG;
        assert (props != null) : AssertConst.ASRT_NULL_ARG;
        
        boolean result = true;
        for (final Object propKey : props.keySet()) {
            if (!supportedKeys.getSupportedKeys().contains(propKey.toString())
                || props.getProperty(propKey.toString()) == null) {
                // property key is invalid, or property is null
                result = false;
                LOGGER.error("invalid property detected: propKey = [%s] ; propValue = [%s]",  //$NON-NLS-1$
                    propKey,
                    props.getProperty(propKey.toString()));
            }
        }
        
        return LOGGER.exit(result);
    }
    
    /**
     * Standard singleton factory method. Cannot be called for the first time. For the first time call use
     * initialization factory methods instead ( {@link PropertiesMngr#getInstance(PropsKeys)} ,
     * {@link PropertiesMngr#getInstance(PropsKeys, Path)} , {@link PropertiesMngr#getInstance(PropsKeys, Properties)}) <br/>
     * <b>PRE-conditions:</b> one of the initialization factory methods should be called<br/>
     * <b>POST-conditions:</b> result not null<br/>
     * <b>Side-effects:</b> NONE<br/>
     * <b>Created on:</b> <i>3:14:20 AM Nov 23, 2014</i>
     * 
     * @return singleton instance of this class
     */
    @SuppressWarnings("synthetic-access")
    public static final PropertiesMngr getInstance() {
        LOGGER.entry();
        
        DEFAULTS_LOCK.readLock().lock();
        try {
            if (DEFAULT_PROPS == null) {
                // this method should be called after initialization factory methods
                throw LOGGER.throwing(new IllegalStateException("this method should be called after any of the " //$NON-NLS-1$
                    + "initialization factory methods")); //$NON-NLS-1$
            }
        } finally {
            DEFAULTS_LOCK.readLock().unlock();
        }
        
        return LOGGER.exit(InstanceHolder.INSTANCE);
    }
    
    /**
     * Standard singleton factory method. <br/>
     * <b>PRE-conditions:</b> not null arg<br/>
     * <b>POST-conditions:</b> {@link PropertiesMngr#DEFAULT_PROPS} not null ; result not null<br/>
     * <b>Side-effects:</b> using empty default properties, so after this call, call of
     * {@link PropertiesMngr#getInstance(PropsKeys, Properties)} will always throw an {@link IllegalStateException}<br/>
     * <b>Created on:</b> <i>2:53:15 AM Nov 22, 2014</i>
     * 
     * @param allPropsKeys
     *            all supported properties keys
     * @return singleton instance of {@link PropertiesMngr}
     */
    @SuppressWarnings("synthetic-access")
    public static final PropertiesMngr getInstance(final PropsKeys allPropsKeys) {
        LOGGER.entry(allPropsKeys);
        if (allPropsKeys == null) {
            throw LOGGER.throwing(new NullArgumentException("allPropsKeys")); //$NON-NLS-1$
        }
        
        DEFAULTS_LOCK.readLock().lock();
        try {
            if (DEFAULT_PROPS != null) {
                // this method couldn't be called twice
                throw LOGGER.throwing(new IllegalStateException("this method can be called only once and only " //$NON-NLS-1$
                    + "for the first time")); //$NON-NLS-1$
            }
        } finally {
            DEFAULTS_LOCK.readLock().unlock();
        }
        
        DATA_LOCK.writeLock().lock();
        try {
            InstanceHolder.INSTANCE.supportedPropsKeys = allPropsKeys;
        } finally {
            DATA_LOCK.writeLock().unlock();
        }
        return LOGGER.exit(InstanceHolder.INSTANCE);
    }
    
    /**
     * Singleton factory method with initialization. <br/>
     * <b>PRE-conditions:</b> {@link PropertiesMngr#DEFAULT_PROPS} is null ; this method is called for the first time ;
     * {@link PropertiesMngr} has never been called before ; defaultProps argument not null <br/>
     * <b>POST-conditions:</b> {@link PropertiesMngr#DEFAULT_PROPS} not null ; result not null<br/>
     * <b>Side-effects:</b> using provided default properties, so after this call, only
     * {@link PropertiesMngr#getInstance(PropsKeys)} should be called for obtaining the instance of this class<br/>
     * <b>Created on:</b> <i>2:57:53 AM Nov 22, 2014</i>
     * 
     * @param defaultProps
     *            default properties to be used in case if only few properties will be redefined
     * @param supportedPropsKeys
     *            all supported properties keys
     * @return singleton instance of {@link PropertiesMngr}
     */
    @SuppressWarnings("synthetic-access")
    public static final PropertiesMngr getInstance(final PropsKeys supportedPropsKeys, final Properties defaultProps) {
        LOGGER.entry(supportedPropsKeys, defaultProps);
        if (supportedPropsKeys == null) {
            throw LOGGER.throwing(new NullArgumentException("supportedPropsKeys")); //$NON-NLS-1$
        }
        if (defaultProps == null) {
            throw LOGGER.throwing(new NullArgumentException("defaultProps")); //$NON-NLS-1$
        }
        
        // check if default properties contain any illegal property
        if (!validateProps(supportedPropsKeys, defaultProps)) {
            LOGGER.error("unsupported property in defaultProps detected"); //$NON-NLS-1$
            throw LOGGER.throwing(new IllegalArgumentException(AssertConst.ASRT_INVALID_ARG.toString()));
        }
        LOGGER.debug("using default application properties: defaultProps = [%s]", defaultProps); //$NON-NLS-1$
        
        DEFAULTS_LOCK.writeLock().lock();
        try {
            if (DEFAULT_PROPS != null) {
                // this method couldn't be called twice
                throw LOGGER.throwing(new IllegalStateException("this method can be called only once and only " //$NON-NLS-1$
                    + "for the first time")); //$NON-NLS-1$
            }
            DEFAULT_PROPS = defaultProps;
        } finally {
            DEFAULTS_LOCK.writeLock().unlock();
        }
        
        DATA_LOCK.writeLock().lock();
        try {
            InstanceHolder.INSTANCE.supportedPropsKeys = supportedPropsKeys;
        } finally {
            DATA_LOCK.writeLock().unlock();
        }
        return LOGGER.exit(InstanceHolder.INSTANCE);
    }
    
    /**
     * Singleton factory method with initialization. Reads default properties from file.<br/>
     * <b>PRE-conditions:</b> {@link PropertiesMngr#DEFAULT_PROPS} is null ; this method is called for the first time ;
     * {@link PropertiesMngr} has never been called before ; defaultPropsFile is a valid properties file<br/>
     * <b>POST-conditions:</b> {@link PropertiesMngr#DEFAULT_PROPS} not null ; result not null <br/>
     * <b>Side-effects:</b> using provided default properties, so after this call, only
     * {@link PropertiesMngr#getInstance(PropsKeys)} should be called for obtaining the instance of this class<br/>
     * <b>Created on:</b> <i>4:58:40 AM Nov 22, 2014</i>
     * 
     * @param supportedPropsKeys
     *            all supported properties keys
     * @param defaultPropsFile
     *            path to file with default properties
     * @return singleton instance of {@link PropertiesMngr}
     * @throws FileNotFoundException
     *             when properties file not found
     * @throws IOException
     *             when errors while accessing file
     */
    public static final PropertiesMngr getInstance(final PropsKeys supportedPropsKeys, final Path defaultPropsFile)
        throws FileNotFoundException, IOException {
        LOGGER.entry(supportedPropsKeys, defaultPropsFile);
        
        if (supportedPropsKeys == null) {
            throw LOGGER.throwing(new NullArgumentException("supportedPropsKeys")); //$NON-NLS-1$
        }
        if (defaultPropsFile == null) {
            throw LOGGER.throwing(new NullArgumentException("defaultPropsFile")); //$NON-NLS-1$
        }
        
        final Properties defaultProps = new Properties();
        try (final BufferedReader bIN = Files.newBufferedReader(defaultPropsFile)) {
            defaultProps.load(bIN);
            if (!validateProps(supportedPropsKeys, defaultProps)) {
                LOGGER.error("default properties file contains usupported properties: defaultPropsFile = [%s]", //$NON-NLS-1$
                    defaultPropsFile);
                throw LOGGER.throwing(new IllegalArgumentException("default properties file contains usupported " //$NON-NLS-1$
                    + "properties")); //$NON-NLS-1$
            }
        } catch (final FileNotFoundException e) {
            LOGGER.error("default properties file not found: defaultPropsFile = [%s]", defaultPropsFile); //$NON-NLS-1$
            throw LOGGER.throwing(e);
        } catch (final IOException e) {
            LOGGER.error("error when accessing file: defaultPropsFile = [%s]", defaultPropsFile); //$NON-NLS-1$
            throw LOGGER.throwing(e);
        }
        
        return LOGGER.exit(getInstance(supportedPropsKeys, defaultProps));
    }
    
    /**
     * Constructor for class : [jtools] dburyak.apptools.PropertiesMngr.<br/>
     * Made private for controlling instance creation (singleton). <br/>
     * Will never be invoked concurrently because is used only in {@link InstanceHolder} and only once. So it's OK to
     * acquire write lock for simplicity.<br/>
     * <b>Side-effects:</b> lock acquisition order: {@link PropertiesMngr#DEFAULTS_LOCK} ,
     * {@link PropertiesMngr#DATA_LOCK}<br/>
     * <br/>
     * <b>Created on:</b> <i>1:53:09 AM Nov 22, 2014</i>
     */
    private PropertiesMngr() {
        LOGGER.entry();
        
        DEFAULTS_LOCK.writeLock().lock();
        try {
            if (DEFAULT_PROPS == null) {
                DATA_LOCK.writeLock().lock();
                try {
                    propsInternal = new Properties(); // empty properties without defaults
                    DEFAULT_PROPS = propsInternal; // set no matter to what, but not null
                } finally {
                    DATA_LOCK.writeLock().unlock();
                }
            } else {
                DATA_LOCK.writeLock().lock();
                try {
                    propsInternal = new Properties(DEFAULT_PROPS); // using default properties
                } finally {
                    DATA_LOCK.writeLock().unlock();
                }
            }
        } finally {
            DEFAULTS_LOCK.writeLock().unlock();
        }
        
        LOGGER.exit();
    }
    
    /**
     * Loads properties from specified file. Then merges loaded properties with current. All loaded properties override
     * current.<br/>
     * <b>PRE-conditions:</b> not null argument ; file exists ; file accessible ; file is a well-formed properties file<br/>
     * <b>POST-conditions:</b> NONE<br/>
     * <b>Side-effects:</b> contents of internal properties are changed<br/>
     * <b>Created on:</b> <i>5:23:34 AM Nov 22, 2014</i>
     * 
     * @param propsFilePath
     *            path to file with properties to be loaded
     * @throws FileNotFoundException
     *             if file cannot be found
     * @throws IOException
     *             when cannot access file
     */
    @SuppressWarnings("synthetic-access")
    public final void loadProperties(final Path propsFilePath) throws FileNotFoundException, IOException {
        LOGGER.entry(propsFilePath);
        if (propsFilePath == null) {
            throw LOGGER.throwing(new NullArgumentException("propsFilePath")); //$NON-NLS-1$
        }
        
        try (final BufferedReader bIN = Files.newBufferedReader(propsFilePath)) {
            final Properties newProps = new Properties();
            newProps.load(bIN);
            PropsKeys supportedKeys = null;
            DATA_LOCK.readLock().lock();
            try {
                supportedKeys = InstanceHolder.INSTANCE.supportedPropsKeys;
            } finally {
                DATA_LOCK.readLock().unlock();
            }
            if (!validateProps(supportedKeys, newProps)) {
                LOGGER.error("properties file contains usupported properties: propsFilePath = [%s]", propsFilePath); //$NON-NLS-1$
                throw LOGGER.throwing(new IllegalArgumentException("properties file contains usupported properties")); //$NON-NLS-1$
            }
            loadProperties(newProps);
        } catch (final FileNotFoundException e) {
            LOGGER.error("properties file not found: filePath = [%s]", propsFilePath); //$NON-NLS-1$
            throw LOGGER.throwing(e);
        } catch (final IOException e) {
            LOGGER.error("error when accessing file: filePath = [%s]", propsFilePath); //$NON-NLS-1$
            throw LOGGER.throwing(e);
        }
        
        LOGGER.exit();
    }
    
    /**
     * Merges current properties with provided. All new properties override previous. <br/>
     * <b>PRE-conditions:</b> arg not null<br/>
     * <b>POST-conditions:</b> NONE<br/>
     * <b>Side-effects:</b> state of {@link PropertiesMngr#propsInternal} is changed<br/>
     * <b>Created on:</b> <i>2:58:48 AM Nov 23, 2014</i>
     * 
     * @param newProps
     *            new properties to load.
     */
    @SuppressWarnings("synthetic-access")
    public final void loadProperties(final Properties newProps) {
        LOGGER.entry(newProps);
        if (newProps == null) {
            throw LOGGER.throwing(new NullArgumentException("newProps")); //$NON-NLS-1$
        }
        
        // check properties validity
        PropsKeys supportedKeys = null;
        DATA_LOCK.readLock().lock();
        try {
            supportedKeys = InstanceHolder.INSTANCE.supportedPropsKeys;
        } finally {
            DATA_LOCK.readLock().unlock();
        }
        if (!validateProps(supportedKeys, newProps)) {
            final String msg = "unsupported propery"; //$NON-NLS-1$
            LOGGER.error(msg);
            throw LOGGER.throwing(new IllegalArgumentException(msg));
        }
        LOGGER.debug("updating properties: newProps = [%s]", newProps); //$NON-NLS-1$
        
        DATA_LOCK.writeLock().lock();
        try {
            propsInternal.putAll(newProps);
        } finally {
            DATA_LOCK.writeLock().unlock();
        }
        
        LOGGER.exit();
    }
    
    /**
     * Get property. <br/>
     * <b>PRE-conditions: not null arg ; property key is valid for this application</b> <br/>
     * <b>POST-conditions:</b> not null result<br/>
     * <b>Side-effects:</b> NONE<br/>
     * <b>Created on:</b> <i>3:53:58 AM Nov 23, 2014</i>
     * 
     * @param key
     *            key for the requested property
     * @return value of a requested property
     */
    public final String getProperty(final String key) {
        LOGGER.entry(key);
        if (key == null) {
            throw LOGGER.throwing(new NullArgumentException("key")); //$NON-NLS-1$
        }
        
        String result = EMPTY_PROP;
        DATA_LOCK.readLock().lock();
        try {
            if (!supportedPropsKeys.getSupportedKeys().contains(key.toString())) {
                LOGGER.warn("unsupported property requested: key = [%s]", key); //$NON-NLS-1$
                throw LOGGER.throwing(new IllegalArgumentException("unsupported property")); //$NON-NLS-1$
            }
            result = propsInternal.getProperty(key);
        } finally {
            DATA_LOCK.readLock().unlock();
        }
        LOGGER.debug("property retrieved: key = [%s] ; value = [%s]", key, result); //$NON-NLS-1$
        
        if (result == null) {
            LOGGER.error("null property detected: key = [%s]", key); //$NON-NLS-1$
            throw LOGGER.throwing(new IllegalStateException("null property detected")); //$NON-NLS-1$
        }
        
        return LOGGER.exit(result);
    }
    
    /**
     * Set property. <br/>
     * <b>PRE-conditions: not null args ; valid key</b> <br/>
     * <b>POST-conditions:</b> NONE<br/>
     * <b>Side-effects:</b> {@link PropertiesMngr#propsInternal} data changed<br/>
     * <b>Created on:</b> <i>4:36:08 AM Nov 23, 2014</i>
     * 
     * @param key
     *            property key
     * @param value
     *            property value
     * @return previous property value, null if this property wasn't set
     */
    public final String setProperty(final String key, final String value) {
        LOGGER.entry(key, value);
        if (key == null) {
            throw LOGGER.throwing(new NullArgumentException("key")); //$NON-NLS-1$
        }
        if (value == null) {
            throw LOGGER.throwing(new NullArgumentException("value")); //$NON-NLS-1$
        }
        
        String result = EMPTY_PROP;
        Object prevValue = null;
        DATA_LOCK.writeLock().lock();
        try {
            if (!supportedPropsKeys.getSupportedKeys().contains(key.toString())) {
                LOGGER.warn("unsupported property requested: key = [%s]", key); //$NON-NLS-1$
                throw LOGGER.throwing(new IllegalArgumentException("unsupported property")); //$NON-NLS-1$
            }
            prevValue = propsInternal.setProperty(key, value);
        } finally {
            DATA_LOCK.writeLock().unlock();
        }
        if (prevValue != null) {
            result = prevValue.toString();
        }
        LOGGER.debug("property set: key = [%s] ; value = [%s] ; prevValue = [%s]", key, value, result); //$NON-NLS-1$
        
        return LOGGER.exit(result);
    }
}
