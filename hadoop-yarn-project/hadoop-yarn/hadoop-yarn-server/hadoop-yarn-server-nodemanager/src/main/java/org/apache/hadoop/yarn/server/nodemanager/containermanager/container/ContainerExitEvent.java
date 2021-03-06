begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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

begin_class
DECL|class|ContainerExitEvent
specifier|public
class|class
name|ContainerExitEvent
extends|extends
name|ContainerEvent
block|{
DECL|field|exitCode
specifier|private
name|int
name|exitCode
decl_stmt|;
DECL|field|diagnosticInfo
specifier|private
specifier|final
name|String
name|diagnosticInfo
decl_stmt|;
DECL|method|ContainerExitEvent (ContainerId cID, ContainerEventType eventType, int exitCode, String diagnosticInfo)
specifier|public
name|ContainerExitEvent
parameter_list|(
name|ContainerId
name|cID
parameter_list|,
name|ContainerEventType
name|eventType
parameter_list|,
name|int
name|exitCode
parameter_list|,
name|String
name|diagnosticInfo
parameter_list|)
block|{
name|super
argument_list|(
name|cID
argument_list|,
name|eventType
argument_list|)
expr_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|exitCode
expr_stmt|;
name|this
operator|.
name|diagnosticInfo
operator|=
name|diagnosticInfo
expr_stmt|;
block|}
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|exitCode
return|;
block|}
DECL|method|getDiagnosticInfo ()
specifier|public
name|String
name|getDiagnosticInfo
parameter_list|()
block|{
return|return
name|diagnosticInfo
return|;
block|}
block|}
end_class

end_unit

