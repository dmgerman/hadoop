begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|SdkBaseException
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
name|s3a
operator|.
name|S3ObjectAttributes
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
name|impl
operator|.
name|StoreContext
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
name|impl
operator|.
name|AbstractStoreOperation
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
name|checkNotNull
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
name|S3AUtils
operator|.
name|translateException
import|;
end_import

begin_comment
comment|/**  * A class which manages updating the metastore with the rename process  * as initiated in the S3AFilesystem rename.  *<p>  * Subclasses must provide an implementation and return it in  * {@code MetadataStore.initiateRenameOperation()}.  *<p>  * The {@link #operationState} field/constructor argument is an opaque state to  * be passed down to the metastore in its move operations; this allows the  * stores to manage ongoing state -while still being able to share  * rename tracker implementations.  *<p>  * This is to avoid performance problems wherein the progressive rename  * tracker causes the store to repeatedly create and write duplicate  * ancestor entries for every file added.  */
end_comment

begin_class
DECL|class|RenameTracker
specifier|public
specifier|abstract
class|class
name|RenameTracker
extends|extends
name|AbstractStoreOperation
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RenameTracker
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** source path. */
DECL|field|sourceRoot
specifier|private
specifier|final
name|Path
name|sourceRoot
decl_stmt|;
comment|/** destination path. */
DECL|field|dest
specifier|private
specifier|final
name|Path
name|dest
decl_stmt|;
comment|/**    * Track the duration of this operation.    */
DECL|field|durationInfo
specifier|private
specifier|final
name|DurationInfo
name|durationInfo
decl_stmt|;
comment|/**    * Generated name for strings.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * Any ongoing state supplied to the rename tracker    * which is to be passed in with each move operation.    * This must be closed at the end of the tracker's life.    */
DECL|field|operationState
specifier|private
specifier|final
name|BulkOperationState
name|operationState
decl_stmt|;
comment|/**    * The metadata store for this tracker.    * Always non-null.    *<p>    * This is passed in separate from the store context to guarantee    * that whichever store creates a tracker is explicitly bound to that    * instance.    */
DECL|field|metadataStore
specifier|private
specifier|final
name|MetadataStore
name|metadataStore
decl_stmt|;
comment|/**    * Constructor.    * @param name tracker name for logs.    * @param storeContext store context.    * @param metadataStore the store    * @param sourceRoot source path.    * @param dest destination path.    * @param operationState ongoing move state.    */
DECL|method|RenameTracker ( final String name, final StoreContext storeContext, final MetadataStore metadataStore, final Path sourceRoot, final Path dest, final BulkOperationState operationState)
specifier|protected
name|RenameTracker
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|StoreContext
name|storeContext
parameter_list|,
specifier|final
name|MetadataStore
name|metadataStore
parameter_list|,
specifier|final
name|Path
name|sourceRoot
parameter_list|,
specifier|final
name|Path
name|dest
parameter_list|,
specifier|final
name|BulkOperationState
name|operationState
parameter_list|)
block|{
name|super
argument_list|(
name|checkNotNull
argument_list|(
name|storeContext
argument_list|)
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|storeContext
operator|.
name|getUsername
argument_list|()
argument_list|,
literal|"No username"
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadataStore
operator|=
name|checkNotNull
argument_list|(
name|metadataStore
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceRoot
operator|=
name|checkNotNull
argument_list|(
name|sourceRoot
argument_list|)
expr_stmt|;
name|this
operator|.
name|dest
operator|=
name|checkNotNull
argument_list|(
name|dest
argument_list|)
expr_stmt|;
name|this
operator|.
name|operationState
operator|=
name|operationState
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|String
operator|.
name|format
argument_list|(
literal|"%s (%s, %s)"
argument_list|,
name|name
argument_list|,
name|sourceRoot
argument_list|,
name|dest
argument_list|)
expr_stmt|;
name|durationInfo
operator|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|false
argument_list|,
name|name
operator|+
literal|" (%s, %s)"
argument_list|,
name|sourceRoot
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|getSourceRoot ()
specifier|public
name|Path
name|getSourceRoot
parameter_list|()
block|{
return|return
name|sourceRoot
return|;
block|}
DECL|method|getDest ()
specifier|public
name|Path
name|getDest
parameter_list|()
block|{
return|return
name|dest
return|;
block|}
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|getStoreContext
argument_list|()
operator|.
name|getUsername
argument_list|()
return|;
block|}
DECL|method|getOperationState ()
specifier|public
name|BulkOperationState
name|getOperationState
parameter_list|()
block|{
return|return
name|operationState
return|;
block|}
comment|/**    * Get the metadata store.    * @return a non-null store.    */
DECL|method|getMetadataStore ()
specifier|protected
name|MetadataStore
name|getMetadataStore
parameter_list|()
block|{
return|return
name|metadataStore
return|;
block|}
comment|/**    * A file has been copied.    *    * @param childSource source of the file. This may actually be different    * from the path of the sourceAttributes. (HOW?)    * @param sourceAttributes status of source.    * @param destAttributes destination attributes    * @param destPath destination path.    * @param blockSize block size.    * @param addAncestors should ancestors be added?    * @throws IOException failure.    */
DECL|method|fileCopied ( Path childSource, S3ObjectAttributes sourceAttributes, S3ObjectAttributes destAttributes, Path destPath, long blockSize, boolean addAncestors)
specifier|public
specifier|abstract
name|void
name|fileCopied
parameter_list|(
name|Path
name|childSource
parameter_list|,
name|S3ObjectAttributes
name|sourceAttributes
parameter_list|,
name|S3ObjectAttributes
name|destAttributes
parameter_list|,
name|Path
name|destPath
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|boolean
name|addAncestors
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * A directory marker has been copied.    * @param sourcePath source path.    * @param destPath destination path.    * @param addAncestors should ancestors be added?    * @throws IOException failure.    */
DECL|method|directoryMarkerCopied ( Path sourcePath, Path destPath, boolean addAncestors)
specifier|public
name|void
name|directoryMarkerCopied
parameter_list|(
name|Path
name|sourcePath
parameter_list|,
name|Path
name|destPath
parameter_list|,
name|boolean
name|addAncestors
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * The delete failed.    *<p>    * By the time this is called, the metastore will already have    * been updated with the results of any partial delete failure,    * such that all files known to have been deleted will have been    * removed.    * @param e exception    * @param pathsToDelete paths which were to be deleted.    * @param undeletedObjects list of objects which were not deleted.    */
DECL|method|deleteFailed ( final Exception e, final List<Path> pathsToDelete, final List<Path> undeletedObjects)
specifier|public
name|IOException
name|deleteFailed
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|,
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|pathsToDelete
parameter_list|,
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|undeletedObjects
parameter_list|)
block|{
return|return
name|convertToIOException
argument_list|(
name|e
argument_list|)
return|;
block|}
comment|/**    * Top level directory move.    * This is invoked after all child entries have been copied    * @throws IOException on failure    */
DECL|method|moveSourceDirectory ()
specifier|public
name|void
name|moveSourceDirectory
parameter_list|()
throws|throws
name|IOException
block|{   }
comment|/**    * Note that source objects have been deleted.    * The metastore will already have been updated.    * @param paths path of objects deleted.    */
DECL|method|sourceObjectsDeleted ( final Collection<Path> paths)
specifier|public
name|void
name|sourceObjectsDeleted
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Path
argument_list|>
name|paths
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Complete the operation.    * @throws IOException failure.    */
DECL|method|completeRename ()
specifier|public
name|void
name|completeRename
parameter_list|()
throws|throws
name|IOException
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
name|noteRenameFinished
argument_list|()
expr_stmt|;
block|}
comment|/**    * Note that the rename has finished by closing the duration info;    * this will log the duration of the operation at debug.    */
DECL|method|noteRenameFinished ()
specifier|protected
name|void
name|noteRenameFinished
parameter_list|()
block|{
name|durationInfo
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Rename has failed.    *<p>    * The metastore now needs to be updated with its current state    * even though the operation is incomplete.    * Implementations MUST NOT throw exceptions here, as this is going to    * be invoked in an exception handler.    * catch and log or catch and return/wrap.    *<p>    * The base implementation returns the IOE passed in and translates    * any AWS exception into an IOE.    * @param ex the exception which caused the failure.    * This is either an IOException or and AWS exception    * @return an IOException to throw in an exception.    */
DECL|method|renameFailed (Exception ex)
specifier|public
name|IOException
name|renameFailed
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rename has failed"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|operationState
argument_list|)
expr_stmt|;
name|noteRenameFinished
argument_list|()
expr_stmt|;
return|return
name|convertToIOException
argument_list|(
name|ex
argument_list|)
return|;
block|}
comment|/**    * Convert a passed in exception (expected to be an IOE or AWS exception)    * into an IOException.    * @param ex exception caught    * @return the exception to throw in the failure handler.    */
DECL|method|convertToIOException (final Exception ex)
specifier|protected
name|IOException
name|convertToIOException
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|IOException
condition|)
block|{
return|return
operator|(
name|IOException
operator|)
name|ex
return|;
block|}
elseif|else
if|if
condition|(
name|ex
operator|instanceof
name|SdkBaseException
condition|)
block|{
return|return
name|translateException
argument_list|(
literal|"rename "
operator|+
name|sourceRoot
operator|+
literal|" to "
operator|+
name|dest
argument_list|,
name|sourceRoot
operator|.
name|toString
argument_list|()
argument_list|,
operator|(
name|SdkBaseException
operator|)
name|ex
argument_list|)
return|;
block|}
else|else
block|{
comment|// should never happen, but for completeness
return|return
operator|new
name|IOException
argument_list|(
name|ex
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

