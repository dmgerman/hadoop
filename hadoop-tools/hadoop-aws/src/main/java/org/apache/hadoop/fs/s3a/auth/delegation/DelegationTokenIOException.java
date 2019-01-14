begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
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
name|auth
operator|.
name|delegation
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
comment|/**  * General IOException for Delegation Token issues.  * Includes recommended error strings, which can be used in tests when  * looking for specific errors.  */
end_comment

begin_class
DECL|class|DelegationTokenIOException
specifier|public
class|class
name|DelegationTokenIOException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|599813827985340023L
decl_stmt|;
comment|/** Error: delegation token/token identifier class isn't the right one. */
DECL|field|TOKEN_WRONG_CLASS
specifier|public
specifier|static
specifier|final
name|String
name|TOKEN_WRONG_CLASS
init|=
literal|"Delegation token is wrong class"
decl_stmt|;
comment|/**    * The far end is expecting a different token kind than    * that which the client created.    */
DECL|field|TOKEN_MISMATCH
specifier|protected
specifier|static
specifier|final
name|String
name|TOKEN_MISMATCH
init|=
literal|"Token mismatch"
decl_stmt|;
DECL|method|DelegationTokenIOException (final String message)
specifier|public
name|DelegationTokenIOException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|DelegationTokenIOException (final String message, final Throwable cause)
specifier|public
name|DelegationTokenIOException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

