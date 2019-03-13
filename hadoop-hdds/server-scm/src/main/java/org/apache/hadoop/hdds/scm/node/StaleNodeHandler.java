begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.node
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
name|node
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
name|pipeline
operator|.
name|Pipeline
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
name|pipeline
operator|.
name|PipelineID
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
name|pipeline
operator|.
name|PipelineManager
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Handles Stale node event.  */
end_comment

begin_class
DECL|class|StaleNodeHandler
specifier|public
class|class
name|StaleNodeHandler
implements|implements
name|EventHandler
argument_list|<
name|DatanodeDetails
argument_list|>
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
name|StaleNodeHandler
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
DECL|field|pipelineManager
specifier|private
specifier|final
name|PipelineManager
name|pipelineManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|method|StaleNodeHandler (NodeManager nodeManager, PipelineManager pipelineManager, OzoneConfiguration conf)
specifier|public
name|StaleNodeHandler
parameter_list|(
name|NodeManager
name|nodeManager
parameter_list|,
name|PipelineManager
name|pipelineManager
parameter_list|,
name|OzoneConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|pipelineManager
operator|=
name|pipelineManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (DatanodeDetails datanodeDetails, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|DatanodeDetails
name|datanodeDetails
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|Set
argument_list|<
name|PipelineID
argument_list|>
name|pipelineIds
init|=
name|nodeManager
operator|.
name|getPipelines
argument_list|(
name|datanodeDetails
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Datanode {} moved to stale state. Finalizing its pipelines {}"
argument_list|,
name|datanodeDetails
argument_list|,
name|pipelineIds
argument_list|)
expr_stmt|;
for|for
control|(
name|PipelineID
name|pipelineID
range|:
name|pipelineIds
control|)
block|{
try|try
block|{
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
name|pipelineManager
operator|.
name|finalizeAndDestroyPipeline
argument_list|(
name|pipeline
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not finalize pipeline={} for dn={}"
argument_list|,
name|pipelineID
argument_list|,
name|datanodeDetails
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

