begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|ProviderService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|actions
operator|.
name|QueueAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|management
operator|.
name|MetricsAndMonitoring
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|AppState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
operator|.
name|StateAccessForProviders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|application
operator|.
name|resources
operator|.
name|ContentCache
import|;
end_import

begin_comment
comment|/**  * Interface to pass information from the Slider AppMaster to the WebApp  */
end_comment

begin_interface
DECL|interface|WebAppApi
specifier|public
interface|interface
name|WebAppApi
block|{
comment|/**    * The {@link AppState} for the current cluster    */
DECL|method|getAppState ()
name|StateAccessForProviders
name|getAppState
parameter_list|()
function_decl|;
comment|/**    * The {@link ProviderService} for the current cluster    */
DECL|method|getProviderService ()
name|ProviderService
name|getProviderService
parameter_list|()
function_decl|;
comment|/**    * Registry operations accessor    * @return registry access    */
DECL|method|getRegistryOperations ()
name|RegistryOperations
name|getRegistryOperations
parameter_list|()
function_decl|;
comment|/**    * Metrics and monitoring service    * @return the (singleton) instance    */
DECL|method|getMetricsAndMonitoring ()
name|MetricsAndMonitoring
name|getMetricsAndMonitoring
parameter_list|()
function_decl|;
comment|/**    * Get the queue accessor    * @return the immediate and scheduled queues    */
DECL|method|getQueues ()
name|QueueAccess
name|getQueues
parameter_list|()
function_decl|;
comment|/**    * Local cache of content    * @return the cache    */
DECL|method|getContentCache ()
name|ContentCache
name|getContentCache
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

