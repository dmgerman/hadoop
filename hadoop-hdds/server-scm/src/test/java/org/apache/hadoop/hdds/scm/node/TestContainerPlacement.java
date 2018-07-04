begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
package|package
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
name|node
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
name|io
operator|.
name|IOUtils
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
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
name|TestUtils
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
name|XceiverClientManager
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
name|ContainerMapping
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
name|ContainerWithPipeline
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
name|placement
operator|.
name|algorithms
operator|.
name|ContainerPlacementPolicy
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
name|placement
operator|.
name|algorithms
operator|.
name|SCMContainerPlacementCapacity
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
name|hdds
operator|.
name|protocol
operator|.
name|DatanodeDetails
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|StorageReportProto
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
name|OzoneConfigKeys
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
name|test
operator|.
name|PathUtils
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
name|ExpectedException
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
name|java
operator|.
name|util
operator|.
name|UUID
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
name|TimeoutException
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_MB
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|NodeState
operator|.
name|HEALTHY
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test for different container placement policy.  */
end_comment

begin_class
DECL|class|TestContainerPlacement
specifier|public
class|class
name|TestContainerPlacement
block|{
annotation|@
name|Rule
DECL|field|thrown
specifier|public
name|ExpectedException
name|thrown
init|=
name|ExpectedException
operator|.
name|none
argument_list|()
decl_stmt|;
DECL|field|xceiverClientManager
specifier|private
specifier|static
name|XceiverClientManager
name|xceiverClientManager
init|=
operator|new
name|XceiverClientManager
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Returns a new copy of Configuration.    *    * @return Config    */
DECL|method|getConf ()
name|OzoneConfiguration
name|getConf
parameter_list|()
block|{
return|return
operator|new
name|OzoneConfiguration
argument_list|()
return|;
block|}
comment|/**    * Creates a NodeManager.    *    * @param config - Config for the node manager.    * @return SCNNodeManager    * @throws IOException    */
DECL|method|createNodeManager (OzoneConfiguration config)
name|SCMNodeManager
name|createNodeManager
parameter_list|(
name|OzoneConfiguration
name|config
parameter_list|)
throws|throws
name|IOException
block|{
name|SCMNodeManager
name|nodeManager
init|=
operator|new
name|SCMNodeManager
argument_list|(
name|config
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
literal|"Node manager should be in chill mode"
argument_list|,
name|nodeManager
operator|.
name|isOutOfChillMode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|nodeManager
return|;
block|}
DECL|method|createContainerManager (Configuration config, NodeManager scmNodeManager)
name|ContainerMapping
name|createContainerManager
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|NodeManager
name|scmNodeManager
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|cacheSize
init|=
name|config
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_DB_CACHE_SIZE_MB
argument_list|,
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|ContainerMapping
argument_list|(
name|config
argument_list|,
name|scmNodeManager
argument_list|,
name|cacheSize
argument_list|)
return|;
block|}
comment|/**    * Test capacity based container placement policy with node reports.    *    * @throws IOException    * @throws InterruptedException    * @throws TimeoutException    */
annotation|@
name|Test
annotation|@
name|Ignore
DECL|method|testContainerPlacementCapacity ()
specifier|public
name|void
name|testContainerPlacementCapacity
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|TimeoutException
block|{
name|OzoneConfiguration
name|conf
init|=
name|getConf
argument_list|()
decl_stmt|;
specifier|final
name|int
name|nodeCount
init|=
literal|4
decl_stmt|;
specifier|final
name|long
name|capacity
init|=
literal|10L
operator|*
name|OzoneConsts
operator|.
name|GB
decl_stmt|;
specifier|final
name|long
name|used
init|=
literal|2L
operator|*
name|OzoneConsts
operator|.
name|GB
decl_stmt|;
specifier|final
name|long
name|remaining
init|=
name|capacity
operator|-
name|used
decl_stmt|;
specifier|final
name|File
name|testDir
init|=
name|PathUtils
operator|.
name|getTestDir
argument_list|(
name|TestContainerPlacement
operator|.
name|class
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|,
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
argument_list|,
name|SCMContainerPlacementCapacity
operator|.
name|class
argument_list|,
name|ContainerPlacementPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|SCMNodeManager
name|nodeManager
init|=
name|createNodeManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ContainerMapping
name|containerManager
init|=
name|createContainerManager
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|datanodes
init|=
name|TestUtils
operator|.
name|getListOfRegisteredDatanodeDetails
argument_list|(
name|nodeManager
argument_list|,
name|nodeCount
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|DatanodeDetails
name|datanodeDetails
range|:
name|datanodes
control|)
block|{
name|String
name|id
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|testDir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"/"
operator|+
name|id
decl_stmt|;
name|List
argument_list|<
name|StorageReportProto
argument_list|>
name|reports
init|=
name|TestUtils
operator|.
name|createStorageReport
argument_list|(
name|capacity
argument_list|,
name|used
argument_list|,
name|remaining
argument_list|,
name|path
argument_list|,
literal|null
argument_list|,
name|id
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|nodeManager
operator|.
name|processHeartbeat
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
comment|//TODO: wait for heartbeat to be processed
name|Thread
operator|.
name|sleep
argument_list|(
literal|4
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|nodeCount
argument_list|,
name|nodeManager
operator|.
name|getNodeCount
argument_list|(
name|HEALTHY
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|capacity
operator|*
name|nodeCount
argument_list|,
operator|(
name|long
operator|)
name|nodeManager
operator|.
name|getStats
argument_list|()
operator|.
name|getCapacity
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|used
operator|*
name|nodeCount
argument_list|,
operator|(
name|long
operator|)
name|nodeManager
operator|.
name|getStats
argument_list|()
operator|.
name|getScmUsed
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|remaining
operator|*
name|nodeCount
argument_list|,
operator|(
name|long
operator|)
name|nodeManager
operator|.
name|getStats
argument_list|()
operator|.
name|getRemaining
argument_list|()
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeManager
operator|.
name|isOutOfChillMode
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerWithPipeline
name|containerWithPipeline
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|xceiverClientManager
operator|.
name|getType
argument_list|()
argument_list|,
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
argument_list|,
literal|"OZONE"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|xceiverClientManager
operator|.
name|getFactor
argument_list|()
operator|.
name|getNumber
argument_list|()
argument_list|,
name|containerWithPipeline
operator|.
name|getPipeline
argument_list|()
operator|.
name|getMachines
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|containerManager
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|testDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

