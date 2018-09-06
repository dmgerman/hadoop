begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.pipelines
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
name|pipelines
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|Mapping
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
name|common
operator|.
name|helpers
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

begin_comment
comment|/**  * Handles pipeline close event.  */
end_comment

begin_class
DECL|class|PipelineCloseHandler
specifier|public
class|class
name|PipelineCloseHandler
implements|implements
name|EventHandler
argument_list|<
name|PipelineID
argument_list|>
block|{
DECL|field|mapping
specifier|private
specifier|final
name|Mapping
name|mapping
decl_stmt|;
DECL|method|PipelineCloseHandler (Mapping mapping)
specifier|public
name|PipelineCloseHandler
parameter_list|(
name|Mapping
name|mapping
parameter_list|)
block|{
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onMessage (PipelineID pipelineID, EventPublisher publisher)
specifier|public
name|void
name|onMessage
parameter_list|(
name|PipelineID
name|pipelineID
parameter_list|,
name|EventPublisher
name|publisher
parameter_list|)
block|{
name|mapping
operator|.
name|handlePipelineClose
argument_list|(
name|pipelineID
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

