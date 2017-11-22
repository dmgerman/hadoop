begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|commit
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
comment|/**  * Exception raised on validation failures; kept as an IOException  * for consistency with other failures.  */
end_comment

begin_class
DECL|class|ValidationFailure
specifier|public
class|class
name|ValidationFailure
extends|extends
name|IOException
block|{
comment|/**    * Create an instance with string formatting applied to the message    * and arguments.    * @param message error message    * @param args optional list of arguments    */
DECL|method|ValidationFailure (String message, Object... args)
specifier|public
name|ValidationFailure
parameter_list|(
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Verify that a condition holds.    * @param expression expression which must be true    * @param message message to raise on a failure    * @param args arguments for the message formatting    * @throws ValidationFailure on a failure    */
DECL|method|verify (boolean expression, String message, Object... args)
specifier|public
specifier|static
name|void
name|verify
parameter_list|(
name|boolean
name|expression
parameter_list|,
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
throws|throws
name|ValidationFailure
block|{
if|if
condition|(
operator|!
name|expression
condition|)
block|{
throw|throw
operator|new
name|ValidationFailure
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

