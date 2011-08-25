begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
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
DECL|interface|Counters
specifier|public
interface|interface
name|Counters
block|{
DECL|method|getAllCounterGroups ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|CounterGroup
argument_list|>
name|getAllCounterGroups
parameter_list|()
function_decl|;
DECL|method|getCounterGroup (String key)
specifier|public
specifier|abstract
name|CounterGroup
name|getCounterGroup
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|getCounter (Enum<?> key)
specifier|public
specifier|abstract
name|Counter
name|getCounter
parameter_list|(
name|Enum
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
function_decl|;
DECL|method|addAllCounterGroups (Map<String, CounterGroup> counterGroups)
specifier|public
specifier|abstract
name|void
name|addAllCounterGroups
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|CounterGroup
argument_list|>
name|counterGroups
parameter_list|)
function_decl|;
DECL|method|setCounterGroup (String key, CounterGroup value)
specifier|public
specifier|abstract
name|void
name|setCounterGroup
parameter_list|(
name|String
name|key
parameter_list|,
name|CounterGroup
name|value
parameter_list|)
function_decl|;
DECL|method|removeCounterGroup (String key)
specifier|public
specifier|abstract
name|void
name|removeCounterGroup
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|clearCounterGroups ()
specifier|public
specifier|abstract
name|void
name|clearCounterGroups
parameter_list|()
function_decl|;
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
block|}
end_interface

end_unit

