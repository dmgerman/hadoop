begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
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
name|mapreduce
operator|.
name|jobhistory
operator|.
name|HistoryEvent
import|;
end_import

begin_class
DECL|class|SingleEventEmitter
specifier|abstract
class|class
name|SingleEventEmitter
block|{
DECL|method|maybeEmitEvent (ParsedLine line, String name, HistoryEventEmitter that)
specifier|abstract
name|HistoryEvent
name|maybeEmitEvent
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|name
parameter_list|,
name|HistoryEventEmitter
name|that
parameter_list|)
function_decl|;
block|}
end_class

end_unit

