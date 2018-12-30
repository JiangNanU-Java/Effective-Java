package io;

import sun.security.action.GetPropertyAction;

import java.io.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件和目录路径名称的一种抽象表示。
 * <p>
 * 用户界面和操作系统使用依赖于系统的路径名称字符串来命名文件和目录，
 * 此类提供了一个抽象的、与系统无关的分层路径名称视图。
 * 一个抽象的路径名称包含两个组件：
 * <p>
 * 1、一个可选的依赖于系统的前缀字符串。例如磁盘驱动器说明符，
 * “/”用于UNIX根目录，或者“\\\\”用户微软UNC路径名。
 * 2、一系列零个或多个字符串的名称
 * <p>
 * 抽象路径名称的第一个名称可能是目录名称，或者是主机名称，例如在微软UNC路径名中。
 * 抽象路径名中的每个后续名称 * 表示目录；最后的名称可以表示 * 目录或文件。
 * 抽象路径名称没有 * 前缀和空名称序列。
 * <p>
 * 将路径名字符串转换为抽象路径名或从抽象路径名转换本质上取决于系统。将抽象
 * 路径名转位路径名字符串时，每个名称将通过默认分隔符的单个副本与下一个名称
 * 分隔开。默认的名称分隔符由系统属性 file.separator 定义，并在此类的公共
 * 静态字段 separator 和 separatorChar 中可用。当路径名字符串转换为抽象
 * 路径名时，其中的名称可以由默认的名称 - 分隔符或基础系统支持的任何其他名称 - 分隔符分割，
 * 路径名，无论是抽象的还是字符串形式，可以是绝对路径名也可以是相对路径名。
 * 绝对路径名是完整的，因为不需要其他信息来定位它表示的文件。相反，相对路径名
 * 必须根据从其他路径名获取的信息来解释。默认情况下，java.io 包中的类始终解析
 * 当前用户目录的相对路径名，此目录由系统属性 user.dir 命名，通常是调用java虚拟机的目录。
 * <p>
 * 抽象路径名的父级可以通过调用此类的getParent方法获得，
 * 并且包含路径名的前缀和路径名的名称序列中的每个名称（除了最后一个）。
 * 每个目录的绝对路径名是任何File对象的祖先，其绝对抽象路径名以目录的绝对路径名开头。
 * 例如，由抽象路径名“/ usr”表示的目录是由路径名“/ usr / local / bin”表示的目录的祖先。
 * 前缀概念用于处理UNIX平台上的根目录，以及Microsoft Windows平台上的驱动器说明符，根目录和UNC路径名，如下所示：
 * <p>
 * 1、对于UNIX平台，绝对路径名的前缀始终为“/”。相对路径名没有前缀。表示根目录的抽象路径名具有前缀“/”和空名称序列。
 * 2、对于Microsoft Windows平台，包含驱动器说明符的路径名的前缀包含驱动器号，后跟“：”，
 * 如果路径名是绝对的，则可能后跟“\\”。 UNC路径名的前缀是“\\\\”;主机名和共享名称是名称序列中的前两个名称。未指定驱动器的相对路径名没有前缀。
 * <p>
 * 此类的实例可能表示也可能不表示实际的文件系统对象，例如文件或目录。如果它确实表示这样的对象，则该对象驻留在分区中。
 * 分区是文件系统的特定于操作系统的存储部分。单个存储设备（例如，物理磁盘驱动器，闪存，CD-ROM）可以包含多个分区。
 * 对象（如果有）将驻留在由此路径名的绝对形式的某个祖先命名的分区上。
 * 文件系统可以对实际文件系统对象上的某些操作实施限制，例如读取，写入和执行。这些限制统称为访问权限。
 * 文件系统可能在单个对象上具有多组访问权限。例如，一组可以应用于对象的所有者，另一组可以应用于所有其他用户。
 * 对象的访问权限可能导致此类中的某些方法失败。 File类的实例是不可变的;也就是说，一旦创建，
 * File对象表示的抽象路径名将永远不会改变。
 */
