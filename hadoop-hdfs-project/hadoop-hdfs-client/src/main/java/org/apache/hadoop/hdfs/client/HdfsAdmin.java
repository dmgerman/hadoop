begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
package|;
end_package

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
name|fs
operator|.
name|BlockStoragePolicySpi
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
name|RemoteIterator
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
name|DFSInotifyEventInputStream
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
name|protocol
operator|.
name|AddErasureCodingPolicyResponse
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
name|CacheDirectiveEntry
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
name|CacheDirectiveInfo
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
name|CachePoolEntry
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
name|CachePoolInfo
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
name|ErasureCodingPolicy
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
name|ErasureCodingPolicyInfo
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
operator|.
name|ReencryptAction
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
name|OpenFileEntry
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
name|OpenFilesIterator
operator|.
name|OpenFilesType
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
name|ZoneReencryptionStatus
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
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * The public API for performing administrative functions on HDFS. Those writing  * applications against HDFS should prefer this interface to directly accessing  * functionality in DistributedFileSystem or DFSClient.  *  * Note that this is distinct from the similarly-named DFSAdmin, which  * is a class that provides the functionality for the CLI `hdfs dfsadmin ...'  * commands.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HdfsAdmin
specifier|public
class|class
name|HdfsAdmin
block|{
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
DECL|field|TRASH_PERMISSION
specifier|private
specifier|static
specifier|final
name|FsPermission
name|TRASH_PERMISSION
init|=
operator|new
name|FsPermission
argument_list|(
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|,
name|FsAction
operator|.
name|ALL
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Create a new HdfsAdmin client.    *    * @param uri the unique URI of the HDFS file system to administer    * @param conf configuration    * @throws IOException in the event the file system could not be created    */
DECL|method|HdfsAdmin (URI uri, Configuration conf)
specifier|public
name|HdfsAdmin
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'"
operator|+
name|uri
operator|+
literal|"' is not an HDFS URI."
argument_list|)
throw|;
block|}
else|else
block|{
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
expr_stmt|;
block|}
block|}
comment|/**    * Set the namespace quota (count of files, directories, and sym links) for a    * directory.    *    * @param src the path to set the quota for    * @param quota the value to set for the quota    * @throws IOException in the event of error    */
DECL|method|setQuota (Path src, long quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|Path
name|src
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|quota
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear the namespace quota (count of files, directories and sym links) for a    * directory.    *    * @param src the path to clear the quota of    * @throws IOException in the event of error    */
DECL|method|clearQuota (Path src)
specifier|public
name|void
name|clearQuota
parameter_list|(
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the storage space quota (size of files) for a directory. Note that    * directories and sym links do not occupy storage space.    *    * @param src the path to set the space quota of    * @param spaceQuota the value to set for the space quota    * @throws IOException in the event of error    */
DECL|method|setSpaceQuota (Path src, long spaceQuota)
specifier|public
name|void
name|setSpaceQuota
parameter_list|(
name|Path
name|src
parameter_list|,
name|long
name|spaceQuota
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
name|spaceQuota
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear the storage space quota (size of files) for a directory. Note that    * directories and sym links do not occupy storage space.    *    * @param src the path to clear the space quota of    * @throws IOException in the event of error    */
DECL|method|clearSpaceQuota (Path src)
specifier|public
name|void
name|clearSpaceQuota
parameter_list|(
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the quota by storage type for a directory. Note that    * directories and sym links do not occupy storage type quota.    *    * @param src the target directory to set the quota by storage type    * @param type the storage type to set for quota by storage type    * @param quota the value to set for quota by storage type    * @throws IOException in the event of error    */
DECL|method|setQuotaByStorageType (Path src, StorageType type, long quota)
specifier|public
name|void
name|setQuotaByStorageType
parameter_list|(
name|Path
name|src
parameter_list|,
name|StorageType
name|type
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuotaByStorageType
argument_list|(
name|src
argument_list|,
name|type
argument_list|,
name|quota
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear the space quota by storage type for a directory. Note that    * directories and sym links do not occupy storage type quota.    *    * @param src the target directory to clear the quota by storage type    * @param type the storage type to clear for quota by storage type    * @throws IOException in the event of error    */
DECL|method|clearQuotaByStorageType (Path src, StorageType type)
specifier|public
name|void
name|clearQuotaByStorageType
parameter_list|(
name|Path
name|src
parameter_list|,
name|StorageType
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuotaByStorageType
argument_list|(
name|src
argument_list|,
name|type
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allow snapshot on a directory.    * @param path The path of the directory where snapshots will be taken.    */
DECL|method|allowSnapshot (Path path)
specifier|public
name|void
name|allowSnapshot
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Disallow snapshot on a directory.    * @param path The path of the snapshottable directory.    */
DECL|method|disallowSnapshot (Path path)
specifier|public
name|void
name|disallowSnapshot
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|disallowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add a new CacheDirectiveInfo.    *    * @param info Information about a directive to add.    * @param flags {@link CacheFlag}s to use for this operation.    * @return the ID of the directive that was created.    * @throws IOException if the directive could not be added    */
DECL|method|addCacheDirective (CacheDirectiveInfo info, EnumSet<CacheFlag> flags)
specifier|public
name|long
name|addCacheDirective
parameter_list|(
name|CacheDirectiveInfo
name|info
parameter_list|,
name|EnumSet
argument_list|<
name|CacheFlag
argument_list|>
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|addCacheDirective
argument_list|(
name|info
argument_list|,
name|flags
argument_list|)
return|;
block|}
comment|/**    * Modify a CacheDirective.    *    * @param info Information about the directive to modify. You must set the ID    *          to indicate which CacheDirective you want to modify.    * @param flags {@link CacheFlag}s to use for this operation.    * @throws IOException if the directive could not be modified    */
DECL|method|modifyCacheDirective (CacheDirectiveInfo info, EnumSet<CacheFlag> flags)
specifier|public
name|void
name|modifyCacheDirective
parameter_list|(
name|CacheDirectiveInfo
name|info
parameter_list|,
name|EnumSet
argument_list|<
name|CacheFlag
argument_list|>
name|flags
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|modifyCacheDirective
argument_list|(
name|info
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove a CacheDirective.    *    * @param id identifier of the CacheDirectiveInfo to remove    * @throws IOException if the directive could not be removed    */
DECL|method|removeCacheDirective (long id)
specifier|public
name|void
name|removeCacheDirective
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|removeCacheDirective
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
comment|/**    * List cache directives. Incrementally fetches results from the server.    *    * @param filter Filter parameters to use when listing the directives, null to    *               list all directives visible to us.    * @return A RemoteIterator which returns CacheDirectiveInfo objects.    */
DECL|method|listCacheDirectives ( CacheDirectiveInfo filter)
specifier|public
name|RemoteIterator
argument_list|<
name|CacheDirectiveEntry
argument_list|>
name|listCacheDirectives
parameter_list|(
name|CacheDirectiveInfo
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listCacheDirectives
argument_list|(
name|filter
argument_list|)
return|;
block|}
comment|/**    * Add a cache pool.    *    * @param info    *          The request to add a cache pool.    * @throws IOException    *          If the request could not be completed.    */
DECL|method|addCachePool (CachePoolInfo info)
specifier|public
name|void
name|addCachePool
parameter_list|(
name|CachePoolInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|addCachePool
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**    * Modify an existing cache pool.    *    * @param info    *          The request to modify a cache pool.    * @throws IOException    *          If the request could not be completed.    */
DECL|method|modifyCachePool (CachePoolInfo info)
specifier|public
name|void
name|modifyCachePool
parameter_list|(
name|CachePoolInfo
name|info
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|modifyCachePool
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove a cache pool.    *    * @param poolName    *          Name of the cache pool to remove.    * @throws IOException    *          if the cache pool did not exist, or could not be removed.    */
DECL|method|removeCachePool (String poolName)
specifier|public
name|void
name|removeCachePool
parameter_list|(
name|String
name|poolName
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|removeCachePool
argument_list|(
name|poolName
argument_list|)
expr_stmt|;
block|}
comment|/**    * List all cache pools.    *    * @return A remote iterator from which you can get CachePoolEntry objects.    *          Requests will be made as needed.    * @throws IOException    *          If there was an error listing cache pools.    */
DECL|method|listCachePools ()
specifier|public
name|RemoteIterator
argument_list|<
name|CachePoolEntry
argument_list|>
name|listCachePools
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listCachePools
argument_list|()
return|;
block|}
comment|/**    * Get KeyProvider if present.    *    * @return the key provider if encryption is enabled on HDFS.    *         Otherwise, it returns null.    * @throws IOException on RPC exception to the NN.    */
DECL|method|getKeyProvider ()
specifier|public
name|KeyProvider
name|getKeyProvider
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getKeyProvider
argument_list|()
return|;
block|}
comment|/**    * Create an encryption zone rooted at an empty existing directory, using the    * specified encryption key. An encryption zone has an associated encryption    * key used when reading and writing files within the zone.    *    * @param path    The path of the root of the encryption zone. Must refer to    *                an empty, existing directory.    * @param keyName Name of key available at the KeyProvider.    * @throws IOException            if there was a general IO exception    * @throws AccessControlException if the caller does not have access to path    * @throws FileNotFoundException  if the path does not exist    */
annotation|@
name|Deprecated
DECL|method|createEncryptionZone (Path path, String keyName)
specifier|public
name|void
name|createEncryptionZone
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|keyName
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
throws|,
name|FileNotFoundException
block|{
name|dfs
operator|.
name|createEncryptionZone
argument_list|(
name|path
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an encryption zone rooted at an empty existing directory, using the    * specified encryption key. An encryption zone has an associated encryption    * key used when reading and writing files within the zone.    *    * Additional options, such as provisioning the trash directory, can be    * specified using {@link CreateEncryptionZoneFlag} flags.    *    * @param path    The path of the root of the encryption zone. Must refer to    *                an empty, existing directory.    * @param keyName Name of key available at the KeyProvider.    * @param flags   flags for this operation.    * @throws IOException            if there was a general IO exception    * @throws AccessControlException if the caller does not have access to path    * @throws FileNotFoundException  if the path does not exist    * @throws HadoopIllegalArgumentException if the flags are invalid    */
DECL|method|createEncryptionZone (Path path, String keyName, EnumSet<CreateEncryptionZoneFlag> flags)
specifier|public
name|void
name|createEncryptionZone
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|keyName
parameter_list|,
name|EnumSet
argument_list|<
name|CreateEncryptionZoneFlag
argument_list|>
name|flags
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|HadoopIllegalArgumentException
block|{
name|dfs
operator|.
name|createEncryptionZone
argument_list|(
name|path
argument_list|,
name|keyName
argument_list|)
expr_stmt|;
if|if
condition|(
name|flags
operator|.
name|contains
argument_list|(
name|CreateEncryptionZoneFlag
operator|.
name|PROVISION_TRASH
argument_list|)
condition|)
block|{
if|if
condition|(
name|flags
operator|.
name|contains
argument_list|(
name|CreateEncryptionZoneFlag
operator|.
name|NO_TRASH
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"can not have both PROVISION_TRASH and NO_TRASH flags"
argument_list|)
throw|;
block|}
name|dfs
operator|.
name|provisionEZTrash
argument_list|(
name|path
argument_list|,
name|TRASH_PERMISSION
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Provision a trash directory for a given encryption zone.     * @param path the root of the encryption zone    * @throws IOException if the trash directory can not be created.    */
DECL|method|provisionEncryptionZoneTrash (Path path)
specifier|public
name|void
name|provisionEncryptionZoneTrash
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|provisionEZTrash
argument_list|(
name|path
argument_list|,
name|TRASH_PERMISSION
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the path of the encryption zone for a given file or directory.    *    * @param path The path to get the ez for.    * @return An EncryptionZone, or null if path does not exist or is not in an    * ez.    * @throws IOException            if there was a general IO exception    * @throws AccessControlException if the caller does not have access to path    */
DECL|method|getEncryptionZoneForPath (Path path)
specifier|public
name|EncryptionZone
name|getEncryptionZoneForPath
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|AccessControlException
block|{
return|return
name|dfs
operator|.
name|getEZForPath
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Returns a RemoteIterator which can be used to list the encryption zones    * in HDFS. For large numbers of encryption zones, the iterator will fetch    * the list of zones in a number of small batches.    *<p>    * Since the list is fetched in batches, it does not represent a    * consistent snapshot of the entire list of encryption zones.    *<p>    * This method can only be called by HDFS superusers.    */
DECL|method|listEncryptionZones ()
specifier|public
name|RemoteIterator
argument_list|<
name|EncryptionZone
argument_list|>
name|listEncryptionZones
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listEncryptionZones
argument_list|()
return|;
block|}
comment|/**    * Performs re-encryption action for a given encryption zone.    *    * @param zone the root of the encryption zone    * @param action the re-encrypt action    * @throws IOException If any error occurs when handling re-encrypt action.    */
DECL|method|reencryptEncryptionZone (final Path zone, final ReencryptAction action)
specifier|public
name|void
name|reencryptEncryptionZone
parameter_list|(
specifier|final
name|Path
name|zone
parameter_list|,
specifier|final
name|ReencryptAction
name|action
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|reencryptEncryptionZone
argument_list|(
name|zone
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a RemoteIterator which can be used to list all re-encryption    * information. For large numbers of re-encryptions, the iterator will fetch    * the list in a number of small batches.    *<p>    * Since the list is fetched in batches, it does not represent a    * consistent snapshot of the entire list of encryption zones.    *<p>    * This method can only be called by HDFS superusers.    */
DECL|method|listReencryptionStatus ()
specifier|public
name|RemoteIterator
argument_list|<
name|ZoneReencryptionStatus
argument_list|>
name|listReencryptionStatus
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listReencryptionStatus
argument_list|()
return|;
block|}
comment|/**    * Returns the FileEncryptionInfo on the HdfsFileStatus for the given path.    * The return value can be null if the path points to a directory, or a file    * that is not in an encryption zone.    *    * @throws FileNotFoundException if the path does not exist.    * @throws AccessControlException if no execute permission on parent path.    */
DECL|method|getFileEncryptionInfo (final Path path)
specifier|public
name|FileEncryptionInfo
name|getFileEncryptionInfo
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getFileEncryptionInfo
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Exposes a stream of namesystem events. Only events occurring after the    * stream is created are available.    * See {@link org.apache.hadoop.hdfs.DFSInotifyEventInputStream}    * for information on stream usage.    * See {@link org.apache.hadoop.hdfs.inotify.Event}    * for information on the available events.    *<p>    * Inotify users may want to tune the following HDFS parameters to    * ensure that enough extra HDFS edits are saved to support inotify clients    * that fall behind the current state of the namespace while reading events.    * The default parameter values should generally be reasonable. If edits are    * deleted before their corresponding events can be read, clients will see a    * {@link org.apache.hadoop.hdfs.inotify.MissingEventsException} on    * {@link org.apache.hadoop.hdfs.DFSInotifyEventInputStream} method calls.    *    * It should generally be sufficient to tune these parameters:    * dfs.namenode.num.extra.edits.retained    * dfs.namenode.max.extra.edits.segments.retained    *    * Parameters that affect the number of created segments and the number of    * edits that are considered necessary, i.e. do not count towards the    * dfs.namenode.num.extra.edits.retained quota):    * dfs.namenode.checkpoint.period    * dfs.namenode.checkpoint.txns    * dfs.namenode.num.checkpoints.retained    * dfs.ha.log-roll.period    *<p>    * It is recommended that local journaling be configured    * (dfs.namenode.edits.dir) for inotify (in addition to a shared journal)    * so that edit transfers from the shared journal can be avoided.    *    * @throws IOException If there was an error obtaining the stream.    */
DECL|method|getInotifyEventStream ()
specifier|public
name|DFSInotifyEventInputStream
name|getInotifyEventStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getInotifyEventStream
argument_list|()
return|;
block|}
comment|/**    * A version of {@link HdfsAdmin#getInotifyEventStream()} meant for advanced    * users who are aware of HDFS edits up to lastReadTxid (e.g. because they    * have access to an FSImage inclusive of lastReadTxid) and only want to read    * events after this point.    */
DECL|method|getInotifyEventStream (long lastReadTxid)
specifier|public
name|DFSInotifyEventInputStream
name|getInotifyEventStream
parameter_list|(
name|long
name|lastReadTxid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getInotifyEventStream
argument_list|(
name|lastReadTxid
argument_list|)
return|;
block|}
comment|/**    * Set the source path to the specified storage policy.    *    * @param src The source path referring to either a directory or a file.    * @param policyName The name of the storage policy.    */
DECL|method|setStoragePolicy (final Path src, final String policyName)
specifier|public
name|void
name|setStoragePolicy
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|,
specifier|final
name|String
name|policyName
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setStoragePolicy
argument_list|(
name|src
argument_list|,
name|policyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Unset the storage policy set for a given file or directory.    *    * @param src file or directory path.    * @throws IOException    */
DECL|method|unsetStoragePolicy (final Path src)
specifier|public
name|void
name|unsetStoragePolicy
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|unsetStoragePolicy
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
comment|/**    * Query the effective storage policy ID for the given file or directory.    *    * @param src file or directory path.    * @return storage policy for the given file or directory.    * @throws IOException    */
DECL|method|getStoragePolicy (final Path src)
specifier|public
name|BlockStoragePolicySpi
name|getStoragePolicy
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getStoragePolicy
argument_list|(
name|src
argument_list|)
return|;
block|}
comment|/**    * Retrieve all the storage policies supported by HDFS file system.    *    * @return all storage policies supported by HDFS file system.    * @throws IOException    */
DECL|method|getAllStoragePolicies ()
specifier|public
name|Collection
argument_list|<
name|?
extends|extends
name|BlockStoragePolicySpi
argument_list|>
name|getAllStoragePolicies
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getAllStoragePolicies
argument_list|()
return|;
block|}
comment|/**    * Set the source path to the specified erasure coding policy.    *    * @param path The source path referring to a directory.    * @param ecPolicyName The erasure coding policy name for the directory.    *    * @throws IOException    * @throws HadoopIllegalArgumentException if the specified EC policy is not    * enabled on the cluster    */
DECL|method|setErasureCodingPolicy (final Path path, final String ecPolicyName)
specifier|public
name|void
name|setErasureCodingPolicy
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|String
name|ecPolicyName
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setErasureCodingPolicy
argument_list|(
name|path
argument_list|,
name|ecPolicyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the erasure coding policy information for the specified path    *    * @param path    * @return Returns the policy information if file or directory on the path is    *          erasure coded. Null otherwise.    * @throws IOException    */
DECL|method|getErasureCodingPolicy (final Path path)
specifier|public
name|ErasureCodingPolicy
name|getErasureCodingPolicy
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getErasureCodingPolicy
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * Set the source path to the specified storage policy.    *    * @param path The source path referring to either a directory or a file.    * @throws IOException    */
DECL|method|satisfyStoragePolicy (final Path path)
specifier|public
name|void
name|satisfyStoragePolicy
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|satisfyStoragePolicy
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the Erasure coding policies supported.    *    * @throws IOException    */
DECL|method|getErasureCodingPolicies ()
specifier|public
name|ErasureCodingPolicyInfo
index|[]
name|getErasureCodingPolicies
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getClient
argument_list|()
operator|.
name|getErasureCodingPolicies
argument_list|()
return|;
block|}
comment|/**    * Unset erasure coding policy from the directory.    *    * @param path The source path referring to a directory.    * @throws IOException    */
DECL|method|unsetErasureCodingPolicy (final Path path)
specifier|public
name|void
name|unsetErasureCodingPolicy
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|unsetErasureCodingPolicy
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add Erasure coding policies to HDFS. For each policy input, schema and    * cellSize are musts, name and id are ignored. They will be automatically    * created and assigned by Namenode once the policy is successfully added,    * and will be returned in the response; policy states will be set to    * DISABLED automatically.    *    * @param policies The user defined ec policy list to add.    * @return Return the response list of adding operations.    * @throws IOException    */
DECL|method|addErasureCodingPolicies ( ErasureCodingPolicy[] policies)
specifier|public
name|AddErasureCodingPolicyResponse
index|[]
name|addErasureCodingPolicies
parameter_list|(
name|ErasureCodingPolicy
index|[]
name|policies
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|addErasureCodingPolicies
argument_list|(
name|policies
argument_list|)
return|;
block|}
comment|/**    * Remove erasure coding policy.    *    * @param ecPolicyName The name of the policy to be removed.    * @throws IOException    */
DECL|method|removeErasureCodingPolicy (String ecPolicyName)
specifier|public
name|void
name|removeErasureCodingPolicy
parameter_list|(
name|String
name|ecPolicyName
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|removeErasureCodingPolicy
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Enable erasure coding policy.    *    * @param ecPolicyName The name of the policy to be enabled.    * @throws IOException    */
DECL|method|enableErasureCodingPolicy (String ecPolicyName)
specifier|public
name|void
name|enableErasureCodingPolicy
parameter_list|(
name|String
name|ecPolicyName
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|enableErasureCodingPolicy
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Disable erasure coding policy.    *    * @param ecPolicyName The name of the policy to be disabled.    * @throws IOException    */
DECL|method|disableErasureCodingPolicy (String ecPolicyName)
specifier|public
name|void
name|disableErasureCodingPolicy
parameter_list|(
name|String
name|ecPolicyName
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|disableErasureCodingPolicy
argument_list|(
name|ecPolicyName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns a RemoteIterator which can be used to list all open files    * currently managed by the NameNode. For large numbers of open files,    * iterator will fetch the list in batches of configured size.    *<p>    * Since the list is fetched in batches, it does not represent a    * consistent snapshot of the all open files.    *<p>    * This method can only be called by HDFS superusers.    */
annotation|@
name|Deprecated
DECL|method|listOpenFiles ()
specifier|public
name|RemoteIterator
argument_list|<
name|OpenFileEntry
argument_list|>
name|listOpenFiles
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listOpenFiles
argument_list|()
return|;
block|}
annotation|@
name|Deprecated
DECL|method|listOpenFiles ( EnumSet<OpenFilesType> openFilesTypes)
specifier|public
name|RemoteIterator
argument_list|<
name|OpenFileEntry
argument_list|>
name|listOpenFiles
parameter_list|(
name|EnumSet
argument_list|<
name|OpenFilesType
argument_list|>
name|openFilesTypes
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listOpenFiles
argument_list|(
name|openFilesTypes
argument_list|)
return|;
block|}
DECL|method|listOpenFiles ( EnumSet<OpenFilesType> openFilesTypes, String path)
specifier|public
name|RemoteIterator
argument_list|<
name|OpenFileEntry
argument_list|>
name|listOpenFiles
parameter_list|(
name|EnumSet
argument_list|<
name|OpenFilesType
argument_list|>
name|openFilesTypes
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|listOpenFiles
argument_list|(
name|openFilesTypes
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
end_class

end_unit

