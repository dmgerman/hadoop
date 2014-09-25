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
name|Collection
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
name|Set
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
name|classification
operator|.
name|InterfaceAudience
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
name|protocol
operator|.
name|BlockStoragePolicy
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
name|protocol
operator|.
name|Block
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
name|FSClusterStats
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
name|NetworkTopology
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
name|Node
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
name|ReflectionUtils
import|;
end_import

begin_comment
comment|/**   * This interface is used for choosing the desired number of targets  * for placing block replicas.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BlockPlacementPolicy
specifier|public
specifier|abstract
class|class
name|BlockPlacementPolicy
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|NotEnoughReplicasException
specifier|public
specifier|static
class|class
name|NotEnoughReplicasException
extends|extends
name|Exception
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|NotEnoughReplicasException (String msg)
name|NotEnoughReplicasException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * choose<i>numOfReplicas</i> data nodes for<i>writer</i>     * to re-replicate a block with size<i>blocksize</i>     * If not, return as many as we can.    *    * @param srcPath the file to which this chooseTargets is being invoked.    * @param numOfReplicas additional number of replicas wanted.    * @param writer the writer's machine, null if not in the cluster.    * @param chosen datanodes that have been chosen as targets.    * @param returnChosenNodes decide if the chosenNodes are returned.    * @param excludedNodes datanodes that should not be considered as targets.    * @param blocksize size of the data to be written.    * @return array of DatanodeDescriptor instances chosen as target    * and sorted as a pipeline.    */
DECL|method|chooseTarget (String srcPath, int numOfReplicas, Node writer, List<DatanodeStorageInfo> chosen, boolean returnChosenNodes, Set<Node> excludedNodes, long blocksize, BlockStoragePolicy storagePolicy)
specifier|public
specifier|abstract
name|DatanodeStorageInfo
index|[]
name|chooseTarget
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|int
name|numOfReplicas
parameter_list|,
name|Node
name|writer
parameter_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|chosen
parameter_list|,
name|boolean
name|returnChosenNodes
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|BlockStoragePolicy
name|storagePolicy
parameter_list|)
function_decl|;
comment|/**    * Same as {@link #chooseTarget(String, int, Node, Set, long, List, StorageType)}    * with added parameter {@code favoredDatanodes}    * @param favoredNodes datanodes that should be favored as targets. This    *          is only a hint and due to cluster state, namenode may not be     *          able to place the blocks on these datanodes.    */
DECL|method|chooseTarget (String src, int numOfReplicas, Node writer, Set<Node> excludedNodes, long blocksize, List<DatanodeDescriptor> favoredNodes, BlockStoragePolicy storagePolicy)
name|DatanodeStorageInfo
index|[]
name|chooseTarget
parameter_list|(
name|String
name|src
parameter_list|,
name|int
name|numOfReplicas
parameter_list|,
name|Node
name|writer
parameter_list|,
name|Set
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|long
name|blocksize
parameter_list|,
name|List
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|favoredNodes
parameter_list|,
name|BlockStoragePolicy
name|storagePolicy
parameter_list|)
block|{
comment|// This class does not provide the functionality of placing
comment|// a block in favored datanodes. The implementations of this class
comment|// are expected to provide this functionality
return|return
name|chooseTarget
argument_list|(
name|src
argument_list|,
name|numOfReplicas
argument_list|,
name|writer
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|(
name|numOfReplicas
argument_list|)
argument_list|,
literal|false
argument_list|,
name|excludedNodes
argument_list|,
name|blocksize
argument_list|,
name|storagePolicy
argument_list|)
return|;
block|}
comment|/**    * Verify if the block's placement meets requirement of placement policy,    * i.e. replicas are placed on no less than minRacks racks in the system.    *     * @param srcPath the full pathname of the file to be verified    * @param lBlk block with locations    * @param numOfReplicas replica number of file to be verified    * @return the result of verification    */
DECL|method|verifyBlockPlacement (String srcPath, LocatedBlock lBlk, int numOfReplicas)
specifier|abstract
specifier|public
name|BlockPlacementStatus
name|verifyBlockPlacement
parameter_list|(
name|String
name|srcPath
parameter_list|,
name|LocatedBlock
name|lBlk
parameter_list|,
name|int
name|numOfReplicas
parameter_list|)
function_decl|;
comment|/**    * Decide whether deleting the specified replica of the block still makes     * the block conform to the configured block placement policy.    *     * @param srcBC block collection of file to which block-to-be-deleted belongs    * @param block The block to be deleted    * @param replicationFactor The required number of replicas for this block    * @param moreThanOne The replica locations of this block that are present    *                    on more than one unique racks.    * @param exactlyOne Replica locations of this block that  are present    *                    on exactly one unique racks.    * @param excessTypes The excess {@link StorageType}s according to the    *                    {@link BlockStoragePolicy}.    * @return the replica that is the best candidate for deletion    */
DECL|method|chooseReplicaToDelete ( BlockCollection srcBC, Block block, short replicationFactor, Collection<DatanodeStorageInfo> moreThanOne, Collection<DatanodeStorageInfo> exactlyOne, List<StorageType> excessTypes)
specifier|abstract
specifier|public
name|DatanodeStorageInfo
name|chooseReplicaToDelete
parameter_list|(
name|BlockCollection
name|srcBC
parameter_list|,
name|Block
name|block
parameter_list|,
name|short
name|replicationFactor
parameter_list|,
name|Collection
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|moreThanOne
parameter_list|,
name|Collection
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|exactlyOne
parameter_list|,
name|List
argument_list|<
name|StorageType
argument_list|>
name|excessTypes
parameter_list|)
function_decl|;
comment|/**    * Used to setup a BlockPlacementPolicy object. This should be defined by     * all implementations of a BlockPlacementPolicy.    *     * @param conf the configuration object    * @param stats retrieve cluster status from here    * @param clusterMap cluster topology    */
DECL|method|initialize (Configuration conf, FSClusterStats stats, NetworkTopology clusterMap, Host2NodesMap host2datanodeMap)
specifier|abstract
specifier|protected
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSClusterStats
name|stats
parameter_list|,
name|NetworkTopology
name|clusterMap
parameter_list|,
name|Host2NodesMap
name|host2datanodeMap
parameter_list|)
function_decl|;
comment|/**    * Get an instance of the configured Block Placement Policy based on the    * the configuration property    * {@link  DFSConfigKeys#DFS_BLOCK_REPLICATOR_CLASSNAME_KEY}.    *     * @param conf the configuration to be used    * @param stats an object that is used to retrieve the load on the cluster    * @param clusterMap the network topology of the cluster    * @return an instance of BlockPlacementPolicy    */
DECL|method|getInstance (Configuration conf, FSClusterStats stats, NetworkTopology clusterMap, Host2NodesMap host2datanodeMap)
specifier|public
specifier|static
name|BlockPlacementPolicy
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|FSClusterStats
name|stats
parameter_list|,
name|NetworkTopology
name|clusterMap
parameter_list|,
name|Host2NodesMap
name|host2datanodeMap
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|BlockPlacementPolicy
argument_list|>
name|replicatorClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_BLOCK_REPLICATOR_CLASSNAME_DEFAULT
argument_list|,
name|BlockPlacementPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|BlockPlacementPolicy
name|replicator
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|replicatorClass
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|replicator
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
name|stats
argument_list|,
name|clusterMap
argument_list|,
name|host2datanodeMap
argument_list|)
expr_stmt|;
return|return
name|replicator
return|;
block|}
comment|/**    * Adjust rackmap, moreThanOne, and exactlyOne after removing replica on cur.    *    * @param rackMap a map from rack to replica    * @param moreThanOne The List of replica nodes on rack which has more than     *        one replica    * @param exactlyOne The List of replica nodes on rack with only one replica    * @param cur current replica to remove    */
DECL|method|adjustSetsWithChosenReplica ( final Map<String, List<DatanodeStorageInfo>> rackMap, final List<DatanodeStorageInfo> moreThanOne, final List<DatanodeStorageInfo> exactlyOne, final DatanodeStorageInfo cur)
specifier|public
name|void
name|adjustSetsWithChosenReplica
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|>
name|rackMap
parameter_list|,
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|moreThanOne
parameter_list|,
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|exactlyOne
parameter_list|,
specifier|final
name|DatanodeStorageInfo
name|cur
parameter_list|)
block|{
specifier|final
name|String
name|rack
init|=
name|getRack
argument_list|(
name|cur
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storages
init|=
name|rackMap
operator|.
name|get
argument_list|(
name|rack
argument_list|)
decl_stmt|;
name|storages
operator|.
name|remove
argument_list|(
name|cur
argument_list|)
expr_stmt|;
if|if
condition|(
name|storages
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|rackMap
operator|.
name|remove
argument_list|(
name|rack
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|moreThanOne
operator|.
name|remove
argument_list|(
name|cur
argument_list|)
condition|)
block|{
if|if
condition|(
name|storages
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|DatanodeStorageInfo
name|remaining
init|=
name|storages
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|moreThanOne
operator|.
name|remove
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
name|exactlyOne
operator|.
name|add
argument_list|(
name|remaining
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|exactlyOne
operator|.
name|remove
argument_list|(
name|cur
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get rack string from a data node    * @return rack of data node    */
DECL|method|getRack (final DatanodeInfo datanode)
specifier|protected
name|String
name|getRack
parameter_list|(
specifier|final
name|DatanodeInfo
name|datanode
parameter_list|)
block|{
return|return
name|datanode
operator|.
name|getNetworkLocation
argument_list|()
return|;
block|}
comment|/**    * Split data nodes into two sets, one set includes nodes on rack with    * more than one  replica, the other set contains the remaining nodes.    *     * @param dataNodes datanodes to be split into two sets    * @param rackMap a map from rack to datanodes    * @param moreThanOne contains nodes on rack with more than one replica    * @param exactlyOne remains contains the remaining nodes    */
DECL|method|splitNodesWithRack ( final Iterable<DatanodeStorageInfo> storages, final Map<String, List<DatanodeStorageInfo>> rackMap, final List<DatanodeStorageInfo> moreThanOne, final List<DatanodeStorageInfo> exactlyOne)
specifier|public
name|void
name|splitNodesWithRack
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storages
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|>
name|rackMap
parameter_list|,
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|moreThanOne
parameter_list|,
specifier|final
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|exactlyOne
parameter_list|)
block|{
for|for
control|(
name|DatanodeStorageInfo
name|s
range|:
name|storages
control|)
block|{
specifier|final
name|String
name|rackName
init|=
name|getRack
argument_list|(
name|s
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storageList
init|=
name|rackMap
operator|.
name|get
argument_list|(
name|rackName
argument_list|)
decl_stmt|;
if|if
condition|(
name|storageList
operator|==
literal|null
condition|)
block|{
name|storageList
operator|=
operator|new
name|ArrayList
argument_list|<
name|DatanodeStorageInfo
argument_list|>
argument_list|()
expr_stmt|;
name|rackMap
operator|.
name|put
argument_list|(
name|rackName
argument_list|,
name|storageList
argument_list|)
expr_stmt|;
block|}
name|storageList
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|// split nodes into two sets
for|for
control|(
name|List
argument_list|<
name|DatanodeStorageInfo
argument_list|>
name|storageList
range|:
name|rackMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|storageList
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// exactlyOne contains nodes on rack with only one replica
name|exactlyOne
operator|.
name|add
argument_list|(
name|storageList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// moreThanOne contains nodes on rack with more than one replica
name|moreThanOne
operator|.
name|addAll
argument_list|(
name|storageList
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

