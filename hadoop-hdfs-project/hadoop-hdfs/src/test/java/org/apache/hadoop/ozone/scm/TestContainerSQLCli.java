begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Test
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
name|CONTAINER_DB
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
DECL|class|TestContainerSQLCli
specifier|public
class|class
name|TestContainerSQLCli
block|{
DECL|field|cli
specifier|private
specifier|static
name|SQLCLI
name|cli
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneCluster
name|cluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|OzoneConfiguration
name|conf
decl_stmt|;
specifier|private
specifier|static
name|StorageContainerLocationProtocolClientSideTranslatorPB
DECL|field|storageContainerLocationClient
name|storageContainerLocationClient
decl_stmt|;
DECL|field|mapping
specifier|private
specifier|static
name|ContainerMapping
name|mapping
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|static
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|blockManager
specifier|private
specifier|static
name|BlockManagerImpl
name|blockManager
decl_stmt|;
DECL|field|pipelineName1
specifier|private
specifier|static
name|String
name|pipelineName1
decl_stmt|;
DECL|field|pipelineName2
specifier|private
specifier|static
name|String
name|pipelineName2
decl_stmt|;
DECL|field|blockContainerMap
specifier|private
specifier|static
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
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
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
name|cluster
operator|=
operator|new
name|MiniOzoneCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
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
literal|"distributed"
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
name|AllocatedBlock
name|ab1
init|=
name|blockManager
operator|.
name|allocateBlock
argument_list|(
name|DEFAULT_BLOCK_SIZE
argument_list|)
decl_stmt|;
name|pipelineName1
operator|=
name|ab1
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
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
name|pipelineName1
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
argument_list|)
expr_stmt|;
name|pipelineName2
operator|=
name|ab2
operator|.
name|getPipeline
argument_list|()
operator|.
name|getContainerName
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
name|pipelineName2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pipelineName2
operator|.
name|equals
argument_list|(
name|pipelineName1
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
name|cli
operator|=
operator|new
name|SQLCLI
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|IOUtils
operator|.
name|cleanup
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
name|OZONE_CONTAINER_METADATA_DIRS
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
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|)
decl_stmt|;
name|String
name|dbPath
init|=
name|dbRootPath
operator|+
literal|"/"
operator|+
name|CONTAINER_DB
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
name|pipelineName1
argument_list|)
operator|&&
name|containerNames
operator|.
name|contains
argument_list|(
name|pipelineName2
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
name|pipelineName1
argument_list|)
operator|&&
name|containerNames
operator|.
name|contains
argument_list|(
name|pipelineName2
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
name|assertEquals
argument_list|(
literal|1
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

