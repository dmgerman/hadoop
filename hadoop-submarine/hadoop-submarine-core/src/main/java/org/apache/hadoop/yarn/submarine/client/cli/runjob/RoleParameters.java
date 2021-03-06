begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.runjob
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|runjob
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
name|submarine
operator|.
name|common
operator|.
name|api
operator|.
name|Role
import|;
end_import

begin_comment
comment|/**  * This class encapsulates data related to a particular Role.  * Some examples: TF Worker process, TF PS process or a PyTorch worker process.  */
end_comment

begin_class
DECL|class|RoleParameters
specifier|public
class|class
name|RoleParameters
block|{
DECL|field|role
specifier|private
specifier|final
name|Role
name|role
decl_stmt|;
DECL|field|replicas
specifier|private
name|int
name|replicas
decl_stmt|;
DECL|field|launchCommand
specifier|private
name|String
name|launchCommand
decl_stmt|;
DECL|field|dockerImage
specifier|private
name|String
name|dockerImage
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
decl_stmt|;
DECL|method|RoleParameters (Role role, int replicas, String launchCommand, String dockerImage, Resource resource)
specifier|public
name|RoleParameters
parameter_list|(
name|Role
name|role
parameter_list|,
name|int
name|replicas
parameter_list|,
name|String
name|launchCommand
parameter_list|,
name|String
name|dockerImage
parameter_list|,
name|Resource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|role
operator|=
name|role
expr_stmt|;
name|this
operator|.
name|replicas
operator|=
name|replicas
expr_stmt|;
name|this
operator|.
name|launchCommand
operator|=
name|launchCommand
expr_stmt|;
name|this
operator|.
name|dockerImage
operator|=
name|dockerImage
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
DECL|method|createEmpty (Role role)
specifier|public
specifier|static
name|RoleParameters
name|createEmpty
parameter_list|(
name|Role
name|role
parameter_list|)
block|{
return|return
operator|new
name|RoleParameters
argument_list|(
name|role
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getRole ()
specifier|public
name|Role
name|getRole
parameter_list|()
block|{
return|return
name|role
return|;
block|}
DECL|method|getReplicas ()
specifier|public
name|int
name|getReplicas
parameter_list|()
block|{
return|return
name|replicas
return|;
block|}
DECL|method|getLaunchCommand ()
specifier|public
name|String
name|getLaunchCommand
parameter_list|()
block|{
return|return
name|launchCommand
return|;
block|}
DECL|method|setLaunchCommand (String launchCommand)
specifier|public
name|void
name|setLaunchCommand
parameter_list|(
name|String
name|launchCommand
parameter_list|)
block|{
name|this
operator|.
name|launchCommand
operator|=
name|launchCommand
expr_stmt|;
block|}
DECL|method|getDockerImage ()
specifier|public
name|String
name|getDockerImage
parameter_list|()
block|{
return|return
name|dockerImage
return|;
block|}
DECL|method|setDockerImage (String dockerImage)
specifier|public
name|void
name|setDockerImage
parameter_list|(
name|String
name|dockerImage
parameter_list|)
block|{
name|this
operator|.
name|dockerImage
operator|=
name|dockerImage
expr_stmt|;
block|}
DECL|method|getResource ()
specifier|public
name|Resource
name|getResource
parameter_list|()
block|{
return|return
name|resource
return|;
block|}
DECL|method|setResource (Resource resource)
specifier|public
name|void
name|setResource
parameter_list|(
name|Resource
name|resource
parameter_list|)
block|{
name|this
operator|.
name|resource
operator|=
name|resource
expr_stmt|;
block|}
DECL|method|setReplicas (int replicas)
specifier|public
name|void
name|setReplicas
parameter_list|(
name|int
name|replicas
parameter_list|)
block|{
name|this
operator|.
name|replicas
operator|=
name|replicas
expr_stmt|;
block|}
block|}
end_class

end_unit

