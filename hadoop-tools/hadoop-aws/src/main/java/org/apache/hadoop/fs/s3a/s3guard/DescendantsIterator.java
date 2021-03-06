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
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
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
name|s3a
operator|.
name|S3AFileStatus
import|;
end_import

begin_comment
comment|/**  * {@code DescendantsIterator} is a {@link RemoteIterator} that implements  * pre-ordering breadth-first traversal (BFS) of a path and all of its  * descendants recursively.  After visiting each path, that path's direct  * children are discovered by calling {@link MetadataStore#listChildren(Path)}.  * Each iteration returns the next direct child, and if that child is a  * directory, also pushes it onto a queue to discover its children later.  *  * For example, assume the consistent store contains metadata representing this  * file system structure:  *  *<pre>  * /dir1  * |-- dir2  * |   |-- file1  * |   `-- file2  * `-- dir3  *     |-- dir4  *     |   `-- file3  *     |-- dir5  *     |   `-- file4  *     `-- dir6  *</pre>  *  * Consider this code sample:  *<pre>  * final PathMetadata dir1 = get(new Path("/dir1"));  * for (DescendantsIterator descendants = new DescendantsIterator(dir1);  *     descendants.hasNext(); ) {  *   final FileStatus status = descendants.next().getFileStatus();  *   System.out.printf("%s %s%n", status.isDirectory() ? 'D' : 'F',  *       status.getPath());  * }  *</pre>  *  * The output is:  *<pre>  * D /dir1  * D /dir1/dir2  * D /dir1/dir3  * F /dir1/dir2/file1  * F /dir1/dir2/file2  * D /dir1/dir3/dir4  * D /dir1/dir3/dir5  * F /dir1/dir3/dir4/file3  * F /dir1/dir3/dir5/file4  * D /dir1/dir3/dir6  *</pre>  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|DescendantsIterator
specifier|public
class|class
name|DescendantsIterator
implements|implements
name|RemoteIterator
argument_list|<
name|S3AFileStatus
argument_list|>
block|{
DECL|field|metadataStore
specifier|private
specifier|final
name|MetadataStore
name|metadataStore
decl_stmt|;
DECL|field|queue
specifier|private
specifier|final
name|Queue
argument_list|<
name|PathMetadata
argument_list|>
name|queue
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Creates a new {@code DescendantsIterator}.    *    * @param ms the associated {@link MetadataStore}    * @param meta base path for descendants iteration, which will be the first    *     returned during iteration (except root). Null makes empty iterator.    * @throws IOException if errors happen during metadata store listing    */
DECL|method|DescendantsIterator (MetadataStore ms, PathMetadata meta)
specifier|public
name|DescendantsIterator
parameter_list|(
name|MetadataStore
name|ms
parameter_list|,
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
name|ms
argument_list|)
expr_stmt|;
name|this
operator|.
name|metadataStore
operator|=
name|ms
expr_stmt|;
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Path
name|path
init|=
name|meta
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|isRoot
argument_list|()
condition|)
block|{
name|DirListingMetadata
name|rootListing
init|=
name|ms
operator|.
name|listChildren
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootListing
operator|!=
literal|null
condition|)
block|{
name|rootListing
operator|=
name|rootListing
operator|.
name|withoutTombstones
argument_list|()
expr_stmt|;
name|queue
operator|.
name|addAll
argument_list|(
name|rootListing
operator|.
name|getListing
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|queue
operator|.
name|add
argument_list|(
name|meta
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|S3AFileStatus
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|hasNext
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No more descendants."
argument_list|)
throw|;
block|}
name|PathMetadata
name|next
decl_stmt|;
name|next
operator|=
name|queue
operator|.
name|poll
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|getFileStatus
argument_list|()
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
specifier|final
name|Path
name|path
init|=
name|next
operator|.
name|getFileStatus
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|DirListingMetadata
name|meta
init|=
name|metadataStore
operator|.
name|listChildren
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|meta
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|PathMetadata
argument_list|>
name|more
init|=
name|meta
operator|.
name|withoutTombstones
argument_list|()
operator|.
name|getListing
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|more
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|queue
operator|.
name|addAll
argument_list|(
name|more
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|next
operator|.
name|getFileStatus
argument_list|()
return|;
block|}
block|}
end_class

end_unit

