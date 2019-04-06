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
comment|/**  * Class that holds values found in 'tensorboard' section of YAML configuration.  */
end_comment

begin_class
DECL|class|TensorBoard
specifier|public
class|class
name|TensorBoard
block|{
DECL|field|dockerImage
specifier|private
name|String
name|dockerImage
decl_stmt|;
DECL|field|resources
specifier|private
name|String
name|resources
decl_stmt|;
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
DECL|method|getResources ()
specifier|public
name|String
name|getResources
parameter_list|()
block|{
return|return
name|resources
return|;
block|}
DECL|method|setResources (String resources)
specifier|public
name|void
name|setResources
parameter_list|(
name|String
name|resources
parameter_list|)
block|{
name|this
operator|.
name|resources
operator|=
name|resources
expr_stmt|;
block|}
block|}
end_class

end_unit

