begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|mapred
operator|.
name|Counters
operator|.
name|Counter
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
name|util
operator|.
name|Progressable
import|;
end_import

begin_comment
comment|/**   * A facility for Map-Reduce applications to report progress and update   * counters, status information etc.  *   *<p>{@link Mapper} and {@link Reducer} can use the<code>Reporter</code>  * provided to report progress or just indicate that they are alive. In   * scenarios where the application takes an insignificant amount of time to   * process individual key/value pairs, this is crucial since the framework   * might assume that the task has timed-out and kill that task.  *  *<p>Applications can also update {@link Counters} via the provided   *<code>Reporter</code> .</p>  *   * @see Progressable  * @see Counters  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|Reporter
specifier|public
interface|interface
name|Reporter
extends|extends
name|Progressable
block|{
comment|/**    * A constant of Reporter type that does nothing.    */
DECL|field|NULL
specifier|public
specifier|static
specifier|final
name|Reporter
name|NULL
init|=
operator|new
name|Reporter
argument_list|()
block|{
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|s
parameter_list|)
block|{       }
specifier|public
name|void
name|progress
parameter_list|()
block|{       }
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Counter
name|getCounter
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|incrCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|long
name|amount
parameter_list|)
block|{       }
specifier|public
name|void
name|incrCounter
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|counter
parameter_list|,
name|long
name|amount
parameter_list|)
block|{       }
specifier|public
name|InputSplit
name|getInputSplit
parameter_list|()
throws|throws
name|UnsupportedOperationException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"NULL reporter has no input"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Set the status description for the task.    *     * @param status brief description of the current status.    */
DECL|method|setStatus (String status)
specifier|public
specifier|abstract
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
function_decl|;
comment|/**    * Get the {@link Counter} of the given group with the given name.    *     * @param name counter name    * @return the<code>Counter</code> of the given group/name.    */
DECL|method|getCounter (Enum<?> name)
specifier|public
specifier|abstract
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|name
parameter_list|)
function_decl|;
comment|/**    * Get the {@link Counter} of the given group with the given name.    *     * @param group counter group    * @param name counter name    * @return the<code>Counter</code> of the given group/name.    */
DECL|method|getCounter (String group, String name)
specifier|public
specifier|abstract
name|Counter
name|getCounter
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Increments the counter identified by the key, which can be of    * any {@link Enum} type, by the specified amount.    *     * @param key key to identify the counter to be incremented. The key can be    *            be any<code>Enum</code>.     * @param amount A non-negative amount by which the counter is to     *               be incremented.    */
DECL|method|incrCounter (Enum<?> key, long amount)
specifier|public
specifier|abstract
name|void
name|incrCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|key
parameter_list|,
name|long
name|amount
parameter_list|)
function_decl|;
comment|/**    * Increments the counter identified by the group and counter name    * by the specified amount.    *     * @param group name to identify the group of the counter to be incremented.    * @param counter name to identify the counter within the group.    * @param amount A non-negative amount by which the counter is to     *               be incremented.    */
DECL|method|incrCounter (String group, String counter, long amount)
specifier|public
specifier|abstract
name|void
name|incrCounter
parameter_list|(
name|String
name|group
parameter_list|,
name|String
name|counter
parameter_list|,
name|long
name|amount
parameter_list|)
function_decl|;
comment|/**    * Get the {@link InputSplit} object for a map.    *     * @return the<code>InputSplit</code> that the map is reading from.    * @throws UnsupportedOperationException if called outside a mapper    */
DECL|method|getInputSplit ()
specifier|public
specifier|abstract
name|InputSplit
name|getInputSplit
parameter_list|()
throws|throws
name|UnsupportedOperationException
function_decl|;
comment|/**    * Get the progress of the task. Progress is represented as a number between    * 0 and 1 (inclusive).    */
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

