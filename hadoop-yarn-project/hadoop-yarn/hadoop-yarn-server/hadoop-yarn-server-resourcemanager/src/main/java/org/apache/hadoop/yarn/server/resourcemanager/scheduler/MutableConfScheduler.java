begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
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
comment|/**  * Interface for a scheduler that supports changing configuration at runtime.  *  */
end_comment

begin_interface
DECL|interface|MutableConfScheduler
specifier|public
interface|interface
name|MutableConfScheduler
extends|extends
name|ResourceScheduler
block|{
comment|/**    * Get the scheduler configuration.    * @return the scheduler configuration    */
DECL|method|getConfiguration ()
name|Configuration
name|getConfiguration
parameter_list|()
function_decl|;
comment|/**    * Get queue object based on queue name.    * @param queueName the queue name    * @return the queue object    */
DECL|method|getQueue (String queueName)
name|Queue
name|getQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
function_decl|;
comment|/**    * Return whether the scheduler configuration is mutable.    * @return whether scheduler configuration is mutable or not.    */
DECL|method|isConfigurationMutable ()
name|boolean
name|isConfigurationMutable
parameter_list|()
function_decl|;
comment|/**    * Get scheduler's configuration provider, so other classes can directly    * call mutation APIs on configuration provider.    * @return scheduler's configuration provider    */
DECL|method|getMutableConfProvider ()
name|MutableConfigurationProvider
name|getMutableConfProvider
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

