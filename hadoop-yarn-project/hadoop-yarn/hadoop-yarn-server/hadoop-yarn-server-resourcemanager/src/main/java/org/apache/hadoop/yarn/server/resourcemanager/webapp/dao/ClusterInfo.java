begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|ha
operator|.
name|HAServiceProtocol
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
name|service
operator|.
name|Service
operator|.
name|STATE
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
name|resourcemanager
operator|.
name|ResourceManager
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|RMStateStore
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
DECL|class|ClusterInfo
specifier|public
class|class
name|ClusterInfo
block|{
DECL|field|id
specifier|protected
name|long
name|id
decl_stmt|;
DECL|field|startedOn
specifier|protected
name|long
name|startedOn
decl_stmt|;
DECL|field|state
specifier|protected
name|STATE
name|state
decl_stmt|;
DECL|field|haState
specifier|protected
name|HAServiceProtocol
operator|.
name|HAServiceState
name|haState
decl_stmt|;
DECL|field|rmStateStoreName
specifier|protected
name|String
name|rmStateStoreName
decl_stmt|;
DECL|field|resourceManagerVersion
specifier|protected
name|String
name|resourceManagerVersion
decl_stmt|;
DECL|field|resourceManagerBuildVersion
specifier|protected
name|String
name|resourceManagerBuildVersion
decl_stmt|;
DECL|field|resourceManagerVersionBuiltOn
specifier|protected
name|String
name|resourceManagerVersionBuiltOn
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
DECL|field|haZooKeeperConnectionState
specifier|protected
name|String
name|haZooKeeperConnectionState
decl_stmt|;
DECL|method|ClusterInfo ()
specifier|public
name|ClusterInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|ClusterInfo (ResourceManager rm)
specifier|public
name|ClusterInfo
parameter_list|(
name|ResourceManager
name|rm
parameter_list|)
block|{
name|long
name|ts
init|=
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
decl_stmt|;
name|this
operator|.
name|id
operator|=
name|ts
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|rm
operator|.
name|getServiceState
argument_list|()
expr_stmt|;
name|this
operator|.
name|haState
operator|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAServiceState
argument_list|()
expr_stmt|;
name|this
operator|.
name|rmStateStoreName
operator|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getStateStore
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|startedOn
operator|=
name|ts
expr_stmt|;
name|this
operator|.
name|resourceManagerVersion
operator|=
name|YarnVersionInfo
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|resourceManagerBuildVersion
operator|=
name|YarnVersionInfo
operator|.
name|getBuildVersion
argument_list|()
expr_stmt|;
name|this
operator|.
name|resourceManagerVersionBuiltOn
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
name|haZooKeeperConnectionState
operator|=
name|rm
operator|.
name|getRMContext
argument_list|()
operator|.
name|getHAZookeeperConnectionState
argument_list|()
expr_stmt|;
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
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getHAState ()
specifier|public
name|String
name|getHAState
parameter_list|()
block|{
return|return
name|this
operator|.
name|haState
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getRMStateStore ()
specifier|public
name|String
name|getRMStateStore
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmStateStoreName
return|;
block|}
DECL|method|getRMVersion ()
specifier|public
name|String
name|getRMVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|resourceManagerVersion
return|;
block|}
DECL|method|getRMBuildVersion ()
specifier|public
name|String
name|getRMBuildVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|resourceManagerBuildVersion
return|;
block|}
DECL|method|getRMVersionBuiltOn ()
specifier|public
name|String
name|getRMVersionBuiltOn
parameter_list|()
block|{
return|return
name|this
operator|.
name|resourceManagerVersionBuiltOn
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
DECL|method|getClusterId ()
specifier|public
name|long
name|getClusterId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|getStartedOn ()
specifier|public
name|long
name|getStartedOn
parameter_list|()
block|{
return|return
name|this
operator|.
name|startedOn
return|;
block|}
DECL|method|getHAZookeeperConnectionState ()
specifier|public
name|String
name|getHAZookeeperConnectionState
parameter_list|()
block|{
return|return
name|this
operator|.
name|haZooKeeperConnectionState
return|;
block|}
block|}
end_class

end_unit

