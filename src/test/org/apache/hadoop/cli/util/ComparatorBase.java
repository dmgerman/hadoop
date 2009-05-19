begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  *  * Comparator interface. To define a new comparator, implement the compare  * method  */
end_comment

begin_class
DECL|class|ComparatorBase
specifier|public
specifier|abstract
class|class
name|ComparatorBase
block|{
DECL|method|ComparatorBase ()
specifier|public
name|ComparatorBase
parameter_list|()
block|{        }
comment|/**    * Compare method for the comparator class.    * @param actual output. can be null    * @param expected output. can be null    * @return true if expected output compares with the actual output, else    *         return false. If actual or expected is null, return false    */
DECL|method|compare (String actual, String expected)
specifier|public
specifier|abstract
name|boolean
name|compare
parameter_list|(
name|String
name|actual
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
block|}
end_class

end_unit

