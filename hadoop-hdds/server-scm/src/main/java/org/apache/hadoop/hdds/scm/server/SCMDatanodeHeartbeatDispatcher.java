begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
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
name|server
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
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|ContainerReportsProto
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|NodeReportProto
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
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|SCMHeartbeatRequestProto
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|events
operator|.
name|EventPublisher
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|ozone
operator|.
name|protocol
operator|.
name|commands
operator|.
name|SCMCommand
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
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
name|events
operator|.
name|SCMEvents
operator|.
name|CONTAINER_REPORT
import|;
end_import

begin_import
import|import static
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
name|events
operator|.
name|SCMEvents
operator|.
name|NODE_REPORT
import|;
end_import

begin_comment
comment|/**  * This class is responsible for dispatching heartbeat from datanode to  * appropriate EventHandler at SCM.  */
end_comment

begin_class
DECL|class|SCMDatanodeHeartbeatDispatcher
specifier|public
specifier|final
class|class
name|SCMDatanodeHeartbeatDispatcher
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
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|eventPublisher
specifier|private
specifier|final
name|EventPublisher
name|eventPublisher
decl_stmt|;
DECL|method|SCMDatanodeHeartbeatDispatcher (NodeManager nodeManager, EventPublisher eventPublisher)
specifier|public
name|SCMDatanodeHeartbeatDispatcher
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|,
name|EventPublisher
name|eventPublisher
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodeManager
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|eventPublisher
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|eventPublisher
operator|=
name|eventPublisher
expr_stmt|;
block|}
comment|/**    * Dispatches heartbeat to registered event handlers.    *    * @param heartbeat heartbeat to be dispatched.    *    * @return list of SCMCommand    */
DECL|method|dispatch (SCMHeartbeatRequestProto heartbeat)
specifier|public
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|dispatch
parameter_list|(
name|SCMHeartbeatRequestProto
name|heartbeat
parameter_list|)
block|{
name|DatanodeDetails
name|datanodeDetails
init|=
name|DatanodeDetails
operator|.
name|getFromProtoBuf
argument_list|(
name|heartbeat
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|)
decl_stmt|;
comment|// should we dispatch heartbeat through eventPublisher?
name|List
argument_list|<
name|SCMCommand
argument_list|>
name|commands
init|=
name|nodeManager
operator|.
name|processHeartbeat
argument_list|(
name|datanodeDetails
argument_list|)
decl_stmt|;
if|if
condition|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Dispatching Node Report."
argument_list|)
expr_stmt|;
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|NODE_REPORT
argument_list|,
operator|new
name|NodeReportFromDatanode
argument_list|(
name|datanodeDetails
argument_list|,
name|heartbeat
operator|.
name|getNodeReport
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|heartbeat
operator|.
name|hasContainerReport
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Dispatching Container Report."
argument_list|)
expr_stmt|;
name|eventPublisher
operator|.
name|fireEvent
argument_list|(
name|CONTAINER_REPORT
argument_list|,
operator|new
name|ContainerReportFromDatanode
argument_list|(
name|datanodeDetails
argument_list|,
name|heartbeat
operator|.
name|getContainerReport
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|commands
return|;
block|}
comment|/**    * Wrapper class for events with the datanode origin.    */
DECL|class|ReportFromDatanode
specifier|public
specifier|static
class|class
name|ReportFromDatanode
parameter_list|<
name|T
extends|extends
name|GeneratedMessage
parameter_list|>
block|{
DECL|field|datanodeDetails
specifier|private
specifier|final
name|DatanodeDetails
name|datanodeDetails
decl_stmt|;
DECL|field|report
specifier|private
specifier|final
name|T
name|report
decl_stmt|;
DECL|method|ReportFromDatanode (DatanodeDetails datanodeDetails, T report)
specifier|public
name|ReportFromDatanode
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|T
name|report
parameter_list|)
block|{
name|this
operator|.
name|datanodeDetails
operator|=
name|datanodeDetails
expr_stmt|;
name|this
operator|.
name|report
operator|=
name|report
expr_stmt|;
block|}
DECL|method|getDatanodeDetails ()
specifier|public
name|DatanodeDetails
name|getDatanodeDetails
parameter_list|()
block|{
return|return
name|datanodeDetails
return|;
block|}
DECL|method|getReport ()
specifier|public
name|T
name|getReport
parameter_list|()
block|{
return|return
name|report
return|;
block|}
block|}
comment|/**    * Node report event payload with origin.    */
DECL|class|NodeReportFromDatanode
specifier|public
specifier|static
class|class
name|NodeReportFromDatanode
extends|extends
name|ReportFromDatanode
argument_list|<
name|NodeReportProto
argument_list|>
block|{
DECL|method|NodeReportFromDatanode (DatanodeDetails datanodeDetails, NodeReportProto report)
specifier|public
name|NodeReportFromDatanode
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|NodeReportProto
name|report
parameter_list|)
block|{
name|super
argument_list|(
name|datanodeDetails
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Container report event payload with origin.    */
DECL|class|ContainerReportFromDatanode
specifier|public
specifier|static
class|class
name|ContainerReportFromDatanode
extends|extends
name|ReportFromDatanode
argument_list|<
name|ContainerReportsProto
argument_list|>
block|{
DECL|method|ContainerReportFromDatanode (DatanodeDetails datanodeDetails, ContainerReportsProto report)
specifier|public
name|ContainerReportFromDatanode
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|ContainerReportsProto
name|report
parameter_list|)
block|{
name|super
argument_list|(
name|datanodeDetails
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

