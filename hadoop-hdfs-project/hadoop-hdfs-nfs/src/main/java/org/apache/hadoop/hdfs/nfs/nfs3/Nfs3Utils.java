begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSClient
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
name|HdfsFileStatus
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
name|nfs
operator|.
name|NfsFileType
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
name|nfs
operator|.
name|NfsTime
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
name|nfs
operator|.
name|nfs3
operator|.
name|FileHandle
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3Constant
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
name|nfs
operator|.
name|nfs3
operator|.
name|Nfs3FileAttributes
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
name|nfs
operator|.
name|nfs3
operator|.
name|response
operator|.
name|WccAttr
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
name|nfs
operator|.
name|nfs3
operator|.
name|response
operator|.
name|WccData
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
name|oncrpc
operator|.
name|XDR
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
name|security
operator|.
name|IdMappingServiceProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
import|;
end_import

begin_comment
comment|/**  * Utility/helper methods related to NFS  */
end_comment

begin_class
DECL|class|Nfs3Utils
specifier|public
class|class
name|Nfs3Utils
block|{
DECL|field|INODEID_PATH_PREFIX
specifier|public
specifier|final
specifier|static
name|String
name|INODEID_PATH_PREFIX
init|=
literal|"/.reserved/.inodes/"
decl_stmt|;
DECL|field|READ_RPC_START
specifier|public
specifier|final
specifier|static
name|String
name|READ_RPC_START
init|=
literal|"READ_RPC_CALL_START____"
decl_stmt|;
DECL|field|READ_RPC_END
specifier|public
specifier|final
specifier|static
name|String
name|READ_RPC_END
init|=
literal|"READ_RPC_CALL_END______"
decl_stmt|;
DECL|field|WRITE_RPC_START
specifier|public
specifier|final
specifier|static
name|String
name|WRITE_RPC_START
init|=
literal|"WRITE_RPC_CALL_START____"
decl_stmt|;
DECL|field|WRITE_RPC_END
specifier|public
specifier|final
specifier|static
name|String
name|WRITE_RPC_END
init|=
literal|"WRITE_RPC_CALL_END______"
decl_stmt|;
DECL|method|getFileIdPath (FileHandle handle)
specifier|public
specifier|static
name|String
name|getFileIdPath
parameter_list|(
name|FileHandle
name|handle
parameter_list|)
block|{
return|return
name|getFileIdPath
argument_list|(
name|handle
operator|.
name|getFileId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getFileIdPath (long fileId)
specifier|public
specifier|static
name|String
name|getFileIdPath
parameter_list|(
name|long
name|fileId
parameter_list|)
block|{
return|return
name|INODEID_PATH_PREFIX
operator|+
name|fileId
return|;
block|}
DECL|method|getFileStatus (DFSClient client, String fileIdPath)
specifier|public
specifier|static
name|HdfsFileStatus
name|getFileStatus
parameter_list|(
name|DFSClient
name|client
parameter_list|,
name|String
name|fileIdPath
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|client
operator|.
name|getFileLinkInfo
argument_list|(
name|fileIdPath
argument_list|)
return|;
block|}
DECL|method|getNfs3FileAttrFromFileStatus ( HdfsFileStatus fs, IdMappingServiceProvider iug)
specifier|public
specifier|static
name|Nfs3FileAttributes
name|getNfs3FileAttrFromFileStatus
parameter_list|(
name|HdfsFileStatus
name|fs
parameter_list|,
name|IdMappingServiceProvider
name|iug
parameter_list|)
block|{
comment|/**      * Some 32bit Linux client has problem with 64bit fileId: it seems the 32bit      * client takes only the lower 32bit of the fileId and treats it as signed      * int. When the 32th bit is 1, the client considers it invalid.      */
name|NfsFileType
name|fileType
init|=
name|fs
operator|.
name|isDirectory
argument_list|()
condition|?
name|NfsFileType
operator|.
name|NFSDIR
else|:
name|NfsFileType
operator|.
name|NFSREG
decl_stmt|;
name|fileType
operator|=
name|fs
operator|.
name|isSymlink
argument_list|()
condition|?
name|NfsFileType
operator|.
name|NFSLNK
else|:
name|fileType
expr_stmt|;
name|int
name|nlink
init|=
operator|(
name|fileType
operator|==
name|NfsFileType
operator|.
name|NFSDIR
operator|)
condition|?
name|fs
operator|.
name|getChildrenNum
argument_list|()
operator|+
literal|2
else|:
literal|1
decl_stmt|;
name|long
name|size
init|=
operator|(
name|fileType
operator|==
name|NfsFileType
operator|.
name|NFSDIR
operator|)
condition|?
name|getDirSize
argument_list|(
name|fs
operator|.
name|getChildrenNum
argument_list|()
argument_list|)
else|:
name|fs
operator|.
name|getLen
argument_list|()
decl_stmt|;
return|return
operator|new
name|Nfs3FileAttributes
argument_list|(
name|fileType
argument_list|,
name|nlink
argument_list|,
name|fs
operator|.
name|getPermission
argument_list|()
operator|.
name|toShort
argument_list|()
argument_list|,
name|iug
operator|.
name|getUidAllowingUnknown
argument_list|(
name|fs
operator|.
name|getOwner
argument_list|()
argument_list|)
argument_list|,
name|iug
operator|.
name|getGidAllowingUnknown
argument_list|(
name|fs
operator|.
name|getGroup
argument_list|()
argument_list|)
argument_list|,
name|size
argument_list|,
literal|0
comment|/* fsid */
argument_list|,
name|fs
operator|.
name|getFileId
argument_list|()
argument_list|,
name|fs
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|fs
operator|.
name|getAccessTime
argument_list|()
argument_list|,
operator|new
name|Nfs3FileAttributes
operator|.
name|Specdata3
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getFileAttr (DFSClient client, String fileIdPath, IdMappingServiceProvider iug)
specifier|public
specifier|static
name|Nfs3FileAttributes
name|getFileAttr
parameter_list|(
name|DFSClient
name|client
parameter_list|,
name|String
name|fileIdPath
parameter_list|,
name|IdMappingServiceProvider
name|iug
parameter_list|)
throws|throws
name|IOException
block|{
name|HdfsFileStatus
name|fs
init|=
name|getFileStatus
argument_list|(
name|client
argument_list|,
name|fileIdPath
argument_list|)
decl_stmt|;
return|return
name|fs
operator|==
literal|null
condition|?
literal|null
else|:
name|getNfs3FileAttrFromFileStatus
argument_list|(
name|fs
argument_list|,
name|iug
argument_list|)
return|;
block|}
comment|/**    * HDFS directory size is always zero. Try to return something meaningful    * here. Assume each child take 32bytes.    */
DECL|method|getDirSize (int childNum)
specifier|public
specifier|static
name|long
name|getDirSize
parameter_list|(
name|int
name|childNum
parameter_list|)
block|{
return|return
operator|(
name|childNum
operator|+
literal|2
operator|)
operator|*
literal|32
return|;
block|}
DECL|method|getWccAttr (DFSClient client, String fileIdPath)
specifier|public
specifier|static
name|WccAttr
name|getWccAttr
parameter_list|(
name|DFSClient
name|client
parameter_list|,
name|String
name|fileIdPath
parameter_list|)
throws|throws
name|IOException
block|{
name|HdfsFileStatus
name|fstat
init|=
name|getFileStatus
argument_list|(
name|client
argument_list|,
name|fileIdPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|fstat
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|size
init|=
name|fstat
operator|.
name|isDirectory
argument_list|()
condition|?
name|getDirSize
argument_list|(
name|fstat
operator|.
name|getChildrenNum
argument_list|()
argument_list|)
else|:
name|fstat
operator|.
name|getLen
argument_list|()
decl_stmt|;
return|return
operator|new
name|WccAttr
argument_list|(
name|size
argument_list|,
operator|new
name|NfsTime
argument_list|(
name|fstat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|,
operator|new
name|NfsTime
argument_list|(
name|fstat
operator|.
name|getModificationTime
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getWccAttr (Nfs3FileAttributes attr)
specifier|public
specifier|static
name|WccAttr
name|getWccAttr
parameter_list|(
name|Nfs3FileAttributes
name|attr
parameter_list|)
block|{
return|return
name|attr
operator|==
literal|null
condition|?
operator|new
name|WccAttr
argument_list|()
else|:
operator|new
name|WccAttr
argument_list|(
name|attr
operator|.
name|getSize
argument_list|()
argument_list|,
name|attr
operator|.
name|getMtime
argument_list|()
argument_list|,
name|attr
operator|.
name|getCtime
argument_list|()
argument_list|)
return|;
block|}
comment|// TODO: maybe not efficient
DECL|method|createWccData (final WccAttr preOpAttr, DFSClient dfsClient, final String fileIdPath, final IdMappingServiceProvider iug)
specifier|public
specifier|static
name|WccData
name|createWccData
parameter_list|(
specifier|final
name|WccAttr
name|preOpAttr
parameter_list|,
name|DFSClient
name|dfsClient
parameter_list|,
specifier|final
name|String
name|fileIdPath
parameter_list|,
specifier|final
name|IdMappingServiceProvider
name|iug
parameter_list|)
throws|throws
name|IOException
block|{
name|Nfs3FileAttributes
name|postOpDirAttr
init|=
name|getFileAttr
argument_list|(
name|dfsClient
argument_list|,
name|fileIdPath
argument_list|,
name|iug
argument_list|)
decl_stmt|;
return|return
operator|new
name|WccData
argument_list|(
name|preOpAttr
argument_list|,
name|postOpDirAttr
argument_list|)
return|;
block|}
comment|/**    * Send a write response to the netty network socket channel    */
DECL|method|writeChannel (Channel channel, XDR out, int xid)
specifier|public
specifier|static
name|void
name|writeChannel
parameter_list|(
name|Channel
name|channel
parameter_list|,
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|)
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
name|RpcProgramNfs3
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Null channel should only happen in tests. Do nothing."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|RpcProgramNfs3
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|RpcProgramNfs3
operator|.
name|LOG
operator|.
name|debug
argument_list|(
name|WRITE_RPC_END
operator|+
name|xid
argument_list|)
expr_stmt|;
block|}
name|ChannelBuffer
name|outBuf
init|=
name|XDR
operator|.
name|writeMessageTcp
argument_list|(
name|out
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|outBuf
argument_list|)
expr_stmt|;
block|}
DECL|method|writeChannelCommit (Channel channel, XDR out, int xid)
specifier|public
specifier|static
name|void
name|writeChannelCommit
parameter_list|(
name|Channel
name|channel
parameter_list|,
name|XDR
name|out
parameter_list|,
name|int
name|xid
parameter_list|)
block|{
if|if
condition|(
name|RpcProgramNfs3
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|RpcProgramNfs3
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Commit done:"
operator|+
name|xid
argument_list|)
expr_stmt|;
block|}
name|ChannelBuffer
name|outBuf
init|=
name|XDR
operator|.
name|writeMessageTcp
argument_list|(
name|out
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|outBuf
argument_list|)
expr_stmt|;
block|}
DECL|method|isSet (int access, int bits)
specifier|private
specifier|static
name|boolean
name|isSet
parameter_list|(
name|int
name|access
parameter_list|,
name|int
name|bits
parameter_list|)
block|{
return|return
operator|(
name|access
operator|&
name|bits
operator|)
operator|==
name|bits
return|;
block|}
DECL|method|getAccessRights (int mode, int type)
specifier|public
specifier|static
name|int
name|getAccessRights
parameter_list|(
name|int
name|mode
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|int
name|rtn
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|isSet
argument_list|(
name|mode
argument_list|,
name|Nfs3Constant
operator|.
name|ACCESS_MODE_READ
argument_list|)
condition|)
block|{
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_READ
expr_stmt|;
comment|// LOOKUP is only meaningful for dir
if|if
condition|(
name|type
operator|==
name|NfsFileType
operator|.
name|NFSDIR
operator|.
name|toValue
argument_list|()
condition|)
block|{
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_LOOKUP
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isSet
argument_list|(
name|mode
argument_list|,
name|Nfs3Constant
operator|.
name|ACCESS_MODE_WRITE
argument_list|)
condition|)
block|{
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_MODIFY
expr_stmt|;
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_EXTEND
expr_stmt|;
comment|// Set delete bit, UNIX may ignore it for regular file since it's up to
comment|// parent dir op permission
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_DELETE
expr_stmt|;
block|}
if|if
condition|(
name|isSet
argument_list|(
name|mode
argument_list|,
name|Nfs3Constant
operator|.
name|ACCESS_MODE_EXECUTE
argument_list|)
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|NfsFileType
operator|.
name|NFSREG
operator|.
name|toValue
argument_list|()
condition|)
block|{
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_EXECUTE
expr_stmt|;
block|}
else|else
block|{
name|rtn
operator||=
name|Nfs3Constant
operator|.
name|ACCESS3_LOOKUP
expr_stmt|;
block|}
block|}
return|return
name|rtn
return|;
block|}
DECL|method|getAccessRightsForUserGroup (int uid, int gid, int[] auxGids, Nfs3FileAttributes attr)
specifier|public
specifier|static
name|int
name|getAccessRightsForUserGroup
parameter_list|(
name|int
name|uid
parameter_list|,
name|int
name|gid
parameter_list|,
name|int
index|[]
name|auxGids
parameter_list|,
name|Nfs3FileAttributes
name|attr
parameter_list|)
block|{
name|int
name|mode
init|=
name|attr
operator|.
name|getMode
argument_list|()
decl_stmt|;
if|if
condition|(
name|uid
operator|==
name|attr
operator|.
name|getUid
argument_list|()
condition|)
block|{
return|return
name|getAccessRights
argument_list|(
name|mode
operator|>>
literal|6
argument_list|,
name|attr
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|gid
operator|==
name|attr
operator|.
name|getGid
argument_list|()
condition|)
block|{
return|return
name|getAccessRights
argument_list|(
name|mode
operator|>>
literal|3
argument_list|,
name|attr
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
comment|// Check for membership in auxiliary groups
if|if
condition|(
name|auxGids
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|auxGid
range|:
name|auxGids
control|)
block|{
if|if
condition|(
name|attr
operator|.
name|getGid
argument_list|()
operator|==
name|auxGid
condition|)
block|{
return|return
name|getAccessRights
argument_list|(
name|mode
operator|>>
literal|3
argument_list|,
name|attr
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|getAccessRights
argument_list|(
name|mode
argument_list|,
name|attr
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
DECL|method|bytesToLong (byte[] data)
specifier|public
specifier|static
name|long
name|bytesToLong
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|long
name|n
init|=
literal|0xffL
operator|&
name|data
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|n
operator|=
operator|(
name|n
operator|<<
literal|8
operator|)
operator||
operator|(
literal|0xffL
operator|&
name|data
index|[
name|i
index|]
operator|)
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
DECL|method|longToByte (long v)
specifier|public
specifier|static
name|byte
index|[]
name|longToByte
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|data
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|56
argument_list|)
expr_stmt|;
name|data
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|48
argument_list|)
expr_stmt|;
name|data
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|40
argument_list|)
expr_stmt|;
name|data
index|[
literal|3
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|32
argument_list|)
expr_stmt|;
name|data
index|[
literal|4
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|24
argument_list|)
expr_stmt|;
name|data
index|[
literal|5
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|16
argument_list|)
expr_stmt|;
name|data
index|[
literal|6
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|8
argument_list|)
expr_stmt|;
name|data
index|[
literal|7
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>>
literal|0
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
DECL|method|getElapsedTime (long startTimeNano)
specifier|public
specifier|static
name|long
name|getElapsedTime
parameter_list|(
name|long
name|startTimeNano
parameter_list|)
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTimeNano
return|;
block|}
block|}
end_class

end_unit

