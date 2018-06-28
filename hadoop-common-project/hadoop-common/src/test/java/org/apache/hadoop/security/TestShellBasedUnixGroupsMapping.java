begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
package|;
end_package

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
name|List
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
name|CommonConfigurationKeys
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ReflectionUtils
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
name|util
operator|.
name|Shell
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
name|util
operator|.
name|Shell
operator|.
name|ExitCodeException
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
name|util
operator|.
name|Shell
operator|.
name|ShellCommandExecutor
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doNothing
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doThrow
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|TestShellBasedUnixGroupsMapping
specifier|public
class|class
name|TestShellBasedUnixGroupsMapping
block|{
DECL|field|TESTLOG
specifier|private
specifier|static
specifier|final
name|Logger
name|TESTLOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestShellBasedUnixGroupsMapping
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|shellMappingLog
specifier|private
specifier|final
name|GenericTestUtils
operator|.
name|LogCapturer
name|shellMappingLog
init|=
name|GenericTestUtils
operator|.
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|ShellBasedUnixGroupsMapping
operator|.
name|LOG
argument_list|)
decl_stmt|;
DECL|class|TestGroupUserNotExist
specifier|private
class|class
name|TestGroupUserNotExist
extends|extends
name|ShellBasedUnixGroupsMapping
block|{
comment|/**      * Create a ShellCommandExecutor object which returns exit code 1,      * emulating the case that the user does not exist.      *      * @param userName not used      * @return a mock ShellCommandExecutor object      */
annotation|@
name|Override
DECL|method|createGroupExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|doThrow
argument_list|(
operator|new
name|ExitCodeException
argument_list|(
literal|1
argument_list|,
literal|"id: foobarusernotexist: No such user"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|TESTLOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|executor
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetGroupsNonexistentUser ()
specifier|public
name|void
name|testGetGroupsNonexistentUser
parameter_list|()
throws|throws
name|Exception
block|{
name|TestGroupUserNotExist
name|mapping
init|=
operator|new
name|TestGroupUserNotExist
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|mapping
operator|.
name|getGroups
argument_list|(
literal|"foobarusernotexist"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|class|TestGroupNotResolvable
specifier|private
class|class
name|TestGroupNotResolvable
extends|extends
name|ShellBasedUnixGroupsMapping
block|{
comment|/**      * Create a ShellCommandExecutor object which returns partially resolved      * group names for a user.      *      * @param userName not used      * @return a mock ShellCommandExecutor object      */
annotation|@
name|Override
DECL|method|createGroupExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
comment|// There is both a group name 9999 and a group ID 9999.
comment|// This is treated as unresolvable group.
name|doThrow
argument_list|(
operator|new
name|ExitCodeException
argument_list|(
literal|1
argument_list|,
literal|"cannot find name for group ID 9999"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"9999\n9999 abc def"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|TESTLOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|executor
return|;
block|}
annotation|@
name|Override
DECL|method|createGroupIDExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupIDExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"9999\n9999 1 2"
argument_list|)
expr_stmt|;
return|return
name|executor
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetGroupsNotResolvable ()
specifier|public
name|void
name|testGetGroupsNotResolvable
parameter_list|()
throws|throws
name|Exception
block|{
name|TestGroupNotResolvable
name|mapping
init|=
operator|new
name|TestGroupNotResolvable
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|mapping
operator|.
name|getGroups
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"def"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestNumericGroupResolvable
specifier|private
class|class
name|TestNumericGroupResolvable
extends|extends
name|ShellBasedUnixGroupsMapping
block|{
comment|/**      * Create a ShellCommandExecutor object which returns numerical group      * names of a user.      *      * @param userName not used      * @return a mock ShellCommandExecutor object      */
annotation|@
name|Override
DECL|method|createGroupExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
comment|// There is a numerical group 23, but no group name 23.
comment|// Thus 23 is treated as a resolvable group name.
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"23\n23 groupname zzz"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|TESTLOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|executor
return|;
block|}
annotation|@
name|Override
DECL|method|createGroupIDExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupIDExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"111\n111 112 113"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|TESTLOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|executor
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetNumericGroupsResolvable ()
specifier|public
name|void
name|testGetNumericGroupsResolvable
parameter_list|()
throws|throws
name|Exception
block|{
name|TestNumericGroupResolvable
name|mapping
init|=
operator|new
name|TestNumericGroupResolvable
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|mapping
operator|.
name|getGroups
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"23"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"groupname"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"zzz"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getTimeoutInterval (String timeout)
specifier|public
name|long
name|getTimeoutInterval
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
literal|"foobarnonexistinguser"
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_SHELL_COMMAND_TIMEOUT_KEY
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
name|TestDelayedGroupCommand
name|mapping
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|TestDelayedGroupCommand
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ShellCommandExecutor
name|executor
init|=
name|mapping
operator|.
name|createGroupExecutor
argument_list|(
name|userName
argument_list|)
decl_stmt|;
return|return
name|executor
operator|.
name|getTimeoutInterval
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testShellTimeOutConf ()
specifier|public
name|void
name|testShellTimeOutConf
parameter_list|()
block|{
comment|// Test a 1 second max-runtime timeout
name|assertEquals
argument_list|(
literal|"Expected the group names executor to carry the configured timeout"
argument_list|,
literal|1000L
argument_list|,
name|getTimeoutInterval
argument_list|(
literal|"1s"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test a 1 minute max-runtime timeout
name|assertEquals
argument_list|(
literal|"Expected the group names executor to carry the configured timeout"
argument_list|,
literal|60000L
argument_list|,
name|getTimeoutInterval
argument_list|(
literal|"1m"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Test a 1 millisecond max-runtime timeout
name|assertEquals
argument_list|(
literal|"Expected the group names executor to carry the configured timeout"
argument_list|,
literal|1L
argument_list|,
name|getTimeoutInterval
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestGroupResolvable
specifier|private
class|class
name|TestGroupResolvable
extends|extends
name|ShellBasedUnixGroupsMapping
block|{
comment|/**      * Create a ShellCommandExecutor object to return the group names of a user.      *      * @param userName not used      * @return a mock ShellCommandExecutor object      */
annotation|@
name|Override
DECL|method|createGroupExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"abc\ndef abc hij"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|TESTLOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|executor
return|;
block|}
annotation|@
name|Override
DECL|method|createGroupIDExecutor (String userName)
specifier|protected
name|ShellCommandExecutor
name|createGroupIDExecutor
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|ShellCommandExecutor
name|executor
init|=
name|mock
argument_list|(
name|ShellCommandExecutor
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|doNothing
argument_list|()
operator|.
name|when
argument_list|(
name|executor
argument_list|)
operator|.
name|execute
argument_list|()
expr_stmt|;
name|when
argument_list|(
name|executor
operator|.
name|getOutput
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"1\n1 2 3"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|TESTLOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|executor
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testGetGroupsResolvable ()
specifier|public
name|void
name|testGetGroupsResolvable
parameter_list|()
throws|throws
name|Exception
block|{
name|TestGroupResolvable
name|mapping
init|=
operator|new
name|TestGroupResolvable
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|groups
init|=
name|mapping
operator|.
name|getGroups
argument_list|(
literal|"user"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"def"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|groups
operator|.
name|contains
argument_list|(
literal|"hij"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestDelayedGroupCommand
specifier|private
specifier|static
class|class
name|TestDelayedGroupCommand
extends|extends
name|ShellBasedUnixGroupsMapping
block|{
DECL|field|timeoutSecs
specifier|private
name|Long
name|timeoutSecs
init|=
literal|1L
decl_stmt|;
DECL|method|TestDelayedGroupCommand ()
name|TestDelayedGroupCommand
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGroupsForUserCommand (String userName)
specifier|protected
name|String
index|[]
name|getGroupsForUserCommand
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
comment|// Sleeps 2 seconds when executed and writes no output
if|if
condition|(
name|Shell
operator|.
name|WINDOWS
condition|)
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"timeout"
block|,
name|timeoutSecs
operator|.
name|toString
argument_list|()
block|}
return|;
block|}
return|return
operator|new
name|String
index|[]
block|{
literal|"sleep"
block|,
name|timeoutSecs
operator|.
name|toString
argument_list|()
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getGroupsIDForUserCommand (String userName)
specifier|protected
name|String
index|[]
name|getGroupsIDForUserCommand
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
return|return
name|getGroupsForUserCommand
argument_list|(
name|userName
argument_list|)
return|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|4000
argument_list|)
DECL|method|testFiniteGroupResolutionTime ()
specifier|public
name|void
name|testFiniteGroupResolutionTime
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|String
name|userName
init|=
literal|"foobarnonexistinguser"
decl_stmt|;
name|String
name|commandTimeoutMessage
init|=
literal|"ran longer than the configured timeout limit"
decl_stmt|;
name|long
name|testTimeout
init|=
literal|500L
decl_stmt|;
comment|// Test a 1 second max-runtime timeout
name|conf
operator|.
name|setLong
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_SHELL_COMMAND_TIMEOUT_KEY
argument_list|,
name|testTimeout
argument_list|)
expr_stmt|;
name|TestDelayedGroupCommand
name|mapping
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|TestDelayedGroupCommand
operator|.
name|class
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ShellCommandExecutor
name|executor
init|=
name|mapping
operator|.
name|createGroupExecutor
argument_list|(
name|userName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected the group names executor to carry the configured timeout"
argument_list|,
name|testTimeout
argument_list|,
name|executor
operator|.
name|getTimeoutInterval
argument_list|()
argument_list|)
expr_stmt|;
name|executor
operator|=
name|mapping
operator|.
name|createGroupIDExecutor
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected the group ID executor to carry the configured timeout"
argument_list|,
name|testTimeout
argument_list|,
name|executor
operator|.
name|getTimeoutInterval
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected no groups to be returned given a shell command timeout"
argument_list|,
literal|0
argument_list|,
name|mapping
operator|.
name|getGroups
argument_list|(
name|userName
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected the logs to carry "
operator|+
literal|"a message about command timeout but was: "
operator|+
name|shellMappingLog
operator|.
name|getOutput
argument_list|()
argument_list|,
name|shellMappingLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|commandTimeoutMessage
argument_list|)
argument_list|)
expr_stmt|;
name|shellMappingLog
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
comment|// Test also the parent Groups framework for expected behaviour
name|conf
operator|.
name|setClass
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_MAPPING
argument_list|,
name|TestDelayedGroupCommand
operator|.
name|class
argument_list|,
name|GroupMappingServiceProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|Groups
name|groups
init|=
operator|new
name|Groups
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|groups
operator|.
name|getGroups
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The groups framework call should "
operator|+
literal|"have failed with a command timeout"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"Expected the logs to carry "
operator|+
literal|"a message about command timeout but was: "
operator|+
name|shellMappingLog
operator|.
name|getOutput
argument_list|()
argument_list|,
name|shellMappingLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|commandTimeoutMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shellMappingLog
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
comment|// Test the no-timeout (default) configuration
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|long
name|defaultTimeout
init|=
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_GROUP_SHELL_COMMAND_TIMEOUT_DEFAULT
decl_stmt|;
name|mapping
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|TestDelayedGroupCommand
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|executor
operator|=
name|mapping
operator|.
name|createGroupExecutor
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected the group names executor to carry the default timeout"
argument_list|,
name|defaultTimeout
argument_list|,
name|executor
operator|.
name|getTimeoutInterval
argument_list|()
argument_list|)
expr_stmt|;
name|executor
operator|=
name|mapping
operator|.
name|createGroupIDExecutor
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected the group ID executor to carry the default timeout"
argument_list|,
name|defaultTimeout
argument_list|,
name|executor
operator|.
name|getTimeoutInterval
argument_list|()
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|getGroups
argument_list|(
name|userName
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Didn't expect a timeout of command in execution but logs carry it: "
operator|+
name|shellMappingLog
operator|.
name|getOutput
argument_list|()
argument_list|,
name|shellMappingLog
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|commandTimeoutMessage
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

