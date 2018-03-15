begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.datamodel
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
name|diskbalancer
operator|.
name|datamodel
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectReader
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
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|diskbalancer
operator|.
name|connectors
operator|.
name|ClusterConnector
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
name|diskbalancer
operator|.
name|planner
operator|.
name|NodePlan
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
name|diskbalancer
operator|.
name|planner
operator|.
name|Planner
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
name|diskbalancer
operator|.
name|planner
operator|.
name|PlannerFactory
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
name|web
operator|.
name|JsonUtil
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Locale
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
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|HashMap
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
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|Executors
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
name|Future
import|;
end_import

begin_comment
comment|/**  * DiskBalancerCluster represents the nodes that we are working against.  *<p/>  * Please Note :  *<p/>  * Semantics of inclusionList and exclusionLists.  *<p/>  * If a non-empty inclusionList is specified then the diskBalancer assumes that  * the user is only interested in processing that list of nodes. This node list  * is checked against the exclusionList and only the nodes in inclusionList but  * not in exclusionList is processed.  *<p/>  * if inclusionList is empty, then we assume that all live nodes in the nodes is  * to be processed by diskBalancer. In that case diskBalancer will avoid any  * nodes specified in the exclusionList but will process all nodes in the  * cluster.  *<p/>  * In other words, an empty inclusionList is means all the nodes otherwise  * only a given list is processed and ExclusionList is always honored.  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
DECL|class|DiskBalancerCluster
specifier|public
class|class
name|DiskBalancerCluster
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
name|DiskBalancerCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|READER
specifier|private
specifier|static
specifier|final
name|ObjectReader
name|READER
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|readerFor
argument_list|(
name|DiskBalancerCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|exclusionList
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|exclusionList
decl_stmt|;
DECL|field|inclusionList
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|inclusionList
decl_stmt|;
DECL|field|clusterConnector
specifier|private
name|ClusterConnector
name|clusterConnector
decl_stmt|;
DECL|field|nodes
specifier|private
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodes
decl_stmt|;
DECL|field|outputpath
specifier|private
name|String
name|outputpath
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|nodesToProcess
specifier|private
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|nodesToProcess
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|ipList
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DiskBalancerDataNode
argument_list|>
name|ipList
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|hostNames
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DiskBalancerDataNode
argument_list|>
name|hostNames
decl_stmt|;
annotation|@
name|JsonIgnore
DECL|field|hostUUID
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DiskBalancerDataNode
argument_list|>
name|hostUUID
decl_stmt|;
DECL|field|threshold
specifier|private
name|float
name|threshold
decl_stmt|;
comment|/**    * Empty Constructor needed by Jackson.    */
DECL|method|DiskBalancerCluster ()
specifier|public
name|DiskBalancerCluster
parameter_list|()
block|{
name|nodes
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|exclusionList
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|inclusionList
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|ipList
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|hostNames
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|hostUUID
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs a DiskBalancerCluster.    *    * @param connector - ClusterConnector    * @throws IOException    */
DECL|method|DiskBalancerCluster (ClusterConnector connector)
specifier|public
name|DiskBalancerCluster
parameter_list|(
name|ClusterConnector
name|connector
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|connector
argument_list|)
expr_stmt|;
name|clusterConnector
operator|=
name|connector
expr_stmt|;
block|}
comment|/**    * Parses a Json string and converts to DiskBalancerCluster.    *    * @param json - Json String    * @return DiskBalancerCluster    * @throws IOException    */
DECL|method|parseJson (String json)
specifier|public
specifier|static
name|DiskBalancerCluster
name|parseJson
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|READER
operator|.
name|readValue
argument_list|(
name|json
argument_list|)
return|;
block|}
comment|/**    * readClusterInfo connects to the cluster and reads the node's data.  This    * data is used as basis of rest of computation in DiskBalancerCluster    */
DECL|method|readClusterInfo ()
specifier|public
name|void
name|readClusterInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|clusterConnector
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using connector : {}"
argument_list|,
name|clusterConnector
operator|.
name|getConnectorInfo
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|clusterConnector
operator|.
name|getNodes
argument_list|()
expr_stmt|;
for|for
control|(
name|DiskBalancerDataNode
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|node
operator|.
name|getDataNodeIP
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|node
operator|.
name|getDataNodeIP
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ipList
operator|.
name|put
argument_list|(
name|node
operator|.
name|getDataNodeIP
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|getDataNodeName
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|node
operator|.
name|getDataNodeName
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// TODO : should we support Internationalized Domain Names ?
comment|// Disk balancer assumes that host names are ascii. If not
comment|// end user can always balance the node via IP address or DataNode UUID.
name|hostNames
operator|.
name|put
argument_list|(
name|node
operator|.
name|getDataNodeName
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|getDataNodeUUID
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|node
operator|.
name|getDataNodeUUID
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|hostUUID
operator|.
name|put
argument_list|(
name|node
operator|.
name|getDataNodeUUID
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Gets all DataNodes in the Cluster.    *    * @return Array of DisKBalancerDataNodes    */
DECL|method|getNodes ()
specifier|public
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
comment|/**    * Sets the list of nodes of this cluster.    *    * @param clusterNodes List of Nodes    */
DECL|method|setNodes (List<DiskBalancerDataNode> clusterNodes)
specifier|public
name|void
name|setNodes
parameter_list|(
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|clusterNodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|clusterNodes
expr_stmt|;
block|}
comment|/**    * Returns the current ExclusionList.    *    * @return List of Nodes that are excluded from diskBalancer right now.    */
DECL|method|getExclusionList ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getExclusionList
parameter_list|()
block|{
return|return
name|exclusionList
return|;
block|}
comment|/**    * sets the list of nodes to exclude from process of diskBalancer.    *    * @param excludedNodes - exclusionList of nodes.    */
DECL|method|setExclusionList (Set<String> excludedNodes)
specifier|public
name|void
name|setExclusionList
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|excludedNodes
parameter_list|)
block|{
name|this
operator|.
name|exclusionList
operator|.
name|addAll
argument_list|(
name|excludedNodes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the threshold value. This is used for indicating how much skew is    * acceptable, This is expressed as a percentage. For example to say 20% skew    * between volumes is acceptable set this value to 20.    *    * @return float    */
DECL|method|getThreshold ()
specifier|public
name|float
name|getThreshold
parameter_list|()
block|{
return|return
name|threshold
return|;
block|}
comment|/**    * Sets the threshold value.    *    * @param thresholdPercent - float - in percentage    */
DECL|method|setThreshold (float thresholdPercent)
specifier|public
name|void
name|setThreshold
parameter_list|(
name|float
name|thresholdPercent
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|(
name|thresholdPercent
operator|>=
literal|0.0f
operator|)
operator|&&
operator|(
name|thresholdPercent
operator|<=
literal|100.0f
operator|)
argument_list|,
literal|"A percentage value expected."
argument_list|)
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
name|thresholdPercent
expr_stmt|;
block|}
comment|/**    * Gets the Inclusion list.    *    * @return List of machine to be processed by diskBalancer.    */
DECL|method|getInclusionList ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getInclusionList
parameter_list|()
block|{
return|return
name|inclusionList
return|;
block|}
comment|/**    * Sets the inclusionList.    *    * @param includeNodes - set of machines to be processed by diskBalancer.    */
DECL|method|setInclusionList (Set<String> includeNodes)
specifier|public
name|void
name|setInclusionList
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|includeNodes
parameter_list|)
block|{
name|this
operator|.
name|inclusionList
operator|.
name|addAll
argument_list|(
name|includeNodes
argument_list|)
expr_stmt|;
block|}
comment|/**    * returns a serialized json string.    *    * @return String - json    * @throws IOException    */
DECL|method|toJson ()
specifier|public
name|String
name|toJson
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|JsonUtil
operator|.
name|toJsonString
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Returns the Nodes to Process which is the real list of nodes processed by    * diskBalancer.    *    * @return List of DiskBalancerDataNodes    */
annotation|@
name|JsonIgnore
DECL|method|getNodesToProcess ()
specifier|public
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|getNodesToProcess
parameter_list|()
block|{
return|return
name|nodesToProcess
return|;
block|}
comment|/**    * Sets the nodes to process.    *    * @param dnNodesToProcess - List of DataNodes to process    */
annotation|@
name|JsonIgnore
DECL|method|setNodesToProcess (List<DiskBalancerDataNode> dnNodesToProcess)
specifier|public
name|void
name|setNodesToProcess
parameter_list|(
name|List
argument_list|<
name|DiskBalancerDataNode
argument_list|>
name|dnNodesToProcess
parameter_list|)
block|{
name|this
operator|.
name|nodesToProcess
operator|=
name|dnNodesToProcess
expr_stmt|;
block|}
comment|/**    * Returns th output path for this cluster.    */
DECL|method|getOutput ()
specifier|public
name|String
name|getOutput
parameter_list|()
block|{
return|return
name|outputpath
return|;
block|}
comment|/**    * Sets the output path for this run.    *    * @param output - Path    */
DECL|method|setOutput (String output)
specifier|public
name|void
name|setOutput
parameter_list|(
name|String
name|output
parameter_list|)
block|{
name|this
operator|.
name|outputpath
operator|=
name|output
expr_stmt|;
block|}
comment|/**    * Writes a snapshot of the cluster to the specified directory.    *    * @param snapShotName - name of the snapshot    */
DECL|method|createSnapshot (String snapShotName)
specifier|public
name|void
name|createSnapshot
parameter_list|(
name|String
name|snapShotName
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|json
init|=
name|this
operator|.
name|toJson
argument_list|()
decl_stmt|;
name|File
name|outFile
init|=
operator|new
name|File
argument_list|(
name|getOutput
argument_list|()
operator|+
literal|"/"
operator|+
name|snapShotName
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|writeStringToFile
argument_list|(
name|outFile
argument_list|,
name|json
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compute plan takes a node and constructs a planner that creates a plan that    * we would like to follow.    *<p/>    * This function creates a thread pool and executes a planner on each node    * that we are supposed to plan for. Each of these planners return a NodePlan    * that we can persist or schedule for execution with a diskBalancer    * Executor.    *    * @param thresholdPercent - in percentage    * @return list of NodePlans    */
DECL|method|computePlan (double thresholdPercent)
specifier|public
name|List
argument_list|<
name|NodePlan
argument_list|>
name|computePlan
parameter_list|(
name|double
name|thresholdPercent
parameter_list|)
block|{
name|List
argument_list|<
name|NodePlan
argument_list|>
name|planList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodesToProcess
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Nodes to process is null. No nodes processed."
argument_list|)
expr_stmt|;
return|return
name|planList
return|;
block|}
name|int
name|poolSize
init|=
name|computePoolSize
argument_list|(
name|nodesToProcess
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|poolSize
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|NodePlan
argument_list|>
argument_list|>
name|futureList
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|nodesToProcess
operator|.
name|size
argument_list|()
condition|;
name|x
operator|++
control|)
block|{
specifier|final
name|DiskBalancerDataNode
name|node
init|=
name|nodesToProcess
operator|.
name|get
argument_list|(
name|x
argument_list|)
decl_stmt|;
specifier|final
name|Planner
name|planner
init|=
name|PlannerFactory
operator|.
name|getPlanner
argument_list|(
name|PlannerFactory
operator|.
name|GREEDY_PLANNER
argument_list|,
name|node
argument_list|,
name|thresholdPercent
argument_list|)
decl_stmt|;
name|futureList
operator|.
name|add
argument_list|(
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|NodePlan
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodePlan
name|call
parameter_list|()
throws|throws
name|Exception
block|{
assert|assert
name|planner
operator|!=
literal|null
assert|;
return|return
name|planner
operator|.
name|plan
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Future
argument_list|<
name|NodePlan
argument_list|>
name|f
range|:
name|futureList
control|)
block|{
try|try
block|{
name|planList
operator|.
name|add
argument_list|(
name|f
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Compute Node plan was cancelled or interrupted : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to compute plan : "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|planList
return|;
block|}
comment|/**    * Return the number of threads we should launch for this cluster.    *<p/>    * Here is the heuristic we are using.    *<p/>    * 1 thread per 100 nodes that we want to process. Minimum nodesToProcess    * threads in the pool. Maximum 100 threads in the pool.    *<p/>    * Generally return a rounded up multiple of 10.    *    * @return number    */
DECL|method|computePoolSize (int nodeCount)
specifier|private
name|int
name|computePoolSize
parameter_list|(
name|int
name|nodeCount
parameter_list|)
block|{
if|if
condition|(
name|nodeCount
operator|<
literal|10
condition|)
block|{
return|return
name|nodeCount
return|;
block|}
name|int
name|threadRatio
init|=
name|nodeCount
operator|/
literal|100
decl_stmt|;
name|int
name|modValue
init|=
name|threadRatio
operator|%
literal|10
decl_stmt|;
if|if
condition|(
operator|(
operator|(
literal|10
operator|-
name|modValue
operator|)
operator|+
name|threadRatio
operator|)
operator|>
literal|100
condition|)
block|{
return|return
literal|100
return|;
block|}
else|else
block|{
return|return
operator|(
literal|10
operator|-
name|modValue
operator|)
operator|+
name|threadRatio
return|;
block|}
block|}
comment|/**    * Returns a node by UUID.    * @param uuid - Node's UUID    * @return DiskBalancerDataNode.    */
DECL|method|getNodeByUUID (String uuid)
specifier|public
name|DiskBalancerDataNode
name|getNodeByUUID
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
return|return
name|hostUUID
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
return|;
block|}
comment|/**    * Returns a node by IP Address.    * @param ipAddresss - IP address String.    * @return DiskBalancerDataNode.    */
DECL|method|getNodeByIPAddress (String ipAddresss)
specifier|public
name|DiskBalancerDataNode
name|getNodeByIPAddress
parameter_list|(
name|String
name|ipAddresss
parameter_list|)
block|{
return|return
name|ipList
operator|.
name|get
argument_list|(
name|ipAddresss
argument_list|)
return|;
block|}
comment|/**    * Returns a node by hostName.    * @param hostName - HostName.    * @return DiskBalancerDataNode.    */
DECL|method|getNodeByName (String hostName)
specifier|public
name|DiskBalancerDataNode
name|getNodeByName
parameter_list|(
name|String
name|hostName
parameter_list|)
block|{
return|return
name|hostNames
operator|.
name|get
argument_list|(
name|hostName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

