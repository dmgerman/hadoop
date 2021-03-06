begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.event
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|event
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|Event
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
name|event
operator|.
name|EventHandler
import|;
end_import

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
DECL|class|InlineDispatcher
specifier|public
class|class
name|InlineDispatcher
extends|extends
name|AsyncDispatcher
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|InlineDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|TestEventHandler
specifier|private
class|class
name|TestEventHandler
implements|implements
name|EventHandler
block|{
annotation|@
name|Override
DECL|method|handle (Event event)
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|dispatch
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|dispatch (Event event)
specifier|protected
name|void
name|dispatch
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Dispatching the event "
operator|+
name|event
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"."
operator|+
name|event
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|type
init|=
name|event
operator|.
name|getType
argument_list|()
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|eventDispatchers
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|eventDispatchers
operator|.
name|get
argument_list|(
name|type
argument_list|)
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|getEventHandler
parameter_list|()
block|{
return|return
operator|new
name|TestEventHandler
argument_list|()
return|;
block|}
DECL|class|EmptyEventHandler
specifier|public
specifier|static
class|class
name|EmptyEventHandler
implements|implements
name|EventHandler
argument_list|<
name|Event
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (Event event)
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
comment|//do nothing
block|}
block|}
block|}
end_class

end_unit

