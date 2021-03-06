begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
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
name|impl
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|ArrayList
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
name|concurrent
operator|.
name|CompletableFuture
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|DeleteObjectsRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|DeleteObjectsResult
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
name|util
operator|.
name|concurrent
operator|.
name|ListeningExecutorService
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
name|PathIsNotEmptyDirectoryException
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
name|s3a
operator|.
name|Invoker
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
name|S3AFileStatus
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
name|S3ALocatedFileStatus
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
name|Tristate
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
name|s3guard
operator|.
name|BulkOperationState
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
name|s3guard
operator|.
name|MetadataStore
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
name|s3guard
operator|.
name|S3Guard
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
name|DurationInfo
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|s3a
operator|.
name|impl
operator|.
name|CallableSupplier
operator|.
name|submit
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
name|s3a
operator|.
name|impl
operator|.
name|CallableSupplier
operator|.
name|waitForCompletion
import|;
end_import

begin_comment
comment|/**  * Implementation of the delete() operation.  *<p>  * How S3Guard/Store inconsistency is handled:  *<ol>  *<li>  *     The list operation does not ask for tombstone markers; objects  *     under tombstones will be found and deleted.  *     The {@code extraFilesDeleted} counter will be incremented here.  *</li>  *<li>  *     That may result in recently deleted files being found and  *     duplicate delete requests issued. This is mostly harmless.  *</li>  *<li>  *     If a path is considered authoritative on the client, so only S3Guard  *     is used for listings, we wrap up the delete with a scan of raw S3.  *     This will find and eliminate OOB additions.  *</li>  *<li>  *     Exception 1: simple directory markers of the form PATH + "/".  *     These are treated as a signal that there are no children; no  *     listing is made.  *</li>  *<li>  *     Exception 2: delete(path, true) where path has a tombstone in S3Guard.  *     Here the delete is downgraded to a no-op even before this operation  *     is created. Thus: no listings of S3.  *</li>  *</ol>  * If this class is logged at debug, requests will be audited:  * the response to a bulk delete call will be reviewed to see if there  * were fewer files deleted than requested; that will be printed  * at WARN level. This is independent of handling rejected delete  * requests which raise exceptions -those are processed lower down.  *<p>  * Performance tuning:  *<p>  * The operation to POST a delete request (or issue many individual  * DELETE calls) then update the S3Guard table is done in an async  * operation so that it can overlap with the LIST calls for data.  * However, only one single operation is queued at a time.  *<p>  * Executing more than one batch delete is possible, it just  * adds complexity in terms of error handling as well as in  * the datastructures used to track outstanding operations.  * If this is done, then it may be good to experiment with different  * page sizes. The default value is  * {@link InternalConstants#MAX_ENTRIES_TO_DELETE}, the maximum a single  * POST permits.  *<p>  * 1. Smaller pages executed in parallel may have different  * performance characteristics when deleting very large directories,  * because it will be the DynamoDB calls which will come to dominate.  * Any exploration of options here MUST be done with performance  * measurements taken from test runs in EC2 against local DDB and S3 stores,  * so as to ensure network latencies do not skew the results.  *<p>  * 2. Note that as the DDB thread/connection pools will be shared across  * all active delete operations, speedups will be minimal unless  * those pools are large enough to cope the extra load.  *<p>  * There are also some opportunities to explore in  * {@code DynamoDBMetadataStore} with batching delete requests  * in the DDB APIs.  */
end_comment

