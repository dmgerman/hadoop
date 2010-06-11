begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|metrics
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|ipc
operator|.
name|Server
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
name|metrics
operator|.
name|util
operator|.
name|MBeanUtil
import|;
end_import

begin_comment
comment|/**  * This class implements the RpcMgt MBean  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|RpcMgt
class|class
name|RpcMgt
implements|implements
name|RpcMgtMBean
block|{
DECL|field|myMetrics
specifier|private
name|RpcMetrics
name|myMetrics
decl_stmt|;
DECL|field|myServer
specifier|private
name|Server
name|myServer
decl_stmt|;
DECL|field|mbeanName
specifier|private
name|ObjectName
name|mbeanName
decl_stmt|;
DECL|method|RpcMgt (final String serviceName, final String port, final RpcMetrics metrics, Server server)
name|RpcMgt
parameter_list|(
specifier|final
name|String
name|serviceName
parameter_list|,
specifier|final
name|String
name|port
parameter_list|,
specifier|final
name|RpcMetrics
name|metrics
parameter_list|,
name|Server
name|server
parameter_list|)
block|{
name|myMetrics
operator|=
name|metrics
expr_stmt|;
name|myServer
operator|=
name|server
expr_stmt|;
name|mbeanName
operator|=
name|MBeanUtil
operator|.
name|registerMBean
argument_list|(
name|serviceName
argument_list|,
literal|"RpcStatisticsForPort"
operator|+
name|port
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|mbeanName
operator|!=
literal|null
condition|)
name|MBeanUtil
operator|.
name|unregisterMBean
argument_list|(
name|mbeanName
argument_list|)
expr_stmt|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsAvgProcessingTime ()
specifier|public
name|long
name|getRpcOpsAvgProcessingTime
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcProcessingTime
operator|.
name|getPreviousIntervalAverageTime
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsAvgProcessingTimeMax ()
specifier|public
name|long
name|getRpcOpsAvgProcessingTimeMax
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcProcessingTime
operator|.
name|getMaxTime
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsAvgProcessingTimeMin ()
specifier|public
name|long
name|getRpcOpsAvgProcessingTimeMin
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcProcessingTime
operator|.
name|getMinTime
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsAvgQueueTime ()
specifier|public
name|long
name|getRpcOpsAvgQueueTime
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcQueueTime
operator|.
name|getPreviousIntervalAverageTime
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsAvgQueueTimeMax ()
specifier|public
name|long
name|getRpcOpsAvgQueueTimeMax
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcQueueTime
operator|.
name|getMaxTime
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsAvgQueueTimeMin ()
specifier|public
name|long
name|getRpcOpsAvgQueueTimeMin
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcQueueTime
operator|.
name|getMinTime
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getRpcOpsNumber ()
specifier|public
name|int
name|getRpcOpsNumber
parameter_list|()
block|{
return|return
name|myMetrics
operator|.
name|rpcProcessingTime
operator|.
name|getPreviousIntervalNumOps
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getNumOpenConnections ()
specifier|public
name|int
name|getNumOpenConnections
parameter_list|()
block|{
return|return
name|myServer
operator|.
name|getNumOpenConnections
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|getCallQueueLen ()
specifier|public
name|int
name|getCallQueueLen
parameter_list|()
block|{
return|return
name|myServer
operator|.
name|getCallQueueLen
argument_list|()
return|;
block|}
comment|/**    * @inheritDoc    */
DECL|method|resetAllMinMax ()
specifier|public
name|void
name|resetAllMinMax
parameter_list|()
block|{
name|myMetrics
operator|.
name|rpcProcessingTime
operator|.
name|resetMinMax
argument_list|()
expr_stmt|;
name|myMetrics
operator|.
name|rpcQueueTime
operator|.
name|resetMinMax
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

