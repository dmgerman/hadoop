begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Interface to capture operations on AttributeValue.  */
end_comment

begin_interface
DECL|interface|AttributeValue
specifier|public
interface|interface
name|AttributeValue
block|{
comment|/**    * @return original value which was set.    */
DECL|method|getValue ()
name|String
name|getValue
parameter_list|()
function_decl|;
comment|/**    * validate the value based on the type and initialize for further compare    * operations.    *    * @param value    * @throws IOException    */
DECL|method|validateAndInitializeValue (String value)
name|void
name|validateAndInitializeValue
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * compare the value against the other based on the    * AttributeExpressionOperation.    *    * @param other    * @param op    * @return true if value<code>other</code> matches the current value for the    *         operation<code>op</code>.    */
DECL|method|compareForOperation (AttributeValue other, AttributeExpressionOperation op)
name|boolean
name|compareForOperation
parameter_list|(
name|AttributeValue
name|other
parameter_list|,
name|AttributeExpressionOperation
name|op
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

