begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|namenode
package|;
end_package

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
name|IOException
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
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
name|DFSUtil
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
name|DeprecatedUTF8
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
name|Block
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
name|blockmanagement
operator|.
name|BlockInfo
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
name|blockmanagement
operator|.
name|BlockInfoUnderConstruction
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BlockUCState
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
name|LongWritable
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
name|ShortWritable
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
name|Text
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
name|WritableUtils
import|;
end_import

begin_comment
comment|/**  * Static utility functions for serializing various pieces of data in the correct  * format for the FSImage file.  *  * Some members are currently public for the benefit of the Offline Image Viewer  * which is located outside of this package. These members should be made  * package-protected when the OIV is refactored.  */
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
DECL|class|FSImageSerialization
specifier|public
class|class
name|FSImageSerialization
block|{
comment|// Static-only class
DECL|method|FSImageSerialization ()
specifier|private
name|FSImageSerialization
parameter_list|()
block|{}
comment|/**    * In order to reduce allocation, we reuse some static objects. However, the methods    * in this class should be thread-safe since image-saving is multithreaded, so     * we need to keep the static objects in a thread-local.    */
DECL|field|TL_DATA
specifier|static
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|TLData
argument_list|>
name|TL_DATA
init|=
operator|new
name|ThreadLocal
argument_list|<
name|TLData
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TLData
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|TLData
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/**    * Simple container "struct" for threadlocal data.    */
DECL|class|TLData
specifier|static
specifier|private
specifier|final
class|class
name|TLData
block|{
DECL|field|U_STR
specifier|final
name|DeprecatedUTF8
name|U_STR
init|=
operator|new
name|DeprecatedUTF8
argument_list|()
decl_stmt|;
DECL|field|U_SHORT
specifier|final
name|ShortWritable
name|U_SHORT
init|=
operator|new
name|ShortWritable
argument_list|()
decl_stmt|;
DECL|field|U_LONG
specifier|final
name|LongWritable
name|U_LONG
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
DECL|field|FILE_PERM
specifier|final
name|FsPermission
name|FILE_PERM
init|=
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0
argument_list|)
decl_stmt|;
block|}
comment|// Helper function that reads in an INodeUnderConstruction
comment|// from the input stream
comment|//
DECL|method|readINodeUnderConstruction ( DataInputStream in)
specifier|static
name|INodeFileUnderConstruction
name|readINodeUnderConstruction
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|name
init|=
name|readBytes
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|short
name|blockReplication
init|=
name|in
operator|.
name|readShort
argument_list|()
decl_stmt|;
name|long
name|modificationTime
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|preferredBlockSize
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|int
name|numBlocks
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|BlockInfo
index|[]
name|blocks
init|=
operator|new
name|BlockInfo
index|[
name|numBlocks
index|]
decl_stmt|;
name|Block
name|blk
init|=
operator|new
name|Block
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|numBlocks
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|blk
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|BlockInfo
argument_list|(
name|blk
argument_list|,
name|blockReplication
argument_list|)
expr_stmt|;
block|}
comment|// last block is UNDER_CONSTRUCTION
if|if
condition|(
name|numBlocks
operator|>
literal|0
condition|)
block|{
name|blk
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|blocks
index|[
name|i
index|]
operator|=
operator|new
name|BlockInfoUnderConstruction
argument_list|(
name|blk
argument_list|,
name|blockReplication
argument_list|,
name|BlockUCState
operator|.
name|UNDER_CONSTRUCTION
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|PermissionStatus
name|perm
init|=
name|PermissionStatus
operator|.
name|read
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|clientName
init|=
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|String
name|clientMachine
init|=
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// We previously stored locations for the last block, now we
comment|// just record that there are none
name|int
name|numLocs
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
assert|assert
name|numLocs
operator|==
literal|0
operator|:
literal|"Unexpected block locations"
assert|;
return|return
operator|new
name|INodeFileUnderConstruction
argument_list|(
name|name
argument_list|,
name|blockReplication
argument_list|,
name|modificationTime
argument_list|,
name|preferredBlockSize
argument_list|,
name|blocks
argument_list|,
name|perm
argument_list|,
name|clientName
argument_list|,
name|clientMachine
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|// Helper function that writes an INodeUnderConstruction
comment|// into the input stream
comment|//
DECL|method|writeINodeUnderConstruction (DataOutputStream out, INodeFileUnderConstruction cons, String path)
specifier|static
name|void
name|writeINodeUnderConstruction
parameter_list|(
name|DataOutputStream
name|out
parameter_list|,
name|INodeFileUnderConstruction
name|cons
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|writeString
argument_list|(
name|path
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|cons
operator|.
name|getFileReplication
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|cons
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|cons
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|nrBlocks
init|=
name|cons
operator|.
name|getBlocks
argument_list|()
operator|.
name|length
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|nrBlocks
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nrBlocks
condition|;
name|i
operator|++
control|)
block|{
name|cons
operator|.
name|getBlocks
argument_list|()
index|[
name|i
index|]
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|cons
operator|.
name|getPermissionStatus
argument_list|()
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|cons
operator|.
name|getClientName
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|cons
operator|.
name|getClientMachine
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//  do not store locations of last block
block|}
comment|/*    * Save one inode's attributes to the image.    */
DECL|method|saveINode2Image (INode node, DataOutputStream out)
specifier|static
name|void
name|saveINode2Image
parameter_list|(
name|INode
name|node
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|name
init|=
name|node
operator|.
name|getLocalNameBytes
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|name
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|FsPermission
name|filePerm
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|FILE_PERM
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeShort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// replication
name|out
operator|.
name|writeLong
argument_list|(
name|node
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// access time
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// preferred block size
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// # of blocks
name|out
operator|.
name|writeLong
argument_list|(
name|node
operator|.
name|getNsQuota
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|node
operator|.
name|getDsQuota
argument_list|()
argument_list|)
expr_stmt|;
name|filePerm
operator|.
name|fromShort
argument_list|(
name|node
operator|.
name|getFsPermissionShort
argument_list|()
argument_list|)
expr_stmt|;
name|PermissionStatus
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|getUserName
argument_list|()
argument_list|,
name|node
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|filePerm
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isLink
argument_list|()
condition|)
block|{
name|out
operator|.
name|writeShort
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// replication
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// modification time
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// access time
name|out
operator|.
name|writeLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// preferred block size
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// # of blocks
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
operator|(
operator|(
name|INodeSymlink
operator|)
name|node
operator|)
operator|.
name|getLinkValue
argument_list|()
argument_list|)
expr_stmt|;
name|filePerm
operator|.
name|fromShort
argument_list|(
name|node
operator|.
name|getFsPermissionShort
argument_list|()
argument_list|)
expr_stmt|;
name|PermissionStatus
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|node
operator|.
name|getUserName
argument_list|()
argument_list|,
name|node
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|filePerm
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|INodeFile
name|fileINode
init|=
operator|(
name|INodeFile
operator|)
name|node
decl_stmt|;
name|out
operator|.
name|writeShort
argument_list|(
name|fileINode
operator|.
name|getFileReplication
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|fileINode
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|fileINode
operator|.
name|getAccessTime
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|fileINode
operator|.
name|getPreferredBlockSize
argument_list|()
argument_list|)
expr_stmt|;
name|Block
index|[]
name|blocks
init|=
name|fileINode
operator|.
name|getBlocks
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Block
name|blk
range|:
name|blocks
control|)
name|blk
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|filePerm
operator|.
name|fromShort
argument_list|(
name|fileINode
operator|.
name|getFsPermissionShort
argument_list|()
argument_list|)
expr_stmt|;
name|PermissionStatus
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|fileINode
operator|.
name|getUserName
argument_list|()
argument_list|,
name|fileINode
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|filePerm
argument_list|)
expr_stmt|;
block|}
block|}
comment|// This should be reverted to package private once the ImageLoader
comment|// code is moved into this package. This method should not be called
comment|// by other code.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|readString (DataInputStream in)
specifier|public
specifier|static
name|String
name|readString
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DeprecatedUTF8
name|ustr
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_STR
decl_stmt|;
name|ustr
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|ustr
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|readString_EmptyAsNull (DataInputStream in)
specifier|static
name|String
name|readString_EmptyAsNull
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|s
init|=
name|readString
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|s
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|s
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|writeString (String str, DataOutputStream out)
specifier|static
name|void
name|writeString
parameter_list|(
name|String
name|str
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|DeprecatedUTF8
name|ustr
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_STR
decl_stmt|;
name|ustr
operator|.
name|set
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|ustr
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** read the long value */
DECL|method|readLong (DataInputStream in)
specifier|static
name|long
name|readLong
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|LongWritable
name|ustr
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_LONG
decl_stmt|;
name|ustr
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|ustr
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** write the long value */
DECL|method|writeLong (long value, DataOutputStream out)
specifier|static
name|void
name|writeLong
parameter_list|(
name|long
name|value
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|LongWritable
name|uLong
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_LONG
decl_stmt|;
name|uLong
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|uLong
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/** read short value */
DECL|method|readShort (DataInputStream in)
specifier|static
name|short
name|readShort
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ShortWritable
name|uShort
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_SHORT
decl_stmt|;
name|uShort
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|uShort
operator|.
name|get
argument_list|()
return|;
block|}
comment|/** write short value */
DECL|method|writeShort (short value, DataOutputStream out)
specifier|static
name|void
name|writeShort
parameter_list|(
name|short
name|value
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ShortWritable
name|uShort
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_SHORT
decl_stmt|;
name|uShort
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|uShort
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|// Same comments apply for this method as for readString()
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|readBytes (DataInputStream in)
specifier|public
specifier|static
name|byte
index|[]
name|readBytes
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DeprecatedUTF8
name|ustr
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_STR
decl_stmt|;
name|ustr
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|len
init|=
name|ustr
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ustr
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|bytes
return|;
block|}
comment|/**    * Reading the path from the image and converting it to byte[][] directly    * this saves us an array copy and conversions to and from String    * @param in    * @return the array each element of which is a byte[] representation     *            of a path component    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|readPathComponents (DataInputStream in)
specifier|public
specifier|static
name|byte
index|[]
index|[]
name|readPathComponents
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DeprecatedUTF8
name|ustr
init|=
name|TL_DATA
operator|.
name|get
argument_list|()
operator|.
name|U_STR
decl_stmt|;
name|ustr
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|DFSUtil
operator|.
name|bytes2byteArray
argument_list|(
name|ustr
operator|.
name|getBytes
argument_list|()
argument_list|,
name|ustr
operator|.
name|getLength
argument_list|()
argument_list|,
operator|(
name|byte
operator|)
name|Path
operator|.
name|SEPARATOR_CHAR
argument_list|)
return|;
block|}
comment|/**    * Write an array of blocks as compactly as possible. This uses    * delta-encoding for the generation stamp and size, following    * the principle that genstamp increases relatively slowly,    * and size is equal for all but the last block of a file.    */
DECL|method|writeCompactBlockArray ( Block[] blocks, DataOutputStream out)
specifier|public
specifier|static
name|void
name|writeCompactBlockArray
parameter_list|(
name|Block
index|[]
name|blocks
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|blocks
operator|.
name|length
argument_list|)
expr_stmt|;
name|Block
name|prev
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Block
name|b
range|:
name|blocks
control|)
block|{
name|long
name|szDelta
init|=
name|b
operator|.
name|getNumBytes
argument_list|()
operator|-
operator|(
name|prev
operator|!=
literal|null
condition|?
name|prev
operator|.
name|getNumBytes
argument_list|()
else|:
literal|0
operator|)
decl_stmt|;
name|long
name|gsDelta
init|=
name|b
operator|.
name|getGenerationStamp
argument_list|()
operator|-
operator|(
name|prev
operator|!=
literal|null
condition|?
name|prev
operator|.
name|getGenerationStamp
argument_list|()
else|:
literal|0
operator|)
decl_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|b
operator|.
name|getBlockId
argument_list|()
argument_list|)
expr_stmt|;
comment|// blockid is random
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|szDelta
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|gsDelta
argument_list|)
expr_stmt|;
name|prev
operator|=
name|b
expr_stmt|;
block|}
block|}
DECL|method|readCompactBlockArray ( DataInputStream in, int logVersion)
specifier|public
specifier|static
name|Block
index|[]
name|readCompactBlockArray
parameter_list|(
name|DataInputStream
name|in
parameter_list|,
name|int
name|logVersion
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|num
init|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|num
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid block array length: "
operator|+
name|num
argument_list|)
throw|;
block|}
name|Block
name|prev
init|=
literal|null
decl_stmt|;
name|Block
index|[]
name|ret
init|=
operator|new
name|Block
index|[
name|num
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
name|num
condition|;
name|i
operator|++
control|)
block|{
name|long
name|id
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|long
name|sz
init|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
operator|+
operator|(
operator|(
name|prev
operator|!=
literal|null
operator|)
condition|?
name|prev
operator|.
name|getNumBytes
argument_list|()
else|:
literal|0
operator|)
decl_stmt|;
name|long
name|gs
init|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
operator|+
operator|(
operator|(
name|prev
operator|!=
literal|null
operator|)
condition|?
name|prev
operator|.
name|getGenerationStamp
argument_list|()
else|:
literal|0
operator|)
decl_stmt|;
name|ret
index|[
name|i
index|]
operator|=
operator|new
name|Block
argument_list|(
name|id
argument_list|,
name|sz
argument_list|,
name|gs
argument_list|)
expr_stmt|;
name|prev
operator|=
name|ret
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

