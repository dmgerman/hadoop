begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|util
operator|.
name|concurrent
operator|.
name|Uninterruptibles
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
name|HadoopIllegalArgumentException
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
name|conf
operator|.
name|Configured
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
name|Options
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
name|hdfs
operator|.
name|DFSUtilClient
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
name|client
operator|.
name|impl
operator|.
name|DfsClientConf
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
name|DistributedFileSystem
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
name|server
operator|.
name|datanode
operator|.
name|BlockMetadataHeader
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
operator|.
name|FsDatasetUtil
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
name|util
operator|.
name|DataChecksum
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
name|Tool
import|;
end_import

begin_comment
comment|/**  * This class implements debug operations on the HDFS command-line.  *  * These operations are only for debugging, and may change or disappear  * between HDFS versions.  */
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
DECL|class|DebugAdmin
specifier|public
class|class
name|DebugAdmin
extends|extends
name|Configured
implements|implements
name|Tool
block|{
comment|/**    * All the debug commands we can run.    */
DECL|field|DEBUG_COMMANDS
specifier|private
name|DebugCommand
name|DEBUG_COMMANDS
index|[]
init|=
block|{
operator|new
name|VerifyMetaCommand
argument_list|()
block|,
operator|new
name|ComputeMetaCommand
argument_list|()
block|,
operator|new
name|RecoverLeaseCommand
argument_list|()
block|,
operator|new
name|HelpCommand
argument_list|()
block|}
decl_stmt|;
comment|/**    * The base class for debug commands.    */
DECL|class|DebugCommand
specifier|private
specifier|abstract
class|class
name|DebugCommand
block|{
DECL|field|name
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|usageText
specifier|final
name|String
name|usageText
decl_stmt|;
DECL|field|helpText
specifier|final
name|String
name|helpText
decl_stmt|;
DECL|method|DebugCommand (String name, String usageText, String helpText)
name|DebugCommand
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|usageText
parameter_list|,
name|String
name|helpText
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|usageText
operator|=
name|usageText
expr_stmt|;
name|this
operator|.
name|helpText
operator|=
name|helpText
expr_stmt|;
block|}
DECL|method|run (List<String> args)
specifier|abstract
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
DECL|field|HEADER_LEN
specifier|private
specifier|static
name|int
name|HEADER_LEN
init|=
literal|7
decl_stmt|;
comment|/**    * The command for verifying a block metadata file and possibly block file.    */
DECL|class|VerifyMetaCommand
specifier|private
class|class
name|VerifyMetaCommand
extends|extends
name|DebugCommand
block|{
DECL|method|VerifyMetaCommand ()
name|VerifyMetaCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"verifyMeta"
argument_list|,
literal|"verifyMeta -meta<metadata-file> [-block<block-file>]"
argument_list|,
literal|"  Verify HDFS metadata and block files.  If a block file is specified, we"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"  will verify that the checksums in the metadata file match the block"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"  file."
argument_list|)
expr_stmt|;
block|}
DECL|method|run (List<String> args)
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usageText
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|helpText
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|String
name|blockFile
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-block"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|metaFile
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-meta"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaFile
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a meta file with -meta"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|FileInputStream
name|metaStream
init|=
literal|null
decl_stmt|,
name|dataStream
init|=
literal|null
decl_stmt|;
name|FileChannel
name|metaChannel
init|=
literal|null
decl_stmt|,
name|dataChannel
init|=
literal|null
decl_stmt|;
name|DataInputStream
name|checksumStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|BlockMetadataHeader
name|header
decl_stmt|;
try|try
block|{
name|metaStream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|metaFile
argument_list|)
expr_stmt|;
name|checksumStream
operator|=
operator|new
name|DataInputStream
argument_list|(
name|metaStream
argument_list|)
expr_stmt|;
name|header
operator|=
name|BlockMetadataHeader
operator|.
name|readHeader
argument_list|(
name|checksumStream
argument_list|)
expr_stmt|;
name|metaChannel
operator|=
name|metaStream
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|metaChannel
operator|.
name|position
argument_list|(
name|HEADER_LEN
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to read HDFS metadata file header for "
operator|+
name|metaFile
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to read HDFS metadata file header for "
operator|+
name|metaFile
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|DataChecksum
name|checksum
init|=
name|header
operator|.
name|getChecksum
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Checksum type: "
operator|+
name|checksum
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockFile
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
name|ByteBuffer
name|metaBuf
decl_stmt|,
name|dataBuf
decl_stmt|;
try|try
block|{
name|dataStream
operator|=
operator|new
name|FileInputStream
argument_list|(
name|blockFile
argument_list|)
expr_stmt|;
name|dataChannel
operator|=
name|dataStream
operator|.
name|getChannel
argument_list|()
expr_stmt|;
specifier|final
name|int
name|CHECKSUMS_PER_BUF
init|=
literal|1024
operator|*
literal|32
decl_stmt|;
name|metaBuf
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|checksum
operator|.
name|getChecksumSize
argument_list|()
operator|*
name|CHECKSUMS_PER_BUF
argument_list|)
expr_stmt|;
name|dataBuf
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
operator|*
name|CHECKSUMS_PER_BUF
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to open HDFS block file for "
operator|+
name|blockFile
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|long
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|dataBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|int
name|dataRead
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|dataRead
operator|=
name|dataChannel
operator|.
name|read
argument_list|(
name|dataBuf
argument_list|)
expr_stmt|;
if|if
condition|(
name|dataRead
operator|<
literal|0
condition|)
block|{
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Got I/O error reading block file "
operator|+
name|blockFile
operator|+
literal|"from disk at offset "
operator|+
name|dataChannel
operator|.
name|position
argument_list|()
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
try|try
block|{
name|int
name|csumToRead
init|=
operator|(
operator|(
operator|(
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
operator|-
literal|1
operator|)
operator|+
name|dataRead
operator|)
operator|/
name|checksum
operator|.
name|getBytesPerChecksum
argument_list|()
operator|)
operator|*
name|checksum
operator|.
name|getChecksumSize
argument_list|()
decl_stmt|;
name|metaBuf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|metaBuf
operator|.
name|limit
argument_list|(
name|csumToRead
argument_list|)
expr_stmt|;
name|metaChannel
operator|.
name|read
argument_list|(
name|metaBuf
argument_list|)
expr_stmt|;
name|dataBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
name|metaBuf
operator|.
name|flip
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Got I/O error reading metadata file "
operator|+
name|metaFile
operator|+
literal|"from disk at offset "
operator|+
name|metaChannel
operator|.
name|position
argument_list|()
operator|+
literal|": "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
try|try
block|{
name|checksum
operator|.
name|verifyChunkedSums
argument_list|(
name|dataBuf
argument_list|,
name|metaBuf
argument_list|,
name|blockFile
argument_list|,
name|offset
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"verifyChunkedSums error: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|offset
operator|+=
name|dataRead
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Checksum verification succeeded on block file "
operator|+
name|blockFile
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|metaStream
argument_list|,
name|dataStream
argument_list|,
name|checksumStream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The command for verifying a block metadata file and possibly block file.    */
DECL|class|ComputeMetaCommand
specifier|private
class|class
name|ComputeMetaCommand
extends|extends
name|DebugCommand
block|{
DECL|method|ComputeMetaCommand ()
name|ComputeMetaCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"computeMeta"
argument_list|,
literal|"computeMeta -block<block-file> -out<output-metadata-file>"
argument_list|,
literal|"  Compute HDFS metadata from the specified block file, and save it"
operator|+
literal|" to"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"  the specified output metadata file."
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"**NOTE: Use at your own risk!"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|" If the block file is corrupt"
operator|+
literal|" and you overwrite it's meta file, "
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|" it will show up"
operator|+
literal|" as good in HDFS, but you can't read the data."
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|" Only use as a last measure, and when you are 100% certain"
operator|+
literal|" the block file is good."
argument_list|)
expr_stmt|;
block|}
DECL|method|createChecksum (Options.ChecksumOpt opt)
specifier|private
name|DataChecksum
name|createChecksum
parameter_list|(
name|Options
operator|.
name|ChecksumOpt
name|opt
parameter_list|)
block|{
name|DataChecksum
name|dataChecksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|opt
operator|.
name|getChecksumType
argument_list|()
argument_list|,
name|opt
operator|.
name|getBytesPerChecksum
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dataChecksum
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Invalid checksum type: userOpt="
operator|+
name|opt
operator|+
literal|", default="
operator|+
name|opt
operator|+
literal|", effective=null"
argument_list|)
throw|;
block|}
return|return
name|dataChecksum
return|;
block|}
DECL|method|run (List<String> args)
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usageText
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|helpText
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
specifier|final
name|String
name|name
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-block"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a block file with -block"
argument_list|)
expr_stmt|;
return|return
literal|2
return|;
block|}
specifier|final
name|File
name|blockFile
init|=
operator|new
name|File
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|blockFile
operator|.
name|exists
argument_list|()
operator|||
operator|!
name|blockFile
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Block file<"
operator|+
name|name
operator|+
literal|"> does not exist "
operator|+
literal|"or is not a file"
argument_list|)
expr_stmt|;
return|return
literal|3
return|;
block|}
specifier|final
name|String
name|outFile
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-out"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|outFile
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must specify a output file with -out"
argument_list|)
expr_stmt|;
return|return
literal|4
return|;
block|}
specifier|final
name|File
name|srcMeta
init|=
operator|new
name|File
argument_list|(
name|outFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcMeta
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"output file already exists!"
argument_list|)
expr_stmt|;
return|return
literal|5
return|;
block|}
name|DataOutputStream
name|metaOut
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
specifier|final
name|Options
operator|.
name|ChecksumOpt
name|checksumOpt
init|=
name|DfsClientConf
operator|.
name|getChecksumOptFromConf
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|DataChecksum
name|checksum
init|=
name|createChecksum
argument_list|(
name|checksumOpt
argument_list|)
decl_stmt|;
specifier|final
name|int
name|smallBufferSize
init|=
name|DFSUtilClient
operator|.
name|getSmallBufferSize
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|metaOut
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|srcMeta
argument_list|)
argument_list|,
name|smallBufferSize
argument_list|)
argument_list|)
expr_stmt|;
name|BlockMetadataHeader
operator|.
name|writeHeader
argument_list|(
name|metaOut
argument_list|,
name|checksum
argument_list|)
expr_stmt|;
name|metaOut
operator|.
name|close
argument_list|()
expr_stmt|;
name|FsDatasetUtil
operator|.
name|computeChecksum
argument_list|(
name|srcMeta
argument_list|,
name|srcMeta
argument_list|,
name|blockFile
argument_list|,
name|smallBufferSize
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Checksum calculation succeeded on block file "
operator|+
name|name
operator|+
literal|" saved metadata to meta file "
operator|+
name|outFile
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
literal|null
argument_list|,
name|metaOut
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * The command for recovering a file lease.    */
DECL|class|RecoverLeaseCommand
specifier|private
class|class
name|RecoverLeaseCommand
extends|extends
name|DebugCommand
block|{
DECL|method|RecoverLeaseCommand ()
name|RecoverLeaseCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"recoverLease"
argument_list|,
literal|"recoverLease -path<path> [-retries<num-retries>]"
argument_list|,
literal|"  Recover the lease on the specified path.  The path must reside on an"
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
operator|+
literal|"  HDFS filesystem.  The default number of retries is 1."
argument_list|)
expr_stmt|;
block|}
DECL|field|TIMEOUT_MS
specifier|private
specifier|static
specifier|final
name|int
name|TIMEOUT_MS
init|=
literal|5000
decl_stmt|;
DECL|method|run (List<String> args)
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|usageText
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|helpText
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|String
name|pathStr
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-path"
argument_list|,
name|args
argument_list|)
decl_stmt|;
name|String
name|retriesStr
init|=
name|StringUtils
operator|.
name|popOptionWithArgument
argument_list|(
literal|"-retries"
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathStr
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"You must supply a -path argument to "
operator|+
literal|"recoverLease."
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|int
name|maxRetries
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|retriesStr
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|maxRetries
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|retriesStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to parse the argument to -retries: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
name|FileSystem
name|fs
decl_stmt|;
try|try
block|{
name|fs
operator|=
name|FileSystem
operator|.
name|newInstance
argument_list|(
operator|new
name|URI
argument_list|(
name|pathStr
argument_list|)
argument_list|,
name|getConf
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"URISyntaxException for "
operator|+
name|pathStr
operator|+
literal|":"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"InterruptedException for "
operator|+
name|pathStr
operator|+
literal|":"
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
name|DistributedFileSystem
name|dfs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Invalid filesystem for path "
operator|+
name|pathStr
operator|+
literal|": "
operator|+
literal|"needed scheme hdfs, but got: "
operator|+
name|fs
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
for|for
control|(
name|int
name|retry
init|=
literal|0
init|;
literal|true
condition|;
control|)
block|{
name|boolean
name|recovered
init|=
literal|false
decl_stmt|;
name|IOException
name|ioe
init|=
literal|null
decl_stmt|;
try|try
block|{
name|recovered
operator|=
name|dfs
operator|.
name|recoverLease
argument_list|(
operator|new
name|Path
argument_list|(
name|pathStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"recoverLease got exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Giving up on recoverLease for "
operator|+
name|pathStr
operator|+
literal|" after 1 try"
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ioe
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|recovered
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"recoverLease SUCCEEDED on "
operator|+
name|pathStr
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
if|if
condition|(
name|ioe
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"recoverLease got exception: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"recoverLease returned false."
argument_list|)
expr_stmt|;
block|}
name|retry
operator|++
expr_stmt|;
if|if
condition|(
name|retry
operator|>=
name|maxRetries
condition|)
block|{
break|break;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Retrying in "
operator|+
name|TIMEOUT_MS
operator|+
literal|" ms..."
argument_list|)
expr_stmt|;
name|Uninterruptibles
operator|.
name|sleepUninterruptibly
argument_list|(
name|TIMEOUT_MS
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Retry #"
operator|+
name|retry
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Giving up on recoverLease for "
operator|+
name|pathStr
operator|+
literal|" after "
operator|+
name|maxRetries
operator|+
operator|(
name|maxRetries
operator|==
literal|1
condition|?
literal|" try."
else|:
literal|" tries."
operator|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
comment|/**    * The command for getting help about other commands.    */
DECL|class|HelpCommand
specifier|private
class|class
name|HelpCommand
extends|extends
name|DebugCommand
block|{
DECL|method|HelpCommand ()
name|HelpCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"help"
argument_list|,
literal|"help [command-name]"
argument_list|,
literal|"  Get help about a command."
argument_list|)
expr_stmt|;
block|}
DECL|method|run (List<String> args)
name|int
name|run
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|DebugCommand
name|command
init|=
name|popCommand
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|command
operator|.
name|usageText
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|command
operator|.
name|helpText
operator|+
name|System
operator|.
name|lineSeparator
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
DECL|method|DebugAdmin (Configuration conf)
specifier|public
name|DebugAdmin
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|popCommand (List<String> args)
specifier|private
name|DebugCommand
name|popCommand
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|String
name|commandStr
init|=
operator|(
name|args
operator|.
name|size
argument_list|()
operator|==
literal|0
operator|)
condition|?
literal|""
else|:
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|commandStr
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|commandStr
operator|=
name|commandStr
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DebugCommand
name|command
range|:
name|DEBUG_COMMANDS
control|)
block|{
if|if
condition|(
name|command
operator|.
name|name
operator|.
name|equals
argument_list|(
name|commandStr
argument_list|)
condition|)
block|{
name|args
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|command
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|run (String[] argv)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
block|{
name|LinkedList
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|LinkedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|argv
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|args
operator|.
name|add
argument_list|(
name|argv
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|DebugCommand
name|command
init|=
name|popCommand
argument_list|(
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|==
literal|null
condition|)
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return
literal|0
return|;
block|}
try|try
block|{
return|return
name|command
operator|.
name|run
argument_list|(
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"IOException: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"RuntimeException: "
operator|+
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
block|}
DECL|method|printUsage ()
specifier|private
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: hdfs debug<command> [arguments]\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"These commands are for advanced users only.\n"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Incorrect usages may result in data loss. "
operator|+
literal|"Use at your own risk.\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|DebugCommand
name|command
range|:
name|DEBUG_COMMANDS
control|)
block|{
if|if
condition|(
operator|!
name|command
operator|.
name|name
operator|.
name|equals
argument_list|(
literal|"help"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|command
operator|.
name|usageText
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|main (String[] argsArray)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argsArray
parameter_list|)
throws|throws
name|IOException
block|{
name|DebugAdmin
name|debugAdmin
init|=
operator|new
name|DebugAdmin
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|debugAdmin
operator|.
name|run
argument_list|(
name|argsArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

