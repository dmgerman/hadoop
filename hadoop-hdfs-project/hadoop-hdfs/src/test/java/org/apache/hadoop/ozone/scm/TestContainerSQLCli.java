begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright containerOwnership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|scm
operator|.
name|block
operator|.
name|BlockManagerImpl
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
name|scm
operator|.
name|cli
operator|.
name|SQLCLI
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
name|ozone
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
name|ozone
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|hadoop
operator|.
name|scm
operator|.
name|protocolPB
operator|.
name|StorageContainerLocationProtocolClientSideTranslatorPB
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|BLOCK_DB
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|SCM_CONTAINER_DB
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|KB
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|NODEPOOL_DB
import|;
end_import

begin_comment
comment|//import static org.apache.hadoop.ozone.OzoneConsts.OPEN_CONTAINERS_DB;
end_comment

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
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * This class tests the CLI that transforms container into SQLite DB files.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestContainerSQLCli
specifier|public
class|class
name|TestContainerSQLCli
block|{
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_LEVELDB
block|}
block|,
block|{
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL_ROCKSDB
block|}
block|}
argument_list|)
return|;
block|}
DECL|field|metaStoreType
specifier|private
specifier|static
name|String
name|metaStoreType
decl_stmt|;
DECL|method|TestContainerSQLCli (String type)
specifier|public
name|TestContainerSQLCli
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|metaStoreType
operator|=
name|type
expr_stmt|;
block|}
DECL|field|cli
specifier|private
specifier|static
name|SQLCLI
name|cli
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniOzoneClassicCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
name|OzoneConfiguration
name|conf
decl_stmt|;
specifier|private
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|mapping
specifier|private
name|ContainerMapping
name|mapping
decl_stmt|;
DECL|field|nodeManager
specifier|private
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|blockManager
specifier|private
name|BlockManagerImpl
name|blockManager
decl_stmt|;
DECL|field|pipeline1
specifier|private
name|Pipeline
name|pipeline1
decl_stmt|;
DECL|field|pipeline2
specifier|private
name|Pipeline
name|pipeline2
decl_stmt|;
DECL|field|blockContainerMap
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|blockContainerMap
decl_stmt|;
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|final
specifier|static
name|long
name|DEFAULT_BLOCK_SIZE
init|=
literal|4
operator|*
name|KB
decl_stmt|;
DECL|field|factor
specifier|private
specifier|static
name|OzoneProtos
operator|.
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|field|type
specifier|private
specifier|static
name|OzoneProtos
operator|.
name|ReplicationType
name|type
decl_stmt|;
DECL|field|containerOwner
specifier|private
specifier|static
specifier|final
name|String
name|containerOwner
init|=
literal|"OZONE"
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|datanodeCapacities
init|=
literal|3
operator|*
name|OzoneConsts
operator|.
name|TB
decl_stmt|;
name|blockContainerMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|conf
operator|=
operator|new
name|OzoneConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
argument_list|,
literal|2
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
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_KEY
argument_list|,
name|ScmConfigKeys
operator|.
name|DFS_CONTAINER_RATIS_ENABLED_DEFAULT
argument_list|)
condition|)
block|{
name|factor
operator|=
name|OzoneProtos
operator|.
name|ReplicationFactor
operator|.
name|THREE
expr_stmt|;
name|type
operator|=
name|OzoneProtos
operator|.
name|ReplicationType
operator|.
name|RATIS
expr_stmt|;
block|}
else|else
block|{
name|factor
operator|=
name|OzoneProtos
operator|.
name|ReplicationFactor
operator|.
name|ONE
expr_stmt|;
name|type
operator|=
name|OzoneProtos
operator|.
name|ReplicationType
operator|.
name|STAND_ALONE
expr_stmt|;
block|}
name|cluster
operator|=
operator|new
name|MiniOzoneClassicCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|2
argument_list|)
operator|.
name|storageCapacities
argument_list|(
operator|new
name|long
index|[]
block|{
name|datanodeCapacities
block|,
name|datanodeCapacities
block|}
argument_list|)
operator|.
name|setHandlerType
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_HANDLER_DISTRIBUTED
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|storageContainerLocationClient
operator|=
name|cluster
operator|.
name|createStorageContainerLocationClient
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForHeartbeatProcessed
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|nodeManager
operator|=
name|cluster
operator|.
name|getStorageContainerManager
argument_list|()
operator|.
name|getScmNodeManager
argument_list|()
expr_stmt|;
name|mapping
operator|=
operator|new
name|ContainerMapping
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
literal|128
argument_list|)
expr_stmt|;
name|blockManager
operator|=
operator|new
name|BlockManagerImpl
argument_list|(
name|conf
argument_list|,
name|nodeManager
argument_list|,
name|mapping
argument_list|,
literal|128
argument_list|)
expr_stmt|;
comment|// blockManager.allocateBlock() will create containers if there is none
comment|// stored in levelDB. The number of containers to create is the value of
comment|// OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE which we set to 2.
comment|// so the first allocateBlock() will create two containers. A random one
comment|// is assigned for the block.
comment|// loop until both the two datanodes are up, try up to about 4 seconds.
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
literal|40
condition|;
name|c
operator|++
control|)
block|{
if|if
condition|(
name|nodeManager
operator|.
name|getAllNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
condition|)
block|{
break|break;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|nodeManager
operator|.
name|getAllNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|AllocatedBlock
name|ab1
init|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
decl_stmt|;
name|pipeline1
operator|=
name|ab1
operator|.
name|getPipeline
argument_list|()
expr_stmt|;
name|blockContainerMap
operator|.
name|put
argument_list|(
name|ab1
operator|.
name|getKey
argument_list|()
argument_list|,
name|pipeline1
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
name|AllocatedBlock
name|ab2
decl_stmt|;
comment|// we want the two blocks on the two provisioned containers respectively,
comment|// however blockManager picks containers randomly, keep retry until we
comment|// assign the second block to the other container. This seems to be the only
comment|// way to get the two containers.
comment|// although each retry will create a block and assign to a container. So
comment|// the size of blockContainerMap will vary each time the test is run.
while|while
condition|(
literal|true
condition|)
block|{
name|ab2
operator|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|containerOwner
argument_list|)
expr_stmt|;
name|pipeline2
operator|=
name|ab2
operator|.
name|getPipeline
argument_list|()
expr_stmt|;
name|blockContainerMap
operator|.
name|put
argument_list|(
name|ab2
operator|.
name|getKey
argument_list|()
argument_list|,
name|pipeline2
operator|.
name|getContainerName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pipeline1
operator|.
name|getContainerName
argument_list|()
operator|.
name|equals
argument_list|(
name|pipeline2
operator|.
name|getContainerName
argument_list|()
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
name|blockManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|mapping
operator|.
name|close
argument_list|()
expr_stmt|;
name|nodeManager
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_STORE_IMPL
argument_list|,
name|metaStoreType
argument_list|)
expr_stmt|;
name|cli
operator|=
operator|new
name|SQLCLI
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
literal|null
argument_list|,
name|storageContainerLocationClient
argument_list|,
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertBlockDB ()
specifier|public
name|void
name|testConvertBlockDB
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dbOutPath
init|=
name|cluster
operator|.
name|getDataDirectory
argument_list|()
operator|+
literal|"/out_sql.db"
decl_stmt|;
name|String
name|dbRootPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
name|String
name|dbPath
init|=
name|dbRootPath
operator|+
literal|"/"
operator|+
name|BLOCK_DB
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-p"
block|,
name|dbPath
block|,
literal|"-o"
block|,
name|dbOutPath
block|}
decl_stmt|;
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Connection
name|conn
init|=
name|connectDB
argument_list|(
name|dbOutPath
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
literal|"SELECT * FROM blockContainer"
decl_stmt|;
name|ResultSet
name|rs
init|=
name|executeQuery
argument_list|(
name|conn
argument_list|,
name|sql
argument_list|)
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|String
name|blockKey
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"blockKey"
argument_list|)
decl_stmt|;
name|String
name|containerName
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"containerName"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|blockContainerMap
operator|.
name|containsKey
argument_list|(
name|blockKey
argument_list|)
operator|&&
name|blockContainerMap
operator|.
name|remove
argument_list|(
name|blockKey
argument_list|)
operator|.
name|equals
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|blockContainerMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dbOutPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertNodepoolDB ()
specifier|public
name|void
name|testConvertNodepoolDB
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dbOutPath
init|=
name|cluster
operator|.
name|getDataDirectory
argument_list|()
operator|+
literal|"/out_sql.db"
decl_stmt|;
name|String
name|dbRootPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
name|String
name|dbPath
init|=
name|dbRootPath
operator|+
literal|"/"
operator|+
name|NODEPOOL_DB
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-p"
block|,
name|dbPath
block|,
literal|"-o"
block|,
name|dbOutPath
block|}
decl_stmt|;
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|// verify the sqlite db
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|expectedPool
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeID
name|dnid
range|:
name|nodeManager
operator|.
name|getAllNodes
argument_list|()
control|)
block|{
name|expectedPool
operator|.
name|put
argument_list|(
name|dnid
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
literal|"DefaultNodePool"
argument_list|)
expr_stmt|;
block|}
name|Connection
name|conn
init|=
name|connectDB
argument_list|(
name|dbOutPath
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
literal|"SELECT * FROM nodePool"
decl_stmt|;
name|ResultSet
name|rs
init|=
name|executeQuery
argument_list|(
name|conn
argument_list|,
name|sql
argument_list|)
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|String
name|datanodeUUID
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"datanodeUUID"
argument_list|)
decl_stmt|;
name|String
name|poolName
init|=
name|rs
operator|.
name|getString
argument_list|(
literal|"poolName"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expectedPool
operator|.
name|remove
argument_list|(
name|datanodeUUID
argument_list|)
operator|.
name|equals
argument_list|(
name|poolName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|expectedPool
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dbOutPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConvertContainerDB ()
specifier|public
name|void
name|testConvertContainerDB
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dbOutPath
init|=
name|cluster
operator|.
name|getDataDirectory
argument_list|()
operator|+
literal|"/out_sql.db"
decl_stmt|;
comment|// TODO : the following will fail due to empty Datanode list, need to fix.
comment|//String dnUUID = cluster.getDataNodes().get(0).getDatanodeUuid();
name|String
name|dbRootPath
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_METADATA_DIRS
argument_list|)
decl_stmt|;
name|String
name|dbPath
init|=
name|dbRootPath
operator|+
literal|"/"
operator|+
name|SCM_CONTAINER_DB
decl_stmt|;
name|String
index|[]
name|args
init|=
block|{
literal|"-p"
block|,
name|dbPath
block|,
literal|"-o"
block|,
name|dbOutPath
block|}
decl_stmt|;
name|Connection
name|conn
decl_stmt|;
name|String
name|sql
decl_stmt|;
name|ResultSet
name|rs
decl_stmt|;
name|cli
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
comment|//verify the sqlite db
comment|// only checks the container names are as expected. Because other fields
comment|// such as datanode UUID are generated randomly each time
name|conn
operator|=
name|connectDB
argument_list|(
name|dbOutPath
argument_list|)
expr_stmt|;
name|sql
operator|=
literal|"SELECT * FROM containerInfo"
expr_stmt|;
name|rs
operator|=
name|executeQuery
argument_list|(
name|conn
argument_list|,
name|sql
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|containerNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|containerNames
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|"containerName"
argument_list|)
argument_list|)
expr_stmt|;
comment|//assertEquals(dnUUID, rs.getString("leaderUUID"));
block|}
name|assertTrue
argument_list|(
name|containerNames
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|&&
name|containerNames
operator|.
name|contains
argument_list|(
name|pipeline1
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|&&
name|containerNames
operator|.
name|contains
argument_list|(
name|pipeline2
operator|.
name|getContainerName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sql
operator|=
literal|"SELECT * FROM containerMembers"
expr_stmt|;
name|rs
operator|=
name|executeQuery
argument_list|(
name|conn
argument_list|,
name|sql
argument_list|)
expr_stmt|;
name|containerNames
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|containerNames
operator|.
name|add
argument_list|(
name|rs
operator|.
name|getString
argument_list|(
literal|"containerName"
argument_list|)
argument_list|)
expr_stmt|;
comment|//assertEquals(dnUUID, rs.getString("datanodeUUID"));
block|}
name|assertTrue
argument_list|(
name|containerNames
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|&&
name|containerNames
operator|.
name|contains
argument_list|(
name|pipeline1
operator|.
name|getContainerName
argument_list|()
argument_list|)
operator|&&
name|containerNames
operator|.
name|contains
argument_list|(
name|pipeline2
operator|.
name|getContainerName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|sql
operator|=
literal|"SELECT * FROM datanodeInfo"
expr_stmt|;
name|rs
operator|=
name|executeQuery
argument_list|(
name|conn
argument_list|,
name|sql
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|rs
operator|.
name|getString
argument_list|(
literal|"ipAddr"
argument_list|)
argument_list|)
expr_stmt|;
comment|//assertEquals(dnUUID, rs.getString("datanodeUUID"));
name|count
operator|+=
literal|1
expr_stmt|;
block|}
comment|// the two containers maybe on the same datanode, maybe not.
name|int
name|expected
init|=
name|pipeline1
operator|.
name|getLeader
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
operator|.
name|equals
argument_list|(
name|pipeline2
operator|.
name|getLeader
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
condition|?
literal|1
else|:
literal|2
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|Files
operator|.
name|delete
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|dbOutPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|executeQuery (Connection conn, String sql)
specifier|private
name|ResultSet
name|executeQuery
parameter_list|(
name|Connection
name|conn
parameter_list|,
name|String
name|sql
parameter_list|)
throws|throws
name|SQLException
block|{
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
decl_stmt|;
return|return
name|stmt
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
return|;
block|}
DECL|method|connectDB (String dbPath)
specifier|private
name|Connection
name|connectDB
parameter_list|(
name|String
name|dbPath
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.sqlite.JDBC"
argument_list|)
expr_stmt|;
name|String
name|connectPath
init|=
name|String
operator|.
name|format
argument_list|(
literal|"jdbc:sqlite:%s"
argument_list|,
name|dbPath
argument_list|)
decl_stmt|;
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|connectPath
argument_list|)
return|;
block|}
block|}
end_class

end_unit

