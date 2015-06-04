begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
package|;
end_package

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
name|ha
operator|.
name|ClientBaseWithFixes
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
name|ServiceFailedException
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_class
DECL|class|TestRMEmbeddedElector
specifier|public
class|class
name|TestRMEmbeddedElector
extends|extends
name|ClientBaseWithFixes
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
name|TestRMEmbeddedElector
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|RM1_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM1_NODE_ID
init|=
literal|"rm1"
decl_stmt|;
DECL|field|RM1_PORT_BASE
specifier|private
specifier|static
specifier|final
name|int
name|RM1_PORT_BASE
init|=
literal|10000
decl_stmt|;
DECL|field|RM2_NODE_ID
specifier|private
specifier|static
specifier|final
name|String
name|RM2_NODE_ID
init|=
literal|"rm2"
decl_stmt|;
DECL|field|RM2_PORT_BASE
specifier|private
specifier|static
specifier|final
name|int
name|RM2_PORT_BASE
init|=
literal|20000
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|callbackCalled
specifier|private
name|AtomicBoolean
name|callbackCalled
decl_stmt|;
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
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_ENABLED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|AUTO_FAILOVER_EMBEDDED
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_CLUSTER_ID
argument_list|,
literal|"yarn-test-cluster"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_ADDRESS
argument_list|,
name|hostPort
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ZK_TIMEOUT_MS
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_IDS
argument_list|,
name|RM1_NODE_ID
operator|+
literal|","
operator|+
name|RM2_NODE_ID
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HA_ID
argument_list|,
name|RM1_NODE_ID
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
name|RM1_NODE_ID
argument_list|,
name|RM1_PORT_BASE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
name|RM2_NODE_ID
argument_list|,
name|RM2_PORT_BASE
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_SLEEPTIME_BASE_MS
argument_list|,
literal|100L
argument_list|)
expr_stmt|;
name|callbackCalled
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that tries to see if there is a deadlock between    * (a) the thread stopping the RM    * (b) thread processing the ZK event asking RM to transition to active    *    * The test times out if there is a deadlock.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testDeadlockShutdownBecomeActive ()
specifier|public
name|void
name|testDeadlockShutdownBecomeActive
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|MockRM
name|rm
init|=
operator|new
name|MockRMWithElector
argument_list|(
name|conf
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|rm
operator|.
name|start
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for callback"
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|callbackCalled
operator|.
name|get
argument_list|()
condition|)
empty_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping RM"
argument_list|)
expr_stmt|;
name|rm
operator|.
name|stop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopped RM"
argument_list|)
expr_stmt|;
block|}
DECL|class|MockRMWithElector
specifier|private
class|class
name|MockRMWithElector
extends|extends
name|MockRM
block|{
DECL|field|delayMs
specifier|private
name|long
name|delayMs
init|=
literal|0
decl_stmt|;
DECL|method|MockRMWithElector (Configuration conf)
name|MockRMWithElector
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|MockRMWithElector (Configuration conf, long delayMs)
name|MockRMWithElector
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|long
name|delayMs
parameter_list|)
block|{
name|this
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|delayMs
operator|=
name|delayMs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createAdminService ()
specifier|protected
name|AdminService
name|createAdminService
parameter_list|()
block|{
return|return
operator|new
name|AdminService
argument_list|(
name|MockRMWithElector
operator|.
name|this
argument_list|,
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|EmbeddedElectorService
name|createEmbeddedElectorService
parameter_list|()
block|{
return|return
operator|new
name|EmbeddedElectorService
argument_list|(
name|getRMContext
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|becomeActive
parameter_list|()
throws|throws
name|ServiceFailedException
block|{
try|try
block|{
name|callbackCalled
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Callback called. Sleeping now"
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|delayMs
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sleep done"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|becomeActive
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

end_unit

