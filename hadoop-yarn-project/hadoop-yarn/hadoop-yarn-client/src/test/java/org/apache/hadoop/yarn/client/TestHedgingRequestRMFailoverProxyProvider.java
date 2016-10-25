begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
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
name|yarn
operator|.
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|exceptions
operator|.
name|ApplicationNotFoundException
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|MiniYARNCluster
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
name|server
operator|.
name|resourcemanager
operator|.
name|HATestUtil
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
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestHedgingRequestRMFailoverProxyProvider
specifier|public
class|class
name|TestHedgingRequestRMFailoverProxyProvider
block|{
annotation|@
name|Test
DECL|method|testHedgingRequestProxyProvider ()
specifier|public
name|void
name|testHedgingRequestProxyProvider
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MiniYARNCluster
name|cluster
init|=
operator|new
name|MiniYARNCluster
argument_list|(
literal|"testHedgingRequestProxyProvider"
argument_list|,
literal|5
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
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
literal|false
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
literal|"cluster1"
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
literal|"rm1,rm2,rm3,rm4,rm5"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|CLIENT_FAILOVER_PROXY_PROVIDER
argument_list|,
name|RequestHedgingRMFailoverProxyProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS
argument_list|,
literal|2000
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
literal|"rm1"
argument_list|,
literal|10000
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
literal|"rm2"
argument_list|,
literal|20000
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
literal|"rm3"
argument_list|,
literal|30000
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
literal|"rm4"
argument_list|,
literal|40000
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|HATestUtil
operator|.
name|setRpcAddressForRM
argument_list|(
literal|"rm5"
argument_list|,
literal|50000
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_MINICLUSTER_FIXED_PORTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|YarnClient
name|client
init|=
name|YarnClient
operator|.
name|createYarnClient
argument_list|()
decl_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Transition rm5 to active;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|makeRMActive
argument_list|(
name|cluster
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|validateActiveRM
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Client call succeeded at "
operator|+
name|end
argument_list|)
expr_stmt|;
comment|// should return the response fast
name|Assert
operator|.
name|assertTrue
argument_list|(
name|end
operator|-
name|start
operator|<=
literal|10000
argument_list|)
expr_stmt|;
comment|// transition rm5 to standby
name|cluster
operator|.
name|getResourceManager
argument_list|(
literal|4
argument_list|)
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMAdminService
argument_list|()
operator|.
name|transitionToStandby
argument_list|(
operator|new
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
argument_list|(
name|HAServiceProtocol
operator|.
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|)
argument_list|)
expr_stmt|;
name|makeRMActive
argument_list|(
name|cluster
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|validateActiveRM
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|validateActiveRM (YarnClient client)
specifier|private
name|void
name|validateActiveRM
parameter_list|(
name|YarnClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
comment|// first check if exception is thrown correctly;
try|try
block|{
comment|// client will retry until the rm becomes active.
name|client
operator|.
name|getApplicationReport
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|ApplicationNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|// now make a valid call.
try|try
block|{
name|client
operator|.
name|getAllQueues
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|makeRMActive (final MiniYARNCluster cluster, final int index)
specifier|private
name|void
name|makeRMActive
parameter_list|(
specifier|final
name|MiniYARNCluster
name|cluster
parameter_list|,
specifier|final
name|int
name|index
parameter_list|)
block|{
name|Thread
name|t
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transition rm"
operator|+
name|index
operator|+
literal|" to active"
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getResourceManager
argument_list|(
name|index
argument_list|)
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMAdminService
argument_list|()
operator|.
name|transitionToActive
argument_list|(
operator|new
name|HAServiceProtocol
operator|.
name|StateChangeRequestInfo
argument_list|(
name|HAServiceProtocol
operator|.
name|RequestSource
operator|.
name|REQUEST_BY_USER
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