begin_class
DECL|class|DeleteOperation
specifier|public
class|class
name|DeleteOperation
extends|extends
name|ExecutingStoreOperation
argument_list|<
name|Boolean
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DeleteOperation
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Pre-fetched source status.    */
DECL|field|status
specifier|private
specifier|final
name|S3AFileStatus
name|status
decl_stmt|;
comment|/**    * Recursive delete?    */
DECL|field|recursive
specifier|private
specifier|final
name|boolean
name|recursive
decl_stmt|;
comment|/**    * Callback provider.    */
DECL|field|callbacks
specifier|private
specifier|final
name|OperationCallbacks
name|callbacks
decl_stmt|;
comment|/**    * Number of entries in a page.    */
DECL|field|pageSize
specifier|private
specifier|final
name|int
name|pageSize
decl_stmt|;
comment|/**    * Metastore -never null but may be the NullMetadataStore.    */
DECL|field|metadataStore
specifier|private
specifier|final
name|MetadataStore
name|metadataStore
decl_stmt|;
comment|/**    * Executor for async operations.    */
DECL|field|executor
specifier|private
specifier|final
name|ListeningExecutorService
name|executor
decl_stmt|;
comment|/**    * List of keys built up for the next delete batch.    */
DECL|field|keys
specifier|private
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|keys
decl_stmt|;
comment|/**    * List of paths built up for deletion.    */
DECL|field|paths
specifier|private
name|List
argument_list|<
name|Path
argument_list|>
name|paths
decl_stmt|;
comment|/**    * The single async delete operation, or null.    */
DECL|field|deleteFuture
specifier|private
name|CompletableFuture
argument_list|<
name|Void
argument_list|>
name|deleteFuture
decl_stmt|;
comment|/**    * Bulk Operation state if this is a bulk operation.    */
DECL|field|operationState
specifier|private
name|BulkOperationState
name|operationState
decl_stmt|;
comment|/**    * Counter of deleted files.    */
DECL|field|filesDeleted
specifier|private
name|long
name|filesDeleted
decl_stmt|;
comment|/**    * Counter of files found in the S3 Store during a raw scan of the store    * after the previous listing was in auth-mode.    */
DECL|field|extraFilesDeleted
specifier|private
name|long
name|extraFilesDeleted
decl_stmt|;
comment|/**    * Constructor.    * @param context store context    * @param status  pre-fetched source status    * @param recursive recursive delete?    * @param callbacks callback provider    * @param pageSize number of entries in a page    */
DECL|method|DeleteOperation (final StoreContext context, final S3AFileStatus status, final boolean recursive, final OperationCallbacks callbacks, final int pageSize)
specifier|public
name|DeleteOperation
parameter_list|(
specifier|final
name|StoreContext
name|context
parameter_list|,
specifier|final
name|S3AFileStatus
name|status
parameter_list|,
specifier|final
name|boolean
name|recursive
parameter_list|,
specifier|final
name|OperationCallbacks
name|callbacks
parameter_list|,
specifier|final
name|int
name|pageSize
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|recursive
operator|=
name|recursive
expr_stmt|;
name|this
operator|.
name|callbacks
operator|=
name|callbacks
expr_stmt|;
name|checkArgument
argument_list|(
name|pageSize
operator|>
literal|0
operator|&&
name|pageSize
operator|<=
name|InternalConstants
operator|.
name|MAX_ENTRIES_TO_DELETE
argument_list|,
literal|"page size out of range: %d"
argument_list|,
name|pageSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|pageSize
operator|=
name|pageSize
expr_stmt|;
name|metadataStore
operator|=
name|context
operator|.
name|getMetadataStore
argument_list|()
expr_stmt|;
name|executor
operator|=
name|context
operator|.
name|createThrottledExecutor
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|getFilesDeleted ()
specifier|public
name|long
name|getFilesDeleted
parameter_list|()
block|{
return|return
name|filesDeleted
return|;
block|}
DECL|method|getExtraFilesDeleted ()
specifier|public
name|long
name|getExtraFilesDeleted
parameter_list|()
block|{
return|return
name|extraFilesDeleted
return|;
block|}
comment|/**    * Delete a file or directory tree.    *<p>    * This call does not create any fake parent directory; that is    * left to the caller.    * The actual delete call is done in a separate thread.    * Only one delete at a time is submitted, however, to reduce the    * complexity of recovering from failures.    *<p>    * The DynamoDB store deletes paths in parallel itself, so that    * potentially slow part of the process is somewhat speeded up.    * The extra parallelization here is to list files from the store/DDB while    * that delete operation is in progress.    *    * @return true, except in the corner cases of root directory deletion    * @throws PathIsNotEmptyDirectoryException if the path is a dir and this    * is not a recursive delete.    * @throws IOException list failures or an inability to delete a file.    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|execute ()
specifier|public
name|Boolean
name|execute
parameter_list|()
throws|throws
name|IOException
block|{
name|executeOnlyOnce
argument_list|()
expr_stmt|;
name|StoreContext
name|context
init|=
name|getStoreContext
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|status
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Delete path {} - recursive {}"
argument_list|,
name|path
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Type = {}"
argument_list|,
name|status
operator|.
name|isFile
argument_list|()
condition|?
literal|"File"
else|:
operator|(
name|status
operator|.
name|isEmptyDirectory
argument_list|()
operator|==
name|Tristate
operator|.
name|TRUE
condition|?
literal|"Empty Directory"
else|:
literal|"Directory"
operator|)
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|context
operator|.
name|pathToKey
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"delete: Path is a directory: {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|checkArgument
argument_list|(
name|status
operator|.
name|isEmptyDirectory
argument_list|()
operator|!=
name|Tristate
operator|.
name|UNKNOWN
argument_list|,
literal|"File status must have directory emptiness computed"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|key
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|key
operator|=
name|key
operator|+
literal|"/"
expr_stmt|;
block|}
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"S3A: Cannot delete the root directory."
operator|+
literal|" Path: {}. Recursive: {}"
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|recursive
operator|&&
name|status
operator|.
name|isEmptyDirectory
argument_list|()
operator|==
name|Tristate
operator|.
name|FALSE
condition|)
block|{
throw|throw
operator|new
name|PathIsNotEmptyDirectoryException
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|status
operator|.
name|isEmptyDirectory
argument_list|()
operator|==
name|Tristate
operator|.
name|TRUE
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"deleting empty directory {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|deleteObjectAtPath
argument_list|(
name|path
argument_list|,
name|key
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|deleteDirectoryTree
argument_list|(
name|path
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// simple file.
name|LOG
operator|.
name|debug
argument_list|(
literal|"deleting simple file {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|deleteObjectAtPath
argument_list|(
name|path
argument_list|,
name|key
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleted {} files"
argument_list|,
name|filesDeleted
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Delete a directory tree.    *<p>    * This is done by asking the filesystem for a list of all objects under    * the directory path, without using any S3Guard tombstone markers to hide    * objects which may be returned in S3 listings but which are considered    * deleted.    *<p>    * Once the first {@link #pageSize} worth of objects has been listed, a batch    * delete is queued for execution in a separate thread; subsequent batches    * block waiting for the first call to complete or fail before again,    * being deleted in the separate thread.    *<p>    * After all listed objects are queued for deletion,    * if the path is considered authoritative in the client, a final scan    * of S3<i>without S3Guard</i> is executed, so as to find and delete    * any out-of-band objects in the tree.    * @param path directory path    * @param dirKey directory key    * @throws IOException failure    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|deleteDirectoryTree (final Path path, final String dirKey)
specifier|protected
name|void
name|deleteDirectoryTree
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|String
name|dirKey
parameter_list|)
throws|throws
name|IOException
block|{
comment|// create an operation state so that the store can manage the bulk
comment|// operation if it needs to
name|operationState
operator|=
name|S3Guard
operator|.
name|initiateBulkWrite
argument_list|(
name|metadataStore
argument_list|,
name|BulkOperationState
operator|.
name|OperationType
operator|.
name|Delete
argument_list|,
name|path
argument_list|)
expr_stmt|;
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|false
argument_list|,
literal|"deleting %s"
argument_list|,
name|dirKey
argument_list|)
init|)
block|{
comment|// init the lists of keys and paths to delete
name|resetDeleteList
argument_list|()
expr_stmt|;
name|deleteFuture
operator|=
literal|null
expr_stmt|;
comment|// list files including any under tombstones through S3Guard
name|LOG
operator|.
name|debug
argument_list|(
literal|"Getting objects for directory prefix {} to delete"
argument_list|,
name|dirKey
argument_list|)
expr_stmt|;
specifier|final
name|RemoteIterator
argument_list|<
name|S3ALocatedFileStatus
argument_list|>
name|locatedFiles
init|=
name|callbacks
operator|.
name|listFilesAndEmptyDirectories
argument_list|(
name|path
argument_list|,
name|status
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// iterate through and delete. The next() call will block when a new S3
comment|// page is required; this any active delete submitted to the executor
comment|// will run in parallel with this.
while|while
condition|(
name|locatedFiles
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// get the next entry in the listing.
name|S3AFileStatus
name|child
init|=
name|locatedFiles
operator|.
name|next
argument_list|()
operator|.
name|toS3AFileStatus
argument_list|()
decl_stmt|;
name|queueForDeletion
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleting final batch of listed files"
argument_list|)
expr_stmt|;
name|submitNextBatch
argument_list|()
expr_stmt|;
name|maybeAwaitCompletion
argument_list|(
name|deleteFuture
argument_list|)
expr_stmt|;
comment|// if s3guard is authoritative we follow up with a bulk list and
comment|// delete process on S3 this helps recover from any situation where S3
comment|// and S3Guard have become inconsistent.
comment|// This is only needed for auth paths; by performing the previous listing
comment|// without tombstone filtering, any files returned by the non-auth
comment|// S3 list which were hidden under tombstones will have been found
comment|// and deleted.
if|if
condition|(
name|callbacks
operator|.
name|allowAuthoritative
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Path is authoritatively guarded;"
operator|+
literal|" listing files on S3 for completeness"
argument_list|)
expr_stmt|;
comment|// let the ongoing delete finish to avoid duplicates
specifier|final
name|RemoteIterator
argument_list|<
name|S3AFileStatus
argument_list|>
name|objects
init|=
name|callbacks
operator|.
name|listObjects
argument_list|(
name|path
argument_list|,
name|dirKey
argument_list|)
decl_stmt|;
comment|// iterate through and delete. The next() call will block when a new S3
comment|// page is required; this any active delete submitted to the executor
comment|// will run in parallel with this.
while|while
condition|(
name|objects
operator|.
name|hasNext
argument_list|()
condition|)
block|{
comment|// get the next entry in the listing.
name|extraFilesDeleted
operator|++
expr_stmt|;
name|queueForDeletion
argument_list|(
name|deletionKey
argument_list|(
name|objects
operator|.
name|next
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|extraFilesDeleted
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Raw S3 Scan found {} extra file(s) to delete"
argument_list|,
name|extraFilesDeleted
argument_list|)
expr_stmt|;
comment|// there is no more data:
comment|// await any ongoing operation
name|submitNextBatch
argument_list|()
expr_stmt|;
name|maybeAwaitCompletion
argument_list|(
name|deleteFuture
argument_list|)
expr_stmt|;
block|}
block|}
comment|// final cleanup of the directory tree in the metastore, including the
comment|// directory entry itself.
try|try
init|(
name|DurationInfo
name|ignored2
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|false
argument_list|,
literal|"Delete metastore"
argument_list|)
init|)
block|{
name|metadataStore
operator|.
name|deleteSubtree
argument_list|(
name|path
argument_list|,
name|operationState
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|operationState
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"Delete \"{}\" completed; deleted {} objects"
argument_list|,
name|path
argument_list|,
name|filesDeleted
argument_list|)
expr_stmt|;
block|}
comment|/**    * Build an S3 key for a delete request,    * possibly adding a "/" if it represents directory and it does    * not have a trailing slash already.    * @param stat status to build the key from    * @return a key for a delete request    */
DECL|method|deletionKey (final S3AFileStatus stat)
specifier|private
name|String
name|deletionKey
parameter_list|(
specifier|final
name|S3AFileStatus
name|stat
parameter_list|)
block|{
return|return
name|getStoreContext
argument_list|()
operator|.
name|fullKey
argument_list|(
name|stat
argument_list|)
return|;
block|}
comment|/**    * Queue for deletion.    * @param stat status to queue    * @throws IOException failure of the previous batch of deletions.    */
DECL|method|queueForDeletion ( final S3AFileStatus stat)
specifier|private
name|void
name|queueForDeletion
parameter_list|(
specifier|final
name|S3AFileStatus
name|stat
parameter_list|)
throws|throws
name|IOException
block|{
name|queueForDeletion
argument_list|(
name|deletionKey
argument_list|(
name|stat
argument_list|)
argument_list|,
name|stat
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Queue keys for deletion.    * Once a page of keys are ready to delete this    * call is submitted to the executor, after waiting for the previous run to    * complete.    *    * @param key key to delete    * @param deletePath nullable path of the key    * @throws IOException failure of the previous batch of deletions.    */
DECL|method|queueForDeletion (final String key, @Nullable final Path deletePath)
specifier|private
name|void
name|queueForDeletion
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Path
name|deletePath
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Adding object to delete: \"{}\""
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|keys
operator|.
name|add
argument_list|(
operator|new
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|deletePath
operator|!=
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|deletePath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keys
operator|.
name|size
argument_list|()
operator|==
name|pageSize
condition|)
block|{
name|submitNextBatch
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Wait for the previous batch to finish then submit this page.    * The lists of keys and pages are reset here.    *    * @throws IOException failure of the previous batch of deletions.    */
DECL|method|submitNextBatch ()
specifier|private
name|void
name|submitNextBatch
parameter_list|()
throws|throws
name|IOException
block|{
comment|// delete a single page of keys and the metadata.
comment|// block for any previous batch.
name|maybeAwaitCompletion
argument_list|(
name|deleteFuture
argument_list|)
expr_stmt|;
comment|// delete the current page of keys and paths
name|deleteFuture
operator|=
name|submitDelete
argument_list|(
name|keys
argument_list|,
name|paths
argument_list|)
expr_stmt|;
comment|// reset the references so a new list can be built up.
name|resetDeleteList
argument_list|()
expr_stmt|;
block|}
comment|/**    * Reset the lists of keys and paths so that a new batch of    * entries can built up.    */
DECL|method|resetDeleteList ()
specifier|private
name|void
name|resetDeleteList
parameter_list|()
block|{
name|keys
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pageSize
argument_list|)
expr_stmt|;
name|paths
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|pageSize
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete a file or directory marker.    * @param path path    * @param key key    * @param isFile is this a file?    * @throws IOException failure    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|deleteObjectAtPath ( final Path path, final String key, final boolean isFile)
specifier|private
name|void
name|deleteObjectAtPath
parameter_list|(
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|boolean
name|isFile
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"delete: {} {}"
argument_list|,
operator|(
name|isFile
condition|?
literal|"file"
else|:
literal|"dir marker"
operator|)
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|filesDeleted
operator|++
expr_stmt|;
name|callbacks
operator|.
name|deleteObjectAtPath
argument_list|(
name|path
argument_list|,
name|key
argument_list|,
name|isFile
argument_list|,
name|operationState
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete a single page of keys and optionally the metadata.    * For a large page, it is the metadata size which dominates.    * Its possible to invoke this with empty lists of keys or paths.    * If both lists are empty no work is submitted and null is returned.    *    * @param keyList keys to delete.    * @param pathList paths to update the metastore with.    * @return the submitted future or null    */
DECL|method|submitDelete ( final List<DeleteObjectsRequest.KeyVersion> keyList, final List<Path> pathList)
specifier|private
name|CompletableFuture
argument_list|<
name|Void
argument_list|>
name|submitDelete
parameter_list|(
specifier|final
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|keyList
parameter_list|,
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|pathList
parameter_list|)
block|{
if|if
condition|(
name|keyList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|pathList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|filesDeleted
operator|+=
name|keyList
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|submit
argument_list|(
name|executor
argument_list|,
parameter_list|()
lambda|->
block|{
name|asyncDeleteAction
argument_list|(
name|operationState
argument_list|,
name|keyList
argument_list|,
name|pathList
argument_list|,
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
return|;
block|}
comment|/**    * The action called in the asynchronous thread to delete    * the keys from S3 and paths from S3Guard.    *    * @param state ongoing operation state    * @param keyList keys to delete.    * @param pathList paths to update the metastore with.    * @param auditDeletedKeys should the results be audited and undeleted    * entries logged?    * @throws IOException failure    */
annotation|@
name|Retries
operator|.
name|RetryTranslated
DECL|method|asyncDeleteAction ( final BulkOperationState state, final List<DeleteObjectsRequest.KeyVersion> keyList, final List<Path> pathList, final boolean auditDeletedKeys)
specifier|private
name|void
name|asyncDeleteAction
parameter_list|(
specifier|final
name|BulkOperationState
name|state
parameter_list|,
specifier|final
name|List
argument_list|<
name|DeleteObjectsRequest
operator|.
name|KeyVersion
argument_list|>
name|keyList
parameter_list|,
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|pathList
parameter_list|,
specifier|final
name|boolean
name|auditDeletedKeys
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|false
argument_list|,
literal|"Delete page of keys"
argument_list|)
init|)
block|{
name|DeleteObjectsResult
name|result
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Path
argument_list|>
name|undeletedObjects
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|keyList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|Invoker
operator|.
name|once
argument_list|(
literal|"Remove S3 Keys"
argument_list|,
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|callbacks
operator|.
name|removeKeys
argument_list|(
name|keyList
argument_list|,
literal|false
argument_list|,
name|undeletedObjects
argument_list|,
name|state
argument_list|,
operator|!
name|auditDeletedKeys
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|pathList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|metadataStore
operator|.
name|deletePaths
argument_list|(
name|pathList
argument_list|,
name|state
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|auditDeletedKeys
operator|&&
name|result
operator|!=
literal|null
condition|)
block|{
comment|// audit the deleted keys
name|List
argument_list|<
name|DeleteObjectsResult
operator|.
name|DeletedObject
argument_list|>
name|deletedObjects
init|=
name|result
operator|.
name|getDeletedObjects
argument_list|()
decl_stmt|;
if|if
condition|(
name|deletedObjects
operator|.
name|size
argument_list|()
operator|!=
name|keyList
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// size mismatch
name|LOG
operator|.
name|warn
argument_list|(
literal|"Size mismatch in deletion operation. "
operator|+
literal|"Expected count of deleted files: {}; "
operator|+
literal|"actual: {}"
argument_list|,
name|keyList
operator|.
name|size
argument_list|()
argument_list|,
name|deletedObjects
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// strip out the deleted keys
for|for
control|(
name|DeleteObjectsResult
operator|.
name|DeletedObject
name|del
range|:
name|deletedObjects
control|)
block|{
name|keyList
operator|.
name|removeIf
argument_list|(
name|kv
lambda|->
name|kv
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|del
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DeleteObjectsRequest
operator|.
name|KeyVersion
name|kv
range|:
name|keyList
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
name|kv
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**    * Block awaiting completion for any non-null future passed in;    * No-op if a null arg was supplied.    * @param future future    * @throws IOException if one of the called futures raised an IOE.    * @throws RuntimeException if one of the futures raised one.    */
DECL|method|maybeAwaitCompletion ( @ullable final CompletableFuture<Void> future)
specifier|private
name|void
name|maybeAwaitCompletion
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|CompletableFuture
argument_list|<
name|Void
argument_list|>
name|future
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|future
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|DurationInfo
name|ignored
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|false
argument_list|,
literal|"delete completion"
argument_list|)
init|)
block|{
name|waitForCompletion
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

