begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|federation
operator|.
name|router
package|;
end_package

begin_import
import|import static
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
name|federation
operator|.
name|FederationTestUtils
operator|.
name|NAMENODES
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|FederationTestUtils
operator|.
name|NAMESERVICES
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
name|assertEquals
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
name|ArrayList
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|MockResolver
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
name|federation
operator|.
name|MiniRouterDFSCluster
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
name|federation
operator|.
name|MiniRouterDFSCluster
operator|.
name|NamenodeContext
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
name|federation
operator|.
name|resolver
operator|.
name|ActiveNamenodeResolver
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
name|federation
operator|.
name|resolver
operator|.
name|FederationNamenodeContext
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
name|service
operator|.
name|Service
operator|.
name|STATE
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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

begin_comment
comment|/**  * Test the service that heartbeats the state of the namenodes to the State  * Store.  */
end_comment

begin_class
DECL|class|TestRouterNamenodeHeartbeat
specifier|public
class|class
name|TestRouterNamenodeHeartbeat
block|{
DECL|field|cluster
specifier|private
specifier|static
name|MiniRouterDFSCluster
name|cluster
decl_stmt|;
DECL|field|namenodeResolver
specifier|private
specifier|static
name|ActiveNamenodeResolver
name|namenodeResolver
decl_stmt|;
DECL|field|services
specifier|private
specifier|static
name|List
argument_list|<
name|NamenodeHeartbeatService
argument_list|>
name|services
decl_stmt|;
annotation|@
name|Rule
DECL|field|name
specifier|public
name|TestName
name|name
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|globalSetUp ()
specifier|public
specifier|static
name|void
name|globalSetUp
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniRouterDFSCluster
argument_list|(
literal|true
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// Start NNs and DNs and wait until ready
name|cluster
operator|.
name|startCluster
argument_list|()
expr_stmt|;
comment|// Mock locator that records the heartbeats
name|List
argument_list|<
name|String
argument_list|>
name|nss
init|=
name|cluster
operator|.
name|getNameservices
argument_list|()
decl_stmt|;
name|String
name|ns
init|=
name|nss
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|cluster
operator|.
name|generateNamenodeConfiguration
argument_list|(
name|ns
argument_list|)
decl_stmt|;
name|namenodeResolver
operator|=
operator|new
name|MockResolver
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|namenodeResolver
operator|.
name|setRouterId
argument_list|(
literal|"testrouter"
argument_list|)
expr_stmt|;
comment|// Create one heartbeat service per NN
name|services
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|NamenodeContext
name|nn
range|:
name|cluster
operator|.
name|getNamenodes
argument_list|()
control|)
block|{
name|String
name|nsId
init|=
name|nn
operator|.
name|getNameserviceId
argument_list|()
decl_stmt|;
name|String
name|nnId
init|=
name|nn
operator|.
name|getNamenodeId
argument_list|()
decl_stmt|;
name|NamenodeHeartbeatService
name|service
init|=
operator|new
name|NamenodeHeartbeatService
argument_list|(
name|namenodeResolver
argument_list|,
name|nsId
argument_list|,
name|nnId
argument_list|)
decl_stmt|;
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|service
operator|.
name|start
argument_list|()
expr_stmt|;
name|services
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
for|for
control|(
name|NamenodeHeartbeatService
name|service
range|:
name|services
control|)
block|{
name|service
operator|.
name|stop
argument_list|()
expr_stmt|;
name|service
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNamenodeHeartbeatService ()
specifier|public
name|void
name|testNamenodeHeartbeatService
parameter_list|()
throws|throws
name|IOException
block|{
name|MiniRouterDFSCluster
name|testCluster
init|=
operator|new
name|MiniRouterDFSCluster
argument_list|(
literal|true
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Configuration
name|heartbeatConfig
init|=
name|testCluster
operator|.
name|generateNamenodeConfiguration
argument_list|(
name|NAMESERVICES
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|NamenodeHeartbeatService
name|server
init|=
operator|new
name|NamenodeHeartbeatService
argument_list|(
name|namenodeResolver
argument_list|,
name|NAMESERVICES
index|[
literal|0
index|]
argument_list|,
name|NAMENODES
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|server
operator|.
name|init
argument_list|(
name|heartbeatConfig
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|INITED
argument_list|,
name|server
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STARTED
argument_list|,
name|server
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|STATE
operator|.
name|STOPPED
argument_list|,
name|server
operator|.
name|getServiceState
argument_list|()
argument_list|)
expr_stmt|;
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHearbeat ()
specifier|public
name|void
name|testHearbeat
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
comment|// Set NAMENODE1 to active for all nameservices
if|if
condition|(
name|cluster
operator|.
name|isHighAvailability
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|ns
range|:
name|cluster
operator|.
name|getNameservices
argument_list|()
control|)
block|{
name|cluster
operator|.
name|switchToActive
argument_list|(
name|ns
argument_list|,
name|NAMENODES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|switchToStandby
argument_list|(
name|ns
argument_list|,
name|NAMENODES
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Wait for heartbeats to record
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// Verify the locator has matching NN entries for each NS
for|for
control|(
name|String
name|ns
range|:
name|cluster
operator|.
name|getNameservices
argument_list|()
control|)
block|{
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|nns
init|=
name|namenodeResolver
operator|.
name|getNamenodesForNameserviceId
argument_list|(
name|ns
argument_list|)
decl_stmt|;
comment|// Active
name|FederationNamenodeContext
name|active
init|=
name|nns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NAMENODES
index|[
literal|0
index|]
argument_list|,
name|active
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Standby
name|FederationNamenodeContext
name|standby
init|=
name|nns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NAMENODES
index|[
literal|1
index|]
argument_list|,
name|standby
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Switch active NNs in 1/2 nameservices
name|List
argument_list|<
name|String
argument_list|>
name|nss
init|=
name|cluster
operator|.
name|getNameservices
argument_list|()
decl_stmt|;
name|String
name|failoverNS
init|=
name|nss
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|normalNs
init|=
name|nss
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|cluster
operator|.
name|switchToStandby
argument_list|(
name|failoverNS
argument_list|,
name|NAMENODES
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|switchToActive
argument_list|(
name|failoverNS
argument_list|,
name|NAMENODES
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
comment|// Wait for heartbeats to record
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// Verify the locator has recorded the failover for the failover NS
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|failoverNSs
init|=
name|namenodeResolver
operator|.
name|getNamenodesForNameserviceId
argument_list|(
name|failoverNS
argument_list|)
decl_stmt|;
comment|// Active
name|FederationNamenodeContext
name|active
init|=
name|failoverNSs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NAMENODES
index|[
literal|1
index|]
argument_list|,
name|active
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Standby
name|FederationNamenodeContext
name|standby
init|=
name|failoverNSs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NAMENODES
index|[
literal|0
index|]
argument_list|,
name|standby
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify the locator has the same records for the other ns
name|List
argument_list|<
name|?
extends|extends
name|FederationNamenodeContext
argument_list|>
name|normalNss
init|=
name|namenodeResolver
operator|.
name|getNamenodesForNameserviceId
argument_list|(
name|normalNs
argument_list|)
decl_stmt|;
comment|// Active
name|active
operator|=
name|normalNss
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NAMENODES
index|[
literal|0
index|]
argument_list|,
name|active
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// Standby
name|standby
operator|=
name|normalNss
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|NAMENODES
index|[
literal|1
index|]
argument_list|,
name|standby
operator|.
name|getNamenodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

