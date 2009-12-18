begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|io
operator|.
name|DataOutputBuffer
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
name|io
operator|.
name|Text
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
name|io
operator|.
name|Writable
import|;
end_import

begin_comment
comment|/**  * An identifier that identifies a token, may contain public information   * about a token, including its kind (or type).  */
end_comment

begin_class
DECL|class|TokenIdentifier
specifier|public
specifier|abstract
class|class
name|TokenIdentifier
implements|implements
name|Writable
block|{
comment|/**    * Get the token kind    * @return the kind of the token    */
DECL|method|getKind ()
specifier|public
specifier|abstract
name|Text
name|getKind
parameter_list|()
function_decl|;
comment|/**    * Get the bytes for the token identifier    * @return the bytes of the identifier    */
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
name|DataOutputBuffer
name|buf
init|=
operator|new
name|DataOutputBuffer
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
try|try
block|{
name|this
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"i/o error in getBytes"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
return|return
name|Arrays
operator|.
name|copyOf
argument_list|(
name|buf
operator|.
name|getData
argument_list|()
argument_list|,
name|buf
operator|.
name|getLength
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

