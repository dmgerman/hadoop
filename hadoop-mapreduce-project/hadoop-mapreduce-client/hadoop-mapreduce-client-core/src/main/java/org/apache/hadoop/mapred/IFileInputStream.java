begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|ChecksumException
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
name|HasFileDescriptor
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
name|IOUtils
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
name|ReadaheadPool
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
name|ReadaheadPool
operator|.
name|ReadaheadRequest
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
name|mapreduce
operator|.
name|MRConfig
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
name|DataChecksum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * A checksum input stream, used for IFiles.  * Used to validate the checksum of files created by {@link IFileOutputStream}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|IFileInputStream
specifier|public
class|class
name|IFileInputStream
extends|extends
name|InputStream
block|{
DECL|field|in
specifier|private
specifier|final
name|InputStream
name|in
decl_stmt|;
comment|//The input stream to be verified for checksum.
DECL|field|inFd
specifier|private
specifier|final
name|FileDescriptor
name|inFd
decl_stmt|;
comment|// the file descriptor, if it is known
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
comment|//The total length of the input file
DECL|field|dataLength
specifier|private
specifier|final
name|long
name|dataLength
decl_stmt|;
DECL|field|sum
specifier|private
name|DataChecksum
name|sum
decl_stmt|;
DECL|field|currentOffset
specifier|private
name|long
name|currentOffset
init|=
literal|0
decl_stmt|;
DECL|field|b
specifier|private
specifier|final
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
DECL|field|csum
specifier|private
name|byte
name|csum
index|[]
init|=
literal|null
decl_stmt|;
DECL|field|checksumSize
specifier|private
name|int
name|checksumSize
decl_stmt|;
DECL|field|curReadahead
specifier|private
name|ReadaheadRequest
name|curReadahead
init|=
literal|null
decl_stmt|;
DECL|field|raPool
specifier|private
name|ReadaheadPool
name|raPool
init|=
name|ReadaheadPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|field|readahead
specifier|private
name|boolean
name|readahead
decl_stmt|;
DECL|field|readaheadLength
specifier|private
name|int
name|readaheadLength
decl_stmt|;
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IFileInputStream
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|disableChecksumValidation
specifier|private
name|boolean
name|disableChecksumValidation
init|=
literal|false
decl_stmt|;
comment|/**    * Create a checksum input stream that reads    * @param in The input stream to be verified for checksum.    * @param len The length of the input stream including checksum bytes.    */
DECL|method|IFileInputStream (InputStream in, long len, Configuration conf)
specifier|public
name|IFileInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|long
name|len
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|inFd
operator|=
name|getFileDescriptorIfAvail
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|sum
operator|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|DataChecksum
operator|.
name|Type
operator|.
name|CRC32
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|checksumSize
operator|=
name|sum
operator|.
name|getChecksumSize
argument_list|()
expr_stmt|;
name|length
operator|=
name|len
expr_stmt|;
name|dataLength
operator|=
name|length
operator|-
name|checksumSize
expr_stmt|;
name|conf
operator|=
operator|(
name|conf
operator|!=
literal|null
operator|)
condition|?
name|conf
else|:
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|readahead
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|MRConfig
operator|.
name|MAPRED_IFILE_READAHEAD
argument_list|,
name|MRConfig
operator|.
name|DEFAULT_MAPRED_IFILE_READAHEAD
argument_list|)
expr_stmt|;
name|readaheadLength
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRConfig
operator|.
name|MAPRED_IFILE_READAHEAD_BYTES
argument_list|,
name|MRConfig
operator|.
name|DEFAULT_MAPRED_IFILE_READAHEAD_BYTES
argument_list|)
expr_stmt|;
name|doReadahead
argument_list|()
expr_stmt|;
block|}
DECL|method|getFileDescriptorIfAvail (InputStream in)
specifier|private
specifier|static
name|FileDescriptor
name|getFileDescriptorIfAvail
parameter_list|(
name|InputStream
name|in
parameter_list|)
block|{
name|FileDescriptor
name|fd
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|in
operator|instanceof
name|HasFileDescriptor
condition|)
block|{
name|fd
operator|=
operator|(
operator|(
name|HasFileDescriptor
operator|)
name|in
operator|)
operator|.
name|getFileDescriptor
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|in
operator|instanceof
name|FileInputStream
condition|)
block|{
name|fd
operator|=
operator|(
operator|(
name|FileInputStream
operator|)
name|in
operator|)
operator|.
name|getFD
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Unable to determine FileDescriptor"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|fd
return|;
block|}
comment|/**    * Close the input stream. Note that we need to read to the end of the    * stream to validate the checksum.    */
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
name|curReadahead
operator|!=
literal|null
condition|)
block|{
name|curReadahead
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|currentOffset
operator|<
name|dataLength
condition|)
block|{
name|byte
index|[]
name|t
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|min
argument_list|(
call|(
name|int
call|)
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|&
operator|(
name|dataLength
operator|-
name|currentOffset
operator|)
argument_list|)
argument_list|,
literal|32
operator|*
literal|1024
argument_list|)
index|]
decl_stmt|;
while|while
condition|(
name|currentOffset
operator|<
name|dataLength
condition|)
block|{
name|int
name|n
init|=
name|read
argument_list|(
name|t
argument_list|,
literal|0
argument_list|,
name|t
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
literal|0
operator|==
name|n
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Could not validate checksum"
argument_list|)
throw|;
block|}
block|}
block|}
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|skip (long n)
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Skip not supported for IFileInputStream"
argument_list|)
throw|;
block|}
DECL|method|getPosition ()
specifier|public
name|long
name|getPosition
parameter_list|()
block|{
return|return
operator|(
name|currentOffset
operator|>=
name|dataLength
operator|)
condition|?
name|dataLength
else|:
name|currentOffset
return|;
block|}
DECL|method|getSize ()
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
name|checksumSize
return|;
block|}
comment|/**    * Read bytes from the stream.    * At EOF, checksum is validated, but the checksum    * bytes are not passed back in the buffer.     */
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
name|currentOffset
operator|>=
name|dataLength
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|doReadahead
argument_list|()
expr_stmt|;
return|return
name|doRead
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|doReadahead ()
specifier|private
name|void
name|doReadahead
parameter_list|()
block|{
if|if
condition|(
name|raPool
operator|!=
literal|null
operator|&&
name|inFd
operator|!=
literal|null
operator|&&
name|readahead
condition|)
block|{
name|curReadahead
operator|=
name|raPool
operator|.
name|readaheadStream
argument_list|(
literal|"ifile"
argument_list|,
name|inFd
argument_list|,
name|currentOffset
argument_list|,
name|readaheadLength
argument_list|,
name|dataLength
argument_list|,
name|curReadahead
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read bytes from the stream.    * At EOF, checksum is validated and sent back    * as the last four bytes of the buffer. The caller should handle    * these bytes appropriately    */
DECL|method|readWithChecksum (byte[] b, int off, int len)
specifier|public
name|int
name|readWithChecksum
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
name|currentOffset
operator|==
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|currentOffset
operator|>=
name|dataLength
condition|)
block|{
comment|// If the previous read drained off all the data, then just return
comment|// the checksum now. Note that checksum validation would have
comment|// happened in the earlier read
name|int
name|lenToCopy
init|=
call|(
name|int
call|)
argument_list|(
name|checksumSize
operator|-
operator|(
name|currentOffset
operator|-
name|dataLength
operator|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|lenToCopy
condition|)
block|{
name|lenToCopy
operator|=
name|len
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|csum
argument_list|,
call|(
name|int
call|)
argument_list|(
name|currentOffset
operator|-
name|dataLength
argument_list|)
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|lenToCopy
argument_list|)
expr_stmt|;
name|currentOffset
operator|+=
name|lenToCopy
expr_stmt|;
return|return
name|lenToCopy
return|;
block|}
name|int
name|bytesRead
init|=
name|doRead
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
name|currentOffset
operator|==
name|dataLength
condition|)
block|{
if|if
condition|(
name|len
operator|>=
name|bytesRead
operator|+
name|checksumSize
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|csum
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|off
operator|+
name|bytesRead
argument_list|,
name|checksumSize
argument_list|)
expr_stmt|;
name|bytesRead
operator|+=
name|checksumSize
expr_stmt|;
name|currentOffset
operator|+=
name|checksumSize
expr_stmt|;
block|}
block|}
return|return
name|bytesRead
return|;
block|}
DECL|method|doRead (byte[]b, int off, int len)
specifier|private
name|int
name|doRead
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
comment|// If we are trying to read past the end of data, just read
comment|// the left over data
if|if
condition|(
name|currentOffset
operator|+
name|len
operator|>
name|dataLength
condition|)
block|{
name|len
operator|=
operator|(
name|int
operator|)
name|dataLength
operator|-
operator|(
name|int
operator|)
name|currentOffset
expr_stmt|;
block|}
name|int
name|bytesRead
init|=
name|in
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
name|bytesRead
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ChecksumException
argument_list|(
literal|"Checksum Error"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
name|sum
operator|.
name|update
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
name|currentOffset
operator|+=
name|bytesRead
expr_stmt|;
if|if
condition|(
name|disableChecksumValidation
condition|)
block|{
return|return
name|bytesRead
return|;
block|}
if|if
condition|(
name|currentOffset
operator|==
name|dataLength
condition|)
block|{
comment|// The last four bytes are checksum. Strip them and verify
name|csum
operator|=
operator|new
name|byte
index|[
name|checksumSize
index|]
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|csum
argument_list|,
literal|0
argument_list|,
name|checksumSize
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|sum
operator|.
name|compare
argument_list|(
name|csum
argument_list|,
literal|0
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ChecksumException
argument_list|(
literal|"Checksum Error"
argument_list|,
literal|0
argument_list|)
throw|;
block|}
block|}
return|return
name|bytesRead
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
name|b
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
name|int
name|l
init|=
name|read
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|<
literal|0
condition|)
return|return
name|l
return|;
comment|// Upgrade the b[0] to an int so as not to misinterpret the
comment|// first bit of the byte as a sign bit
name|int
name|result
init|=
literal|0xFF
operator|&
name|b
index|[
literal|0
index|]
decl_stmt|;
return|return
name|result
return|;
block|}
DECL|method|getChecksum ()
specifier|public
name|byte
index|[]
name|getChecksum
parameter_list|()
block|{
return|return
name|csum
return|;
block|}
DECL|method|disableChecksumValidation ()
name|void
name|disableChecksumValidation
parameter_list|()
block|{
name|disableChecksumValidation
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

