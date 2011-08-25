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
DECL|interface|CounterGroup
specifier|public
interface|interface
name|CounterGroup
block|{
DECL|method|getName ()
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
DECL|method|getDisplayName ()
specifier|public
specifier|abstract
name|String
name|getDisplayName
parameter_list|()
function_decl|;
DECL|method|getAllCounters ()
specifier|public
specifier|abstract
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|getAllCounters
parameter_list|()
function_decl|;
DECL|method|getCounter (String key)
specifier|public
specifier|abstract
name|Counter
name|getCounter
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|setName (String name)
specifier|public
specifier|abstract
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|setDisplayName (String displayName)
specifier|public
specifier|abstract
name|void
name|setDisplayName
parameter_list|(
name|String
name|displayName
parameter_list|)
function_decl|;
DECL|method|addAllCounters (Map<String, Counter> counters)
specifier|public
specifier|abstract
name|void
name|addAllCounters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Counter
argument_list|>
name|counters
parameter_list|)
function_decl|;
DECL|method|setCounter (String key, Counter value)
specifier|public
specifier|abstract
name|void
name|setCounter
parameter_list|(
name|String
name|key
parameter_list|,
name|Counter
name|value
parameter_list|)
function_decl|;
DECL|method|removeCounter (String key)
specifier|public
specifier|abstract
name|void
name|removeCounter
parameter_list|(
name|String
name|key
parameter_list|)
function_decl|;
DECL|method|clearCounters ()
specifier|public
specifier|abstract
name|void
name|clearCounters
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

