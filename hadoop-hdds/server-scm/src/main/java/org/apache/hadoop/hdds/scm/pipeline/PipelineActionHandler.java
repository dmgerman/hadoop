begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipeline
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
name|pipeline
package|;
end_package

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
name|conf
operator|.
name|OzoneConfiguration
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
name|PipelineAction
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
name|SCMDatanodeHeartbeatDispatcher
operator|.
name|PipelineActionsFromDatanode
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
name|EventHandler
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

begin_comment
comment|/**  * Handles pipeline actions from datanode.  */
end_comment

begin_class
DECL|class|PipelineActionHandler
specifier|public
class|class
name|PipelineActionHandler
implements|implements
name|EventHandler
argument_list|<
name|PipelineActionsFromDatanode
argument_list|>
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|PipelineActionHandler
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|ozoneConf
specifier|private
specifier|final
name|Configuration
name|ozoneConf
decl_stmt|;
DECL|method|PipelineActionHandler (PipelineManager pipelineManager, OzoneConfiguration conf)
specifier|public
name|PipelineActionHandler
parameter_list|(
name|PipelineManager
name|pipelineManager
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
name|this
operator|.
name|ozoneConf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (PipelineActionsFromDatanode report, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|PipelineActionsFromDatanode
name|report
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
for|for
control|(
name|PipelineAction
name|action
range|:
name|report
operator|.
name|getReport
argument_list|()
operator|.
name|getPipelineActionsList
argument_list|()
control|)
block|{
if|if
condition|(
name|action
operator|.
name|getAction
argument_list|()
operator|==
name|PipelineAction
operator|.
name|Action
operator|.
name|CLOSE
condition|)
block|{
name|PipelineID
name|pipelineID
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pipelineID
operator|=
name|PipelineID
operator|.
name|getFromProtobuf
argument_list|(
name|action
operator|.
name|getClosePipeline
argument_list|()
operator|.
name|getPipelineID
argument_list|()
argument_list|)
expr_stmt|;
name|Pipeline
name|pipeline
init|=
name|pipelineManager
operator|.
name|getPipeline
argument_list|(
name|pipelineID
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received pipeline action {} for {} from datanode {}. "
operator|+
literal|"Reason : {}"
argument_list|,
name|action
operator|.
name|getAction
argument_list|()
argument_list|,
name|pipeline
argument_list|,
name|report
operator|.
name|getDatanodeDetails
argument_list|()
argument_list|,
name|action
operator|.
name|getClosePipeline
argument_list|()
operator|.
name|getDetailedReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not execute pipeline action={} pipeline={} {}"
argument_list|,
name|action
argument_list|,
name|pipelineID
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"unknown pipeline action:{}"
operator|+
name|action
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

