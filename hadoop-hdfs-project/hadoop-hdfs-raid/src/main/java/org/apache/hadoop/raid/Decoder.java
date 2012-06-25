begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|BlockMissingException
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
name|FileStatus
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
name|FileSystem
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|Path
import|;
end_import

begin_comment
comment|/**  * Represents a generic decoder that can be used to read a file with  * corrupt blocks by using the parity file.  * This is an abstract class, concrete subclasses need to implement  * fixErasedBlock.  */
end_comment

begin_class
DECL|class|Decoder
specifier|public
specifier|abstract
class|class
name|Decoder
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.raid.Decoder"
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|protected
name|Configuration
name|conf
decl_stmt|;
DECL|field|stripeSize
specifier|protected
name|int
name|stripeSize
decl_stmt|;
DECL|field|paritySize
specifier|protected
name|int
name|paritySize
decl_stmt|;
DECL|field|rand
specifier|protected
name|Random
name|rand
decl_stmt|;
DECL|field|bufSize
specifier|protected
name|int
name|bufSize
decl_stmt|;
DECL|field|readBufs
specifier|protected
name|byte
index|[]
index|[]
name|readBufs
decl_stmt|;
DECL|field|writeBufs
specifier|protected
name|byte
index|[]
index|[]
name|writeBufs
decl_stmt|;
DECL|method|Decoder (Configuration conf, int stripeSize, int paritySize)
name|Decoder
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|int
name|stripeSize
parameter_list|,
name|int
name|paritySize
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|stripeSize
operator|=
name|stripeSize
expr_stmt|;
name|this
operator|.
name|paritySize
operator|=
name|paritySize
expr_stmt|;
name|this
operator|.
name|rand
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
name|this
operator|.
name|bufSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
literal|"raid.decoder.bufsize"
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|this
operator|.
name|readBufs
operator|=
operator|new
name|byte
index|[
name|stripeSize
operator|+
name|paritySize
index|]
index|[]
expr_stmt|;
name|this
operator|.
name|writeBufs
operator|=
operator|new
name|byte
index|[
name|paritySize
index|]
index|[]
expr_stmt|;
name|allocateBuffers
argument_list|()
expr_stmt|;
block|}
DECL|method|allocateBuffers ()
specifier|private
name|void
name|allocateBuffers
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stripeSize
operator|+
name|paritySize
condition|;
name|i
operator|++
control|)
block|{
name|readBufs
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|bufSize
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paritySize
condition|;
name|i
operator|++
control|)
block|{
name|writeBufs
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|bufSize
index|]
expr_stmt|;
block|}
block|}
DECL|method|configureBuffers (long blockSize)
specifier|private
name|void
name|configureBuffers
parameter_list|(
name|long
name|blockSize
parameter_list|)
block|{
if|if
condition|(
operator|(
name|long
operator|)
name|bufSize
operator|>
name|blockSize
condition|)
block|{
name|bufSize
operator|=
operator|(
name|int
operator|)
name|blockSize
expr_stmt|;
name|allocateBuffers
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|blockSize
operator|%
name|bufSize
operator|!=
literal|0
condition|)
block|{
name|bufSize
operator|=
call|(
name|int
call|)
argument_list|(
name|blockSize
operator|/
literal|256L
argument_list|)
expr_stmt|;
comment|// heuristic.
if|if
condition|(
name|bufSize
operator|==
literal|0
condition|)
block|{
name|bufSize
operator|=
literal|1024
expr_stmt|;
block|}
name|bufSize
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bufSize
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|allocateBuffers
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * The interface to generate a decoded file using the good portion of the    * source file and the parity file.    * @param fs The filesystem containing the source file.    * @param srcFile The damaged source file.    * @param parityFs The filesystem containing the parity file. This could be    *        different from fs in case the parity file is part of a HAR archive.    * @param parityFile The parity file.    * @param errorOffset Known location of error in the source file. There could    *        be additional errors in the source file that are discovered during    *        the decode process.    * @param decodedFile The decoded file. This will have the exact same contents    *        as the source file on success.    */
DECL|method|decodeFile ( FileSystem fs, Path srcFile, FileSystem parityFs, Path parityFile, long errorOffset, Path decodedFile)
specifier|public
name|void
name|decodeFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcFile
parameter_list|,
name|FileSystem
name|parityFs
parameter_list|,
name|Path
name|parityFile
parameter_list|,
name|long
name|errorOffset
parameter_list|,
name|Path
name|decodedFile
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Create "
operator|+
name|decodedFile
operator|+
literal|" for error at "
operator|+
name|srcFile
operator|+
literal|":"
operator|+
name|errorOffset
argument_list|)
expr_stmt|;
name|FileStatus
name|srcStat
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|srcFile
argument_list|)
decl_stmt|;
name|long
name|blockSize
init|=
name|srcStat
operator|.
name|getBlockSize
argument_list|()
decl_stmt|;
name|configureBuffers
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
comment|// Move the offset to the start of the block.
name|errorOffset
operator|=
operator|(
name|errorOffset
operator|/
name|blockSize
operator|)
operator|*
name|blockSize
expr_stmt|;
comment|// Create the decoded file.
name|FSDataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|decodedFile
argument_list|,
literal|false
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
argument_list|,
name|srcStat
operator|.
name|getReplication
argument_list|()
argument_list|,
name|srcStat
operator|.
name|getBlockSize
argument_list|()
argument_list|)
decl_stmt|;
comment|// Open the source file.
name|FSDataInputStream
name|in
init|=
name|fs
operator|.
name|open
argument_list|(
name|srcFile
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
argument_list|)
decl_stmt|;
comment|// Start copying data block-by-block.
for|for
control|(
name|long
name|offset
init|=
literal|0
init|;
name|offset
operator|<
name|srcStat
operator|.
name|getLen
argument_list|()
condition|;
name|offset
operator|+=
name|blockSize
control|)
block|{
name|long
name|limit
init|=
name|Math
operator|.
name|min
argument_list|(
name|blockSize
argument_list|,
name|srcStat
operator|.
name|getLen
argument_list|()
operator|-
name|offset
argument_list|)
decl_stmt|;
name|long
name|bytesAlreadyCopied
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|offset
operator|!=
name|errorOffset
condition|)
block|{
try|try
block|{
name|in
operator|=
name|fs
operator|.
name|open
argument_list|(
name|srcFile
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
literal|"io.file.buffer.size"
argument_list|,
literal|64
operator|*
literal|1024
argument_list|)
argument_list|)
expr_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|RaidUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|readBufs
index|[
literal|0
index|]
argument_list|,
name|limit
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|out
operator|.
name|getPos
argument_list|()
operator|==
name|offset
operator|+
name|limit
operator|)
assert|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Copied till "
operator|+
name|out
operator|.
name|getPos
argument_list|()
operator|+
literal|" from "
operator|+
name|srcFile
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|BlockMissingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered BME at "
operator|+
name|srcFile
operator|+
literal|":"
operator|+
name|offset
argument_list|)
expr_stmt|;
name|bytesAlreadyCopied
operator|=
name|out
operator|.
name|getPos
argument_list|()
operator|-
name|offset
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChecksumException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Encountered CE at "
operator|+
name|srcFile
operator|+
literal|":"
operator|+
name|offset
argument_list|)
expr_stmt|;
name|bytesAlreadyCopied
operator|=
name|out
operator|.
name|getPos
argument_list|()
operator|-
name|offset
expr_stmt|;
block|}
block|}
comment|// If we are here offset == errorOffset or we got an exception.
comment|// Recover the block starting at offset.
name|fixErasedBlock
argument_list|(
name|fs
argument_list|,
name|srcFile
argument_list|,
name|parityFs
argument_list|,
name|parityFile
argument_list|,
name|blockSize
argument_list|,
name|offset
argument_list|,
name|bytesAlreadyCopied
argument_list|,
name|limit
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|setOwner
argument_list|(
name|decodedFile
argument_list|,
name|srcStat
operator|.
name|getOwner
argument_list|()
argument_list|,
name|srcStat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setPermission
argument_list|(
name|decodedFile
argument_list|,
name|srcStat
operator|.
name|getPermission
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setTimes
argument_list|(
name|decodedFile
argument_list|,
name|srcStat
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|srcStat
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exc
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Didn't manage to copy meta information because of "
operator|+
name|exc
operator|+
literal|" Ignoring..."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Recovers a corrupt block to local file.    *    * @param srcFs The filesystem containing the source file.    * @param srcPath The damaged source file.    * @param parityFs The filesystem containing the parity file. This could be    *        different from fs in case the parity file is part of a HAR archive.    * @param parityPath The parity file.    * @param blockSize The block size of the file.    * @param blockOffset Known location of error in the source file. There could    *        be additional errors in the source file that are discovered during    *        the decode process.    * @param localBlockFile The file to write the block to.    * @param limit The maximum number of bytes to be written out.    *              This is to prevent writing beyond the end of the file.    */
DECL|method|recoverBlockToFile ( FileSystem srcFs, Path srcPath, FileSystem parityFs, Path parityPath, long blockSize, long blockOffset, File localBlockFile, long limit)
specifier|public
name|void
name|recoverBlockToFile
parameter_list|(
name|FileSystem
name|srcFs
parameter_list|,
name|Path
name|srcPath
parameter_list|,
name|FileSystem
name|parityFs
parameter_list|,
name|Path
name|parityPath
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|long
name|blockOffset
parameter_list|,
name|File
name|localBlockFile
parameter_list|,
name|long
name|limit
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|localBlockFile
argument_list|)
decl_stmt|;
name|fixErasedBlock
argument_list|(
name|srcFs
argument_list|,
name|srcPath
argument_list|,
name|parityFs
argument_list|,
name|parityPath
argument_list|,
name|blockSize
argument_list|,
name|blockOffset
argument_list|,
literal|0
argument_list|,
name|limit
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Implementation-specific mechanism of writing a fixed block.    * @param fs The filesystem containing the source file.    * @param srcFile The damaged source file.    * @param parityFs The filesystem containing the parity file. This could be    *        different from fs in case the parity file is part of a HAR archive.    * @param parityFile The parity file.    * @param blockSize The maximum size of a block.    * @param errorOffset Known location of error in the source file. There could    *        be additional errors in the source file that are discovered during    *        the decode process.    * @param bytesToSkip After the block is generated, these many bytes should be    *       skipped before writing to the output. This is needed because the    *       output may have a portion of the block written from the source file    *       before a new corruption is discovered in the block.    * @param limit The maximum number of bytes to be written out, including    *       bytesToSkip. This is to prevent writing beyond the end of the file.    * @param out The output.    */
DECL|method|fixErasedBlock ( FileSystem fs, Path srcFile, FileSystem parityFs, Path parityFile, long blockSize, long errorOffset, long bytesToSkip, long limit, OutputStream out)
specifier|protected
specifier|abstract
name|void
name|fixErasedBlock
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcFile
parameter_list|,
name|FileSystem
name|parityFs
parameter_list|,
name|Path
name|parityFile
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|long
name|errorOffset
parameter_list|,
name|long
name|bytesToSkip
parameter_list|,
name|long
name|limit
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

