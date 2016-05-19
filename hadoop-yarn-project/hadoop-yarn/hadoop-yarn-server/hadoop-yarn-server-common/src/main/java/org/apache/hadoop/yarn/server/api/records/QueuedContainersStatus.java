begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records
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
name|api
operator|.
name|records
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|InterfaceStability
operator|.
name|Evolving
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  *<code>QueuedContainersStatus</code> captures information pertaining to the  * state of execution of the Queueable containers within a node.  *</p>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|QueuedContainersStatus
specifier|public
specifier|abstract
class|class
name|QueuedContainersStatus
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|QueuedContainersStatus
name|newInstance
parameter_list|()
block|{
return|return
name|Records
operator|.
name|newRecord
argument_list|(
name|QueuedContainersStatus
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|getEstimatedQueueWaitTime ()
specifier|public
specifier|abstract
name|int
name|getEstimatedQueueWaitTime
parameter_list|()
function_decl|;
DECL|method|setEstimatedQueueWaitTime (int queueWaitTime)
specifier|public
specifier|abstract
name|void
name|setEstimatedQueueWaitTime
parameter_list|(
name|int
name|queueWaitTime
parameter_list|)
function_decl|;
DECL|method|getWaitQueueLength ()
specifier|public
specifier|abstract
name|int
name|getWaitQueueLength
parameter_list|()
function_decl|;
DECL|method|setWaitQueueLength (int waitQueueLength)
specifier|public
specifier|abstract
name|void
name|setWaitQueueLength
parameter_list|(
name|int
name|waitQueueLength
parameter_list|)
function_decl|;
block|}
end_class

end_unit

