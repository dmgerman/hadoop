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
name|event
operator|.
name|AbstractEvent
import|;
end_import

begin_class
DECL|class|JobFinishEvent
specifier|public
class|class
name|JobFinishEvent
extends|extends
name|AbstractEvent
argument_list|<
name|JobFinishEvent
operator|.
name|Type
argument_list|>
block|{
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|enumConstant|STATE_CHANGED
name|STATE_CHANGED
block|}
DECL|field|jobID
specifier|private
name|JobId
name|jobID
decl_stmt|;
DECL|method|JobFinishEvent (JobId jobID)
specifier|public
name|JobFinishEvent
parameter_list|(
name|JobId
name|jobID
parameter_list|)
block|{
name|super
argument_list|(
name|Type
operator|.
name|STATE_CHANGED
argument_list|)
expr_stmt|;
name|this
operator|.
name|jobID
operator|=
name|jobID
expr_stmt|;
block|}
DECL|method|getJobId ()
specifier|public
name|JobId
name|getJobId
parameter_list|()
block|{
return|return
name|jobID
return|;
block|}
block|}
end_class

end_unit

