begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import static
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
name|StringHelper
operator|.
name|join
import|;
end_import

begin_import
import|import static
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
name|StringHelper
operator|.
name|ujoin
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
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
name|ContainerExitStatus
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
name|ContainerStatus
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
name|Resource
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
name|server
operator|.
name|nodemanager
operator|.
name|Context
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
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"container"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|ContainerInfo
specifier|public
class|class
name|ContainerInfo
block|{
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|state
specifier|protected
name|String
name|state
decl_stmt|;
DECL|field|exitCode
specifier|protected
name|int
name|exitCode
decl_stmt|;
DECL|field|diagnostics
specifier|protected
name|String
name|diagnostics
decl_stmt|;
DECL|field|user
specifier|protected
name|String
name|user
decl_stmt|;
DECL|field|totalMemoryNeededMB
specifier|protected
name|long
name|totalMemoryNeededMB
decl_stmt|;
DECL|field|totalVCoresNeeded
specifier|protected
name|long
name|totalVCoresNeeded
decl_stmt|;
DECL|field|containerLogsLink
specifier|protected
name|String
name|containerLogsLink
decl_stmt|;
DECL|field|nodeId
specifier|protected
name|String
name|nodeId
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|containerLogsShortLink
specifier|protected
name|String
name|containerLogsShortLink
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|exitStatus
specifier|protected
name|String
name|exitStatus
decl_stmt|;
DECL|method|ContainerInfo ()
specifier|public
name|ContainerInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|ContainerInfo (final Context nmContext, final Container container)
specifier|public
name|ContainerInfo
parameter_list|(
specifier|final
name|Context
name|nmContext
parameter_list|,
specifier|final
name|Container
name|container
parameter_list|)
block|{
name|this
argument_list|(
name|nmContext
argument_list|,
name|container
argument_list|,
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|ContainerInfo (final Context nmContext, final Container container, String requestUri, String pathPrefix)
specifier|public
name|ContainerInfo
parameter_list|(
specifier|final
name|Context
name|nmContext
parameter_list|,
specifier|final
name|Container
name|container
parameter_list|,
name|String
name|requestUri
parameter_list|,
name|String
name|pathPrefix
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|container
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nmContext
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|ContainerStatus
name|containerData
init|=
name|container
operator|.
name|cloneAndGetContainerStatus
argument_list|()
decl_stmt|;
name|this
operator|.
name|exitCode
operator|=
name|containerData
operator|.
name|getExitStatus
argument_list|()
expr_stmt|;
name|this
operator|.
name|exitStatus
operator|=
operator|(
name|this
operator|.
name|exitCode
operator|==
name|ContainerExitStatus
operator|.
name|INVALID
operator|)
condition|?
literal|"N/A"
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|exitCode
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|container
operator|.
name|getContainerState
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|containerData
operator|.
name|getDiagnostics
argument_list|()
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|diagnostics
operator|==
literal|null
operator|||
name|this
operator|.
name|diagnostics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|diagnostics
operator|=
literal|""
expr_stmt|;
block|}
name|this
operator|.
name|user
operator|=
name|container
operator|.
name|getUser
argument_list|()
expr_stmt|;
name|Resource
name|res
init|=
name|container
operator|.
name|getResource
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|totalMemoryNeededMB
operator|=
name|res
operator|.
name|getMemory
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalVCoresNeeded
operator|=
name|res
operator|.
name|getVirtualCores
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|containerLogsShortLink
operator|=
name|ujoin
argument_list|(
literal|"containerlogs"
argument_list|,
name|this
operator|.
name|id
argument_list|,
name|container
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestUri
operator|==
literal|null
condition|)
block|{
name|requestUri
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|pathPrefix
operator|==
literal|null
condition|)
block|{
name|pathPrefix
operator|=
literal|""
expr_stmt|;
block|}
name|this
operator|.
name|containerLogsLink
operator|=
name|join
argument_list|(
name|requestUri
argument_list|,
name|pathPrefix
argument_list|,
name|this
operator|.
name|containerLogsShortLink
argument_list|)
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|getNodeId ()
specifier|public
name|String
name|getNodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
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
DECL|method|getExitStatus ()
specifier|public
name|String
name|getExitStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|exitStatus
return|;
block|}
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|user
return|;
block|}
DECL|method|getShortLogLink ()
specifier|public
name|String
name|getShortLogLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerLogsShortLink
return|;
block|}
DECL|method|getLogLink ()
specifier|public
name|String
name|getLogLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|containerLogsLink
return|;
block|}
DECL|method|getMemoryNeeded ()
specifier|public
name|long
name|getMemoryNeeded
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalMemoryNeededMB
return|;
block|}
DECL|method|getVCoresNeeded ()
specifier|public
name|long
name|getVCoresNeeded
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalVCoresNeeded
return|;
block|}
block|}
end_class

end_unit

