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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_CRYPTO_BUFFER_SIZE_DEFAULT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_CRYPTO_BUFFER_SIZE_KEY
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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|conf
operator|.
name|Configuration
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
name|Seekable
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

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|CryptoStreamUtils
specifier|public
class|class
name|CryptoStreamUtils
block|{
DECL|field|MIN_BUFFER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MIN_BUFFER_SIZE
init|=
literal|512
decl_stmt|;
comment|/** Forcibly free the direct buffer. */
DECL|method|freeDB (ByteBuffer buffer)
specifier|public
specifier|static
name|void
name|freeDB
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|instanceof
name|sun
operator|.
name|nio
operator|.
name|ch
operator|.
name|DirectBuffer
condition|)
block|{
specifier|final
name|sun
operator|.
name|misc
operator|.
name|Cleaner
name|bufferCleaner
init|=
operator|(
operator|(
name|sun
operator|.
name|nio
operator|.
name|ch
operator|.
name|DirectBuffer
operator|)
name|buffer
operator|)
operator|.
name|cleaner
argument_list|()
decl_stmt|;
name|bufferCleaner
operator|.
name|clean
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Read crypto buffer size */
DECL|method|getBufferSize (Configuration conf)
specifier|public
specifier|static
name|int
name|getBufferSize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|conf
operator|.
name|getInt
argument_list|(
name|HADOOP_SECURITY_CRYPTO_BUFFER_SIZE_KEY
argument_list|,
name|HADOOP_SECURITY_CRYPTO_BUFFER_SIZE_DEFAULT
argument_list|)
return|;
block|}
comment|/** Check and floor buffer size */
DECL|method|checkBufferSize (CryptoCodec codec, int bufferSize)
specifier|public
specifier|static
name|int
name|checkBufferSize
parameter_list|(
name|CryptoCodec
name|codec
parameter_list|,
name|int
name|bufferSize
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bufferSize
operator|>=
name|MIN_BUFFER_SIZE
argument_list|,
literal|"Minimum value of buffer size is "
operator|+
name|MIN_BUFFER_SIZE
operator|+
literal|"."
argument_list|)
expr_stmt|;
return|return
name|bufferSize
operator|-
name|bufferSize
operator|%
name|codec
operator|.
name|getCipherSuite
argument_list|()
operator|.
name|getAlgorithmBlockSize
argument_list|()
return|;
block|}
comment|/**    * If input stream is {@link org.apache.hadoop.fs.Seekable}, return it's    * current position, otherwise return 0;    */
DECL|method|getInputStreamOffset (InputStream in)
specifier|public
specifier|static
name|long
name|getInputStreamOffset
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|instanceof
name|Seekable
condition|)
block|{
return|return
operator|(
operator|(
name|Seekable
operator|)
name|in
operator|)
operator|.
name|getPos
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

