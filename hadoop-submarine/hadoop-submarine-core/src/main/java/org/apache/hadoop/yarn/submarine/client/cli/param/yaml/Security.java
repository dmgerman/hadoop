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
comment|/**  * Class that holds values found in 'security' section of YAML configuration.  */
end_comment

begin_class
DECL|class|Security
specifier|public
class|class
name|Security
block|{
DECL|field|keytab
specifier|private
name|String
name|keytab
decl_stmt|;
DECL|field|principal
specifier|private
name|String
name|principal
decl_stmt|;
DECL|field|distributeKeytab
specifier|private
name|boolean
name|distributeKeytab
decl_stmt|;
DECL|method|getKeytab ()
specifier|public
name|String
name|getKeytab
parameter_list|()
block|{
return|return
name|keytab
return|;
block|}
DECL|method|setKeytab (String keytab)
specifier|public
name|void
name|setKeytab
parameter_list|(
name|String
name|keytab
parameter_list|)
block|{
name|this
operator|.
name|keytab
operator|=
name|keytab
expr_stmt|;
block|}
DECL|method|getPrincipal ()
specifier|public
name|String
name|getPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
DECL|method|setPrincipal (String principal)
specifier|public
name|void
name|setPrincipal
parameter_list|(
name|String
name|principal
parameter_list|)
block|{
name|this
operator|.
name|principal
operator|=
name|principal
expr_stmt|;
block|}
DECL|method|isDistributeKeytab ()
specifier|public
name|boolean
name|isDistributeKeytab
parameter_list|()
block|{
return|return
name|distributeKeytab
return|;
block|}
DECL|method|setDistributeKeytab (boolean distributeKeytab)
specifier|public
name|void
name|setDistributeKeytab
parameter_list|(
name|boolean
name|distributeKeytab
parameter_list|)
block|{
name|this
operator|.
name|distributeKeytab
operator|=
name|distributeKeytab
expr_stmt|;
block|}
block|}
end_class

end_unit

