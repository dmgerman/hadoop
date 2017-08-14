begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_import
import|import
name|com
operator|.
name|beust
operator|.
name|jcommander
operator|.
name|Parameters
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
name|service
operator|.
name|client
operator|.
name|params
operator|.
name|AbstractActionArgs
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
name|service
operator|.
name|client
operator|.
name|params
operator|.
name|SliderActions
import|;
end_import

begin_comment
comment|/**  * The version command  */
end_comment

begin_class
annotation|@
name|Parameters
argument_list|(
name|commandNames
operator|=
block|{
name|SliderActions
operator|.
name|ACTION_VERSION
block|}
argument_list|,
name|commandDescription
operator|=
name|SliderActions
operator|.
name|DESCRIBE_ACTION_VERSION
argument_list|)
DECL|class|ActionVersionArgs
specifier|public
class|class
name|ActionVersionArgs
extends|extends
name|AbstractActionArgs
block|{
annotation|@
name|Override
DECL|method|getActionName ()
specifier|public
name|String
name|getActionName
parameter_list|()
block|{
return|return
name|SliderActions
operator|.
name|ACTION_VERSION
return|;
block|}
DECL|method|getMinParams ()
specifier|public
name|int
name|getMinParams
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**    * This action does not need hadoop services    * @return false    */
annotation|@
name|Override
DECL|method|getHadoopServicesRequired ()
specifier|public
name|boolean
name|getHadoopServicesRequired
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

