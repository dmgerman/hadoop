begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
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
name|federation
operator|.
name|store
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
name|InterfaceAudience
operator|.
name|Public
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
name|Unstable
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  * ApplicationHomeSubCluster is a report of the runtime information of the  * application that is running in the federated cluster.  *  *<p>  * It includes information such as:  *<ul>  *<li>{@link ApplicationId}</li>  *<li>{@link SubClusterId}</li>  *</ul>  *  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ApplicationHomeSubCluster
specifier|public
specifier|abstract
class|class
name|ApplicationHomeSubCluster
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (ApplicationId appId, SubClusterId homeSubCluster)
specifier|public
specifier|static
name|ApplicationHomeSubCluster
name|newInstance
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|SubClusterId
name|homeSubCluster
parameter_list|)
block|{
name|ApplicationHomeSubCluster
name|appMapping
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|ApplicationHomeSubCluster
operator|.
name|class
argument_list|)
decl_stmt|;
name|appMapping
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|appMapping
operator|.
name|setHomeSubCluster
argument_list|(
name|homeSubCluster
argument_list|)
expr_stmt|;
return|return
name|appMapping
return|;
block|}
comment|/**    * Get the {@link ApplicationId} representing the unique identifier of the    * application.    *    * @return the application identifier    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getApplicationId ()
specifier|public
specifier|abstract
name|ApplicationId
name|getApplicationId
parameter_list|()
function_decl|;
comment|/**    * Set the {@link ApplicationId} representing the unique identifier of the    * application.    *    * @param applicationId the application identifier    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setApplicationId (ApplicationId applicationId)
specifier|public
specifier|abstract
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
function_decl|;
comment|/**    * Get the {@link SubClusterId} representing the unique identifier of the home    * subcluster in which the ApplicationMaster of the application is running.    *    * @return the home subcluster identifier    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getHomeSubCluster ()
specifier|public
specifier|abstract
name|SubClusterId
name|getHomeSubCluster
parameter_list|()
function_decl|;
comment|/**    * Set the {@link SubClusterId} representing the unique identifier of the home    * subcluster in which the ApplicationMaster of the application is running.    *    * @param homeSubCluster the home subcluster identifier    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setHomeSubCluster (SubClusterId homeSubCluster)
specifier|public
specifier|abstract
name|void
name|setHomeSubCluster
parameter_list|(
name|SubClusterId
name|homeSubCluster
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ApplicationHomeSubCluster
name|other
init|=
operator|(
name|ApplicationHomeSubCluster
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getApplicationId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getApplicationId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|getHomeSubCluster
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getHomeSubCluster
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getApplicationId
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|getHomeSubCluster
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ApplicationHomeSubCluster [getApplicationId()="
operator|+
name|getApplicationId
argument_list|()
operator|+
literal|", getHomeSubCluster()="
operator|+
name|getHomeSubCluster
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

