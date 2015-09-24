begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|Joiner
import|;
end_import

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
name|Optional
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
operator|.
name|DefaultAuditLogger
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
name|log4j
operator|.
name|Level
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
name|Timeout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Inet4Address
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
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
name|*
import|;
end_import

begin_comment
comment|/**  * Test that the HDFS Audit logger respects DFS_NAMENODE_AUDIT_LOG_DEBUG_CMDLIST.   */
end_comment

begin_class
DECL|class|TestAuditLogAtDebug
specifier|public
class|class
name|TestAuditLogAtDebug
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestAuditLogAtDebug
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|DUMMY_COMMAND_1
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_COMMAND_1
init|=
literal|"dummycommand1"
decl_stmt|;
DECL|field|DUMMY_COMMAND_2
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_COMMAND_2
init|=
literal|"dummycommand2"
decl_stmt|;
DECL|method|makeSpyLogger ( Level level, Optional<List<String>> debugCommands)
specifier|private
name|DefaultAuditLogger
name|makeSpyLogger
parameter_list|(
name|Level
name|level
parameter_list|,
name|Optional
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|debugCommands
parameter_list|)
block|{
name|DefaultAuditLogger
name|logger
init|=
operator|new
name|DefaultAuditLogger
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
if|if
condition|(
name|debugCommands
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AUDIT_LOG_DEBUG_CMDLIST
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|debugCommands
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|initialize
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSNamesystem
operator|.
name|auditLog
argument_list|,
name|level
argument_list|)
expr_stmt|;
return|return
name|spy
argument_list|(
name|logger
argument_list|)
return|;
block|}
DECL|method|logDummyCommandToAuditLog (HdfsAuditLogger logger, String command)
specifier|private
name|void
name|logDummyCommandToAuditLog
parameter_list|(
name|HdfsAuditLogger
name|logger
parameter_list|,
name|String
name|command
parameter_list|)
block|{
name|logger
operator|.
name|logAuditEvent
argument_list|(
literal|true
argument_list|,
literal|""
argument_list|,
name|Inet4Address
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|,
name|command
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDebugCommandNotLoggedAtInfo ()
specifier|public
name|void
name|testDebugCommandNotLoggedAtInfo
parameter_list|()
block|{
name|DefaultAuditLogger
name|logger
init|=
name|makeSpyLogger
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DUMMY_COMMAND_1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|logAuditMessage
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDebugCommandLoggedAtDebug ()
specifier|public
name|void
name|testDebugCommandLoggedAtDebug
parameter_list|()
block|{
name|DefaultAuditLogger
name|logger
init|=
name|makeSpyLogger
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DUMMY_COMMAND_1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_1
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|logAuditMessage
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInfoCommandLoggedAtInfo ()
specifier|public
name|void
name|testInfoCommandLoggedAtInfo
parameter_list|()
block|{
name|DefaultAuditLogger
name|logger
init|=
name|makeSpyLogger
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DUMMY_COMMAND_1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|logAuditMessage
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleDebugCommandsNotLoggedAtInfo ()
specifier|public
name|void
name|testMultipleDebugCommandsNotLoggedAtInfo
parameter_list|()
block|{
name|DefaultAuditLogger
name|logger
init|=
name|makeSpyLogger
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DUMMY_COMMAND_1
argument_list|,
name|DUMMY_COMMAND_2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_1
argument_list|)
expr_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|never
argument_list|()
argument_list|)
operator|.
name|logAuditMessage
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMultipleDebugCommandsLoggedAtDebug ()
specifier|public
name|void
name|testMultipleDebugCommandsLoggedAtDebug
parameter_list|()
block|{
name|DefaultAuditLogger
name|logger
init|=
name|makeSpyLogger
argument_list|(
name|Level
operator|.
name|DEBUG
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DUMMY_COMMAND_1
argument_list|,
name|DUMMY_COMMAND_2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_1
argument_list|)
expr_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|logAuditMessage
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEmptyDebugCommands ()
specifier|public
name|void
name|testEmptyDebugCommands
parameter_list|()
block|{
name|DefaultAuditLogger
name|logger
init|=
name|makeSpyLogger
argument_list|(
name|Level
operator|.
name|INFO
argument_list|,
name|Optional
operator|.
expr|<
name|List
argument_list|<
name|String
argument_list|>
operator|>
name|absent
argument_list|()
argument_list|)
decl_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_1
argument_list|)
expr_stmt|;
name|logDummyCommandToAuditLog
argument_list|(
name|logger
argument_list|,
name|DUMMY_COMMAND_2
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|logger
argument_list|,
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|logAuditMessage
argument_list|(
name|anyString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

