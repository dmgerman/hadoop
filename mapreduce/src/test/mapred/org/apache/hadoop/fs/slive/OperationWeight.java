begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.slive
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
package|;
end_package

begin_comment
comment|/**  * Class which holds an operation and its weight (used in operation selection)  */
end_comment

begin_class
DECL|class|OperationWeight
class|class
name|OperationWeight
block|{
DECL|field|weight
specifier|private
name|double
name|weight
decl_stmt|;
DECL|field|operation
specifier|private
name|Operation
name|operation
decl_stmt|;
DECL|method|OperationWeight (Operation op, double weight)
name|OperationWeight
parameter_list|(
name|Operation
name|op
parameter_list|,
name|double
name|weight
parameter_list|)
block|{
name|this
operator|.
name|operation
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|/**    * Fetches the given operation weight    *     * @return Double    */
DECL|method|getWeight ()
name|double
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
comment|/**    * Gets the operation    *     * @return Operation    */
DECL|method|getOperation ()
name|Operation
name|getOperation
parameter_list|()
block|{
return|return
name|operation
return|;
block|}
block|}
end_class

end_unit

