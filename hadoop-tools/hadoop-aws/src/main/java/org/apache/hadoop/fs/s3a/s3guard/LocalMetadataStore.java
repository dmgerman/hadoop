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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|StringUtils
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
name|FileStatus
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
name|Tristate
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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

begin_comment
comment|/**  * This is a local, in-memory, implementation of MetadataStore.  * This is<i>not</i> a coherent cache across processes.  It is only  * locally-coherent.  *  * The purpose of this is for unit and integration testing.  * It could also be used to accelerate local-only operations where only one  * process is operating on a given object store, or multiple processes are  * accessing a read-only storage bucket.  *  * This MetadataStore does not enforce filesystem rules such as disallowing  * non-recursive removal of non-empty directories.  It is assumed the caller  * already has to perform these sorts of checks.  */
end_comment

begin_class
DECL|class|LocalMetadataStore
specifier|public
class|class
name|LocalMetadataStore
implements|implements
name|MetadataStore
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
name|MetadataStore
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// TODO HADOOP-13649: use time instead of capacity for eviction.
DECL|field|DEFAULT_MAX_RECORDS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_RECORDS
init|=
literal|128
decl_stmt|;
comment|/**    * Maximum number of records.    */
DECL|field|CONF_MAX_RECORDS
specifier|public
specifier|static
specifier|final
name|String
name|CONF_MAX_RECORDS
init|=
literal|"fs.metadatastore.local.max_records"
decl_stmt|;
comment|/** Contains directories and files. */
DECL|field|fileHash
specifier|private
name|LruHashMap
argument_list|<
name|Path
argument_list|,
name|PathMetadata
argument_list|>
name|fileHash
decl_stmt|;
comment|/** Contains directory listings. */
DECL|field|dirHash
specifier|private
name|LruHashMap
argument_list|<
name|Path
argument_list|,
name|DirListingMetadata
argument_list|>
name|dirHash
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
comment|/* Null iff this FS does not have an associated URI host. */
DECL|field|uriHost
specifier|private
name|String
name|uriHost
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (FileSystem fileSystem)
specifier|public
name|void
name|initialize
parameter_list|(
name|FileSystem
name|fileSystem
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|fileSystem
argument_list|)
expr_stmt|;
name|fs
operator|=
name|fileSystem
expr_stmt|;
name|URI
name|fsURI
init|=
name|fs
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|uriHost
operator|=
name|fsURI
operator|.
name|getHost
argument_list|()
expr_stmt|;
if|if
condition|(
name|uriHost
operator|!=
literal|null
operator|&&
name|uriHost
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|uriHost
operator|=
literal|null
expr_stmt|;
block|}
name|initialize
argument_list|(
name|fs
operator|.
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|maxRecords
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|CONF_MAX_RECORDS
argument_list|,
name|DEFAULT_MAX_RECORDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxRecords
operator|<
literal|4
condition|)
block|{
name|maxRecords
operator|=
literal|4
expr_stmt|;
block|}
comment|// Start w/ less than max capacity.  Space / time trade off.
name|fileHash
operator|=
operator|new
name|LruHashMap
argument_list|<>
argument_list|(
name|maxRecords
operator|/
literal|2
argument_list|,
name|maxRecords
argument_list|)
expr_stmt|;
name|dirHash
operator|=
operator|new
name|LruHashMap
argument_list|<>
argument_list|(
name|maxRecords
operator|/
literal|4
argument_list|,
name|maxRecords
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"LocalMetadataStore{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"uriHost='"
argument_list|)
operator|.
name|append
argument_list|(
name|uriHost
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path p)
specifier|public
name|void
name|delete
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|doDelete
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|forgetMetadata (Path p)
specifier|public
name|void
name|forgetMetadata
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|doDelete
argument_list|(
name|p
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteSubtree (Path path)
specifier|public
name|void
name|deleteSubtree
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|doDelete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|doDelete (Path p, boolean recursive, boolean tombstone)
specifier|private
specifier|synchronized
name|void
name|doDelete
parameter_list|(
name|Path
name|p
parameter_list|,
name|boolean
name|recursive
parameter_list|,
name|boolean
name|tombstone
parameter_list|)
block|{
name|Path
name|path
init|=
name|standardize
argument_list|(
name|p
argument_list|)
decl_stmt|;
comment|// Delete entry from file cache, then from cached parent directory, if any
name|deleteHashEntries
argument_list|(
name|path
argument_list|,
name|tombstone
argument_list|)
expr_stmt|;
if|if
condition|(
name|recursive
condition|)
block|{
comment|// Remove all entries that have this dir as path prefix.
name|deleteHashByAncestor
argument_list|(
name|path
argument_list|,
name|dirHash
argument_list|,
name|tombstone
argument_list|)
expr_stmt|;
name|deleteHashByAncestor
argument_list|(
name|path
argument_list|,
name|fileHash
argument_list|,
name|tombstone
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|get (Path p)
specifier|public
specifier|synchronized
name|PathMetadata
name|get
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|get
argument_list|(
name|p
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|get (Path p, boolean wantEmptyDirectoryFlag)
specifier|public
name|PathMetadata
name|get
parameter_list|(
name|Path
name|p
parameter_list|,
name|boolean
name|wantEmptyDirectoryFlag
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|standardize
argument_list|(
name|p
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|PathMetadata
name|m
init|=
name|fileHash
operator|.
name|mruGet
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|wantEmptyDirectoryFlag
operator|&&
name|m
operator|!=
literal|null
operator|&&
name|m
operator|.
name|getFileStatus
argument_list|()
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|m
operator|.
name|setIsEmptyDirectory
argument_list|(
name|isEmptyDirectory
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"get({}) -> {}"
argument_list|,
name|path
argument_list|,
name|m
operator|==
literal|null
condition|?
literal|"null"
else|:
name|m
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|m
return|;
block|}
block|}
comment|/**    * Determine if directory is empty.    * Call with lock held.    * @param p a Path, already filtered through standardize()    * @return TRUE / FALSE if known empty / not-empty, UNKNOWN otherwise.    */
DECL|method|isEmptyDirectory (Path p)
specifier|private
name|Tristate
name|isEmptyDirectory
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|DirListingMetadata
name|dirMeta
init|=
name|dirHash
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
return|return
name|dirMeta
operator|.
name|withoutTombstones
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listChildren (Path p)
specifier|public
specifier|synchronized
name|DirListingMetadata
name|listChildren
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|standardize
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|DirListingMetadata
name|listing
init|=
name|dirHash
operator|.
name|mruGet
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"listChildren({}) -> {}"
argument_list|,
name|path
argument_list|,
name|listing
operator|==
literal|null
condition|?
literal|"null"
else|:
name|listing
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Make a copy so callers can mutate without affecting our state
return|return
name|listing
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|DirListingMetadata
argument_list|(
name|listing
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|move (Collection<Path> pathsToDelete, Collection<PathMetadata> pathsToCreate)
specifier|public
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
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pathsToDelete
argument_list|,
literal|"pathsToDelete is null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pathsToCreate
argument_list|,
literal|"pathsToCreate is null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|pathsToDelete
operator|.
name|size
argument_list|()
operator|==
name|pathsToCreate
operator|.
name|size
argument_list|()
argument_list|,
literal|"Must supply same number of paths to delete/create."
argument_list|)
expr_stmt|;
comment|// I feel dirty for using reentrant lock. :-|
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// 1. Delete pathsToDelete
for|for
control|(
name|Path
name|meta
range|:
name|pathsToDelete
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"move: deleting metadata {}"
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|delete
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
comment|// 2. Create new destination path metadata
for|for
control|(
name|PathMetadata
name|meta
range|:
name|pathsToCreate
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"move: adding metadata {}"
argument_list|,
name|meta
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
comment|// 3. We now know full contents of all dirs in destination subtree
for|for
control|(
name|PathMetadata
name|meta
range|:
name|pathsToCreate
control|)
block|{
name|FileStatus
name|status
init|=
name|meta
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
operator|||
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|DirListingMetadata
name|dir
init|=
name|listChildren
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
comment|// could be evicted already
name|dir
operator|.
name|setAuthoritative
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|put (PathMetadata meta)
specifier|public
name|void
name|put
parameter_list|(
name|PathMetadata
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|FileStatus
name|status
init|=
name|meta
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|standardize
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|/* Add entry for this file. */
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"put {} -> {}"
argument_list|,
name|path
argument_list|,
name|meta
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fileHash
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|meta
argument_list|)
expr_stmt|;
comment|/* Directory case:        * We also make sure we have an entry in the dirHash, so subsequent        * listStatus(path) at least see the directory.        *        * If we had a boolean flag argument "isNew", we would know whether this        * is an existing directory the client discovered via getFileStatus(),        * or if it is a newly-created directory.  In the latter case, we would        * be able to mark the directory as authoritative (fully-cached),        * saving round trips to underlying store for subsequent listStatus()        */
if|if
condition|(
name|status
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|DirListingMetadata
name|dir
init|=
name|dirHash
operator|.
name|mruGet
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|==
literal|null
condition|)
block|{
name|dirHash
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|DirListingMetadata
argument_list|(
name|path
argument_list|,
name|DirListingMetadata
operator|.
name|EMPTY_DIR
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* Update cached parent dir. */
name|Path
name|parentPath
init|=
name|path
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentPath
operator|!=
literal|null
condition|)
block|{
name|DirListingMetadata
name|parent
init|=
name|dirHash
operator|.
name|mruGet
argument_list|(
name|parentPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
comment|/* Track this new file's listing in parent.  Parent is not          * authoritative, since there may be other items in it we don't know          * about. */
name|parent
operator|=
operator|new
name|DirListingMetadata
argument_list|(
name|parentPath
argument_list|,
name|DirListingMetadata
operator|.
name|EMPTY_DIR
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dirHash
operator|.
name|put
argument_list|(
name|parentPath
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|put
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|put (DirListingMetadata meta)
specifier|public
specifier|synchronized
name|void
name|put
parameter_list|(
name|DirListingMetadata
name|meta
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"put dirMeta {}"
argument_list|,
name|meta
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dirHash
operator|.
name|put
argument_list|(
name|standardize
argument_list|(
name|meta
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
name|meta
argument_list|)
expr_stmt|;
block|}
DECL|method|put (Collection<PathMetadata> metas)
specifier|public
specifier|synchronized
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
block|{
for|for
control|(
name|PathMetadata
name|meta
range|:
name|metas
control|)
block|{
name|put
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{    }
annotation|@
name|Override
DECL|method|destroy ()
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|dirHash
operator|!=
literal|null
condition|)
block|{
name|dirHash
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|prune (long modTime)
specifier|public
name|void
name|prune
parameter_list|(
name|long
name|modTime
parameter_list|)
throws|throws
name|IOException
block|{
name|prune
argument_list|(
name|modTime
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prune (long modTime, String keyPrefix)
specifier|public
specifier|synchronized
name|void
name|prune
parameter_list|(
name|long
name|modTime
parameter_list|,
name|String
name|keyPrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|PathMetadata
argument_list|>
argument_list|>
name|files
init|=
name|fileHash
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|files
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|PathMetadata
argument_list|>
name|entry
init|=
name|files
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|expired
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getFileStatus
argument_list|()
argument_list|,
name|modTime
argument_list|,
name|keyPrefix
argument_list|)
condition|)
block|{
name|files
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|DirListingMetadata
argument_list|>
argument_list|>
name|dirs
init|=
name|dirHash
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|dirs
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|DirListingMetadata
argument_list|>
name|entry
init|=
name|dirs
operator|.
name|next
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|DirListingMetadata
name|metadata
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|oldChildren
init|=
name|metadata
operator|.
name|getListing
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|newChildren
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|PathMetadata
name|child
range|:
name|oldChildren
control|)
block|{
name|FileStatus
name|status
init|=
name|child
operator|.
name|getFileStatus
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|expired
argument_list|(
name|status
argument_list|,
name|modTime
argument_list|,
name|keyPrefix
argument_list|)
condition|)
block|{
name|newChildren
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|newChildren
operator|.
name|size
argument_list|()
operator|!=
name|oldChildren
operator|.
name|size
argument_list|()
condition|)
block|{
name|dirHash
operator|.
name|put
argument_list|(
name|path
argument_list|,
operator|new
name|DirListingMetadata
argument_list|(
name|path
argument_list|,
name|newChildren
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|path
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|DirListingMetadata
name|parent
init|=
name|dirHash
operator|.
name|get
argument_list|(
name|path
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|setAuthoritative
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|expired (FileStatus status, long expiry, String keyPrefix)
specifier|private
name|boolean
name|expired
parameter_list|(
name|FileStatus
name|status
parameter_list|,
name|long
name|expiry
parameter_list|,
name|String
name|keyPrefix
parameter_list|)
block|{
comment|// Note: S3 doesn't track modification time on directories, so for
comment|// consistency with the DynamoDB implementation we ignore that here
return|return
name|status
operator|.
name|getModificationTime
argument_list|()
operator|<
name|expiry
operator|&&
operator|!
name|status
operator|.
name|isDirectory
argument_list|()
operator|&&
name|status
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|keyPrefix
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|deleteHashByAncestor (Path ancestor, Map<Path, T> hash, boolean tombstone)
specifier|static
parameter_list|<
name|T
parameter_list|>
name|void
name|deleteHashByAncestor
parameter_list|(
name|Path
name|ancestor
parameter_list|,
name|Map
argument_list|<
name|Path
argument_list|,
name|T
argument_list|>
name|hash
parameter_list|,
name|boolean
name|tombstone
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|T
argument_list|>
argument_list|>
name|it
init|=
name|hash
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|Path
argument_list|,
name|T
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|Path
name|f
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|T
name|meta
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|isAncestorOf
argument_list|(
name|ancestor
argument_list|,
name|f
argument_list|)
condition|)
block|{
if|if
condition|(
name|tombstone
condition|)
block|{
if|if
condition|(
name|meta
operator|instanceof
name|PathMetadata
condition|)
block|{
name|entry
operator|.
name|setValue
argument_list|(
operator|(
name|T
operator|)
name|PathMetadata
operator|.
name|tombstone
argument_list|(
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|meta
operator|instanceof
name|DirListingMetadata
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown type in hash"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * @return true iff 'ancestor' is ancestor dir in path 'f'.    * All paths here are absolute.  Dir does not count as its own ancestor.    */
DECL|method|isAncestorOf (Path ancestor, Path f)
specifier|private
specifier|static
name|boolean
name|isAncestorOf
parameter_list|(
name|Path
name|ancestor
parameter_list|,
name|Path
name|f
parameter_list|)
block|{
name|String
name|aStr
init|=
name|ancestor
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ancestor
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|aStr
operator|+=
literal|"/"
expr_stmt|;
block|}
name|String
name|fStr
init|=
name|f
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
operator|(
name|fStr
operator|.
name|startsWith
argument_list|(
name|aStr
argument_list|)
operator|)
return|;
block|}
comment|/**    * Update fileHash and dirHash to reflect deletion of file 'f'.  Call with    * lock held.    */
DECL|method|deleteHashEntries (Path path, boolean tombstone)
specifier|private
name|void
name|deleteHashEntries
parameter_list|(
name|Path
name|path
parameter_list|,
name|boolean
name|tombstone
parameter_list|)
block|{
comment|// Remove target file/dir
name|LOG
operator|.
name|debug
argument_list|(
literal|"delete file entry for {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|tombstone
condition|)
block|{
name|fileHash
operator|.
name|put
argument_list|(
name|path
argument_list|,
name|PathMetadata
operator|.
name|tombstone
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fileHash
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|// Update this and parent dir listing, if any
comment|/* If this path is a dir, remove its listing */
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing listing of {}"
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|dirHash
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|/* Remove this path from parent's dir listing */
name|Path
name|parent
init|=
name|path
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|DirListingMetadata
name|dir
init|=
name|dirHash
operator|.
name|get
argument_list|(
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"removing parent's entry for {} "
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|tombstone
condition|)
block|{
name|dir
operator|.
name|markDeleted
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dir
operator|.
name|remove
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Return a "standardized" version of a path so we always have a consistent    * hash value.  Also asserts the path is absolute, and contains host    * component.    * @param p input Path    * @return standardized version of Path, suitable for hash key    */
DECL|method|standardize (Path p)
specifier|private
name|Path
name|standardize
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|p
operator|.
name|isAbsolute
argument_list|()
argument_list|,
literal|"Path must be absolute"
argument_list|)
expr_stmt|;
name|URI
name|uri
init|=
name|p
operator|.
name|toUri
argument_list|()
decl_stmt|;
if|if
condition|(
name|uriHost
operator|!=
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|uri
operator|.
name|getHost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|p
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ()
specifier|public
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
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"local://metadata"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"uriHost"
argument_list|,
name|uriHost
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"description"
argument_list|,
literal|"Local in-VM metadata store for testing"
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
annotation|@
name|Override
DECL|method|updateParameters (Map<String, String> parameters)
specifier|public
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
block|{   }
block|}
end_class

end_unit

