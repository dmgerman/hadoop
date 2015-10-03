begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
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
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * An InputStream implementations which reads from some other InputStream  * but expects an exact number of bytes. Any attempts to read past the  * specified number of bytes will return as if the end of the stream  * was reached. If the end of the underlying stream is reached prior to  * the specified number of bytes, an EOFException is thrown.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|ExactSizeInputStream
specifier|public
class|class
name|ExactSizeInputStream
extends|extends
name|FilterInputStream
block|{
DECL|field|remaining
specifier|private
name|int
name|remaining
decl_stmt|;
comment|/**    * Construct an input stream that will read no more than    * 'numBytes' bytes.    *    * If an EOF occurs on the underlying stream before numBytes    * bytes have been read, an EOFException will be thrown.    *    * @param in the inputstream to wrap    * @param numBytes the number of bytes to read    */
DECL|method|ExactSizeInputStream (InputStream in, int numBytes)
specifier|public
name|ExactSizeInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|int
name|numBytes
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|numBytes
operator|>=
literal|0
argument_list|,
literal|"Negative expected bytes: "
argument_list|,
name|numBytes
argument_list|)
expr_stmt|;
name|this
operator|.
name|remaining
operator|=
name|numBytes
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Math
operator|.
name|min
argument_list|(
name|super
operator|.
name|available
argument_list|()
argument_list|,
name|remaining
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
comment|// EOF if we reached our limit
if|if
condition|(
name|remaining
operator|<=
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|final
name|int
name|result
init|=
name|super
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|>=
literal|0
condition|)
block|{
operator|--
name|remaining
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
comment|// Underlying stream reached EOF but we haven't read the expected
comment|// number of bytes.
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Premature EOF. Expected "
operator|+
name|remaining
operator|+
literal|"more bytes"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|read (@onnull final byte[] b, final int off, int len)
specifier|public
name|int
name|read
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|remaining
operator|<=
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|remaining
argument_list|)
expr_stmt|;
specifier|final
name|int
name|result
init|=
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|>=
literal|0
condition|)
block|{
name|remaining
operator|-=
name|result
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
comment|// Underlying stream reached EOF but we haven't read the expected
comment|// number of bytes.
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Premature EOF. Expected "
operator|+
name|remaining
operator|+
literal|"more bytes"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|skip (final long n)
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|result
init|=
name|super
operator|.
name|skip
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|remaining
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|>
literal|0
condition|)
block|{
name|remaining
operator|-=
name|result
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
comment|// Underlying stream reached EOF but we haven't read the expected
comment|// number of bytes.
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Premature EOF. Expected "
operator|+
name|remaining
operator|+
literal|"more bytes"
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|markSupported ()
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|mark (int readlimit)
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

