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
comment|/**  * Class that holds values found in 'spec' section of YAML configuration.  */
end_comment

begin_class
DECL|class|Spec
specifier|public
class|class
name|Spec
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|jobType
specifier|private
name|String
name|jobType
decl_stmt|;
DECL|field|framework
specifier|private
name|String
name|framework
decl_stmt|;
DECL|method|getJobType ()
specifier|public
name|String
name|getJobType
parameter_list|()
block|{
return|return
name|jobType
return|;
block|}
DECL|method|setJobType (String jobtype)
specifier|public
name|void
name|setJobType
parameter_list|(
name|String
name|jobtype
parameter_list|)
block|{
name|this
operator|.
name|jobType
operator|=
name|jobtype
expr_stmt|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|setName (String name)
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
DECL|method|getFramework ()
specifier|public
name|String
name|getFramework
parameter_list|()
block|{
return|return
name|framework
return|;
block|}
DECL|method|setFramework (String framework)
specifier|public
name|void
name|setFramework
parameter_list|(
name|String
name|framework
parameter_list|)
block|{
name|this
operator|.
name|framework
operator|=
name|framework
expr_stmt|;
block|}
block|}
end_class

end_unit

