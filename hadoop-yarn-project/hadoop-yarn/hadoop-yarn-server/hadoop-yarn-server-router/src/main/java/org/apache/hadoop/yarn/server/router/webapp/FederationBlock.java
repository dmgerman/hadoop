begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|router
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|Comparator
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
name|util
operator|.
name|StringUtils
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterInfo
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|utils
operator|.
name|FederationStateStoreFacade
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ClusterMetricsInfo
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
name|yarn
operator|.
name|server
operator|.
name|router
operator|.
name|Router
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet2
operator|.
name|Hamlet
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TABLE
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet2
operator|.
name|Hamlet
operator|.
name|TBODY
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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONJAXBContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|json
operator|.
name|JSONUnmarshaller
import|;
end_import

begin_class
DECL|class|FederationBlock
class|class
name|FederationBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|BYTES_IN_MB
specifier|private
specifier|static
specifier|final
name|long
name|BYTES_IN_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|router
specifier|private
specifier|final
name|Router
name|router
decl_stmt|;
annotation|@
name|Inject
DECL|method|FederationBlock (ViewContext ctx, Router router)
name|FederationBlock
parameter_list|(
name|ViewContext
name|ctx
parameter_list|,
name|Router
name|router
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|router
operator|=
name|router
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|public
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|Configuration
name|conf
init|=
name|this
operator|.
name|router
operator|.
name|getConfig
argument_list|()
decl_stmt|;
name|boolean
name|isEnabled
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|FEDERATION_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_FEDERATION_ENABLED
argument_list|)
decl_stmt|;
if|if
condition|(
name|isEnabled
condition|)
block|{
name|setTitle
argument_list|(
literal|"Federation"
argument_list|)
expr_stmt|;
comment|// Table header
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
name|table
argument_list|(
literal|"#rms"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
literal|".id"
argument_list|,
literal|"SubCluster"
argument_list|)
operator|.
name|th
argument_list|(
literal|".submittedA"
argument_list|,
literal|"Applications Submitted*"
argument_list|)
operator|.
name|th
argument_list|(
literal|".pendingA"
argument_list|,
literal|"Applications Pending*"
argument_list|)
operator|.
name|th
argument_list|(
literal|".runningA"
argument_list|,
literal|"Applications Running*"
argument_list|)
operator|.
name|th
argument_list|(
literal|".failedA"
argument_list|,
literal|"Applications Failed*"
argument_list|)
operator|.
name|th
argument_list|(
literal|".killedA"
argument_list|,
literal|"Applications Killed*"
argument_list|)
operator|.
name|th
argument_list|(
literal|".completedA"
argument_list|,
literal|"Applications Completed*"
argument_list|)
operator|.
name|th
argument_list|(
literal|".contAllocated"
argument_list|,
literal|"Containers Allocated"
argument_list|)
operator|.
name|th
argument_list|(
literal|".contReserved"
argument_list|,
literal|"Containers Reserved"
argument_list|)
operator|.
name|th
argument_list|(
literal|".contPending"
argument_list|,
literal|"Containers Pending"
argument_list|)
operator|.
name|th
argument_list|(
literal|".availableM"
argument_list|,
literal|"Available Memory"
argument_list|)
operator|.
name|th
argument_list|(
literal|".allocatedM"
argument_list|,
literal|"Allocated Memory"
argument_list|)
operator|.
name|th
argument_list|(
literal|".reservedM"
argument_list|,
literal|"Reserved Memory"
argument_list|)
operator|.
name|th
argument_list|(
literal|".totalM"
argument_list|,
literal|"Total Memory"
argument_list|)
operator|.
name|th
argument_list|(
literal|".availableVC"
argument_list|,
literal|"Available VirtualCores"
argument_list|)
operator|.
name|th
argument_list|(
literal|".allocatedVC"
argument_list|,
literal|"Allocated VirtualCores"
argument_list|)
operator|.
name|th
argument_list|(
literal|".reservedVC"
argument_list|,
literal|"Reserved VirtualCores"
argument_list|)
operator|.
name|th
argument_list|(
literal|".totalVC"
argument_list|,
literal|"Total VirtualCores"
argument_list|)
operator|.
name|th
argument_list|(
literal|".activeN"
argument_list|,
literal|"Active Nodes"
argument_list|)
operator|.
name|th
argument_list|(
literal|".lostN"
argument_list|,
literal|"Lost Nodes"
argument_list|)
operator|.
name|th
argument_list|(
literal|".availableN"
argument_list|,
literal|"Available Nodes"
argument_list|)
operator|.
name|th
argument_list|(
literal|".unhealtyN"
argument_list|,
literal|"Unhealthy Nodes"
argument_list|)
operator|.
name|th
argument_list|(
literal|".rebootedN"
argument_list|,
literal|"Rebooted Nodes"
argument_list|)
operator|.
name|th
argument_list|(
literal|".totalN"
argument_list|,
literal|"Total Nodes"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
try|try
block|{
comment|// Binding to the FederationStateStore
name|FederationStateStoreFacade
name|facade
init|=
name|FederationStateStoreFacade
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|SubClusterId
argument_list|,
name|SubClusterInfo
argument_list|>
name|subClustersInfo
init|=
name|facade
operator|.
name|getSubClusters
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|// Sort the SubClusters
name|List
argument_list|<
name|SubClusterInfo
argument_list|>
name|subclusters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|subclusters
operator|.
name|addAll
argument_list|(
name|subClustersInfo
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|Comparator
argument_list|<
name|?
super|super
name|SubClusterInfo
argument_list|>
name|cmp
init|=
operator|new
name|Comparator
argument_list|<
name|SubClusterInfo
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|SubClusterInfo
name|o1
parameter_list|,
name|SubClusterInfo
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|getSubClusterId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|getSubClusterId
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|subclusters
argument_list|,
name|cmp
argument_list|)
expr_stmt|;
for|for
control|(
name|SubClusterInfo
name|subcluster
range|:
name|subclusters
control|)
block|{
name|SubClusterId
name|subClusterId
init|=
name|subcluster
operator|.
name|getSubClusterId
argument_list|()
decl_stmt|;
name|String
name|webAppAddress
init|=
name|subcluster
operator|.
name|getRMWebServiceAddress
argument_list|()
decl_stmt|;
name|String
name|capability
init|=
name|subcluster
operator|.
name|getCapability
argument_list|()
decl_stmt|;
name|ClusterMetricsInfo
name|subClusterInfo
init|=
name|getClusterMetricsInfo
argument_list|(
name|capability
argument_list|)
decl_stmt|;
comment|// Building row per SubCluster
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|"//"
operator|+
name|webAppAddress
argument_list|,
name|subClusterId
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAppsSubmitted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAppsPending
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAppsRunning
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAppsFailed
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAppsKilled
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAppsCompleted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getContainersAllocated
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getReservedContainers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getPendingContainers
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|subClusterInfo
operator|.
name|getAvailableMB
argument_list|()
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|subClusterInfo
operator|.
name|getAllocatedMB
argument_list|()
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|subClusterInfo
operator|.
name|getReservedMB
argument_list|()
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|StringUtils
operator|.
name|byteDesc
argument_list|(
name|subClusterInfo
operator|.
name|getTotalMB
argument_list|()
operator|*
name|BYTES_IN_MB
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAvailableVirtualCores
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getAllocatedVirtualCores
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getReservedVirtualCores
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getTotalVirtualCores
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getActiveNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getLostNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getDecommissionedNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getUnhealthyNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getRebootedNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterInfo
operator|.
name|getTotalNodes
argument_list|()
argument_list|)
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot render ResourceManager"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|tbody
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
operator|.
name|div
argument_list|()
operator|.
name|p
argument_list|()
operator|.
name|__
argument_list|(
literal|"*The application counts are local per subcluster"
argument_list|)
operator|.
name|__
argument_list|()
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|setTitle
argument_list|(
literal|"Federation is not Enabled!"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getClusterMetricsInfo (String capability)
specifier|private
specifier|static
name|ClusterMetricsInfo
name|getClusterMetricsInfo
parameter_list|(
name|String
name|capability
parameter_list|)
block|{
name|ClusterMetricsInfo
name|clusterMetrics
init|=
literal|null
decl_stmt|;
try|try
block|{
name|JSONJAXBContext
name|jc
init|=
operator|new
name|JSONJAXBContext
argument_list|(
name|JSONConfiguration
operator|.
name|mapped
argument_list|()
operator|.
name|rootUnwrapping
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ClusterMetricsInfo
operator|.
name|class
argument_list|)
decl_stmt|;
name|JSONUnmarshaller
name|unmarshaller
init|=
name|jc
operator|.
name|createJSONUnmarshaller
argument_list|()
decl_stmt|;
name|clusterMetrics
operator|=
name|unmarshaller
operator|.
name|unmarshalFromJSON
argument_list|(
operator|new
name|StringReader
argument_list|(
name|capability
argument_list|)
argument_list|,
name|ClusterMetricsInfo
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot parse SubCluster info"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|clusterMetrics
return|;
block|}
block|}
end_class

end_unit

