begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.typedbytes
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|typedbytes
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|BytesWritable
import|;
end_import

begin_comment
comment|/**  * Writable for typed bytes.  */
end_comment

begin_class
DECL|class|TypedBytesWritable
specifier|public
class|class
name|TypedBytesWritable
extends|extends
name|BytesWritable
block|{
comment|/** Create a TypedBytesWritable. */
DECL|method|TypedBytesWritable ()
specifier|public
name|TypedBytesWritable
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Create a TypedBytesWritable with a given byte array as initial value. */
DECL|method|TypedBytesWritable (byte[] bytes)
specifier|public
name|TypedBytesWritable
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|super
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/** Set the typed bytes from a given Java object. */
DECL|method|setValue (Object obj)
specifier|public
name|void
name|setValue
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
try|try
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|TypedBytesOutput
name|tbo
init|=
name|TypedBytesOutput
operator|.
name|get
argument_list|(
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
argument_list|)
decl_stmt|;
name|tbo
operator|.
name|write
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|set
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Get the typed bytes as a Java object. */
DECL|method|getValue ()
specifier|public
name|Object
name|getValue
parameter_list|()
block|{
try|try
block|{
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|TypedBytesInput
name|tbi
init|=
name|TypedBytesInput
operator|.
name|get
argument_list|(
operator|new
name|DataInputStream
argument_list|(
name|bais
argument_list|)
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
name|tbi
operator|.
name|read
argument_list|()
decl_stmt|;
return|return
name|obj
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Get the type code embedded in the first byte. */
DECL|method|getType ()
specifier|public
name|Type
name|getType
parameter_list|()
block|{
name|byte
index|[]
name|bytes
init|=
name|getBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|bytes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|Type
name|type
range|:
name|Type
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|code
operator|==
operator|(
name|int
operator|)
name|bytes
index|[
literal|0
index|]
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** Generate a suitable string representation. */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

