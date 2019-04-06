begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli.param.yaml
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
name|param
operator|.
name|yaml
package|;
end_package

begin_comment
comment|/**  * Root class of YAML configuration.  */
end_comment

begin_class
DECL|class|YamlConfigFile
specifier|public
class|class
name|YamlConfigFile
block|{
DECL|field|spec
specifier|private
name|Spec
name|spec
decl_stmt|;
DECL|field|configs
specifier|private
name|Configs
name|configs
decl_stmt|;
DECL|field|roles
specifier|private
name|Roles
name|roles
decl_stmt|;
DECL|field|scheduling
specifier|private
name|Scheduling
name|scheduling
decl_stmt|;
DECL|field|security
specifier|private
name|Security
name|security
decl_stmt|;
DECL|field|tensorBoard
specifier|private
name|TensorBoard
name|tensorBoard
decl_stmt|;
DECL|method|getSpec ()
specifier|public
name|Spec
name|getSpec
parameter_list|()
block|{
return|return
name|spec
return|;
block|}
DECL|method|setSpec (Spec spec)
specifier|public
name|void
name|setSpec
parameter_list|(
name|Spec
name|spec
parameter_list|)
block|{
name|this
operator|.
name|spec
operator|=
name|spec
expr_stmt|;
block|}
DECL|method|getConfigs ()
specifier|public
name|Configs
name|getConfigs
parameter_list|()
block|{
return|return
name|configs
return|;
block|}
DECL|method|setConfigs (Configs configs)
specifier|public
name|void
name|setConfigs
parameter_list|(
name|Configs
name|configs
parameter_list|)
block|{
name|this
operator|.
name|configs
operator|=
name|configs
expr_stmt|;
block|}
DECL|method|getRoles ()
specifier|public
name|Roles
name|getRoles
parameter_list|()
block|{
return|return
name|roles
return|;
block|}
DECL|method|setRoles (Roles roles)
specifier|public
name|void
name|setRoles
parameter_list|(
name|Roles
name|roles
parameter_list|)
block|{
name|this
operator|.
name|roles
operator|=
name|roles
expr_stmt|;
block|}
DECL|method|getScheduling ()
specifier|public
name|Scheduling
name|getScheduling
parameter_list|()
block|{
return|return
name|scheduling
return|;
block|}
DECL|method|setScheduling (Scheduling scheduling)
specifier|public
name|void
name|setScheduling
parameter_list|(
name|Scheduling
name|scheduling
parameter_list|)
block|{
name|this
operator|.
name|scheduling
operator|=
name|scheduling
expr_stmt|;
block|}
DECL|method|getSecurity ()
specifier|public
name|Security
name|getSecurity
parameter_list|()
block|{
return|return
name|security
return|;
block|}
DECL|method|setSecurity (Security security)
specifier|public
name|void
name|setSecurity
parameter_list|(
name|Security
name|security
parameter_list|)
block|{
name|this
operator|.
name|security
operator|=
name|security
expr_stmt|;
block|}
DECL|method|getTensorBoard ()
specifier|public
name|TensorBoard
name|getTensorBoard
parameter_list|()
block|{
return|return
name|tensorBoard
return|;
block|}
DECL|method|setTensorBoard (TensorBoard tensorBoard)
specifier|public
name|void
name|setTensorBoard
parameter_list|(
name|TensorBoard
name|tensorBoard
parameter_list|)
block|{
name|this
operator|.
name|tensorBoard
operator|=
name|tensorBoard
expr_stmt|;
block|}
block|}
end_class

end_unit

