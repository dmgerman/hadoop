begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.job.event
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|api
operator|.
name|records
operator|.
name|NodeReport
import|;
end_import

begin_class
DECL|class|JobUpdatedNodesEvent
specifier|public
class|class
name|JobUpdatedNodesEvent
extends|extends
name|JobEvent
block|{
DECL|field|updatedNodes
specifier|private
specifier|final
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
decl_stmt|;
DECL|method|JobUpdatedNodesEvent (JobId jobId, List<NodeReport> updatedNodes)
specifier|public
name|JobUpdatedNodesEvent
parameter_list|(
name|JobId
name|jobId
parameter_list|,
name|List
argument_list|<
name|NodeReport
argument_list|>
name|updatedNodes
parameter_list|)
block|{
name|super
argument_list|(
name|jobId
argument_list|,
name|JobEventType
operator|.
name|JOB_UPDATED_NODES
argument_list|)
expr_stmt|;
name|this
operator|.
name|updatedNodes
operator|=
name|updatedNodes
expr_stmt|;
block|}
DECL|method|getUpdatedNodes ()
specifier|public
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getUpdatedNodes
parameter_list|()
block|{
return|return
name|updatedNodes
return|;
block|}
block|}
end_class

end_unit

