begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|CreateFlag
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
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|protocol
operator|.
name|DatanodeInfo
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
name|protocol
operator|.
name|HdfsFileStatus
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
name|protocol
operator|.
name|LocatedBlock
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
name|blockmanagement
operator|.
name|BlockPlacementPolicy
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
name|blockmanagement
operator|.
name|BlockPlacementPolicyRackFaultTolerant
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
name|protocol
operator|.
name|NamenodeProtocols
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
name|net
operator|.
name|StaticMapping
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
name|*
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

begin_class
DECL|class|TestBlockPlacementPolicyRackFaultTolerant
specifier|public
class|class
name|TestBlockPlacementPolicyRackFaultTolerant
block|{
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|field|nameNodeRpc
specifier|private
name|NamenodeProtocols
name|nameNodeRpc
init|=
literal|null
decl_stmt|;
DECL|field|namesystem
specifier|private
name|FSNamesystem
name|namesystem
init|=
literal|null
decl_stmt|;
DECL|field|perm
specifier|private
name|PermissionStatus
name|perm
init|=
literal|null
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
name|StaticMapping
operator|.
name|resetMap
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|rackList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|hostList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|2
condition|;
name|j
operator|++
control|)
block|{
name|rackList
operator|.
name|add
argument_list|(
literal|"/rack"
operator|+
name|i
argument_list|)
expr_stmt|;
name|hostList
operator|.
name|add
argument_list|(
literal|"/host"
operator|+
name|i
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
block|}
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|BlockPlacementPolicyRackFaultTolerant
operator|.
name|class
argument_list|,
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_SIZE_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BYTES_PER_CHECKSUM_KEY
argument_list|,
name|DEFAULT_BLOCK_SIZE
operator|/
literal|2
argument_list|)
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|hostList
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|racks
argument_list|(
name|rackList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|rackList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
operator|.
name|hosts
argument_list|(
name|hostList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|hostList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|nameNodeRpc
operator|=
name|cluster
operator|.
name|getNameNodeRpc
argument_list|()
expr_stmt|;
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|perm
operator|=
operator|new
name|PermissionStatus
argument_list|(
literal|"TestBlockPlacementPolicyEC"
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testChooseTarget ()
specifier|public
name|void
name|testChooseTarget
parameter_list|()
throws|throws
name|Exception
block|{
name|doTestChooseTargetNormalCase
argument_list|()
expr_stmt|;
name|doTestChooseTargetSpecialCase
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestChooseTargetNormalCase ()
specifier|private
name|void
name|doTestChooseTargetNormalCase
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientMachine
init|=
literal|"client.foo.com"
decl_stmt|;
name|short
index|[]
index|[]
name|testSuite
init|=
block|{
block|{
literal|3
block|,
literal|2
block|}
block|,
block|{
literal|3
block|,
literal|7
block|}
block|,
block|{
literal|3
block|,
literal|8
block|}
block|,
block|{
literal|3
block|,
literal|10
block|}
block|,
block|{
literal|9
block|,
literal|1
block|}
block|,
block|{
literal|10
block|,
literal|1
block|}
block|,
block|{
literal|10
block|,
literal|6
block|}
block|,
block|{
literal|11
block|,
literal|6
block|}
block|,
block|{
literal|11
block|,
literal|9
block|}
block|}
decl_stmt|;
comment|// Test 5 files
name|int
name|fileCount
init|=
literal|0
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|short
index|[]
name|testCase
range|:
name|testSuite
control|)
block|{
name|short
name|replication
init|=
name|testCase
index|[
literal|0
index|]
decl_stmt|;
name|short
name|additionalReplication
init|=
name|testCase
index|[
literal|1
index|]
decl_stmt|;
name|String
name|src
init|=
literal|"/testfile"
operator|+
operator|(
name|fileCount
operator|++
operator|)
decl_stmt|;
comment|// Create the file with client machine
name|HdfsFileStatus
name|fileStatus
init|=
name|namesystem
operator|.
name|startFile
argument_list|(
name|src
argument_list|,
name|perm
argument_list|,
name|clientMachine
argument_list|,
name|clientMachine
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
literal|true
argument_list|,
name|replication
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//test chooseTarget for new file
name|LocatedBlock
name|locatedBlock
init|=
name|nameNodeRpc
operator|.
name|addBlock
argument_list|(
name|src
argument_list|,
name|clientMachine
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|fileStatus
operator|.
name|getFileId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doTestLocatedBlock
argument_list|(
name|replication
argument_list|,
name|locatedBlock
argument_list|)
expr_stmt|;
comment|//test chooseTarget for existing file.
name|LocatedBlock
name|additionalLocatedBlock
init|=
name|nameNodeRpc
operator|.
name|getAdditionalDatanode
argument_list|(
name|src
argument_list|,
name|fileStatus
operator|.
name|getFileId
argument_list|()
argument_list|,
name|locatedBlock
operator|.
name|getBlock
argument_list|()
argument_list|,
name|locatedBlock
operator|.
name|getLocations
argument_list|()
argument_list|,
name|locatedBlock
operator|.
name|getStorageIDs
argument_list|()
argument_list|,
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
argument_list|,
name|additionalReplication
argument_list|,
name|clientMachine
argument_list|)
decl_stmt|;
name|doTestLocatedBlock
argument_list|(
name|replication
operator|+
name|additionalReplication
argument_list|,
name|additionalLocatedBlock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test more randomly. So it covers some special cases.    * Like when some racks already have 2 replicas, while some racks have none,    * we should choose the racks that have none.    */
DECL|method|doTestChooseTargetSpecialCase ()
specifier|private
name|void
name|doTestChooseTargetSpecialCase
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientMachine
init|=
literal|"client.foo.com"
decl_stmt|;
comment|// Test 5 files
name|String
name|src
init|=
literal|"/testfile_1_"
decl_stmt|;
comment|// Create the file with client machine
name|HdfsFileStatus
name|fileStatus
init|=
name|namesystem
operator|.
name|startFile
argument_list|(
name|src
argument_list|,
name|perm
argument_list|,
name|clientMachine
argument_list|,
name|clientMachine
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
literal|true
argument_list|,
operator|(
name|short
operator|)
literal|20
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//test chooseTarget for new file
name|LocatedBlock
name|locatedBlock
init|=
name|nameNodeRpc
operator|.
name|addBlock
argument_list|(
name|src
argument_list|,
name|clientMachine
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|fileStatus
operator|.
name|getFileId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doTestLocatedBlock
argument_list|(
literal|20
argument_list|,
name|locatedBlock
argument_list|)
expr_stmt|;
name|DatanodeInfo
index|[]
name|locs
init|=
name|locatedBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|String
index|[]
name|storageIDs
init|=
name|locatedBlock
operator|.
name|getStorageIDs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|time
init|=
literal|0
init|;
name|time
operator|<
literal|5
condition|;
name|time
operator|++
control|)
block|{
name|shuffle
argument_list|(
name|locs
argument_list|,
name|storageIDs
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|locs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|DatanodeInfo
index|[]
name|partLocs
init|=
operator|new
name|DatanodeInfo
index|[
name|i
index|]
decl_stmt|;
name|String
index|[]
name|partStorageIDs
init|=
operator|new
name|String
index|[
name|i
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|locs
argument_list|,
literal|0
argument_list|,
name|partLocs
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|storageIDs
argument_list|,
literal|0
argument_list|,
name|partStorageIDs
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
literal|20
operator|-
name|i
condition|;
name|j
operator|++
control|)
block|{
name|LocatedBlock
name|additionalLocatedBlock
init|=
name|nameNodeRpc
operator|.
name|getAdditionalDatanode
argument_list|(
name|src
argument_list|,
name|fileStatus
operator|.
name|getFileId
argument_list|()
argument_list|,
name|locatedBlock
operator|.
name|getBlock
argument_list|()
argument_list|,
name|partLocs
argument_list|,
name|partStorageIDs
argument_list|,
operator|new
name|DatanodeInfo
index|[
literal|0
index|]
argument_list|,
name|j
argument_list|,
name|clientMachine
argument_list|)
decl_stmt|;
name|doTestLocatedBlock
argument_list|(
name|i
operator|+
name|j
argument_list|,
name|additionalLocatedBlock
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|shuffle (DatanodeInfo[] locs, String[] storageIDs)
specifier|private
name|void
name|shuffle
parameter_list|(
name|DatanodeInfo
index|[]
name|locs
parameter_list|,
name|String
index|[]
name|storageIDs
parameter_list|)
block|{
name|int
name|length
init|=
name|locs
operator|.
name|length
decl_stmt|;
name|Object
index|[]
index|[]
name|pairs
init|=
operator|new
name|Object
index|[
name|length
index|]
index|[]
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pairs
index|[
name|i
index|]
operator|=
operator|new
name|Object
index|[]
block|{
name|locs
index|[
name|i
index|]
block|,
name|storageIDs
index|[
name|i
index|]
block|}
expr_stmt|;
block|}
name|DFSUtil
operator|.
name|shuffle
argument_list|(
name|pairs
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|locs
index|[
name|i
index|]
operator|=
operator|(
name|DatanodeInfo
operator|)
name|pairs
index|[
name|i
index|]
index|[
literal|0
index|]
expr_stmt|;
name|storageIDs
index|[
name|i
index|]
operator|=
operator|(
name|String
operator|)
name|pairs
index|[
name|i
index|]
index|[
literal|1
index|]
expr_stmt|;
block|}
block|}
DECL|method|doTestLocatedBlock (int replication, LocatedBlock locatedBlock)
specifier|private
name|void
name|doTestLocatedBlock
parameter_list|(
name|int
name|replication
parameter_list|,
name|LocatedBlock
name|locatedBlock
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|replication
argument_list|,
name|locatedBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|racksCount
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|DatanodeInfo
name|node
range|:
name|locatedBlock
operator|.
name|getLocations
argument_list|()
control|)
block|{
name|addToRacksCount
argument_list|(
name|node
operator|.
name|getNetworkLocation
argument_list|()
argument_list|,
name|racksCount
argument_list|)
expr_stmt|;
block|}
name|int
name|minCount
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
name|int
name|maxCount
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|Integer
name|rackCount
range|:
name|racksCount
operator|.
name|values
argument_list|()
control|)
block|{
name|minCount
operator|=
name|Math
operator|.
name|min
argument_list|(
name|minCount
argument_list|,
name|rackCount
argument_list|)
expr_stmt|;
name|maxCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxCount
argument_list|,
name|rackCount
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|maxCount
operator|-
name|minCount
operator|<=
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|addToRacksCount (String rack, HashMap<String, Integer> racksCount)
specifier|private
name|void
name|addToRacksCount
parameter_list|(
name|String
name|rack
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|racksCount
parameter_list|)
block|{
name|Integer
name|count
init|=
name|racksCount
operator|.
name|get
argument_list|(
name|rack
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
name|racksCount
operator|.
name|put
argument_list|(
name|rack
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|racksCount
operator|.
name|put
argument_list|(
name|rack
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

