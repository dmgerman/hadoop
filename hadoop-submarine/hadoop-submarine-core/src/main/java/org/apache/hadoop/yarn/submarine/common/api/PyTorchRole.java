begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License. See accompanying LICENSE file.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.common.api
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
name|common
operator|.
name|api
package|;
end_package

begin_comment
comment|/**  * Enum to represent a PyTorch Role.  */
end_comment

begin_enum
DECL|enum|PyTorchRole
specifier|public
enum|enum
name|PyTorchRole
implements|implements
name|Role
block|{
DECL|enumConstant|PRIMARY_WORKER
name|PRIMARY_WORKER
argument_list|(
literal|"master"
argument_list|)
block|,
DECL|enumConstant|WORKER
name|WORKER
argument_list|(
literal|"worker"
argument_list|)
block|;
DECL|field|compName
specifier|private
name|String
name|compName
decl_stmt|;
DECL|method|PyTorchRole (String compName)
name|PyTorchRole
parameter_list|(
name|String
name|compName
parameter_list|)
block|{
name|this
operator|.
name|compName
operator|=
name|compName
expr_stmt|;
block|}
DECL|method|getComponentName ()
specifier|public
name|String
name|getComponentName
parameter_list|()
block|{
return|return
name|compName
return|;
block|}
annotation|@
name|Override
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

