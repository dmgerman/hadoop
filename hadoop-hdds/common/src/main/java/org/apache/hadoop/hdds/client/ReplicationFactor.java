begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * The replication factor to be used while writing key into ozone.  */
end_comment

begin_enum
DECL|enum|ReplicationFactor
specifier|public
enum|enum
name|ReplicationFactor
block|{
DECL|enumConstant|ONE
name|ONE
argument_list|(
literal|1
argument_list|)
block|,
DECL|enumConstant|THREE
name|THREE
argument_list|(
literal|3
argument_list|)
block|;
comment|/**    * Integer representation of replication.    */
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
comment|/**    * Initializes ReplicationFactor with value.    * @param value replication value    */
DECL|method|ReplicationFactor (int value)
name|ReplicationFactor
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**    * Returns enum value corresponding to the int value.    * @param value replication value    * @return ReplicationFactor    */
DECL|method|valueOf (int value)
specifier|public
specifier|static
name|ReplicationFactor
name|valueOf
parameter_list|(
name|int
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|1
condition|)
block|{
return|return
name|ONE
return|;
block|}
if|if
condition|(
name|value
operator|==
literal|3
condition|)
block|{
return|return
name|THREE
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported value: "
operator|+
name|value
argument_list|)
throw|;
block|}
comment|/**    * Returns integer representation of ReplicationFactor.    * @return replication value    */
DECL|method|getValue ()
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
end_enum

end_unit

