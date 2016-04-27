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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|DatanodeAdminProperties
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
name|BlockPlacementPolicyWithUpgradeDomain
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
name|CombinedHostFileManager
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
name|HostConfigManager
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
name|hdfs
operator|.
name|util
operator|.
name|HostsFileWriter
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

begin_comment
comment|/**  * End-to-end test case for upgrade domain  * The test configs upgrade domain for nodes via admin json  * config file and put some nodes to decommission state.  * The test then verifies replicas are placed on the nodes that  * satisfy the upgrade domain policy.  *  */
end_comment

begin_class
DECL|class|TestUpgradeDomainBlockPlacementPolicy
specifier|public
class|class
name|TestUpgradeDomainBlockPlacementPolicy
block|{
DECL|field|REPLICATION_FACTOR
specifier|private
specifier|static
specifier|final
name|short
name|REPLICATION_FACTOR
init|=
operator|(
name|short
operator|)
literal|3
decl_stmt|;
DECL|field|DEFAULT_BLOCK_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_BLOCK_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|racks
specifier|static
specifier|final
name|String
index|[]
name|racks
init|=
block|{
literal|"/RACK1"
block|,
literal|"/RACK1"
block|,
literal|"/RACK1"
block|,
literal|"/RACK2"
block|,
literal|"/RACK2"
block|,
literal|"/RACK2"
block|}
decl_stmt|;
comment|/**    *  Use host names that can be resolved (    *  InetSocketAddress#isUnresolved == false). Otherwise,    *  CombinedHostFileManager won't allow those hosts.    */
DECL|field|hosts
specifier|static
specifier|final
name|String
index|[]
name|hosts
init|=
block|{
literal|"127.0.0.1"
block|,
literal|"127.0.0.1"
block|,
literal|"127.0.0.1"
block|,
literal|"127.0.0.1"
block|,
literal|"127.0.0.1"
block|,
literal|"127.0.0.1"
block|}
decl_stmt|;
DECL|field|upgradeDomains
specifier|static
specifier|final
name|String
index|[]
name|upgradeDomains
init|=
block|{
literal|"ud1"
block|,
literal|"ud2"
block|,
literal|"ud3"
block|,
literal|"ud1"
block|,
literal|"ud2"
block|,
literal|"ud3"
block|}
decl_stmt|;
DECL|field|expectedDatanodeIDs
specifier|static
specifier|final
name|Set
argument_list|<
name|DatanodeID
argument_list|>
name|expectedDatanodeIDs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|conf
operator|.
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|BlockPlacementPolicyWithUpgradeDomain
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
name|setClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HOSTS_PROVIDER_CLASSNAME_KEY
argument_list|,
name|CombinedHostFileManager
operator|.
name|class
argument_list|,
name|HostConfigManager
operator|.
name|class
argument_list|)
expr_stmt|;
name|HostsFileWriter
name|hostsFileWriter
init|=
operator|new
name|HostsFileWriter
argument_list|()
decl_stmt|;
name|hostsFileWriter
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|"temp/upgradedomainpolicy"
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
literal|6
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts
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
literal|"TestDefaultBlockPlacementPolicy"
argument_list|,
literal|null
argument_list|,
name|FsPermission
operator|.
name|getDefault
argument_list|()
argument_list|)
expr_stmt|;
name|refreshDatanodeAdminProperties
argument_list|(
name|hostsFileWriter
argument_list|)
expr_stmt|;
name|hostsFileWriter
operator|.
name|cleanup
argument_list|()
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
comment|/**    * Define admin properties for these datanodes as follows.    * dn0 and dn3 have upgrade domain ud1.    * dn1 and dn4 have upgrade domain ud2.    * dn2 and dn5 have upgrade domain ud3.    * dn0 and dn5 are decommissioned.    * Given dn0, dn1 and dn2 are on rack1 and dn3, dn4 and dn5 are on    * rack2. Then any block's replicas should be on either    * {dn1, dn2, d3} or {dn2, dn3, dn4}.    */
DECL|method|refreshDatanodeAdminProperties (HostsFileWriter hostsFileWriter)
specifier|private
name|void
name|refreshDatanodeAdminProperties
parameter_list|(
name|HostsFileWriter
name|hostsFileWriter
parameter_list|)
throws|throws
name|IOException
block|{
name|DatanodeAdminProperties
index|[]
name|datanodes
init|=
operator|new
name|DatanodeAdminProperties
index|[
name|hosts
operator|.
name|length
index|]
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
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|datanodes
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeAdminProperties
argument_list|()
expr_stmt|;
name|DatanodeID
name|datanodeID
init|=
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
decl_stmt|;
name|datanodes
index|[
name|i
index|]
operator|.
name|setHostName
argument_list|(
name|datanodeID
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|datanodes
index|[
name|i
index|]
operator|.
name|setPort
argument_list|(
name|datanodeID
operator|.
name|getXferPort
argument_list|()
argument_list|)
expr_stmt|;
name|datanodes
index|[
name|i
index|]
operator|.
name|setUpgradeDomain
argument_list|(
name|upgradeDomains
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|datanodes
index|[
literal|0
index|]
operator|.
name|setAdminState
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|DECOMMISSIONED
argument_list|)
expr_stmt|;
name|datanodes
index|[
literal|5
index|]
operator|.
name|setAdminState
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|DECOMMISSIONED
argument_list|)
expr_stmt|;
name|hostsFileWriter
operator|.
name|initIncludeHosts
argument_list|(
name|datanodes
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|getFileSystem
argument_list|()
operator|.
name|refreshNodes
argument_list|()
expr_stmt|;
name|expectedDatanodeIDs
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
name|expectedDatanodeIDs
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|getDataNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|3
argument_list|)
operator|.
name|getDatanodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPlacement ()
specifier|public
name|void
name|testPlacement
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|clientMachine
init|=
literal|"127.0.0.1"
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
name|String
name|src
init|=
literal|"/test-"
operator|+
name|i
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
name|REPLICATION_FACTOR
argument_list|,
name|DEFAULT_BLOCK_SIZE
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"Block should be allocated sufficient locations"
argument_list|,
name|REPLICATION_FACTOR
argument_list|,
name|locatedBlock
operator|.
name|getLocations
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DatanodeInfo
argument_list|>
name|locs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|locatedBlock
operator|.
name|getLocations
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeID
name|datanodeID
range|:
name|expectedDatanodeIDs
control|)
block|{
name|locs
operator|.
name|contains
argument_list|(
name|datanodeID
argument_list|)
expr_stmt|;
block|}
name|nameNodeRpc
operator|.
name|abandonBlock
argument_list|(
name|locatedBlock
operator|.
name|getBlock
argument_list|()
argument_list|,
name|fileStatus
operator|.
name|getFileId
argument_list|()
argument_list|,
name|src
argument_list|,
name|clientMachine
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

