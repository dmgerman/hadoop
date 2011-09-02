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
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|io
operator|.
name|File
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
name|ArrayList
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
name|Manifest
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
name|conf
operator|.
name|Configuration
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
comment|/** Pattern that matches any string */
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
comment|/**    * Unpack a jar file into a directory.    *    * This version unpacks all files inside the jar regardless of filename.    */
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
comment|/**    * Unpack matching files from a jar. Entries inside the jar that do    * not match the given pattern will be skipped.    *    * @param jarFile the .jar file to unpack    * @param toDir the destination directory into which to unpack the jar    * @param unpackRegex the pattern to match jar entries against    */
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
name|JarFile
name|jar
init|=
operator|new
name|JarFile
argument_list|(
name|jarFile
argument_list|)
decl_stmt|;
try|try
block|{
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
name|JarEntry
name|entry
init|=
operator|(
name|JarEntry
operator|)
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
name|InputStream
name|in
init|=
name|jar
operator|.
name|getInputStream
argument_list|(
name|entry
argument_list|)
decl_stmt|;
try|try
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
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
literal|8192
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|jar
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Ensure the existence of a given directory.    *    * @throws IOException if it cannot be created and does not already exist    */
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
operator|new
name|Configuration
argument_list|()
operator|.
name|get
argument_list|(
literal|"hadoop.tmp.dir"
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
init|=
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
decl_stmt|;
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
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
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
argument_list|)
expr_stmt|;
name|unJar
argument_list|(
name|file
argument_list|,
name|workDir
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|URL
argument_list|>
name|classPath
init|=
operator|new
name|ArrayList
argument_list|<
name|URL
argument_list|>
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
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|libs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|classPath
operator|.
name|add
argument_list|(
name|libs
index|[
name|i
index|]
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
name|ClassLoader
name|loader
init|=
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
literal|0
index|]
argument_list|)
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
operator|new
name|Class
index|[]
block|{
name|Array
operator|.
name|newInstance
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|0
argument_list|)
operator|.
name|getClass
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|String
index|[]
name|newArgs
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
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
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
block|}
end_class

end_unit

