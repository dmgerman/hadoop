begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor
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
name|monitor
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
import|;
end_import

begin_interface
DECL|interface|SchedulingEditPolicy
specifier|public
interface|interface
name|SchedulingEditPolicy
block|{
DECL|method|init (Configuration config, RMContext context, ResourceScheduler scheduler)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|RMContext
name|context
parameter_list|,
name|ResourceScheduler
name|scheduler
parameter_list|)
function_decl|;
comment|/**    * This method is invoked at regular intervals. Internally the policy is    * allowed to track containers and affect the scheduler. The "actions"    * performed are passed back through an EventHandler.    */
DECL|method|editSchedule ()
name|void
name|editSchedule
parameter_list|()
function_decl|;
DECL|method|getMonitoringInterval ()
name|long
name|getMonitoringInterval
parameter_list|()
function_decl|;
DECL|method|getPolicyName ()
name|String
name|getPolicyName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

