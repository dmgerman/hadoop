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
name|FileInputStream
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
name|StringUtils
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
name|Progressable
import|;
end_import

begin_comment
comment|/**  * Represents a generic encoder that can generate a parity file for a source  * file.  * This is an abstract class, concrete subclasses need to implement  * encodeFileImpl.  */
end_comment

begin_class
DECL|class|Encoder
specifier|public
specifier|abstract
class|class
name|Encoder
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
literal|"org.apache.hadoop.raid.Encoder"
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
comment|/**    * A class that acts as a sink for data, similar to /dev/null.    */
DECL|class|NullOutputStream
specifier|static
class|class
name|NullOutputStream
extends|extends
name|OutputStream
block|{
DECL|method|write (byte[] b)
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{}
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
block|{}
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
block|{}
block|}
DECL|method|Encoder ( Configuration conf, int stripeSize, int paritySize)
name|Encoder
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
literal|"raid.encoder.bufsize"
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
comment|/**    * The interface to use to generate a parity file.    * This method can be called multiple times with the same Encoder object,    * thus allowing reuse of the buffers allocated by the Encoder object.    *    * @param fs The filesystem containing the source file.    * @param srcFile The source file.    * @param parityFile The parity file to be generated.    */
DECL|method|encodeFile ( FileSystem fs, Path srcFile, FileSystem parityFs, Path parityFile, short parityRepl, Progressable reporter)
specifier|public
name|void
name|encodeFile
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
name|short
name|parityRepl
parameter_list|,
name|Progressable
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
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
name|srcSize
init|=
name|srcStat
operator|.
name|getLen
argument_list|()
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
comment|// Create a tmp file to which we will write first.
name|Path
name|tmpDir
init|=
name|getParityTempPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parityFs
operator|.
name|mkdirs
argument_list|(
name|tmpDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not create tmp dir "
operator|+
name|tmpDir
argument_list|)
throw|;
block|}
name|Path
name|parityTmp
init|=
operator|new
name|Path
argument_list|(
name|tmpDir
argument_list|,
name|parityFile
operator|.
name|getName
argument_list|()
operator|+
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|out
init|=
name|parityFs
operator|.
name|create
argument_list|(
name|parityTmp
argument_list|,
literal|true
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
name|parityRepl
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
try|try
block|{
name|encodeFileToStream
argument_list|(
name|fs
argument_list|,
name|srcFile
argument_list|,
name|srcSize
argument_list|,
name|blockSize
argument_list|,
name|out
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|=
literal|null
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Wrote temp parity file "
operator|+
name|parityTmp
argument_list|)
expr_stmt|;
comment|// delete destination if exists
if|if
condition|(
name|parityFs
operator|.
name|exists
argument_list|(
name|parityFile
argument_list|)
condition|)
block|{
name|parityFs
operator|.
name|delete
argument_list|(
name|parityFile
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|parityFs
operator|.
name|mkdirs
argument_list|(
name|parityFile
operator|.
name|getParent
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|parityFs
operator|.
name|rename
argument_list|(
name|parityTmp
argument_list|,
name|parityFile
argument_list|)
condition|)
block|{
name|String
name|msg
init|=
literal|"Unable to rename file "
operator|+
name|parityTmp
operator|+
literal|" to "
operator|+
name|parityFile
decl_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Wrote parity file "
operator|+
name|parityFile
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|out
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|parityFs
operator|.
name|delete
argument_list|(
name|parityTmp
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Recovers a corrupt block in a parity file to a local file.    *    * The encoder generates paritySize parity blocks for a source file stripe.    * Since we want only one of the parity blocks, this function creates    * null outputs for the blocks to be discarded.    *    * @param fs The filesystem in which both srcFile and parityFile reside.    * @param srcFile The source file.    * @param srcSize The size of the source file.    * @param blockSize The block size for the source/parity files.    * @param corruptOffset The location of corruption in the parity file.    * @param localBlockFile The destination for the reovered block.    */
DECL|method|recoverParityBlockToFile ( FileSystem fs, Path srcFile, long srcSize, long blockSize, Path parityFile, long corruptOffset, File localBlockFile)
specifier|public
name|void
name|recoverParityBlockToFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcFile
parameter_list|,
name|long
name|srcSize
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Path
name|parityFile
parameter_list|,
name|long
name|corruptOffset
parameter_list|,
name|File
name|localBlockFile
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
try|try
block|{
name|recoverParityBlockToStream
argument_list|(
name|fs
argument_list|,
name|srcFile
argument_list|,
name|srcSize
argument_list|,
name|blockSize
argument_list|,
name|parityFile
argument_list|,
name|corruptOffset
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Recovers a corrupt block in a parity file to a local file.    *    * The encoder generates paritySize parity blocks for a source file stripe.    * Since we want only one of the parity blocks, this function creates    * null outputs for the blocks to be discarded.    *    * @param fs The filesystem in which both srcFile and parityFile reside.    * @param srcFile The source file.    * @param srcSize The size of the source file.    * @param blockSize The block size for the source/parity files.    * @param corruptOffset The location of corruption in the parity file.    * @param out The destination for the reovered block.    */
DECL|method|recoverParityBlockToStream ( FileSystem fs, Path srcFile, long srcSize, long blockSize, Path parityFile, long corruptOffset, OutputStream out)
specifier|public
name|void
name|recoverParityBlockToStream
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcFile
parameter_list|,
name|long
name|srcSize
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Path
name|parityFile
parameter_list|,
name|long
name|corruptOffset
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Recovering parity block"
operator|+
name|parityFile
operator|+
literal|":"
operator|+
name|corruptOffset
argument_list|)
expr_stmt|;
comment|// Get the start offset of the corrupt block.
name|corruptOffset
operator|=
operator|(
name|corruptOffset
operator|/
name|blockSize
operator|)
operator|*
name|blockSize
expr_stmt|;
comment|// Output streams to each block in the parity file stripe.
name|OutputStream
index|[]
name|outs
init|=
operator|new
name|OutputStream
index|[
name|paritySize
index|]
decl_stmt|;
name|long
name|indexOfCorruptBlockInParityStripe
init|=
operator|(
name|corruptOffset
operator|/
name|blockSize
operator|)
operator|%
name|paritySize
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Index of corrupt block in parity stripe: "
operator|+
name|indexOfCorruptBlockInParityStripe
argument_list|)
expr_stmt|;
comment|// Create a real output stream for the block we want to recover,
comment|// and create null streams for the rest.
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
if|if
condition|(
name|indexOfCorruptBlockInParityStripe
operator|==
name|i
condition|)
block|{
name|outs
index|[
name|i
index|]
operator|=
name|out
expr_stmt|;
block|}
else|else
block|{
name|outs
index|[
name|i
index|]
operator|=
operator|new
name|NullOutputStream
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Get the stripe index and start offset of stripe.
name|long
name|stripeIdx
init|=
name|corruptOffset
operator|/
operator|(
name|paritySize
operator|*
name|blockSize
operator|)
decl_stmt|;
name|long
name|stripeStart
init|=
name|stripeIdx
operator|*
name|blockSize
operator|*
name|stripeSize
decl_stmt|;
comment|// Get input streams to each block in the source file stripe.
name|InputStream
index|[]
name|blocks
init|=
name|stripeInputs
argument_list|(
name|fs
argument_list|,
name|srcFile
argument_list|,
name|stripeStart
argument_list|,
name|srcSize
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting recovery by using source stripe "
operator|+
name|srcFile
operator|+
literal|":"
operator|+
name|stripeStart
argument_list|)
expr_stmt|;
comment|// Read the data from the blocks and write to the parity file.
name|encodeStripe
argument_list|(
name|blocks
argument_list|,
name|stripeStart
argument_list|,
name|blockSize
argument_list|,
name|outs
argument_list|,
operator|new
name|RaidUtils
operator|.
name|DummyProgressable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Recovers a corrupt block in a parity file to an output stream.    *    * The encoder generates paritySize parity blocks for a source file stripe.    * Since there is only one output provided, some blocks are written out to    * files before being written out to the output.    *    * @param fs The filesystem in which both srcFile and parityFile reside.    * @param srcFile The source file.    * @param srcSize The size of the source file.    * @param blockSize The block size for the source/parity files.    * @param out The destination for the reovered block.    */
DECL|method|encodeFileToStream (FileSystem fs, Path srcFile, long srcSize, long blockSize, OutputStream out, Progressable reporter)
specifier|private
name|void
name|encodeFileToStream
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcFile
parameter_list|,
name|long
name|srcSize
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|OutputStream
name|out
parameter_list|,
name|Progressable
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
index|[]
name|tmpOuts
init|=
operator|new
name|OutputStream
index|[
name|paritySize
index|]
decl_stmt|;
comment|// One parity block can be written directly to out, rest to local files.
name|tmpOuts
index|[
literal|0
index|]
operator|=
name|out
expr_stmt|;
name|File
index|[]
name|tmpFiles
init|=
operator|new
name|File
index|[
name|paritySize
operator|-
literal|1
index|]
decl_stmt|;
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
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|tmpFiles
index|[
name|i
index|]
operator|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"parity"
argument_list|,
literal|"_"
operator|+
name|i
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Created tmp file "
operator|+
name|tmpFiles
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|tmpFiles
index|[
name|i
index|]
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// Loop over stripes in the file.
for|for
control|(
name|long
name|stripeStart
init|=
literal|0
init|;
name|stripeStart
operator|<
name|srcSize
condition|;
name|stripeStart
operator|+=
name|blockSize
operator|*
name|stripeSize
control|)
block|{
name|reporter
operator|.
name|progress
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting encoding of stripe "
operator|+
name|srcFile
operator|+
literal|":"
operator|+
name|stripeStart
argument_list|)
expr_stmt|;
comment|// Create input streams for blocks in the stripe.
name|InputStream
index|[]
name|blocks
init|=
name|stripeInputs
argument_list|(
name|fs
argument_list|,
name|srcFile
argument_list|,
name|stripeStart
argument_list|,
name|srcSize
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
comment|// Create output streams to the temp files.
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
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|tmpOuts
index|[
name|i
operator|+
literal|1
index|]
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpFiles
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
comment|// Call the implementation of encoding.
name|encodeStripe
argument_list|(
name|blocks
argument_list|,
name|stripeStart
argument_list|,
name|blockSize
argument_list|,
name|tmpOuts
argument_list|,
name|reporter
argument_list|)
expr_stmt|;
comment|// Close output streams to the temp files and write the temp files
comment|// to the output provided.
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
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|tmpOuts
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|tmpOuts
index|[
name|i
operator|+
literal|1
index|]
operator|=
literal|null
expr_stmt|;
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|tmpFiles
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|RaidUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|writeBufs
index|[
name|i
index|]
argument_list|,
name|blockSize
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|progress
argument_list|()
expr_stmt|;
block|}
block|}
block|}
finally|finally
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
name|paritySize
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tmpOuts
index|[
name|i
operator|+
literal|1
index|]
operator|!=
literal|null
condition|)
block|{
name|tmpOuts
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|tmpFiles
index|[
name|i
index|]
operator|.
name|delete
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleted tmp file "
operator|+
name|tmpFiles
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Return input streams for each block in a source file's stripe.    * @param fs The filesystem where the file resides.    * @param srcFile The source file.    * @param stripeStartOffset The start offset of the stripe.    * @param srcSize The size of the source file.    * @param blockSize The block size for the source file.    */
DECL|method|stripeInputs ( FileSystem fs, Path srcFile, long stripeStartOffset, long srcSize, long blockSize )
specifier|protected
name|InputStream
index|[]
name|stripeInputs
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|srcFile
parameter_list|,
name|long
name|stripeStartOffset
parameter_list|,
name|long
name|srcSize
parameter_list|,
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
index|[]
name|blocks
init|=
operator|new
name|InputStream
index|[
name|stripeSize
index|]
decl_stmt|;
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
condition|;
name|i
operator|++
control|)
block|{
name|long
name|seekOffset
init|=
name|stripeStartOffset
operator|+
name|i
operator|*
name|blockSize
decl_stmt|;
if|if
condition|(
name|seekOffset
operator|<
name|srcSize
condition|)
block|{
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
name|in
operator|.
name|seek
argument_list|(
name|seekOffset
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Opening stream at "
operator|+
name|srcFile
operator|+
literal|":"
operator|+
name|seekOffset
argument_list|)
expr_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
name|in
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using zeros at offset "
operator|+
name|seekOffset
argument_list|)
expr_stmt|;
comment|// We have no src data at this offset.
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|RaidUtils
operator|.
name|ZeroInputStream
argument_list|(
name|seekOffset
operator|+
name|blockSize
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|blocks
return|;
block|}
comment|/**    * The implementation of generating parity data for a stripe.    *    * @param blocks The streams to blocks in the stripe.    * @param srcFile The source file.    * @param stripeStartOffset The start offset of the stripe    * @param blockSize The maximum size of a block.    * @param outs output streams to the parity blocks.    * @param reporter progress indicator.    */
DECL|method|encodeStripe ( InputStream[] blocks, long stripeStartOffset, long blockSize, OutputStream[] outs, Progressable reporter)
specifier|protected
specifier|abstract
name|void
name|encodeStripe
parameter_list|(
name|InputStream
index|[]
name|blocks
parameter_list|,
name|long
name|stripeStartOffset
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|OutputStream
index|[]
name|outs
parameter_list|,
name|Progressable
name|reporter
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the temp path for the parity file    */
DECL|method|getParityTempPath ()
specifier|protected
specifier|abstract
name|Path
name|getParityTempPath
parameter_list|()
function_decl|;
block|}
end_class

end_unit

