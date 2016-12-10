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
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Evolving
import|;
end_import

begin_comment
comment|/**  * Event Dispatcher interface. It dispatches events to registered   * event handlers based on event types.  *   */
end_comment

begin_interface
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Public
annotation|@
name|Evolving
DECL|interface|Dispatcher
specifier|public
interface|interface
name|Dispatcher
block|{
comment|// Configuration to make sure dispatcher crashes but doesn't do system-exit in
comment|// case of errors. By default, it should be false, so that tests are not
comment|// affected. For all daemons it should be explicitly set to true so that
comment|// daemons can crash instead of hanging around.
DECL|field|DISPATCHER_EXIT_ON_ERROR_KEY
specifier|public
specifier|static
specifier|final
name|String
name|DISPATCHER_EXIT_ON_ERROR_KEY
init|=
literal|"yarn.dispatcher.exit-on-error"
decl_stmt|;
DECL|field|DEFAULT_DISPATCHER_EXIT_ON_ERROR
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_DISPATCHER_EXIT_ON_ERROR
init|=
literal|false
decl_stmt|;
DECL|method|getEventHandler ()
name|EventHandler
argument_list|<
name|Event
argument_list|>
name|getEventHandler
parameter_list|()
function_decl|;
DECL|method|register (Class<? extends Enum> eventType, EventHandler handler)
name|void
name|register
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|eventType
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

