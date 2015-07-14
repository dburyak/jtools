package dburyak.jtools;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

import net.jcip.annotations.NotThreadSafe;

import org.apache.commons.lang.NullArgumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Project : GetterLogParser.<br/>
 * Represents list of files for provided glob-pattern or regex-pattern. <br/>
 * <b>Created on:</b> <i>11:43:35 AM Apr 14, 2013</i>
 * 
 * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
 * @version 0.1
 */
@NotThreadSafe
public class FileList implements Iterable<Path> {

    // TODO : add constructor with Collection<Path>

    // TODO : sorting? via comparator?

    // TODO : add criteria functionality for multiple filtering

    /**
     * Default logger for {@link FileList}. <br/>
     * <b>Created on:</b> <i>1:52:05 AM Apr 15, 2013</i>
     */
    private static final Logger LOGGER = LogManager.getFormatterLogger(FileList.class);

    /**
     * Empty result constant to be used instead of null. <br/>
     * <b>Created on:</b> <i>6:05:25 PM Dec 2, 2014</i>
     */
    private static final Collection<Path> EMPTY_FILE_LIST = Collections.unmodifiableCollection(new ArrayList<>(0));


    /**
     * Project : GetterLogParser.<br/>
     * FileVisitor implementation for gathering matched by provided glob-pattern or regex-pattern files into list. <br/>
     * <b>Created on:</b> <i>11:53:06 AM Apr 14, 2013</i>
     * 
     * @author <i>Dmytro Buryak &ltdmytro.buryak@gmail.com&gt</i>
     * @version 0.1
     */
    private static class MatchFileVisitor implements FileVisitor<Path> {

        /**
         * Default logger for {@link MatchFileVisitor}. <br/>
         * <b>Created on:</b> <i>12:05:41 PM Apr 14, 2013</i>
         */
        @SuppressWarnings("hiding")
        private static final Logger LOGGER = LogManager.getFormatterLogger(MatchFileVisitor.class);

        /**
         * List contains matched file paths. <br/>
         * <b>Created on:</b> <i>11:55:45 AM Apr 14, 2013</i>
         */
        private final Collection<Path> fileList = new LinkedList<>();

        /**
         * Matcher for matching paths by glob syntax. <br/>
         * <b>Created on:</b> <i>11:58:55 AM Apr 14, 2013</i>
         */
        private final PathMatcher pathMatcher;


        /**
         * Constructor for class : [GetterLogParser] dburyak.getterlogparser.util.MatchFileVisitor.<br/>
         * Constructs new matching file visitor based on glob-pattern. <br/>
         * <b>Created on:</b> <i>12:01:53 PM Apr 14, 2013</i>
         * 
         * @param glob
         *            Pattern for matching file paths in glob syntax.
         */
        public MatchFileVisitor(final String glob) {
            LOGGER.debug("using glob-pattern for FileVisitor : glob = [%s]", glob); //$NON-NLS-1$
            this.pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob); //$NON-NLS-1$
        }

        /**
         * Constructor for class : [jtools] dburyak.jtools.MatchFileVisitor.<br/>
         * Constructs new matching file visitor based on regex-pattern. <br/>
         * <b>Created on:</b> <i>5:01:52 PM Nov 28, 2014</i>
         * 
         * @param regex
         */
        public MatchFileVisitor(final Pattern regex) {
            LOGGER.debug("using regex-pattern for FileVisitor : regex = [%s]", regex); //$NON-NLS-1$
            String regexStr = regex.pattern();
            if (!regexStr.startsWith("^") && !regexStr.endsWith("$")) {  //$NON-NLS-1$//$NON-NLS-2$
                // only file-name pattern, convert to full-path pattern
                regexStr = ".*" + regexStr + "$"; //$NON-NLS-1$ //$NON-NLS-2$
                final Pattern regexMod = Pattern.compile(regexStr);
                this.pathMatcher = FileSystems.getDefault().getPathMatcher("regex:" + regexMod); //$NON-NLS-1$
            } else {
                this.pathMatcher = FileSystems.getDefault().getPathMatcher("regex:" + regex); //$NON-NLS-1$
            }
        }

