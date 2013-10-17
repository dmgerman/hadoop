begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|LinkedHashMap
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
name|javax
operator|.
name|management
operator|.
name|NotCompliantMBeanException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|StandardMBean
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
name|rmnode
operator|.
name|RMNode
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
name|scheduler
operator|.
name|ResourceScheduler
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
name|scheduler
operator|.
name|SchedulerNodeReport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_comment
comment|/**  * JMX bean listing statuses of all node managers.  */
end_comment

begin_class
DECL|class|RMNMInfo
specifier|public
class|class
name|RMNMInfo
implements|implements
name|RMNMInfoBeans
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RMNMInfo
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|scheduler
specifier|private
name|ResourceScheduler
name|scheduler
decl_stmt|;
comment|/**    * Constructor for RMNMInfo registers the bean with JMX.    *     * @param rmc resource manager's context object    * @param sched resource manager's scheduler object    */
DECL|method|RMNMInfo (RMContext rmc, ResourceScheduler sched)
specifier|public
name|RMNMInfo
parameter_list|(
name|RMContext
name|rmc
parameter_list|,
name|ResourceScheduler
name|sched
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmc
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|sched
expr_stmt|;
name|StandardMBean
name|bean
decl_stmt|;
try|try
block|{
name|bean
operator|=
operator|new
name|StandardMBean
argument_list|(
name|this
argument_list|,
name|RMNMInfoBeans
operator|.
name|class
argument_list|)
expr_stmt|;
name|MBeans
operator|.
name|register
argument_list|(
literal|"ResourceManager"
argument_list|,
literal|"RMNMInfo"
argument_list|,
name|bean
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompliantMBeanException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error registering RMNMInfo MBean"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Registered RMNMInfo MBean"
argument_list|)
expr_stmt|;
block|}
DECL|class|InfoMap
specifier|static
class|class
name|InfoMap
extends|extends
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
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
block|}
comment|/**    * Implements getLiveNodeManagers()    *     * @return JSON formatted string containing statuses of all node managers    */
annotation|@
name|Override
comment|// RMNMInfoBeans
DECL|method|getLiveNodeManagers ()
specifier|public
name|String
name|getLiveNodeManagers
parameter_list|()
block|{
name|Collection
argument_list|<
name|RMNode
argument_list|>
name|nodes
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|InfoMap
argument_list|>
name|nodesInfo
init|=
operator|new
name|ArrayList
argument_list|<
name|InfoMap
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|RMNode
name|ni
range|:
name|nodes
control|)
block|{
name|SchedulerNodeReport
name|report
init|=
name|scheduler
operator|.
name|getNodeReport
argument_list|(
name|ni
operator|.
name|getNodeID
argument_list|()
argument_list|)
decl_stmt|;
name|InfoMap
name|info
init|=
operator|new
name|InfoMap
argument_list|()
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"HostName"
argument_list|,
name|ni
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"Rack"
argument_list|,
name|ni
operator|.
name|getRackName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"State"
argument_list|,
name|ni
operator|.
name|getState
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"NodeId"
argument_list|,
name|ni
operator|.
name|getNodeID
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"NodeHTTPAddress"
argument_list|,
name|ni
operator|.
name|getHttpAddress
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"LastHealthUpdate"
argument_list|,
name|ni
operator|.
name|getLastHealthReportTime
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"HealthReport"
argument_list|,
name|ni
operator|.
name|getHealthReport
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"NodeManagerVersion"
argument_list|,
name|ni
operator|.
name|getNodeManagerVersion
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
block|{
name|info
operator|.
name|put
argument_list|(
literal|"NumContainers"
argument_list|,
name|report
operator|.
name|getNumContainers
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"UsedMemoryMB"
argument_list|,
name|report
operator|.
name|getUsedResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"AvailableMemoryMB"
argument_list|,
name|report
operator|.
name|getAvailableResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodesInfo
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|JSON
operator|.
name|toString
argument_list|(
name|nodesInfo
argument_list|)
return|;
block|}
block|}
end_class

end_unit

