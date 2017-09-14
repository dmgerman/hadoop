begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.client.params
package|package
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
name|conf
operator|.
name|Configuration
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
name|utils
operator|.
name|SliderUtils
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
name|exceptions
operator|.
name|BadCommandArgumentsException
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
name|exceptions
operator|.
name|ErrorStrings
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
name|exceptions
operator|.
name|SliderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Client CLI Args  */
end_comment

begin_class
DECL|class|ClientArgs
specifier|public
class|class
name|ClientArgs
extends|extends
name|CommonArgs
block|{
comment|// =========================================================
comment|// Keep all of these in alphabetical order. Thanks.
comment|// =========================================================
DECL|field|actionBuildArgs
specifier|private
specifier|final
name|ActionBuildArgs
name|actionBuildArgs
init|=
operator|new
name|ActionBuildArgs
argument_list|()
decl_stmt|;
DECL|field|actionClientArgs
specifier|private
specifier|final
name|ActionClientArgs
name|actionClientArgs
init|=
operator|new
name|ActionClientArgs
argument_list|()
decl_stmt|;
DECL|field|actionCreateArgs
specifier|private
specifier|final
name|ActionCreateArgs
name|actionCreateArgs
init|=
operator|new
name|ActionCreateArgs
argument_list|()
decl_stmt|;
DECL|field|actionDependencyArgs
specifier|private
specifier|final
name|ActionDependencyArgs
name|actionDependencyArgs
init|=
operator|new
name|ActionDependencyArgs
argument_list|()
decl_stmt|;
DECL|field|actionDestroyArgs
specifier|private
specifier|final
name|ActionDestroyArgs
name|actionDestroyArgs
init|=
operator|new
name|ActionDestroyArgs
argument_list|()
decl_stmt|;
DECL|field|actionExistsArgs
specifier|private
specifier|final
name|ActionExistsArgs
name|actionExistsArgs
init|=
operator|new
name|ActionExistsArgs
argument_list|()
decl_stmt|;
DECL|field|actionFlexArgs
specifier|private
specifier|final
name|ActionFlexArgs
name|actionFlexArgs
init|=
operator|new
name|ActionFlexArgs
argument_list|()
decl_stmt|;
DECL|field|actionFreezeArgs
specifier|private
specifier|final
name|ActionFreezeArgs
name|actionFreezeArgs
init|=
operator|new
name|ActionFreezeArgs
argument_list|()
decl_stmt|;
DECL|field|actionHelpArgs
specifier|private
specifier|final
name|ActionHelpArgs
name|actionHelpArgs
init|=
operator|new
name|ActionHelpArgs
argument_list|()
decl_stmt|;
DECL|field|actionKeytabArgs
specifier|private
specifier|final
name|ActionKeytabArgs
name|actionKeytabArgs
init|=
operator|new
name|ActionKeytabArgs
argument_list|()
decl_stmt|;
DECL|field|actionListArgs
specifier|private
specifier|final
name|ActionListArgs
name|actionListArgs
init|=
operator|new
name|ActionListArgs
argument_list|()
decl_stmt|;
DECL|field|actionRegistryArgs
specifier|private
specifier|final
name|ActionRegistryArgs
name|actionRegistryArgs
init|=
operator|new
name|ActionRegistryArgs
argument_list|()
decl_stmt|;
DECL|field|actionResolveArgs
specifier|private
specifier|final
name|ActionResolveArgs
name|actionResolveArgs
init|=
operator|new
name|ActionResolveArgs
argument_list|()
decl_stmt|;
DECL|field|actionResourceArgs
specifier|private
specifier|final
name|ActionResourceArgs
name|actionResourceArgs
init|=
operator|new
name|ActionResourceArgs
argument_list|()
decl_stmt|;
DECL|field|actionStatusArgs
specifier|private
specifier|final
name|ActionStatusArgs
name|actionStatusArgs
init|=
operator|new
name|ActionStatusArgs
argument_list|()
decl_stmt|;
DECL|field|actionThawArgs
specifier|private
specifier|final
name|ActionThawArgs
name|actionThawArgs
init|=
operator|new
name|ActionThawArgs
argument_list|()
decl_stmt|;
DECL|field|actionTokenArgs
specifier|private
specifier|final
name|ActionTokensArgs
name|actionTokenArgs
init|=
operator|new
name|ActionTokensArgs
argument_list|()
decl_stmt|;
DECL|field|actionUpdateArgs
specifier|private
specifier|final
name|ActionUpdateArgs
name|actionUpdateArgs
init|=
operator|new
name|ActionUpdateArgs
argument_list|()
decl_stmt|;
DECL|method|ClientArgs (String[] args)
specifier|public
name|ClientArgs
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|ClientArgs (Collection args)
specifier|public
name|ClientArgs
parameter_list|(
name|Collection
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addActionArguments ()
specifier|protected
name|void
name|addActionArguments
parameter_list|()
block|{
name|addActions
argument_list|(
name|actionBuildArgs
argument_list|,
name|actionCreateArgs
argument_list|,
name|actionDependencyArgs
argument_list|,
name|actionDestroyArgs
argument_list|,
name|actionFlexArgs
argument_list|,
name|actionFreezeArgs
argument_list|,
name|actionHelpArgs
argument_list|,
name|actionStatusArgs
argument_list|,
name|actionThawArgs
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyDefinitions (Configuration conf)
specifier|public
name|void
name|applyDefinitions
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|BadCommandArgumentsException
block|{
name|super
operator|.
name|applyDefinitions
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|getActionBuildArgs ()
specifier|public
name|ActionBuildArgs
name|getActionBuildArgs
parameter_list|()
block|{
return|return
name|actionBuildArgs
return|;
block|}
DECL|method|getActionUpdateArgs ()
specifier|public
name|ActionUpdateArgs
name|getActionUpdateArgs
parameter_list|()
block|{
return|return
name|actionUpdateArgs
return|;
block|}
DECL|method|getActionCreateArgs ()
specifier|public
name|ActionCreateArgs
name|getActionCreateArgs
parameter_list|()
block|{
return|return
name|actionCreateArgs
return|;
block|}
DECL|method|getActionDependencyArgs ()
specifier|public
name|ActionDependencyArgs
name|getActionDependencyArgs
parameter_list|()
block|{
return|return
name|actionDependencyArgs
return|;
block|}
DECL|method|getActionFlexArgs ()
specifier|public
name|ActionFlexArgs
name|getActionFlexArgs
parameter_list|()
block|{
return|return
name|actionFlexArgs
return|;
block|}
comment|/**    * Look at the chosen action and bind it as the core action for the operation.    * @throws SliderException bad argument or similar    */
annotation|@
name|Override
DECL|method|applyAction ()
specifier|public
name|void
name|applyAction
parameter_list|()
throws|throws
name|SliderException
block|{
name|String
name|action
init|=
name|getAction
argument_list|()
decl_stmt|;
if|if
condition|(
name|SliderUtils
operator|.
name|isUnset
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|action
operator|=
name|ACTION_HELP
expr_stmt|;
block|}
switch|switch
condition|(
name|action
condition|)
block|{
case|case
name|ACTION_BUILD
case|:
name|bindCoreAction
argument_list|(
name|actionBuildArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_CREATE
case|:
name|bindCoreAction
argument_list|(
name|actionCreateArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_STOP
case|:
name|bindCoreAction
argument_list|(
name|actionFreezeArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_START
case|:
name|bindCoreAction
argument_list|(
name|actionThawArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_DEPENDENCY
case|:
name|bindCoreAction
argument_list|(
name|actionDependencyArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_DESTROY
case|:
name|bindCoreAction
argument_list|(
name|actionDestroyArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_EXISTS
case|:
name|bindCoreAction
argument_list|(
name|actionExistsArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_FLEX
case|:
name|bindCoreAction
argument_list|(
name|actionFlexArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_HELP
case|:
name|bindCoreAction
argument_list|(
name|actionHelpArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_KEYTAB
case|:
name|bindCoreAction
argument_list|(
name|actionKeytabArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_LIST
case|:
name|bindCoreAction
argument_list|(
name|actionListArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_REGISTRY
case|:
name|bindCoreAction
argument_list|(
name|actionRegistryArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_RESOLVE
case|:
name|bindCoreAction
argument_list|(
name|actionResolveArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_RESOURCE
case|:
name|bindCoreAction
argument_list|(
name|actionResourceArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_STATUS
case|:
name|bindCoreAction
argument_list|(
name|actionStatusArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_TOKENS
case|:
name|bindCoreAction
argument_list|(
name|actionTokenArgs
argument_list|)
expr_stmt|;
break|break;
case|case
name|ACTION_UPDATE
case|:
name|bindCoreAction
argument_list|(
name|actionUpdateArgs
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|BadCommandArgumentsException
argument_list|(
name|ErrorStrings
operator|.
name|ERROR_UNKNOWN_ACTION
operator|+
literal|" "
operator|+
name|action
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

