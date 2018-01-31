begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLClassLoader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|Manifest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|TeeInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FileUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/** Run a Hadoop job jar. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|RunJar
specifier|public
class|class
name|RunJar
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RunJar
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Pattern that matches any string. */
DECL|field|MATCH_ANY
specifier|public
specifier|static
specifier|final
name|Pattern
name|MATCH_ANY
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*"
argument_list|)
decl_stmt|;
comment|/**    * Priority of the RunJar shutdown hook.    */
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|public
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|10
decl_stmt|;
comment|/**    * Environment key for using the client classloader.    */
DECL|field|HADOOP_USE_CLIENT_CLASSLOADER
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_USE_CLIENT_CLASSLOADER
init|=
literal|"HADOOP_USE_CLIENT_CLASSLOADER"
decl_stmt|;
comment|/**    * Environment key for the (user-provided) hadoop classpath.    */
DECL|field|HADOOP_CLASSPATH
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_CLASSPATH
init|=
literal|"HADOOP_CLASSPATH"
decl_stmt|;
comment|/**    * Environment key for the system classes.    */
DECL|field|HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES
specifier|public
specifier|static
specifier|final
name|String
name|HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES
init|=
literal|"HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES"
decl_stmt|;
comment|/**    * Buffer size for copy the content of compressed file to new file.    */
DECL|field|BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|BUFFER_SIZE
init|=
literal|8_192
decl_stmt|;
comment|/**    * Unpack a jar file into a directory.    *    * This version unpacks all files inside the jar regardless of filename.    *    * @param jarFile the .jar file to unpack    * @param toDir the destination directory into which to unpack the jar    *    * @throws IOException if an I/O error has occurred or toDir    * cannot be created and does not already exist    */
DECL|method|unJar (File jarFile, File toDir)
specifier|public
specifier|static
name|void
name|unJar
parameter_list|(
name|File
name|jarFile
parameter_list|,
name|File
name|toDir
parameter_list|)
throws|throws
name|IOException
block|{
name|unJar
argument_list|(
name|jarFile
argument_list|,
name|toDir
argument_list|,
name|MATCH_ANY
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unpack matching files from a jar. Entries inside the jar that do    * not match the given pattern will be skipped.    *    * @param inputStream the jar stream to unpack    * @param toDir the destination directory into which to unpack the jar    * @param unpackRegex the pattern to match jar entries against    *    * @throws IOException if an I/O error has occurred or toDir    * cannot be created and does not already exist    */
DECL|method|unJar (InputStream inputStream, File toDir, Pattern unpackRegex)
specifier|public
specifier|static
name|void
name|unJar
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|File
name|toDir
parameter_list|,
name|Pattern
name|unpackRegex
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|JarInputStream
name|jar
init|=
operator|new
name|JarInputStream
argument_list|(
name|inputStream
argument_list|)
init|)
block|{
name|int
name|numOfFailedLastModifiedSet
init|=
literal|0
decl_stmt|;
for|for
control|(
name|JarEntry
name|entry
init|=
name|jar
operator|.
name|getNextJarEntry
argument_list|()
init|;
name|entry
operator|!=
literal|null
condition|;
name|entry
operator|=
name|jar
operator|.
name|getNextJarEntry
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|entry
operator|.
name|isDirectory
argument_list|()
operator|&&
name|unpackRegex
operator|.
name|matcher
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|toDir
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ensureDirectory
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|jar
argument_list|,
name|out
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|file
operator|.
name|setLastModified
argument_list|(
name|entry
operator|.
name|getTime
argument_list|()
argument_list|)
condition|)
block|{
name|numOfFailedLastModifiedSet
operator|++
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|numOfFailedLastModifiedSet
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not set last modfied time for {} file(s)"
argument_list|,
name|numOfFailedLastModifiedSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Unpack matching files from a jar. Entries inside the jar that do    * not match the given pattern will be skipped. Keep also a copy    * of the entire jar in the same directory for backward compatibility.    * TODO remove this feature in a new release and do only unJar    *    * @param inputStream the jar stream to unpack    * @param toDir the destination directory into which to unpack the jar    * @param unpackRegex the pattern to match jar entries against    *    * @throws IOException if an I/O error has occurred or toDir    * cannot be created and does not already exist    */
annotation|@
name|Deprecated
DECL|method|unJarAndSave (InputStream inputStream, File toDir, String name, Pattern unpackRegex)
specifier|public
specifier|static
name|void
name|unJarAndSave
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|File
name|toDir
parameter_list|,
name|String
name|name
parameter_list|,
name|Pattern
name|unpackRegex
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|toDir
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|ensureDirectory
argument_list|(
name|toDir
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|jar
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
init|;
name|TeeInputStream
name|teeInputStream
operator|=
operator|new
name|TeeInputStream
argument_list|(
name|inputStream
argument_list|,
name|jar
argument_list|)
init|)
block|{
name|unJar
argument_list|(
name|teeInputStream
argument_list|,
name|toDir
argument_list|,
name|unpackRegex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Unpack matching files from a jar. Entries inside the jar that do    * not match the given pattern will be skipped.    *    * @param jarFile the .jar file to unpack    * @param toDir the destination directory into which to unpack the jar    * @param unpackRegex the pattern to match jar entries against    *    * @throws IOException if an I/O error has occurred or toDir    * cannot be created and does not already exist    */
DECL|method|unJar (File jarFile, File toDir, Pattern unpackRegex)
specifier|public
specifier|static
name|void
name|unJar
parameter_list|(
name|File
name|jarFile
parameter_list|,
name|File
name|toDir
parameter_list|,
name|Pattern
name|unpackRegex
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|JarFile
name|jar
init|=
operator|new
name|JarFile
argument_list|(
name|jarFile
argument_list|)
init|)
block|{
name|int
name|numOfFailedLastModifiedSet
init|=
literal|0
decl_stmt|;
name|Enumeration
argument_list|<
name|JarEntry
argument_list|>
name|entries
init|=
name|jar
operator|.
name|entries
argument_list|()
decl_stmt|;
while|while
condition|(
name|entries
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|JarEntry
name|entry
init|=
name|entries
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|entry
operator|.
name|isDirectory
argument_list|()
operator|&&
name|unpackRegex
operator|.
name|matcher
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|matches
argument_list|()
condition|)
block|{
try|try
init|(
name|InputStream
name|in
init|=
name|jar
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
init|)
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|toDir
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ensureDirectory
argument_list|(
name|file
operator|.
name|getParentFile
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|file
operator|.
name|setLastModified
argument_list|(
name|entry
operator|.
name|getTime
argument_list|()
argument_list|)
condition|)
block|{
name|numOfFailedLastModifiedSet
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|numOfFailedLastModifiedSet
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not set last modfied time for {} file(s)"
argument_list|,
name|numOfFailedLastModifiedSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Ensure the existence of a given directory.    *    * @param dir Directory to check    *    * @throws IOException if it cannot be created and does not already exist    */
DECL|method|ensureDirectory (File dir)
specifier|private
specifier|static
name|void
name|ensureDirectory
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|mkdirs
argument_list|()
operator|&&
operator|!
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|dir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** Run a Hadoop job jar.  If the main class is not in the jar's manifest,    * then it must be provided on the command line. */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
operator|new
name|RunJar
argument_list|()
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|run (String[] args)
specifier|public
name|void
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
name|String
name|usage
init|=
literal|"RunJar jarFile [mainClass] args..."
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|int
name|firstArg
init|=
literal|0
decl_stmt|;
name|String
name|fileName
init|=
name|args
index|[
name|firstArg
operator|++
index|]
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"JAR does not exist or is not a normal file: "
operator|+
name|file
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|mainClassName
init|=
literal|null
decl_stmt|;
name|JarFile
name|jarFile
decl_stmt|;
try|try
block|{
name|jarFile
operator|=
operator|new
name|JarFile
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error opening job jar: "
operator|+
name|fileName
argument_list|)
operator|.
name|initCause
argument_list|(
name|io
argument_list|)
throw|;
block|}
name|Manifest
name|manifest
init|=
name|jarFile
operator|.
name|getManifest
argument_list|()
decl_stmt|;
if|if
condition|(
name|manifest
operator|!=
literal|null
condition|)
block|{
name|mainClassName
operator|=
name|manifest
operator|.
name|getMainAttributes
argument_list|()
operator|.
name|getValue
argument_list|(
literal|"Main-Class"
argument_list|)
expr_stmt|;
block|}
name|jarFile
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|mainClassName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|usage
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|mainClassName
operator|=
name|args
index|[
name|firstArg
operator|++
index|]
expr_stmt|;
block|}
name|mainClassName
operator|=
name|mainClassName
operator|.
name|replaceAll
argument_list|(
literal|"/"
argument_list|,
literal|"."
argument_list|)
expr_stmt|;
name|File
name|tmpDir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
name|ensureDirectory
argument_list|(
name|tmpDir
argument_list|)
expr_stmt|;
specifier|final
name|File
name|workDir
decl_stmt|;
try|try
block|{
name|workDir
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"hadoop-unjar"
argument_list|,
literal|""
argument_list|,
name|tmpDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// If user has insufficient perms to write to tmpDir, default
comment|// "Permission denied" message doesn't specify a filename.
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error creating temp dir in java.io.tmpdir "
operator|+
name|tmpDir
operator|+
literal|" due to "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|workDir
operator|.
name|delete
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Delete failed for "
operator|+
name|workDir
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|ensureDirectory
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|workDir
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|unJar
argument_list|(
name|file
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|ClassLoader
name|loader
init|=
name|createClassLoader
argument_list|(
name|file
argument_list|,
name|workDir
argument_list|)
decl_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setContextClassLoader
argument_list|(
name|loader
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|mainClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|mainClassName
argument_list|,
literal|true
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|Method
name|main
init|=
name|mainClass
operator|.
name|getMethod
argument_list|(
literal|"main"
argument_list|,
name|String
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|newArgsSubList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|args
argument_list|)
operator|.
name|subList
argument_list|(
name|firstArg
argument_list|,
name|args
operator|.
name|length
argument_list|)
decl_stmt|;
name|String
index|[]
name|newArgs
init|=
name|newArgsSubList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|newArgsSubList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
try|try
block|{
name|main
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
operator|new
name|Object
index|[]
block|{
name|newArgs
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|e
operator|.
name|getTargetException
argument_list|()
throw|;
block|}
block|}
comment|/**    * Creates a classloader based on the environment that was specified by the    * user. If HADOOP_USE_CLIENT_CLASSLOADER is specified, it creates an    * application classloader that provides the isolation of the user class space    * from the hadoop classes and their dependencies. It forms a class space for    * the user jar as well as the HADOOP_CLASSPATH. Otherwise, it creates a    * classloader that simply adds the user jar to the classpath.    */
DECL|method|createClassLoader (File file, final File workDir)
specifier|private
name|ClassLoader
name|createClassLoader
parameter_list|(
name|File
name|file
parameter_list|,
specifier|final
name|File
name|workDir
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|ClassLoader
name|loader
decl_stmt|;
comment|// see if the client classloader is enabled
if|if
condition|(
name|useClientClassLoader
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|workDir
argument_list|)
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparator
argument_list|)
operator|.
name|append
argument_list|(
name|file
argument_list|)
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparator
argument_list|)
operator|.
name|append
argument_list|(
name|workDir
argument_list|)
operator|.
name|append
argument_list|(
literal|"/classes/"
argument_list|)
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparator
argument_list|)
operator|.
name|append
argument_list|(
name|workDir
argument_list|)
operator|.
name|append
argument_list|(
literal|"/lib/*"
argument_list|)
expr_stmt|;
comment|// HADOOP_CLASSPATH is added to the client classpath
name|String
name|hadoopClasspath
init|=
name|getHadoopClasspath
argument_list|()
decl_stmt|;
if|if
condition|(
name|hadoopClasspath
operator|!=
literal|null
operator|&&
operator|!
name|hadoopClasspath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|File
operator|.
name|pathSeparator
argument_list|)
operator|.
name|append
argument_list|(
name|hadoopClasspath
argument_list|)
expr_stmt|;
block|}
name|String
name|clientClasspath
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// get the system classes
name|String
name|systemClasses
init|=
name|getSystemClasses
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|systemClassesList
init|=
name|systemClasses
operator|==
literal|null
condition|?
literal|null
else|:
name|Arrays
operator|.
name|asList
argument_list|(
name|StringUtils
operator|.
name|getTrimmedStrings
argument_list|(
name|systemClasses
argument_list|)
argument_list|)
decl_stmt|;
comment|// create an application classloader that isolates the user classes
name|loader
operator|=
operator|new
name|ApplicationClassLoader
argument_list|(
name|clientClasspath
argument_list|,
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
name|systemClassesList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|URL
argument_list|>
name|classPath
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|classPath
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|workDir
operator|+
literal|"/"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
name|classPath
operator|.
name|add
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
name|classPath
operator|.
name|add
argument_list|(
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"classes/"
argument_list|)
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
name|File
index|[]
name|libs
init|=
operator|new
name|File
argument_list|(
name|workDir
argument_list|,
literal|"lib"
argument_list|)
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|libs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|lib
range|:
name|libs
control|)
block|{
name|classPath
operator|.
name|add
argument_list|(
name|lib
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// create a normal parent-delegating classloader
name|loader
operator|=
operator|new
name|URLClassLoader
argument_list|(
name|classPath
operator|.
name|toArray
argument_list|(
operator|new
name|URL
index|[
name|classPath
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|loader
return|;
block|}
DECL|method|useClientClassLoader ()
name|boolean
name|useClientClassLoader
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getenv
argument_list|(
name|HADOOP_USE_CLIENT_CLASSLOADER
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getHadoopClasspath ()
name|String
name|getHadoopClasspath
parameter_list|()
block|{
return|return
name|System
operator|.
name|getenv
argument_list|(
name|HADOOP_CLASSPATH
argument_list|)
return|;
block|}
DECL|method|getSystemClasses ()
name|String
name|getSystemClasses
parameter_list|()
block|{
return|return
name|System
operator|.
name|getenv
argument_list|(
name|HADOOP_CLIENT_CLASSLOADER_SYSTEM_CLASSES
argument_list|)
return|;
block|}
block|}
end_class

end_unit

