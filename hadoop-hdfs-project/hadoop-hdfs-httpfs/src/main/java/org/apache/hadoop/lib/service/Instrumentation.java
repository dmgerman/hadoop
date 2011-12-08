begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.service
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|service
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_interface
DECL|interface|Instrumentation
specifier|public
interface|interface
name|Instrumentation
block|{
DECL|interface|Cron
specifier|public
interface|interface
name|Cron
block|{
DECL|method|start ()
specifier|public
name|Cron
name|start
parameter_list|()
function_decl|;
DECL|method|stop ()
specifier|public
name|Cron
name|stop
parameter_list|()
function_decl|;
block|}
DECL|interface|Variable
specifier|public
interface|interface
name|Variable
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|getValue ()
name|T
name|getValue
parameter_list|()
function_decl|;
block|}
DECL|method|createCron ()
specifier|public
name|Cron
name|createCron
parameter_list|()
function_decl|;
DECL|method|incr (String group, String name, long count)
specifier|public
name|void
name|incr
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|,
name|long
name|count
parameter_list|)
function_decl|;
DECL|method|addCron (String group, String name, Cron cron)
specifier|public
name|void
name|addCron
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|,
name|Cron
name|cron
parameter_list|)
function_decl|;
DECL|method|addVariable (String group, String name, Variable<?> variable)
specifier|public
name|void
name|addVariable
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|,
name|Variable
argument_list|<
name|?
argument_list|>
name|variable
parameter_list|)
function_decl|;
comment|//sampling happens once a second
DECL|method|addSampler (String group, String name, int samplingSize, Variable<Long> variable)
specifier|public
name|void
name|addSampler
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|samplingSize
parameter_list|,
name|Variable
argument_list|<
name|Long
argument_list|>
name|variable
parameter_list|)
function_decl|;
DECL|method|getSnapshot ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
argument_list|>
argument_list|>
name|getSnapshot
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