        /**
         * Returns result list which contains successfully matched Path file-paths. <br/>
         * <b>Created on:</b> <i>2:09:08 AM Apr 15, 2013</i>
         * 
         * @return list containing file-paths.
         */
        private Collection<Path> getResultList() {
            return Collections.unmodifiableCollection(fileList);
        }

        /**
         * In case of fail just continue traversing, does nothing. <br/>
         * <b>Created on:</b> <i>1:16:44 AM Apr 15, 2013</i>
         * 
         * @see java.nio.file.FileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
         * @param file
         *            file-path which have been visited.
         * @param exc
         *            exception which blocked access to file.
         * @return behavior on failing of visiting file.
         * @throws IOException
         *             in case of {@link IOException} occurrence.
         */
        @SuppressWarnings("unused")
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            MatchFileVisitor.LOGGER.warn("failed accessing file with exception : file = [%s]", //$NON-NLS-1$
                file.toString(),
                exc);

            // ----- do nothing -----
            return FileVisitResult.CONTINUE;
        }

        /**
         * Determines actions to be performed on visiting file. <br/>
         * <b>Created on:</b> <i>1:25:11 AM Apr 15, 2013</i>
         * 
         * @see java.nio.file.FileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
         * @param file
         *            file-path which have been visited.
         * @param attrs
         *            attributes of visited file.
         * @return behavior on successful visiting file.
         * @throws IOException
         *             in case of {@link IOException} occurrence.
         */
        @SuppressWarnings("unused")
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

            // ----- check matching by glob-pattern -----
            // ----- determined in constructor -----
            if (pathMatcher.matches(file)) {
                LOGGER.debug("matched file : [%s]", file); //$NON-NLS-1$
                fileList.add(file);
            }

            // ----- continue visiting in any case -----
            return FileVisitResult.CONTINUE;
        }

        /**
         * Just continues traversing, does nothing. <br/>
         * <b>Created on:</b> <i>1:31:29 AM Apr 15, 2013</i>
         * 
         * @see java.nio.file.FileVisitor#preVisitDirectory(java.lang.Object,
         *      java.nio.file.attribute.BasicFileAttributes)
         * @param dir
         *            directory to visit.
         * @param attrs
         *            attributes of directory.
         * @return behavior on pre-visiting of directory.
         * @throws IOException
         *             in case of {@link IOException} occurrence.
         */
        @SuppressWarnings("unused")
        @Override
        public FileVisitResult preVisitDirectory(
            Path dir, BasicFileAttributes attrs) throws IOException {
            // do nothing
            return FileVisitResult.CONTINUE;
        }

        /**
         * Just continues traversing, does nothing. <br/>
         * <b>Created on:</b> <i>1:41:57 AM Apr 15, 2013</i>
         * 
         * @see java.nio.file.FileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
         * @param dir
         *            directory visited.
         * @param exc
         *            exception if it occurred after visiting directory.
         * @return behavior on post-visiting of directory.
         * @throws IOException
         *             in case of {@link IOException} occurrence.
         */
        @SuppressWarnings("unused")
        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (exc != null) {
                MatchFileVisitor.LOGGER.warn("failed to access directory : [%s]", dir.toString()); //$NON-NLS-1$
            }
            // do nothing
            return FileVisitResult.CONTINUE;
        }
    }


    /**
     * GLOB-pattern for this {@link FileList} object. <br/>
     * <b>Created on:</b> <i>1:46:00 AM Apr 15, 2013</i>
     */
    private final String glob;

    /**
     * REGEX-pattern for this {@link FileList} object. <br/>
     * <b>Created on:</b> <i>4:51:19 PM Dec 2, 2014</i>
     */
    private final Pattern regex;

    /**
     * Dir where to start file tree walking from. Current dir (".") by default. <br/>
     * <b>Created on:</b> <i>6:19:06 PM Dec 2, 2014</i>
     */
    private final Path dir;

    /**
     * Underlying list of {@link Path} objects. <br/>
     * <b>Created on:</b> <i>1:48:17 AM Apr 15, 2013</i>
     */
    private Collection<Path> paths = EMPTY_FILE_LIST;


    /**
     * Constructor for class : [GetterLogParser]
     * dburyak.getterlogparser.util.FileList.<br/>
     * <br/>
     * <b>Created on:</b> <i>1:48:51 AM Apr 15, 2013</i>
     * 
     * @param glob
     *            glob-pattern for this {@link FileList}.
     */
    public FileList(final String glob) {
        if (glob == null) {
            throw LOGGER.throwing(new NullArgumentException("glob")); //$NON-NLS-1$
        }
        LOGGER.debug("using GLOB pattern for file list : glob = [%s]", //$NON-NLS-1$
            glob);
        this.glob = glob;
        this.regex = null;
        this.dir = Paths.get("."); //$NON-NLS-1$
    }

    /**
     * Constructor for class : [jtools] dburyak.jtools.FileList.<br/>
     * <br/>
     * <b>Created on:</b> <i>4:50:44 PM Dec 2, 2014</i>
     * 
     * @param regex
     */
    public FileList(final Pattern regex) {
        if (regex == null) {
            throw LOGGER.throwing(new NullArgumentException("regex")); //$NON-NLS-1$
        }
        LOGGER.debug("using REGEX pattern for file list : regex = [%s]"); //$NON-NLS-1$
        this.regex = regex;
        this.glob = null;
        this.dir = Paths.get("."); //$NON-NLS-1$
    }

    /**
     * Constructor for class : [jtools] dburyak.jtools.FileList.<br/>
     * <br/>
     * <b>Created on:</b> <i>6:21:03 PM Dec 2, 2014</i>
     * 
     * @param glob
     * @param dir
     */
    public FileList(final String glob, final Path dir) {
        if (glob == null) {
            throw LOGGER.throwing(new NullArgumentException("glob")); //$NON-NLS-1$
        }
        if (dir == null) {
            throw LOGGER.throwing(new NullArgumentException("dir")); //$NON-NLS-1$
        }
        if (!Files.isDirectory(dir)) {
            throw LOGGER.throwing(new IllegalArgumentException("not a directory")); //$NON-NLS-1$
        }
        this.glob = glob;
        this.regex = null;
        this.dir = dir;
    }

    /**
     * Constructor for class : [jtools] dburyak.jtools.FileList.<br/>
     * <br/>
     * <b>Created on:</b> <i>6:25:39 PM Dec 2, 2014</i>
     * 
     * @param regex
     * @param dir
     */
    public FileList(final Pattern regex, final Path dir) {
        if (regex == null) {
            throw LOGGER.throwing(new NullArgumentException("regex")); //$NON-NLS-1$
        }
        if (dir == null) {
            throw LOGGER.throwing(new NullArgumentException("dir")); //$NON-NLS-1$
        }
        if (!Files.isDirectory(dir)) {
            throw LOGGER.throwing(new IllegalArgumentException("not a directory")); //$NON-NLS-1$
        }
        this.regex = regex;
        this.glob = null;
        this.dir = dir;
    }

    /**
     * Constructor for class : [jtools] dburyak.jtools.FileList.<br/>
     * Creates a new file-list which contains paths from given collection. This file list can be later modified. <br/>
     * <b>Created on:</b> <i>12:04:29 PM Dec 15, 2014</i>
     * 
     * @param paths
     *            collection of {@link Path} objects, which the result file list should contain
     */
    public FileList(final Collection<Path> paths) {
        if (paths == null) {
            throw LOGGER.throwing(new NullArgumentException("paths")); //$NON-NLS-1$
        }
        this.regex = null;
        this.glob = null;
        this.dir = null;
        this.paths = new LinkedList<>(paths);
    }

    /**
     * Constructor for class : [jtools] dburyak.jtools.FileList.<br/>
     * Creates an empty file-list which can be later modified. <br/>
     * <b>Created on:</b> <i>12:04:25 PM Dec 15, 2014</i>
     */
    public FileList() {
        this.regex = null;
        this.glob = null;
        this.dir = null;
        this.paths = new LinkedList<>();
    }

    /**
     * Returns collection of successfully matched files with default sorting. <br/>
     * <b>Created on:</b> <i>1:55:52 AM Apr 15, 2013</i>
     * 
     * @return collection of {@link Path}s.
     * @throws IOException
     *             in case of {@link IOException} occurred while matching files.
     */
    @SuppressWarnings("synthetic-access")
    public Collection<Path> getResultList() throws IOException {
        LOGGER.entry();

        // ----- traverse directory tree only once (lazy computation) -----
        if (paths == EMPTY_FILE_LIST) {
            MatchFileVisitor visitor = null;
            String patternStr = ""; //$NON-NLS-1$
            if (glob != null && regex == null) {
                // use glob syntax
                visitor = new MatchFileVisitor(glob);
                patternStr = glob;
            } else if (glob == null && regex != null) {
                // use regex syntax
                visitor = new MatchFileVisitor(regex);
                patternStr = regex.toString();
            } else {
                // illegal state
                throw LOGGER.throwing(new IllegalStateException("invariant violation")); //$NON-NLS-1$
            }

            // ----- traverse the file tree -----
            LOGGER.debug("start traversing directory matching with pattern : dir = [%s] ; pattern = [%s]", //$NON-NLS-1$
                dir.toAbsolutePath(),
                patternStr);
            Files.walkFileTree(dir, visitor);

            // ----- gathering result -----
            paths = visitor.getResultList();
            LOGGER.debug("traversal done : dir = [%s] ; pattern = [%s] ; foundAmnt = [%d]", //$NON-NLS-1$
                dir.toAbsolutePath(),
                patternStr,
                paths.size());

            // ----- sorting result array -----
            // TODO : implement sorting of result collection
        } else if (glob == null && regex == null && dir == null) {
            // Collection<Path> wrapper mode
            return Collections.unmodifiableCollection(paths);
        }

        return LOGGER.exit(paths);
    }

    /**
     * Add a file path to this list. Works ONLY in paths collection wrapper mode. Shouldn't be used when this file list
     * was created by traversing file-system directory tree with matching glob or regex pattern. <br/>
     * <b>Created on:</b> <i>12:15:42 PM Dec 15, 2014</i>
     * 
     * @param filePath
     *            a file to add to this file list
     * @return true if file was successfully added, false otherwise
     * @throws UnsupportedOperationException
     *             if this file list was created with glob or regex pattern
     */
    public final boolean addPath(final Path filePath) throws UnsupportedOperationException {
        LOGGER.entry(filePath);
        if (filePath == null) {
            throw LOGGER.throwing(new NullArgumentException("filePath")); //$NON-NLS-1$
        }

        boolean result = false;
        if (glob == null && regex == null && dir == null) {
            // Collection<Path> wrapper mode
            result = paths.add(filePath);
        } else {
            throw LOGGER.throwing(new UnsupportedOperationException());
        }

        return LOGGER.exit(result);
    }

    /**
     * Create iterator which performs traverse over all Path objects this FileList contains. <br/>
     * <b>Created on:</b> <i>12:21:57 PM Dec 15, 2014</i>
     * 
     * @see java.lang.Iterable#iterator()
     * @return iterator over Path objects this file list contains
     */
    @Override
    public Iterator<Path> iterator() {
        Iterator<Path> result = EMPTY_FILE_LIST.iterator();
        try {
            result = getResultList().iterator();
        } catch (final IOException e) {
            LOGGER.error("error occured when reading file list", e); //$NON-NLS-1$
        }
        return result;
    }

}
