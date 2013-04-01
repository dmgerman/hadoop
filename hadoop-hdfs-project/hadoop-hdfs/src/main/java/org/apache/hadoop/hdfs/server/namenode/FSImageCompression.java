begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|compress
operator|.
name|CompressionCodec
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
name|CompressionCodecFactory
import|;
end_import

begin_comment
comment|/**  * Simple container class that handles support for compressed fsimage files.  */
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
DECL|class|FSImageCompression
class|class
name|FSImageCompression
block|{
comment|/** Codec to use to save or load image, or null if the image is not compressed */
DECL|field|imageCodec
specifier|private
name|CompressionCodec
name|imageCodec
decl_stmt|;
comment|/**    * Create a "noop" compression - i.e. uncompressed    */
DECL|method|FSImageCompression ()
specifier|private
name|FSImageCompression
parameter_list|()
block|{   }
comment|/**    * Create compression using a particular codec    */
DECL|method|FSImageCompression (CompressionCodec codec)
specifier|private
name|FSImageCompression
parameter_list|(
name|CompressionCodec
name|codec
parameter_list|)
block|{
name|imageCodec
operator|=
name|codec
expr_stmt|;
block|}
comment|/**    * Create a "noop" compression - i.e. uncompressed    */
DECL|method|createNoopCompression ()
specifier|static
name|FSImageCompression
name|createNoopCompression
parameter_list|()
block|{
return|return
operator|new
name|FSImageCompression
argument_list|()
return|;
block|}
comment|/**    * Create a compression instance based on the user's configuration in the given    * Configuration object.    * @throws IOException if the specified codec is not available.    */
DECL|method|createCompression (Configuration conf)
specifier|static
name|FSImageCompression
name|createCompression
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|compressImage
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESS_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESS_DEFAULT
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|compressImage
condition|)
block|{
return|return
name|createNoopCompression
argument_list|()
return|;
block|}
name|String
name|codecClassName
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESSION_CODEC_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_IMAGE_COMPRESSION_CODEC_DEFAULT
argument_list|)
decl_stmt|;
return|return
name|createCompression
argument_list|(
name|conf
argument_list|,
name|codecClassName
argument_list|)
return|;
block|}
comment|/**    * Create a compression instance using the codec specified by    *<code>codecClassName</code>    */
DECL|method|createCompression (Configuration conf, String codecClassName)
specifier|private
specifier|static
name|FSImageCompression
name|createCompression
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|codecClassName
parameter_list|)
throws|throws
name|IOException
block|{
name|CompressionCodecFactory
name|factory
init|=
operator|new
name|CompressionCodecFactory
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|CompressionCodec
name|codec
init|=
name|factory
operator|.
name|getCodecByClassName
argument_list|(
name|codecClassName
argument_list|)
decl_stmt|;
if|if
condition|(
name|codec
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not a supported codec: "
operator|+
name|codecClassName
argument_list|)
throw|;
block|}
return|return
operator|new
name|FSImageCompression
argument_list|(
name|codec
argument_list|)
return|;
block|}
comment|/**    * Create a compression instance based on a header read from an input stream.    * @throws IOException if the specified codec is not available or the    * underlying IO fails.    */
DECL|method|readCompressionHeader ( Configuration conf, DataInput in)
specifier|static
name|FSImageCompression
name|readCompressionHeader
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|isCompressed
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isCompressed
condition|)
block|{
return|return
name|createNoopCompression
argument_list|()
return|;
block|}
else|else
block|{
name|String
name|codecClassName
init|=
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|createCompression
argument_list|(
name|conf
argument_list|,
name|codecClassName
argument_list|)
return|;
block|}
block|}
comment|/**    * Unwrap a compressed input stream by wrapping it with a decompressor based    * on this codec. If this instance represents no compression, simply adds    * buffering to the input stream.    * @return a buffered stream that provides uncompressed data    * @throws IOException If the decompressor cannot be instantiated or an IO    * error occurs.    */
DECL|method|unwrapInputStream (InputStream is)
name|DataInputStream
name|unwrapInputStream
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|imageCodec
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
name|imageCodec
operator|.
name|createInputStream
argument_list|(
name|is
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
name|is
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Write out a header to the given stream that indicates the chosen    * compression codec, and return the same stream wrapped with that codec.    * If no codec is specified, simply adds buffering to the stream, so that    * the returned stream is always buffered.    *     * @param os The stream to write header to and wrap. This stream should    * be unbuffered.    * @return A stream wrapped with the specified compressor, or buffering    * if compression is not enabled.    * @throws IOException if an IO error occurs or the compressor cannot be    * instantiated    */
DECL|method|writeHeaderAndWrapStream (OutputStream os)
name|DataOutputStream
name|writeHeaderAndWrapStream
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputStream
name|dos
init|=
operator|new
name|DataOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|dos
operator|.
name|writeBoolean
argument_list|(
name|imageCodec
operator|!=
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|imageCodec
operator|!=
literal|null
condition|)
block|{
name|String
name|codecClassName
init|=
name|imageCodec
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
decl_stmt|;
name|Text
operator|.
name|writeString
argument_list|(
name|dos
argument_list|,
name|codecClassName
argument_list|)
expr_stmt|;
return|return
operator|new
name|DataOutputStream
argument_list|(
name|imageCodec
operator|.
name|createOutputStream
argument_list|(
name|os
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// use a buffered output stream
return|return
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|os
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|imageCodec
operator|!=
literal|null
condition|)
block|{
return|return
literal|"codec "
operator|+
name|imageCodec
operator|.
name|getClass
argument_list|()
operator|.
name|getCanonicalName
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|"no compression"
return|;
block|}
block|}
block|}
end_class

end_unit

