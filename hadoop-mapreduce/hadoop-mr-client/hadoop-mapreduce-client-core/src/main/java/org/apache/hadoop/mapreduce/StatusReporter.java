begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|StatusReporter
specifier|public
specifier|abstract
class|class
name|StatusReporter
block|{
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
DECL|method|progress ()
specifier|public
specifier|abstract
name|void
name|progress
parameter_list|()
function_decl|;
comment|/**    * Get the current progress.    * @return a number between 0.0 and 1.0 (inclusive) indicating the attempt's     * progress.    */
DECL|method|getProgress ()
specifier|public
specifier|abstract
name|float
name|getProgress
parameter_list|()
function_decl|;
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
block|}
end_class

end_unit

