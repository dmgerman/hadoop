begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmnode
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
name|resourcemanager
operator|.
name|rmnode
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
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|NodeId
import|;
end_import

begin_class
DECL|class|RMNodeCleanAppEvent
specifier|public
class|class
name|RMNodeCleanAppEvent
extends|extends
name|RMNodeEvent
block|{
DECL|field|appId
specifier|private
name|ApplicationId
name|appId
decl_stmt|;
DECL|method|RMNodeCleanAppEvent (NodeId nodeId, ApplicationId appId)
specifier|public
name|RMNodeCleanAppEvent
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|ApplicationId
name|appId
parameter_list|)
block|{
name|super
argument_list|(
name|nodeId
argument_list|,
name|RMNodeEventType
operator|.
name|CLEANUP_APP
argument_list|)
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|appId
expr_stmt|;
block|}
DECL|method|getAppId ()
specifier|public
name|ApplicationId
name|getAppId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appId
return|;
block|}
block|}
end_class

end_unit

