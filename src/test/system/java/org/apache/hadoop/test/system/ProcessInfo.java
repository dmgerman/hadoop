begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test.system
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|system
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * Daemon system level process information.  */
end_comment

begin_interface
DECL|interface|ProcessInfo
specifier|public
interface|interface
name|ProcessInfo
extends|extends
name|Writable
block|{
comment|/**    * Get the current time in the millisecond.<br/>    *     * @return current time on daemon clock in millisecond.    */
DECL|method|currentTimeMillis ()
specifier|public
name|long
name|currentTimeMillis
parameter_list|()
function_decl|;
comment|/**    * Get the environment that was used to start the Daemon process.<br/>    *     * @return the environment variable list.    */
DECL|method|getEnv ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getEnv
parameter_list|()
function_decl|;
comment|/**    * Get the System properties of the Daemon process.<br/>    *     * @return the properties list.    */
DECL|method|getSystemProperties ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getSystemProperties
parameter_list|()
function_decl|;
comment|/**    * Get the number of active threads in Daemon VM.<br/>    *     * @return number of active threads in Daemon VM.    */
DECL|method|activeThreadCount ()
specifier|public
name|int
name|activeThreadCount
parameter_list|()
function_decl|;
comment|/**    * Get the maximum heap size that is configured for the Daemon VM.<br/>    *     * @return maximum heap size.    */
DECL|method|maxMemory ()
specifier|public
name|long
name|maxMemory
parameter_list|()
function_decl|;
comment|/**    * Get the free memory in Daemon VM.<br/>    *     * @return free memory.    */
DECL|method|freeMemory ()
specifier|public
name|long
name|freeMemory
parameter_list|()
function_decl|;
comment|/**    * Get the total used memory in Demon VM.<br/>    *     * @return total used memory.    */
DECL|method|totalMemory ()
specifier|public
name|long
name|totalMemory
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

