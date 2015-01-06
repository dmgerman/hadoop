begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|BufferedInputStream
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
name|commons
operator|.
name|io
operator|.
name|Charsets
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
name|fs
operator|.
name|Seekable
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
name|bzip2
operator|.
name|BZip2Constants
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
name|bzip2
operator|.
name|CBZip2InputStream
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
name|bzip2
operator|.
name|CBZip2OutputStream
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
name|bzip2
operator|.
name|Bzip2Factory
import|;
end_import

begin_comment
comment|/**  * This class provides output and input streams for bzip2 compression  * and decompression.  It uses the native bzip2 library on the system  * if possible, else it uses a pure-Java implementation of the bzip2  * algorithm.  The configuration parameter  * io.compression.codec.bzip2.library can be used to control this  * behavior.  *  * In the pure-Java mode, the Compressor and Decompressor interfaces  * are not implemented.  Therefore, in that mode, those methods of  * CompressionCodec which have a Compressor or Decompressor type  * argument, throw UnsupportedOperationException.  *  * Currently, support for splittability is available only in the  * pure-Java mode; therefore, if a SplitCompressionInputStream is  * requested, the pure-Java implementation is used, regardless of the  * setting of the configuration parameter mentioned above.  */
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
DECL|class|BZip2Codec
specifier|public
class|class
name|BZip2Codec
implements|implements
name|Configurable
implements|,
name|SplittableCompressionCodec
block|{
DECL|field|HEADER
specifier|private
specifier|static
specifier|final
name|String
name|HEADER
init|=
literal|"BZ"
decl_stmt|;
DECL|field|HEADER_LEN
specifier|private
specifier|static
specifier|final
name|int
name|HEADER_LEN
init|=
name|HEADER
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|field|SUB_HEADER
specifier|private
specifier|static
specifier|final
name|String
name|SUB_HEADER
init|=
literal|"h9"
decl_stmt|;
DECL|field|SUB_HEADER_LEN
specifier|private
specifier|static
specifier|final
name|int
name|SUB_HEADER_LEN
init|=
name|SUB_HEADER
operator|.
name|length
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
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
comment|/**   * Creates a new instance of BZip2Codec.   */
DECL|method|BZip2Codec ()
specifier|public
name|BZip2Codec
parameter_list|()
block|{ }
comment|/**    * Create a {@link CompressionOutputStream} that will write to the given    * {@link OutputStream}.    *    * @param out        the location for the final output stream    * @return a stream the user can write uncompressed data to, to have it     *         compressed    * @throws IOException    */
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
comment|/**    * Create a {@link CompressionOutputStream} that will write to the given    * {@link OutputStream} with the given {@link Compressor}.    *    * @param out        the location for the final output stream    * @param compressor compressor to use    * @return a stream the user can write uncompressed data to, to have it     *         compressed    * @throws IOException    */
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
name|Bzip2Factory
operator|.
name|isNativeBzip2Loaded
argument_list|(
name|conf
argument_list|)
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
operator|new
name|BZip2CompressionOutputStream
argument_list|(
name|out
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
return|return
name|Bzip2Factory
operator|.
name|getBzip2CompressorType
argument_list|(
name|conf
argument_list|)
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
return|return
name|Bzip2Factory
operator|.
name|getBzip2Compressor
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Create a {@link CompressionInputStream} that will read from the given    * input stream and return a stream for uncompressed data.    *    * @param in the stream to read compressed bytes from    * @return a stream to read uncompressed bytes from    * @throws IOException    */
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
comment|/**    * Create a {@link CompressionInputStream} that will read from the given    * {@link InputStream} with the given {@link Decompressor}, and return a     * stream for uncompressed data.    *    * @param in           the stream to read compressed bytes from    * @param decompressor decompressor to use    * @return a stream to read uncompressed bytes from    * @throws IOException    */
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
return|return
name|Bzip2Factory
operator|.
name|isNativeBzip2Loaded
argument_list|(
name|conf
argument_list|)
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
operator|new
name|BZip2CompressionInputStream
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**    * Creates CompressionInputStream to be used to read off uncompressed data    * in one of the two reading modes. i.e. Continuous or Blocked reading modes    *    * @param seekableIn The InputStream    * @param start The start offset into the compressed stream    * @param end The end offset into the compressed stream    * @param readMode Controls whether progress is reported continuously or    *                 only at block boundaries.    *    * @return CompressionInputStream for BZip2 aligned at block boundaries    */
DECL|method|createInputStream (InputStream seekableIn, Decompressor decompressor, long start, long end, READ_MODE readMode)
specifier|public
name|SplitCompressionInputStream
name|createInputStream
parameter_list|(
name|InputStream
name|seekableIn
parameter_list|,
name|Decompressor
name|decompressor
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|READ_MODE
name|readMode
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
operator|(
name|seekableIn
operator|instanceof
name|Seekable
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"seekableIn must be an instance of "
operator|+
name|Seekable
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|//find the position of first BZip2 start up marker
operator|(
operator|(
name|Seekable
operator|)
name|seekableIn
operator|)
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// BZip2 start of block markers are of 6 bytes.  But the very first block
comment|// also has "BZh9", making it 10 bytes.  This is the common case.  But at
comment|// time stream might start without a leading BZ.
specifier|final
name|long
name|FIRST_BZIP2_BLOCK_MARKER_POSITION
init|=
name|CBZip2InputStream
operator|.
name|numberOfBytesTillNextMarker
argument_list|(
name|seekableIn
argument_list|)
decl_stmt|;
name|long
name|adjStart
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0L
argument_list|,
name|start
operator|-
name|FIRST_BZIP2_BLOCK_MARKER_POSITION
argument_list|)
decl_stmt|;
operator|(
operator|(
name|Seekable
operator|)
name|seekableIn
operator|)
operator|.
name|seek
argument_list|(
name|adjStart
argument_list|)
expr_stmt|;
name|SplitCompressionInputStream
name|in
init|=
operator|new
name|BZip2CompressionInputStream
argument_list|(
name|seekableIn
argument_list|,
name|adjStart
argument_list|,
name|end
argument_list|,
name|readMode
argument_list|)
decl_stmt|;
comment|// The following if clause handles the following case:
comment|// Assume the following scenario in BZip2 compressed stream where
comment|// . represent compressed data.
comment|// .....[48 bit Block].....[48 bit   Block].....[48 bit Block]...
comment|// ........................[47 bits][1 bit].....[48 bit Block]...
comment|// ................................^[Assume a Byte alignment here]
comment|// ........................................^^[current position of stream]
comment|// .....................^^[We go back 10 Bytes in stream and find a Block marker]
comment|// ........................................^^[We align at wrong position!]
comment|// ...........................................................^^[While this pos is correct]
if|if
condition|(
name|in
operator|.
name|getPos
argument_list|()
operator|<
name|start
condition|)
block|{
operator|(
operator|(
name|Seekable
operator|)
name|seekableIn
operator|)
operator|.
name|seek
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|in
operator|=
operator|new
name|BZip2CompressionInputStream
argument_list|(
name|seekableIn
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|readMode
argument_list|)
expr_stmt|;
block|}
return|return
name|in
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
return|return
name|Bzip2Factory
operator|.
name|getBzip2DecompressorType
argument_list|(
name|conf
argument_list|)
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
return|return
name|Bzip2Factory
operator|.
name|getBzip2Decompressor
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**   * .bz2 is recognized as the default extension for compressed BZip2 files   *   * @return A String telling the default bzip2 file extension   */
annotation|@
name|Override
DECL|method|getDefaultExtension ()
specifier|public
name|String
name|getDefaultExtension
parameter_list|()
block|{
return|return
literal|".bz2"
return|;
block|}
DECL|class|BZip2CompressionOutputStream
specifier|private
specifier|static
class|class
name|BZip2CompressionOutputStream
extends|extends
name|CompressionOutputStream
block|{
comment|// class data starts here//
DECL|field|output
specifier|private
name|CBZip2OutputStream
name|output
decl_stmt|;
DECL|field|needsReset
specifier|private
name|boolean
name|needsReset
decl_stmt|;
comment|// class data ends here//
DECL|method|BZip2CompressionOutputStream (OutputStream out)
specifier|public
name|BZip2CompressionOutputStream
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
name|needsReset
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|writeStreamHeader ()
specifier|private
name|void
name|writeStreamHeader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|super
operator|.
name|out
operator|!=
literal|null
condition|)
block|{
comment|// The compressed bzip2 stream should start with the
comment|// identifying characters BZ. Caller of CBZip2OutputStream
comment|// i.e. this class must write these characters.
name|out
operator|.
name|write
argument_list|(
name|HEADER
operator|.
name|getBytes
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|needsReset
condition|)
block|{
comment|// In the case that nothing is written to this stream, we still need to
comment|// write out the header before closing, otherwise the stream won't be
comment|// recognized by BZip2CompressionInputStream.
name|internalReset
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|output
operator|.
name|finish
argument_list|()
expr_stmt|;
name|needsReset
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|internalReset ()
specifier|private
name|void
name|internalReset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|needsReset
condition|)
block|{
name|needsReset
operator|=
literal|false
expr_stmt|;
name|writeStreamHeader
argument_list|()
expr_stmt|;
name|this
operator|.
name|output
operator|=
operator|new
name|CBZip2OutputStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|resetState ()
specifier|public
name|void
name|resetState
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Cannot write to out at this point because out might not be ready
comment|// yet, as in SequenceFile.Writer implementation.
name|needsReset
operator|=
literal|true
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
if|if
condition|(
name|needsReset
condition|)
block|{
name|internalReset
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|output
operator|.
name|write
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|needsReset
condition|)
block|{
name|internalReset
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|output
operator|.
name|write
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
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
if|if
condition|(
name|needsReset
condition|)
block|{
comment|// In the case that nothing is written to this stream, we still need to
comment|// write out the header before closing, otherwise the stream won't be
comment|// recognized by BZip2CompressionInputStream.
name|internalReset
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
name|this
operator|.
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|needsReset
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// end of class BZip2CompressionOutputStream
comment|/**    * This class is capable to de-compress BZip2 data in two modes;    * CONTINOUS and BYBLOCK.  BYBLOCK mode makes it possible to    * do decompression starting any arbitrary position in the stream.    *    * So this facility can easily be used to parallelize decompression    * of a large BZip2 file for performance reasons.  (It is exactly    * done so for Hadoop framework.  See LineRecordReader for an    * example).  So one can break the file (of course logically) into    * chunks for parallel processing.  These "splits" should be like    * default Hadoop splits (e.g as in FileInputFormat getSplit metod).    * So this code is designed and tested for FileInputFormat's way    * of splitting only.    */
DECL|class|BZip2CompressionInputStream
specifier|private
specifier|static
class|class
name|BZip2CompressionInputStream
extends|extends
name|SplitCompressionInputStream
block|{
comment|// class data starts here//
DECL|field|input
specifier|private
name|CBZip2InputStream
name|input
decl_stmt|;
DECL|field|needsReset
name|boolean
name|needsReset
decl_stmt|;
DECL|field|bufferedIn
specifier|private
name|BufferedInputStream
name|bufferedIn
decl_stmt|;
DECL|field|isHeaderStripped
specifier|private
name|boolean
name|isHeaderStripped
init|=
literal|false
decl_stmt|;
DECL|field|isSubHeaderStripped
specifier|private
name|boolean
name|isSubHeaderStripped
init|=
literal|false
decl_stmt|;
DECL|field|readMode
specifier|private
name|READ_MODE
name|readMode
init|=
name|READ_MODE
operator|.
name|CONTINUOUS
decl_stmt|;
DECL|field|startingPos
specifier|private
name|long
name|startingPos
init|=
literal|0L
decl_stmt|;
comment|// Following state machine handles different states of compressed stream
comment|// position
comment|// HOLD : Don't advertise compressed stream position
comment|// ADVERTISE : Read 1 more character and advertise stream position
comment|// See more comments about it before updatePos method.
DECL|enum|POS_ADVERTISEMENT_STATE_MACHINE
specifier|private
enum|enum
name|POS_ADVERTISEMENT_STATE_MACHINE
block|{
DECL|enumConstant|HOLD
DECL|enumConstant|ADVERTISE
name|HOLD
block|,
name|ADVERTISE
block|}
empty_stmt|;
DECL|field|posSM
name|POS_ADVERTISEMENT_STATE_MACHINE
name|posSM
init|=
name|POS_ADVERTISEMENT_STATE_MACHINE
operator|.
name|HOLD
decl_stmt|;
DECL|field|compressedStreamPosition
name|long
name|compressedStreamPosition
init|=
literal|0
decl_stmt|;
comment|// class data ends here//
DECL|method|BZip2CompressionInputStream (InputStream in)
specifier|public
name|BZip2CompressionInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|in
argument_list|,
literal|0L
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|READ_MODE
operator|.
name|CONTINUOUS
argument_list|)
expr_stmt|;
block|}
DECL|method|BZip2CompressionInputStream (InputStream in, long start, long end, READ_MODE readMode)
specifier|public
name|BZip2CompressionInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|,
name|READ_MODE
name|readMode
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
expr_stmt|;
name|needsReset
operator|=
literal|false
expr_stmt|;
name|bufferedIn
operator|=
operator|new
name|BufferedInputStream
argument_list|(
name|super
operator|.
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|startingPos
operator|=
name|super
operator|.
name|getPos
argument_list|()
expr_stmt|;
name|this
operator|.
name|readMode
operator|=
name|readMode
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|startingPos
operator|==
literal|0
condition|)
block|{
comment|// We only strip header if it is start of file
name|bufferedIn
operator|=
name|readStreamHeader
argument_list|()
expr_stmt|;
block|}
name|input
operator|=
operator|new
name|CBZip2InputStream
argument_list|(
name|bufferedIn
argument_list|,
name|readMode
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|isHeaderStripped
condition|)
block|{
name|input
operator|.
name|updateReportedByteCount
argument_list|(
name|HEADER_LEN
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|isSubHeaderStripped
condition|)
block|{
name|input
operator|.
name|updateReportedByteCount
argument_list|(
name|SUB_HEADER_LEN
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|updatePos
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|readStreamHeader ()
specifier|private
name|BufferedInputStream
name|readStreamHeader
parameter_list|()
throws|throws
name|IOException
block|{
comment|// We are flexible enough to allow the compressed stream not to
comment|// start with the header of BZ. So it works fine either we have
comment|// the header or not.
if|if
condition|(
name|super
operator|.
name|in
operator|!=
literal|null
condition|)
block|{
name|bufferedIn
operator|.
name|mark
argument_list|(
name|HEADER_LEN
argument_list|)
expr_stmt|;
name|byte
index|[]
name|headerBytes
init|=
operator|new
name|byte
index|[
name|HEADER_LEN
index|]
decl_stmt|;
name|int
name|actualRead
init|=
name|bufferedIn
operator|.
name|read
argument_list|(
name|headerBytes
argument_list|,
literal|0
argument_list|,
name|HEADER_LEN
argument_list|)
decl_stmt|;
if|if
condition|(
name|actualRead
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|header
init|=
operator|new
name|String
argument_list|(
name|headerBytes
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|.
name|compareTo
argument_list|(
name|HEADER
argument_list|)
operator|!=
literal|0
condition|)
block|{
name|bufferedIn
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|isHeaderStripped
operator|=
literal|true
expr_stmt|;
comment|// In case of BYBLOCK mode, we also want to strip off
comment|// remaining two character of the header.
if|if
condition|(
name|this
operator|.
name|readMode
operator|==
name|READ_MODE
operator|.
name|BYBLOCK
condition|)
block|{
name|actualRead
operator|=
name|bufferedIn
operator|.
name|read
argument_list|(
name|headerBytes
argument_list|,
literal|0
argument_list|,
name|SUB_HEADER_LEN
argument_list|)
expr_stmt|;
if|if
condition|(
name|actualRead
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|isSubHeaderStripped
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|bufferedIn
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to read bzip2 stream."
argument_list|)
throw|;
block|}
return|return
name|bufferedIn
return|;
block|}
comment|// end of method
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
name|needsReset
condition|)
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|needsReset
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|/**     * This method updates compressed stream position exactly when the     * client of this code has read off at least one byte passed any BZip2     * end of block marker.     *     * This mechanism is very helpful to deal with data level record     * boundaries. Please see constructor and next methods of     * org.apache.hadoop.mapred.LineRecordReader as an example usage of this     * feature.  We elaborate it with an example in the following:     *     * Assume two different scenarios of the BZip2 compressed stream, where     * [m] represent end of block, \n is line delimiter and . represent compressed     * data.     *     * ............[m]......\n.......     *     * ..........\n[m]......\n.......     *     * Assume that end is right after [m].  In the first case the reading     * will stop at \n and there is no need to read one more line.  (To see the     * reason of reading one more line in the next() method is explained in LineRecordReader.)     * While in the second example LineRecordReader needs to read one more line     * (till the second \n).  Now since BZip2Codecs only update position     * at least one byte passed a maker, so it is straight forward to differentiate     * between the two cases mentioned.     *     */
DECL|method|read (byte[] b, int off, int len)
specifier|public
name|int
name|read
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
if|if
condition|(
name|needsReset
condition|)
block|{
name|internalReset
argument_list|()
expr_stmt|;
block|}
name|int
name|result
init|=
literal|0
decl_stmt|;
name|result
operator|=
name|this
operator|.
name|input
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
name|BZip2Constants
operator|.
name|END_OF_BLOCK
condition|)
block|{
name|this
operator|.
name|posSM
operator|=
name|POS_ADVERTISEMENT_STATE_MACHINE
operator|.
name|ADVERTISE
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|posSM
operator|==
name|POS_ADVERTISEMENT_STATE_MACHINE
operator|.
name|ADVERTISE
condition|)
block|{
name|result
operator|=
name|this
operator|.
name|input
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|off
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// This is the precise time to update compressed stream position
comment|// to the client of this code.
name|this
operator|.
name|updatePos
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|posSM
operator|=
name|POS_ADVERTISEMENT_STATE_MACHINE
operator|.
name|HOLD
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|read ()
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
index|[]
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|int
name|result
init|=
name|this
operator|.
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
return|return
operator|(
name|result
operator|<
literal|0
operator|)
condition|?
name|result
else|:
operator|(
name|b
index|[
literal|0
index|]
operator|&
literal|0xff
operator|)
return|;
block|}
DECL|method|internalReset ()
specifier|private
name|void
name|internalReset
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|needsReset
condition|)
block|{
name|needsReset
operator|=
literal|false
expr_stmt|;
name|BufferedInputStream
name|bufferedIn
init|=
name|readStreamHeader
argument_list|()
decl_stmt|;
name|input
operator|=
operator|new
name|CBZip2InputStream
argument_list|(
name|bufferedIn
argument_list|,
name|this
operator|.
name|readMode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|resetState ()
specifier|public
name|void
name|resetState
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Cannot read from bufferedIn at this point because bufferedIn
comment|// might not be ready
comment|// yet, as in SequenceFile.Reader implementation.
name|needsReset
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getPos ()
specifier|public
name|long
name|getPos
parameter_list|()
block|{
return|return
name|this
operator|.
name|compressedStreamPosition
return|;
block|}
comment|/*      * As the comments before read method tell that      * compressed stream is advertised when at least      * one byte passed EOB have been read off.  But      * there is an exception to this rule.  When we      * construct the stream we advertise the position      * exactly at EOB.  In the following method      * shouldAddOn boolean captures this exception.      *      */
DECL|method|updatePos (boolean shouldAddOn)
specifier|private
name|void
name|updatePos
parameter_list|(
name|boolean
name|shouldAddOn
parameter_list|)
block|{
name|int
name|addOn
init|=
name|shouldAddOn
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|this
operator|.
name|compressedStreamPosition
operator|=
name|this
operator|.
name|startingPos
operator|+
name|this
operator|.
name|input
operator|.
name|getProcessedByteCount
argument_list|()
operator|+
name|addOn
expr_stmt|;
block|}
block|}
comment|// end of BZip2CompressionInputStream
block|}
end_class

end_unit

