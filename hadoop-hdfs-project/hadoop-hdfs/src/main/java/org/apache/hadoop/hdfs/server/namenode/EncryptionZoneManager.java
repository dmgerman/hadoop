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
name|java
operator|.
name|util
operator|.
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSDirectory
operator|.
name|DirOp
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

begin_import
import|import static
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
name|CRYPTO_XATTR_ENCRYPTION_ZONE
import|;
end_import

begin_comment
comment|/**  * Manages the list of encryption zones in the filesystem.  *<p/>  * The EncryptionZoneManager has its own lock, but relies on the FSDirectory  * lock being held for many operations. The FSDirectory lock should not be  * taken if the manager lock is already held.  */
end_comment

begin_class
DECL|class|EncryptionZoneManager
specifier|public
class|class
name|EncryptionZoneManager
block|{
DECL|field|LOG
specifier|public
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|EncryptionZoneManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * EncryptionZoneInt is the internal representation of an encryption zone. The    * external representation of an EZ is embodied in an EncryptionZone and    * contains the EZ's pathname.    */
DECL|class|EncryptionZoneInt
specifier|private
specifier|static
class|class
name|EncryptionZoneInt
block|{
DECL|field|inodeId
specifier|private
specifier|final
name|long
name|inodeId
decl_stmt|;
DECL|field|suite
specifier|private
specifier|final
name|CipherSuite
name|suite
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|CryptoProtocolVersion
name|version
decl_stmt|;
DECL|field|keyName
specifier|private
specifier|final
name|String
name|keyName
decl_stmt|;
DECL|method|EncryptionZoneInt (long inodeId, CipherSuite suite, CryptoProtocolVersion version, String keyName)
name|EncryptionZoneInt
parameter_list|(
name|long
name|inodeId
parameter_list|,
name|CipherSuite
name|suite
parameter_list|,
name|CryptoProtocolVersion
name|version
parameter_list|,
name|String
name|keyName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|suite
operator|!=
name|CipherSuite
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|version
operator|!=
name|CryptoProtocolVersion
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|this
operator|.
name|inodeId
operator|=
name|inodeId
expr_stmt|;
name|this
operator|.
name|suite
operator|=
name|suite
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|keyName
operator|=
name|keyName
expr_stmt|;
block|}
DECL|method|getINodeId ()
name|long
name|getINodeId
parameter_list|()
block|{
return|return
name|inodeId
return|;
block|}
DECL|method|getSuite ()
name|CipherSuite
name|getSuite
parameter_list|()
block|{
return|return
name|suite
return|;
block|}
DECL|method|getVersion ()
name|CryptoProtocolVersion
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|getKeyName ()
name|String
name|getKeyName
parameter_list|()
block|{
return|return
name|keyName
return|;
block|}
block|}
DECL|field|encryptionZones
specifier|private
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|EncryptionZoneInt
argument_list|>
name|encryptionZones
init|=
literal|null
decl_stmt|;
DECL|field|dir
specifier|private
specifier|final
name|FSDirectory
name|dir
decl_stmt|;
DECL|field|maxListEncryptionZonesResponses
specifier|private
specifier|final
name|int
name|maxListEncryptionZonesResponses
decl_stmt|;
comment|/**    * Construct a new EncryptionZoneManager.    *    * @param dir Enclosing FSDirectory    */
DECL|method|EncryptionZoneManager (FSDirectory dir, Configuration conf)
specifier|public
name|EncryptionZoneManager
parameter_list|(
name|FSDirectory
name|dir
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|maxListEncryptionZonesResponses
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIST_ENCRYPTION_ZONES_NUM_RESPONSES
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIST_ENCRYPTION_ZONES_NUM_RESPONSES_DEFAULT
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|maxListEncryptionZonesResponses
operator|>=
literal|0
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_LIST_ENCRYPTION_ZONES_NUM_RESPONSES
operator|+
literal|" "
operator|+
literal|"must be a positive integer."
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a new encryption zone.    *<p/>    * Called while holding the FSDirectory lock.    *    * @param inodeId of the encryption zone    * @param keyName encryption zone key name    */
DECL|method|addEncryptionZone (Long inodeId, CipherSuite suite, CryptoProtocolVersion version, String keyName)
name|void
name|addEncryptionZone
parameter_list|(
name|Long
name|inodeId
parameter_list|,
name|CipherSuite
name|suite
parameter_list|,
name|CryptoProtocolVersion
name|version
parameter_list|,
name|String
name|keyName
parameter_list|)
block|{
assert|assert
name|dir
operator|.
name|hasWriteLock
argument_list|()
assert|;
name|unprotectedAddEncryptionZone
argument_list|(
name|inodeId
argument_list|,
name|suite
argument_list|,
name|version
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a new encryption zone.    *<p/>    * Does not assume that the FSDirectory lock is held.    *    * @param inodeId of the encryption zone    * @param keyName encryption zone key name    */
DECL|method|unprotectedAddEncryptionZone (Long inodeId, CipherSuite suite, CryptoProtocolVersion version, String keyName)
name|void
name|unprotectedAddEncryptionZone
parameter_list|(
name|Long
name|inodeId
parameter_list|,
name|CipherSuite
name|suite
parameter_list|,
name|CryptoProtocolVersion
name|version
parameter_list|,
name|String
name|keyName
parameter_list|)
block|{
specifier|final
name|EncryptionZoneInt
name|ez
init|=
operator|new
name|EncryptionZoneInt
argument_list|(
name|inodeId
argument_list|,
name|suite
argument_list|,
name|version
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|encryptionZones
operator|==
literal|null
condition|)
block|{
name|encryptionZones
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|encryptionZones
operator|.
name|put
argument_list|(
name|inodeId
argument_list|,
name|ez
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove an encryption zone.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|removeEncryptionZone (Long inodeId)
name|void
name|removeEncryptionZone
parameter_list|(
name|Long
name|inodeId
parameter_list|)
block|{
assert|assert
name|dir
operator|.
name|hasWriteLock
argument_list|()
assert|;
if|if
condition|(
name|hasCreatedEncryptionZone
argument_list|()
condition|)
block|{
name|encryptionZones
operator|.
name|remove
argument_list|(
name|inodeId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns true if an IIP is within an encryption zone.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|isInAnEZ (INodesInPath iip)
name|boolean
name|isInAnEZ
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
throws|throws
name|UnresolvedLinkException
throws|,
name|SnapshotAccessControlException
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
return|return
operator|(
name|getEncryptionZoneForPath
argument_list|(
name|iip
argument_list|)
operator|!=
literal|null
operator|)
return|;
block|}
comment|/**    * Returns the path of the EncryptionZoneInt.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|getFullPathName (EncryptionZoneInt ezi)
specifier|private
name|String
name|getFullPathName
parameter_list|(
name|EncryptionZoneInt
name|ezi
parameter_list|)
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
return|return
name|dir
operator|.
name|getInode
argument_list|(
name|ezi
operator|.
name|getINodeId
argument_list|()
argument_list|)
operator|.
name|getFullPathName
argument_list|()
return|;
block|}
comment|/**    * Get the key name for an encryption zone. Returns null if<tt>iip</tt> is    * not within an encryption zone.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|getKeyName (final INodesInPath iip)
name|String
name|getKeyName
parameter_list|(
specifier|final
name|INodesInPath
name|iip
parameter_list|)
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
name|EncryptionZoneInt
name|ezi
init|=
name|getEncryptionZoneForPath
argument_list|(
name|iip
argument_list|)
decl_stmt|;
if|if
condition|(
name|ezi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ezi
operator|.
name|getKeyName
argument_list|()
return|;
block|}
comment|/**    * Looks up the EncryptionZoneInt for a path within an encryption zone.    * Returns null if path is not within an EZ.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|getEncryptionZoneForPath (INodesInPath iip)
specifier|private
name|EncryptionZoneInt
name|getEncryptionZoneForPath
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|iip
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|hasCreatedEncryptionZone
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|INode
argument_list|>
name|inodes
init|=
name|iip
operator|.
name|getReadOnlyINodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|inodes
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|INode
name|inode
init|=
name|inodes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|inode
operator|!=
literal|null
condition|)
block|{
specifier|final
name|EncryptionZoneInt
name|ezi
init|=
name|encryptionZones
operator|.
name|get
argument_list|(
name|inode
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ezi
operator|!=
literal|null
condition|)
block|{
return|return
name|ezi
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Looks up the nearest ancestor EncryptionZoneInt that contains the given    * path (excluding itself).    * Returns null if path is not within an EZ, or the path is the root dir '/'    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|getParentEncryptionZoneForPath (INodesInPath iip)
specifier|private
name|EncryptionZoneInt
name|getParentEncryptionZoneForPath
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|iip
argument_list|)
expr_stmt|;
name|INodesInPath
name|parentIIP
init|=
name|iip
operator|.
name|getParentINodesInPath
argument_list|()
decl_stmt|;
return|return
name|parentIIP
operator|==
literal|null
condition|?
literal|null
else|:
name|getEncryptionZoneForPath
argument_list|(
name|parentIIP
argument_list|)
return|;
block|}
comment|/**    * Returns an EncryptionZone representing the ez for a given path.    * Returns an empty marker EncryptionZone if path is not in an ez.    *    * @param iip The INodesInPath of the path to check    * @return the EncryptionZone representing the ez for the path.    */
DECL|method|getEZINodeForPath (INodesInPath iip)
name|EncryptionZone
name|getEZINodeForPath
parameter_list|(
name|INodesInPath
name|iip
parameter_list|)
block|{
specifier|final
name|EncryptionZoneInt
name|ezi
init|=
name|getEncryptionZoneForPath
argument_list|(
name|iip
argument_list|)
decl_stmt|;
if|if
condition|(
name|ezi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
operator|new
name|EncryptionZone
argument_list|(
name|ezi
operator|.
name|getINodeId
argument_list|()
argument_list|,
name|getFullPathName
argument_list|(
name|ezi
argument_list|)
argument_list|,
name|ezi
operator|.
name|getSuite
argument_list|()
argument_list|,
name|ezi
operator|.
name|getVersion
argument_list|()
argument_list|,
name|ezi
operator|.
name|getKeyName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Throws an exception if the provided path cannot be renamed into the    * destination because of differing parent encryption zones.    *<p/>    * Called while holding the FSDirectory lock.    *    * @param srcIIP source IIP    * @param dstIIP destination IIP    * @throws IOException if the src cannot be renamed to the dst    */
DECL|method|checkMoveValidity (INodesInPath srcIIP, INodesInPath dstIIP)
name|void
name|checkMoveValidity
parameter_list|(
name|INodesInPath
name|srcIIP
parameter_list|,
name|INodesInPath
name|dstIIP
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
if|if
condition|(
operator|!
name|hasCreatedEncryptionZone
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|EncryptionZoneInt
name|srcParentEZI
init|=
name|getParentEncryptionZoneForPath
argument_list|(
name|srcIIP
argument_list|)
decl_stmt|;
specifier|final
name|EncryptionZoneInt
name|dstParentEZI
init|=
name|getParentEncryptionZoneForPath
argument_list|(
name|dstIIP
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|srcInEZ
init|=
operator|(
name|srcParentEZI
operator|!=
literal|null
operator|)
decl_stmt|;
specifier|final
name|boolean
name|dstInEZ
init|=
operator|(
name|dstParentEZI
operator|!=
literal|null
operator|)
decl_stmt|;
if|if
condition|(
name|srcInEZ
operator|&&
operator|!
name|dstInEZ
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|srcIIP
operator|.
name|getPath
argument_list|()
operator|+
literal|" can't be moved from an encryption zone."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|dstInEZ
operator|&&
operator|!
name|srcInEZ
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|srcIIP
operator|.
name|getPath
argument_list|()
operator|+
literal|" can't be moved into an encryption zone."
argument_list|)
throw|;
block|}
if|if
condition|(
name|srcInEZ
condition|)
block|{
if|if
condition|(
name|srcParentEZI
operator|!=
name|dstParentEZI
condition|)
block|{
specifier|final
name|String
name|srcEZPath
init|=
name|getFullPathName
argument_list|(
name|srcParentEZI
argument_list|)
decl_stmt|;
specifier|final
name|String
name|dstEZPath
init|=
name|getFullPathName
argument_list|(
name|dstParentEZI
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|srcIIP
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" can't be moved from encryption zone "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|srcEZPath
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" to encryption zone "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|dstEZPath
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Create a new encryption zone.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|createEncryptionZone (INodesInPath srcIIP, CipherSuite suite, CryptoProtocolVersion version, String keyName)
name|XAttr
name|createEncryptionZone
parameter_list|(
name|INodesInPath
name|srcIIP
parameter_list|,
name|CipherSuite
name|suite
parameter_list|,
name|CryptoProtocolVersion
name|version
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dir
operator|.
name|hasWriteLock
argument_list|()
assert|;
comment|// Check if src is a valid path for new EZ creation
if|if
condition|(
name|srcIIP
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
literal|"cannot find "
operator|+
name|srcIIP
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|dir
operator|.
name|isNonEmptyDirectory
argument_list|(
name|srcIIP
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempt to create an encryption zone for a non-empty directory."
argument_list|)
throw|;
block|}
name|INode
name|srcINode
init|=
name|srcIIP
operator|.
name|getLastINode
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|srcINode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Attempt to create an encryption zone for a file."
argument_list|)
throw|;
block|}
if|if
condition|(
name|hasCreatedEncryptionZone
argument_list|()
operator|&&
name|encryptionZones
operator|.
name|get
argument_list|(
name|srcINode
operator|.
name|getId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Directory "
operator|+
name|srcIIP
operator|.
name|getPath
argument_list|()
operator|+
literal|" is already an encryption zone."
argument_list|)
throw|;
block|}
specifier|final
name|HdfsProtos
operator|.
name|ZoneEncryptionInfoProto
name|proto
init|=
name|PBHelperClient
operator|.
name|convert
argument_list|(
name|suite
argument_list|,
name|version
argument_list|,
name|keyName
argument_list|)
decl_stmt|;
specifier|final
name|XAttr
name|ezXAttr
init|=
name|XAttrHelper
operator|.
name|buildXAttr
argument_list|(
name|CRYPTO_XATTR_ENCRYPTION_ZONE
argument_list|,
name|proto
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|XAttr
argument_list|>
name|xattrs
init|=
name|Lists
operator|.
name|newArrayListWithCapacity
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|xattrs
operator|.
name|add
argument_list|(
name|ezXAttr
argument_list|)
expr_stmt|;
comment|// updating the xattr will call addEncryptionZone,
comment|// done this way to handle edit log loading
name|FSDirXAttrOp
operator|.
name|unprotectedSetXAttrs
argument_list|(
name|dir
argument_list|,
name|srcIIP
argument_list|,
name|xattrs
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
return|return
name|ezXAttr
return|;
block|}
comment|/**    * Cursor-based listing of encryption zones.    *<p/>    * Called while holding the FSDirectory lock.    */
DECL|method|listEncryptionZones (long prevId)
name|BatchedListEntries
argument_list|<
name|EncryptionZone
argument_list|>
name|listEncryptionZones
parameter_list|(
name|long
name|prevId
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
if|if
condition|(
operator|!
name|hasCreatedEncryptionZone
argument_list|()
condition|)
block|{
return|return
operator|new
name|BatchedListEntries
argument_list|<
name|EncryptionZone
argument_list|>
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|EncryptionZoneInt
argument_list|>
name|tailMap
init|=
name|encryptionZones
operator|.
name|tailMap
argument_list|(
name|prevId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numResponses
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxListEncryptionZonesResponses
argument_list|,
name|tailMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|EncryptionZone
argument_list|>
name|zones
init|=
name|Lists
operator|.
name|newArrayListWithExpectedSize
argument_list|(
name|numResponses
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|EncryptionZoneInt
name|ezi
range|:
name|tailMap
operator|.
name|values
argument_list|()
control|)
block|{
comment|/*        Skip EZs that are only present in snapshots. Re-resolve the path to         see if the path's current inode ID matches EZ map's INode ID.                INode#getFullPathName simply calls getParent recursively, so will return        the INode's parents at the time it was snapshotted. It will not         contain a reference INode.       */
specifier|final
name|String
name|pathName
init|=
name|getFullPathName
argument_list|(
name|ezi
argument_list|)
decl_stmt|;
name|INode
name|inode
init|=
name|dir
operator|.
name|getInode
argument_list|(
name|ezi
operator|.
name|getINodeId
argument_list|()
argument_list|)
decl_stmt|;
name|INode
name|lastINode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|inode
operator|.
name|getParent
argument_list|()
operator|!=
literal|null
operator|||
name|inode
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|INodesInPath
name|iip
init|=
name|dir
operator|.
name|getINodesInPath
argument_list|(
name|pathName
argument_list|,
name|DirOp
operator|.
name|READ_LINK
argument_list|)
decl_stmt|;
name|lastINode
operator|=
name|iip
operator|.
name|getLastINode
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|lastINode
operator|==
literal|null
operator|||
name|lastINode
operator|.
name|getId
argument_list|()
operator|!=
name|ezi
operator|.
name|getINodeId
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// Add the EZ to the result list
name|zones
operator|.
name|add
argument_list|(
operator|new
name|EncryptionZone
argument_list|(
name|ezi
operator|.
name|getINodeId
argument_list|()
argument_list|,
name|pathName
argument_list|,
name|ezi
operator|.
name|getSuite
argument_list|()
argument_list|,
name|ezi
operator|.
name|getVersion
argument_list|()
argument_list|,
name|ezi
operator|.
name|getKeyName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|>=
name|numResponses
condition|)
block|{
break|break;
block|}
block|}
specifier|final
name|boolean
name|hasMore
init|=
operator|(
name|numResponses
operator|<
name|tailMap
operator|.
name|size
argument_list|()
operator|)
decl_stmt|;
return|return
operator|new
name|BatchedListEntries
argument_list|<
name|EncryptionZone
argument_list|>
argument_list|(
name|zones
argument_list|,
name|hasMore
argument_list|)
return|;
block|}
comment|/**    * @return number of encryption zones.    */
DECL|method|getNumEncryptionZones ()
specifier|public
name|int
name|getNumEncryptionZones
parameter_list|()
block|{
return|return
name|hasCreatedEncryptionZone
argument_list|()
condition|?
name|encryptionZones
operator|.
name|size
argument_list|()
else|:
literal|0
return|;
block|}
comment|/**    * @return Whether there has been any attempt to create an encryption zone in    * the cluster at all. If not, it is safe to quickly return null when    * checking the encryption information of any file or directory in the    * cluster.    */
DECL|method|hasCreatedEncryptionZone ()
specifier|public
name|boolean
name|hasCreatedEncryptionZone
parameter_list|()
block|{
return|return
name|encryptionZones
operator|!=
literal|null
return|;
block|}
comment|/**    * @return a list of all key names.    */
DECL|method|getKeyNames ()
name|String
index|[]
name|getKeyNames
parameter_list|()
block|{
assert|assert
name|dir
operator|.
name|hasReadLock
argument_list|()
assert|;
if|if
condition|(
operator|!
name|hasCreatedEncryptionZone
argument_list|()
condition|)
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
name|String
index|[]
name|ret
init|=
operator|new
name|String
index|[
name|encryptionZones
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|EncryptionZoneInt
argument_list|>
name|entry
range|:
name|encryptionZones
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ret
index|[
name|index
operator|++
index|]
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getKeyName
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

