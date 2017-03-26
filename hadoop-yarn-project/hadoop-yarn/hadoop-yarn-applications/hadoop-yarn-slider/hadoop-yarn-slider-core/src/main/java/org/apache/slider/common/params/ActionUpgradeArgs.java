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
name|Parameter
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
annotation|@
name|Parameters
argument_list|(
name|commandNames
operator|=
block|{
name|SliderActions
operator|.
name|ACTION_UPGRADE
block|}
argument_list|,
name|commandDescription
operator|=
name|SliderActions
operator|.
name|DESCRIBE_ACTION_UPGRADE
argument_list|)
DECL|class|ActionUpgradeArgs
specifier|public
class|class
name|ActionUpgradeArgs
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
name|ACTION_UPGRADE
return|;
block|}
comment|//  TODO upgrade container
comment|//  @Parameter(names={ARG_CONTAINERS}, variableArity = true,
comment|//             description = "stop specific containers")
comment|//  public List<String> containers = new ArrayList<>(0);
comment|//
comment|//  @Parameter(names={ARG_COMPONENTS}, variableArity = true,
comment|//      description = "stop all containers of specific components")
comment|//  public List<String> components = new ArrayList<>(0);
comment|//
comment|//  @Parameter(names = {ARG_FORCE},
comment|//      description = "force spec upgrade operation")
comment|//  public boolean force;
block|}
end_class

end_unit

