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
import|import static
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
name|CRYPTO_XATTR_FILE_ENCRYPTION_INFO
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
name|security
operator|.
name|GeneralSecurityException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedExceptionAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
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
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|Map
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
name|crypto
operator|.
name|CipherSuite
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
name|crypto
operator|.
name|CryptoProtocolVersion
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderCryptoExtension
operator|.
name|EncryptedKeyVersion
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
name|FileEncryptionInfo
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
name|UnresolvedLinkException
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
name|XAttr
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
name|XAttrSetFlag
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
name|BatchedRemoteIterator
operator|.
name|BatchedListEntries
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
name|FsAction
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
name|XAttrHelper
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
name|EncryptionZone
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
name|hdfs
operator|.
name|protocol
operator|.
name|SnapshotAccessControlException
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
name|proto
operator|.
name|HdfsProtos
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
name|protocolPB
operator|.
name|PBHelperClient
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
name|SecurityUtil
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
name|base
operator|.
name|Preconditions
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|InvalidProtocolBufferException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
operator|.
name|monotonicNow
import|;
end_import

begin_comment
comment|/**  * Helper class to perform encryption zone operation.  */
end_comment

begin_class
DECL|class|FSDirEncryptionZoneOp
specifier|final
class|class
name|FSDirEncryptionZoneOp
block|{
comment|/**    * Private constructor for preventing FSDirEncryptionZoneOp object creation.    * Static-only class.    */
DECL|method|FSDirEncryptionZoneOp ()
specifier|private
name|FSDirEncryptionZoneOp
parameter_list|()
block|{}
comment|/**    * Invoke KeyProvider APIs to generate an encrypted data encryption key for    * an encryption zone. Should not be called with any locks held.    *    * @param fsd fsdirectory    * @param ezKeyName key name of an encryption zone    * @return New EDEK, or null if ezKeyName is null    * @throws IOException    */
DECL|method|generateEncryptedDataEncryptionKey ( final FSDirectory fsd, final String ezKeyName)
specifier|static
name|EncryptedKeyVersion
name|generateEncryptedDataEncryptionKey
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|ezKeyName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|ezKeyName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|generateEDEKStartTime
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
comment|// Generate EDEK with login user (hdfs) so that KMS does not need
comment|// an extra proxy configuration allowing hdfs to proxy its clients and
comment|// KMS does not need configuration to allow non-hdfs user GENERATE_EEK
comment|// operation.
name|EncryptedKeyVersion
name|edek
init|=
name|SecurityUtil
operator|.
name|doAsLoginUser
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|EncryptedKeyVersion
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|EncryptedKeyVersion
name|run
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|fsd
operator|.
name|getProvider
argument_list|()
operator|.
name|generateEncryptedKey
argument_list|(
name|ezKeyName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GeneralSecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|long
name|generateEDEKTime
init|=
name|monotonicNow
argument_list|()
operator|-
name|generateEDEKStartTime
decl_stmt|;
name|NameNode
operator|.
name|getNameNodeMetrics
argument_list|()
operator|.
name|addGenerateEDEKTime
argument_list|(
name|generateEDEKTime
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|edek
argument_list|)
expr_stmt|;
return|return
name|edek
return|;
block|}
DECL|method|ensureKeyIsInitialized (final FSDirectory fsd, final String keyName, final String src)
specifier|static
name|KeyProvider
operator|.
name|Metadata
name|ensureKeyIsInitialized
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|keyName
parameter_list|,
specifier|final
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|KeyProviderCryptoExtension
name|provider
init|=
name|fsd
operator|.
name|getProvider
argument_list|()
decl_stmt|;
if|if
condition|(
name|provider
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't create an encryption zone for "
operator|+
name|src
operator|+
literal|" since no key provider is available."
argument_list|)
throw|;
block|}
if|if
condition|(
name|keyName
operator|==
literal|null
operator|||
name|keyName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Must specify a key name when creating an "
operator|+
literal|"encryption zone"
argument_list|)
throw|;
block|}
name|KeyProvider
operator|.
name|Metadata
name|metadata
init|=
name|provider
operator|.
name|getMetadata
argument_list|(
name|keyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
block|{
comment|/*        * It would be nice if we threw something more specific than        * IOException when the key is not found, but the KeyProvider API        * doesn't provide for that. If that API is ever changed to throw        * something more specific (e.g. UnknownKeyException) then we can        * update this to match it, or better yet, just rethrow the        * KeyProvider's exception.        */
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Key "
operator|+
name|keyName
operator|+
literal|" doesn't exist."
argument_list|)
throw|;
block|}
comment|// If the provider supports pool for EDEKs, this will fill in the pool
name|provider
operator|.
name|warmUpEncryptedKeys
argument_list|(
name|keyName
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
comment|/**    * Create an encryption zone on directory path using the specified key.    *    * @param fsd fsdirectory    * @param srcArg the path of a directory which will be the root of the    *               encryption zone. The directory must be empty    * @param pc permission checker to check fs permission    * @param cipher cipher    * @param keyName name of a key which must be present in the configured    *                KeyProvider    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding    * @return HdfsFileStatus    * @throws IOException    */
DECL|method|createEncryptionZone (final FSDirectory fsd, final String srcArg, final FSPermissionChecker pc, final String cipher, final String keyName, final boolean logRetryCache)
specifier|static
name|HdfsFileStatus
name|createEncryptionZone
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
specifier|final
name|FSPermissionChecker
name|pc
parameter_list|,
specifier|final
name|String
name|cipher
parameter_list|,
specifier|final
name|String
name|keyName
parameter_list|,
specifier|final
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|srcArg
argument_list|)
decl_stmt|;
specifier|final
name|CipherSuite
name|suite
init|=
name|CipherSuite
operator|.
name|convert
argument_list|(
name|cipher
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|String
name|src
decl_stmt|;
comment|// For now this is hard coded, as we only support one method.
specifier|final
name|CryptoProtocolVersion
name|version
init|=
name|CryptoProtocolVersion
operator|.
name|ENCRYPTION_ZONES
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|srcArg
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
specifier|final
name|XAttr
name|ezXAttr
init|=
name|fsd
operator|.
name|ezManager
operator|.
name|createEncryptionZone
argument_list|(
name|src
argument_list|,
name|suite
argument_list|,
name|version
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|ezXAttr
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logSetXAttrs
argument_list|(
name|src
argument_list|,
name|xAttrs
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
specifier|final
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|getINodesInPath4Write
argument_list|(
name|src
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
return|;
block|}
comment|/**    * Get the encryption zone for the specified path.    *    * @param fsd fsdirectory    * @param srcArg the path of a file or directory to get the EZ for    * @param pc permission checker to check fs permission    * @return the EZ with file status.    */
DECL|method|getEZForPath ( final FSDirectory fsd, final String srcArg, final FSPermissionChecker pc)
specifier|static
name|Map
operator|.
name|Entry
argument_list|<
name|EncryptionZone
argument_list|,
name|HdfsFileStatus
argument_list|>
name|getEZForPath
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|srcArg
parameter_list|,
specifier|final
name|FSPermissionChecker
name|pc
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|FSDirectory
operator|.
name|getPathComponentsForReservedPath
argument_list|(
name|srcArg
argument_list|)
decl_stmt|;
specifier|final
name|String
name|src
decl_stmt|;
specifier|final
name|INodesInPath
name|iip
decl_stmt|;
specifier|final
name|EncryptionZone
name|ret
decl_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|src
operator|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|srcArg
argument_list|,
name|pathComponents
argument_list|)
expr_stmt|;
name|iip
operator|=
name|fsd
operator|.
name|getINodesInPath
argument_list|(
name|src
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|iip
operator|.
name|getLastINode
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Path not found: "
operator|+
name|iip
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkPathAccess
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
name|ret
operator|=
name|fsd
operator|.
name|ezManager
operator|.
name|getEZINodeForPath
argument_list|(
name|iip
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
name|HdfsFileStatus
name|auditStat
init|=
name|fsd
operator|.
name|getAuditFileInfo
argument_list|(
name|iip
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractMap
operator|.
name|SimpleImmutableEntry
argument_list|<>
argument_list|(
name|ret
argument_list|,
name|auditStat
argument_list|)
return|;
block|}
DECL|method|getEZForPath (final FSDirectory fsd, final INodesInPath iip)
specifier|static
name|EncryptionZone
name|getEZForPath
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|INodesInPath
name|iip
parameter_list|)
block|{
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|fsd
operator|.
name|ezManager
operator|.
name|getEZINodeForPath
argument_list|(
name|iip
argument_list|)
return|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|listEncryptionZones ( final FSDirectory fsd, final long prevId)
specifier|static
name|BatchedListEntries
argument_list|<
name|EncryptionZone
argument_list|>
name|listEncryptionZones
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|long
name|prevId
parameter_list|)
throws|throws
name|IOException
block|{
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|fsd
operator|.
name|ezManager
operator|.
name|listEncryptionZones
argument_list|(
name|prevId
argument_list|)
return|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Set the FileEncryptionInfo for an INode.    *    * @param fsd fsdirectory    * @param src the path of a directory which will be the root of the    *            encryption zone.    * @param info file encryption information    * @throws IOException    */
DECL|method|setFileEncryptionInfo (final FSDirectory fsd, final String src, final FileEncryptionInfo info)
specifier|static
name|void
name|setFileEncryptionInfo
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|FileEncryptionInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Make the PB for the xattr
specifier|final
name|HdfsProtos
operator|.
name|PerFileEncryptionInfoProto
name|proto
init|=
name|PBHelperClient
operator|.
name|convertPerFileEncInfo
argument_list|(
name|info
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|protoBytes
init|=
name|proto
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
specifier|final
name|XAttr
name|fileEncryptionAttr
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|CRYPTO_XATTR_FILE_ENCRYPTION_INFO
argument_list|,
name|protoBytes
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xAttrs
operator|.
name|add
argument_list|(
name|fileEncryptionAttr
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|FSDirXAttrOp
operator|.
name|unprotectedSetXAttrs
argument_list|(
name|fsd
argument_list|,
name|src
argument_list|,
name|xAttrs
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|XAttrSetFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * This function combines the per-file encryption info (obtained    * from the inode's XAttrs), and the encryption info from its zone, and    * returns a consolidated FileEncryptionInfo instance. Null is returned    * for non-encrypted files.    *    * @param fsd fsdirectory    * @param inode inode of the file    * @param snapshotId ID of the snapshot that    *                   we want to get encryption info from    * @param iip inodes in the path containing the file, passed in to    *            avoid obtaining the list of inodes again; if iip is    *            null then the list of inodes will be obtained again    * @return consolidated file encryption info; null for non-encrypted files    */
DECL|method|getFileEncryptionInfo (final FSDirectory fsd, final INode inode, final int snapshotId, final INodesInPath iip)
specifier|static
name|FileEncryptionInfo
name|getFileEncryptionInfo
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|INode
name|inode
parameter_list|,
specifier|final
name|int
name|snapshotId
parameter_list|,
specifier|final
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|inode
operator|.
name|isFile
argument_list|()
operator|||
operator|!
name|fsd
operator|.
name|ezManager
operator|.
name|hasCreatedEncryptionZone
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|EncryptionZone
name|encryptionZone
init|=
name|getEZForPath
argument_list|(
name|fsd
argument_list|,
name|iip
argument_list|)
decl_stmt|;
if|if
condition|(
name|encryptionZone
operator|==
literal|null
condition|)
block|{
comment|// not an encrypted file
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|encryptionZone
operator|.
name|getPath
argument_list|()
operator|==
literal|null
operator|||
name|encryptionZone
operator|.
name|getPath
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|NameNode
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Encryption zone "
operator|+
name|encryptionZone
operator|.
name|getPath
argument_list|()
operator|+
literal|" does not have a valid path."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|CryptoProtocolVersion
name|version
init|=
name|encryptionZone
operator|.
name|getVersion
argument_list|()
decl_stmt|;
specifier|final
name|CipherSuite
name|suite
init|=
name|encryptionZone
operator|.
name|getSuite
argument_list|()
decl_stmt|;
specifier|final
name|String
name|keyName
init|=
name|encryptionZone
operator|.
name|getKeyName
argument_list|()
decl_stmt|;
name|XAttr
name|fileXAttr
init|=
name|FSDirXAttrOp
operator|.
name|unprotectedGetXAttrByPrefixedName
argument_list|(
name|inode
argument_list|,
name|snapshotId
argument_list|,
name|CRYPTO_XATTR_FILE_ENCRYPTION_INFO
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileXAttr
operator|==
literal|null
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not find encryption XAttr for file "
operator|+
name|iip
operator|.
name|getPath
argument_list|()
operator|+
literal|" in encryption zone "
operator|+
name|encryptionZone
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
try|try
block|{
name|HdfsProtos
operator|.
name|PerFileEncryptionInfoProto
name|fileProto
init|=
name|HdfsProtos
operator|.
name|PerFileEncryptionInfoProto
operator|.
name|parseFrom
argument_list|(
name|fileXAttr
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|fileProto
argument_list|,
name|suite
argument_list|,
name|version
argument_list|,
name|keyName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvalidProtocolBufferException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not parse file encryption info for "
operator|+
literal|"inode "
operator|+
name|inode
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isInAnEZ (final FSDirectory fsd, final INodesInPath iip)
specifier|static
name|boolean
name|isInAnEZ
parameter_list|(
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|UnresolvedLinkException
throws|,
name|SnapshotAccessControlException
block|{
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|fsd
operator|.
name|ezManager
operator|.
name|isInAnEZ
argument_list|(
name|iip
argument_list|)
return|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Proactively warm up the edek cache. We'll get all the edek key names,    * then launch up a separate thread to warm them up.    */
DECL|method|warmUpEdekCache (final ExecutorService executor, final FSDirectory fsd, final int delay, final int interval)
specifier|static
name|void
name|warmUpEdekCache
parameter_list|(
specifier|final
name|ExecutorService
name|executor
parameter_list|,
specifier|final
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|int
name|delay
parameter_list|,
specifier|final
name|int
name|interval
parameter_list|)
block|{
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|String
index|[]
name|edeks
init|=
name|fsd
operator|.
name|ezManager
operator|.
name|getKeyNames
argument_list|()
decl_stmt|;
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|EDEKCacheLoader
argument_list|(
name|edeks
argument_list|,
name|fsd
operator|.
name|getProvider
argument_list|()
argument_list|,
name|delay
argument_list|,
name|interval
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * EDEKCacheLoader is being run in a separate thread to loop through all the    * EDEKs and warm them up in the KMS cache.    */
DECL|class|EDEKCacheLoader
specifier|static
class|class
name|EDEKCacheLoader
implements|implements
name|Runnable
block|{
DECL|field|keyNames
specifier|private
specifier|final
name|String
index|[]
name|keyNames
decl_stmt|;
DECL|field|kp
specifier|private
specifier|final
name|KeyProviderCryptoExtension
name|kp
decl_stmt|;
DECL|field|initialDelay
specifier|private
name|int
name|initialDelay
decl_stmt|;
DECL|field|retryInterval
specifier|private
name|int
name|retryInterval
decl_stmt|;
DECL|method|EDEKCacheLoader (final String[] names, final KeyProviderCryptoExtension kp, final int delay, final int interval)
name|EDEKCacheLoader
parameter_list|(
specifier|final
name|String
index|[]
name|names
parameter_list|,
specifier|final
name|KeyProviderCryptoExtension
name|kp
parameter_list|,
specifier|final
name|int
name|delay
parameter_list|,
specifier|final
name|int
name|interval
parameter_list|)
block|{
name|this
operator|.
name|keyNames
operator|=
name|names
expr_stmt|;
name|this
operator|.
name|kp
operator|=
name|kp
expr_stmt|;
name|this
operator|.
name|initialDelay
operator|=
name|delay
expr_stmt|;
name|this
operator|.
name|retryInterval
operator|=
name|interval
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Warming up {} EDEKs... (initialDelay={}, "
operator|+
literal|"retryInterval={})"
argument_list|,
name|keyNames
operator|.
name|length
argument_list|,
name|initialDelay
argument_list|,
name|retryInterval
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|initialDelay
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"EDEKCacheLoader interrupted before warming up."
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|logCoolDown
init|=
literal|10000
decl_stmt|;
comment|// periodically print error log (if any)
name|int
name|sinceLastLog
init|=
name|logCoolDown
decl_stmt|;
comment|// always print the first failure
name|boolean
name|success
init|=
literal|false
decl_stmt|;
name|IOException
name|lastSeenIOE
init|=
literal|null
decl_stmt|;
name|long
name|warmUpEDEKStartTime
init|=
name|monotonicNow
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|kp
operator|.
name|warmUpEncryptedKeys
argument_list|(
name|keyNames
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully warmed up {} EDEKs."
argument_list|,
name|keyNames
operator|.
name|length
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|lastSeenIOE
operator|=
name|ioe
expr_stmt|;
if|if
condition|(
name|sinceLastLog
operator|>=
name|logCoolDown
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to warm up EDEKs."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
name|sinceLastLog
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to warm up EDEKs."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot warm up EDEKs."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|retryInterval
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|info
argument_list|(
literal|"EDEKCacheLoader interrupted during retry."
argument_list|)
expr_stmt|;
break|break;
block|}
name|sinceLastLog
operator|+=
name|retryInterval
expr_stmt|;
block|}
name|long
name|warmUpEDEKTime
init|=
name|monotonicNow
argument_list|()
operator|-
name|warmUpEDEKStartTime
decl_stmt|;
name|NameNode
operator|.
name|getNameNodeMetrics
argument_list|()
operator|.
name|addWarmUpEDEKTime
argument_list|(
name|warmUpEDEKTime
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to warm up EDEKs."
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastSeenIOE
operator|!=
literal|null
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Last seen exception:"
argument_list|,
name|lastSeenIOE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

