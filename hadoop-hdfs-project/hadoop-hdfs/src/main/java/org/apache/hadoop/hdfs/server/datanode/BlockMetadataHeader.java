begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
name|datanode
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
name|ByteArrayInputStream
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
name|EOFException
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * BlockMetadataHeader manages metadata for data blocks on Datanodes.  * This is not related to the Block related functionality in Namenode.  * The biggest part of data block metadata is CRC for the block.  */
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
DECL|class|BlockMetadataHeader
specifier|public
class|class
name|BlockMetadataHeader
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockMetadataHeader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|short
name|VERSION
init|=
literal|1
decl_stmt|;
comment|/**    * Header includes everything except the checksum(s) themselves.    * Version is two bytes. Following it is the DataChecksum    * that occupies 5 bytes.     */
DECL|field|version
specifier|private
specifier|final
name|short
name|version
decl_stmt|;
DECL|field|checksum
specifier|private
name|DataChecksum
name|checksum
init|=
literal|null
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|BlockMetadataHeader (short version, DataChecksum checksum)
specifier|public
name|BlockMetadataHeader
parameter_list|(
name|short
name|version
parameter_list|,
name|DataChecksum
name|checksum
parameter_list|)
block|{
name|this
operator|.
name|checksum
operator|=
name|checksum
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
comment|/** Get the version */
DECL|method|getVersion ()
specifier|public
name|short
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/** Get the checksum */
DECL|method|getChecksum ()
specifier|public
name|DataChecksum
name|getChecksum
parameter_list|()
block|{
return|return
name|checksum
return|;
block|}
comment|/**    * Read the checksum header from the meta file.    * @return the data checksum obtained from the header.    */
DECL|method|readDataChecksum (File metaFile)
specifier|public
specifier|static
name|DataChecksum
name|readDataChecksum
parameter_list|(
name|File
name|metaFile
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|metaFile
argument_list|)
argument_list|,
name|HdfsConstants
operator|.
name|IO_FILE_BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|readDataChecksum
argument_list|(
name|in
argument_list|,
name|metaFile
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read the checksum header from the meta input stream.    * @return the data checksum obtained from the header.    */
DECL|method|readDataChecksum (final DataInputStream metaIn, final Object name)
specifier|public
specifier|static
name|DataChecksum
name|readDataChecksum
parameter_list|(
specifier|final
name|DataInputStream
name|metaIn
parameter_list|,
specifier|final
name|Object
name|name
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read and handle the common header here. For now just a version
specifier|final
name|BlockMetadataHeader
name|header
init|=
name|readHeader
argument_list|(
name|metaIn
argument_list|)
decl_stmt|;
if|if
condition|(
name|header
operator|.
name|getVersion
argument_list|()
operator|!=
name|VERSION
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected meta-file version for "
operator|+
name|name
operator|+
literal|": version in file is "
operator|+
name|header
operator|.
name|getVersion
argument_list|()
operator|+
literal|" but expected version is "
operator|+
name|VERSION
argument_list|)
expr_stmt|;
block|}
return|return
name|header
operator|.
name|getChecksum
argument_list|()
return|;
block|}
comment|/**    * Read the header without changing the position of the FileChannel.    *    * @param fc The FileChannel to read.    * @return the Metadata Header.    * @throws IOException on error.    */
DECL|method|preadHeader (FileChannel fc)
specifier|public
specifier|static
name|BlockMetadataHeader
name|preadHeader
parameter_list|(
name|FileChannel
name|fc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
name|arr
index|[]
init|=
operator|new
name|byte
index|[
name|getHeaderSize
argument_list|()
index|]
decl_stmt|;
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|arr
argument_list|)
decl_stmt|;
while|while
condition|(
name|buf
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
if|if
condition|(
name|fc
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|)
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"unexpected EOF while reading "
operator|+
literal|"metadata file header"
argument_list|)
throw|;
block|}
block|}
name|short
name|version
init|=
call|(
name|short
call|)
argument_list|(
operator|(
name|arr
index|[
literal|0
index|]
operator|<<
literal|8
operator|)
operator||
operator|(
name|arr
index|[
literal|1
index|]
operator|&
literal|0xff
operator|)
argument_list|)
decl_stmt|;
name|DataChecksum
name|dataChecksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|arr
argument_list|,
literal|2
argument_list|)
decl_stmt|;
return|return
operator|new
name|BlockMetadataHeader
argument_list|(
name|version
argument_list|,
name|dataChecksum
argument_list|)
return|;
block|}
comment|/**    * This reads all the fields till the beginning of checksum.    * @return Metadata Header    * @throws IOException    */
DECL|method|readHeader (DataInputStream in)
specifier|public
specifier|static
name|BlockMetadataHeader
name|readHeader
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readHeader
argument_list|(
name|in
operator|.
name|readShort
argument_list|()
argument_list|,
name|in
argument_list|)
return|;
block|}
comment|/**    * Reads header at the top of metadata file and returns the header.    *     * @return metadata header for the block    * @throws IOException    */
DECL|method|readHeader (File file)
specifier|public
specifier|static
name|BlockMetadataHeader
name|readHeader
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|DataInputStream
name|in
init|=
literal|null
decl_stmt|;
try|try
block|{
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|BufferedInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|readHeader
argument_list|(
name|in
argument_list|)
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Read the header at the beginning of the given block meta file.    * The current file position will be altered by this method.    * If an error occurs, the file is<em>not</em> closed.    */
DECL|method|readHeader (RandomAccessFile raf)
specifier|public
specifier|static
name|BlockMetadataHeader
name|readHeader
parameter_list|(
name|RandomAccessFile
name|raf
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|getHeaderSize
argument_list|()
index|]
decl_stmt|;
name|raf
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|raf
operator|.
name|readFully
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|readHeader
argument_list|(
operator|new
name|DataInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buf
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|// Version is already read.
DECL|method|readHeader (short version, DataInputStream in)
specifier|private
specifier|static
name|BlockMetadataHeader
name|readHeader
parameter_list|(
name|short
name|version
parameter_list|,
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DataChecksum
name|checksum
init|=
name|DataChecksum
operator|.
name|newDataChecksum
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
operator|new
name|BlockMetadataHeader
argument_list|(
name|version
argument_list|,
name|checksum
argument_list|)
return|;
block|}
comment|/**    * This writes all the fields till the beginning of checksum.    * @param out DataOutputStream    * @throws IOException    */
annotation|@
name|VisibleForTesting
DECL|method|writeHeader (DataOutputStream out, BlockMetadataHeader header)
specifier|public
specifier|static
name|void
name|writeHeader
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|BlockMetadataHeader
name|header
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeShort
argument_list|(
name|header
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|.
name|getChecksum
argument_list|()
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**    * Writes all the fields till the beginning of checksum.    * @throws IOException on error    */
DECL|method|writeHeader (DataOutputStream out, DataChecksum checksum)
specifier|public
specifier|static
name|void
name|writeHeader
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|DataChecksum
name|checksum
parameter_list|)
throws|throws
name|IOException
block|{
name|writeHeader
argument_list|(
name|out
argument_list|,
operator|new
name|BlockMetadataHeader
argument_list|(
name|VERSION
argument_list|,
name|checksum
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the size of the header    */
DECL|method|getHeaderSize ()
specifier|public
specifier|static
name|int
name|getHeaderSize
parameter_list|()
block|{
return|return
name|Short
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
operator|+
name|DataChecksum
operator|.
name|getChecksumHeaderSize
argument_list|()
return|;
block|}
block|}
end_class

end_unit

