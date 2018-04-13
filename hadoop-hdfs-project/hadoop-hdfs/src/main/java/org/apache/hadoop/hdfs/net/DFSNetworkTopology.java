begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.net
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|net
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|server
operator|.
name|blockmanagement
operator|.
name|DatanodeDescriptor
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
name|net
operator|.
name|NodeBase
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
name|Random
import|;
end_import

begin_comment
comment|/**  * The HDFS specific network topology class. The main purpose of doing this  * subclassing is to add storage-type-aware chooseRandom method. All the  * remaining parts should be the same.  *  * Currently a placeholder to test storage type info.  */
end_comment

begin_class
DECL|class|DFSNetworkTopology
specifier|public
class|class
name|DFSNetworkTopology
extends|extends
name|NetworkTopology
block|{
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|method|getInstance (Configuration conf)
specifier|public
specifier|static
name|DFSNetworkTopology
name|getInstance
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|DFSNetworkTopology
name|nt
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NET_TOPOLOGY_IMPL_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NET_TOPOLOGY_IMPL_DEFAULT
argument_list|,
name|DFSNetworkTopology
operator|.
name|class
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
operator|(
name|DFSNetworkTopology
operator|)
name|nt
operator|.
name|init
argument_list|(
name|DFSTopologyNodeImpl
operator|.
name|FACTORY
argument_list|)
return|;
block|}
comment|/**    * Randomly choose one node from<i>scope</i>, with specified storage type.    *    * If scope starts with ~, choose one from the all nodes except for the    * ones in<i>scope</i>; otherwise, choose one from<i>scope</i>.    * If excludedNodes is given, choose a node that's not in excludedNodes.    *    * @param scope range of nodes from which a node will be chosen    * @param excludedNodes nodes to be excluded from    * @param type the storage type we search for    * @return the chosen node    */
DECL|method|chooseRandomWithStorageType (final String scope, final Collection<Node> excludedNodes, StorageType type)
specifier|public
name|Node
name|chooseRandomWithStorageType
parameter_list|(
specifier|final
name|String
name|scope
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|scope
operator|.
name|startsWith
argument_list|(
literal|"~"
argument_list|)
condition|)
block|{
return|return
name|chooseRandomWithStorageType
argument_list|(
name|NodeBase
operator|.
name|ROOT
argument_list|,
name|scope
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
name|excludedNodes
argument_list|,
name|type
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|chooseRandomWithStorageType
argument_list|(
name|scope
argument_list|,
literal|null
argument_list|,
name|excludedNodes
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Randomly choose one node from<i>scope</i> with the given storage type.    *    * If scope starts with ~, choose one from the all nodes except for the    * ones in<i>scope</i>; otherwise, choose one from<i>scope</i>.    * If excludedNodes is given, choose a node that's not in excludedNodes.    *    * This call would make up to two calls. It first tries to get a random node    * (with old method) and check if it satisfies. If yes, simply return it.    * Otherwise, it make a second call (with the new method) by passing in a    * storage type.    *    * This is for better performance reason. Put in short, the key note is that    * the old method is faster but may take several runs, while the new method    * is somewhat slower, and always succeed in one trial.    * See HDFS-11535 for more detail.    *    * @param scope range of nodes from which a node will be chosen    * @param excludedNodes nodes to be excluded from    * @param type the storage type we search for    * @return the chosen node    */
DECL|method|chooseRandomWithStorageTypeTwoTrial (final String scope, final Collection<Node> excludedNodes, StorageType type)
specifier|public
name|Node
name|chooseRandomWithStorageTypeTwoTrial
parameter_list|(
specifier|final
name|String
name|scope
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|String
name|searchScope
decl_stmt|;
name|String
name|excludedScope
decl_stmt|;
if|if
condition|(
name|scope
operator|.
name|startsWith
argument_list|(
literal|"~"
argument_list|)
condition|)
block|{
name|searchScope
operator|=
name|NodeBase
operator|.
name|ROOT
expr_stmt|;
name|excludedScope
operator|=
name|scope
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|searchScope
operator|=
name|scope
expr_stmt|;
name|excludedScope
operator|=
literal|null
expr_stmt|;
block|}
comment|// next do a two-trial search
comment|// first trial, call the old method, inherited from NetworkTopology
name|Node
name|n
init|=
name|chooseRandom
argument_list|(
name|searchScope
argument_list|,
name|excludedScope
argument_list|,
name|excludedNodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No node to choose."
argument_list|)
expr_stmt|;
block|}
comment|// this means there is simply no node to choose from
return|return
literal|null
return|;
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|n
operator|instanceof
name|DatanodeDescriptor
argument_list|)
expr_stmt|;
name|DatanodeDescriptor
name|dnDescriptor
init|=
operator|(
name|DatanodeDescriptor
operator|)
name|n
decl_stmt|;
if|if
condition|(
name|dnDescriptor
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|// the first trial succeeded, just return
return|return
name|dnDescriptor
return|;
block|}
else|else
block|{
comment|// otherwise, make the second trial by calling the new method
name|LOG
operator|.
name|debug
argument_list|(
literal|"First trial failed, node has no type {}, "
operator|+
literal|"making second trial carrying this type"
argument_list|,
name|type
argument_list|)
expr_stmt|;
return|return
name|chooseRandomWithStorageType
argument_list|(
name|searchScope
argument_list|,
name|excludedScope
argument_list|,
name|excludedNodes
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
finally|finally
block|{
name|netlock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Choose a random node based on given scope, excludedScope and excludedNodes    * set. Although in general the topology has at most three layers, this class    * will not impose such assumption.    *    * At high level, the idea is like this, say:    *    * R has two children A and B, and storage type is X, say:    * A has X = 6 (rooted at A there are 6 datanodes with X) and B has X = 8.    *    * Then R will generate a random int between 1~14, if it's<= 6, recursively    * call into A, otherwise B. This will maintain a uniformed randomness of    * choosing datanodes.    *    * The tricky part is how to handle excludes.    *    * For excludedNodes, since this set is small: currently the main reason of    * being an excluded node is because it already has a replica. So randomly    * picking up this node again should be rare. Thus we only check that, if the    * chosen node is excluded, we do chooseRandom again.    *    * For excludedScope, we locate the root of the excluded scope. Subtracting    * all it's ancestors' storage counters accordingly, this way the excluded    * root is out of the picture.    *    * @param scope the scope where we look for node.    * @param excludedScope the scope where the node must NOT be from.    * @param excludedNodes the returned node must not be in this set    * @return a node with required storage type    */
annotation|@
name|VisibleForTesting
DECL|method|chooseRandomWithStorageType (final String scope, String excludedScope, final Collection<Node> excludedNodes, StorageType type)
name|Node
name|chooseRandomWithStorageType
parameter_list|(
specifier|final
name|String
name|scope
parameter_list|,
name|String
name|excludedScope
parameter_list|,
specifier|final
name|Collection
argument_list|<
name|Node
argument_list|>
name|excludedNodes
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
if|if
condition|(
name|excludedScope
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|scope
operator|.
name|startsWith
argument_list|(
name|excludedScope
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|excludedScope
operator|.
name|startsWith
argument_list|(
name|scope
argument_list|)
condition|)
block|{
name|excludedScope
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|Node
name|node
init|=
name|getNode
argument_list|(
name|scope
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Invalid scope {}, non-existing node"
argument_list|,
name|scope
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|DFSTopologyNodeImpl
operator|)
condition|)
block|{
comment|// a node is either DFSTopologyNodeImpl, or a DatanodeDescriptor
return|return
operator|(
operator|(
name|DatanodeDescriptor
operator|)
name|node
operator|)
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|?
name|node
else|:
literal|null
return|;
block|}
name|DFSTopologyNodeImpl
name|root
init|=
operator|(
name|DFSTopologyNodeImpl
operator|)
name|node
decl_stmt|;
name|Node
name|excludeRoot
init|=
name|excludedScope
operator|==
literal|null
condition|?
literal|null
else|:
name|getNode
argument_list|(
name|excludedScope
argument_list|)
decl_stmt|;
comment|// check to see if there are nodes satisfying the condition at all
name|int
name|availableCount
init|=
name|root
operator|.
name|getSubtreeStorageCount
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludeRoot
operator|!=
literal|null
operator|&&
name|root
operator|.
name|isAncestor
argument_list|(
name|excludeRoot
argument_list|)
condition|)
block|{
if|if
condition|(
name|excludeRoot
operator|instanceof
name|DFSTopologyNodeImpl
condition|)
block|{
name|availableCount
operator|-=
operator|(
operator|(
name|DFSTopologyNodeImpl
operator|)
name|excludeRoot
operator|)
operator|.
name|getSubtreeStorageCount
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|availableCount
operator|-=
operator|(
operator|(
name|DatanodeDescriptor
operator|)
name|excludeRoot
operator|)
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
block|}
if|if
condition|(
name|excludedNodes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Node
name|excludedNode
range|:
name|excludedNodes
control|)
block|{
if|if
condition|(
name|excludedNode
operator|instanceof
name|DatanodeDescriptor
condition|)
block|{
name|availableCount
operator|-=
operator|(
operator|(
name|DatanodeDescriptor
operator|)
name|excludedNode
operator|)
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|excludedNode
operator|instanceof
name|DFSTopologyNodeImpl
condition|)
block|{
name|availableCount
operator|-=
operator|(
operator|(
name|DFSTopologyNodeImpl
operator|)
name|excludedNode
operator|)
operator|.
name|getSubtreeStorageCount
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|excludedNode
operator|instanceof
name|DatanodeInfo
condition|)
block|{
comment|// find out the corresponding DatanodeDescriptor object, beacuse
comment|// we need to get its storage type info.
comment|// could be expensive operation, fortunately the size of excluded
comment|// nodes set is supposed to be very small.
name|String
name|nodeLocation
init|=
name|excludedNode
operator|.
name|getNetworkLocation
argument_list|()
operator|+
literal|"/"
operator|+
name|excludedNode
operator|.
name|getName
argument_list|()
decl_stmt|;
name|DatanodeDescriptor
name|dn
init|=
operator|(
name|DatanodeDescriptor
operator|)
name|getNode
argument_list|(
name|nodeLocation
argument_list|)
decl_stmt|;
name|availableCount
operator|-=
name|dn
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unexpected node type: {}."
argument_list|,
name|excludedNode
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|availableCount
operator|<=
literal|0
condition|)
block|{
comment|// should never be<0 in general, adding<0 check for safety purpose
return|return
literal|null
return|;
block|}
comment|// to this point, it is guaranteed that there is at least one node
comment|// that satisfies the requirement, keep trying until we found one.
name|Node
name|chosen
decl_stmt|;
do|do
block|{
name|chosen
operator|=
name|chooseRandomWithStorageTypeAndExcludeRoot
argument_list|(
name|root
argument_list|,
name|excludeRoot
argument_list|,
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|excludedNodes
operator|==
literal|null
operator|||
operator|!
name|excludedNodes
operator|.
name|contains
argument_list|(
name|chosen
argument_list|)
condition|)
block|{
break|break;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Node {} is excluded, continuing."
argument_list|,
name|chosen
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"chooseRandom returning {}"
argument_list|,
name|chosen
argument_list|)
expr_stmt|;
return|return
name|chosen
return|;
block|}
comment|/**    * Choose a random node that has the required storage type, under the given    * root, with an excluded subtree root (could also just be a leaf node).    *    * Note that excludedNode is checked after a random node, so it is not being    * handled here.    *    * @param root the root node where we start searching for a datanode    * @param excludeRoot the root of the subtree what should be excluded    * @param type the expected storage type    * @return a random datanode, with the storage type, and is not in excluded    * scope    */
DECL|method|chooseRandomWithStorageTypeAndExcludeRoot ( DFSTopologyNodeImpl root, Node excludeRoot, StorageType type)
specifier|private
name|Node
name|chooseRandomWithStorageTypeAndExcludeRoot
parameter_list|(
name|DFSTopologyNodeImpl
name|root
parameter_list|,
name|Node
name|excludeRoot
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
name|Node
name|chosenNode
decl_stmt|;
if|if
condition|(
name|root
operator|.
name|isRack
argument_list|()
condition|)
block|{
comment|// children are datanode descriptor
name|ArrayList
argument_list|<
name|Node
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|root
operator|.
name|getChildren
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
name|excludeRoot
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|DatanodeDescriptor
name|dnDescriptor
init|=
operator|(
name|DatanodeDescriptor
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|dnDescriptor
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|candidates
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|candidates
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// to this point, all nodes in candidates are valid choices, and they are
comment|// all datanodes, pick a random one.
name|chosenNode
operator|=
name|candidates
operator|.
name|get
argument_list|(
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the children are inner nodes
name|ArrayList
argument_list|<
name|DFSTopologyNodeImpl
argument_list|>
name|candidates
init|=
name|getEligibleChildren
argument_list|(
name|root
argument_list|,
name|excludeRoot
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|candidates
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// again, all children are also inner nodes, we can do this cast.
comment|// to maintain uniformality, the search needs to be based on the counts
comment|// of valid datanodes. Below is a random weighted choose.
name|int
name|totalCounts
init|=
literal|0
decl_stmt|;
name|int
index|[]
name|countArray
init|=
operator|new
name|int
index|[
name|candidates
operator|.
name|size
argument_list|()
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
name|candidates
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|DFSTopologyNodeImpl
name|innerNode
init|=
name|candidates
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|subTreeCount
init|=
name|innerNode
operator|.
name|getSubtreeStorageCount
argument_list|(
name|type
argument_list|)
decl_stmt|;
name|totalCounts
operator|+=
name|subTreeCount
expr_stmt|;
name|countArray
index|[
name|i
index|]
operator|=
name|subTreeCount
expr_stmt|;
block|}
comment|// generate a random val between [1, totalCounts]
name|int
name|randomCounts
init|=
name|RANDOM
operator|.
name|nextInt
argument_list|(
name|totalCounts
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|idxChosen
init|=
literal|0
decl_stmt|;
comment|// searching for the idxChosen can potentially be done with binary
comment|// search, but does not seem to worth it here.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|countArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|randomCounts
operator|<=
name|countArray
index|[
name|i
index|]
condition|)
block|{
name|idxChosen
operator|=
name|i
expr_stmt|;
break|break;
block|}
name|randomCounts
operator|-=
name|countArray
index|[
name|i
index|]
expr_stmt|;
block|}
name|DFSTopologyNodeImpl
name|nextRoot
init|=
name|candidates
operator|.
name|get
argument_list|(
name|idxChosen
argument_list|)
decl_stmt|;
name|chosenNode
operator|=
name|chooseRandomWithStorageTypeAndExcludeRoot
argument_list|(
name|nextRoot
argument_list|,
name|excludeRoot
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
name|chosenNode
return|;
block|}
comment|/**    * Given root, excluded root and storage type. Find all the children of the    * root, that has the storage type available. One check is that if the    * excluded root is under a children, this children must subtract the storage    * count of the excluded root.    * @param root the subtree root we check.    * @param excludeRoot the root of the subtree that should be excluded.    * @param type the storage type we look for.    * @return a list of possible nodes, each of them is eligible as the next    * level root we search.    */
DECL|method|getEligibleChildren ( DFSTopologyNodeImpl root, Node excludeRoot, StorageType type)
specifier|private
name|ArrayList
argument_list|<
name|DFSTopologyNodeImpl
argument_list|>
name|getEligibleChildren
parameter_list|(
name|DFSTopologyNodeImpl
name|root
parameter_list|,
name|Node
name|excludeRoot
parameter_list|,
name|StorageType
name|type
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|DFSTopologyNodeImpl
argument_list|>
name|candidates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|excludeCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|excludeRoot
operator|!=
literal|null
operator|&&
name|root
operator|.
name|isAncestor
argument_list|(
name|excludeRoot
argument_list|)
condition|)
block|{
comment|// the subtree to be excluded is under the given root,
comment|// find out the number of nodes to be excluded.
if|if
condition|(
name|excludeRoot
operator|instanceof
name|DFSTopologyNodeImpl
condition|)
block|{
comment|// if excludedRoot is an inner node, get the counts of all nodes on
comment|// this subtree of that storage type.
name|excludeCount
operator|=
operator|(
operator|(
name|DFSTopologyNodeImpl
operator|)
name|excludeRoot
operator|)
operator|.
name|getSubtreeStorageCount
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// if excludedRoot is a datanode, simply ignore this one node
if|if
condition|(
operator|(
operator|(
name|DatanodeDescriptor
operator|)
name|excludeRoot
operator|)
operator|.
name|hasStorageType
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|excludeCount
operator|=
literal|1
expr_stmt|;
block|}
block|}
block|}
comment|// have calculated the number of storage counts to be excluded.
comment|// walk through all children to check eligibility.
for|for
control|(
name|Node
name|node
range|:
name|root
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|DFSTopologyNodeImpl
name|dfsNode
init|=
operator|(
name|DFSTopologyNodeImpl
operator|)
name|node
decl_stmt|;
name|int
name|storageCount
init|=
name|dfsNode
operator|.
name|getSubtreeStorageCount
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|excludeRoot
operator|!=
literal|null
operator|&&
name|excludeCount
operator|!=
literal|0
operator|&&
operator|(
name|dfsNode
operator|.
name|isAncestor
argument_list|(
name|excludeRoot
argument_list|)
operator|||
name|dfsNode
operator|.
name|equals
argument_list|(
name|excludeRoot
argument_list|)
operator|)
condition|)
block|{
name|storageCount
operator|-=
name|excludeCount
expr_stmt|;
block|}
if|if
condition|(
name|storageCount
operator|>
literal|0
condition|)
block|{
name|candidates
operator|.
name|add
argument_list|(
name|dfsNode
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|candidates
return|;
block|}
block|}
end_class

end_unit

