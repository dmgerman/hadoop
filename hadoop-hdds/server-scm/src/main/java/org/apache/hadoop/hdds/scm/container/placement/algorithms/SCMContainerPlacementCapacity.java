begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.container.placement.algorithms
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
name|container
operator|.
name|placement
operator|.
name|algorithms
package|;
end_package

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
name|scm
operator|.
name|container
operator|.
name|placement
operator|.
name|metrics
operator|.
name|SCMNodeMetric
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
name|exceptions
operator|.
name|SCMException
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
name|node
operator|.
name|NodeManager
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
name|annotations
operator|.
name|VisibleForTesting
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

begin_comment
comment|/**  * Container placement policy that randomly choose datanodes with remaining  * space to satisfy the size constraints.  *<p>  * The Algorithm is as follows, Pick 2 random nodes from a given pool of nodes  * and then pick the node which lower utilization. This leads to a higher  * probability of nodes with lower utilization to be picked.  *<p>  * For those wondering why we choose two nodes randomly and choose the node  * with lower utilization. There are links to this original papers in  * HDFS-11564.  *<p>  * A brief summary -- We treat the nodes from a scale of lowest utilized to  * highest utilized, there are (s * ( s + 1)) / 2 possibilities to build  * distinct pairs of nodes.  There are s - k pairs of nodes in which the rank  * k node is less than the couple. So probability of a picking a node is  * (2 * (s -k)) / (s * (s - 1)).  *<p>  * In English, There is a much higher probability of picking less utilized nodes  * as compared to nodes with higher utilization since we pick 2 nodes and  * then pick the node with lower utilization.  *<p>  * This avoids the issue of users adding new nodes into the cluster and HDFS  * sending all traffic to those nodes if we only use a capacity based  * allocation scheme. Unless those nodes are part of the set of the first 2  * nodes then newer nodes will not be in the running to get the container.  *<p>  * This leads to an I/O pattern where the lower utilized nodes are favoured  * more than higher utilized nodes, but part of the I/O will still go to the  * older higher utilized nodes.  *<p>  * With this algorithm in place, our hope is that balancer tool needs to do  * little or no work and the cluster will achieve a balanced distribution  * over time.  */
end_comment

begin_class
DECL|class|SCMContainerPlacementCapacity
specifier|public
specifier|final
class|class
name|SCMContainerPlacementCapacity
extends|extends
name|SCMCommonPolicy
block|{
annotation|@
name|VisibleForTesting
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SCMContainerPlacementCapacity
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Constructs a Container Placement with considering only capacity.    * That is this policy tries to place containers based on node weight.    *    * @param nodeManager Node Manager    * @param conf Configuration    */
DECL|method|SCMContainerPlacementCapacity (final NodeManager nodeManager, final Configuration conf)
specifier|public
name|SCMContainerPlacementCapacity
parameter_list|(
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Called by SCM to choose datanodes.    *    *    * @param excludedNodes - list of the datanodes to exclude.    * @param favoredNodes - list of nodes preferred.    * @param nodesRequired - number of datanodes required.    * @param sizeRequired - size required for the container or block.    * @return List of datanodes.    * @throws SCMException  SCMException    */
annotation|@
name|Override
DECL|method|chooseDatanodes ( List<DatanodeDetails> excludedNodes, List<DatanodeDetails> favoredNodes, final int nodesRequired, final long sizeRequired)
specifier|public
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|chooseDatanodes
parameter_list|(
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|excludedNodes
parameter_list|,
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|favoredNodes
parameter_list|,
specifier|final
name|int
name|nodesRequired
parameter_list|,
specifier|final
name|long
name|sizeRequired
parameter_list|)
throws|throws
name|SCMException
block|{
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|healthyNodes
init|=
name|super
operator|.
name|chooseDatanodes
argument_list|(
name|excludedNodes
argument_list|,
name|favoredNodes
argument_list|,
name|nodesRequired
argument_list|,
name|sizeRequired
argument_list|)
decl_stmt|;
if|if
condition|(
name|healthyNodes
operator|.
name|size
argument_list|()
operator|==
name|nodesRequired
condition|)
block|{
return|return
name|healthyNodes
return|;
block|}
return|return
name|getResultSet
argument_list|(
name|nodesRequired
argument_list|,
name|healthyNodes
argument_list|)
return|;
block|}
comment|/**    * Find a node from the healthy list and return it after removing it from the    * list that we are operating on.    *    * @param healthyNodes - List of healthy nodes that meet the size    * requirement.    * @return DatanodeDetails that is chosen.    */
annotation|@
name|Override
DECL|method|chooseNode (List<DatanodeDetails> healthyNodes)
specifier|public
name|DatanodeDetails
name|chooseNode
parameter_list|(
name|List
argument_list|<
name|DatanodeDetails
argument_list|>
name|healthyNodes
parameter_list|)
block|{
name|int
name|firstNodeNdx
init|=
name|getRand
argument_list|()
operator|.
name|nextInt
argument_list|(
name|healthyNodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|secondNodeNdx
init|=
name|getRand
argument_list|()
operator|.
name|nextInt
argument_list|(
name|healthyNodes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|DatanodeDetails
name|datanodeDetails
decl_stmt|;
comment|// There is a possibility that both numbers will be same.
comment|// if that is so, we just return the node.
if|if
condition|(
name|firstNodeNdx
operator|==
name|secondNodeNdx
condition|)
block|{
name|datanodeDetails
operator|=
name|healthyNodes
operator|.
name|get
argument_list|(
name|firstNodeNdx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|DatanodeDetails
name|firstNodeDetails
init|=
name|healthyNodes
operator|.
name|get
argument_list|(
name|firstNodeNdx
argument_list|)
decl_stmt|;
name|DatanodeDetails
name|secondNodeDetails
init|=
name|healthyNodes
operator|.
name|get
argument_list|(
name|secondNodeNdx
argument_list|)
decl_stmt|;
name|SCMNodeMetric
name|firstNodeMetric
init|=
name|getNodeManager
argument_list|()
operator|.
name|getNodeStat
argument_list|(
name|firstNodeDetails
argument_list|)
decl_stmt|;
name|SCMNodeMetric
name|secondNodeMetric
init|=
name|getNodeManager
argument_list|()
operator|.
name|getNodeStat
argument_list|(
name|secondNodeDetails
argument_list|)
decl_stmt|;
name|datanodeDetails
operator|=
name|firstNodeMetric
operator|.
name|isGreater
argument_list|(
name|secondNodeMetric
operator|.
name|get
argument_list|()
argument_list|)
condition|?
name|firstNodeDetails
else|:
name|secondNodeDetails
expr_stmt|;
block|}
name|healthyNodes
operator|.
name|remove
argument_list|(
name|datanodeDetails
argument_list|)
expr_stmt|;
return|return
name|datanodeDetails
return|;
block|}
block|}
end_class

end_unit

