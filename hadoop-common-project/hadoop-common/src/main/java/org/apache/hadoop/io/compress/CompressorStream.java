begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|compress
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
name|OutputStream
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
name|compress
operator|.
name|CompressionOutputStream
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
name|compress
operator|.
name|Compressor
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CompressorStream
specifier|public
class|class
name|CompressorStream
extends|extends
name|CompressionOutputStream
block|{
DECL|field|compressor
specifier|protected
name|Compressor
name|compressor
decl_stmt|;
DECL|field|buffer
specifier|protected
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|closed
specifier|protected
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|CompressorStream (OutputStream out, Compressor compressor, int bufferSize)
specifier|public
name|CompressorStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|Compressor
name|compressor
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|==
literal|null
operator|||
name|compressor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|bufferSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal bufferSize"
argument_list|)
throw|;
block|}
name|this
operator|.
name|compressor
operator|=
name|compressor
expr_stmt|;
name|buffer
operator|=
operator|new
name|byte
index|[
name|bufferSize
index|]
expr_stmt|;
block|}
DECL|method|CompressorStream (OutputStream out, Compressor compressor)
specifier|public
name|CompressorStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|Compressor
name|compressor
parameter_list|)
block|{
name|this
argument_list|(
name|out
argument_list|,
name|compressor
argument_list|,
literal|512
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allow derived classes to directly set the underlying stream.    *     * @param out Underlying output stream.    */
DECL|method|CompressorStream (OutputStream out)
specifier|protected
name|CompressorStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (byte[] b, int off, int len)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Sanity checks
if|if
condition|(
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"write beyond end of stream"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|off
operator||
name|len
operator||
operator|(
name|off
operator|+
name|len
operator|)
operator||
operator|(
name|b
operator|.
name|length
operator|-
operator|(
name|off
operator|+
name|len
operator|)
operator|)
operator|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|compressor
operator|.
name|setInput
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|compressor
operator|.
name|needsInput
argument_list|()
condition|)
block|{
name|compress
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|compress ()
specifier|protected
name|void
name|compress
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|compressor
operator|.
name|compress
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|compressor
operator|.
name|finish
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|compressor
operator|.
name|finished
argument_list|()
condition|)
block|{
name|compress
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|resetState ()
specifier|public
name|void
name|resetState
parameter_list|()
throws|throws
name|IOException
block|{
name|compressor
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|field|oneByte
specifier|private
name|byte
index|[]
name|oneByte
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
annotation|@
name|Override
DECL|method|write (int b)
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|oneByte
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|b
operator|&
literal|0xff
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|oneByte
argument_list|,
literal|0
argument_list|,
name|oneByte
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

