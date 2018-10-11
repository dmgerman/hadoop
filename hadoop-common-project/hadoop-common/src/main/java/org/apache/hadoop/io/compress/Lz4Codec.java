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
name|conf
operator|.
name|Configurable
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
name|io
operator|.
name|compress
operator|.
name|lz4
operator|.
name|Lz4Compressor
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
name|lz4
operator|.
name|Lz4Decompressor
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
name|CommonConfigurationKeys
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
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_comment
comment|/**  * This class creates lz4 compressors/decompressors.  */
end_comment

begin_class
DECL|class|Lz4Codec
specifier|public
class|class
name|Lz4Codec
implements|implements
name|Configurable
implements|,
name|CompressionCodec
block|{
static|static
block|{
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
expr_stmt|;
block|}
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
comment|/**    * Set the configuration to be used by this object.    *    * @param conf the configuration object.    */
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
comment|/**    * Return the configuration used by this object.    *    * @return the configuration object used by this objec.    */
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Are the native lz4 libraries loaded&amp; initialized?    *    * @return true if loaded&amp; initialized, otherwise false    */
DECL|method|isNativeCodeLoaded ()
specifier|public
specifier|static
name|boolean
name|isNativeCodeLoaded
parameter_list|()
block|{
return|return
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
return|;
block|}
DECL|method|getLibraryName ()
specifier|public
specifier|static
name|String
name|getLibraryName
parameter_list|()
block|{
return|return
name|Lz4Compressor
operator|.
name|getLibraryName
argument_list|()
return|;
block|}
comment|/**    * Create a {@link CompressionOutputStream} that will write to the given    * {@link OutputStream}.    *    * @param out the location for the final output stream    * @return a stream the user can write uncompressed data to have it compressed    * @throws IOException    */
annotation|@
name|Override
DECL|method|createOutputStream (OutputStream out)
specifier|public
name|CompressionOutputStream
name|createOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CompressionCodec
operator|.
name|Util
operator|.
name|createOutputStreamWithCodecPool
argument_list|(
name|this
argument_list|,
name|conf
argument_list|,
name|out
argument_list|)
return|;
block|}
comment|/**    * Create a {@link CompressionOutputStream} that will write to the given    * {@link OutputStream} with the given {@link Compressor}.    *    * @param out        the location for the final output stream    * @param compressor compressor to use    * @return a stream the user can write uncompressed data to have it compressed    * @throws IOException    */
annotation|@
name|Override
DECL|method|createOutputStream (OutputStream out, Compressor compressor)
specifier|public
name|CompressionOutputStream
name|createOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|Compressor
name|compressor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"native lz4 library not available"
argument_list|)
throw|;
block|}
name|int
name|bufferSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|compressionOverhead
init|=
name|bufferSize
operator|/
literal|255
operator|+
literal|16
decl_stmt|;
return|return
operator|new
name|BlockCompressorStream
argument_list|(
name|out
argument_list|,
name|compressor
argument_list|,
name|bufferSize
argument_list|,
name|compressionOverhead
argument_list|)
return|;
block|}
comment|/**    * Get the type of {@link Compressor} needed by this {@link CompressionCodec}.    *    * @return the type of compressor needed by this codec.    */
annotation|@
name|Override
DECL|method|getCompressorType ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Compressor
argument_list|>
name|getCompressorType
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"native lz4 library not available"
argument_list|)
throw|;
block|}
return|return
name|Lz4Compressor
operator|.
name|class
return|;
block|}
comment|/**    * Create a new {@link Compressor} for use by this {@link CompressionCodec}.    *    * @return a new compressor for use by this codec    */
annotation|@
name|Override
DECL|method|createCompressor ()
specifier|public
name|Compressor
name|createCompressor
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"native lz4 library not available"
argument_list|)
throw|;
block|}
name|int
name|bufferSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_DEFAULT
argument_list|)
decl_stmt|;
name|boolean
name|useLz4HC
init|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_USELZ4HC_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_USELZ4HC_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Lz4Compressor
argument_list|(
name|bufferSize
argument_list|,
name|useLz4HC
argument_list|)
return|;
block|}
comment|/**    * Create a {@link CompressionInputStream} that will read from the given    * input stream.    *    * @param in the stream to read compressed bytes from    * @return a stream to read uncompressed bytes from    * @throws IOException    */
annotation|@
name|Override
DECL|method|createInputStream (InputStream in)
specifier|public
name|CompressionInputStream
name|createInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CompressionCodec
operator|.
name|Util
operator|.
name|createInputStreamWithCodecPool
argument_list|(
name|this
argument_list|,
name|conf
argument_list|,
name|in
argument_list|)
return|;
block|}
comment|/**    * Create a {@link CompressionInputStream} that will read from the given    * {@link InputStream} with the given {@link Decompressor}.    *    * @param in           the stream to read compressed bytes from    * @param decompressor decompressor to use    * @return a stream to read uncompressed bytes from    * @throws IOException    */
annotation|@
name|Override
DECL|method|createInputStream (InputStream in, Decompressor decompressor)
specifier|public
name|CompressionInputStream
name|createInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|Decompressor
name|decompressor
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"native lz4 library not available"
argument_list|)
throw|;
block|}
return|return
operator|new
name|BlockDecompressorStream
argument_list|(
name|in
argument_list|,
name|decompressor
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_DEFAULT
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the type of {@link Decompressor} needed by this {@link CompressionCodec}.    *    * @return the type of decompressor needed by this codec.    */
annotation|@
name|Override
DECL|method|getDecompressorType ()
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Decompressor
argument_list|>
name|getDecompressorType
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"native lz4 library not available"
argument_list|)
throw|;
block|}
return|return
name|Lz4Decompressor
operator|.
name|class
return|;
block|}
comment|/**    * Create a new {@link Decompressor} for use by this {@link CompressionCodec}.    *    * @return a new decompressor for use by this codec    */
annotation|@
name|Override
DECL|method|createDecompressor ()
specifier|public
name|Decompressor
name|createDecompressor
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isNativeCodeLoaded
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"native lz4 library not available"
argument_list|)
throw|;
block|}
name|int
name|bufferSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_KEY
argument_list|,
name|CommonConfigurationKeys
operator|.
name|IO_COMPRESSION_CODEC_LZ4_BUFFERSIZE_DEFAULT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Lz4Decompressor
argument_list|(
name|bufferSize
argument_list|)
return|;
block|}
comment|/**    * Get the default filename extension for this kind of compression.    *    * @return<code>.lz4</code>.    */
annotation|@
name|Override
DECL|method|getDefaultExtension ()
specifier|public
name|String
name|getDefaultExtension
parameter_list|()
block|{
return|return
literal|".lz4"
return|;
block|}
block|}
end_class

end_unit