public class File
        implements Serializable, Comparable<File> {

    /**
     * 对象表示本平台的本地文件系统
     */
    private static final FileSystem fs = DefaultFileSystem.getFileSystem();

    /**
     * 此抽象路径名的规范化路径名字符串。规范化的* pathname字符串
     * 使用默认的名称 - 分隔符，并且不包含任何重复或冗余的分隔符
     */
    private final String path;

    /**
     * 枚举类型，指示文件路径的状态。
     */
    private static enum PathStatus {
        INVALID, CHECKED
    }

    ;

    /**
     * 指示文件路径是否无效的标志。
     */
    private transient PathStatus status = null;

    /**
     * 检查文件是否包含无效路径。目前，*文件路径的检查非常有限，它只包含Nul字符检查。
     * *返回true意味着路径肯定是无效/垃圾。但*返回false并不能保证路径有效
     *
     * @return true if the file path is invalid.
     */
    final boolean isInvalid() {
        if (status == null) {
            status = (this.path.indexOf('\u0000') < 0) ? PathStatus.CHECKED
                    : PathStatus.INVALID;
        }
        return status == PathStatus.INVALID;
    }

    /**
     * The length of this abstract pathname's prefix, or zero if it has no
     * prefix.
     */
    private final transient int prefixLength;

    /**
     * Returns the length of this abstract pathname's prefix.
     * For use by FileSystem classes.
     */
    int getPrefixLength() {
        return prefixLength;
    }

    /**
     * The system-dependent default name-separator character.  This field is
     * initialized to contain the first character of the value of the system
     * property <code>file.separator</code>.  On UNIX systems the value of this
     * field is <code>'/'</code>; on Microsoft Windows systems it is <code>'\\'</code>.
     *
     * @see java.lang.System#getProperty(java.lang.String)
     */
    public static final char separatorChar = fs.getSeparator();

    /**
     * The system-dependent default name-separator character, represented as a
     * string for convenience.  This string contains a single character, namely
     * <code>{@link #separatorChar}</code>.
     */
    public static final String separator = "" + separatorChar;

    /**
     * The system-dependent path-separator character.  This field is
     * initialized to contain the first character of the value of the system
     * property <code>path.separator</code>.  This character is used to
     * separate filenames in a sequence of files given as a <em>path list</em>.
     * On UNIX systems, this character is <code>':'</code>; on Microsoft Windows systems it
     * is <code>';'</code>.
     *
     * @see java.lang.System#getProperty(java.lang.String)
     */
    public static final char pathSeparatorChar = fs.getPathSeparator();

    /**
     * The system-dependent path-separator character, represented as a string
     * for convenience.  This string contains a single character, namely
     * <code>{@link #pathSeparatorChar}</code>.
     */
    public static final String pathSeparator = "" + pathSeparatorChar;


    /* -- Constructors -- */

    /**
     * Internal constructor for already-normalized pathname strings.
     */
    private File(String pathname, int prefixLength) {
        this.path = pathname;
        this.prefixLength = prefixLength;
    }

    /**
     * Internal constructor for already-normalized pathname strings.
     * The parameter order is used to disambiguate this method from the
     * public(File, String) constructor.
     */
    private File(String child, java.io.File parent) {
        assert parent.path != null;
        assert (!parent.path.equals(""));
        this.path = fs.resolve(parent.path, child);
        this.prefixLength = parent.prefixLength;
    }

    /**
     * 通过将给定的* pathname字符串转换为抽象路径名来创建新的<code> File </ code>实例。
     * 如果给定的字符串是*空字符串，则结果是空的抽象路径名。
     *
     * @param pathname A pathname string
     * @throws NullPointerException If the <code>pathname</code> argument is <code>null</code>
     */
    public File(String pathname) {
        if (pathname == null) {
            throw new NullPointerException();
        }
        this.path = fs.normalize(pathname);
        this.prefixLength = fs.prefixLength(this.path);
    }

    /* 双参数文件构造函数不会将空父抽象路径名解释为当前用户目录。
    相反，空父项会使子目录针​​对FileSystem.getDefaultParent方法定义的依赖于系统的目录进行解析。
    在Unix上，此默认值为“/”，而在Microsoft Windows上则为“\\”。这是与此类的原始行为兼容所必需的 */

    /**
     * Creates a new <code>File</code> instance from a parent pathname string
     * and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> pathname string is taken to denote
     * a directory, and the <code>child</code> pathname string is taken to
     * denote either a directory or a file.  If the <code>child</code> pathname
     * string is absolute then it is converted into a relative pathname in a
     * system-dependent way.  If <code>parent</code> is the empty string then
     * the new <code>File</code> instance is created by converting
     * <code>child</code> into an abstract pathname and resolving the result
     * against a system-dependent default directory.  Otherwise each pathname
     * string is converted into an abstract pathname and the child abstract
     * pathname is resolved against the parent.
     *
     * @param parent The parent pathname string
     * @param child  The child pathname string
     * @throws NullPointerException If <code>child</code> is <code>null</code>
     */
    public File(String parent, String child) {
        if (child == null) {
            throw new NullPointerException();
        }
        if (parent != null) {
            if (parent.equals("")) {
                this.path = fs.resolve(fs.getDefaultParent(),
                        fs.normalize(child));
            } else {
                this.path = fs.resolve(fs.normalize(parent),
                        fs.normalize(child));
            }
        } else {
            this.path = fs.normalize(child);
        }
        this.prefixLength = fs.prefixLength(this.path);
    }

    /**
     * Creates a new <code>File</code> instance from a parent abstract
     * pathname and a child pathname string.
     *
     * <p> If <code>parent</code> is <code>null</code> then the new
     * <code>File</code> instance is created as if by invoking the
     * single-argument <code>File</code> constructor on the given
     * <code>child</code> pathname string.
     *
     * <p> Otherwise the <code>parent</code> abstract pathname is taken to
     * denote a directory, and the <code>child</code> pathname string is taken
     * to denote either a directory or a file.  If the <code>child</code>
     * pathname string is absolute then it is converted into a relative
     * pathname in a system-dependent way.  If <code>parent</code> is the empty
     * abstract pathname then the new <code>File</code> instance is created by
     * converting <code>child</code> into an abstract pathname and resolving
     * the result against a system-dependent default directory.  Otherwise each
     * pathname string is converted into an abstract pathname and the child
     * abstract pathname is resolved against the parent.
     *
     * @param parent The parent abstract pathname
     * @param child  The child pathname string
     * @throws NullPointerException If <code>child</code> is <code>null</code>
     */
    public File(java.io.File parent, String child) {
        if (child == null) {
            throw new NullPointerException();
        }
        if (parent != null) {
            if (parent.path.equals("")) {
                this.path = fs.resolve(fs.getDefaultParent(),
                        fs.normalize(child));
            } else {
                this.path = fs.resolve(parent.path,
                        fs.normalize(child));
            }
        } else {
            this.path = fs.normalize(child);
        }
        this.prefixLength = fs.prefixLength(this.path);
    }

    /**
     * Creates a new <tt>File</tt> instance by converting the given
     * <tt>file:</tt> URI into an abstract pathname.
     *
     * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
     * the transformation performed by this constructor is also
     * system-dependent.
     *
     * <p> For a given abstract pathname <i>f</i> it is guaranteed that
     *
     * <blockquote><tt>
     * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  This relationship typically does not hold,
     * however, when a <tt>file:</tt> URI that is created in a virtual machine
     * on one operating system is converted into an abstract pathname in a
     * virtual machine on a different operating system.
     *
     * @param uri An absolute, hierarchical URI with a scheme equal to
     *            <tt>"file"</tt>, a non-empty path component, and undefined
     *            authority, query, and fragment components
     * @throws NullPointerException     If <tt>uri</tt> is <tt>null</tt>
     * @throws IllegalArgumentException If the preconditions on the parameter do not hold
     * @see #toURI()
     * @see java.net.URI
     * @since 1.4
     */
    public File(URI uri) {

        // Check our many preconditions 先决条件
        if (!uri.isAbsolute())
            throw new IllegalArgumentException("URI is not absolute");
        if (uri.isOpaque())
            throw new IllegalArgumentException("URI is not hierarchical");
        String scheme = uri.getScheme();
        if ((scheme == null) || !scheme.equalsIgnoreCase("file"))
            throw new IllegalArgumentException("URI scheme is not \"file\"");
        if (uri.getAuthority() != null)
            throw new IllegalArgumentException("URI has an authority component");
        if (uri.getFragment() != null)
            throw new IllegalArgumentException("URI has a fragment component");
        if (uri.getQuery() != null)
            throw new IllegalArgumentException("URI has a query component");
        String p = uri.getPath();
        if (p.equals(""))
            throw new IllegalArgumentException("URI path component is empty");

        // Okay, now initialize
        p = fs.fromURIPath(p);
        if (java.io.File.separatorChar != '/')
            p = p.replace('/', java.io.File.separatorChar);
        this.path = fs.normalize(p);
        this.prefixLength = fs.prefixLength(this.path);
    }


    /* -- Path-component accessors -- */

    /**
     * Returns the name of the file or directory denoted by this abstract
     * pathname.  This is just the last name in the pathname's name
     * sequence.  If the pathname's name sequence is empty, then the empty
     * string is returned.
     *
     * @return The name of the file or directory denoted by this abstract
     * pathname, or the empty string if this pathname's name sequence
     * is empty
     */
    public String getName() {
        int index = path.lastIndexOf(separatorChar);
        if (index < prefixLength) return path.substring(prefixLength);
        return path.substring(index + 1);
    }

    /**
     * Returns the pathname string of this abstract pathname's parent, or
     * <code>null</code> if this pathname does not name a parent directory.
     *
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     *
     * @return The pathname string of the parent directory named by this
     * abstract pathname, or <code>null</code> if this pathname
     * does not name a parent
     */
    public String getParent() {
        int index = path.lastIndexOf(separatorChar);
        if (index < prefixLength) {
            if ((prefixLength > 0) && (path.length() > prefixLength))
                return path.substring(0, prefixLength);
            return null;
        }
        return path.substring(0, index);
    }

    /**
     * Returns the abstract pathname of this abstract pathname's parent,
     * or <code>null</code> if this pathname does not name a parent
     * directory.
     *
     * <p> The <em>parent</em> of an abstract pathname consists of the
     * pathname's prefix, if any, and each name in the pathname's name
     * sequence except for the last.  If the name sequence is empty then
     * the pathname does not name a parent directory.
     *
     * @return The abstract pathname of the parent directory named by this
     * abstract pathname, or <code>null</code> if this pathname
     * does not name a parent
     * @since 1.2
     */
    public java.io.File getParentFile() {
        String p = this.getParent();
        if (p == null) return null;
        return new java.io.File(p, this.prefixLength);
    }

    /**
     * Converts this abstract pathname into a pathname string.  The resulting
     * string uses the {@link #separator default name-separator character} to
     * separate the names in the name sequence.
     *
     * @return The string form of this abstract pathname
     */
    public String getPath() {
        return path;
    }


    /* -- Path operations -- */

    /**
     * Tests whether this abstract pathname is absolute.  The definition of
     * absolute pathname is system dependent.  On UNIX systems, a pathname is
     * absolute if its prefix is <code>"/"</code>.  On Microsoft Windows systems, a
     * pathname is absolute if its prefix is a drive specifier followed by
     * <code>"\\"</code>, or if its prefix is <code>"\\\\"</code>.
     *
     * @return <code>true</code> if this abstract pathname is absolute,
     * <code>false</code> otherwise
     */
    public boolean isAbsolute() {
        return fs.isAbsolute(this);
    }

    /**
     * Returns the absolute pathname string of this abstract pathname.
     *
     * <p> If this abstract pathname is already absolute, then the pathname
     * string is simply returned as if by the <code>{@link #getPath}</code>
     * method.  If this abstract pathname is the empty abstract pathname then
     * the pathname string of the current user directory, which is named by the
     * system property <code>user.dir</code>, is returned.  Otherwise this
     * pathname is resolved in a system-dependent way.  On UNIX systems, a
     * relative pathname is made absolute by resolving it against the current
     * user directory.  On Microsoft Windows systems, a relative pathname is made absolute
     * by resolving it against the current directory of the drive named by the
     * pathname, if any; if not, it is resolved against the current user
     * directory.
     *
     * @return The absolute pathname string denoting the same file or
     * directory as this abstract pathname
     * @throws SecurityException If a required system property value cannot be accessed.
     * @see java.io.File#isAbsolute()
     */
    public String getAbsolutePath() {
        return fs.resolve(this);
    }

    /**
     * Returns the absolute form of this abstract pathname.  Equivalent to
     * <code>new&nbsp;File(this.{@link #getAbsolutePath})</code>.
     *
     * @return The absolute abstract pathname denoting the same file or
     * directory as this abstract pathname
     * @throws SecurityException If a required system property value cannot be accessed.
     * @since 1.2
     */
    public java.io.File getAbsoluteFile() {
        String absPath = getAbsolutePath();
        return new java.io.File(absPath, fs.prefixLength(absPath));
    }

    /**
     * Returns the canonical pathname string of this abstract pathname.
     *
     * <p> A canonical pathname is both absolute and unique.  The precise
     * definition of canonical form is system-dependent.  This method first
     * converts this pathname to absolute form if necessary, as if by invoking the
     * {@link #getAbsolutePath} method, and then maps it to its unique form in a
     * system-dependent way.  This typically involves removing redundant names
     * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
     * symbolic links (on UNIX platforms), and converting drive letters to a
     * standard case (on Microsoft Windows platforms).
     *
     * <p> Every pathname that denotes an existing file or directory has a
     * unique canonical form.  Every pathname that denotes a nonexistent file
     * or directory also has a unique canonical form.  The canonical form of
     * the pathname of a nonexistent file or directory may be different from
     * the canonical form of the same pathname after the file or directory is
     * created.  Similarly, the canonical form of the pathname of an existing
     * file or directory may be different from the canonical form of the same
     * pathname after the file or directory is deleted.
     *
     * @return The canonical pathname string denoting the same file or
     * directory as this abstract pathname
     * @throws IOException       If an I/O error occurs, which is possible because the
     *                           construction of the canonical pathname may require
     *                           filesystem queries
     * @throws SecurityException If a required system property value cannot be accessed, or
     *                           if a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead}</code> method denies
     *                           read access to the file
     * @see Path#toRealPath
     * @since JDK1.1
     */
    public String getCanonicalPath() throws IOException {
        if (isInvalid()) {
            throw new IOException("Invalid file path");
        }
        return fs.canonicalize(fs.resolve(this));
    }

    /**
     * Returns the canonical form of this abstract pathname.  Equivalent to
     * <code>new&nbsp;File(this.{@link #getCanonicalPath})</code>.
     *
     * @return The canonical pathname string denoting the same file or
     * directory as this abstract pathname
     * @throws IOException       If an I/O error occurs, which is possible because the
     *                           construction of the canonical pathname may require
     *                           filesystem queries
     * @throws SecurityException If a required system property value cannot be accessed, or
     *                           if a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead}</code> method denies
     *                           read access to the file
     * @see Path#toRealPath
     * @since 1.2
     */
    public java.io.File getCanonicalFile() throws IOException {
        String canonPath = getCanonicalPath();
        return new java.io.File(canonPath, fs.prefixLength(canonPath));
    }

    private static String slashify(String path, boolean isDirectory) {
        String p = path;
        if (java.io.File.separatorChar != '/')
            p = p.replace(java.io.File.separatorChar, '/');
        if (!p.startsWith("/"))
            p = "/" + p;
        if (!p.endsWith("/") && isDirectory)
            p = p + "/";
        return p;
    }

    /**
     * Converts this abstract pathname into a <code>file:</code> URL.  The
     * exact form of the URL is system-dependent.  If it can be determined that
     * the file denoted by this abstract pathname is a directory, then the
     * resulting URL will end with a slash.
     *
     * @return A URL object representing the equivalent file URL
     * @throws MalformedURLException If the path cannot be parsed as a URL
     * @see #toURI()
     * @see java.net.URI
     * @see java.net.URI#toURL()
     * @see java.net.URL
     * @since 1.2
     * @deprecated This method does not automatically escape characters that
     * are illegal in URLs.  It is recommended that new code convert an
     * abstract pathname into a URL by first converting it into a URI, via the
     * {@link #toURI() toURI} method, and then converting the URI into a URL
     * via the {@link java.net.URI#toURL() URI.toURL} method.
     */
    @Deprecated
    public URL toURL() throws MalformedURLException {
        if (isInvalid()) {
            throw new MalformedURLException("Invalid file path");
        }
        return new URL("file", "", slashify(getAbsolutePath(), isDirectory()));
    }

    /**
     * Constructs a <tt>file:</tt> URI that represents this abstract pathname.
     *
     * <p> The exact form of the URI is system-dependent.  If it can be
     * determined that the file denoted by this abstract pathname is a
     * directory, then the resulting URI will end with a slash.
     *
     * <p> For a given abstract pathname <i>f</i>, it is guaranteed that
     *
     * <blockquote><tt>
     * new {@link #File(java.net.URI) File}(</tt><i>&nbsp;f</i><tt>.toURI()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
     * </tt></blockquote>
     * <p>
     * so long as the original abstract pathname, the URI, and the new abstract
     * pathname are all created in (possibly different invocations of) the same
     * Java virtual machine.  Due to the system-dependent nature of abstract
     * pathnames, however, this relationship typically does not hold when a
     * <tt>file:</tt> URI that is created in a virtual machine on one operating
     * system is converted into an abstract pathname in a virtual machine on a
     * different operating system.
     *
     * <p> Note that when this abstract pathname represents a UNC pathname then
     * all components of the UNC (including the server name component) are encoded
     * in the {@code URI} path. The authority component is undefined, meaning
     * that it is represented as {@code null}. The {@link Path} class defines the
     * {@link Path#toUri toUri} method to encode the server name in the authority
     * component of the resulting {@code URI}. The {@link #toPath toPath} method
     * may be used to obtain a {@code Path} representing this abstract pathname.
     *
     * @return An absolute, hierarchical URI with a scheme equal to
     * <tt>"file"</tt>, a path representing this abstract pathname,
     * and undefined authority, query, and fragment components
     * @throws SecurityException If a required system property value cannot
     *                           be accessed.
     * @see #File(java.net.URI)
     * @see java.net.URI
     * @see java.net.URI#toURL()
     * @since 1.4
     */
    public URI toURI() {
        try {
            java.io.File f = getAbsoluteFile();
            String sp = slashify(f.getPath(), f.isDirectory());
            if (sp.startsWith("//"))
                sp = "//" + sp;
            return new URI("file", null, sp, null);
        } catch (URISyntaxException x) {
            throw new Error(x);         // Can't happen
        }
    }


    /* -- Attribute accessors -- */

    /**
     * Tests whether the application can read the file denoted by this
     * abstract pathname. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to read
     * files that are marked as unreadable. Consequently this method may return
     * {@code true} even though the file does not have read permissions.
     *
     * @return <code>true</code> if and only if the file specified by this
     * abstract pathname exists <em>and</em> can be read by the
     * application; <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
     */
    public boolean canRead() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.checkAccess(this, java.io.FileSystem.ACCESS_READ);
    }

    /**
     * Tests whether the application can modify the file denoted by this
     * abstract pathname. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to modify
     * files that are marked read-only. Consequently this method may return
     * {@code true} even though the file is marked read-only.
     *
     * @return <code>true</code> if and only if the file system actually
     * contains a file denoted by this abstract pathname <em>and</em>
     * the application is allowed to write to the file;
     * <code>false</code> otherwise.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     */
    public boolean canWrite() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.checkAccess(this, java.io.FileSystem.ACCESS_WRITE);
    }

    /**
     * Tests whether the file or directory denoted by this abstract pathname
     * exists.
     *
     * @return <code>true</code> if and only if the file or directory denoted
     * by this abstract pathname exists; <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file or directory
     */
    public boolean exists() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & java.io.FileSystem.BA_EXISTS) != 0);
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a
     * directory.
     *
     * <p> Where it is required to distinguish an I/O exception from the case
     * that the file is not a directory, or where several attributes of the
     * same file are required at the same time, then the {@link
     * java.nio.file.Files#readAttributes(Path, Class, LinkOption[])
     * Files.readAttributes} method may be used.
     *
     * @return <code>true</code> if and only if the file denoted by this
     * abstract pathname exists <em>and</em> is a directory;
     * <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
     */
    public boolean isDirectory() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & java.io.FileSystem.BA_DIRECTORY)
                != 0);
    }

    /**
     * Tests whether the file denoted by this abstract pathname is a normal
     * file.  A file is <em>normal</em> if it is not a directory and, in
     * addition, satisfies other system-dependent criteria.  Any non-directory
     * file created by a Java application is guaranteed to be a normal file.
     *
     * <p> Where it is required to distinguish an I/O exception from the case
     * that the file is not a normal file, or where several attributes of the
     * same file are required at the same time, then the {@link
     * java.nio.file.Files#readAttributes(Path, Class, LinkOption[])
     * Files.readAttributes} method may be used.
     *
     * @return <code>true</code> if and only if the file denoted by this
     * abstract pathname exists <em>and</em> is a normal file;
     * <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
     */
    public boolean isFile() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & java.io.FileSystem.BA_REGULAR) != 0);
    }

    /**
     * Tests whether the file named by this abstract pathname is a hidden
     * file.  The exact definition of <em>hidden</em> is system-dependent.  On
     * UNIX systems, a file is considered to be hidden if its name begins with
     * a period character (<code>'.'</code>).  On Microsoft Windows systems, a file is
     * considered to be hidden if it has been marked as such in the filesystem.
     *
     * @return <code>true</code> if and only if the file denoted by this
     * abstract pathname is hidden according to the conventions of the
     * underlying platform
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
     * @since 1.2
     */
    public boolean isHidden() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return false;
        }
        return ((fs.getBooleanAttributes(this) & java.io.FileSystem.BA_HIDDEN) != 0);
    }

    /**
     * Returns the time that the file denoted by this abstract pathname was
     * last modified.
     *
     * <p> Where it is required to distinguish an I/O exception from the case
     * where {@code 0L} is returned, or where several attributes of the
     * same file are required at the same time, or where the time of last
     * access or the creation time are required, then the {@link
     * java.nio.file.Files#readAttributes(Path, Class, LinkOption[])
     * Files.readAttributes} method may be used.
     *
     * @return A <code>long</code> value representing the time the file was
     * last modified, measured in milliseconds since the epoch
     * (00:00:00 GMT, January 1, 1970), or <code>0L</code> if the
     * file does not exist or if an I/O error occurs
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
     */
    public long lastModified() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getLastModifiedTime(this);
    }

    /**
     * Returns the length of the file denoted by this abstract pathname.
     * The return value is unspecified if this pathname denotes a directory.
     *
     * <p> Where it is required to distinguish an I/O exception from the case
     * that {@code 0L} is returned, or where several attributes of the same file
     * are required at the same time, then the {@link
     * java.nio.file.Files#readAttributes(Path, Class, LinkOption[])
     * Files.readAttributes} method may be used.
     *
     * @return The length, in bytes, of the file denoted by this abstract
     * pathname, or <code>0L</code> if the file does not exist.  Some
     * operating systems may return <code>0L</code> for pathnames
     * denoting system-dependent entities such as devices or pipes.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method denies read access to the file
     */
    public long length() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getLength(this);
    }


    /* -- File operations -- */

    /**
     * Atomically creates a new, empty file named by this abstract pathname if
     * and only if a file with this name does not yet exist.  The check for the
     * existence of the file and the creation of the file if it does not exist
     * are a single operation that is atomic with respect to all other
     * filesystem activities that might affect the file.
     * <p>
     * Note: this method should <i>not</i> be used for file-locking, as
     * the resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock}
     * facility should be used instead.
     *
     * @return <code>true</code> if the named file does not exist and was
     * successfully created; <code>false</code> if the named file
     * already exists
     * @throws IOException       If an I/O error occurred
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     * @since 1.2
     */
    public boolean createNewFile() throws IOException {
        SecurityManager security = System.getSecurityManager();
        if (security != null) security.checkWrite(path);
        if (isInvalid()) {
            throw new IOException("Invalid file path");
        }
        return fs.createFileExclusively(path);
    }

    /**
     * Deletes the file or directory denoted by this abstract pathname.  If
     * this pathname denotes a directory, then the directory must be empty in
     * order to be deleted.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#delete(Path) delete} method to throw an {@link IOException}
     * when a file cannot be deleted. This is useful for error reporting and to
     * diagnose why a file cannot be deleted.
     *
     * @return <code>true</code> if and only if the file or directory is
     * successfully deleted; <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkDelete}</code> method denies
     *                           delete access to the file
     */
    public boolean delete() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkDelete(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.delete(this);
    }

    /**
     * Requests that the file or directory denoted by this abstract
     * pathname be deleted when the virtual machine terminates.
     * Files (or directories) are deleted in the reverse order that
     * they are registered. Invoking this method to delete a file or
     * directory that is already registered for deletion has no effect.
     * Deletion will be attempted only for normal termination of the
     * virtual machine, as defined by the Java Language Specification.
     *
     * <p> Once deletion has been requested, it is not possible to cancel the
     * request.  This method should therefore be used with care.
     * <p>
     * <p>
     * Note: this method should <i>not</i> be used for file-locking, as
     * the resulting protocol cannot be made to work reliably. The
     * {@link java.nio.channels.FileLock FileLock}
     * facility should be used instead.
     *
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkDelete}</code> method denies
     *                           delete access to the file
     * @see #delete
     * @since 1.2
     */
    public void deleteOnExit() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkDelete(path);
        }
        if (isInvalid()) {
            return;
        }
        DeleteOnExitHook.add(path);
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname.
     *
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of strings is
     * returned, one for each file or directory in the directory.  Names
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each string is a file name rather than a
     * complete path.
     *
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method to
     * open a directory and iterate over the names of the files in the directory.
     * This may use less resources when working with very large directories, and
     * may be more responsive when working with remote directories.
     *
     * @return An array of strings naming the files and directories in the
     * directory denoted by this abstract pathname.  The array will be
     * empty if the directory is empty.  Returns {@code null} if
     * this abstract pathname does not denote a directory, or if an
     * I/O error occurs.
     * @throws SecurityException If a security manager exists and its {@link
     *                           SecurityManager#checkRead(String)} method denies read access to
     *                           the directory
     */
    public String[] list() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkRead(path);
        }
        if (isInvalid()) {
            return null;
        }
        return fs.list(this);
    }

    /**
     * Returns an array of strings naming the files and directories in the
     * directory denoted by this abstract pathname that satisfy the specified
     * filter.  The behavior of this method is the same as that of the
     * {@link #list()} method, except that the strings in the returned array
     * must satisfy the filter.  If the given {@code filter} is {@code null}
     * then all names are accepted.  Otherwise, a name satisfies the filter if
     * and only if the value {@code true} results when the {@link
     * FilenameFilter#accept FilenameFilter.accept(File,&nbsp;String)} method
     * of the filter is invoked on this abstract pathname and the name of a
     * file or directory in the directory that it denotes.
     *
     * @param filter A filename filter
     * @return An array of strings naming the files and directories in the
     * directory denoted by this abstract pathname that were accepted
     * by the given {@code filter}.  The array will be empty if the
     * directory is empty or if no names were accepted by the filter.
     * Returns {@code null} if this abstract pathname does not denote
     * a directory, or if an I/O error occurs.
     * @throws SecurityException If a security manager exists and its {@link
     *                           SecurityManager#checkRead(String)} method denies read access to
     *                           the directory
     * @see java.nio.file.Files#newDirectoryStream(Path, String)
     */
    public String[] list(FilenameFilter filter) {
        String names[] = list();
        if ((names == null) || (filter == null)) {
            return names;
        }
        List<String> v = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            if (filter.accept(this, names[i])) {
                v.add(names[i]);
            }
        }
        return v.toArray(new String[v.size()]);
    }

    /**
     * Returns an array of abstract pathnames denoting the files in the
     * directory denoted by this abstract pathname.
     *
     * <p> If this abstract pathname does not denote a directory, then this
     * method returns {@code null}.  Otherwise an array of {@code File} objects
     * is returned, one for each file or directory in the directory.  Pathnames
     * denoting the directory itself and the directory's parent directory are
     * not included in the result.  Each resulting abstract pathname is
     * constructed from this abstract pathname using the {@link #File(java.io.File,
     * String) File(File,&nbsp;String)} constructor.  Therefore if this
     * pathname is absolute then each resulting pathname is absolute; if this
     * pathname is relative then each resulting pathname will be relative to
     * the same directory.
     *
     * <p> There is no guarantee that the name strings in the resulting array
     * will appear in any specific order; they are not, in particular,
     * guaranteed to appear in alphabetical order.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#newDirectoryStream(Path) newDirectoryStream} method
     * to open a directory and iterate over the names of the files in the
     * directory. This may use less resources when working with very large
     * directories.
     *
     * @return An array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname.
     * The array will be empty if the directory is empty.  Returns
     * {@code null} if this abstract pathname does not denote a
     * directory, or if an I/O error occurs.
     * @throws SecurityException If a security manager exists and its {@link
     *                           SecurityManager#checkRead(String)} method denies read access to
     *                           the directory
     * @since 1.2
     */
    public java.io.File[] listFiles() {
        String[] ss = list();
        if (ss == null) return null;
        int n = ss.length;
        java.io.File[] fs = new java.io.File[n];
        for (int i = 0; i < n; i++) {
            fs[i] = new java.io.File(ss[i], this);
        }
        return fs;
    }

    /**
     * Returns an array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname that
     * satisfy the specified filter.  The behavior of this method is the same
     * as that of the {@link #listFiles()} method, except that the pathnames in
     * the returned array must satisfy the filter.  If the given {@code filter}
     * is {@code null} then all pathnames are accepted.  Otherwise, a pathname
     * satisfies the filter if and only if the value {@code true} results when
     * the {@link FilenameFilter#accept
     * FilenameFilter.accept(File,&nbsp;String)} method of the filter is
     * invoked on this abstract pathname and the name of a file or directory in
     * the directory that it denotes.
     *
     * @param filter A filename filter
     * @return An array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname.
     * The array will be empty if the directory is empty.  Returns
     * {@code null} if this abstract pathname does not denote a
     * directory, or if an I/O error occurs.
     * @throws SecurityException If a security manager exists and its {@link
     *                           SecurityManager#checkRead(String)} method denies read access to
     *                           the directory
     * @see java.nio.file.Files#newDirectoryStream(Path, String)
     * @since 1.2
     */
    public java.io.File[] listFiles(FilenameFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<java.io.File> files = new ArrayList<>();
        for (String s : ss)
            if ((filter == null) || filter.accept(this, s))
                files.add(new java.io.File(s, this));
        return files.toArray(new java.io.File[files.size()]);
    }

    /**
     * Returns an array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname that
     * satisfy the specified filter.  The behavior of this method is the same
     * as that of the {@link #listFiles()} method, except that the pathnames in
     * the returned array must satisfy the filter.  If the given {@code filter}
     * is {@code null} then all pathnames are accepted.  Otherwise, a pathname
     * satisfies the filter if and only if the value {@code true} results when
     * the {@link FileFilter#accept FileFilter.accept(File)} method of the
     * filter is invoked on the pathname.
     *
     * @param filter A file filter
     * @return An array of abstract pathnames denoting the files and
     * directories in the directory denoted by this abstract pathname.
     * The array will be empty if the directory is empty.  Returns
     * {@code null} if this abstract pathname does not denote a
     * directory, or if an I/O error occurs.
     * @throws SecurityException If a security manager exists and its {@link
     *                           SecurityManager#checkRead(String)} method denies read access to
     *                           the directory
     * @see java.nio.file.Files#newDirectoryStream(Path, java.nio.file.DirectoryStream.Filter)
     * @since 1.2
     */
    public java.io.File[] listFiles(FileFilter filter) {
        String ss[] = list();
        if (ss == null) return null;
        ArrayList<java.io.File> files = new ArrayList<>();
        for (String s : ss) {
            java.io.File f = new java.io.File(s, this);
            if ((filter == null) || filter.accept(f))
                files.add(f);
        }
        return files.toArray(new java.io.File[files.size()]);
    }

    /**
     * Creates the directory named by this abstract pathname.
     *
     * @return <code>true</code> if and only if the directory was
     * created; <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method does not permit the named directory to be created
     */
    public boolean mkdir() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.createDirectory(this);
    }

    /**
     * Creates the directory named by this abstract pathname, including any
     * necessary but nonexistent parent directories.  Note that if this
     * operation fails it may have succeeded in creating some of the necessary
     * parent directories.
     *
     * @return <code>true</code> if and only if the directory was created,
     * along with all necessary parent directories; <code>false</code>
     * otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkRead(java.lang.String)}</code>
     *                           method does not permit verification of the existence of the
     *                           named directory and all necessary parent directories; or if
     *                           the <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method does not permit the named directory and all necessary
     *                           parent directories to be created
     */
    public boolean mkdirs() {
        if (exists()) {
            return false;
        }
        if (mkdir()) {
            return true;
        }
        java.io.File canonFile = null;
        try {
            canonFile = getCanonicalFile();
        } catch (IOException e) {
            return false;
        }

        java.io.File parent = canonFile.getParentFile();
        return (parent != null && (parent.mkdirs() || parent.exists()) &&
                canonFile.mkdir());
    }

    /**
     * Renames the file denoted by this abstract pathname.
     *
     * <p> Many aspects of the behavior of this method are inherently
     * platform-dependent: The rename operation might not be able to move a
     * file from one filesystem to another, it might not be atomic, and it
     * might not succeed if a file with the destination abstract pathname
     * already exists.  The return value should always be checked to make sure
     * that the rename operation was successful.
     *
     * <p> Note that the {@link java.nio.file.Files} class defines the {@link
     * java.nio.file.Files#move move} method to move or rename a file in a
     * platform independent manner.
     *
     * @param dest The new abstract pathname for the named file
     * @return <code>true</code> if and only if the renaming succeeded;
     * <code>false</code> otherwise
     * @throws SecurityException    If a security manager exists and its <code>{@link
     *                              java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                              method denies write access to either the old or new pathnames
     * @throws NullPointerException If parameter <code>dest</code> is <code>null</code>
     */
    public boolean renameTo(java.io.File dest) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
            security.checkWrite(dest.path);
        }
        if (dest == null) {
            throw new NullPointerException();
        }
        if (this.isInvalid() || dest.isInvalid()) {
            return false;
        }
        return fs.rename(this, dest);
    }

    /**
     * Sets the last-modified time of the file or directory named by this
     * abstract pathname.
     *
     * <p> All platforms support file-modification times to the nearest second,
     * but some provide more precision.  The argument will be truncated to fit
     * the supported precision.  If the operation succeeds and no intervening
     * operations on the file take place, then the next invocation of the
     * <code>{@link #lastModified}</code> method will return the (possibly
     * truncated) <code>time</code> argument that was passed to this method.
     *
     * @param time The new last-modified time, measured in milliseconds since
     *             the epoch (00:00:00 GMT, January 1, 1970)
     * @return <code>true</code> if and only if the operation succeeded;
     * <code>false</code> otherwise
     * @throws IllegalArgumentException If the argument is negative
     * @throws SecurityException        If a security manager exists and its <code>{@link
     *                                  java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                                  method denies write access to the named file
     * @since 1.2
     */
    public boolean setLastModified(long time) {
        if (time < 0) throw new IllegalArgumentException("Negative time");
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setLastModifiedTime(this, time);
    }

    /**
     * Marks the file or directory named by this abstract pathname so that
     * only read operations are allowed. After invoking this method the file
     * or directory will not change until it is either deleted or marked
     * to allow write access. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to modify
     * files that are marked read-only. Whether or not a read-only file or
     * directory may be deleted depends upon the underlying system.
     *
     * @return <code>true</code> if and only if the operation succeeded;
     * <code>false</code> otherwise
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the named file
     * @since 1.2
     */
    public boolean setReadOnly() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setReadOnly(this);
    }

    /**
     * Sets the owner's or everybody's write permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to modify files that
     * disallow write operations.
     *
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     *
     * @param writable  If <code>true</code>, sets the access permission to allow write
     *                  operations; if <code>false</code> to disallow write operations
     * @param ownerOnly If <code>true</code>, the write permission applies only to the
     *                  owner's write permission; otherwise, it applies to everybody.  If
     *                  the underlying file system can not distinguish the owner's write
     *                  permission from that of others, then the permission will apply to
     *                  everybody, regardless of this value.
     * @return <code>true</code> if and only if the operation succeeded. The
     * operation will fail if the user does not have permission to change
     * the access permissions of this abstract pathname.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the named file
     * @since 1.6
     */
    public boolean setWritable(boolean writable, boolean ownerOnly) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setPermission(this, java.io.FileSystem.ACCESS_WRITE, writable, ownerOnly);
    }

    /**
     * A convenience method to set the owner's write permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to modify files that
     * disallow write operations.
     *
     * <p> An invocation of this method of the form <tt>file.setWritable(arg)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     file.setWritable(arg, true) </pre>
     *
     * @param writable If <code>true</code>, sets the access permission to allow write
     *                 operations; if <code>false</code> to disallow write operations
     * @return <code>true</code> if and only if the operation succeeded.  The
     * operation will fail if the user does not have permission to
     * change the access permissions of this abstract pathname.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     * @since 1.6
     */
    public boolean setWritable(boolean writable) {
        return setWritable(writable, true);
    }

    /**
     * Sets the owner's or everybody's read permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to read files that are
     * marked as unreadable.
     *
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     *
     * @param readable  If <code>true</code>, sets the access permission to allow read
     *                  operations; if <code>false</code> to disallow read operations
     * @param ownerOnly If <code>true</code>, the read permission applies only to the
     *                  owner's read permission; otherwise, it applies to everybody.  If
     *                  the underlying file system can not distinguish the owner's read
     *                  permission from that of others, then the permission will apply to
     *                  everybody, regardless of this value.
     * @return <code>true</code> if and only if the operation succeeded.  The
     * operation will fail if the user does not have permission to
     * change the access permissions of this abstract pathname.  If
     * <code>readable</code> is <code>false</code> and the underlying
     * file system does not implement a read permission, then the
     * operation will fail.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     * @since 1.6
     */
    public boolean setReadable(boolean readable, boolean ownerOnly) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setPermission(this, java.io.FileSystem.ACCESS_READ, readable, ownerOnly);
    }

    /**
     * A convenience method to set the owner's read permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to read files that that are
     * marked as unreadable.
     *
     * <p>An invocation of this method of the form <tt>file.setReadable(arg)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     file.setReadable(arg, true) </pre>
     *
     * @param readable If <code>true</code>, sets the access permission to allow read
     *                 operations; if <code>false</code> to disallow read operations
     * @return <code>true</code> if and only if the operation succeeded.  The
     * operation will fail if the user does not have permission to
     * change the access permissions of this abstract pathname.  If
     * <code>readable</code> is <code>false</code> and the underlying
     * file system does not implement a read permission, then the
     * operation will fail.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     * @since 1.6
     */
    public boolean setReadable(boolean readable) {
        return setReadable(readable, true);
    }

    /**
     * Sets the owner's or everybody's execute permission for this abstract
     * pathname. On some platforms it may be possible to start the Java virtual
     * machine with special privileges that allow it to execute files that are
     * not marked executable.
     *
     * <p> The {@link java.nio.file.Files} class defines methods that operate on
     * file attributes including file permissions. This may be used when finer
     * manipulation of file permissions is required.
     *
     * @param executable If <code>true</code>, sets the access permission to allow execute
     *                   operations; if <code>false</code> to disallow execute operations
     * @param ownerOnly  If <code>true</code>, the execute permission applies only to the
     *                   owner's execute permission; otherwise, it applies to everybody.
     *                   If the underlying file system can not distinguish the owner's
     *                   execute permission from that of others, then the permission will
     *                   apply to everybody, regardless of this value.
     * @return <code>true</code> if and only if the operation succeeded.  The
     * operation will fail if the user does not have permission to
     * change the access permissions of this abstract pathname.  If
     * <code>executable</code> is <code>false</code> and the underlying
     * file system does not implement an execute permission, then the
     * operation will fail.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     * @since 1.6
     */
    public boolean setExecutable(boolean executable, boolean ownerOnly) {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkWrite(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.setPermission(this, java.io.FileSystem.ACCESS_EXECUTE, executable, ownerOnly);
    }

    /**
     * A convenience method to set the owner's execute permission for this
     * abstract pathname. On some platforms it may be possible to start the Java
     * virtual machine with special privileges that allow it to execute files
     * that are not marked executable.
     *
     * <p>An invocation of this method of the form <tt>file.setExcutable(arg)</tt>
     * behaves in exactly the same way as the invocation
     *
     * <pre>
     *     file.setExecutable(arg, true) </pre>
     *
     * @param executable If <code>true</code>, sets the access permission to allow execute
     *                   operations; if <code>false</code> to disallow execute operations
     * @return <code>true</code> if and only if the operation succeeded.  The
     * operation will fail if the user does not have permission to
     * change the access permissions of this abstract pathname.  If
     * <code>executable</code> is <code>false</code> and the underlying
     * file system does not implement an execute permission, then the
     * operation will fail.
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                           method denies write access to the file
     * @since 1.6
     */
    public boolean setExecutable(boolean executable) {
        return setExecutable(executable, true);
    }

    /**
     * Tests whether the application can execute the file denoted by this
     * abstract pathname. On some platforms it may be possible to start the
     * Java virtual machine with special privileges that allow it to execute
     * files that are not marked executable. Consequently this method may return
     * {@code true} even though the file does not have execute permissions.
     *
     * @return <code>true</code> if and only if the abstract pathname exists
     * <em>and</em> the application is allowed to execute the file
     * @throws SecurityException If a security manager exists and its <code>{@link
     *                           java.lang.SecurityManager#checkExec(java.lang.String)}</code>
     *                           method denies execute access to the file
     * @since 1.6
     */
    public boolean canExecute() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkExec(path);
        }
        if (isInvalid()) {
            return false;
        }
        return fs.checkAccess(this, java.io.FileSystem.ACCESS_EXECUTE);
    }


    /* -- Filesystem interface -- */

    /**
     * List the available filesystem roots.
     *
     * <p> A particular Java platform may support zero or more
     * hierarchically-organized file systems.  Each file system has a
     * {@code root} directory from which all other files in that file system
     * can be reached.  Windows platforms, for example, have a root directory
     * for each active drive; UNIX platforms have a single root directory,
     * namely {@code "/"}.  The set of available filesystem roots is affected
     * by various system-level operations such as the insertion or ejection of
     * removable media and the disconnecting or unmounting of physical or
     * virtual disk drives.
     *
     * <p> This method returns an array of {@code File} objects that denote the
     * root directories of the available filesystem roots.  It is guaranteed
     * that the canonical pathname of any file physically present on the local
     * machine will begin with one of the roots returned by this method.
     *
     * <p> The canonical pathname of a file that resides on some other machine
     * and is accessed via a remote-filesystem protocol such as SMB or NFS may
     * or may not begin with one of the roots returned by this method.  If the
     * pathname of a remote file is syntactically indistinguishable from the
     * pathname of a local file then it will begin with one of the roots
     * returned by this method.  Thus, for example, {@code File} objects
     * denoting the root directories of the mapped network drives of a Windows
     * platform will be returned by this method, while {@code File} objects
     * containing UNC pathnames will not be returned by this method.
     *
     * <p> Unlike most methods in this class, this method does not throw
     * security exceptions.  If a security manager exists and its {@link
     * SecurityManager#checkRead(String)} method denies read access to a
     * particular root directory, then that directory will not appear in the
     * result.
     *
     * @return An array of {@code File} objects denoting the available
     * filesystem roots, or {@code null} if the set of roots could not
     * be determined.  The array will be empty if there are no
     * filesystem roots.
     * @see java.nio.file.FileStore
     * @since 1.2
     */
    public static java.io.File[] listRoots() {
        return fs.listRoots();
    }


    /* -- Disk usage -- */

    /**
     * Returns the size of the partition <a href="#partName">named</a> by this
     * abstract pathname.
     *
     * @return The size, in bytes, of the partition or <tt>0L</tt> if this
     * abstract pathname does not name a partition
     * @throws SecurityException If a security manager has been installed and it denies
     *                           {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *                           or its {@link SecurityManager#checkRead(String)} method denies
     *                           read access to the file named by this abstract pathname
     * @since 1.6
     */
    public long getTotalSpace() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getFileSystemAttributes"));
            sm.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getSpace(this, java.io.FileSystem.SPACE_TOTAL);
    }

    /**
     * Returns the number of unallocated bytes in the partition <a
     * href="#partName">named</a> by this abstract path name.
     *
     * <p> The returned number of unallocated bytes is a hint, but not
     * a guarantee, that it is possible to use most or any of these
     * bytes.  The number of unallocated bytes is most likely to be
     * accurate immediately after this call.  It is likely to be made
     * inaccurate by any external I/O operations including those made
     * on the system outside of this virtual machine.  This method
     * makes no guarantee that write operations to this file system
     * will succeed.
     *
     * @return The number of unallocated bytes on the partition or <tt>0L</tt>
     * if the abstract pathname does not name a partition.  This
     * value will be less than or equal to the total file system size
     * returned by {@link #getTotalSpace}.
     * @throws SecurityException If a security manager has been installed and it denies
     *                           {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *                           or its {@link SecurityManager#checkRead(String)} method denies
     *                           read access to the file named by this abstract pathname
     * @since 1.6
     */
    public long getFreeSpace() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getFileSystemAttributes"));
            sm.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getSpace(this, java.io.FileSystem.SPACE_FREE);
    }

    /**
     * Returns the number of bytes available to this virtual machine on the
     * partition <a href="#partName">named</a> by this abstract pathname.  When
     * possible, this method checks for write permissions and other operating
     * system restrictions and will therefore usually provide a more accurate
     * estimate of how much new data can actually be written than {@link
     * #getFreeSpace}.
     *
     * <p> The returned number of available bytes is a hint, but not a
     * guarantee, that it is possible to use most or any of these bytes.  The
     * number of unallocated bytes is most likely to be accurate immediately
     * after this call.  It is likely to be made inaccurate by any external
     * I/O operations including those made on the system outside of this
     * virtual machine.  This method makes no guarantee that write operations
     * to this file system will succeed.
     *
     * @return The number of available bytes on the partition or <tt>0L</tt>
     * if the abstract pathname does not name a partition.  On
     * systems where this information is not available, this method
     * will be equivalent to a call to {@link #getFreeSpace}.
     * @throws SecurityException If a security manager has been installed and it denies
     *                           {@link RuntimePermission}<tt>("getFileSystemAttributes")</tt>
     *                           or its {@link SecurityManager#checkRead(String)} method denies
     *                           read access to the file named by this abstract pathname
     * @since 1.6
     */
    public long getUsableSpace() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("getFileSystemAttributes"));
            sm.checkRead(path);
        }
        if (isInvalid()) {
            return 0L;
        }
        return fs.getSpace(this, java.io.FileSystem.SPACE_USABLE);
    }

    /* -- Temporary files -- */

    private static class TempDirectory {
        private TempDirectory() {
        }

        // temporary directory location
        private static final java.io.File tmpdir = new java.io.File(AccessController
                .doPrivileged(new GetPropertyAction("java.io.tmpdir")));

        static java.io.File location() {
            return tmpdir;
        }

        // file name generation
        private static final SecureRandom random = new SecureRandom();

        static java.io.File generateFile(String prefix, String suffix, java.io.File dir)
                throws IOException {
            long n = random.nextLong();
            if (n == Long.MIN_VALUE) {
                n = 0;      // corner case
            } else {
                n = Math.abs(n);
            }

            // Use only the file name from the supplied prefix
            prefix = (new java.io.File(prefix)).getName();

            String name = prefix + Long.toString(n) + suffix;
            java.io.File f = new java.io.File(dir, name);
            if (!name.equals(f.getName()) || f.isInvalid()) {
                if (System.getSecurityManager() != null)
                    throw new IOException("Unable to create temporary file");
                else
                    throw new IOException("Unable to create temporary file, " + f);
            }
            return f;
        }
    }

    /**
     * <p> Creates a new empty file in the specified directory, using the
     * given prefix and suffix strings to generate its name.  If this method
     * returns successfully then it is guaranteed that:
     *
     * <ol>
     * <li> The file denoted by the returned abstract pathname did not exist
     * before this method was invoked, and
     * <li> Neither this method nor any of its variants will return the same
     * abstract pathname again in the current invocation of the virtual
     * machine.
     * </ol>
     * <p>
     * This method provides only part of a temporary-file facility.  To arrange
     * for a file created by this method to be deleted automatically, use the
     * <code>{@link #deleteOnExit}</code> method.
     *
     * <p> The <code>prefix</code> argument must be at least three characters
     * long.  It is recommended that the prefix be a short, meaningful string
     * such as <code>"hjb"</code> or <code>"mail"</code>.  The
     * <code>suffix</code> argument may be <code>null</code>, in which case the
     * suffix <code>".tmp"</code> will be used.
     *
     * <p> To create the new file, the prefix and the suffix may first be
     * adjusted to fit the limitations of the underlying platform.  If the
     * prefix is too long then it will be truncated, but its first three
     * characters will always be preserved.  If the suffix is too long then it
     * too will be truncated, but if it begins with a period character
     * (<code>'.'</code>) then the period and the first three characters
     * following it will always be preserved.  Once these adjustments have been
     * made the name of the new file will be generated by concatenating the
     * prefix, five or more internally-generated characters, and the suffix.
     *
     * <p> If the <code>directory</code> argument is <code>null</code> then the
     * system-dependent default temporary-file directory will be used.  The
     * default temporary-file directory is specified by the system property
     * <code>java.io.tmpdir</code>.  On UNIX systems the default value of this
     * property is typically <code>"/tmp"</code> or <code>"/var/tmp"</code>; on
     * Microsoft Windows systems it is typically <code>"C:\\WINNT\\TEMP"</code>.  A different
     * value may be given to this system property when the Java virtual machine
     * is invoked, but programmatic changes to this property are not guaranteed
     * to have any effect upon the temporary directory used by this method.
     *
     * @param prefix    The prefix string to be used in generating the file's
     *                  name; must be at least three characters long
     * @param suffix    The suffix string to be used in generating the file's
     *                  name; may be <code>null</code>, in which case the
     *                  suffix <code>".tmp"</code> will be used
     * @param directory The directory in which the file is to be created, or
     *                  <code>null</code> if the default temporary-file
     *                  directory is to be used
     * @return An abstract pathname denoting a newly-created empty file
     * @throws IllegalArgumentException If the <code>prefix</code> argument contains fewer than three
     *                                  characters
     * @throws IOException              If a file could not be created
     * @throws SecurityException        If a security manager exists and its <code>{@link
     *                                  java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                                  method does not allow a file to be created
     * @since 1.2
     */
    public static java.io.File createTempFile(String prefix, String suffix,
                                              java.io.File directory)
            throws IOException {
        if (prefix.length() < 3)
            throw new IllegalArgumentException("Prefix string too short");
        if (suffix == null)
            suffix = ".tmp";

        java.io.File tmpdir = (directory != null) ? directory
                : java.io.File.TempDirectory.location();
        SecurityManager sm = System.getSecurityManager();
        java.io.File f;
        do {
            f = java.io.File.TempDirectory.generateFile(prefix, suffix, tmpdir);

            if (sm != null) {
                try {
                    sm.checkWrite(f.getPath());
                } catch (SecurityException se) {
                    // don't reveal temporary directory location
                    if (directory == null)
                        throw new SecurityException("Unable to create temporary file");
                    throw se;
                }
            }
        } while ((fs.getBooleanAttributes(f) & java.io.FileSystem.BA_EXISTS) != 0);

        if (!fs.createFileExclusively(f.getPath()))
            throw new IOException("Unable to create temporary file");

        return f;
    }

    /**
     * Creates an empty file in the default temporary-file directory, using
     * the given prefix and suffix to generate its name. Invoking this method
     * is equivalent to invoking <code>{@link #createTempFile(java.lang.String,
     * java.lang.String, java.io.File)
     * createTempFile(prefix,&nbsp;suffix,&nbsp;null)}</code>.
     *
     * <p> The {@link
     * java.nio.file.Files#createTempFile(String, String, java.nio.file.attribute.FileAttribute[])
     * Files.createTempFile} method provides an alternative method to create an
     * empty file in the temporary-file directory. Files created by that method
     * may have more restrictive access permissions to files created by this
     * method and so may be more suited to security-sensitive applications.
     *
     * @param prefix The prefix string to be used in generating the file's
     *               name; must be at least three characters long
     * @param suffix The suffix string to be used in generating the file's
     *               name; may be <code>null</code>, in which case the
     *               suffix <code>".tmp"</code> will be used
     * @return An abstract pathname denoting a newly-created empty file
     * @throws IllegalArgumentException If the <code>prefix</code> argument contains fewer than three
     *                                  characters
     * @throws IOException              If a file could not be created
     * @throws SecurityException        If a security manager exists and its <code>{@link
     *                                  java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
     *                                  method does not allow a file to be created
     * @see java.nio.file.Files#createTempDirectory(String, FileAttribute[])
     * @since 1.2
     */
    public static java.io.File createTempFile(String prefix, String suffix)
            throws IOException {
        return createTempFile(prefix, suffix, null);
    }

    /* -- Basic infrastructure -- */

    /**
     * Compares two abstract pathnames lexicographically.  The ordering
     * defined by this method depends upon the underlying system.  On UNIX
     * systems, alphabetic case is significant in comparing pathnames; on Microsoft Windows
     * systems it is not.
     *
     * @param pathname The abstract pathname to be compared to this abstract
     *                 pathname
     * @return Zero if the argument is equal to this abstract pathname, a
     * value less than zero if this abstract pathname is
     * lexicographically less than the argument, or a value greater
     * than zero if this abstract pathname is lexicographically
     * greater than the argument
     * @since 1.2
     */
    public int compareTo(java.io.File pathname) {
        return fs.compare(this, pathname);
    }

    /**
     * Tests this abstract pathname for equality with the given object.
     * Returns <code>true</code> if and only if the argument is not
     * <code>null</code> and is an abstract pathname that denotes the same file
     * or directory as this abstract pathname.  Whether or not two abstract
     * pathnames are equal depends upon the underlying system.  On UNIX
     * systems, alphabetic case is significant in comparing pathnames; on Microsoft Windows
     * systems it is not.
     *
     * @param obj The object to be compared with this abstract pathname
     * @return <code>true</code> if and only if the objects are the same;
     * <code>false</code> otherwise
     */
    public boolean equals(Object obj) {
        if ((obj != null) && (obj instanceof java.io.File)) {
            return compareTo((java.io.File) obj) == 0;
        }
        return false;
    }

    /**
     * Computes a hash code for this abstract pathname.  Because equality of
     * abstract pathnames is inherently system-dependent, so is the computation
     * of their hash codes.  On UNIX systems, the hash code of an abstract
     * pathname is equal to the exclusive <em>or</em> of the hash code
     * of its pathname string and the decimal value
     * <code>1234321</code>.  On Microsoft Windows systems, the hash
     * code is equal to the exclusive <em>or</em> of the hash code of
     * its pathname string converted to lower case and the decimal
     * value <code>1234321</code>.  Locale is not taken into account on
     * lowercasing the pathname string.
     *
     * @return A hash code for this abstract pathname
     */
    public int hashCode() {
        return fs.hashCode(this);
    }

    /**
     * Returns the pathname string of this abstract pathname.  This is just the
     * string returned by the <code>{@link #getPath}</code> method.
     *
     * @return The string form of this abstract pathname
     */
    public String toString() {
        return getPath();
    }

    /**
     * WriteObject is called to save this filename.
     * The separator character is saved also so it can be replaced
     * in case the path is reconstituted on a different host type.
     * <p>
     *
     * @serialData Default fields followed by separator character.
     */
    private synchronized void writeObject(java.io.ObjectOutputStream s)
            throws IOException {
        s.defaultWriteObject();
        s.writeChar(separatorChar); // Add the separator character
    }

    /**
     * readObject is called to restore this filename.
     * The original separator character is read.  If it is different
     * than the separator character on this system, then the old separator
     * is replaced by the local separator.
     */
    private synchronized void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField fields = s.readFields();
        String pathField = (String) fields.get("path", null);
        char sep = s.readChar(); // read the previous separator char
        if (sep != separatorChar)
            pathField = pathField.replace(sep, separatorChar);
        String path = fs.normalize(pathField);
        UNSAFE.putObject(this, PATH_OFFSET, path);
        UNSAFE.putIntVolatile(this, PREFIX_LENGTH_OFFSET, fs.prefixLength(path));
    }

    private static final long PATH_OFFSET;
    private static final long PREFIX_LENGTH_OFFSET;
    private static final sun.misc.Unsafe UNSAFE;

    static {
        try {
            sun.misc.Unsafe unsafe = sun.misc.Unsafe.getUnsafe();
            PATH_OFFSET = unsafe.objectFieldOffset(
                    java.io.File.class.getDeclaredField("path"));
            PREFIX_LENGTH_OFFSET = unsafe.objectFieldOffset(
                    java.io.File.class.getDeclaredField("prefixLength"));
            UNSAFE = unsafe;
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }


    /**
     * use serialVersionUID from JDK 1.0.2 for interoperability
     */
    private static final long serialVersionUID = 301077366599181567L;

    // -- Integration with java.nio.file --

    private volatile transient Path filePath;

    /**
     * Returns a {@link Path java.nio.file.Path} object constructed from the
     * this abstract path. The resulting {@code Path} is associated with the
     * {@link java.nio.file.FileSystems#getDefault default-filesystem}.
     *
     * <p> The first invocation of this method works as if invoking it were
     * equivalent to evaluating the expression:
     * <blockquote><pre>
     * {@link java.nio.file.FileSystems#getDefault FileSystems.getDefault}().{@link
     * java.nio.file.FileSystem#getPath getPath}(this.{@link #getPath getPath}());
     * </pre></blockquote>
     * Subsequent invocations of this method return the same {@code Path}.
     *
     * <p> If this abstract pathname is the empty abstract pathname then this
     * method returns a {@code Path} that may be used to access the current
     * user directory.
     *
     * @return a {@code Path} constructed from this abstract path
     * @throws java.nio.file.InvalidPathException if a {@code Path} object cannot be constructed from the abstract
     *                                            path (see {@link java.nio.file.FileSystem#getPath FileSystem.getPath})
     * @see Path#toFile
     * @since 1.7
     */
    public Path toPath() {
        Path result = filePath;
        if (result == null) {
            synchronized (this) {
                result = filePath;
                if (result == null) {
                    result = FileSystems.getDefault().getPath(path);
                    filePath = result;
                }
            }
        }
        return result;
    }
}
