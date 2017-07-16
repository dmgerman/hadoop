begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An utility class to store a batch of DB write operations.  */
end_comment

begin_class
DECL|class|BatchOperation
specifier|public
class|class
name|BatchOperation
block|{
comment|/**    * Enum for write operations.    */
DECL|enum|Operation
specifier|public
enum|enum
name|Operation
block|{
DECL|enumConstant|DELETE
DECL|enumConstant|PUT
name|DELETE
block|,
name|PUT
block|}
DECL|field|operations
specifier|private
name|List
argument_list|<
name|SingleOperation
argument_list|>
name|operations
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|/**    * Add a PUT operation into the batch.    */
DECL|method|put (byte[] key, byte[] value)
specifier|public
name|void
name|put
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|SingleOperation
argument_list|(
name|Operation
operator|.
name|PUT
argument_list|,
name|key
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a DELETE operation into the batch.    */
DECL|method|delete (byte[] key)
specifier|public
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
block|{
name|operations
operator|.
name|add
argument_list|(
operator|new
name|SingleOperation
argument_list|(
name|Operation
operator|.
name|DELETE
argument_list|,
name|key
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getOperations ()
specifier|public
name|List
argument_list|<
name|SingleOperation
argument_list|>
name|getOperations
parameter_list|()
block|{
return|return
name|operations
return|;
block|}
comment|/**    * A SingleOperation represents a PUT or DELETE operation    * and the data the operation needs to manipulates.    */
DECL|class|SingleOperation
specifier|public
specifier|static
class|class
name|SingleOperation
block|{
DECL|field|opt
specifier|private
name|Operation
name|opt
decl_stmt|;
DECL|field|key
specifier|private
name|byte
index|[]
name|key
decl_stmt|;
DECL|field|value
specifier|private
name|byte
index|[]
name|value
decl_stmt|;
DECL|method|SingleOperation (Operation opt, byte[] key, byte[] value)
specifier|public
name|SingleOperation
parameter_list|(
name|Operation
name|opt
parameter_list|,
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|this
operator|.
name|opt
operator|=
name|opt
expr_stmt|;
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"key cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|key
operator|=
name|key
operator|.
name|clone
argument_list|()
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|clone
argument_list|()
expr_stmt|;
block|}
DECL|method|getOpt ()
specifier|public
name|Operation
name|getOpt
parameter_list|()
block|{
return|return
name|opt
return|;
block|}
DECL|method|getKey ()
specifier|public
name|byte
index|[]
name|getKey
parameter_list|()
block|{
return|return
name|key
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getValue ()
specifier|public
name|byte
index|[]
name|getValue
parameter_list|()
block|{
return|return
name|value
operator|==
literal|null
condition|?
literal|null
else|:
name|value
operator|.
name|clone
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

