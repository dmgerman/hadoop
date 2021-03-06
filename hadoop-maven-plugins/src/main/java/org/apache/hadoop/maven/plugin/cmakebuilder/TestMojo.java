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
name|execution
operator|.
name|MavenSession
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
name|BufferedWriter
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
name|OutputStreamWriter
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
name|HashMap
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
comment|/**  * Goal which runs a native unit test.  */
end_comment

begin_class
annotation|@
name|Mojo
argument_list|(
name|name
operator|=
literal|"cmake-test"
argument_list|,
name|defaultPhase
operator|=
name|LifecyclePhase
operator|.
name|TEST
argument_list|)
DECL|class|TestMojo
specifier|public
class|class
name|TestMojo
extends|extends
name|AbstractMojo
block|{
comment|/**    * A value for -Dtest= that runs all native tests.    */
DECL|field|ALL_NATIVE
specifier|private
specifier|final
specifier|static
name|String
name|ALL_NATIVE
init|=
literal|"allNative"
decl_stmt|;
comment|/**    * Location of the binary to run.    */
annotation|@
name|Parameter
argument_list|(
name|required
operator|=
literal|true
argument_list|)
DECL|field|binary
specifier|private
name|File
name|binary
decl_stmt|;
comment|/**    * Name of this test.    *    * Defaults to the basename of the binary.  So if your binary is /foo/bar/baz,    * this will default to 'baz.'    */
annotation|@
name|Parameter
DECL|field|testName
specifier|private
name|String
name|testName
decl_stmt|;
comment|/**    * Environment variables to pass to the binary.    */
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
comment|/**    * Arguments to pass to the binary.    */
annotation|@
name|Parameter
DECL|field|args
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Number of seconds to wait before declaring the test failed.    *    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"600"
argument_list|)
DECL|field|timeout
specifier|private
name|int
name|timeout
decl_stmt|;
comment|/**    * The working directory to use.    */
annotation|@
name|Parameter
DECL|field|workingDirectory
specifier|private
name|File
name|workingDirectory
decl_stmt|;
comment|/**    * Path to results directory.    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"native-results"
argument_list|)
DECL|field|results
specifier|private
name|File
name|results
decl_stmt|;
comment|/**    * A list of preconditions which must be true for this test to be run.    */
annotation|@
name|Parameter
DECL|field|preconditions
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|preconditions
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * If true, pass over the test without an error if the binary is missing.    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"false"
argument_list|)
DECL|field|skipIfMissing
specifier|private
name|boolean
name|skipIfMissing
decl_stmt|;
comment|/**    * What result to expect from the test    *    * Can be either "success", "failure", or "any".    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"success"
argument_list|)
DECL|field|expectedResult
specifier|private
name|String
name|expectedResult
decl_stmt|;
comment|/**    * The Maven Session Object.    */
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${session}"
argument_list|,
name|readonly
operator|=
literal|true
argument_list|,
name|required
operator|=
literal|true
argument_list|)
DECL|field|session
specifier|private
name|MavenSession
name|session
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
comment|/**    * The test thread waits for the process to terminate.    *    * Since Process#waitFor doesn't take a timeout argument, we simulate one by    * interrupting this thread after a certain amount of time has elapsed.    */
DECL|class|TestThread
specifier|private
specifier|static
class|class
name|TestThread
extends|extends
name|Thread
block|{
DECL|field|proc
specifier|private
name|Process
name|proc
decl_stmt|;
DECL|field|retCode
specifier|private
name|int
name|retCode
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|TestThread (Process proc)
specifier|public
name|TestThread
parameter_list|(
name|Process
name|proc
parameter_list|)
block|{
name|this
operator|.
name|proc
operator|=
name|proc
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|retCode
operator|=
name|proc
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|retCode
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
DECL|method|retCode ()
specifier|public
name|int
name|retCode
parameter_list|()
block|{
return|return
name|retCode
return|;
block|}
block|}
comment|/**    * Write to the status file.    *    * The status file will contain a string describing the exit status of the    * test.  It will be SUCCESS if the test returned success (return code 0), a    * numerical code if it returned a non-zero status, or IN_PROGRESS or    * TIMED_OUT.    */
DECL|method|writeStatusFile (String status)
specifier|private
name|void
name|writeStatusFile
parameter_list|(
name|String
name|status
parameter_list|)
throws|throws
name|IOException
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|results
argument_list|,
name|testName
operator|+
literal|".pstatus"
argument_list|)
argument_list|)
decl_stmt|;
name|BufferedWriter
name|out
init|=
literal|null
decl_stmt|;
try|try
block|{
name|out
operator|=
operator|new
name|BufferedWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|fos
argument_list|,
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|status
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|isTruthy (String str)
specifier|private
specifier|static
name|boolean
name|isTruthy
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|""
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"false"
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"off"
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"disable"
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|field|VALID_PRECONDITION_TYPES_STR
specifier|final
specifier|static
specifier|private
name|String
name|VALID_PRECONDITION_TYPES_STR
init|=
literal|"Valid precondition types are \"and\", \"andNot\""
decl_stmt|;
comment|/**    * Validate the parameters that the user has passed.    * @throws MojoExecutionException    */
DECL|method|validateParameters ()
specifier|private
name|void
name|validateParameters
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
if|if
condition|(
operator|!
operator|(
name|expectedResult
operator|.
name|equals
argument_list|(
literal|"success"
argument_list|)
operator|||
name|expectedResult
operator|.
name|equals
argument_list|(
literal|"failure"
argument_list|)
operator|||
name|expectedResult
operator|.
name|equals
argument_list|(
literal|"any"
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"expectedResult must be either "
operator|+
literal|"success, failure, or any"
argument_list|)
throw|;
block|}
block|}
DECL|method|shouldRunTest ()
specifier|private
name|boolean
name|shouldRunTest
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
comment|// Were we told to skip all tests?
name|String
name|skipTests
init|=
name|session
operator|.
name|getSystemProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"skipTests"
argument_list|)
decl_stmt|;
if|if
condition|(
name|isTruthy
argument_list|(
name|skipTests
argument_list|)
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"skipTests is in effect for test "
operator|+
name|testName
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Does the binary exist?  If not, we shouldn't try to run it.
if|if
condition|(
operator|!
name|binary
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|skipIfMissing
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Skipping missing test "
operator|+
name|testName
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Test "
operator|+
name|binary
operator|+
literal|" was not built!  (File does not exist.)"
argument_list|)
throw|;
block|}
block|}
comment|// If there is an explicit list of tests to run, it should include this
comment|// test.
name|String
name|testProp
init|=
name|session
operator|.
name|getSystemProperties
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
name|testProp
operator|!=
literal|null
condition|)
block|{
name|String
name|testPropArr
index|[]
init|=
name|testProp
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|test
range|:
name|testPropArr
control|)
block|{
if|if
condition|(
name|test
operator|.
name|equals
argument_list|(
name|ALL_NATIVE
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|test
operator|.
name|equals
argument_list|(
name|testName
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"did not find test '"
operator|+
name|testName
operator|+
literal|"' in "
operator|+
literal|"list "
operator|+
name|testProp
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|// Are all the preconditions satistfied?
if|if
condition|(
name|preconditions
operator|!=
literal|null
condition|)
block|{
name|int
name|idx
init|=
literal|1
decl_stmt|;
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
name|preconditions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|val
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"NULL is not a valid "
operator|+
literal|"precondition type.  "
operator|+
name|VALID_PRECONDITION_TYPES_STR
argument_list|)
throw|;
block|}
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"and"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|isTruthy
argument_list|(
name|val
argument_list|)
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Skipping test "
operator|+
name|testName
operator|+
literal|" because precondition number "
operator|+
name|idx
operator|+
literal|" was not met."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
literal|"andNot"
argument_list|)
condition|)
block|{
if|if
condition|(
name|isTruthy
argument_list|(
name|val
argument_list|)
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Skipping test "
operator|+
name|testName
operator|+
literal|" because negative precondition number "
operator|+
name|idx
operator|+
literal|" was met."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
name|key
operator|+
literal|" is not a valid "
operator|+
literal|"precondition type.  "
operator|+
name|VALID_PRECONDITION_TYPES_STR
argument_list|)
throw|;
block|}
name|idx
operator|++
expr_stmt|;
block|}
block|}
comment|// OK, we should run this.
return|return
literal|true
return|;
block|}
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
if|if
condition|(
name|testName
operator|==
literal|null
condition|)
block|{
name|testName
operator|=
name|binary
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|validatePlatform
argument_list|()
expr_stmt|;
name|validateParameters
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|shouldRunTest
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|results
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|results
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Failed to create "
operator|+
literal|"output directory '"
operator|+
name|results
operator|+
literal|"'!"
argument_list|)
throw|;
block|}
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
name|binary
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"-------------------------------------------------------"
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|" C M A K E B U I L D E R    T E S T"
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"-------------------------------------------------------"
argument_list|)
expr_stmt|;
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|append
argument_list|(
name|testName
argument_list|)
operator|.
name|append
argument_list|(
literal|": running "
argument_list|)
expr_stmt|;
name|bld
operator|.
name|append
argument_list|(
name|binary
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|entry
range|:
name|args
control|)
block|{
name|cmd
operator|.
name|add
argument_list|(
name|entry
argument_list|)
expr_stmt|;
name|bld
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
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
name|Exec
operator|.
name|addEnvironment
argument_list|(
name|pb
argument_list|,
name|env
argument_list|)
expr_stmt|;
if|if
condition|(
name|workingDirectory
operator|!=
literal|null
condition|)
block|{
name|pb
operator|.
name|directory
argument_list|(
name|workingDirectory
argument_list|)
expr_stmt|;
block|}
name|pb
operator|.
name|redirectError
argument_list|(
operator|new
name|File
argument_list|(
name|results
argument_list|,
name|testName
operator|+
literal|".stderr"
argument_list|)
argument_list|)
expr_stmt|;
name|pb
operator|.
name|redirectOutput
argument_list|(
operator|new
name|File
argument_list|(
name|results
argument_list|,
name|testName
operator|+
literal|".stdout"
argument_list|)
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
name|Process
name|proc
init|=
literal|null
decl_stmt|;
name|TestThread
name|testThread
init|=
literal|null
decl_stmt|;
name|int
name|retCode
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|status
init|=
literal|"IN_PROGRESS"
decl_stmt|;
try|try
block|{
name|writeStatusFile
argument_list|(
name|status
argument_list|)
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
literal|"Error writing the status file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
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
name|testThread
operator|=
operator|new
name|TestThread
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|testThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|testThread
operator|.
name|join
argument_list|(
name|timeout
operator|*
literal|1000
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|testThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|retCode
operator|=
name|testThread
operator|.
name|retCode
argument_list|()
expr_stmt|;
name|testThread
operator|=
literal|null
expr_stmt|;
name|proc
operator|=
literal|null
expr_stmt|;
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
literal|"IOException while executing the test "
operator|+
name|testName
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
literal|"Interrupted while executing "
operator|+
literal|"the test "
operator|+
name|testName
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|testThread
operator|!=
literal|null
condition|)
block|{
comment|// If the test thread didn't exit yet, that means the timeout expired.
name|testThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|testThread
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
literal|"Interrupted while waiting for testThread"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|status
operator|=
literal|"TIMED OUT"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|retCode
operator|==
literal|0
condition|)
block|{
name|status
operator|=
literal|"SUCCESS"
expr_stmt|;
block|}
else|else
block|{
name|status
operator|=
literal|"ERROR CODE "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|retCode
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|writeStatusFile
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"failed to write status file!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
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
literal|"STATUS: "
operator|+
name|status
operator|+
literal|" after "
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
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"-------------------------------------------------------"
argument_list|)
expr_stmt|;
if|if
condition|(
name|status
operator|.
name|equals
argument_list|(
literal|"TIMED_OUT"
argument_list|)
condition|)
block|{
if|if
condition|(
name|expectedResult
operator|.
name|equals
argument_list|(
literal|"success"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Test "
operator|+
name|binary
operator|+
literal|" timed out after "
operator|+
name|timeout
operator|+
literal|" seconds!"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|status
operator|.
name|equals
argument_list|(
literal|"SUCCESS"
argument_list|)
condition|)
block|{
if|if
condition|(
name|expectedResult
operator|.
name|equals
argument_list|(
literal|"success"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Test "
operator|+
name|binary
operator|+
literal|" returned "
operator|+
name|status
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|expectedResult
operator|.
name|equals
argument_list|(
literal|"failure"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|MojoExecutionException
argument_list|(
literal|"Test "
operator|+
name|binary
operator|+
literal|" succeeded, but we expected failure!"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

