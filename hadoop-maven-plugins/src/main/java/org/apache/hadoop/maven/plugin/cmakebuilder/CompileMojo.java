begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.maven.plugin.cmakebuilder
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|maven
operator|.
name|plugin
operator|.
name|cmakebuilder
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|maven
operator|.
name|plugin
operator|.
name|util
operator|.
name|Exec
operator|.
name|OutputBufferThread
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
name|maven
operator|.
name|plugin
operator|.
name|util
operator|.
name|Exec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|LifecyclePhase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|Mojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|Parameter
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Goal which builds the native sources.  */
end_comment

begin_class
annotation|@
name|Mojo
argument_list|(
name|name
operator|=
literal|"cmake-compile"
argument_list|,
name|defaultPhase
operator|=
name|LifecyclePhase
operator|.
name|COMPILE
argument_list|)
DECL|class|CompileMojo
specifier|public
class|class
name|CompileMojo
extends|extends
name|AbstractMojo
block|{
DECL|field|availableProcessors
specifier|private
specifier|static
name|int
name|availableProcessors
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
decl_stmt|;
comment|/**    * Location of the build products.    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project.build.directory}/native"
argument_list|)
DECL|field|output
specifier|private
name|File
name|output
decl_stmt|;
comment|/**    * Location of the source files.    * This should be where the sources are checked in.    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${basedir}/src/main/native"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
DECL|field|source
specifier|private
name|File
name|source
decl_stmt|;
comment|/**    * CMake build target.    */
annotation|@
name|Parameter
DECL|field|target
specifier|private
name|String
name|target
decl_stmt|;
comment|/**    * Environment variables to pass to CMake.    *    * Note that it is usually better to use a CMake variable than an environment    * variable.  To quote the CMake FAQ:    *    * "One should avoid using environment variables for controlling the flow of    * CMake code (such as in IF commands). The build system generated by CMake    * may re-run CMake automatically when CMakeLists.txt files change. The    * environment in which this is executed is controlled by the build system and    * may not match that in which CMake was originally run. If you want to    * control build settings on the CMake command line, you need to use cache    * variables set with the -D option. The settings will be saved in    * CMakeCache.txt so that they don't have to be repeated every time CMake is    * run on the same build tree."    */
annotation|@
name|Parameter
DECL|field|env
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|env
decl_stmt|;
comment|/**    * CMake cached variables to set.    */
annotation|@
name|Parameter
DECL|field|vars
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|vars
decl_stmt|;
comment|// TODO: support Windows
DECL|method|validatePlatform ()
specifier|private
specifier|static
name|void
name|validatePlatform
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"windows"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"CMakeBuilder does not yet support "
operator|+
literal|"the Windows platform."
argument_list|)
throw|;
block|}
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|validatePlatform
argument_list|()
expr_stmt|;
name|runCMake
argument_list|()
expr_stmt|;
name|runMake
argument_list|()
expr_stmt|;
name|runMake
argument_list|()
expr_stmt|;
comment|// The second make is a workaround for HADOOP-9215.  It can be
comment|// removed when cmake 2.6 is no longer supported.
name|long
name|end
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"cmake compilation finished successfully in "
operator|+
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|convert
argument_list|(
name|end
operator|-
name|start
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
operator|+
literal|" millisecond(s)."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate that source parameters look sane.    */
DECL|method|validateSourceParams (File source, File output)
specifier|static
name|void
name|validateSourceParams
parameter_list|(
name|File
name|source
parameter_list|,
name|File
name|output
parameter_list|)
throws|throws
name|MojoExecutionException
block|{
name|String
name|cOutput
init|=
literal|null
decl_stmt|,
name|cSource
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cOutput
operator|=
name|output
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"error getting canonical path "
operator|+
literal|"for output"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|cSource
operator|=
name|source
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"error getting canonical path "
operator|+
literal|"for source"
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// This doesn't catch all the bad cases-- we could be following symlinks or
comment|// hardlinks, etc.  However, this will usually catch a common mistake.
if|if
condition|(
name|cSource
operator|.
name|startsWith
argument_list|(
name|cOutput
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"The source directory must not be "
operator|+
literal|"inside the output directory (it would be destroyed by "
operator|+
literal|"'mvn clean')"
argument_list|)
throw|;
block|}
block|}
DECL|method|runCMake ()
specifier|public
name|void
name|runCMake
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
name|validatePlatform
argument_list|()
expr_stmt|;
name|validateSourceParams
argument_list|(
name|source
argument_list|,
name|output
argument_list|)
expr_stmt|;
if|if
condition|(
name|output
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"mkdirs '"
operator|+
name|output
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"cmake"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|source
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|vars
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|entry
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|)
condition|)
block|{
name|cmd
operator|.
name|add
argument_list|(
literal|"-D"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|cmd
operator|.
name|add
argument_list|(
literal|"-G"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"Unix Makefiles"
argument_list|)
expr_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|cmd
control|)
block|{
name|bld
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|" "
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|bld
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"with extra environment variables "
operator|+
name|Exec
operator|.
name|envToString
argument_list|(
name|env
argument_list|)
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|pb
operator|.
name|directory
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|pb
operator|.
name|redirectErrorStream
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Exec
operator|.
name|addEnvironment
argument_list|(
name|pb
argument_list|,
name|env
argument_list|)
expr_stmt|;
name|Process
name|proc
init|=
literal|null
decl_stmt|;
name|OutputBufferThread
name|outThread
init|=
literal|null
decl_stmt|;
name|int
name|retCode
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|proc
operator|=
name|pb
operator|.
name|start
argument_list|()
expr_stmt|;
name|outThread
operator|=
operator|new
name|OutputBufferThread
argument_list|(
name|proc
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|outThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|retCode
operator|=
name|proc
operator|.
name|waitFor
argument_list|()
expr_stmt|;
if|if
condition|(
name|retCode
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"CMake failed with error code "
operator|+
name|retCode
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Error executing CMake"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Interrupted while waiting for "
operator|+
literal|"CMake process"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|proc
operator|!=
literal|null
condition|)
block|{
name|proc
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|outThread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|outThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|outThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"Interrupted while joining output thread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|retCode
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|String
name|line
range|:
name|outThread
operator|.
name|getOutput
argument_list|()
control|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|runMake ()
specifier|public
name|void
name|runMake
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|cmd
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"make"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"-j"
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|availableProcessors
argument_list|)
argument_list|)
expr_stmt|;
name|cmd
operator|.
name|add
argument_list|(
literal|"VERBOSE=1"
argument_list|)
expr_stmt|;
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
block|{
name|cmd
operator|.
name|add
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|cmd
control|)
block|{
name|bld
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|" "
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Running "
operator|+
name|bld
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|pb
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|pb
operator|.
name|directory
argument_list|(
name|output
argument_list|)
expr_stmt|;
name|Process
name|proc
init|=
literal|null
decl_stmt|;
name|int
name|retCode
init|=
operator|-
literal|1
decl_stmt|;
name|OutputBufferThread
name|stdoutThread
init|=
literal|null
decl_stmt|,
name|stderrThread
init|=
literal|null
decl_stmt|;
try|try
block|{
name|proc
operator|=
name|pb
operator|.
name|start
argument_list|()
expr_stmt|;
name|stdoutThread
operator|=
operator|new
name|OutputBufferThread
argument_list|(
name|proc
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|stderrThread
operator|=
operator|new
name|OutputBufferThread
argument_list|(
name|proc
operator|.
name|getErrorStream
argument_list|()
argument_list|)
expr_stmt|;
name|stdoutThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|stderrThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|retCode
operator|=
name|proc
operator|.
name|waitFor
argument_list|()
expr_stmt|;
if|if
condition|(
name|retCode
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"make failed with error code "
operator|+
name|retCode
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Interrupted during Process#waitFor"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Error executing make"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|stdoutThread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stdoutThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"Interrupted while joining stdoutThread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|retCode
operator|!=
literal|0
condition|)
block|{
for|for
control|(
name|String
name|line
range|:
name|stdoutThread
operator|.
name|getOutput
argument_list|()
control|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|stderrThread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|stderrThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"Interrupted while joining stderrThread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// We always print stderr, since it contains the compiler warning
comment|// messages.  These are interesting even if compilation succeeded.
for|for
control|(
name|String
name|line
range|:
name|stderrThread
operator|.
name|getOutput
argument_list|()
control|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|proc
operator|!=
literal|null
condition|)
block|{
name|proc
operator|.
name|destroy
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

