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
name|EnumSet
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
name|hdfs
operator|.
name|tools
operator|.
name|DFSAdmin
import|;
end_import

begin_comment
comment|/**  * The public API for performing administrative functions on HDFS. Those writing  * applications against HDFS should prefer this interface to directly accessing  * functionality in DistributedFileSystem or DFSClient.  *   * Note that this is distinct from the similarly-named {@link DFSAdmin}, which  * is a class that provides the functionality for the CLI `hdfs dfsadmin ...'  * commands.  */
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
comment|/**    * Create a new HdfsAdmin client.    *     * @param uri the unique URI of the HDFS file system to administer    * @param conf configuration    * @throws IOException in the event the file system could not be created    */
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
comment|/**    * Set the namespace quota (count of files, directories, and sym links) for a    * directory.    *     * @param src the path to set the quota for    * @param quota the value to set for the quota    * @throws IOException in the event of error    */
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
comment|/**    * Clear the namespace quota (count of files, directories and sym links) for a    * directory.    *     * @param src the path to clear the quota of    * @throws IOException in the event of error    */
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
comment|/**    * Set the storage space quota (size of files) for a directory. Note that    * directories and sym links do not occupy storage space.    *     * @param src the path to set the space quota of    * @param spaceQuota the value to set for the space quota    * @throws IOException in the event of error    */
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
comment|/**    * Clear the storage space quota (size of files) for a directory. Note that    * directories and sym links do not occupy storage space.    *     * @param src the path to clear the space quota of    * @throws IOException in the event of error    */
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
comment|/**    * Add a new CacheDirectiveInfo.    *     * @param info Information about a directive to add.    * @param flags {@link CacheFlag}s to use for this operation.    * @return the ID of the directive that was created.    * @throws IOException if the directive could not be added    */
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
comment|/**    * Modify a CacheDirective.    *     * @param info Information about the directive to modify. You must set the ID    *          to indicate which CacheDirective you want to modify.    * @param flags {@link CacheFlag}s to use for this operation.    * @throws IOException if the directive could not be modified    */
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
comment|/**    * Remove a CacheDirective.    *     * @param id identifier of the CacheDirectiveInfo to remove    * @throws IOException if the directive could not be removed    */
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
comment|/**    * List cache directives. Incrementally fetches results from the server.    *     * @param filter Filter parameters to use when listing the directives, null to    *               list all directives visible to us.    * @return A RemoteIterator which returns CacheDirectiveInfo objects.    */
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
comment|/**    * Add a cache pool.    *    * @param info    *          The request to add a cache pool.    * @throws IOException     *          If the request could not be completed.    */
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
comment|/**    * Modify an existing cache pool.    *    * @param info    *          The request to modify a cache pool.    * @throws IOException     *          If the request could not be completed.    */
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
comment|/**    * Remove a cache pool.    *    * @param poolName    *          Name of the cache pool to remove.    * @throws IOException     *          if the cache pool did not exist, or could not be removed.    */
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
comment|/**    * Create an encryption zone rooted at an empty existing directory, using the    * specified encryption key. An encryption zone has an associated encryption    * key used when reading and writing files within the zone.    *    * @param path    The path of the root of the encryption zone. Must refer to    *                an empty, existing directory.    * @param keyName Name of key available at the KeyProvider.    * @throws IOException            if there was a general IO exception    * @throws AccessControlException if the caller does not have access to path    * @throws FileNotFoundException  if the path does not exist    */
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
comment|/**    * Get the path of the encryption zone for a given file or directory.    *    * @param path The path to get the ez for.    *    * @return The EncryptionZone of the ez, or null if path is not in an ez.    * @throws IOException            if there was a general IO exception    * @throws AccessControlException if the caller does not have access to path    * @throws FileNotFoundException  if the path does not exist    */
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
throws|,
name|FileNotFoundException
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
comment|/**    * Returns a RemoteIterator which can be used to list the encryption zones    * in HDFS. For large numbers of encryption zones, the iterator will fetch    * the list of zones in a number of small batches.    *<p/>    * Since the list is fetched in batches, it does not represent a    * consistent snapshot of the entire list of encryption zones.    *<p/>    * This method can only be called by HDFS superusers.    */
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
comment|/**    * Exposes a stream of namesystem events. Only events occurring after the    * stream is created are available.    * See {@link org.apache.hadoop.hdfs.DFSInotifyEventInputStream}    * for information on stream usage.    * See {@link org.apache.hadoop.hdfs.inotify.Event}    * for information on the available events.    *<p/>    * Inotify users may want to tune the following HDFS parameters to    * ensure that enough extra HDFS edits are saved to support inotify clients    * that fall behind the current state of the namespace while reading events.    * The default parameter values should generally be reasonable. If edits are    * deleted before their corresponding events can be read, clients will see a    * {@link org.apache.hadoop.hdfs.inotify.MissingEventsException} on    * {@link org.apache.hadoop.hdfs.DFSInotifyEventInputStream} method calls.    *    * It should generally be sufficient to tune these parameters:    * dfs.namenode.num.extra.edits.retained    * dfs.namenode.max.extra.edits.segments.retained    *    * Parameters that affect the number of created segments and the number of    * edits that are considered necessary, i.e. do not count towards the    * dfs.namenode.num.extra.edits.retained quota):    * dfs.namenode.checkpoint.period    * dfs.namenode.checkpoint.txns    * dfs.namenode.num.checkpoints.retained    * dfs.ha.log-roll.period    *<p/>    * It is recommended that local journaling be configured    * (dfs.namenode.edits.dir) for inotify (in addition to a shared journal)    * so that edit transfers from the shared journal can be avoided.    *    * @throws IOException If there was an error obtaining the stream.    */
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
block|}
end_class

end_unit

