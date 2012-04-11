begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

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
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|PrintStream
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
name|DFSUtil
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
name|ha
operator|.
name|HAServiceProtocol
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|RequestSource
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
name|ha
operator|.
name|HAServiceStatus
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
name|ha
operator|.
name|HAServiceTarget
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
name|ha
operator|.
name|HealthCheckFailedException
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
name|AccessControlException
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
name|MockitoUtil
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|Charsets
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
name|Joiner
import|;
end_import

begin_class
DECL|class|TestDFSHAAdmin
specifier|public
class|class
name|TestDFSHAAdmin
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDFSHAAdmin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tool
specifier|private
name|DFSHAAdmin
name|tool
decl_stmt|;
DECL|field|errOutBytes
specifier|private
name|ByteArrayOutputStream
name|errOutBytes
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|errOutput
specifier|private
name|String
name|errOutput
decl_stmt|;
DECL|field|mockProtocol
specifier|private
name|HAServiceProtocol
name|mockProtocol
decl_stmt|;
DECL|field|NSID
specifier|private
specifier|static
specifier|final
name|String
name|NSID
init|=
literal|"ns1"
decl_stmt|;
DECL|field|STANDBY_READY_RESULT
specifier|private
specifier|static
specifier|final
name|HAServiceStatus
name|STANDBY_READY_RESULT
init|=
operator|new
name|HAServiceStatus
argument_list|(
name|HAServiceState
operator|.
name|STANDBY
argument_list|)
operator|.
name|setReadyToBecomeActive
argument_list|()
decl_stmt|;
DECL|field|reqInfoCaptor
specifier|private
name|ArgumentCaptor
argument_list|<
name|StateChangeRequestInfo
argument_list|>
name|reqInfoCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|StateChangeRequestInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|HOST_A
specifier|private
specifier|static
name|String
name|HOST_A
init|=
literal|"1.2.3.1"
decl_stmt|;
DECL|field|HOST_B
specifier|private
specifier|static
name|String
name|HOST_B
init|=
literal|"1.2.3.2"
decl_stmt|;
DECL|method|getHAConf ()
specifier|private
name|HdfsConfiguration
name|getHAConf
parameter_list|()
block|{
name|HdfsConfiguration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
name|NSID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_FEDERATION_NAMESERVICE_ID
argument_list|,
name|NSID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
argument_list|,
name|NSID
argument_list|)
argument_list|,
literal|"nn1,nn2"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODE_ID_KEY
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|NSID
argument_list|,
literal|"nn1"
argument_list|)
argument_list|,
name|HOST_A
operator|+
literal|":12345"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|NSID
argument_list|,
literal|"nn2"
argument_list|)
argument_list|,
name|HOST_B
operator|+
literal|":12345"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
name|mockProtocol
operator|=
name|MockitoUtil
operator|.
name|mockProtocol
argument_list|(
name|HAServiceProtocol
operator|.
name|class
argument_list|)
expr_stmt|;
name|tool
operator|=
operator|new
name|DFSHAAdmin
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|HAServiceTarget
name|resolveTarget
parameter_list|(
name|String
name|nnId
parameter_list|)
block|{
name|HAServiceTarget
name|target
init|=
name|super
operator|.
name|resolveTarget
argument_list|(
name|nnId
argument_list|)
decl_stmt|;
name|HAServiceTarget
name|spy
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|target
argument_list|)
decl_stmt|;
comment|// OVerride the target to return our mock protocol
try|try
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|when
argument_list|(
name|spy
argument_list|)
operator|.
name|getProxy
argument_list|(
name|Mockito
operator|.
expr|<
name|Configuration
operator|>
name|any
argument_list|()
argument_list|,
name|Mockito
operator|.
name|anyInt
argument_list|()
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
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
comment|// mock setup doesn't really throw
block|}
return|return
name|spy
return|;
block|}
block|}
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|getHAConf
argument_list|()
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setErrOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|errOutBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertOutputContains (String string)
specifier|private
name|void
name|assertOutputContains
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
operator|!
name|errOutput
operator|.
name|contains
argument_list|(
name|string
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Expected output to contain '"
operator|+
name|string
operator|+
literal|"' but was:\n"
operator|+
name|errOutput
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNameserviceOption ()
specifier|public
name|void
name|testNameserviceOption
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-ns"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Missing nameservice ID"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-ns"
argument_list|,
literal|"ns1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Missing command"
argument_list|)
expr_stmt|;
comment|// "ns1" isn't defined but we check this lazily and help doesn't use the ns
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-ns"
argument_list|,
literal|"ns1"
argument_list|,
literal|"-help"
argument_list|,
literal|"transitionToActive"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Transitions the service into Active"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNamenodeResolution ()
specifier|public
name|void
name|testNamenodeResolution
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"undefined"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Unable to determine service address for namenode 'undefined'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHelp ()
specifier|public
name|void
name|testHelp
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-help"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-help"
argument_list|,
literal|"transitionToActive"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Transitions the service into Active"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransitionToActive ()
specifier|public
name|void
name|testTransitionToActive
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|reqInfoCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|,
name|reqInfoCaptor
operator|.
name|getValue
argument_list|()
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, if automatic HA is enabled, none of the mutative operations    * will succeed, unless the -forcemanual flag is specified.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testMutativeOperationsWithAutoHaEnabled ()
specifier|public
name|void
name|testMutativeOperationsWithAutoHaEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
comment|// Turn on auto-HA in the config
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_AUTO_FAILOVER_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Should fail without the forcemanual flag
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|errOutput
operator|.
name|contains
argument_list|(
literal|"Refusing to manually manage"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|errOutput
operator|.
name|contains
argument_list|(
literal|"Refusing to manually manage"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|errOutput
operator|.
name|contains
argument_list|(
literal|"Refusing to manually manage"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|anyReqInfo
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|anyReqInfo
argument_list|()
argument_list|)
expr_stmt|;
comment|// Force flag should bypass the check and change the request source
comment|// for the RPC
name|setupConfirmationOnSystemIn
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToActive"
argument_list|,
literal|"-forcemanual"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|setupConfirmationOnSystemIn
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"-forcemanual"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|setupConfirmationOnSystemIn
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"-forcemanual"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|transitionToActive
argument_list|(
name|reqInfoCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|,
name|Mockito
operator|.
name|times
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|reqInfoCaptor
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
comment|// All of the RPCs should have had the "force" source
for|for
control|(
name|StateChangeRequestInfo
name|ri
range|:
name|reqInfoCaptor
operator|.
name|getAllValues
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|RequestSource
operator|.
name|REQUEST_BY_USER_FORCED
argument_list|,
name|ri
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Setup System.in with a stream that feeds a "yes" answer on the    * next prompt.    */
DECL|method|setupConfirmationOnSystemIn ()
specifier|private
specifier|static
name|void
name|setupConfirmationOnSystemIn
parameter_list|()
block|{
comment|// Answer "yes" to the prompt about transition to active
name|System
operator|.
name|setIn
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
literal|"yes\n"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that, even if automatic HA is enabled, the monitoring operations    * still function correctly.    */
annotation|@
name|Test
DECL|method|testMonitoringOperationsWithAutoHaEnabled ()
specifier|public
name|void
name|testMonitoringOperationsWithAutoHaEnabled
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
comment|// Turn on auto-HA
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_AUTO_FAILOVER_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-checkHealth"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransitionToStandby ()
specifier|public
name|void
name|testTransitionToStandby
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-transitionToStandby"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|transitionToStandby
argument_list|(
name|anyReqInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithNoFencerConfigured ()
specifier|public
name|void
name|testFailoverWithNoFencerConfigured
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithFencerConfigured ()
specifier|public
name|void
name|testFailoverWithFencerConfigured
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithFencerAndNameservice ()
specifier|public
name|void
name|testFailoverWithFencerAndNameservice
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-ns"
argument_list|,
literal|"ns1"
argument_list|,
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithFencerConfiguredAndForce ()
specifier|public
name|void
name|testFailoverWithFencerConfiguredAndForce
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithForceActive ()
specifier|public
name|void
name|testFailoverWithForceActive
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forceactive"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithInvalidFenceArg ()
specifier|public
name|void
name|testFailoverWithInvalidFenceArg
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"notforcefence"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithFenceButNoFencer ()
specifier|public
name|void
name|testFailoverWithFenceButNoFencer
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFailoverWithFenceAndBadFencer ()
specifier|public
name|void
name|testFailoverWithFenceAndBadFencer
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"foobar!"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testForceFenceOptionListedBeforeArgs ()
specifier|public
name|void
name|testForceFenceOptionListedBeforeArgs
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"--forcefence"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetServiceStatus ()
specifier|public
name|void
name|testGetServiceStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-getServiceState"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCheckHealth ()
specifier|public
name|void
name|testCheckHealth
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-checkHealth"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|doThrow
argument_list|(
operator|new
name|HealthCheckFailedException
argument_list|(
literal|"fake health check failure"
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|monitorHealth
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-checkHealth"
argument_list|,
literal|"nn1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertOutputContains
argument_list|(
literal|"Health check failed: fake health check failure"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the fencing configuration can be overridden per-nameservice    * or per-namenode    */
annotation|@
name|Test
DECL|method|testFencingConfigPerNameNode ()
specifier|public
name|void
name|testFencingConfigPerNameNode
parameter_list|()
throws|throws
name|Exception
block|{
name|Mockito
operator|.
name|doReturn
argument_list|(
name|STANDBY_READY_RESULT
argument_list|)
operator|.
name|when
argument_list|(
name|mockProtocol
argument_list|)
operator|.
name|getServiceStatus
argument_list|()
expr_stmt|;
specifier|final
name|String
name|nsSpecificKey
init|=
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
operator|+
literal|"."
operator|+
name|NSID
decl_stmt|;
specifier|final
name|String
name|nnSpecificKey
init|=
name|nsSpecificKey
operator|+
literal|".nn1"
decl_stmt|;
name|HdfsConfiguration
name|conf
init|=
name|getHAConf
argument_list|()
decl_stmt|;
comment|// Set the default fencer to succeed
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_FENCE_METHODS_KEY
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the NN-specific fencer to fail. Should fail to fence.
name|conf
operator|.
name|set
argument_list|(
name|nnSpecificKey
argument_list|,
literal|"shell(false)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|nnSpecificKey
argument_list|)
expr_stmt|;
comment|// Set an NS-specific fencer to fail. Should fail.
name|conf
operator|.
name|set
argument_list|(
name|nsSpecificKey
argument_list|,
literal|"shell(false)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Set the NS-specific fencer to succeed. Should succeed
name|conf
operator|.
name|set
argument_list|(
name|nsSpecificKey
argument_list|,
literal|"shell(true)"
argument_list|)
expr_stmt|;
name|tool
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|runTool
argument_list|(
literal|"-failover"
argument_list|,
literal|"nn1"
argument_list|,
literal|"nn2"
argument_list|,
literal|"--forcefence"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runTool (String .... args)
specifier|private
name|Object
name|runTool
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|errOutBytes
operator|.
name|reset
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Running: DFSHAAdmin "
operator|+
name|Joiner
operator|.
name|on
argument_list|(
literal|" "
argument_list|)
operator|.
name|join
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|ret
init|=
name|tool
operator|.
name|run
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|errOutput
operator|=
operator|new
name|String
argument_list|(
name|errOutBytes
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Output:\n"
operator|+
name|errOutput
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|anyReqInfo ()
specifier|private
name|StateChangeRequestInfo
name|anyReqInfo
parameter_list|()
block|{
return|return
name|Mockito
operator|.
expr|<
name|StateChangeRequestInfo
operator|>
name|any
argument_list|()
return|;
block|}
block|}
end_class

end_unit

