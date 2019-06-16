begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.s3guard
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|s3guard
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Collection
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
name|s3a
operator|.
name|Retries
operator|.
name|RetryTranslated
import|;
end_import

begin_comment
comment|/**  * {@code MetadataStore} defines the set of operations that any metadata store  * implementation must provide.  Note that all {@link Path} objects provided  * to methods must be absolute, not relative paths.  * Implementations must implement any retries needed internally, such that  * transient errors are generally recovered from without throwing exceptions  * from this API.  */
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
DECL|interface|MetadataStore
specifier|public
interface|interface
name|MetadataStore
extends|extends
name|Closeable
block|{
comment|/**    * Performs one-time initialization of the metadata store.    *    * @param fs {@code FileSystem} associated with the MetadataStore    * @throws IOException if there is an error    */
DECL|method|initialize (FileSystem fs)
name|void
name|initialize
parameter_list|(
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Performs one-time initialization of the metadata store via configuration.    * @see #initialize(FileSystem)    * @param conf Configuration.    * @throws IOException if there is an error    */
DECL|method|initialize (Configuration conf)
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes exactly one path, leaving a tombstone to prevent lingering,    * inconsistent copies of it from being listed.    *    * Deleting an entry with a tombstone needs a    * {@link org.apache.hadoop.fs.s3a.s3guard.S3Guard.TtlTimeProvider} because    * the lastUpdated field of the record has to be updated to<pre>now</pre>.    *    * @param path the path to delete    * @param ttlTimeProvider the time provider to set last_updated. Must not    *                        be null.    * @throws IOException if there is an error    */
DECL|method|delete (Path path, ITtlTimeProvider ttlTimeProvider)
name|void
name|delete
parameter_list|(
name|Path
name|path
parameter_list|,
name|ITtlTimeProvider
name|ttlTimeProvider
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Removes the record of exactly one path.  Does not leave a tombstone (see    * {@link MetadataStore#delete(Path, ITtlTimeProvider)}. It is currently    * intended for testing only, and a need to use it as part of normal    * FileSystem usage is not anticipated.    *    * @param path the path to delete    * @throws IOException if there is an error    */
annotation|@
name|VisibleForTesting
DECL|method|forgetMetadata (Path path)
name|void
name|forgetMetadata
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes the entire sub-tree rooted at the given path, leaving tombstones    * to prevent lingering, inconsistent copies of it from being listed.    *    * In addition to affecting future calls to {@link #get(Path)},    * implementations must also update any stored {@code DirListingMetadata}    * objects which track the parent of this file.    *    * Deleting a subtree with a tombstone needs a    * {@link org.apache.hadoop.fs.s3a.s3guard.S3Guard.TtlTimeProvider} because    * the lastUpdated field of all records have to be updated to<pre>now</pre>.    *    * @param path the root of the sub-tree to delete    * @param ttlTimeProvider the time provider to set last_updated. Must not    *                        be null.    * @throws IOException if there is an error    */
DECL|method|deleteSubtree (Path path, ITtlTimeProvider ttlTimeProvider)
name|void
name|deleteSubtree
parameter_list|(
name|Path
name|path
parameter_list|,
name|ITtlTimeProvider
name|ttlTimeProvider
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets metadata for a path.    *    * @param path the path to get    * @return metadata for {@code path}, {@code null} if not found    * @throws IOException if there is an error    */
DECL|method|get (Path path)
name|PathMetadata
name|get
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets metadata for a path.  Alternate method that includes a hint    * whether or not the MetadataStore should do work to compute the value for    * {@link PathMetadata#isEmptyDirectory()}.  Since determining emptiness    * may be an expensive operation, this can save wasted work.    *    * @param path the path to get    * @param wantEmptyDirectoryFlag Set to true to give a hint to the    *   MetadataStore that it should try to compute the empty directory flag.    * @return metadata for {@code path}, {@code null} if not found    * @throws IOException if there is an error    */
DECL|method|get (Path path, boolean wantEmptyDirectoryFlag)
name|PathMetadata
name|get
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|wantEmptyDirectoryFlag
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists metadata for all direct children of a path.    *    * @param path the path to list    * @return metadata for all direct children of {@code path} which are being    *     tracked by the MetadataStore, or {@code null} if the path was not found    *     in the MetadataStore.    * @throws IOException if there is an error    */
DECL|method|listChildren (Path path)
name|DirListingMetadata
name|listChildren
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the effects of a {@link FileSystem#rename(Path, Path)} in the    * MetadataStore.  Clients provide explicit enumeration of the affected    * paths (recursively), before and after the rename.    *    * This operation is not atomic, unless specific implementations claim    * otherwise.    *    * On the need to provide an enumeration of directory trees instead of just    * source and destination paths:    * Since a MetadataStore does not have to track all metadata for the    * underlying storage system, and a new MetadataStore may be created on an    * existing underlying filesystem, this move() may be the first time the    * MetadataStore sees the affected paths.  Therefore, simply providing src    * and destination paths may not be enough to record the deletions (under    * src path) and creations (at destination) that are happening during the    * rename().    *    * @param pathsToDelete Collection of all paths that were removed from the    *                      source directory tree of the move.    * @param pathsToCreate Collection of all PathMetadata for the new paths    *                      that were created at the destination of the rename    *                      ().    * @param ttlTimeProvider the time provider to set last_updated. Must not    *                        be null.    * @throws IOException if there is an error    */
DECL|method|move (Collection<Path> pathsToDelete, Collection<PathMetadata> pathsToCreate, ITtlTimeProvider ttlTimeProvider)
name|void
name|move
parameter_list|(
name|Collection
argument_list|<
name|Path
argument_list|>
name|pathsToDelete
parameter_list|,
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|pathsToCreate
parameter_list|,
name|ITtlTimeProvider
name|ttlTimeProvider
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Saves metadata for exactly one path.    *    * Implementations may pre-create all the path's ancestors automatically.    * Implementations must update any {@code DirListingMetadata} objects which    * track the immediate parent of this file.    *    * @param meta the metadata to save    * @throws IOException if there is an error    */
annotation|@
name|RetryTranslated
DECL|method|put (PathMetadata meta)
name|void
name|put
parameter_list|(
name|PathMetadata
name|meta
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Saves metadata for any number of paths.    *    * Semantics are otherwise the same as single-path puts.    *    * @param metas the metadata to save    * @throws IOException if there is an error    */
DECL|method|put (Collection<PathMetadata> metas)
name|void
name|put
parameter_list|(
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|metas
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Save directory listing metadata. Callers may save a partial directory    * listing for a given path, or may store a complete and authoritative copy    * of the directory listing.  {@code MetadataStore} implementations may    * subsequently keep track of all modifications to the directory contents at    * this path, and return authoritative results from subsequent calls to    * {@link #listChildren(Path)}. See {@link DirListingMetadata}.    *    * Any authoritative results returned are only authoritative for the scope    * of the {@code MetadataStore}:  A per-process {@code MetadataStore}, for    * example, would only show results visible to that process, potentially    * missing metadata updates (create, delete) made to the same path by    * another process.    *    * @param meta Directory listing metadata.    * @throws IOException if there is an error    */
DECL|method|put (DirListingMetadata meta)
name|void
name|put
parameter_list|(
name|DirListingMetadata
name|meta
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Destroy all resources associated with the metadata store.    *    * The destroyed resources can be DynamoDB tables, MySQL databases/tables, or    * HDFS directories. Any operations after calling this method may possibly    * fail.    *    * This operation is idempotent.    *    * @throws IOException if there is an error    */
DECL|method|destroy ()
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Prune method with two modes of operation:    *<ul>    *<li>    *    {@link PruneMode#ALL_BY_MODTIME}    *    Clear any metadata older than a specified mod_time from the store.    *    Note that this modification time is the S3 modification time from the    *    object's metadata - from the object store.    *    Implementations MUST clear file metadata, and MAY clear directory    *    metadata (s3a itself does not track modification time for directories).    *    Implementations may also choose to throw UnsupportedOperationException    *    instead. Note that modification times must be in UTC, as returned by    *    System.currentTimeMillis at the time of modification.    *</li>    *</ul>    *    *<ul>    *<li>    *    {@link PruneMode#TOMBSTONES_BY_LASTUPDATED}    *    Clear any tombstone updated earlier than a specified time from the    *    store. Note that this last_updated is the time when the metadata    *    entry was last updated and maintained by the metadata store.    *    Implementations MUST clear file metadata, and MAY clear directory    *    metadata (s3a itself does not track modification time for directories).    *    Implementations may also choose to throw UnsupportedOperationException    *    instead. Note that last_updated must be in UTC, as returned by    *    System.currentTimeMillis at the time of modification.    *</li>    *</ul>    *    * @param pruneMode    * @param cutoff Oldest time to allow (UTC)    * @throws IOException if there is an error    * @throws UnsupportedOperationException if not implemented    */
DECL|method|prune (PruneMode pruneMode, long cutoff)
name|void
name|prune
parameter_list|(
name|PruneMode
name|pruneMode
parameter_list|,
name|long
name|cutoff
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
function_decl|;
comment|/**    * Same as {@link MetadataStore#prune(PruneMode, long)}, but with an    * additional keyPrefix parameter to filter the pruned keys with a prefix.    *    * @param pruneMode    * @param cutoff Oldest time to allow (UTC)    * @param keyPrefix The prefix for the keys that should be removed    * @throws IOException if there is an error    * @throws UnsupportedOperationException if not implemented    */
DECL|method|prune (PruneMode pruneMode, long cutoff, String keyPrefix)
name|void
name|prune
parameter_list|(
name|PruneMode
name|pruneMode
parameter_list|,
name|long
name|cutoff
parameter_list|,
name|String
name|keyPrefix
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnsupportedOperationException
function_decl|;
comment|/**    * Get any diagnostics information from a store, as a list of (key, value)    * tuples for display. Arbitrary values; no guarantee of stability.    * These are for debugging and testing only.    * @return a map of strings.    * @throws IOException if there is an error    */
DECL|method|getDiagnostics ()
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getDiagnostics
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Tune/update parameters for an existing table.    * @param parameters map of params to change.    * @throws IOException if there is an error    */
DECL|method|updateParameters (Map<String, String> parameters)
name|void
name|updateParameters
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Modes of operation for prune.    * For details see {@link MetadataStore#prune(PruneMode, long)}    */
DECL|enum|PruneMode
enum|enum
name|PruneMode
block|{
DECL|enumConstant|ALL_BY_MODTIME
name|ALL_BY_MODTIME
block|,
DECL|enumConstant|TOMBSTONES_BY_LASTUPDATED
name|TOMBSTONES_BY_LASTUPDATED
block|}
block|}
end_interface

end_unit

