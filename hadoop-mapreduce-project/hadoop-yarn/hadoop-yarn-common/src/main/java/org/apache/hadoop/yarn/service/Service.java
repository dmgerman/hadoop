begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
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

begin_comment
comment|/**  * Service LifeCycle.  */
end_comment

begin_interface
DECL|interface|Service
specifier|public
interface|interface
name|Service
block|{
DECL|enum|STATE
specifier|public
enum|enum
name|STATE
block|{
DECL|enumConstant|NOTINITED
name|NOTINITED
block|,
DECL|enumConstant|INITED
name|INITED
block|,
DECL|enumConstant|STARTED
name|STARTED
block|,
DECL|enumConstant|STOPPED
name|STOPPED
block|;   }
DECL|method|init (Configuration config)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|)
function_decl|;
DECL|method|start ()
name|void
name|start
parameter_list|()
function_decl|;
DECL|method|stop ()
name|void
name|stop
parameter_list|()
function_decl|;
DECL|method|register (ServiceStateChangeListener listener)
name|void
name|register
parameter_list|(
name|ServiceStateChangeListener
name|listener
parameter_list|)
function_decl|;
DECL|method|unregister (ServiceStateChangeListener listener)
name|void
name|unregister
parameter_list|(
name|ServiceStateChangeListener
name|listener
parameter_list|)
function_decl|;
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getConfig ()
name|Configuration
name|getConfig
parameter_list|()
function_decl|;
DECL|method|getServiceState ()
name|STATE
name|getServiceState
parameter_list|()
function_decl|;
DECL|method|getStartTime ()
name|long
name|getStartTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

