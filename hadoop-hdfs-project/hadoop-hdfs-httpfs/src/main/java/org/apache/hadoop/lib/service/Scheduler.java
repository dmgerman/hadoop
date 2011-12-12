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
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_interface
DECL|interface|Scheduler
specifier|public
interface|interface
name|Scheduler
block|{
DECL|method|schedule (Callable<?> callable, long delay, long interval, TimeUnit unit)
specifier|public
specifier|abstract
name|void
name|schedule
parameter_list|(
name|Callable
argument_list|<
name|?
argument_list|>
name|callable
parameter_list|,
name|long
name|delay
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
function_decl|;
DECL|method|schedule (Runnable runnable, long delay, long interval, TimeUnit unit)
specifier|public
specifier|abstract
name|void
name|schedule
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|long
name|delay
parameter_list|,
name|long
name|interval
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

