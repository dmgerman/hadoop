begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
package|;
end_package

begin_comment
comment|/**  * Thrown to indicate that the specific codec is not supported.  */
end_comment

begin_class
DECL|class|UnsupportedCodecException
specifier|public
class|class
name|UnsupportedCodecException
extends|extends
name|RuntimeException
block|{
comment|/** Default constructor */
DECL|method|UnsupportedCodecException ()
specifier|public
name|UnsupportedCodecException
parameter_list|()
block|{   }
comment|/**    * Constructs an UnsupportedCodecException with the specified    * detail message.    *     * @param message the detail message    */
DECL|method|UnsupportedCodecException (String message)
specifier|public
name|UnsupportedCodecException
parameter_list|(
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
comment|/**    * Constructs a new exception with the specified detail message and    * cause.    *     * @param message the detail message    * @param cause the cause    */
DECL|method|UnsupportedCodecException (String message, Throwable cause)
specifier|public
name|UnsupportedCodecException
parameter_list|(
name|String
name|message
parameter_list|,
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
comment|/**    * Constructs a new exception with the specified cause.    *     * @param cause the cause    */
DECL|method|UnsupportedCodecException (Throwable cause)
specifier|public
name|UnsupportedCodecException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6713920435487942224L
decl_stmt|;
block|}
end_class

end_unit

