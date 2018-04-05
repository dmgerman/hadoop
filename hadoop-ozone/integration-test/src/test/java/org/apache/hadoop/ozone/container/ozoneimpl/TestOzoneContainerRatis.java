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
name|MiniOzoneClassicCluster
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
name|hdds
operator|.
name|conf
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
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|hdds
operator|.
name|scm
operator|.
name|XceiverClientSpi
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|apache
operator|.
name|ratis
operator|.
name|util
operator|.
name|CheckedBiConsumer
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
name|util
operator|.
name|CollectionUtils
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

begin_comment
comment|/**  * Tests ozone containers with Apache Ratis.  */
end_comment

begin_class
annotation|@
name|Ignore
argument_list|(
literal|"Disabling Ratis tests for pipeline work."
argument_list|)
DECL|class|TestOzoneContainerRatis
specifier|public
class|class
name|TestOzoneContainerRatis
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
name|TestOzoneContainerRatis
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Set the timeout for every test.    */
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
literal|300000
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
name|TestOzoneContainerRatis
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
DECL|method|runTestOzoneContainerViaDataNodeRatis ( RpcType rpc, int numNodes)
specifier|private
specifier|static
name|void
name|runTestOzoneContainerViaDataNodeRatis
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|int
name|numNodes
parameter_list|)
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
literal|"runTestOzoneContainerViaDataNodeRatis"
argument_list|,
name|rpc
argument_list|,
name|numNodes
argument_list|,
name|TestOzoneContainer
operator|::
name|runTestOzoneContainerViaDataNode
argument_list|)
expr_stmt|;
block|}
DECL|method|runTest ( String testName, RpcType rpc, int numNodes, CheckedBiConsumer<String, XceiverClientSpi, Exception> test)
specifier|private
specifier|static
name|void
name|runTest
parameter_list|(
name|String
name|testName
parameter_list|,
name|RpcType
name|rpc
parameter_list|,
name|int
name|numNodes
parameter_list|,
name|CheckedBiConsumer
argument_list|<
name|String
argument_list|,
name|XceiverClientSpi
argument_list|,
name|Exception
argument_list|>
name|test
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
name|testName
operator|+
literal|"(rpc="
operator|+
name|rpc
operator|+
literal|", numNodes="
operator|+
name|numNodes
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
name|MiniOzoneClassicCluster
name|cluster
init|=
operator|new
name|MiniOzoneClassicCluster
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
name|numNodes
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
name|String
name|containerName
init|=
name|OzoneUtils
operator|.
name|getRequestID
argument_list|()
decl_stmt|;
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
name|Pipeline
name|pipeline
init|=
name|ContainerTestHelper
operator|.
name|createPipeline
argument_list|(
name|containerName
argument_list|,
name|CollectionUtils
operator|.
name|as
argument_list|(
name|datanodes
argument_list|,
name|MiniOzoneClassicCluster
operator|::
name|getDatanodeDetails
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"pipeline="
operator|+
name|pipeline
argument_list|)
expr_stmt|;
comment|// Create Ratis cluster
comment|//      final String ratisId = "ratis1";
comment|//      final PipelineManager manager = RatisManagerImpl.newRatisManager(conf);
comment|//      manager.createPipeline(ratisId, pipeline.getMachines());
comment|//      LOG.info("Created RatisCluster " + ratisId);
comment|//
comment|//      // check Ratis cluster members
comment|//      final List<DatanodeDetails> dns = manager.getMembers(ratisId);
comment|//      Assert.assertEquals(pipeline.getMachines(), dns);
comment|//
comment|//      // run test
comment|//      final XceiverClientSpi client = XceiverClientRatis
comment|// .newXceiverClientRatis(
comment|//          pipeline, conf);
comment|//      test.accept(containerName, client);
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
DECL|method|runTestBothGetandPutSmallFileRatis ( RpcType rpc, int numNodes)
specifier|private
specifier|static
name|void
name|runTestBothGetandPutSmallFileRatis
parameter_list|(
name|RpcType
name|rpc
parameter_list|,
name|int
name|numNodes
parameter_list|)
throws|throws
name|Exception
block|{
name|runTest
argument_list|(
literal|"runTestBothGetandPutSmallFileRatis"
argument_list|,
name|rpc
argument_list|,
name|numNodes
argument_list|,
name|TestOzoneContainer
operator|::
name|runTestBothGetandPutSmallFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOzoneContainerViaDataNodeRatisGrpc ()
specifier|public
name|void
name|testOzoneContainerViaDataNodeRatisGrpc
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestOzoneContainerViaDataNodeRatis
argument_list|(
name|SupportedRpcType
operator|.
name|GRPC
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|runTestOzoneContainerViaDataNodeRatis
argument_list|(
name|SupportedRpcType
operator|.
name|GRPC
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOzoneContainerViaDataNodeRatisNetty ()
specifier|public
name|void
name|testOzoneContainerViaDataNodeRatisNetty
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestOzoneContainerViaDataNodeRatis
argument_list|(
name|SupportedRpcType
operator|.
name|NETTY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|runTestOzoneContainerViaDataNodeRatis
argument_list|(
name|SupportedRpcType
operator|.
name|NETTY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBothGetandPutSmallFileRatisNetty ()
specifier|public
name|void
name|testBothGetandPutSmallFileRatisNetty
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestBothGetandPutSmallFileRatis
argument_list|(
name|SupportedRpcType
operator|.
name|NETTY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|runTestBothGetandPutSmallFileRatis
argument_list|(
name|SupportedRpcType
operator|.
name|NETTY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBothGetandPutSmallFileRatisGrpc ()
specifier|public
name|void
name|testBothGetandPutSmallFileRatisGrpc
parameter_list|()
throws|throws
name|Exception
block|{
name|runTestBothGetandPutSmallFileRatis
argument_list|(
name|SupportedRpcType
operator|.
name|GRPC
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|runTestBothGetandPutSmallFileRatis
argument_list|(
name|SupportedRpcType
operator|.
name|GRPC
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

