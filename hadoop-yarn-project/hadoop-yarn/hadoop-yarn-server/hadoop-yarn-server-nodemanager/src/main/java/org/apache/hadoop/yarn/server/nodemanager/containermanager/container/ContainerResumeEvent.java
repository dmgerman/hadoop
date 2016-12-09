begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.container
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
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
name|ContainerId
import|;
end_import

begin_comment
comment|/**  * ContainerEvent for ContainerEventType.RESUME_CONTAINER.  */
end_comment

begin_class
DECL|class|ContainerResumeEvent
specifier|public
class|class
name|ContainerResumeEvent
extends|extends
name|ContainerEvent
block|{
DECL|field|diagnostic
specifier|private
specifier|final
name|String
name|diagnostic
decl_stmt|;
DECL|method|ContainerResumeEvent (ContainerId cId, String diagnostic)
specifier|public
name|ContainerResumeEvent
parameter_list|(
name|ContainerId
name|cId
parameter_list|,
name|String
name|diagnostic
parameter_list|)
block|{
name|super
argument_list|(
name|cId
argument_list|,
name|ContainerEventType
operator|.
name|RESUME_CONTAINER
argument_list|)
expr_stmt|;
name|this
operator|.
name|diagnostic
operator|=
name|diagnostic
expr_stmt|;
block|}
DECL|method|getDiagnostic ()
specifier|public
name|String
name|getDiagnostic
parameter_list|()
block|{
return|return
name|this
operator|.
name|diagnostic
return|;
block|}
block|}
end_class

end_unit

