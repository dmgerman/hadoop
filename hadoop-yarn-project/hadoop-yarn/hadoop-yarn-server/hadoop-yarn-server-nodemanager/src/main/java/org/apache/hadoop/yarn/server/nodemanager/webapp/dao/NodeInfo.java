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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|VersionInfo
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
name|NodeManager
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
name|ResourceView
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
name|YarnVersionInfo
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|NodeInfo
specifier|public
class|class
name|NodeInfo
block|{
DECL|field|BYTES_IN_MB
specifier|private
specifier|static
specifier|final
name|long
name|BYTES_IN_MB
init|=
literal|1024
operator|*
literal|1024
decl_stmt|;
DECL|field|healthReport
specifier|protected
name|String
name|healthReport
decl_stmt|;
DECL|field|totalVmemAllocatedContainersMB
specifier|protected
name|long
name|totalVmemAllocatedContainersMB
decl_stmt|;
DECL|field|totalPmemAllocatedContainersMB
specifier|protected
name|long
name|totalPmemAllocatedContainersMB
decl_stmt|;
DECL|field|totalVCoresAllocatedContainers
specifier|protected
name|long
name|totalVCoresAllocatedContainers
decl_stmt|;
DECL|field|vmemCheckEnabled
specifier|protected
name|boolean
name|vmemCheckEnabled
decl_stmt|;
DECL|field|pmemCheckEnabled
specifier|protected
name|boolean
name|pmemCheckEnabled
decl_stmt|;
DECL|field|lastNodeUpdateTime
specifier|protected
name|long
name|lastNodeUpdateTime
decl_stmt|;
DECL|field|nodeHealthy
specifier|protected
name|boolean
name|nodeHealthy
decl_stmt|;
DECL|field|nodeManagerVersion
specifier|protected
name|String
name|nodeManagerVersion
decl_stmt|;
DECL|field|nodeManagerBuildVersion
specifier|protected
name|String
name|nodeManagerBuildVersion
decl_stmt|;
DECL|field|nodeManagerVersionBuiltOn
specifier|protected
name|String
name|nodeManagerVersionBuiltOn
decl_stmt|;
DECL|field|hadoopVersion
specifier|protected
name|String
name|hadoopVersion
decl_stmt|;
DECL|field|hadoopBuildVersion
specifier|protected
name|String
name|hadoopBuildVersion
decl_stmt|;
DECL|field|hadoopVersionBuiltOn
specifier|protected
name|String
name|hadoopVersionBuiltOn
decl_stmt|;
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|nodeHostName
specifier|protected
name|String
name|nodeHostName
decl_stmt|;
DECL|field|nmStartupTime
specifier|protected
name|long
name|nmStartupTime
decl_stmt|;
DECL|method|NodeInfo ()
specifier|public
name|NodeInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|NodeInfo (final Context context, final ResourceView resourceView)
specifier|public
name|NodeInfo
parameter_list|(
specifier|final
name|Context
name|context
parameter_list|,
specifier|final
name|ResourceView
name|resourceView
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|context
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeHostName
operator|=
name|context
operator|.
name|getNodeId
argument_list|()
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalVmemAllocatedContainersMB
operator|=
name|resourceView
operator|.
name|getVmemAllocatedForContainers
argument_list|()
operator|/
name|BYTES_IN_MB
expr_stmt|;
name|this
operator|.
name|vmemCheckEnabled
operator|=
name|resourceView
operator|.
name|isVmemCheckEnabled
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalPmemAllocatedContainersMB
operator|=
name|resourceView
operator|.
name|getPmemAllocatedForContainers
argument_list|()
operator|/
name|BYTES_IN_MB
expr_stmt|;
name|this
operator|.
name|pmemCheckEnabled
operator|=
name|resourceView
operator|.
name|isPmemCheckEnabled
argument_list|()
expr_stmt|;
name|this
operator|.
name|totalVCoresAllocatedContainers
operator|=
name|resourceView
operator|.
name|getVCoresAllocatedForContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeHealthy
operator|=
name|context
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getIsNodeHealthy
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastNodeUpdateTime
operator|=
name|context
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getLastHealthReportTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|healthReport
operator|=
name|context
operator|.
name|getNodeHealthStatus
argument_list|()
operator|.
name|getHealthReport
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeManagerVersion
operator|=
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeManagerBuildVersion
operator|=
name|YarnVersionInfo
operator|.
name|getBuildVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeManagerVersionBuiltOn
operator|=
name|YarnVersionInfo
operator|.
name|getDate
argument_list|()
expr_stmt|;
name|this
operator|.
name|hadoopVersion
operator|=
name|VersionInfo
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|hadoopBuildVersion
operator|=
name|VersionInfo
operator|.
name|getBuildVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|hadoopVersionBuiltOn
operator|=
name|VersionInfo
operator|.
name|getDate
argument_list|()
expr_stmt|;
name|this
operator|.
name|nmStartupTime
operator|=
name|NodeManager
operator|.
name|getNMStartupTime
argument_list|()
expr_stmt|;
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
name|id
return|;
block|}
DECL|method|getNodeHostName ()
specifier|public
name|String
name|getNodeHostName
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHostName
return|;
block|}
DECL|method|getNMVersion ()
specifier|public
name|String
name|getNMVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeManagerVersion
return|;
block|}
DECL|method|getNMBuildVersion ()
specifier|public
name|String
name|getNMBuildVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeManagerBuildVersion
return|;
block|}
DECL|method|getNMVersionBuiltOn ()
specifier|public
name|String
name|getNMVersionBuiltOn
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeManagerVersionBuiltOn
return|;
block|}
DECL|method|getHadoopVersion ()
specifier|public
name|String
name|getHadoopVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|hadoopVersion
return|;
block|}
DECL|method|getHadoopBuildVersion ()
specifier|public
name|String
name|getHadoopBuildVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|hadoopBuildVersion
return|;
block|}
DECL|method|getHadoopVersionBuiltOn ()
specifier|public
name|String
name|getHadoopVersionBuiltOn
parameter_list|()
block|{
return|return
name|this
operator|.
name|hadoopVersionBuiltOn
return|;
block|}
DECL|method|getHealthStatus ()
specifier|public
name|boolean
name|getHealthStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHealthy
return|;
block|}
DECL|method|getLastNodeUpdateTime ()
specifier|public
name|long
name|getLastNodeUpdateTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastNodeUpdateTime
return|;
block|}
DECL|method|getHealthReport ()
specifier|public
name|String
name|getHealthReport
parameter_list|()
block|{
return|return
name|this
operator|.
name|healthReport
return|;
block|}
DECL|method|getTotalVmemAllocated ()
specifier|public
name|long
name|getTotalVmemAllocated
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalVmemAllocatedContainersMB
return|;
block|}
DECL|method|getTotalVCoresAllocated ()
specifier|public
name|long
name|getTotalVCoresAllocated
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalVCoresAllocatedContainers
return|;
block|}
DECL|method|isVmemCheckEnabled ()
specifier|public
name|boolean
name|isVmemCheckEnabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|vmemCheckEnabled
return|;
block|}
DECL|method|getTotalPmemAllocated ()
specifier|public
name|long
name|getTotalPmemAllocated
parameter_list|()
block|{
return|return
name|this
operator|.
name|totalPmemAllocatedContainersMB
return|;
block|}
DECL|method|isPmemCheckEnabled ()
specifier|public
name|boolean
name|isPmemCheckEnabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|pmemCheckEnabled
return|;
block|}
DECL|method|getNMStartupTime ()
specifier|public
name|long
name|getNMStartupTime
parameter_list|()
block|{
return|return
name|nmStartupTime
return|;
block|}
block|}
end_class

end_unit

