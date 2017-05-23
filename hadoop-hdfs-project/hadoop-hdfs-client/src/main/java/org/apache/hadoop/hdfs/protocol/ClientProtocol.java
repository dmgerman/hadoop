begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|BatchedRemoteIterator
operator|.
name|BatchedEntries
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
name|AddBlockFlag
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
name|CacheFlag
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
name|ContentSummary
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
name|CreateFlag
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
name|FsServerDefaults
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
name|QuotaUsage
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
name|StorageType
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
name|permission
operator|.
name|AclEntry
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
name|AclStatus
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
name|hdfs
operator|.
name|inotify
operator|.
name|EventBatchList
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
operator|.
name|RollingUpgradeAction
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|DataEncryptionKey
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenSelector
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
name|protocol
operator|.
name|DatanodeStorageReport
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
name|EnumSetWritable
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
name|retry
operator|.
name|AtMostOnce
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
name|retry
operator|.
name|Idempotent
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
name|AccessControlException
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
name|KerberosInfo
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
name|token
operator|.
name|Token
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
name|token
operator|.
name|TokenInfo
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
name|client
operator|.
name|HdfsClientConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
import|;
end_import

begin_comment
comment|/**********************************************************************  * ClientProtocol is used by user code via the DistributedFileSystem class to  * communicate with the NameNode.  User code can manipulate the directory  * namespace, as well as open/close file streams, etc.  *  **********************************************************************/
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|)
annotation|@
name|TokenInfo
argument_list|(
name|DelegationTokenSelector
operator|.
name|class
argument_list|)
DECL|interface|ClientProtocol
specifier|public
interface|interface
name|ClientProtocol
block|{
comment|/**    * Until version 69, this class ClientProtocol served as both    * the client interface to the NN AND the RPC protocol used to    * communicate with the NN.    *    * This class is used by both the DFSClient and the    * NN server side to insulate from the protocol serialization.    *    * If you are adding/changing this interface then you need to    * change both this class and ALSO related protocol buffer    * wire protocol definition in ClientNamenodeProtocol.proto.    *    * For more details on protocol buffer wire protocol, please see    * .../org/apache/hadoop/hdfs/protocolPB/overview.html    *    * The log of historical changes can be retrieved from the svn).    * 69: Eliminate overloaded method names.    *    * 69L is the last version id when this class was used for protocols    *  serialization. DO not update this version any further.    */
DECL|field|versionID
name|long
name|versionID
init|=
literal|69L
decl_stmt|;
comment|///////////////////////////////////////
comment|// File contents
comment|///////////////////////////////////////
comment|/**    * Get locations of the blocks of the specified file    * within the specified range.    * DataNode locations for each block are sorted by    * the proximity to the client.    *<p>    * Return {@link LocatedBlocks} which contains    * file length, blocks and their locations.    * DataNode locations for each block are sorted by    * the distance to the client's address.    *<p>    * The client will then have to contact    * one of the indicated DataNodes to obtain the actual data.    *    * @param src file name    * @param offset range start offset    * @param length range length    *    * @return file length and array of blocks with their locations    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> does not    *           exist    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getBlockLocations (String src, long offset, long length)
name|LocatedBlocks
name|getBlockLocations
parameter_list|(
name|String
name|src
parameter_list|,
name|long
name|offset
parameter_list|,
name|long
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get server default values for a number of configuration params.    * @return a set of server default configuration values    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getServerDefaults ()
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a new file entry in the namespace.    *<p>    * This will create an empty file specified by the source path.    * The path should reflect a full path originated at the root.    * The name-node does not have a notion of "current" directory for a client.    *<p>    * Once created, the file is visible and available for read to other clients.    * Although, other clients cannot {@link #delete(String, boolean)}, re-create    * or {@link #rename(String, String)} it until the file is completed    * or explicitly as a result of lease expiration.    *<p>    * Blocks have a maximum size.  Clients that intend to create    * multi-block files must also use    * {@link #addBlock}    *    * @param src path of the file being created.    * @param masked masked permission.    * @param clientName name of the current client.    * @param flag indicates whether the file should be overwritten if it already    *             exists or create if it does not exist or append, or whether the    *             file should be a replicate file, no matter what its ancestor's    *             replication or erasure coding policy is.    * @param createParent create missing parent directory if true    * @param replication block replication factor.    * @param blockSize maximum block size.    * @param supportedVersions CryptoProtocolVersions supported by the client    * @param ecPolicyName the name of erasure coding policy. A null value means    *                     this file will inherit its parent directory's policy,    *                     either traditional replication or erasure coding    *                     policy. ecPolicyName and SHOULD_REPLICATE CreateFlag    *                     are mutually exclusive. It's invalid to set both    *                     SHOULD_REPLICATE flag and a non-null ecPolicyName.    *    * @return the status of the created file, it could be null if the server    *           doesn't support returning the file status    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws AlreadyBeingCreatedException if the path does not exist.    * @throws DSQuotaExceededException If file creation violates disk space    *           quota restriction    * @throws org.apache.hadoop.fs.FileAlreadyExistsException If file    *<code>src</code> already exists    * @throws java.io.FileNotFoundException If parent of<code>src</code> does    *           not exist and<code>createParent</code> is false    * @throws org.apache.hadoop.fs.ParentNotDirectoryException If parent of    *<code>src</code> is not a directory.    * @throws NSQuotaExceededException If file creation violates name space    *           quota restriction    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException create not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    *    * RuntimeExceptions:    * @throws org.apache.hadoop.fs.InvalidPathException Path<code>src</code> is    *           invalid    *<p>    *<em>Note that create with {@link CreateFlag#OVERWRITE} is idempotent.</em>    */
annotation|@
name|AtMostOnce
DECL|method|create (String src, FsPermission masked, String clientName, EnumSetWritable<CreateFlag> flag, boolean createParent, short replication, long blockSize, CryptoProtocolVersion[] supportedVersions, String ecPolicyName)
name|HdfsFileStatus
name|create
parameter_list|(
name|String
name|src
parameter_list|,
name|FsPermission
name|masked
parameter_list|,
name|String
name|clientName
parameter_list|,
name|EnumSetWritable
argument_list|<
name|CreateFlag
argument_list|>
name|flag
parameter_list|,
name|boolean
name|createParent
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|CryptoProtocolVersion
index|[]
name|supportedVersions
parameter_list|,
name|String
name|ecPolicyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Append to the end of the file.    * @param src path of the file being created.    * @param clientName name of the current client.    * @param flag indicates whether the data is appended to a new block.    * @return wrapper with information about the last partial block and file    *    status if any    * @throws org.apache.hadoop.security.AccessControlException if permission to    * append file is denied by the system. As usually on the client side the    * exception will be wrapped into    * {@link org.apache.hadoop.ipc.RemoteException}.    * Allows appending to an existing file if the server is    * configured with the parameter dfs.support.append set to true, otherwise    * throws an IOException.    *    * @throws org.apache.hadoop.security.AccessControlException If permission to    *           append to file is denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws DSQuotaExceededException If append violates disk space quota    *           restriction    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException append not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred.    *    * RuntimeExceptions:    * @throws UnsupportedOperationException if append is not supported    */
annotation|@
name|AtMostOnce
DECL|method|append (String src, String clientName, EnumSetWritable<CreateFlag> flag)
name|LastBlockWithStatus
name|append
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|clientName
parameter_list|,
name|EnumSetWritable
argument_list|<
name|CreateFlag
argument_list|>
name|flag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set replication for an existing file.    *<p>    * The NameNode sets replication to the new value and returns.    * The actual block replication is not expected to be performed during    * this method call. The blocks will be populated or removed in the    * background as the result of the routine block maintenance procedures.    *    * @param src file name    * @param replication new replication    *    * @return true if successful;    *         false if file does not exist or is a directory    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws DSQuotaExceededException If replication violates disk space    *           quota restriction    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|setReplication (String src, short replication)
name|boolean
name|setReplication
parameter_list|(
name|String
name|src
parameter_list|,
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get all the available block storage policies.    * @return All the in-use block storage policies currently.    */
annotation|@
name|Idempotent
DECL|method|getStoragePolicies ()
name|BlockStoragePolicy
index|[]
name|getStoragePolicies
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the storage policy for a file/directory.    * @param src Path of an existing file/directory.    * @param policyName The name of the storage policy    * @throws SnapshotAccessControlException If access is denied    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>src</code>    *           contains a symlink    * @throws java.io.FileNotFoundException If file/dir<code>src</code> is not    *           found    * @throws QuotaExceededException If changes violate the quota restriction    */
annotation|@
name|Idempotent
DECL|method|setStoragePolicy (String src, String policyName)
name|void
name|setStoragePolicy
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|policyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Unset the storage policy set for a given file or directory.    * @param src Path of an existing file/directory.    * @throws SnapshotAccessControlException If access is denied    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>src</code>    *           contains a symlink    * @throws java.io.FileNotFoundException If file/dir<code>src</code> is not    *           found    * @throws QuotaExceededException If changes violate the quota restriction    */
annotation|@
name|Idempotent
DECL|method|unsetStoragePolicy (String src)
name|void
name|unsetStoragePolicy
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the storage policy for a file/directory.    * @param path    *          Path of an existing file/directory.    * @throws AccessControlException    *           If access is denied    * @throws org.apache.hadoop.fs.UnresolvedLinkException    *           if<code>src</code> contains a symlink    * @throws java.io.FileNotFoundException    *           If file/dir<code>src</code> is not found    */
annotation|@
name|Idempotent
DECL|method|getStoragePolicy (String path)
name|BlockStoragePolicy
name|getStoragePolicy
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set permissions for an existing file/directory.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|setPermission (String src, FsPermission permission)
name|void
name|setPermission
parameter_list|(
name|String
name|src
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set Owner of a path (i.e. a file or a directory).    * The parameters username and groupname cannot both be null.    * @param src file path    * @param username If it is null, the original username remains unchanged.    * @param groupname If it is null, the original groupname remains unchanged.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|setOwner (String src, String username, String groupname)
name|void
name|setOwner
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|groupname
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The client can give up on a block by calling abandonBlock().    * The client can then either obtain a new block, or complete or abandon the    * file.    * Any partial writes to the block will be discarded.    *    * @param b         Block to abandon    * @param fileId    The id of the file where the block resides.  Older clients    *                    will pass GRANDFATHER_INODE_ID here.    * @param src       The path of the file where the block resides.    * @param holder    Lease holder.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException file<code>src</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|abandonBlock (ExtendedBlock b, long fileId, String src, String holder)
name|void
name|abandonBlock
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|,
name|long
name|fileId
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|holder
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A client that wants to write an additional block to the    * indicated filename (which must currently be open for writing)    * should call addBlock().    *    * addBlock() allocates a new block and datanodes the block data    * should be replicated to.    *    * addBlock() also commits the previous block by reporting    * to the name-node the actual generation stamp and the length    * of the block that the client has transmitted to data-nodes.    *    * @param src the file being created    * @param clientName the name of the client that adds the block    * @param previous  previous block    * @param excludeNodes a list of nodes that should not be    * allocated for the current block    * @param fileId the id uniquely identifying a file    * @param favoredNodes the list of nodes where the client wants the blocks.    *          Nodes are identified by either host name or address.    * @param addBlockFlags flags to advise the behavior of allocating and placing    *                      a new block.    *    * @return LocatedBlock allocated block information.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.NotReplicatedYetException    *           previous blocks of the file are not replicated yet.    *           Blocks cannot be added until replication completes.    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException create not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|addBlock (String src, String clientName, ExtendedBlock previous, DatanodeInfo[] excludeNodes, long fileId, String[] favoredNodes, EnumSet<AddBlockFlag> addBlockFlags)
name|LocatedBlock
name|addBlock
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|clientName
parameter_list|,
name|ExtendedBlock
name|previous
parameter_list|,
name|DatanodeInfo
index|[]
name|excludeNodes
parameter_list|,
name|long
name|fileId
parameter_list|,
name|String
index|[]
name|favoredNodes
parameter_list|,
name|EnumSet
argument_list|<
name|AddBlockFlag
argument_list|>
name|addBlockFlags
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a datanode for an existing pipeline.    *    * @param src the file being written    * @param fileId the ID of the file being written    * @param blk the block being written    * @param existings the existing nodes in the pipeline    * @param excludes the excluded nodes    * @param numAdditionalNodes number of additional datanodes    * @param clientName the name of the client    *    * @return the located block.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException create not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getAdditionalDatanode (final String src, final long fileId, final ExtendedBlock blk, final DatanodeInfo[] existings, final String[] existingStorageIDs, final DatanodeInfo[] excludes, final int numAdditionalNodes, final String clientName )
name|LocatedBlock
name|getAdditionalDatanode
parameter_list|(
specifier|final
name|String
name|src
parameter_list|,
specifier|final
name|long
name|fileId
parameter_list|,
specifier|final
name|ExtendedBlock
name|blk
parameter_list|,
specifier|final
name|DatanodeInfo
index|[]
name|existings
parameter_list|,
specifier|final
name|String
index|[]
name|existingStorageIDs
parameter_list|,
specifier|final
name|DatanodeInfo
index|[]
name|excludes
parameter_list|,
specifier|final
name|int
name|numAdditionalNodes
parameter_list|,
specifier|final
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The client is done writing data to the given filename, and would    * like to complete it.    *    * The function returns whether the file has been closed successfully.    * If the function returns false, the caller should try again.    *    * close() also commits the last block of file by reporting    * to the name-node the actual generation stamp and the length    * of the block that the client has transmitted to data-nodes.    *    * A call to complete() will not return true until all the file's    * blocks have been replicated the minimum number of times.  Thus,    * DataNode failures may cause a client to call complete() several    * times before succeeding.    *    * @param src the file being created    * @param clientName the name of the client that adds the block    * @param last the last block info    * @param fileId the id uniquely identifying a file    *    * @return true if all file blocks are minimally replicated or false otherwise    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException create not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|complete (String src, String clientName, ExtendedBlock last, long fileId)
name|boolean
name|complete
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|clientName
parameter_list|,
name|ExtendedBlock
name|last
parameter_list|,
name|long
name|fileId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * The client wants to report corrupted blocks (blocks with specified    * locations on datanodes).    * @param blocks Array of located blocks to report    */
annotation|@
name|Idempotent
DECL|method|reportBadBlocks (LocatedBlock[] blocks)
name|void
name|reportBadBlocks
parameter_list|(
name|LocatedBlock
index|[]
name|blocks
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|///////////////////////////////////////
comment|// Namespace management
comment|///////////////////////////////////////
comment|/**    * Rename an item in the file system namespace.    * @param src existing file or directory name.    * @param dst new name.    * @return true if successful, or false if the old name does not exist    * or if the new name already belongs to the namespace.    *    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException an I/O error occurred    */
annotation|@
name|AtMostOnce
DECL|method|rename (String src, String dst)
name|boolean
name|rename
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Moves blocks from srcs to trg and delete srcs.    *    * @param trg existing file    * @param srcs - list of existing files (same block size, same replication)    * @throws IOException if some arguments are invalid    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>trg</code> or    *<code>srcs</code> contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    */
annotation|@
name|AtMostOnce
DECL|method|concat (String trg, String[] srcs)
name|void
name|concat
parameter_list|(
name|String
name|trg
parameter_list|,
name|String
index|[]
name|srcs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Rename src to dst.    *<ul>    *<li>Fails if src is a file and dst is a directory.    *<li>Fails if src is a directory and dst is a file.    *<li>Fails if the parent of dst does not exist or is a file.    *</ul>    *<p>    * Without OVERWRITE option, rename fails if the dst already exists.    * With OVERWRITE option, rename overwrites the dst, if it is a file    * or an empty directory. Rename fails if dst is a non-empty directory.    *<p>    * This implementation of rename is atomic.    *<p>    * @param src existing file or directory name.    * @param dst new name.    * @param options Rename options    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws DSQuotaExceededException If rename violates disk space    *           quota restriction    * @throws org.apache.hadoop.fs.FileAlreadyExistsException If<code>dst</code>    *           already exists and<code>options</code> has    *           {@link org.apache.hadoop.fs.Options.Rename#OVERWRITE} option    *           false.    * @throws java.io.FileNotFoundException If<code>src</code> does not exist    * @throws NSQuotaExceededException If rename violates namespace    *           quota restriction    * @throws org.apache.hadoop.fs.ParentNotDirectoryException If parent of    *<code>dst</code> is not a directory    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException rename not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code> or    *<code>dst</code> contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|AtMostOnce
DECL|method|rename2 (String src, String dst, Options.Rename... options)
name|void
name|rename2
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|Options
operator|.
name|Rename
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Truncate file src to new size.    *<ul>    *<li>Fails if src is a directory.    *<li>Fails if src does not exist.    *<li>Fails if src is not closed.    *<li>Fails if new size is greater than current size.    *</ul>    *<p>    * This implementation of truncate is purely a namespace operation if truncate    * occurs at a block boundary. Requires DataNode block recovery otherwise.    *<p>    * @param src  existing file    * @param newLength  the target size    *    * @return true if client does not need to wait for block recovery,    * false if client needs to wait for block recovery.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException truncate    *           not allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|truncate (String src, long newLength, String clientName)
name|boolean
name|truncate
parameter_list|(
name|String
name|src
parameter_list|,
name|long
name|newLength
parameter_list|,
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete the given file or directory from the file system.    *<p>    * same as delete but provides a way to avoid accidentally    * deleting non empty directories programmatically.    * @param src existing name    * @param recursive if true deletes a non empty directory recursively,    * else throws an exception.    * @return true only if the existing file or directory was actually removed    * from the file system.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws java.io.FileNotFoundException If file<code>src</code> is not found    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException create not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|AtMostOnce
DECL|method|delete (String src, boolean recursive)
name|boolean
name|delete
parameter_list|(
name|String
name|src
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a directory (or hierarchy of directories) with the given    * name and permission.    *    * @param src The path of the directory being created    * @param masked The masked permission of the directory being created    * @param createParent create missing parent directory if true    *    * @return True if the operation success.    *    * @throws org.apache.hadoop.security.AccessControlException If access is    *           denied    * @throws org.apache.hadoop.fs.FileAlreadyExistsException If<code>src</code>    *           already exists    * @throws java.io.FileNotFoundException If parent of<code>src</code> does    *           not exist and<code>createParent</code> is false    * @throws NSQuotaExceededException If file creation violates quota    *           restriction    * @throws org.apache.hadoop.fs.ParentNotDirectoryException If parent of    *<code>src</code> is not a directory    * @throws org.apache.hadoop.hdfs.server.namenode.SafeModeException create not    *           allowed in safemode    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred.    *    * RunTimeExceptions:    * @throws org.apache.hadoop.fs.InvalidPathException If<code>src</code> is    *           invalid    */
annotation|@
name|Idempotent
DECL|method|mkdirs (String src, FsPermission masked, boolean createParent)
name|boolean
name|mkdirs
parameter_list|(
name|String
name|src
parameter_list|,
name|FsPermission
name|masked
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a partial listing of the indicated directory.    *    * @param src the directory name    * @param startAfter the name to start listing after encoded in java UTF8    * @param needLocation if the FileStatus should contain block locations    *    * @return a partial listing starting after startAfter    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>src</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException If<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getListing (String src, byte[] startAfter, boolean needLocation)
name|DirectoryListing
name|getListing
parameter_list|(
name|String
name|src
parameter_list|,
name|byte
index|[]
name|startAfter
parameter_list|,
name|boolean
name|needLocation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get listing of all the snapshottable directories.    *    * @return Information about all the current snapshottable directory    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getSnapshottableDirListing ()
name|SnapshottableDirectoryStatus
index|[]
name|getSnapshottableDirListing
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|///////////////////////////////////////
comment|// System issues and management
comment|///////////////////////////////////////
comment|/**    * Client programs can cause stateful changes in the NameNode    * that affect other clients.  A client may obtain a file and    * neither abandon nor complete it.  A client might hold a series    * of locks that prevent other clients from proceeding.    * Clearly, it would be bad if a client held a bunch of locks    * that it never gave up.  This can happen easily if the client    * dies unexpectedly.    *<p>    * So, the NameNode will revoke the locks and live file-creates    * for clients that it thinks have died.  A client tells the    * NameNode that it is still alive by periodically calling    * renewLease().  If a certain amount of time passes since    * the last call to renewLease(), the NameNode assumes the    * client has died.    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|renewLease (String clientName)
name|void
name|renewLease
parameter_list|(
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Start lease recovery.    * Lightweight NameNode operation to trigger lease recovery    *    * @param src path of the file to start lease recovery    * @param clientName name of the current client    * @return true if the file is already closed    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|recoverLease (String src, String clientName)
name|boolean
name|recoverLease
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|field|GET_STATS_CAPACITY_IDX
name|int
name|GET_STATS_CAPACITY_IDX
init|=
literal|0
decl_stmt|;
DECL|field|GET_STATS_USED_IDX
name|int
name|GET_STATS_USED_IDX
init|=
literal|1
decl_stmt|;
DECL|field|GET_STATS_REMAINING_IDX
name|int
name|GET_STATS_REMAINING_IDX
init|=
literal|2
decl_stmt|;
DECL|field|GET_STATS_UNDER_REPLICATED_IDX
name|int
name|GET_STATS_UNDER_REPLICATED_IDX
init|=
literal|3
decl_stmt|;
DECL|field|GET_STATS_CORRUPT_BLOCKS_IDX
name|int
name|GET_STATS_CORRUPT_BLOCKS_IDX
init|=
literal|4
decl_stmt|;
DECL|field|GET_STATS_MISSING_BLOCKS_IDX
name|int
name|GET_STATS_MISSING_BLOCKS_IDX
init|=
literal|5
decl_stmt|;
DECL|field|GET_STATS_MISSING_REPL_ONE_BLOCKS_IDX
name|int
name|GET_STATS_MISSING_REPL_ONE_BLOCKS_IDX
init|=
literal|6
decl_stmt|;
DECL|field|GET_STATS_BYTES_IN_FUTURE_BLOCKS_IDX
name|int
name|GET_STATS_BYTES_IN_FUTURE_BLOCKS_IDX
init|=
literal|7
decl_stmt|;
DECL|field|GET_STATS_PENDING_DELETION_BLOCKS_IDX
name|int
name|GET_STATS_PENDING_DELETION_BLOCKS_IDX
init|=
literal|8
decl_stmt|;
DECL|field|STATS_ARRAY_LENGTH
name|int
name|STATS_ARRAY_LENGTH
init|=
literal|9
decl_stmt|;
comment|/**    * Get a set of statistics about the filesystem.    * Right now, only eight values are returned.    *<ul>    *<li> [0] contains the total storage capacity of the system, in bytes.</li>    *<li> [1] contains the total used space of the system, in bytes.</li>    *<li> [2] contains the available storage of the system, in bytes.</li>    *<li> [3] contains number of under replicated blocks in the system.</li>    *<li> [4] contains number of blocks with a corrupt replica.</li>    *<li> [5] contains number of blocks without any good replicas left.</li>    *<li> [6] contains number of blocks which have replication factor    *          1 and have lost the only replica.</li>    *<li> [7] contains number of bytes  that are at risk for deletion.</li>    *<li> [8] contains number of pending deletion blocks.</li>    *</ul>    * Use public constants like {@link #GET_STATS_CAPACITY_IDX} in place of    * actual numbers to index into the array.    */
annotation|@
name|Idempotent
DECL|method|getStats ()
name|long
index|[]
name|getStats
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a report on the system's current datanodes.    * One DatanodeInfo object is returned for each DataNode.    * Return live datanodes if type is LIVE; dead datanodes if type is DEAD;    * otherwise all datanodes if type is ALL.    */
annotation|@
name|Idempotent
DECL|method|getDatanodeReport (HdfsConstants.DatanodeReportType type)
name|DatanodeInfo
index|[]
name|getDatanodeReport
parameter_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
name|type
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a report on the current datanode storages.    */
annotation|@
name|Idempotent
DECL|method|getDatanodeStorageReport ( HdfsConstants.DatanodeReportType type)
name|DatanodeStorageReport
index|[]
name|getDatanodeStorageReport
parameter_list|(
name|HdfsConstants
operator|.
name|DatanodeReportType
name|type
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the block size for the given file.    * @param filename The name of the file    * @return The number of bytes in each block    * @throws IOException    * @throws org.apache.hadoop.fs.UnresolvedLinkException if the path contains    *           a symlink.    */
annotation|@
name|Idempotent
DECL|method|getPreferredBlockSize (String filename)
name|long
name|getPreferredBlockSize
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Enter, leave or get safe mode.    *<p>    * Safe mode is a name node state when it    *<ol><li>does not accept changes to name space (read-only), and</li>    *<li>does not replicate or delete blocks.</li></ol>    *    *<p>    * Safe mode is entered automatically at name node startup.    * Safe mode can also be entered manually using    * {@link #setSafeMode(HdfsConstants.SafeModeAction,boolean)    * setSafeMode(SafeModeAction.SAFEMODE_ENTER,false)}.    *<p>    * At startup the name node accepts data node reports collecting    * information about block locations.    * In order to leave safe mode it needs to collect a configurable    * percentage called threshold of blocks, which satisfy the minimal    * replication condition.    * The minimal replication condition is that each block must have at least    *<tt>dfs.namenode.replication.min</tt> replicas.    * When the threshold is reached the name node extends safe mode    * for a configurable amount of time    * to let the remaining data nodes to check in before it    * will start replicating missing blocks.    * Then the name node leaves safe mode.    *<p>    * If safe mode is turned on manually using    * {@link #setSafeMode(HdfsConstants.SafeModeAction,boolean)    * setSafeMode(SafeModeAction.SAFEMODE_ENTER,false)}    * then the name node stays in safe mode until it is manually turned off    * using {@link #setSafeMode(HdfsConstants.SafeModeAction,boolean)    * setSafeMode(SafeModeAction.SAFEMODE_LEAVE,false)}.    * Current state of the name node can be verified using    * {@link #setSafeMode(HdfsConstants.SafeModeAction,boolean)    * setSafeMode(SafeModeAction.SAFEMODE_GET,false)}    *<h4>Configuration parameters:</h4>    *<tt>dfs.safemode.threshold.pct</tt> is the threshold parameter.<br>    *<tt>dfs.safemode.extension</tt> is the safe mode extension parameter.<br>    *<tt>dfs.namenode.replication.min</tt> is the minimal replication parameter.    *    *<h4>Special cases:</h4>    * The name node does not enter safe mode at startup if the threshold is    * set to 0 or if the name space is empty.<br>    * If the threshold is set to 1 then all blocks need to have at least    * minimal replication.<br>    * If the threshold value is greater than 1 then the name node will not be    * able to turn off safe mode automatically.<br>    * Safe mode can always be turned off manually.    *    * @param action<ul><li>0 leave safe mode;</li>    *<li>1 enter safe mode;</li>    *<li>2 get safe mode state.</li></ul>    * @param isChecked If true then action will be done only in ActiveNN.    *    * @return<ul><li>0 if the safe mode is OFF or</li>    *<li>1 if the safe mode is ON.</li></ul>    *    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|setSafeMode (HdfsConstants.SafeModeAction action, boolean isChecked)
name|boolean
name|setSafeMode
parameter_list|(
name|HdfsConstants
operator|.
name|SafeModeAction
name|action
parameter_list|,
name|boolean
name|isChecked
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Save namespace image.    *<p>    * Saves current namespace into storage directories and reset edits log.    * Requires superuser privilege and safe mode.    *    * @param timeWindow NameNode does a checkpoint if the latest checkpoint was    *                   done beyond the given time period (in seconds).    * @param txGap NameNode does a checkpoint if the gap between the latest    *              checkpoint and the latest transaction id is greater this gap.    * @return whether an extra checkpoint has been done    *    * @throws IOException if image creation failed.    */
annotation|@
name|AtMostOnce
DECL|method|saveNamespace (long timeWindow, long txGap)
name|boolean
name|saveNamespace
parameter_list|(
name|long
name|timeWindow
parameter_list|,
name|long
name|txGap
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Roll the edit log.    * Requires superuser privileges.    *    * @throws org.apache.hadoop.security.AccessControlException if the superuser    *           privilege is violated    * @throws IOException if log roll fails    * @return the txid of the new segment    */
annotation|@
name|Idempotent
DECL|method|rollEdits ()
name|long
name|rollEdits
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Enable/Disable restore failed storage.    *<p>    * sets flag to enable restore of failed storage replicas    *    * @throws org.apache.hadoop.security.AccessControlException if the superuser    *           privilege is violated.    */
annotation|@
name|Idempotent
DECL|method|restoreFailedStorage (String arg)
name|boolean
name|restoreFailedStorage
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Tells the namenode to reread the hosts and exclude files.    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|refreshNodes ()
name|void
name|refreshNodes
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Finalize previous upgrade.    * Remove file system state saved during the upgrade.    * The upgrade will become irreversible.    *    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|finalizeUpgrade ()
name|void
name|finalizeUpgrade
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Rolling upgrade operations.    * @param action either query, prepare or finalize.    * @return rolling upgrade information. On query, if no upgrade is in    * progress, returns null.    */
annotation|@
name|Idempotent
DECL|method|rollingUpgrade (RollingUpgradeAction action)
name|RollingUpgradeInfo
name|rollingUpgrade
parameter_list|(
name|RollingUpgradeAction
name|action
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return CorruptFileBlocks, containing a list of corrupt files (with    *         duplicates if there is more than one corrupt block in a file)    *         and a cookie    * @throws IOException    *    * Each call returns a subset of the corrupt files in the system. To obtain    * all corrupt files, call this method repeatedly and each time pass in the    * cookie returned from the previous call.    */
annotation|@
name|Idempotent
DECL|method|listCorruptFileBlocks (String path, String cookie)
name|CorruptFileBlocks
name|listCorruptFileBlocks
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|cookie
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Dumps namenode data structures into specified file. If the file    * already exists, then append.    *    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|metaSave (String filename)
name|void
name|metaSave
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Tell all datanodes to use a new, non-persistent bandwidth value for    * dfs.datanode.balance.bandwidthPerSec.    *    * @param bandwidth Blanacer bandwidth in bytes per second for this datanode.    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|setBalancerBandwidth (long bandwidth)
name|void
name|setBalancerBandwidth
parameter_list|(
name|long
name|bandwidth
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the file info for a specific file or directory.    * @param src The string representation of the path to the file    *    * @return object containing information regarding the file    *         or null if file not found    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>src</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException if the path contains    *           a symlink.    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getFileInfo (String src)
name|HdfsFileStatus
name|getFileInfo
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the close status of a file.    * @param src The string representation of the path to the file    *    * @return return true if file is closed    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>src</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException if the path contains    *           a symlink.    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|isFileClosed (String src)
name|boolean
name|isFileClosed
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the file info for a specific file or directory. If the path    * refers to a symlink then the FileStatus of the symlink is returned.    * @param src The string representation of the path to the file    *    * @return object containing information regarding the file    *         or null if file not found    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>src</code>    *           contains a symlink    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getFileLinkInfo (String src)
name|HdfsFileStatus
name|getFileLinkInfo
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get {@link ContentSummary} rooted at the specified directory.    * @param path The string representation of the path    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>path</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>path</code>    *           contains a symlink.    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getContentSummary (String path)
name|ContentSummary
name|getContentSummary
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the quota for a directory.    * @param path  The string representation of the path to the directory    * @param namespaceQuota Limit on the number of names in the tree rooted    *                       at the directory    * @param storagespaceQuota Limit on storage space occupied all the files    *                       under this directory.    * @param type StorageType that the space quota is intended to be set on.    *             It may be null when called by traditional space/namespace    *             quota. When type is is not null, the storagespaceQuota    *             parameter is for type specified and namespaceQuota must be    *             {@link HdfsConstants#QUOTA_DONT_SET}.    *    *<br><br>    *    * The quota can have three types of values : (1) 0 or more will set    * the quota to that value, (2) {@link HdfsConstants#QUOTA_DONT_SET}  implies    * the quota will not be changed, and (3) {@link HdfsConstants#QUOTA_RESET}    * implies the quota will be reset. Any other value is a runtime error.    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>path</code> is not found    * @throws QuotaExceededException if the directory size    *           is greater than the given quota    * @throws org.apache.hadoop.fs.UnresolvedLinkException if the    *<code>path</code> contains a symlink.    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|setQuota (String path, long namespaceQuota, long storagespaceQuota, StorageType type)
name|void
name|setQuota
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|namespaceQuota
parameter_list|,
name|long
name|storagespaceQuota
parameter_list|,
name|StorageType
name|type
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Write all metadata for this file into persistent storage.    * The file must be currently open for writing.    * @param src The string representation of the path    * @param inodeId The inode ID, or GRANDFATHER_INODE_ID if the client is    *                too old to support fsync with inode IDs.    * @param client The string representation of the client    * @param lastBlockLength The length of the last block (under construction)    *                        to be reported to NameNode    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>src</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>src</code>    *           contains a symlink.    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|fsync (String src, long inodeId, String client, long lastBlockLength)
name|void
name|fsync
parameter_list|(
name|String
name|src
parameter_list|,
name|long
name|inodeId
parameter_list|,
name|String
name|client
parameter_list|,
name|long
name|lastBlockLength
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets the modification and access time of the file to the specified time.    * @param src The string representation of the path    * @param mtime The number of milliseconds since Jan 1, 1970.    *              Setting mtime to -1 means that modification time should not    *              be set by this call.    * @param atime The number of milliseconds since Jan 1, 1970.    *              Setting atime to -1 means that access time should not be set    *              by this call.    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>src</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>src</code>    *           contains a symlink.    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|setTimes (String src, long mtime, long atime)
name|void
name|setTimes
parameter_list|(
name|String
name|src
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create symlink to a file or directory.    * @param target The path of the destination that the    *               link points to.    * @param link The path of the link being created.    * @param dirPerm permissions to use when creating parent directories    * @param createParent - if true then missing parent dirs are created    *                       if false then parent must exist    *    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws org.apache.hadoop.fs.FileAlreadyExistsException If file    *<code>link</code> already exists    * @throws java.io.FileNotFoundException If parent of<code>link</code> does    *           not exist and<code>createParent</code> is false    * @throws org.apache.hadoop.fs.ParentNotDirectoryException If parent of    *<code>link</code> is not a directory.    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>link</code>    *           contains a symlink.    * @throws SnapshotAccessControlException if path is in RO snapshot    * @throws IOException If an I/O error occurred    */
annotation|@
name|AtMostOnce
DECL|method|createSymlink (String target, String link, FsPermission dirPerm, boolean createParent)
name|void
name|createSymlink
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|link
parameter_list|,
name|FsPermission
name|dirPerm
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return the target of the given symlink. If there is an intermediate    * symlink in the path (ie a symlink leading up to the final path component)    * then the given path is returned with this symlink resolved.    *    * @param path The path with a link that needs resolution.    * @return The path after resolving the first symbolic link in the path.    * @throws org.apache.hadoop.security.AccessControlException permission denied    * @throws java.io.FileNotFoundException If<code>path</code> does not exist    * @throws IOException If the given path does not refer to a symlink    *           or an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getLinkTarget (String path)
name|String
name|getLinkTarget
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a new generation stamp together with an access token for    * a block under construction    *    * This method is called only when a client needs to recover a failed    * pipeline or set up a pipeline for appending to a block.    *    * @param block a block    * @param clientName the name of the client    * @return a located block with a new generation stamp and an access token    * @throws IOException if any error occurs    */
annotation|@
name|Idempotent
DECL|method|updateBlockForPipeline (ExtendedBlock block, String clientName)
name|LocatedBlock
name|updateBlockForPipeline
parameter_list|(
name|ExtendedBlock
name|block
parameter_list|,
name|String
name|clientName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Update a pipeline for a block under construction.    *    * @param clientName the name of the client    * @param oldBlock the old block    * @param newBlock the new block containing new generation stamp and length    * @param newNodes datanodes in the pipeline    * @throws IOException if any error occurs    */
annotation|@
name|AtMostOnce
DECL|method|updatePipeline (String clientName, ExtendedBlock oldBlock, ExtendedBlock newBlock, DatanodeID[] newNodes, String[] newStorageIDs)
name|void
name|updatePipeline
parameter_list|(
name|String
name|clientName
parameter_list|,
name|ExtendedBlock
name|oldBlock
parameter_list|,
name|ExtendedBlock
name|newBlock
parameter_list|,
name|DatanodeID
index|[]
name|newNodes
parameter_list|,
name|String
index|[]
name|newStorageIDs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a valid Delegation Token.    *    * @param renewer the designated renewer for the token    * @return Token<DelegationTokenIdentifier>    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getDelegationToken (Text renewer)
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|getDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Renew an existing delegation token.    *    * @param token delegation token obtained earlier    * @return the new expiration time    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|renewDelegationToken (Token<DelegationTokenIdentifier> token)
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Cancel an existing delegation token.    *    * @param token delegation token    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|cancelDelegationToken (Token<DelegationTokenIdentifier> token)
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return encryption key so a client can encrypt data sent via the    *         DataTransferProtocol to/from DataNodes.    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getDataEncryptionKey ()
name|DataEncryptionKey
name|getDataEncryptionKey
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Create a snapshot.    * @param snapshotRoot the path that is being snapshotted    * @param snapshotName name of the snapshot created    * @return the snapshot path.    * @throws IOException    */
annotation|@
name|AtMostOnce
DECL|method|createSnapshot (String snapshotRoot, String snapshotName)
name|String
name|createSnapshot
parameter_list|(
name|String
name|snapshotRoot
parameter_list|,
name|String
name|snapshotName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Delete a specific snapshot of a snapshottable directory.    * @param snapshotRoot  The snapshottable directory    * @param snapshotName Name of the snapshot for the snapshottable directory    * @throws IOException    */
annotation|@
name|AtMostOnce
DECL|method|deleteSnapshot (String snapshotRoot, String snapshotName)
name|void
name|deleteSnapshot
parameter_list|(
name|String
name|snapshotRoot
parameter_list|,
name|String
name|snapshotName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Rename a snapshot.    * @param snapshotRoot the directory path where the snapshot was taken    * @param snapshotOldName old name of the snapshot    * @param snapshotNewName new name of the snapshot    * @throws IOException    */
annotation|@
name|AtMostOnce
DECL|method|renameSnapshot (String snapshotRoot, String snapshotOldName, String snapshotNewName)
name|void
name|renameSnapshot
parameter_list|(
name|String
name|snapshotRoot
parameter_list|,
name|String
name|snapshotOldName
parameter_list|,
name|String
name|snapshotNewName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Allow snapshot on a directory.    * @param snapshotRoot the directory to be snapped    * @throws IOException on error    */
annotation|@
name|Idempotent
DECL|method|allowSnapshot (String snapshotRoot)
name|void
name|allowSnapshot
parameter_list|(
name|String
name|snapshotRoot
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Disallow snapshot on a directory.    * @param snapshotRoot the directory to disallow snapshot    * @throws IOException on error    */
annotation|@
name|Idempotent
DECL|method|disallowSnapshot (String snapshotRoot)
name|void
name|disallowSnapshot
parameter_list|(
name|String
name|snapshotRoot
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the difference between two snapshots, or between a snapshot and the    * current tree of a directory.    *    * @param snapshotRoot    *          full path of the directory where snapshots are taken    * @param fromSnapshot    *          snapshot name of the from point. Null indicates the current    *          tree    * @param toSnapshot    *          snapshot name of the to point. Null indicates the current    *          tree.    * @return The difference report represented as a {@link SnapshotDiffReport}.    * @throws IOException on error    */
annotation|@
name|Idempotent
DECL|method|getSnapshotDiffReport (String snapshotRoot, String fromSnapshot, String toSnapshot)
name|SnapshotDiffReport
name|getSnapshotDiffReport
parameter_list|(
name|String
name|snapshotRoot
parameter_list|,
name|String
name|fromSnapshot
parameter_list|,
name|String
name|toSnapshot
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add a CacheDirective to the CacheManager.    *    * @param directive A CacheDirectiveInfo to be added    * @param flags {@link CacheFlag}s to use for this operation.    * @return A CacheDirectiveInfo associated with the added directive    * @throws IOException if the directive could not be added    */
annotation|@
name|AtMostOnce
DECL|method|addCacheDirective (CacheDirectiveInfo directive, EnumSet<CacheFlag> flags)
name|long
name|addCacheDirective
parameter_list|(
name|CacheDirectiveInfo
name|directive
parameter_list|,
name|EnumSet
argument_list|<
name|CacheFlag
argument_list|>
name|flags
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Modify a CacheDirective in the CacheManager.    *    * @param flags {@link CacheFlag}s to use for this operation.    * @throws IOException if the directive could not be modified    */
annotation|@
name|AtMostOnce
DECL|method|modifyCacheDirective (CacheDirectiveInfo directive, EnumSet<CacheFlag> flags)
name|void
name|modifyCacheDirective
parameter_list|(
name|CacheDirectiveInfo
name|directive
parameter_list|,
name|EnumSet
argument_list|<
name|CacheFlag
argument_list|>
name|flags
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove a CacheDirectiveInfo from the CacheManager.    *    * @param id of a CacheDirectiveInfo    * @throws IOException if the cache directive could not be removed    */
annotation|@
name|AtMostOnce
DECL|method|removeCacheDirective (long id)
name|void
name|removeCacheDirective
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List the set of cached paths of a cache pool. Incrementally fetches results    * from the server.    *    * @param prevId The last listed entry ID, or -1 if this is the first call to    *               listCacheDirectives.    * @param filter Parameters to use to filter the list results,    *               or null to display all directives visible to us.    * @return A batch of CacheDirectiveEntry objects.    */
annotation|@
name|Idempotent
DECL|method|listCacheDirectives ( long prevId, CacheDirectiveInfo filter)
name|BatchedEntries
argument_list|<
name|CacheDirectiveEntry
argument_list|>
name|listCacheDirectives
parameter_list|(
name|long
name|prevId
parameter_list|,
name|CacheDirectiveInfo
name|filter
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add a new cache pool.    *    * @param info Description of the new cache pool    * @throws IOException If the request could not be completed.    */
annotation|@
name|AtMostOnce
DECL|method|addCachePool (CachePoolInfo info)
name|void
name|addCachePool
parameter_list|(
name|CachePoolInfo
name|info
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Modify an existing cache pool.    *    * @param req    *          The request to modify a cache pool.    * @throws IOException    *          If the request could not be completed.    */
annotation|@
name|AtMostOnce
DECL|method|modifyCachePool (CachePoolInfo req)
name|void
name|modifyCachePool
parameter_list|(
name|CachePoolInfo
name|req
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove a cache pool.    *    * @param pool name of the cache pool to remove.    * @throws IOException if the cache pool did not exist, or could not be    *           removed.    */
annotation|@
name|AtMostOnce
DECL|method|removeCachePool (String pool)
name|void
name|removeCachePool
parameter_list|(
name|String
name|pool
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List the set of cache pools. Incrementally fetches results from the server.    *    * @param prevPool name of the last pool listed, or the empty string if this    *          is the first invocation of listCachePools    * @return A batch of CachePoolEntry objects.    */
annotation|@
name|Idempotent
DECL|method|listCachePools (String prevPool)
name|BatchedEntries
argument_list|<
name|CachePoolEntry
argument_list|>
name|listCachePools
parameter_list|(
name|String
name|prevPool
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Modifies ACL entries of files and directories.  This method can add new ACL    * entries or modify the permissions on existing ACL entries.  All existing    * ACL entries that are not specified in this call are retained without    * changes.  (Modifications are merged into the current ACL.)    */
annotation|@
name|Idempotent
DECL|method|modifyAclEntries (String src, List<AclEntry> aclSpec)
name|void
name|modifyAclEntries
parameter_list|(
name|String
name|src
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes ACL entries from files and directories.  Other ACL entries are    * retained.    */
annotation|@
name|Idempotent
DECL|method|removeAclEntries (String src, List<AclEntry> aclSpec)
name|void
name|removeAclEntries
parameter_list|(
name|String
name|src
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes all default ACL entries from files and directories.    */
annotation|@
name|Idempotent
DECL|method|removeDefaultAcl (String src)
name|void
name|removeDefaultAcl
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes all but the base ACL entries of files and directories.  The entries    * for user, group, and others are retained for compatibility with permission    * bits.    */
annotation|@
name|Idempotent
DECL|method|removeAcl (String src)
name|void
name|removeAcl
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Fully replaces ACL of files and directories, discarding all existing    * entries.    */
annotation|@
name|Idempotent
DECL|method|setAcl (String src, List<AclEntry> aclSpec)
name|void
name|setAcl
parameter_list|(
name|String
name|src
parameter_list|,
name|List
argument_list|<
name|AclEntry
argument_list|>
name|aclSpec
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets the ACLs of files and directories.    */
annotation|@
name|Idempotent
DECL|method|getAclStatus (String src)
name|AclStatus
name|getAclStatus
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Create an encryption zone.    */
annotation|@
name|AtMostOnce
DECL|method|createEncryptionZone (String src, String keyName)
name|void
name|createEncryptionZone
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the encryption zone for a path.    */
annotation|@
name|Idempotent
DECL|method|getEZForPath (String src)
name|EncryptionZone
name|getEZForPath
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Used to implement cursor-based batched listing of {@EncryptionZone}s.    *    * @param prevId ID of the last item in the previous batch. If there is no    *               previous batch, a negative value can be used.    * @return Batch of encryption zones.    */
annotation|@
name|Idempotent
DECL|method|listEncryptionZones ( long prevId)
name|BatchedEntries
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
function_decl|;
comment|/**    * Set xattr of a file or directory.    * The name must be prefixed with the namespace followed by ".". For example,    * "user.attr".    *<p/>    * Refer to the HDFS extended attributes user documentation for details.    *    * @param src file or directory    * @param xAttr<code>XAttr</code> to set    * @param flag set flag    * @throws IOException    */
annotation|@
name|AtMostOnce
DECL|method|setXAttr (String src, XAttr xAttr, EnumSet<XAttrSetFlag> flag)
name|void
name|setXAttr
parameter_list|(
name|String
name|src
parameter_list|,
name|XAttr
name|xAttr
parameter_list|,
name|EnumSet
argument_list|<
name|XAttrSetFlag
argument_list|>
name|flag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get xattrs of a file or directory. Values in xAttrs parameter are ignored.    * If xAttrs is null or empty, this is the same as getting all xattrs of the    * file or directory.  Only those xattrs for which the logged-in user has    * permissions to view are returned.    *<p/>    * Refer to the HDFS extended attributes user documentation for details.    *    * @param src file or directory    * @param xAttrs xAttrs to get    * @return List<XAttr><code>XAttr</code> list    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getXAttrs (String src, List<XAttr> xAttrs)
name|List
argument_list|<
name|XAttr
argument_list|>
name|getXAttrs
parameter_list|(
name|String
name|src
parameter_list|,
name|List
argument_list|<
name|XAttr
argument_list|>
name|xAttrs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * List the xattrs names for a file or directory.    * Only the xattr names for which the logged in user has the permissions to    * access will be returned.    *<p/>    * Refer to the HDFS extended attributes user documentation for details.    *    * @param src file or directory    * @return List<XAttr><code>XAttr</code> list    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|listXAttrs (String src)
name|List
argument_list|<
name|XAttr
argument_list|>
name|listXAttrs
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove xattr of a file or directory.Value in xAttr parameter is ignored.    * The name must be prefixed with the namespace followed by ".". For example,    * "user.attr".    *<p/>    * Refer to the HDFS extended attributes user documentation for details.    *    * @param src file or directory    * @param xAttr<code>XAttr</code> to remove    * @throws IOException    */
annotation|@
name|AtMostOnce
DECL|method|removeXAttr (String src, XAttr xAttr)
name|void
name|removeXAttr
parameter_list|(
name|String
name|src
parameter_list|,
name|XAttr
name|xAttr
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Checks if the user can access a path.  The mode specifies which access    * checks to perform.  If the requested permissions are granted, then the    * method returns normally.  If access is denied, then the method throws an    * {@link org.apache.hadoop.security.AccessControlException}.    * In general, applications should avoid using this method, due to the risk of    * time-of-check/time-of-use race conditions.  The permissions on a file may    * change immediately after the access call returns.    *    * @param path Path to check    * @param mode type of access to check    * @throws org.apache.hadoop.security.AccessControlException if access is    *           denied    * @throws java.io.FileNotFoundException if the path does not exist    * @throws IOException see specific implementation    */
annotation|@
name|Idempotent
DECL|method|checkAccess (String path, FsAction mode)
name|void
name|checkAccess
parameter_list|(
name|String
name|path
parameter_list|,
name|FsAction
name|mode
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the highest txid the NameNode knows has been written to the edit    * log, or -1 if the NameNode's edit log is not yet open for write. Used as    * the starting point for the inotify event stream.    */
annotation|@
name|Idempotent
DECL|method|getCurrentEditLogTxid ()
name|long
name|getCurrentEditLogTxid
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get an ordered list of batches of events corresponding to the edit log    * transactions for txids equal to or greater than txid.    */
annotation|@
name|Idempotent
DECL|method|getEditsFromTxid (long txid)
name|EventBatchList
name|getEditsFromTxid
parameter_list|(
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Set an erasure coding policy on a specified path.    * @param src The path to set policy on.    * @param ecPolicyName The erasure coding policy name.    */
annotation|@
name|AtMostOnce
DECL|method|setErasureCodingPolicy (String src, String ecPolicyName)
name|void
name|setErasureCodingPolicy
parameter_list|(
name|String
name|src
parameter_list|,
name|String
name|ecPolicyName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Add Erasure coding policies.    *    * @param policies The user defined ec policy list to add.    * @return Return the response list of adding operations.    * @throws IOException    */
annotation|@
name|AtMostOnce
DECL|method|addErasureCodingPolicies ( ErasureCodingPolicy[] policies)
name|AddingECPolicyResponse
index|[]
name|addErasureCodingPolicies
parameter_list|(
name|ErasureCodingPolicy
index|[]
name|policies
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the erasure coding policies loaded in Namenode.    *    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getErasureCodingPolicies ()
name|ErasureCodingPolicy
index|[]
name|getErasureCodingPolicies
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the erasure coding codecs loaded in Namenode.    *    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getErasureCodingCodecs ()
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getErasureCodingCodecs
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the information about the EC policy for the path.    *    * @param src path to get the info for    * @throws IOException    */
annotation|@
name|Idempotent
DECL|method|getErasureCodingPolicy (String src)
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Unset erasure coding policy from a specified path.    * @param src The path to unset policy.    */
annotation|@
name|AtMostOnce
DECL|method|unsetErasureCodingPolicy (String src)
name|void
name|unsetErasureCodingPolicy
parameter_list|(
name|String
name|src
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get {@link QuotaUsage} rooted at the specified directory.    * @param path The string representation of the path    *    * @throws AccessControlException permission denied    * @throws java.io.FileNotFoundException file<code>path</code> is not found    * @throws org.apache.hadoop.fs.UnresolvedLinkException if<code>path</code>    *         contains a symlink.    * @throws IOException If an I/O error occurred    */
annotation|@
name|Idempotent
DECL|method|getQuotaUsage (String path)
name|QuotaUsage
name|getQuotaUsage
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

