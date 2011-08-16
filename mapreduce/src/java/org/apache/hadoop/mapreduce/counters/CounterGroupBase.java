begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.counters
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|counters
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
name|io
operator|.
name|Writable
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
name|mapreduce
operator|.
name|Counter
import|;
end_import

begin_comment
comment|/**  * The common counter group interface.  *  * @param<T> type of the counter for the group  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|CounterGroupBase
specifier|public
interface|interface
name|CounterGroupBase
parameter_list|<
name|T
extends|extends
name|Counter
parameter_list|>
extends|extends
name|Writable
extends|,
name|Iterable
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * Get the internal name of the group    * @return the internal name    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Get the display name of the group.    * @return the human readable name    */
DECL|method|getDisplayName ()
name|String
name|getDisplayName
parameter_list|()
function_decl|;
comment|/**    * Set the display name of the group    * @param displayName of the group    */
DECL|method|setDisplayName (String displayName)
name|void
name|setDisplayName
parameter_list|(
name|String
name|displayName
parameter_list|)
function_decl|;
comment|/** Add a counter to this group.    * @param counter to add    */
DECL|method|addCounter (T counter)
name|void
name|addCounter
parameter_list|(
name|T
name|counter
parameter_list|)
function_decl|;
comment|/**    * Add a counter to this group    * @param name  of the counter    * @param displayName of the counter    * @param value of the counter    * @return the counter    */
DECL|method|addCounter (String name, String displayName, long value)
name|T
name|addCounter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|displayName
parameter_list|,
name|long
name|value
parameter_list|)
function_decl|;
comment|/**    * Find a counter in the group.    * @param counterName the name of the counter    * @param displayName the display name of the counter    * @return the counter that was found or added    */
DECL|method|findCounter (String counterName, String displayName)
name|T
name|findCounter
parameter_list|(
name|String
name|counterName
parameter_list|,
name|String
name|displayName
parameter_list|)
function_decl|;
comment|/**    * Find a counter in the group    * @param counterName the name of the counter    * @param create create the counter if not found if true    * @return the counter that was found or added or null if create is false    */
DECL|method|findCounter (String counterName, boolean create)
name|T
name|findCounter
parameter_list|(
name|String
name|counterName
parameter_list|,
name|boolean
name|create
parameter_list|)
function_decl|;
comment|/**    * Find a counter in the group.    * @param counterName the name of the counter    * @return the counter that was found or added    */
DECL|method|findCounter (String counterName)
name|T
name|findCounter
parameter_list|(
name|String
name|counterName
parameter_list|)
function_decl|;
comment|/**    * @return the number of counters in this group.    */
DECL|method|size ()
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Increment all counters by a group of counters    * @param rightGroup  the group to be added to this group    */
DECL|method|incrAllCounters (CounterGroupBase<T> rightGroup)
name|void
name|incrAllCounters
parameter_list|(
name|CounterGroupBase
argument_list|<
name|T
argument_list|>
name|rightGroup
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

