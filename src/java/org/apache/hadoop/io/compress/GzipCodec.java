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
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
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
name|ZlibCompressor
operator|.
name|CompressionLevel
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
name|ZlibCompressor
operator|.
name|CompressionStrategy
import|;
end_import

begin_comment
comment|/**  * This class creates gzip compressors/decompressors.   */
end_comment

begin_class
DECL|class|GzipCodec
specifier|public
class|class
name|GzipCodec
extends|extends
name|DefaultCodec
block|{
comment|/**    * A bridge that wraps around a DeflaterOutputStream to make it     * a CompressionOutputStream.    */
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
DECL|class|GzipInputStream
specifier|protected
specifier|static
class|class
name|GzipInputStream
extends|extends
name|DecompressorStream
block|{
DECL|class|ResetableGZIPInputStream
specifier|private
specifier|static
class|class
name|ResetableGZIPInputStream
extends|extends
name|GZIPInputStream
block|{
DECL|method|ResetableGZIPInputStream (InputStream in)
specifier|public
name|ResetableGZIPInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
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
name|inf
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|GzipInputStream (InputStream in)
specifier|public
name|GzipInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
operator|new
name|ResetableGZIPInputStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Allow subclasses to directly set the inflater stream.      * @throws IOException      */
DECL|method|GzipInputStream (DecompressorStream in)
specifier|protected
name|GzipInputStream
parameter_list|(
name|DecompressorStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|available ()
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|available
argument_list|()
return|;
block|}
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|()
return|;
block|}
DECL|method|read (byte[] data, int offset, int len)
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|read
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|skip (long offset)
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|skip
argument_list|(
name|offset
argument_list|)
return|;
block|}
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
name|ResetableGZIPInputStream
operator|)
name|in
operator|)
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
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
name|CompressorStream
argument_list|(
name|out
argument_list|,
name|createCompressor
argument_list|()
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
operator|new
name|GzipOutputStream
argument_list|(
name|out
argument_list|)
return|;
block|}
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
name|BuiltInZlibDeflater
operator|.
name|class
return|;
block|}
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
name|DecompressorStream
argument_list|(
name|in
argument_list|,
name|createDecompressor
argument_list|()
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
operator|new
name|GzipInputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
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
return|return
operator|(
name|decompressor
operator|!=
literal|null
operator|)
condition|?
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
else|:
name|createInputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
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
literal|null
return|;
block|}
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
name|BuiltInZlibInflater
operator|.
name|class
return|;
block|}
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

