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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * Used to hold max wait time / queue length information to be  * passed back to the NodeManager.  */
end_comment

begin_class
DECL|class|ContainerQueuingLimit
specifier|public
specifier|abstract
class|class
name|ContainerQueuingLimit
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|ContainerQueuingLimit
name|newInstance
parameter_list|()
block|{
name|ContainerQueuingLimit
name|containerQueuingLimit
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ContainerQueuingLimit
operator|.
name|class
argument_list|)
decl_stmt|;
name|containerQueuingLimit
operator|.
name|setMaxQueueLength
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|containerQueuingLimit
operator|.
name|setMaxQueueWaitTimeInMs
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|containerQueuingLimit
return|;
block|}
DECL|method|getMaxQueueLength ()
specifier|public
specifier|abstract
name|int
name|getMaxQueueLength
parameter_list|()
function_decl|;
DECL|method|setMaxQueueLength (int queueLength)
specifier|public
specifier|abstract
name|void
name|setMaxQueueLength
parameter_list|(
name|int
name|queueLength
parameter_list|)
function_decl|;
DECL|method|getMaxQueueWaitTimeInMs ()
specifier|public
specifier|abstract
name|int
name|getMaxQueueWaitTimeInMs
parameter_list|()
function_decl|;
DECL|method|setMaxQueueWaitTimeInMs (int waitTime)
specifier|public
specifier|abstract
name|void
name|setMaxQueueWaitTimeInMs
parameter_list|(
name|int
name|waitTime
parameter_list|)
function_decl|;
block|}
end_class

end_unit

