begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
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
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
name|DataInputStream
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
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|Deflater
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DeflaterOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|InflaterInputStream
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/** A base-class for Writables which store themselves compressed and lazily  * inflate on field access.  This is useful for large objects whose fields are  * not be altered during a map or reduce operation: leaving the field data  * compressed makes copying the instance from one file to another much  * faster. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|CompressedWritable
specifier|public
specifier|abstract
class|class
name|CompressedWritable
implements|implements
name|Writable
block|{
comment|// if non-null, the compressed field data of this instance.
DECL|field|compressed
specifier|private
name|byte
index|[]
name|compressed
decl_stmt|;
DECL|method|CompressedWritable ()
specifier|public
name|CompressedWritable
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|readFields (DataInput in)
specifier|public
specifier|final
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|compressed
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|compressed
argument_list|,
literal|0
argument_list|,
name|compressed
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Must be called by all methods which access fields to ensure that the data    * has been uncompressed. */
DECL|method|ensureInflated ()
specifier|protected
name|void
name|ensureInflated
parameter_list|()
block|{
if|if
condition|(
name|compressed
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ByteArrayInputStream
name|deflated
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|compressed
argument_list|)
decl_stmt|;
name|DataInput
name|inflater
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|InflaterInputStream
argument_list|(
name|deflated
argument_list|)
argument_list|)
decl_stmt|;
name|readFieldsCompressed
argument_list|(
name|inflater
argument_list|)
expr_stmt|;
name|compressed
operator|=
literal|null
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
block|}
comment|/** Subclasses implement this instead of {@link #readFields(DataInput)}. */
DECL|method|readFieldsCompressed (DataInput in)
specifier|protected
specifier|abstract
name|void
name|readFieldsCompressed
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|write (DataOutput out)
specifier|public
specifier|final
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|compressed
operator|==
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|deflated
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|Deflater
name|deflater
init|=
operator|new
name|Deflater
argument_list|(
name|Deflater
operator|.
name|BEST_SPEED
argument_list|)
decl_stmt|;
name|DataOutputStream
name|dout
init|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|DeflaterOutputStream
argument_list|(
name|deflated
argument_list|,
name|deflater
argument_list|)
argument_list|)
decl_stmt|;
name|writeCompressed
argument_list|(
name|dout
argument_list|)
expr_stmt|;
name|dout
operator|.
name|close
argument_list|()
expr_stmt|;
name|deflater
operator|.
name|end
argument_list|()
expr_stmt|;
name|compressed
operator|=
name|deflated
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|compressed
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
block|}
comment|/** Subclasses implement this instead of {@link #write(DataOutput)}. */
DECL|method|writeCompressed (DataOutput out)
specifier|protected
specifier|abstract
name|void
name|writeCompressed
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

