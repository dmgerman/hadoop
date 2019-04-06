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
comment|/**  * This class represents a section of the YAML configuration file.  */
end_comment

begin_class
DECL|class|Roles
specifier|public
class|class
name|Roles
block|{
DECL|field|worker
specifier|private
name|Role
name|worker
decl_stmt|;
DECL|field|ps
specifier|private
name|Role
name|ps
decl_stmt|;
DECL|method|getWorker ()
specifier|public
name|Role
name|getWorker
parameter_list|()
block|{
return|return
name|worker
return|;
block|}
DECL|method|setWorker (Role worker)
specifier|public
name|void
name|setWorker
parameter_list|(
name|Role
name|worker
parameter_list|)
block|{
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
block|}
DECL|method|getPs ()
specifier|public
name|Role
name|getPs
parameter_list|()
block|{
return|return
name|ps
return|;
block|}
DECL|method|setPs (Role ps)
specifier|public
name|void
name|setPs
parameter_list|(
name|Role
name|ps
parameter_list|)
block|{
name|this
operator|.
name|ps
operator|=
name|ps
expr_stmt|;
block|}
block|}
end_class

end_unit

