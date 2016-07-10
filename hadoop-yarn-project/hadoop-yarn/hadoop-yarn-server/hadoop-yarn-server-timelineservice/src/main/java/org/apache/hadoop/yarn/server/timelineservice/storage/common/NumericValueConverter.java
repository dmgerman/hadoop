begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
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
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Extends ValueConverter interface for numeric converters to support numerical  * operations such as comparison, addition, etc.  */
end_comment

begin_interface
DECL|interface|NumericValueConverter
specifier|public
interface|interface
name|NumericValueConverter
extends|extends
name|ValueConverter
extends|,
name|Comparator
argument_list|<
name|Number
argument_list|>
block|{
comment|/**    * Adds two or more numbers. If either of the numbers are null, it is taken as    * 0.    *    * @param num1 the first number to add.    * @param num2 the second number to add.    * @param numbers Rest of the numbers to be added.    * @return result after adding up the numbers.    */
DECL|method|add (Number num1, Number num2, Number...numbers)
name|Number
name|add
parameter_list|(
name|Number
name|num1
parameter_list|,
name|Number
name|num2
parameter_list|,
name|Number
modifier|...
name|numbers
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

