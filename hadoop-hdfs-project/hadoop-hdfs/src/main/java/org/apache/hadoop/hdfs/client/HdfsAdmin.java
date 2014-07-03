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
comment|/**    * Set the disk space quota (size of files) for a directory. Note that    * directories and sym links do not occupy disk space.    *     * @param src the path to set the space quota of    * @param spaceQuota the value to set for the space quota    * @throws IOException in the event of error    */
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
comment|/**    * Clear the disk space quota (size of files) for a directory. Note that    * directories and sym links do not occupy disk space.    *     * @param src the path to clear the space quota of    * @throws IOException in the event of error    */
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
comment|/**    * Create an encryption zone rooted at path using the optional encryption key    * id. An encryption zone is a portion of the HDFS file system hierarchy in    * which all files are encrypted with the same key, but possibly different    * key versions per file.    *<p/>    * Path must refer to an empty, existing directory. Otherwise an IOException    * will be thrown. keyId specifies the id of an encryption key in the    * KeyProvider that the Namenode has been configured to use. If keyId is    * null, then a key is generated in the KeyProvider using {@link    * java.util.UUID} to generate a key id.    *    * @param path The path of the root of the encryption zone.    *    * @param keyId An optional keyId in the KeyProvider. If null, then    * a key is generated.    *    * @throws IOException if there was a general IO exception    *    * @throws AccessControlException if the caller does not have access to path    *    * @throws FileNotFoundException if the path does not exist    */
DECL|method|createEncryptionZone (Path path, String keyId)
specifier|public
name|void
name|createEncryptionZone
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|keyId
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
name|keyId
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return a list of all {@EncryptionZone}s in the HDFS hierarchy which are    * visible to the caller. If the caller is the HDFS admin, then the returned    * EncryptionZone instances will have the key id field filled in. If the    * caller is not the HDFS admin, then the EncryptionZone instances will only    * have the path field filled in and only those zones that are visible to the    * user are returned.    *    * @throws IOException if there was a general IO exception    *    * @return List<EncryptionZone> the list of Encryption Zones that the caller has    * access to.    */
DECL|method|listEncryptionZones ()
specifier|public
name|List
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
block|}
end_class

end_unit

