begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.ozoneimpl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|ozoneimpl
package|;
end_package

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
name|protocol
operator|.
name|DatanodeID
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
name|datanode
operator|.
name|DataNode
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
name|ozone
operator|.
name|MiniOzoneCluster
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
name|ozone
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|RatisTestHelper
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
name|ozone
operator|.
name|container
operator|.
name|ContainerTestHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|RpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|rpc
operator|.
name|SupportedRpcType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
name|concurrent
operator|.
name|ThreadLocalRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Tests ozone containers with Apache Ratis.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Disabling Ratis tests for pipeline work."
argument_list|)
DECL|class|TestRatisManager
specifier|public
class|class
name|TestRatisManager
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestRatisManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|newOzoneConfiguration ()
specifier|static
name|OzoneConfiguration
name|newOzoneConfiguration
parameter_list|()
block|{
specifier|final
name|OzoneConfiguration
name|conf
init|=
operator|new
name|OzoneConfiguration
argument_list|()
decl_stmt|;
name|ContainerTestHelper
operator|.
name|setOzoneLocalStorageRoot
argument_list|(
name|TestRatisManager
operator|.
name|class
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/** Set the timeout for every test. */
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
literal|200_000
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testTestRatisManagerGrpc ()
specifier|public
name|void
name|testTestRatisManagerGrpc
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestRatisManager
argument_list|(
name|SupportedRpcType
operator|.
name|GRPC
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTestRatisManagerNetty ()
specifier|public
name|void
name|testTestRatisManagerNetty
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestRatisManager
argument_list|(
name|SupportedRpcType
operator|.
name|NETTY
argument_list|)
expr_stmt|;
block|}
DECL|method|runTestRatisManager (RpcType rpc)
specifier|private
specifier|static
name|void
name|runTestRatisManager
parameter_list|(
name|RpcType
name|rpc
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"runTestRatisManager, rpc="
operator|+
name|rpc
argument_list|)
expr_stmt|;
comment|// create Ozone clusters
specifier|final
name|OzoneConfiguration
name|conf
init|=
name|newOzoneConfiguration
argument_list|()
decl_stmt|;
name|RatisTestHelper
operator|.
name|initRatisConf
argument_list|(
name|rpc
argument_list|,
name|conf
argument_list|)
expr_stmt|;
specifier|final
name|MiniOzoneCluster
name|cluster
init|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_LOCAL
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|5
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|cluster
operator|.
name|waitOzoneReady
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|DataNode
argument_list|>
name|datanodes
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|allIds
init|=
name|datanodes
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|DataNode
operator|::
name|getDatanodeId
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
comment|//final RatisManager manager = RatisManager.newRatisManager(conf);
specifier|final
name|int
index|[]
name|idIndex
init|=
block|{
literal|3
block|,
literal|4
block|,
literal|5
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|idIndex
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|previous
init|=
name|i
operator|==
literal|0
condition|?
literal|0
else|:
name|idIndex
index|[
name|i
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|subIds
init|=
name|allIds
operator|.
name|subList
argument_list|(
name|previous
argument_list|,
name|idIndex
index|[
name|i
index|]
argument_list|)
decl_stmt|;
comment|// Create Ratis cluster
specifier|final
name|String
name|ratisId
init|=
literal|"ratis"
operator|+
name|i
decl_stmt|;
comment|//manager.createRatisCluster(ratisId, subIds);
name|LOG
operator|.
name|info
argument_list|(
literal|"Created RatisCluster "
operator|+
name|ratisId
argument_list|)
expr_stmt|;
comment|// check Ratis cluster members
comment|//final List<DatanodeID> dns = manager.getMembers(ratisId);
comment|//Assert.assertEquals(subIds, dns);
block|}
comment|// randomly close two of the clusters
specifier|final
name|int
name|chosen
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|(
name|idIndex
operator|.
name|length
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"chosen = "
operator|+
name|chosen
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
name|idIndex
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|!=
name|chosen
condition|)
block|{
specifier|final
name|String
name|ratisId
init|=
literal|"ratis"
operator|+
name|i
decl_stmt|;
comment|//manager.closeRatisCluster(ratisId);
block|}
block|}
comment|// update datanodes
specifier|final
name|String
name|ratisId
init|=
literal|"ratis"
operator|+
name|chosen
decl_stmt|;
comment|//manager.updatePipeline(ratisId, allIds);
comment|// check Ratis cluster members
comment|//final List<DatanodeID> dns = manager.getMembers(ratisId);
comment|//Assert.assertEquals(allIds, dns);
block|}
finally|finally
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

