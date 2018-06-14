begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server.report
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
operator|.
name|report
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
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
name|server
operator|.
name|StorageContainerManager
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
name|concurrent
operator|.
name|HadoopExecutors
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
name|HashMap
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
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_comment
comment|/**  * This class is responsible for dispatching heartbeat from datanode to  * appropriate ReportHandlers at SCM.  * Only one handler per report is supported now, it's very easy to support  * multiple handlers for a report.  */
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
comment|/**    * This stores Report to Handler mapping.    */
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|,
DECL|field|handlers
name|SCMDatanodeReportHandler
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|>
name|handlers
decl_stmt|;
comment|/**    * Executor service which will be used for processing reports.    */
DECL|field|executorService
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
comment|/**    * Constructs SCMDatanodeHeartbeatDispatcher instance with the given    * handlers.    *    * @param handlers report to report handler mapping    */
DECL|method|SCMDatanodeHeartbeatDispatcher (Map<Class<? extends GeneratedMessage>, SCMDatanodeReportHandler<? extends GeneratedMessage>> handlers)
specifier|private
name|SCMDatanodeHeartbeatDispatcher
parameter_list|(
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|,
name|SCMDatanodeReportHandler
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|>
name|handlers
parameter_list|)
block|{
name|this
operator|.
name|handlers
operator|=
name|handlers
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|HadoopExecutors
operator|.
name|newCachedThreadPool
argument_list|(
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"SCMDatanode Heartbeat Dispatcher Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Dispatches heartbeat to registered handlers.    *    * @param heartbeat heartbeat to be dispatched.    */
DECL|method|dispatch (SCMHeartbeatRequestProto heartbeat)
specifier|public
name|void
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
if|if
condition|(
name|heartbeat
operator|.
name|hasNodeReport
argument_list|()
condition|)
block|{
name|processReport
argument_list|(
name|datanodeDetails
argument_list|,
name|heartbeat
operator|.
name|getNodeReport
argument_list|()
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
name|processReport
argument_list|(
name|datanodeDetails
argument_list|,
name|heartbeat
operator|.
name|getContainerReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Invokes appropriate ReportHandler and submits the task to executor    * service for processing.    *    * @param datanodeDetails Datanode Information    * @param report Report to be processed    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|processReport (DatanodeDetails datanodeDetails, GeneratedMessage report)
specifier|private
name|void
name|processReport
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|GeneratedMessage
name|report
parameter_list|)
block|{
name|executorService
operator|.
name|submit
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|SCMDatanodeReportHandler
name|handler
init|=
name|handlers
operator|.
name|get
argument_list|(
name|report
operator|.
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
name|handler
operator|.
name|processReport
argument_list|(
name|datanodeDetails
argument_list|,
name|report
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception wile processing report {}, from {}"
argument_list|,
name|report
operator|.
name|getClass
argument_list|()
argument_list|,
name|datanodeDetails
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shuts down SCMDatanodeHeartbeatDispatcher.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Returns a new Builder to construct {@link SCMDatanodeHeartbeatDispatcher}.    *    * @param conf Configuration to be used by SCMDatanodeHeartbeatDispatcher    * @param scm {@link StorageContainerManager} instance to be used by report    *            handlers    *    * @return {@link SCMDatanodeHeartbeatDispatcher.Builder} instance    */
DECL|method|newBuilder (Configuration conf, StorageContainerManager scm)
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StorageContainerManager
name|scm
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|conf
argument_list|,
name|scm
argument_list|)
return|;
block|}
comment|/**    * Builder for SCMDatanodeHeartbeatDispatcher.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|reportHandlerFactory
specifier|private
specifier|final
name|SCMDatanodeReportHandlerFactory
name|reportHandlerFactory
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|,
DECL|field|report2handler
name|SCMDatanodeReportHandler
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
argument_list|>
name|report2handler
decl_stmt|;
comment|/**      * Constructs SCMDatanodeHeartbeatDispatcher.Builder instance.      *      * @param conf Configuration object to be used.      * @param scm StorageContainerManager instance to be used for report      *            handler initialization.      */
DECL|method|Builder (Configuration conf, StorageContainerManager scm)
specifier|private
name|Builder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|StorageContainerManager
name|scm
parameter_list|)
block|{
name|this
operator|.
name|report2handler
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|reportHandlerFactory
operator|=
operator|new
name|SCMDatanodeReportHandlerFactory
argument_list|(
name|conf
argument_list|,
name|scm
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds new report handler for the given report.      *      * @param report Report for which handler has to be added      *      * @return Builder      */
DECL|method|addHandlerFor (Class<? extends GeneratedMessage> report)
specifier|public
name|Builder
name|addHandlerFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
name|report
parameter_list|)
block|{
name|report2handler
operator|.
name|put
argument_list|(
name|report
argument_list|,
name|reportHandlerFactory
operator|.
name|getHandlerFor
argument_list|(
name|report
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Associates the given report handler for the given report.      *      * @param report Report to be associated with      * @param handler Handler to be used for the report      *      * @return Builder      */
DECL|method|addHandler (Class<? extends GeneratedMessage> report, SCMDatanodeReportHandler<? extends GeneratedMessage> handler)
specifier|public
name|Builder
name|addHandler
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
name|report
parameter_list|,
name|SCMDatanodeReportHandler
argument_list|<
name|?
extends|extends
name|GeneratedMessage
argument_list|>
name|handler
parameter_list|)
block|{
name|report2handler
operator|.
name|put
argument_list|(
name|report
argument_list|,
name|handler
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Builds and returns {@link SCMDatanodeHeartbeatDispatcher} instance.      *      * @return SCMDatanodeHeartbeatDispatcher      */
DECL|method|build ()
specifier|public
name|SCMDatanodeHeartbeatDispatcher
name|build
parameter_list|()
block|{
return|return
operator|new
name|SCMDatanodeHeartbeatDispatcher
argument_list|(
name|report2handler
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

