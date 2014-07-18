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
name|*
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
name|GZIPOutputStream
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
name|io
operator|.
name|compress
operator|.
name|DefaultCodec
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
name|zlib
operator|.
name|*
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
name|zlib
operator|.
name|ZlibDecompressor
operator|.
name|ZlibDirectDecompressor
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
name|util
operator|.
name|PlatformName
operator|.
name|IBM_JAVA
import|;
end_import

begin_comment
comment|/**  * This class creates gzip compressors/decompressors.   */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|GzipCodec
specifier|public
class|class
name|GzipCodec
extends|extends
name|DefaultCodec
block|{
comment|/**    * A bridge that wraps around a DeflaterOutputStream to make it     * a CompressionOutputStream.    */
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|GzipOutputStream
specifier|protected
specifier|static
class|class
name|GzipOutputStream
extends|extends
name|CompressorStream
block|{
DECL|class|ResetableGZIPOutputStream
specifier|private
specifier|static
class|class
name|ResetableGZIPOutputStream
extends|extends
name|GZIPOutputStream
block|{
DECL|field|TRAILER_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|TRAILER_SIZE
init|=
literal|8
decl_stmt|;
DECL|field|JVMVersion
specifier|public
specifier|static
specifier|final
name|String
name|JVMVersion
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
decl_stmt|;
DECL|field|HAS_BROKEN_FINISH
specifier|private
specifier|static
specifier|final
name|boolean
name|HAS_BROKEN_FINISH
init|=
operator|(
name|IBM_JAVA
operator|&&
name|JVMVersion
operator|.
name|contains
argument_list|(
literal|"1.6.0"
argument_list|)
operator|)
decl_stmt|;
DECL|method|ResetableGZIPOutputStream (OutputStream out)
specifier|public
name|ResetableGZIPOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|method|resetState ()
specifier|public
name|void
name|resetState
parameter_list|()
throws|throws
name|IOException
block|{
name|def
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**        * Override this method for HADOOP-8419.        * Override because IBM implementation calls def.end() which        * causes problem when reseting the stream for reuse.        *        */
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
name|HAS_BROKEN_FINISH
condition|)
block|{
if|if
condition|(
operator|!
name|def
operator|.
name|finished
argument_list|()
condition|)
block|{
name|def
operator|.
name|finish
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|def
operator|.
name|finished
argument_list|()
condition|)
block|{
name|int
name|i
init|=
name|def
operator|.
name|deflate
argument_list|(
name|this
operator|.
name|buf
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|buf
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|def
operator|.
name|finished
argument_list|()
operator|)
operator|&&
operator|(
name|i
operator|<=
name|this
operator|.
name|buf
operator|.
name|length
operator|-
name|TRAILER_SIZE
operator|)
condition|)
block|{
name|writeTrailer
argument_list|(
name|this
operator|.
name|buf
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|i
operator|+=
name|TRAILER_SIZE
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|this
operator|.
name|buf
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|this
operator|.
name|buf
argument_list|,
literal|0
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
name|byte
index|[]
name|arrayOfByte
init|=
operator|new
name|byte
index|[
name|TRAILER_SIZE
index|]
decl_stmt|;
name|writeTrailer
argument_list|(
name|arrayOfByte
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|arrayOfByte
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** re-implement for HADOOP-8419 because the relative method in jdk is invisible */
DECL|method|writeTrailer (byte[] paramArrayOfByte, int paramInt)
specifier|private
name|void
name|writeTrailer
parameter_list|(
name|byte
index|[]
name|paramArrayOfByte
parameter_list|,
name|int
name|paramInt
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|this
operator|.
name|crc
operator|.
name|getValue
argument_list|()
argument_list|,
name|paramArrayOfByte
argument_list|,
name|paramInt
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
name|this
operator|.
name|def
operator|.
name|getTotalIn
argument_list|()
argument_list|,
name|paramArrayOfByte
argument_list|,
name|paramInt
operator|+
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/** re-implement for HADOOP-8419 because the relative method in jdk is invisible */
DECL|method|writeInt (int paramInt1, byte[] paramArrayOfByte, int paramInt2)
specifier|private
name|void
name|writeInt
parameter_list|(
name|int
name|paramInt1
parameter_list|,
name|byte
index|[]
name|paramArrayOfByte
parameter_list|,
name|int
name|paramInt2
parameter_list|)
throws|throws
name|IOException
block|{
name|writeShort
argument_list|(
name|paramInt1
operator|&
literal|0xFFFF
argument_list|,
name|paramArrayOfByte
argument_list|,
name|paramInt2
argument_list|)
expr_stmt|;
name|writeShort
argument_list|(
name|paramInt1
operator|>>
literal|16
operator|&
literal|0xFFFF
argument_list|,
name|paramArrayOfByte
argument_list|,
name|paramInt2
operator|+
literal|2
argument_list|)
expr_stmt|;
block|}
comment|/** re-implement for HADOOP-8419 because the relative method in jdk is invisible */
DECL|method|writeShort (int paramInt1, byte[] paramArrayOfByte, int paramInt2)
specifier|private
name|void
name|writeShort
parameter_list|(
name|int
name|paramInt1
parameter_list|,
name|byte
index|[]
name|paramArrayOfByte
parameter_list|,
name|int
name|paramInt2
parameter_list|)
throws|throws
name|IOException
block|{
name|paramArrayOfByte
index|[
name|paramInt2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|paramInt1
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
name|paramArrayOfByte
index|[
operator|(
name|paramInt2
operator|+
literal|1
operator|)
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|paramInt1
operator|>>
literal|8
operator|&
literal|0xFF
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|GzipOutputStream (OutputStream out)
specifier|public
name|GzipOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|ResetableGZIPOutputStream
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allow children types to put a different type in here.      * @param out the Deflater stream to use      */
DECL|method|GzipOutputStream (CompressorStream out)
specifier|protected
name|GzipOutputStream
parameter_list|(
name|CompressorStream
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
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush ()
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
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
name|out
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write (byte[] data, int offset, int length)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
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
operator|(
operator|(
name|ResetableGZIPOutputStream
operator|)
name|out
operator|)
operator|.
name|finish
argument_list|()
expr_stmt|;
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
operator|(
operator|(
name|ResetableGZIPOutputStream
operator|)
name|out
operator|)
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
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
if|if
condition|(
operator|!
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
condition|)
block|{
return|return
operator|new
name|GzipOutputStream
argument_list|(
name|out
argument_list|)
return|;
block|}
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
return|return
operator|(
name|compressor
operator|!=
literal|null
operator|)
condition|?
operator|new
name|CompressorStream
argument_list|(
name|out
argument_list|,
name|compressor
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|)
else|:
name|createOutputStream
argument_list|(
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createCompressor ()
specifier|public
name|Compressor
name|createCompressor
parameter_list|()
block|{
return|return
operator|(
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
operator|new
name|GzipZlibCompressor
argument_list|(
name|conf
argument_list|)
else|:
literal|null
return|;
block|}
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
return|return
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
condition|?
name|GzipZlibCompressor
operator|.
name|class
else|:
literal|null
return|;
block|}
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
name|decompressor
operator|==
literal|null
condition|)
block|{
name|decompressor
operator|=
name|createDecompressor
argument_list|()
expr_stmt|;
comment|// always succeeds (or throws)
block|}
return|return
operator|new
name|DecompressorStream
argument_list|(
name|in
argument_list|,
name|decompressor
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|4
operator|*
literal|1024
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createDecompressor ()
specifier|public
name|Decompressor
name|createDecompressor
parameter_list|()
block|{
return|return
operator|(
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
operator|)
condition|?
operator|new
name|GzipZlibDecompressor
argument_list|()
else|:
operator|new
name|BuiltInGzipDecompressor
argument_list|()
return|;
block|}
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
return|return
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
condition|?
name|GzipZlibDecompressor
operator|.
name|class
else|:
name|BuiltInGzipDecompressor
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|createDirectDecompressor ()
specifier|public
name|DirectDecompressor
name|createDirectDecompressor
parameter_list|()
block|{
return|return
name|ZlibFactory
operator|.
name|isNativeZlibLoaded
argument_list|(
name|conf
argument_list|)
condition|?
operator|new
name|ZlibDecompressor
operator|.
name|ZlibDirectDecompressor
argument_list|(
name|ZlibDecompressor
operator|.
name|CompressionHeader
operator|.
name|AUTODETECT_GZIP_ZLIB
argument_list|,
literal|0
argument_list|)
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getDefaultExtension ()
specifier|public
name|String
name|getDefaultExtension
parameter_list|()
block|{
return|return
literal|".gz"
return|;
block|}
DECL|class|GzipZlibCompressor
specifier|static
specifier|final
class|class
name|GzipZlibCompressor
extends|extends
name|ZlibCompressor
block|{
DECL|method|GzipZlibCompressor ()
specifier|public
name|GzipZlibCompressor
parameter_list|()
block|{
name|super
argument_list|(
name|ZlibCompressor
operator|.
name|CompressionLevel
operator|.
name|DEFAULT_COMPRESSION
argument_list|,
name|ZlibCompressor
operator|.
name|CompressionStrategy
operator|.
name|DEFAULT_STRATEGY
argument_list|,
name|ZlibCompressor
operator|.
name|CompressionHeader
operator|.
name|GZIP_FORMAT
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|GzipZlibCompressor (Configuration conf)
specifier|public
name|GzipZlibCompressor
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ZlibFactory
operator|.
name|getCompressionLevel
argument_list|(
name|conf
argument_list|)
argument_list|,
name|ZlibFactory
operator|.
name|getCompressionStrategy
argument_list|(
name|conf
argument_list|)
argument_list|,
name|ZlibCompressor
operator|.
name|CompressionHeader
operator|.
name|GZIP_FORMAT
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|GzipZlibDecompressor
specifier|static
specifier|final
class|class
name|GzipZlibDecompressor
extends|extends
name|ZlibDecompressor
block|{
DECL|method|GzipZlibDecompressor ()
specifier|public
name|GzipZlibDecompressor
parameter_list|()
block|{
name|super
argument_list|(
name|ZlibDecompressor
operator|.
name|CompressionHeader
operator|.
name|AUTODETECT_GZIP_ZLIB
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

