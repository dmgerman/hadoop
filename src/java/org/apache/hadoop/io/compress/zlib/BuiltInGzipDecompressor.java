begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.compress.zlib
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
operator|.
name|zlib
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
name|util
operator|.
name|zip
operator|.
name|DataFormatException
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
name|Inflater
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
name|PureJavaCrc32
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
name|Decompressor
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
name|DoNotPool
import|;
end_import

begin_comment
comment|/**  * A {@link Decompressor} based on the popular gzip compressed file format.  * http://www.gzip.org/  *  */
end_comment

begin_class
annotation|@
name|DoNotPool
DECL|class|BuiltInGzipDecompressor
specifier|public
class|class
name|BuiltInGzipDecompressor
implements|implements
name|Decompressor
block|{
DECL|field|GZIP_MAGIC_ID
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_MAGIC_ID
init|=
literal|0x8b1f
decl_stmt|;
comment|// if read as LE short int
DECL|field|GZIP_DEFLATE_METHOD
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_DEFLATE_METHOD
init|=
literal|8
decl_stmt|;
DECL|field|GZIP_FLAGBIT_HEADER_CRC
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_FLAGBIT_HEADER_CRC
init|=
literal|0x02
decl_stmt|;
DECL|field|GZIP_FLAGBIT_EXTRA_FIELD
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_FLAGBIT_EXTRA_FIELD
init|=
literal|0x04
decl_stmt|;
DECL|field|GZIP_FLAGBIT_FILENAME
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_FLAGBIT_FILENAME
init|=
literal|0x08
decl_stmt|;
DECL|field|GZIP_FLAGBIT_COMMENT
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_FLAGBIT_COMMENT
init|=
literal|0x10
decl_stmt|;
DECL|field|GZIP_FLAGBITS_RESERVED
specifier|private
specifier|static
specifier|final
name|int
name|GZIP_FLAGBITS_RESERVED
init|=
literal|0xe0
decl_stmt|;
comment|// 'true' (nowrap) => Inflater will handle raw deflate stream only
DECL|field|inflater
specifier|private
name|Inflater
name|inflater
init|=
operator|new
name|Inflater
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|userBuf
specifier|private
name|byte
index|[]
name|userBuf
init|=
literal|null
decl_stmt|;
DECL|field|userBufOff
specifier|private
name|int
name|userBufOff
init|=
literal|0
decl_stmt|;
DECL|field|userBufLen
specifier|private
name|int
name|userBufLen
init|=
literal|0
decl_stmt|;
DECL|field|localBuf
specifier|private
name|byte
index|[]
name|localBuf
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
DECL|field|localBufOff
specifier|private
name|int
name|localBufOff
init|=
literal|0
decl_stmt|;
DECL|field|headerBytesRead
specifier|private
name|int
name|headerBytesRead
init|=
literal|0
decl_stmt|;
DECL|field|trailerBytesRead
specifier|private
name|int
name|trailerBytesRead
init|=
literal|0
decl_stmt|;
DECL|field|numExtraFieldBytesRemaining
specifier|private
name|int
name|numExtraFieldBytesRemaining
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|crc
specifier|private
name|PureJavaCrc32
name|crc
init|=
operator|new
name|PureJavaCrc32
argument_list|()
decl_stmt|;
DECL|field|hasExtraField
specifier|private
name|boolean
name|hasExtraField
init|=
literal|false
decl_stmt|;
DECL|field|hasFilename
specifier|private
name|boolean
name|hasFilename
init|=
literal|false
decl_stmt|;
DECL|field|hasComment
specifier|private
name|boolean
name|hasComment
init|=
literal|false
decl_stmt|;
DECL|field|hasHeaderCRC
specifier|private
name|boolean
name|hasHeaderCRC
init|=
literal|false
decl_stmt|;
DECL|field|state
specifier|private
name|GzipStateLabel
name|state
decl_stmt|;
comment|/**    * The current state of the gzip decoder, external to the Inflater context.    * (Technically, the private variables localBuf through hasHeaderCRC are    * also part of the state, so this enum is merely the label for it.)    */
DECL|enum|GzipStateLabel
specifier|private
specifier|static
enum|enum
name|GzipStateLabel
block|{
comment|/**      * Immediately prior to or (strictly) within the 10-byte basic gzip header.      */
DECL|enumConstant|HEADER_BASIC
name|HEADER_BASIC
block|,
comment|/**      * Immediately prior to or within the optional "extra field."      */
DECL|enumConstant|HEADER_EXTRA_FIELD
name|HEADER_EXTRA_FIELD
block|,
comment|/**      * Immediately prior to or within the optional filename field.      */
DECL|enumConstant|HEADER_FILENAME
name|HEADER_FILENAME
block|,
comment|/**      * Immediately prior to or within the optional comment field.      */
DECL|enumConstant|HEADER_COMMENT
name|HEADER_COMMENT
block|,
comment|/**      * Immediately prior to or within the optional 2-byte header CRC value.      */
DECL|enumConstant|HEADER_CRC
name|HEADER_CRC
block|,
comment|/**      * Immediately prior to or within the main compressed (deflate) data stream.      */
DECL|enumConstant|DEFLATE_STREAM
name|DEFLATE_STREAM
block|,
comment|/**      * Immediately prior to or (strictly) within the 4-byte uncompressed CRC.      */
DECL|enumConstant|TRAILER_CRC
name|TRAILER_CRC
block|,
comment|/**      * Immediately prior to or (strictly) within the 4-byte uncompressed size.      */
DECL|enumConstant|TRAILER_SIZE
name|TRAILER_SIZE
block|,
comment|/**      * Immediately after the trailer (and potentially prior to the next gzip      * member/substream header), without reset() having been called.      */
DECL|enumConstant|FINISHED
name|FINISHED
block|;   }
comment|/**    * Creates a new (pure Java) gzip decompressor.    */
DECL|method|BuiltInGzipDecompressor ()
specifier|public
name|BuiltInGzipDecompressor
parameter_list|()
block|{
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_BASIC
expr_stmt|;
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// FIXME? Inflater docs say:  'it is also necessary to provide an extra
comment|//        "dummy" byte as input. This is required by the ZLIB native
comment|//        library in order to support certain optimizations.'  However,
comment|//        this does not appear to be true, and in any case, it's not
comment|//        entirely clear where the byte should go or what its value
comment|//        should be.  Perhaps it suffices to have some deflated bytes
comment|//        in the first buffer load?  (But how else would one do it?)
block|}
comment|/** {@inheritDoc} */
DECL|method|needsInput ()
specifier|public
specifier|synchronized
name|boolean
name|needsInput
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|DEFLATE_STREAM
condition|)
block|{
comment|// most common case
return|return
name|inflater
operator|.
name|needsInput
argument_list|()
return|;
block|}
comment|// see userBufLen comment at top of decompress(); currently no need to
comment|// verify userBufLen<= 0
return|return
operator|(
name|state
operator|!=
name|GzipStateLabel
operator|.
name|FINISHED
operator|)
return|;
block|}
comment|/** {@inheritDoc} */
comment|/*    * In our case, the input data includes both gzip header/trailer bytes (which    * we handle in executeState()) and deflate-stream bytes (which we hand off    * to Inflater).    *    * NOTE:  This code assumes the data passed in via b[] remains unmodified    *        until _we_ signal that it's safe to modify it (via needsInput()).    *        The alternative would require an additional buffer-copy even for    *        the bulk deflate stream, which is a performance hit we don't want    *        to absorb.  (Decompressor now documents this requirement.)    */
DECL|method|setInput (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|void
name|setInput
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
block|{
if|if
condition|(
name|b
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
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|off
argument_list|>
name|b
operator|.
name|length
operator|-
name|len
condition|)
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|userBuf
operator|=
name|b
expr_stmt|;
name|userBufOff
operator|=
name|off
expr_stmt|;
name|userBufLen
operator|=
name|len
expr_stmt|;
comment|// note:  might be zero
block|}
comment|/**    * Decompress the data (gzip header, deflate stream, gzip trailer) in the    * provided buffer.    *    * @return the number of decompressed bytes placed into b    */
comment|/* From the caller's perspective, this is where the state machine lives.    * The code is written such that we never return from decompress() with    * data remaining in userBuf unless we're in FINISHED state and there was    * data beyond the current gzip member (e.g., we're within a concatenated    * gzip stream).  If this ever changes, {@link #needsInput()} will also    * need to be modified (i.e., uncomment the userBufLen condition).    *    * The actual deflate-stream processing (decompression) is handled by    * Java's Inflater class.  Unlike the gzip header/trailer code (execute*    * methods below), the deflate stream is never copied; Inflater operates    * directly on the user's buffer.    */
DECL|method|decompress (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|int
name|decompress
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
name|int
name|numAvailBytes
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|state
operator|!=
name|GzipStateLabel
operator|.
name|DEFLATE_STREAM
condition|)
block|{
name|executeHeaderState
argument_list|()
expr_stmt|;
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return
name|numAvailBytes
return|;
block|}
block|}
comment|// "executeDeflateStreamState()"
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|DEFLATE_STREAM
condition|)
block|{
comment|// hand off user data (or what's left of it) to Inflater--but note that
comment|// Inflater may not have consumed all of previous bufferload (e.g., if
comment|// data highly compressed or output buffer very small), in which case
comment|// userBufLen will be zero
if|if
condition|(
name|userBufLen
operator|>
literal|0
condition|)
block|{
name|inflater
operator|.
name|setInput
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|userBufLen
argument_list|)
expr_stmt|;
name|userBufOff
operator|+=
name|userBufLen
expr_stmt|;
name|userBufLen
operator|=
literal|0
expr_stmt|;
block|}
comment|// now decompress it into b[]
try|try
block|{
name|numAvailBytes
operator|=
name|inflater
operator|.
name|inflate
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DataFormatException
name|dfe
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|dfe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|crc
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|numAvailBytes
argument_list|)
expr_stmt|;
comment|// CRC-32 is on _uncompressed_ data
if|if
condition|(
name|inflater
operator|.
name|finished
argument_list|()
condition|)
block|{
name|state
operator|=
name|GzipStateLabel
operator|.
name|TRAILER_CRC
expr_stmt|;
name|int
name|bytesRemaining
init|=
name|inflater
operator|.
name|getRemaining
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|bytesRemaining
operator|>=
literal|0
operator|)
operator|:
literal|"logic error: Inflater finished; byte-count is inconsistent"
assert|;
comment|// could save a copy of userBufLen at call to inflater.setInput() and
comment|// verify that bytesRemaining<= origUserBufLen, but would have to
comment|// be a (class) member variable...seems excessive for a sanity check
name|userBufOff
operator|-=
name|bytesRemaining
expr_stmt|;
name|userBufLen
operator|=
name|bytesRemaining
expr_stmt|;
comment|// or "+=", but guaranteed 0 coming in
block|}
else|else
block|{
return|return
name|numAvailBytes
return|;
comment|// minor optimization
block|}
block|}
name|executeTrailerState
argument_list|()
expr_stmt|;
return|return
name|numAvailBytes
return|;
block|}
comment|/**    * Parse the gzip header (assuming we're in the appropriate state).    * In order to deal with degenerate cases (e.g., user buffer is one byte    * long), we copy (some) header bytes to another buffer.  (Filename,    * comment, and extra-field bytes are simply skipped.)</p>    *    * See http://www.ietf.org/rfc/rfc1952.txt for the gzip spec.  Note that    * no version of gzip to date (at least through 1.4.0, 2010-01-20) supports    * the FHCRC header-CRC16 flagbit; instead, the implementation treats it    * as a multi-file continuation flag (which it also doesn't support). :-(    * Sun's JDK v6 (1.6) supports the header CRC, however, and so do we.    */
DECL|method|executeHeaderState ()
specifier|private
name|void
name|executeHeaderState
parameter_list|()
throws|throws
name|IOException
block|{
comment|// this can happen because DecompressorStream's decompress() is written
comment|// to call decompress() first, setInput() second:
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
comment|// "basic"/required header:  somewhere in first 10 bytes
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|HEADER_BASIC
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
literal|10
operator|-
name|localBufOff
argument_list|)
decl_stmt|;
comment|// (or 10-headerBytesRead)
name|checkAndCopyBytesToLocal
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|// modifies userBufLen, etc.
if|if
condition|(
name|localBufOff
operator|>=
literal|10
condition|)
block|{
comment|// should be strictly ==
name|processBasicHeader
argument_list|()
expr_stmt|;
comment|// sig, compression method, flagbits
name|localBufOff
operator|=
literal|0
expr_stmt|;
comment|// no further need for basic header
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_EXTRA_FIELD
expr_stmt|;
block|}
block|}
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
comment|// optional header stuff (extra field, filename, comment, header CRC)
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|HEADER_EXTRA_FIELD
condition|)
block|{
if|if
condition|(
name|hasExtraField
condition|)
block|{
comment|// 2 substates:  waiting for 2 bytes => get numExtraFieldBytesRemaining,
comment|// or already have 2 bytes& waiting to finish skipping specified length
if|if
condition|(
name|numExtraFieldBytesRemaining
operator|<
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
literal|2
operator|-
name|localBufOff
argument_list|)
decl_stmt|;
name|checkAndCopyBytesToLocal
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|localBufOff
operator|>=
literal|2
condition|)
block|{
name|numExtraFieldBytesRemaining
operator|=
name|readUShortLE
argument_list|(
name|localBuf
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|localBufOff
operator|=
literal|0
expr_stmt|;
block|}
block|}
if|if
condition|(
name|numExtraFieldBytesRemaining
operator|>
literal|0
operator|&&
name|userBufLen
operator|>
literal|0
condition|)
block|{
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
name|numExtraFieldBytesRemaining
argument_list|)
decl_stmt|;
name|checkAndSkipBytes
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|// modifies userBufLen, etc.
name|numExtraFieldBytesRemaining
operator|-=
name|n
expr_stmt|;
block|}
if|if
condition|(
name|numExtraFieldBytesRemaining
operator|==
literal|0
condition|)
block|{
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_FILENAME
expr_stmt|;
block|}
block|}
else|else
block|{
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_FILENAME
expr_stmt|;
block|}
block|}
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|HEADER_FILENAME
condition|)
block|{
if|if
condition|(
name|hasFilename
condition|)
block|{
name|boolean
name|doneWithFilename
init|=
name|checkAndSkipBytesUntilNull
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|doneWithFilename
condition|)
block|{
return|return;
comment|// exit early:  used up entire buffer without hitting NULL
block|}
block|}
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_COMMENT
expr_stmt|;
block|}
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|HEADER_COMMENT
condition|)
block|{
if|if
condition|(
name|hasComment
condition|)
block|{
name|boolean
name|doneWithComment
init|=
name|checkAndSkipBytesUntilNull
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|doneWithComment
condition|)
block|{
return|return;
comment|// exit early:  used up entire buffer
block|}
block|}
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_CRC
expr_stmt|;
block|}
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|HEADER_CRC
condition|)
block|{
if|if
condition|(
name|hasHeaderCRC
condition|)
block|{
assert|assert
operator|(
name|localBufOff
operator|<
literal|2
operator|)
assert|;
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
literal|2
operator|-
name|localBufOff
argument_list|)
decl_stmt|;
name|copyBytesToLocal
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|localBufOff
operator|>=
literal|2
condition|)
block|{
name|long
name|headerCRC
init|=
name|readUShortLE
argument_list|(
name|localBuf
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|headerCRC
operator|!=
operator|(
name|crc
operator|.
name|getValue
argument_list|()
operator|&
literal|0xffff
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"gzip header CRC failure"
argument_list|)
throw|;
block|}
name|localBufOff
operator|=
literal|0
expr_stmt|;
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|state
operator|=
name|GzipStateLabel
operator|.
name|DEFLATE_STREAM
expr_stmt|;
block|}
block|}
else|else
block|{
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// will reuse for CRC-32 of uncompressed data
name|state
operator|=
name|GzipStateLabel
operator|.
name|DEFLATE_STREAM
expr_stmt|;
comment|// switching to Inflater now
block|}
block|}
block|}
comment|/**    * Parse the gzip trailer (assuming we're in the appropriate state).    * In order to deal with degenerate cases (e.g., user buffer is one byte    * long), we copy trailer bytes (all 8 of 'em) to a local buffer.</p>    *    * See http://www.ietf.org/rfc/rfc1952.txt for the gzip spec.    */
DECL|method|executeTrailerState ()
specifier|private
name|void
name|executeTrailerState
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
comment|// verify that the CRC-32 of the decompressed stream matches the value
comment|// stored in the gzip trailer
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|TRAILER_CRC
condition|)
block|{
comment|// localBuf was empty before we handed off to Inflater, so we handle this
comment|// exactly like header fields
assert|assert
operator|(
name|localBufOff
operator|<
literal|4
operator|)
assert|;
comment|// initially 0, but may need multiple calls
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
literal|4
operator|-
name|localBufOff
argument_list|)
decl_stmt|;
name|copyBytesToLocal
argument_list|(
name|n
argument_list|)
expr_stmt|;
if|if
condition|(
name|localBufOff
operator|>=
literal|4
condition|)
block|{
name|long
name|streamCRC
init|=
name|readUIntLE
argument_list|(
name|localBuf
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|streamCRC
operator|!=
name|crc
operator|.
name|getValue
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"gzip stream CRC failure"
argument_list|)
throw|;
block|}
name|localBufOff
operator|=
literal|0
expr_stmt|;
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|state
operator|=
name|GzipStateLabel
operator|.
name|TRAILER_SIZE
expr_stmt|;
block|}
block|}
if|if
condition|(
name|userBufLen
operator|<=
literal|0
condition|)
block|{
return|return;
block|}
comment|// verify that the mod-2^32 decompressed stream size matches the value
comment|// stored in the gzip trailer
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|TRAILER_SIZE
condition|)
block|{
assert|assert
operator|(
name|localBufOff
operator|<
literal|4
operator|)
assert|;
comment|// initially 0, but may need multiple calls
name|int
name|n
init|=
name|Math
operator|.
name|min
argument_list|(
name|userBufLen
argument_list|,
literal|4
operator|-
name|localBufOff
argument_list|)
decl_stmt|;
name|copyBytesToLocal
argument_list|(
name|n
argument_list|)
expr_stmt|;
comment|// modifies userBufLen, etc.
if|if
condition|(
name|localBufOff
operator|>=
literal|4
condition|)
block|{
comment|// should be strictly ==
name|long
name|inputSize
init|=
name|readUIntLE
argument_list|(
name|localBuf
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputSize
operator|!=
operator|(
name|inflater
operator|.
name|getBytesWritten
argument_list|()
operator|&
literal|0xffffffff
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"stored gzip size doesn't match decompressed size"
argument_list|)
throw|;
block|}
name|localBufOff
operator|=
literal|0
expr_stmt|;
name|state
operator|=
name|GzipStateLabel
operator|.
name|FINISHED
expr_stmt|;
block|}
block|}
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|FINISHED
condition|)
block|{
return|return;
block|}
block|}
comment|/**    * Returns the total number of compressed bytes input so far, including    * gzip header/trailer bytes.</p>    *    * @return the total (non-negative) number of compressed bytes read so far    */
DECL|method|getBytesRead ()
specifier|public
specifier|synchronized
name|long
name|getBytesRead
parameter_list|()
block|{
return|return
name|headerBytesRead
operator|+
name|inflater
operator|.
name|getBytesRead
argument_list|()
operator|+
name|trailerBytesRead
return|;
block|}
comment|/**    * Returns the number of bytes remaining in the input buffer; normally    * called when finished() is true to determine amount of post-gzip-stream    * data.  Note that, other than the finished state with concatenated data    * after the end of the current gzip stream, this will never return a    * non-zero value unless called after {@link #setInput(byte[] b, int off,    * int len)} and before {@link #decompress(byte[] b, int off, int len)}.    * (That is, after {@link #decompress(byte[] b, int off, int len)} it    * always returns zero, except in finished state with concatenated data.)</p>    *    * @return the total (non-negative) number of unprocessed bytes in input    */
DECL|method|getRemaining ()
specifier|public
specifier|synchronized
name|int
name|getRemaining
parameter_list|()
block|{
return|return
name|userBufLen
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|needsDictionary ()
specifier|public
specifier|synchronized
name|boolean
name|needsDictionary
parameter_list|()
block|{
return|return
name|inflater
operator|.
name|needsDictionary
argument_list|()
return|;
block|}
comment|/** {@inheritDoc} */
DECL|method|setDictionary (byte[] b, int off, int len)
specifier|public
specifier|synchronized
name|void
name|setDictionary
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
block|{
name|inflater
operator|.
name|setDictionary
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns true if the end of the gzip substream (single "member") has been    * reached.</p>    */
DECL|method|finished ()
specifier|public
specifier|synchronized
name|boolean
name|finished
parameter_list|()
block|{
return|return
operator|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|FINISHED
operator|)
return|;
block|}
comment|/**    * Resets everything, including the input buffer, regardless of whether the    * current gzip substream is finished.</p>    */
DECL|method|reset ()
specifier|public
specifier|synchronized
name|void
name|reset
parameter_list|()
block|{
comment|// could optionally emit INFO message if state != GzipStateLabel.FINISHED
name|inflater
operator|.
name|reset
argument_list|()
expr_stmt|;
name|state
operator|=
name|GzipStateLabel
operator|.
name|HEADER_BASIC
expr_stmt|;
name|crc
operator|.
name|reset
argument_list|()
expr_stmt|;
name|userBufOff
operator|=
name|userBufLen
operator|=
literal|0
expr_stmt|;
name|localBufOff
operator|=
literal|0
expr_stmt|;
name|headerBytesRead
operator|=
literal|0
expr_stmt|;
name|trailerBytesRead
operator|=
literal|0
expr_stmt|;
name|numExtraFieldBytesRemaining
operator|=
operator|-
literal|1
expr_stmt|;
name|hasExtraField
operator|=
literal|false
expr_stmt|;
name|hasFilename
operator|=
literal|false
expr_stmt|;
name|hasComment
operator|=
literal|false
expr_stmt|;
name|hasHeaderCRC
operator|=
literal|false
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
DECL|method|end ()
specifier|public
specifier|synchronized
name|void
name|end
parameter_list|()
block|{
name|inflater
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
comment|/**    * Check ID bytes (throw if necessary), compression method (throw if not 8),    * and flag bits (set hasExtraField, hasFilename, hasComment, hasHeaderCRC).    * Ignore MTIME, XFL, OS.  Caller must ensure we have at least 10 bytes (at    * the start of localBuf).</p>    */
comment|/*    * Flag bits (remainder are reserved and must be zero):    *   bit 0   FTEXT    *   bit 1   FHCRC   (never implemented in gzip, at least through version    *                   1.4.0; instead interpreted as "continuation of multi-    *                   part gzip file," which is unsupported through 1.4.0)    *   bit 2   FEXTRA    *   bit 3   FNAME    *   bit 4   FCOMMENT    *  [bit 5   encrypted]    */
DECL|method|processBasicHeader ()
specifier|private
name|void
name|processBasicHeader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|readUShortLE
argument_list|(
name|localBuf
argument_list|,
literal|0
argument_list|)
operator|!=
name|GZIP_MAGIC_ID
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"not a gzip file"
argument_list|)
throw|;
block|}
if|if
condition|(
name|readUByte
argument_list|(
name|localBuf
argument_list|,
literal|2
argument_list|)
operator|!=
name|GZIP_DEFLATE_METHOD
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"gzip data not compressed with deflate method"
argument_list|)
throw|;
block|}
name|int
name|flg
init|=
name|readUByte
argument_list|(
name|localBuf
argument_list|,
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|flg
operator|&
name|GZIP_FLAGBITS_RESERVED
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unknown gzip format (reserved flagbits set)"
argument_list|)
throw|;
block|}
name|hasExtraField
operator|=
operator|(
operator|(
name|flg
operator|&
name|GZIP_FLAGBIT_EXTRA_FIELD
operator|)
operator|!=
literal|0
operator|)
expr_stmt|;
name|hasFilename
operator|=
operator|(
operator|(
name|flg
operator|&
name|GZIP_FLAGBIT_FILENAME
operator|)
operator|!=
literal|0
operator|)
expr_stmt|;
name|hasComment
operator|=
operator|(
operator|(
name|flg
operator|&
name|GZIP_FLAGBIT_COMMENT
operator|)
operator|!=
literal|0
operator|)
expr_stmt|;
name|hasHeaderCRC
operator|=
operator|(
operator|(
name|flg
operator|&
name|GZIP_FLAGBIT_HEADER_CRC
operator|)
operator|!=
literal|0
operator|)
expr_stmt|;
block|}
DECL|method|checkAndCopyBytesToLocal (int len)
specifier|private
name|void
name|checkAndCopyBytesToLocal
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|localBuf
argument_list|,
name|localBufOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|localBufOff
operator|+=
name|len
expr_stmt|;
comment|// alternatively, could call checkAndSkipBytes(len) for rest...
name|crc
operator|.
name|update
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|userBufOff
operator|+=
name|len
expr_stmt|;
name|userBufLen
operator|-=
name|len
expr_stmt|;
name|headerBytesRead
operator|+=
name|len
expr_stmt|;
block|}
DECL|method|checkAndSkipBytes (int len)
specifier|private
name|void
name|checkAndSkipBytes
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|crc
operator|.
name|update
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|userBufOff
operator|+=
name|len
expr_stmt|;
name|userBufLen
operator|-=
name|len
expr_stmt|;
name|headerBytesRead
operator|+=
name|len
expr_stmt|;
block|}
comment|// returns true if saw NULL, false if ran out of buffer first; called _only_
comment|// during gzip-header processing (not trailer)
comment|// (caller can check before/after state of userBufLen to compute num bytes)
DECL|method|checkAndSkipBytesUntilNull ()
specifier|private
name|boolean
name|checkAndSkipBytesUntilNull
parameter_list|()
block|{
name|boolean
name|hitNull
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|userBufLen
operator|>
literal|0
condition|)
block|{
do|do
block|{
name|hitNull
operator|=
operator|(
name|userBuf
index|[
name|userBufOff
index|]
operator|==
literal|0
operator|)
expr_stmt|;
name|crc
operator|.
name|update
argument_list|(
name|userBuf
index|[
name|userBufOff
index|]
argument_list|)
expr_stmt|;
operator|++
name|userBufOff
expr_stmt|;
operator|--
name|userBufLen
expr_stmt|;
operator|++
name|headerBytesRead
expr_stmt|;
block|}
do|while
condition|(
name|userBufLen
operator|>
literal|0
operator|&&
operator|!
name|hitNull
condition|)
do|;
block|}
return|return
name|hitNull
return|;
block|}
comment|// this one doesn't update the CRC and does support trailer processing but
comment|// otherwise is same as its "checkAnd" sibling
DECL|method|copyBytesToLocal (int len)
specifier|private
name|void
name|copyBytesToLocal
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|userBuf
argument_list|,
name|userBufOff
argument_list|,
name|localBuf
argument_list|,
name|localBufOff
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|localBufOff
operator|+=
name|len
expr_stmt|;
name|userBufOff
operator|+=
name|len
expr_stmt|;
name|userBufLen
operator|-=
name|len
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|GzipStateLabel
operator|.
name|TRAILER_CRC
operator|||
name|state
operator|==
name|GzipStateLabel
operator|.
name|TRAILER_SIZE
condition|)
block|{
name|trailerBytesRead
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
name|headerBytesRead
operator|+=
name|len
expr_stmt|;
block|}
block|}
DECL|method|readUByte (byte[] b, int off)
specifier|private
name|int
name|readUByte
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|)
block|{
return|return
operator|(
operator|(
name|int
operator|)
name|b
index|[
name|off
index|]
operator|&
literal|0xff
operator|)
return|;
block|}
comment|// caller is responsible for not overrunning buffer
DECL|method|readUShortLE (byte[] b, int off)
specifier|private
name|int
name|readUShortLE
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
operator|(
name|b
index|[
name|off
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|b
index|[
name|off
index|]
operator|&
literal|0xff
operator|)
operator|)
operator|)
operator|&
literal|0xffff
operator|)
return|;
block|}
comment|// caller is responsible for not overrunning buffer
DECL|method|readUIntLE (byte[] b, int off)
specifier|private
name|long
name|readUIntLE
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
call|(
name|long
call|)
argument_list|(
name|b
index|[
name|off
operator|+
literal|3
index|]
operator|&
literal|0xff
argument_list|)
operator|<<
literal|24
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|b
index|[
name|off
operator|+
literal|2
index|]
operator|&
literal|0xff
argument_list|)
operator|<<
literal|16
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|b
index|[
name|off
operator|+
literal|1
index|]
operator|&
literal|0xff
argument_list|)
operator|<<
literal|8
operator|)
operator||
operator|(
call|(
name|long
call|)
argument_list|(
name|b
index|[
name|off
index|]
operator|&
literal|0xff
argument_list|)
operator|)
operator|)
operator|&
literal|0xffffffff
operator|)
return|;
block|}
block|}
end_class

end_unit

