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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|slive
operator|.
name|Constants
operator|.
name|OperationType
import|;
end_import

begin_comment
comment|/**  * Factory class which returns instances of operations given there operation  * type enumeration (in string or enumeration format).  */
end_comment

begin_class
DECL|class|OperationFactory
class|class
name|OperationFactory
block|{
DECL|field|typedOperations
specifier|private
name|Map
argument_list|<
name|OperationType
argument_list|,
name|Operation
argument_list|>
name|typedOperations
decl_stmt|;
DECL|field|config
specifier|private
name|ConfigExtractor
name|config
decl_stmt|;
DECL|field|rnd
specifier|private
name|Random
name|rnd
decl_stmt|;
DECL|method|OperationFactory (ConfigExtractor cfg, Random rnd)
name|OperationFactory
parameter_list|(
name|ConfigExtractor
name|cfg
parameter_list|,
name|Random
name|rnd
parameter_list|)
block|{
name|this
operator|.
name|typedOperations
operator|=
operator|new
name|HashMap
argument_list|<
name|OperationType
argument_list|,
name|Operation
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|cfg
expr_stmt|;
name|this
operator|.
name|rnd
operator|=
name|rnd
expr_stmt|;
block|}
comment|/**    * Gets an operation instance (cached) for a given operation type    *     * @param type    *          the operation type to fetch for    *     * @return Operation operation instance or null if it can not be fetched.    */
DECL|method|getOperation (OperationType type)
name|Operation
name|getOperation
parameter_list|(
name|OperationType
name|type
parameter_list|)
block|{
name|Operation
name|op
init|=
name|typedOperations
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
block|{
return|return
name|op
return|;
block|}
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|READ
case|:
name|op
operator|=
operator|new
name|ReadOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|LS
case|:
name|op
operator|=
operator|new
name|ListOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|MKDIR
case|:
name|op
operator|=
operator|new
name|MkdirOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|APPEND
case|:
name|op
operator|=
operator|new
name|AppendOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|RENAME
case|:
name|op
operator|=
operator|new
name|RenameOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|op
operator|=
operator|new
name|DeleteOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|CREATE
case|:
name|op
operator|=
operator|new
name|CreateOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
case|case
name|TRUNCATE
case|:
name|op
operator|=
operator|new
name|TruncateOp
argument_list|(
name|this
operator|.
name|config
argument_list|,
name|rnd
argument_list|)
expr_stmt|;
break|break;
block|}
name|typedOperations
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|op
argument_list|)
expr_stmt|;
return|return
name|op
return|;
block|}
block|}
end_class

end_unit

