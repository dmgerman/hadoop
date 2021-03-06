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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
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
name|FileUtils
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
name|security
operator|.
name|alias
operator|.
name|AbstractJavaKeyStoreProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|FileNotFoundException
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
name|InterruptedIOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Shell
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_class
DECL|class|TestShell
specifier|public
class|class
name|TestShell
extends|extends
name|Assert
block|{
comment|/**    * Set the timeout for every test    */
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|30000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
DECL|field|rootTestDir
specifier|private
name|File
name|rootTestDir
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
decl_stmt|;
comment|/**    * A filename generated uniquely for each test method. The file    * itself is neither created nor deleted during test setup/teardown.    */
DECL|field|methodDir
specifier|private
name|File
name|methodDir
decl_stmt|;
DECL|class|Command
specifier|private
specifier|static
class|class
name|Command
extends|extends
name|Shell
block|{
DECL|field|runCount
specifier|private
name|int
name|runCount
init|=
literal|0
decl_stmt|;
DECL|method|Command (long interval)
specifier|private
name|Command
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|super
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExecString ()
specifier|protected
name|String
index|[]
name|getExecString
parameter_list|()
block|{
comment|// There is no /bin/echo equivalent on Windows so just launch it as a
comment|// shell built-in.
comment|//
return|return
name|WINDOWS
condition|?
operator|(
operator|new
name|String
index|[]
block|{
literal|"cmd.exe"
block|,
literal|"/c"
block|,
literal|"echo"
block|,
literal|"hello"
block|}
operator|)
else|:
operator|(
operator|new
name|String
index|[]
block|{
literal|"echo"
block|,
literal|"hello"
block|}
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseExecResult (BufferedReader lines)
specifier|protected
name|void
name|parseExecResult
parameter_list|(
name|BufferedReader
name|lines
parameter_list|)
throws|throws
name|IOException
block|{
operator|++
name|runCount
expr_stmt|;
block|}
DECL|method|getRunCount ()
specifier|public
name|int
name|getRunCount
parameter_list|()
block|{
return|return
name|runCount
return|;
block|}
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|rootTestDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not a directory "
operator|+
name|rootTestDir
argument_list|,
name|rootTestDir
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|methodDir
operator|=
operator|new
name|File
argument_list|(
name|rootTestDir
argument_list|,
name|methodName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInterval ()
specifier|public
name|void
name|testInterval
parameter_list|()
throws|throws
name|IOException
block|{
name|testInterval
argument_list|(
name|Long
operator|.
name|MIN_VALUE
operator|/
literal|60000
argument_list|)
expr_stmt|;
comment|// test a negative interval
name|testInterval
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
comment|// test a zero interval
name|testInterval
argument_list|(
literal|10L
argument_list|)
expr_stmt|;
comment|// interval equal to 10mins
name|testInterval
argument_list|(
name|Time
operator|.
name|now
argument_list|()
operator|/
literal|60000
operator|+
literal|60
argument_list|)
expr_stmt|;
comment|// test a very big interval
block|}
comment|/**    * Assert that a string has a substring in it    * @param string string to search    * @param search what to search for it    */
DECL|method|assertInString (String string, String search)
specifier|private
name|void
name|assertInString
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|search
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Empty String"
argument_list|,
name|string
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|string
operator|.
name|contains
argument_list|(
name|search
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Did not find \""
operator|+
name|search
operator|+
literal|"\" in "
operator|+
name|string
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testShellCommandExecutorToString ()
specifier|public
name|void
name|testShellCommandExecutorToString
parameter_list|()
throws|throws
name|Throwable
block|{
name|Shell
operator|.
name|ShellCommandExecutor
name|sce
init|=
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"ls"
block|,
literal|".."
block|,
literal|"arg 2"
block|}
argument_list|)
decl_stmt|;
name|String
name|command
init|=
name|sce
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertInString
argument_list|(
name|command
argument_list|,
literal|"ls"
argument_list|)
expr_stmt|;
name|assertInString
argument_list|(
name|command
argument_list|,
literal|" .. "
argument_list|)
expr_stmt|;
name|assertInString
argument_list|(
name|command
argument_list|,
literal|"\"arg 2\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testShellCommandTimeout ()
specifier|public
name|void
name|testShellCommandTimeout
parameter_list|()
throws|throws
name|Throwable
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|WINDOWS
argument_list|)
expr_stmt|;
name|String
name|rootDir
init|=
name|rootTestDir
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|File
name|shellFile
init|=
operator|new
name|File
argument_list|(
name|rootDir
argument_list|,
literal|"timeout.sh"
argument_list|)
decl_stmt|;
name|String
name|timeoutCommand
init|=
literal|"sleep 4; echo \"hello\""
decl_stmt|;
name|Shell
operator|.
name|ShellCommandExecutor
name|shexc
decl_stmt|;
try|try
init|(
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|shellFile
argument_list|)
argument_list|)
init|)
block|{
name|writer
operator|.
name|println
argument_list|(
name|timeoutCommand
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileUtil
operator|.
name|setExecutable
argument_list|(
name|shellFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|shexc
operator|=
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
name|shellFile
operator|.
name|getAbsolutePath
argument_list|()
block|}
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
argument_list|)
expr_stmt|;
try|try
block|{
name|shexc
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//When timing out exception is thrown.
block|}
name|shellFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Script did not timeout"
argument_list|,
name|shexc
operator|.
name|isTimedOut
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnvVarsWithInheritance ()
specifier|public
name|void
name|testEnvVarsWithInheritance
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|WINDOWS
argument_list|)
expr_stmt|;
name|testEnvHelper
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnvVarsWithoutInheritance ()
specifier|public
name|void
name|testEnvVarsWithoutInheritance
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|WINDOWS
argument_list|)
expr_stmt|;
name|testEnvHelper
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|testEnvHelper (boolean inheritParentEnv)
specifier|private
name|void
name|testEnvHelper
parameter_list|(
name|boolean
name|inheritParentEnv
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|customEnv
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|customEnv
operator|.
name|put
argument_list|(
literal|"AAA"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|"AAA"
argument_list|)
expr_stmt|;
name|customEnv
operator|.
name|put
argument_list|(
literal|"BBB"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|"BBB"
argument_list|)
expr_stmt|;
name|customEnv
operator|.
name|put
argument_list|(
literal|"CCC"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|"CCC"
argument_list|)
expr_stmt|;
name|Shell
operator|.
name|ShellCommandExecutor
name|command
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"env"
block|}
argument_list|,
literal|null
argument_list|,
name|customEnv
argument_list|,
literal|0L
argument_list|,
name|inheritParentEnv
argument_list|)
decl_stmt|;
name|command
operator|.
name|execute
argument_list|()
expr_stmt|;
name|String
index|[]
name|varsArr
init|=
name|command
operator|.
name|getOutput
argument_list|()
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|vars
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|var
range|:
name|varsArr
control|)
block|{
name|int
name|eqIndex
init|=
name|var
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
name|vars
operator|.
name|put
argument_list|(
name|var
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|eqIndex
argument_list|)
argument_list|,
name|var
operator|.
name|substring
argument_list|(
name|eqIndex
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedEnv
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|expectedEnv
operator|.
name|putAll
argument_list|(
name|customEnv
argument_list|)
expr_stmt|;
if|if
condition|(
name|inheritParentEnv
condition|)
block|{
name|expectedEnv
operator|.
name|putAll
argument_list|(
name|System
operator|.
name|getenv
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedEnv
argument_list|,
name|vars
argument_list|)
expr_stmt|;
block|}
DECL|method|countTimerThreads ()
specifier|private
specifier|static
name|int
name|countTimerThreads
parameter_list|()
block|{
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|ThreadInfo
index|[]
name|infos
init|=
name|threadBean
operator|.
name|getThreadInfo
argument_list|(
name|threadBean
operator|.
name|getAllThreadIds
argument_list|()
argument_list|,
literal|20
argument_list|)
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|info
range|:
name|infos
control|)
block|{
if|if
condition|(
name|info
operator|==
literal|null
condition|)
continue|continue;
for|for
control|(
name|StackTraceElement
name|elem
range|:
name|info
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
if|if
condition|(
name|elem
operator|.
name|getClassName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Timer"
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Test
DECL|method|testShellCommandTimerLeak ()
specifier|public
name|void
name|testShellCommandTimerLeak
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|quickCommand
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"/bin/sleep"
block|,
literal|"100"
block|}
decl_stmt|;
name|int
name|timersBefore
init|=
name|countTimerThreads
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"before: "
operator|+
name|timersBefore
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Shell
operator|.
name|ShellCommandExecutor
name|shexec
init|=
operator|new
name|Shell
operator|.
name|ShellCommandExecutor
argument_list|(
name|quickCommand
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|shexec
operator|.
name|execute
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Bad command should throw exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|int
name|timersAfter
init|=
name|countTimerThreads
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"after: "
operator|+
name|timersAfter
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|timersBefore
argument_list|,
name|timersAfter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetCheckProcessIsAliveCommand ()
specifier|public
name|void
name|testGetCheckProcessIsAliveCommand
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|anyPid
init|=
literal|"9999"
decl_stmt|;
name|String
index|[]
name|checkProcessAliveCommand
init|=
name|getCheckProcessIsAliveCommand
argument_list|(
name|anyPid
argument_list|)
decl_stmt|;
name|String
index|[]
name|expectedCommand
decl_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|expectedCommand
operator|=
operator|new
name|String
index|[]
block|{
name|getWinUtilsPath
argument_list|()
block|,
literal|"task"
block|,
literal|"isAlive"
block|,
name|anyPid
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Shell
operator|.
name|isSetsidAvailable
condition|)
block|{
name|expectedCommand
operator|=
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"kill -0 -- -'"
operator|+
name|anyPid
operator|+
literal|"'"
block|}
expr_stmt|;
block|}
else|else
block|{
name|expectedCommand
operator|=
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"kill -0 '"
operator|+
name|anyPid
operator|+
literal|"'"
block|}
expr_stmt|;
block|}
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|expectedCommand
argument_list|,
name|checkProcessAliveCommand
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetSignalKillCommand ()
specifier|public
name|void
name|testGetSignalKillCommand
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|anyPid
init|=
literal|"9999"
decl_stmt|;
name|int
name|anySignal
init|=
literal|9
decl_stmt|;
name|String
index|[]
name|checkProcessAliveCommand
init|=
name|getSignalKillCommand
argument_list|(
name|anySignal
argument_list|,
name|anyPid
argument_list|)
decl_stmt|;
name|String
index|[]
name|expectedCommand
decl_stmt|;
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
name|expectedCommand
operator|=
operator|new
name|String
index|[]
block|{
name|getWinUtilsPath
argument_list|()
block|,
literal|"task"
block|,
literal|"kill"
block|,
name|anyPid
block|}
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Shell
operator|.
name|isSetsidAvailable
condition|)
block|{
name|expectedCommand
operator|=
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"kill -9 -- -'"
operator|+
name|anyPid
operator|+
literal|"'"
block|}
expr_stmt|;
block|}
else|else
block|{
name|expectedCommand
operator|=
operator|new
name|String
index|[]
block|{
literal|"bash"
block|,
literal|"-c"
block|,
literal|"kill -9 '"
operator|+
name|anyPid
operator|+
literal|"'"
block|}
expr_stmt|;
block|}
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|expectedCommand
argument_list|,
name|checkProcessAliveCommand
argument_list|)
expr_stmt|;
block|}
DECL|method|testInterval (long interval)
specifier|private
name|void
name|testInterval
parameter_list|(
name|long
name|interval
parameter_list|)
throws|throws
name|IOException
block|{
name|Command
name|command
init|=
operator|new
name|Command
argument_list|(
name|interval
argument_list|)
decl_stmt|;
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|command
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
if|if
condition|(
name|interval
operator|>
literal|0
condition|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|command
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|command
operator|.
name|getRunCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeUnset ()
specifier|public
name|void
name|testHadoopHomeUnset
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertHomeResolveFailed
argument_list|(
literal|null
argument_list|,
literal|"unset"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeEmpty ()
specifier|public
name|void
name|testHadoopHomeEmpty
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertHomeResolveFailed
argument_list|(
literal|""
argument_list|,
name|E_HADOOP_PROPS_EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeEmptyDoubleQuotes ()
specifier|public
name|void
name|testHadoopHomeEmptyDoubleQuotes
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertHomeResolveFailed
argument_list|(
literal|"\"\""
argument_list|,
name|E_HADOOP_PROPS_EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeEmptySingleQuote ()
specifier|public
name|void
name|testHadoopHomeEmptySingleQuote
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertHomeResolveFailed
argument_list|(
literal|"\""
argument_list|,
name|E_HADOOP_PROPS_EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeValid ()
specifier|public
name|void
name|testHadoopHomeValid
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|f
init|=
name|checkHadoopHomeInner
argument_list|(
name|rootTestDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rootTestDir
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeValidQuoted ()
specifier|public
name|void
name|testHadoopHomeValidQuoted
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|f
init|=
name|checkHadoopHomeInner
argument_list|(
literal|'"'
operator|+
name|rootTestDir
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|'"'
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rootTestDir
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeNoDir ()
specifier|public
name|void
name|testHadoopHomeNoDir
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertHomeResolveFailed
argument_list|(
name|methodDir
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|E_DOES_NOT_EXIST
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeNotADir ()
specifier|public
name|void
name|testHadoopHomeNotADir
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|touched
init|=
name|touch
argument_list|(
name|methodDir
argument_list|)
decl_stmt|;
try|try
block|{
name|assertHomeResolveFailed
argument_list|(
name|touched
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|E_NOT_DIRECTORY
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|touched
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testHadoopHomeRelative ()
specifier|public
name|void
name|testHadoopHomeRelative
parameter_list|()
throws|throws
name|Throwable
block|{
name|assertHomeResolveFailed
argument_list|(
literal|"./target"
argument_list|,
name|E_IS_RELATIVE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBinDirMissing ()
specifier|public
name|void
name|testBinDirMissing
parameter_list|()
throws|throws
name|Throwable
block|{
name|FileNotFoundException
name|ex
init|=
name|assertWinutilsResolveFailed
argument_list|(
name|methodDir
argument_list|,
name|E_DOES_NOT_EXIST
argument_list|)
decl_stmt|;
name|assertInString
argument_list|(
name|ex
operator|.
name|toString
argument_list|()
argument_list|,
literal|"Hadoop bin directory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHadoopBinNotADir ()
specifier|public
name|void
name|testHadoopBinNotADir
parameter_list|()
throws|throws
name|Throwable
block|{
name|File
name|bin
init|=
operator|new
name|File
argument_list|(
name|methodDir
argument_list|,
literal|"bin"
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|bin
argument_list|)
expr_stmt|;
try|try
block|{
name|assertWinutilsResolveFailed
argument_list|(
name|methodDir
argument_list|,
name|E_NOT_DIRECTORY
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|methodDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBinWinUtilsFound ()
specifier|public
name|void
name|testBinWinUtilsFound
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|File
name|bin
init|=
operator|new
name|File
argument_list|(
name|methodDir
argument_list|,
literal|"bin"
argument_list|)
decl_stmt|;
name|File
name|winutils
init|=
operator|new
name|File
argument_list|(
name|bin
argument_list|,
name|WINUTILS_EXE
argument_list|)
decl_stmt|;
name|touch
argument_list|(
name|winutils
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|winutils
operator|.
name|getCanonicalPath
argument_list|()
argument_list|,
name|getQualifiedBinInner
argument_list|(
name|methodDir
argument_list|,
name|WINUTILS_EXE
argument_list|)
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|methodDir
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBinWinUtilsNotAFile ()
specifier|public
name|void
name|testBinWinUtilsNotAFile
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|File
name|bin
init|=
operator|new
name|File
argument_list|(
name|methodDir
argument_list|,
literal|"bin"
argument_list|)
decl_stmt|;
name|File
name|winutils
init|=
operator|new
name|File
argument_list|(
name|bin
argument_list|,
name|WINUTILS_EXE
argument_list|)
decl_stmt|;
name|winutils
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|assertWinutilsResolveFailed
argument_list|(
name|methodDir
argument_list|,
name|E_NOT_EXECUTABLE_FILE
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|methodDir
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * This test takes advantage of the invariant winutils path is valid    * or access to it will raise an exception holds on Linux, and without    * any winutils binary even if HADOOP_HOME points to a real hadoop    * directory, the exception reporting can be validated    */
annotation|@
name|Test
DECL|method|testNoWinutilsOnUnix ()
specifier|public
name|void
name|testNoWinutilsOnUnix
parameter_list|()
throws|throws
name|Throwable
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|WINDOWS
argument_list|)
expr_stmt|;
try|try
block|{
name|getWinUtilsFile
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|assertExContains
argument_list|(
name|ex
argument_list|,
name|E_NOT_A_WINDOWS_SYSTEM
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|getWinUtilsPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|assertExContains
argument_list|(
name|ex
argument_list|,
name|E_NOT_A_WINDOWS_SYSTEM
argument_list|)
expr_stmt|;
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|==
literal|null
operator|||
operator|!
operator|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|FileNotFoundException
operator|)
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
comment|/**    * Touch a file; creating parent dirs on demand.    * @param path path of file    * @return the file created    * @throws IOException on any failure to write    */
DECL|method|touch (File path)
specifier|private
name|File
name|touch
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|path
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|writeByteArrayToFile
argument_list|(
name|path
argument_list|,
operator|new
name|byte
index|[]
block|{}
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
comment|/**    * Assert that an attept to resolve the hadoop home dir failed with    * an expected text in the exception string value.    * @param path input    * @param expectedText expected exception text    * @return the caught exception    * @throws FileNotFoundException any FileNotFoundException that was thrown    * but which did not contain the expected text    */
DECL|method|assertHomeResolveFailed (String path, String expectedText)
specifier|private
name|FileNotFoundException
name|assertHomeResolveFailed
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|expectedText
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|File
name|f
init|=
name|checkHadoopHomeInner
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expected an exception with the text `"
operator|+
name|expectedText
operator|+
literal|"`"
operator|+
literal|" -but got the path "
operator|+
name|f
argument_list|)
expr_stmt|;
comment|// unreachable
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|assertExContains
argument_list|(
name|ex
argument_list|,
name|expectedText
argument_list|)
expr_stmt|;
return|return
name|ex
return|;
block|}
block|}
comment|/**    * Assert that an attept to resolve the {@code bin/winutils.exe} failed with    * an expected text in the exception string value.    * @param hadoopHome hadoop home directory    * @param expectedText expected exception text    * @return the caught exception    * @throws Exception any Exception that was thrown    * but which did not contain the expected text    */
DECL|method|assertWinutilsResolveFailed (File hadoopHome, String expectedText)
specifier|private
name|FileNotFoundException
name|assertWinutilsResolveFailed
parameter_list|(
name|File
name|hadoopHome
parameter_list|,
name|String
name|expectedText
parameter_list|)
throws|throws
name|Exception
block|{
try|try
block|{
name|File
name|f
init|=
name|getQualifiedBinInner
argument_list|(
name|hadoopHome
argument_list|,
name|WINUTILS_EXE
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expected an exception with the text `"
operator|+
name|expectedText
operator|+
literal|"`"
operator|+
literal|" -but got the path "
operator|+
name|f
argument_list|)
expr_stmt|;
comment|// unreachable
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|assertExContains
argument_list|(
name|ex
argument_list|,
name|expectedText
argument_list|)
expr_stmt|;
return|return
name|ex
return|;
block|}
block|}
DECL|method|assertExContains (Exception ex, String expectedText)
specifier|private
name|void
name|assertExContains
parameter_list|(
name|Exception
name|ex
parameter_list|,
name|String
name|expectedText
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|ex
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|expectedText
argument_list|)
condition|)
block|{
throw|throw
name|ex
throw|;
block|}
block|}
annotation|@
name|Test
DECL|method|testBashQuote ()
specifier|public
name|void
name|testBashQuote
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"'foobar'"
argument_list|,
name|Shell
operator|.
name|bashQuote
argument_list|(
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"'foo'\\''bar'"
argument_list|,
name|Shell
operator|.
name|bashQuote
argument_list|(
literal|"foo'bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"''\\''foo'\\''bar'\\'''"
argument_list|,
name|Shell
operator|.
name|bashQuote
argument_list|(
literal|"'foo'bar'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testDestroyAllShellProcesses ()
specifier|public
name|void
name|testDestroyAllShellProcesses
parameter_list|()
throws|throws
name|Throwable
block|{
name|Assume
operator|.
name|assumeFalse
argument_list|(
name|WINDOWS
argument_list|)
expr_stmt|;
name|StringBuffer
name|sleepCommand
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|sleepCommand
operator|.
name|append
argument_list|(
literal|"sleep 200"
argument_list|)
expr_stmt|;
name|String
index|[]
name|shellCmd
init|=
block|{
literal|"bash"
block|,
literal|"-c"
block|,
name|sleepCommand
operator|.
name|toString
argument_list|()
block|}
decl_stmt|;
specifier|final
name|ShellCommandExecutor
name|shexc1
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|shellCmd
argument_list|)
decl_stmt|;
specifier|final
name|ShellCommandExecutor
name|shexc2
init|=
operator|new
name|ShellCommandExecutor
argument_list|(
name|shellCmd
argument_list|)
decl_stmt|;
name|Thread
name|shellThread1
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|shexc1
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//ignore IOException from thread interrupt
block|}
block|}
block|}
decl_stmt|;
name|Thread
name|shellThread2
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|shexc2
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//ignore IOException from thread interrupt
block|}
block|}
block|}
decl_stmt|;
name|shellThread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|shellThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|shexc1
operator|.
name|getProcess
argument_list|()
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
return|return
name|shexc2
operator|.
name|getProcess
argument_list|()
operator|!=
literal|null
return|;
block|}
block|}
argument_list|,
literal|10
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
name|Shell
operator|.
name|destroyAllShellProcesses
argument_list|()
expr_stmt|;
name|shexc1
operator|.
name|getProcess
argument_list|()
operator|.
name|waitFor
argument_list|()
expr_stmt|;
name|shexc2
operator|.
name|getProcess
argument_list|()
operator|.
name|waitFor
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsJavaVersionAtLeast ()
specifier|public
name|void
name|testIsJavaVersionAtLeast
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Shell
operator|.
name|isJavaVersionAtLeast
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIsBashSupported ()
specifier|public
name|void
name|testIsBashSupported
parameter_list|()
throws|throws
name|InterruptedIOException
block|{
name|assumeTrue
argument_list|(
literal|"Bash is not supported"
argument_list|,
name|Shell
operator|.
name|checkIsBashSupported
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

