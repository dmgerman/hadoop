begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Path
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
name|Collections
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
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|fs
operator|.
name|StorageType
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
name|ExtendedBlock
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
name|namenode
operator|.
name|FSNamesystem
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
name|DatanodeInfoWithStorage
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
name|HdfsConstants
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|ExportedBlockKeys
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
name|common
operator|.
name|HdfsServerConstants
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
name|common
operator|.
name|StorageInfo
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
name|DatanodeRegistration
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
name|DNSToSwitchMapping
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
name|util
operator|.
name|Shell
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|*
import|;
end_import

begin_class
DECL|class|TestDatanodeManager
specifier|public
class|class
name|TestDatanodeManager
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestDatanodeManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//The number of times the registration / removal of nodes should happen
DECL|field|NUM_ITERATIONS
specifier|final
name|int
name|NUM_ITERATIONS
init|=
literal|500
decl_stmt|;
DECL|method|mockDatanodeManager ( FSNamesystem fsn, Configuration conf)
specifier|private
specifier|static
name|DatanodeManager
name|mockDatanodeManager
parameter_list|(
name|FSNamesystem
name|fsn
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|BlockManager
name|bm
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|BlockManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|BlockReportLeaseManager
name|blm
init|=
operator|new
name|BlockReportLeaseManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|bm
operator|.
name|getBlockReportLeaseManager
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|blm
argument_list|)
expr_stmt|;
name|DatanodeManager
name|dm
init|=
operator|new
name|DatanodeManager
argument_list|(
name|bm
argument_list|,
name|fsn
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|dm
return|;
block|}
comment|/**    * Create an InetSocketAddress for a host:port string    * @param host a host identifier in host:port format    * @return a corresponding InetSocketAddress object    */
DECL|method|entry (String host)
specifier|private
specifier|static
name|InetSocketAddress
name|entry
parameter_list|(
name|String
name|host
parameter_list|)
block|{
return|return
name|HostFileManager
operator|.
name|parseEntry
argument_list|(
literal|"dummy"
argument_list|,
literal|"dummy"
argument_list|,
name|host
argument_list|)
return|;
block|}
comment|/**    * This test sends a random sequence of node registrations and node removals    * to the DatanodeManager (of nodes with different IDs and versions), and    * checks that the DatanodeManager keeps a correct count of different software    * versions at all times.    */
annotation|@
name|Test
DECL|method|testNumVersionsReportedCorrect ()
specifier|public
name|void
name|testNumVersionsReportedCorrect
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Create the DatanodeManager which will be tested
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsn
operator|.
name|hasWriteLock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DatanodeManager
name|dm
init|=
name|mockDatanodeManager
argument_list|(
name|fsn
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
comment|//Seed the RNG with a known value so test failures are easier to reproduce
name|Random
name|rng
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|int
name|seed
init|=
name|rng
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|rng
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Using seed "
operator|+
name|seed
operator|+
literal|" for testing"
argument_list|)
expr_stmt|;
comment|//A map of the Storage IDs to the DN registration it was registered with
name|HashMap
argument_list|<
name|String
argument_list|,
name|DatanodeRegistration
argument_list|>
name|sIdToDnReg
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DatanodeRegistration
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
name|NUM_ITERATIONS
condition|;
operator|++
name|i
control|)
block|{
comment|//If true, remove a node for every 3rd time (if there's one)
if|if
condition|(
name|rng
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|i
operator|%
literal|3
operator|==
literal|0
operator|&&
name|sIdToDnReg
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
comment|//Pick a random node.
name|int
name|randomIndex
init|=
name|rng
operator|.
name|nextInt
argument_list|()
operator|%
name|sIdToDnReg
operator|.
name|size
argument_list|()
decl_stmt|;
comment|//Iterate to that random position
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DatanodeRegistration
argument_list|>
argument_list|>
name|it
init|=
name|sIdToDnReg
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|randomIndex
operator|-
literal|1
condition|;
operator|++
name|j
control|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|DatanodeRegistration
name|toRemove
init|=
name|it
operator|.
name|next
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing node "
operator|+
name|toRemove
operator|.
name|getDatanodeUuid
argument_list|()
operator|+
literal|" ip "
operator|+
name|toRemove
operator|.
name|getXferAddr
argument_list|()
operator|+
literal|" version : "
operator|+
name|toRemove
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|)
expr_stmt|;
comment|//Remove that random node
name|dm
operator|.
name|removeDatanode
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// Otherwise register a node. This node may be a new / an old one
else|else
block|{
comment|//Pick a random storageID to register.
name|String
name|storageID
init|=
literal|"someStorageID"
operator|+
name|rng
operator|.
name|nextInt
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|DatanodeRegistration
name|dr
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|storageID
argument_list|)
expr_stmt|;
comment|//If this storageID had already been registered before
if|if
condition|(
name|sIdToDnReg
operator|.
name|containsKey
argument_list|(
name|storageID
argument_list|)
condition|)
block|{
name|dr
operator|=
name|sIdToDnReg
operator|.
name|get
argument_list|(
name|storageID
argument_list|)
expr_stmt|;
comment|//Half of the times, change the IP address
if|if
condition|(
name|rng
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|dr
operator|.
name|setIpAddr
argument_list|(
name|dr
operator|.
name|getIpAddr
argument_list|()
operator|+
literal|"newIP"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//This storageID has never been registered
comment|//Ensure IP address is unique to storageID
name|String
name|ip
init|=
literal|"someIP"
operator|+
name|storageID
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getIpAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ip
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getXferAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ip
operator|+
literal|":9000"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getXferPort
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|9000
argument_list|)
expr_stmt|;
block|}
comment|//Pick a random version to register with
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"version"
operator|+
name|rng
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering node storageID: "
operator|+
name|dr
operator|.
name|getDatanodeUuid
argument_list|()
operator|+
literal|", version: "
operator|+
name|dr
operator|.
name|getSoftwareVersion
argument_list|()
operator|+
literal|", IP address: "
operator|+
name|dr
operator|.
name|getXferAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|//Register this random node
name|dm
operator|.
name|registerDatanode
argument_list|(
name|dr
argument_list|)
expr_stmt|;
name|sIdToDnReg
operator|.
name|put
argument_list|(
name|storageID
argument_list|,
name|dr
argument_list|)
expr_stmt|;
block|}
comment|//Verify DatanodeManager still has the right count
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|mapToCheck
init|=
name|dm
operator|.
name|getDatanodesSoftwareVersions
argument_list|()
decl_stmt|;
comment|//Remove counts from versions and make sure that after removing all nodes
comment|//mapToCheck is empty
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|DatanodeRegistration
argument_list|>
name|it
range|:
name|sIdToDnReg
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|ver
init|=
name|it
operator|.
name|getValue
argument_list|()
operator|.
name|getSoftwareVersion
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|mapToCheck
operator|.
name|containsKey
argument_list|(
name|ver
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"The correct number of datanodes of a "
operator|+
literal|"version was not found on iteration "
operator|+
name|i
argument_list|)
throw|;
block|}
name|mapToCheck
operator|.
name|put
argument_list|(
name|ver
argument_list|,
name|mapToCheck
operator|.
name|get
argument_list|(
name|ver
argument_list|)
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|mapToCheck
operator|.
name|get
argument_list|(
name|ver
argument_list|)
operator|==
literal|0
condition|)
block|{
name|mapToCheck
operator|.
name|remove
argument_list|(
name|ver
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry
range|:
name|mapToCheck
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Still in map: "
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|" has "
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"The map of version counts returned by DatanodeManager was"
operator|+
literal|" not what it was expected to be on iteration "
operator|+
name|i
argument_list|,
literal|0
argument_list|,
name|mapToCheck
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|100000
argument_list|)
DECL|method|testRejectUnresolvedDatanodes ()
specifier|public
name|void
name|testRejectUnresolvedDatanodes
parameter_list|()
throws|throws
name|IOException
block|{
comment|//Create the DatanodeManager which will be tested
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsn
operator|.
name|hasWriteLock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|//Set configuration property for rejecting unresolved topology mapping
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REJECT_UNRESOLVED_DN_TOPOLOGY_MAPPING_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|//set TestDatanodeManager.MyResolver to be used for topology resolving
name|conf
operator|.
name|setClass
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY
argument_list|,
name|TestDatanodeManager
operator|.
name|MyResolver
operator|.
name|class
argument_list|,
name|DNSToSwitchMapping
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//create DatanodeManager
name|DatanodeManager
name|dm
init|=
name|mockDatanodeManager
argument_list|(
name|fsn
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|//storageID to register.
name|String
name|storageID
init|=
literal|"someStorageID-123"
decl_stmt|;
name|DatanodeRegistration
name|dr
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|storageID
argument_list|)
expr_stmt|;
try|try
block|{
comment|//Register this node
name|dm
operator|.
name|registerDatanode
argument_list|(
name|dr
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an UnresolvedTopologyException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnresolvedTopologyException
name|ute
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected - topology is not resolved and "
operator|+
literal|"registration is rejected."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Expected an UnresolvedTopologyException"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * MyResolver class provides resolve method which always returns null     * in order to simulate unresolved topology mapping.    */
DECL|class|MyResolver
specifier|public
specifier|static
class|class
name|MyResolver
implements|implements
name|DNSToSwitchMapping
block|{
annotation|@
name|Override
DECL|method|resolve (List<String> names)
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|reloadCachedMappings ()
specifier|public
name|void
name|reloadCachedMappings
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|reloadCachedMappings (List<String> names)
specifier|public
name|void
name|reloadCachedMappings
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{       }
block|}
comment|/**    * This test creates a LocatedBlock with 5 locations, sorts the locations    * based on the network topology, and ensures the locations are still aligned    * with the storage ids and storage types.    */
annotation|@
name|Test
DECL|method|testSortLocatedBlocks ()
specifier|public
name|void
name|testSortLocatedBlocks
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|HelperFunction
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Execute a functional topology script and make sure that helper    * function works correctly    *    * @throws IOException    * @throws URISyntaxException    */
annotation|@
name|Test
DECL|method|testgoodScript ()
specifier|public
name|void
name|testgoodScript
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|HelperFunction
argument_list|(
literal|"/"
operator|+
name|Shell
operator|.
name|appendScriptExtension
argument_list|(
literal|"topology-script"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Run a broken script and verify that helper function is able to    * ignore the broken script and work correctly    *    * @throws IOException    * @throws URISyntaxException    */
annotation|@
name|Test
DECL|method|testBadScript ()
specifier|public
name|void
name|testBadScript
parameter_list|()
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|HelperFunction
argument_list|(
literal|"/"
operator|+
name|Shell
operator|.
name|appendScriptExtension
argument_list|(
literal|"topology-broken-script"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Helper function that tests the DatanodeManagers SortedBlock function    * we invoke this function with and without topology scripts    *    * @param scriptFileName - Script Name or null    *    * @throws URISyntaxException    * @throws IOException    */
DECL|method|HelperFunction (String scriptFileName)
specifier|public
name|void
name|HelperFunction
parameter_list|(
name|String
name|scriptFileName
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|IOException
block|{
comment|// create the DatanodeManager which will be tested
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsn
operator|.
name|hasWriteLock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|scriptFileName
operator|!=
literal|null
operator|&&
operator|!
name|scriptFileName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|URL
name|shellScript
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
name|scriptFileName
argument_list|)
decl_stmt|;
name|Path
name|resourcePath
init|=
name|Paths
operator|.
name|get
argument_list|(
name|shellScript
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtil
operator|.
name|setExecutable
argument_list|(
name|resourcePath
operator|.
name|toFile
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
argument_list|,
name|resourcePath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DatanodeManager
name|dm
init|=
name|mockDatanodeManager
argument_list|(
name|fsn
argument_list|,
name|conf
argument_list|)
decl_stmt|;
comment|// register 5 datanodes, each with different storage ID and type
name|DatanodeInfo
index|[]
name|locs
init|=
operator|new
name|DatanodeInfo
index|[
literal|5
index|]
decl_stmt|;
name|String
index|[]
name|storageIDs
init|=
operator|new
name|String
index|[
literal|5
index|]
decl_stmt|;
name|StorageType
index|[]
name|storageTypes
init|=
operator|new
name|StorageType
index|[]
block|{
name|StorageType
operator|.
name|ARCHIVE
block|,
name|StorageType
operator|.
name|DEFAULT
block|,
name|StorageType
operator|.
name|DISK
block|,
name|StorageType
operator|.
name|RAM_DISK
block|,
name|StorageType
operator|.
name|SSD
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
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// register new datanode
name|String
name|uuid
init|=
literal|"UUID-"
operator|+
name|i
decl_stmt|;
name|String
name|ip
init|=
literal|"IP-"
operator|+
name|i
decl_stmt|;
name|DatanodeRegistration
name|dr
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|DatanodeRegistration
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getIpAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ip
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getXferAddr
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|ip
operator|+
literal|":9000"
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getXferPort
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|9000
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|dr
operator|.
name|getSoftwareVersion
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"version1"
argument_list|)
expr_stmt|;
name|dm
operator|.
name|registerDatanode
argument_list|(
name|dr
argument_list|)
expr_stmt|;
comment|// get location and storage information
name|locs
index|[
name|i
index|]
operator|=
name|dm
operator|.
name|getDatanode
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|storageIDs
index|[
name|i
index|]
operator|=
literal|"storageID-"
operator|+
name|i
expr_stmt|;
block|}
comment|// set first 2 locations as decomissioned
name|locs
index|[
literal|0
index|]
operator|.
name|setDecommissioned
argument_list|()
expr_stmt|;
name|locs
index|[
literal|1
index|]
operator|.
name|setDecommissioned
argument_list|()
expr_stmt|;
comment|// create LocatedBlock with above locations
name|ExtendedBlock
name|b
init|=
operator|new
name|ExtendedBlock
argument_list|(
literal|"somePoolID"
argument_list|,
literal|1234
argument_list|)
decl_stmt|;
name|LocatedBlock
name|block
init|=
operator|new
name|LocatedBlock
argument_list|(
name|b
argument_list|,
name|locs
argument_list|,
name|storageIDs
argument_list|,
name|storageTypes
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|blocks
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
specifier|final
name|String
name|targetIp
init|=
name|locs
index|[
literal|4
index|]
operator|.
name|getIpAddr
argument_list|()
decl_stmt|;
comment|// sort block locations
name|dm
operator|.
name|sortLocatedBlocks
argument_list|(
name|targetIp
argument_list|,
name|blocks
argument_list|)
expr_stmt|;
comment|// check that storage IDs/types are aligned with datanode locs
name|DatanodeInfo
index|[]
name|sortedLocs
init|=
name|block
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|storageIDs
operator|=
name|block
operator|.
name|getStorageIDs
argument_list|()
expr_stmt|;
name|storageTypes
operator|=
name|block
operator|.
name|getStorageTypes
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|sortedLocs
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|storageIDs
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|storageTypes
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|5
argument_list|)
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
name|sortedLocs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
operator|(
operator|(
name|DatanodeInfoWithStorage
operator|)
name|sortedLocs
index|[
name|i
index|]
operator|)
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|is
argument_list|(
name|storageIDs
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|DatanodeInfoWithStorage
operator|)
name|sortedLocs
index|[
name|i
index|]
operator|)
operator|.
name|getStorageType
argument_list|()
argument_list|,
name|is
argument_list|(
name|storageTypes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Ensure the local node is first.
name|assertThat
argument_list|(
name|sortedLocs
index|[
literal|0
index|]
operator|.
name|getIpAddr
argument_list|()
argument_list|,
name|is
argument_list|(
name|targetIp
argument_list|)
argument_list|)
expr_stmt|;
comment|// Ensure the two decommissioned DNs were moved to the end.
name|assertThat
argument_list|(
name|sortedLocs
index|[
name|sortedLocs
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|getAdminState
argument_list|()
argument_list|,
name|is
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|DECOMMISSIONED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|sortedLocs
index|[
name|sortedLocs
operator|.
name|length
operator|-
literal|2
index|]
operator|.
name|getAdminState
argument_list|()
argument_list|,
name|is
argument_list|(
name|DatanodeInfo
operator|.
name|AdminStates
operator|.
name|DECOMMISSIONED
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test whether removing a host from the includes list without adding it to    * the excludes list will exclude it from data node reports.    */
annotation|@
name|Test
DECL|method|testRemoveIncludedNode ()
specifier|public
name|void
name|testRemoveIncludedNode
parameter_list|()
throws|throws
name|IOException
block|{
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Set the write lock so that the DatanodeManager can start
name|Mockito
operator|.
name|when
argument_list|(
name|fsn
operator|.
name|hasWriteLock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DatanodeManager
name|dm
init|=
name|mockDatanodeManager
argument_list|(
name|fsn
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|HostFileManager
name|hm
init|=
operator|new
name|HostFileManager
argument_list|()
decl_stmt|;
name|HostSet
name|noNodes
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
name|HostSet
name|oneNode
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
name|HostSet
name|twoNodes
init|=
operator|new
name|HostSet
argument_list|()
decl_stmt|;
name|DatanodeRegistration
name|dr1
init|=
operator|new
name|DatanodeRegistration
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|"someStorageID-123"
argument_list|,
literal|12345
argument_list|,
literal|12345
argument_list|,
literal|12345
argument_list|,
literal|12345
argument_list|)
argument_list|,
operator|new
name|StorageInfo
argument_list|(
name|HdfsServerConstants
operator|.
name|NodeType
operator|.
name|DATA_NODE
argument_list|)
argument_list|,
operator|new
name|ExportedBlockKeys
argument_list|()
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|DatanodeRegistration
name|dr2
init|=
operator|new
name|DatanodeRegistration
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|"127.0.0.1"
argument_list|,
literal|"127.0.0.1"
argument_list|,
literal|"someStorageID-234"
argument_list|,
literal|23456
argument_list|,
literal|23456
argument_list|,
literal|23456
argument_list|,
literal|23456
argument_list|)
argument_list|,
operator|new
name|StorageInfo
argument_list|(
name|HdfsServerConstants
operator|.
name|NodeType
operator|.
name|DATA_NODE
argument_list|)
argument_list|,
operator|new
name|ExportedBlockKeys
argument_list|()
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|twoNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:12345"
argument_list|)
argument_list|)
expr_stmt|;
name|twoNodes
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:23456"
argument_list|)
argument_list|)
expr_stmt|;
name|oneNode
operator|.
name|add
argument_list|(
name|entry
argument_list|(
literal|"127.0.0.1:23456"
argument_list|)
argument_list|)
expr_stmt|;
name|hm
operator|.
name|refresh
argument_list|(
name|twoNodes
argument_list|,
name|noNodes
argument_list|)
expr_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|dm
argument_list|,
literal|"hostConfigManager"
argument_list|,
name|hm
argument_list|)
expr_stmt|;
comment|// Register two data nodes to simulate them coming up.
comment|// We need to add two nodes, because if we have only one node, removing it
comment|// will cause the includes list to be empty, which means all hosts will be
comment|// allowed.
name|dm
operator|.
name|registerDatanode
argument_list|(
name|dr1
argument_list|)
expr_stmt|;
name|dm
operator|.
name|registerDatanode
argument_list|(
name|dr2
argument_list|)
expr_stmt|;
comment|// Make sure that both nodes are reported
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|both
init|=
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
comment|// Sort the list so that we know which one is which
name|Collections
operator|.
name|sort
argument_list|(
name|both
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect number of hosts reported"
argument_list|,
literal|2
argument_list|,
name|both
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected host or host in unexpected position"
argument_list|,
literal|"127.0.0.1:12345"
argument_list|,
name|both
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getInfoAddr
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected host or host in unexpected position"
argument_list|,
literal|"127.0.0.1:23456"
argument_list|,
name|both
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getInfoAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove one node from includes, but do not add it to excludes.
name|hm
operator|.
name|refresh
argument_list|(
name|oneNode
argument_list|,
name|noNodes
argument_list|)
expr_stmt|;
comment|// Make sure that only one node is still reported
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|onlyOne
init|=
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect number of hosts reported"
argument_list|,
literal|1
argument_list|,
name|onlyOne
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected host reported"
argument_list|,
literal|"127.0.0.1:23456"
argument_list|,
name|onlyOne
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getInfoAddr
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove all nodes from includes
name|hm
operator|.
name|refresh
argument_list|(
name|noNodes
argument_list|,
name|noNodes
argument_list|)
expr_stmt|;
comment|// Check that both nodes are reported again
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|bothAgain
init|=
name|dm
operator|.
name|getDatanodeListForReport
argument_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
operator|.
name|ALL
argument_list|)
decl_stmt|;
comment|// Sort the list so that we know which one is which
name|Collections
operator|.
name|sort
argument_list|(
name|bothAgain
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Incorrect number of hosts reported"
argument_list|,
literal|2
argument_list|,
name|bothAgain
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected host or host in unexpected position"
argument_list|,
literal|"127.0.0.1:12345"
argument_list|,
name|bothAgain
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getInfoAddr
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Unexpected host or host in unexpected position"
argument_list|,
literal|"127.0.0.1:23456"
argument_list|,
name|bothAgain
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getInfoAddr
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

