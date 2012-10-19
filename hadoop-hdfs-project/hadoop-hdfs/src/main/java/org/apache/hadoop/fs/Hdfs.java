begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|net
operator|.
name|URISyntaxException
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
name|NoSuchElementException
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
name|fs
operator|.
name|Options
operator|.
name|ChecksumOpt
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
name|CorruptFileBlockIterator
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
name|DFSClient
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
name|DFSUtil
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
name|HdfsConfiguration
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
name|client
operator|.
name|HdfsDataInputStream
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
name|client
operator|.
name|HdfsDataOutputStream
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
name|DirectoryListing
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
name|HdfsFileStatus
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
name|HdfsLocatedFileStatus
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
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|token
operator|.
name|SecretManager
operator|.
name|InvalidToken
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
name|delegation
operator|.
name|AbstractDelegationTokenIdentifier
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
name|Progressable
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Hdfs
specifier|public
class|class
name|Hdfs
extends|extends
name|AbstractFileSystem
block|{
DECL|field|dfs
name|DFSClient
name|dfs
decl_stmt|;
DECL|field|verifyChecksum
specifier|private
name|boolean
name|verifyChecksum
init|=
literal|true
decl_stmt|;
static|static
block|{
name|HdfsConfiguration
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * This constructor has the signature needed by    * {@link AbstractFileSystem#createFileSystem(URI, Configuration)}    *     * @param theUri    *          which must be that of Hdfs    * @param conf    * @throws IOException    */
DECL|method|Hdfs (final URI theUri, final Configuration conf)
name|Hdfs
parameter_list|(
specifier|final
name|URI
name|theUri
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|super
argument_list|(
name|theUri
argument_list|,
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|,
literal|true
argument_list|,
name|NameNode
operator|.
name|DEFAULT_PORT
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|theUri
operator|.
name|getScheme
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Passed URI's scheme is not for Hdfs"
argument_list|)
throw|;
block|}
name|String
name|host
init|=
name|theUri
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|host
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Incomplete HDFS URI, no host: "
operator|+
name|theUri
argument_list|)
throw|;
block|}
name|this
operator|.
name|dfs
operator|=
operator|new
name|DFSClient
argument_list|(
name|theUri
argument_list|,
name|conf
argument_list|,
name|getStatistics
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUriDefaultPort ()
specifier|public
name|int
name|getUriDefaultPort
parameter_list|()
block|{
return|return
name|NameNode
operator|.
name|DEFAULT_PORT
return|;
block|}
annotation|@
name|Override
DECL|method|createInternal (Path f, EnumSet<CreateFlag> createFlag, FsPermission absolutePermission, int bufferSize, short replication, long blockSize, Progressable progress, ChecksumOpt checksumOpt, boolean createParent)
specifier|public
name|HdfsDataOutputStream
name|createInternal
parameter_list|(
name|Path
name|f
parameter_list|,
name|EnumSet
argument_list|<
name|CreateFlag
argument_list|>
name|createFlag
parameter_list|,
name|FsPermission
name|absolutePermission
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|short
name|replication
parameter_list|,
name|long
name|blockSize
parameter_list|,
name|Progressable
name|progress
parameter_list|,
name|ChecksumOpt
name|checksumOpt
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HdfsDataOutputStream
argument_list|(
name|dfs
operator|.
name|primitiveCreate
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|absolutePermission
argument_list|,
name|createFlag
argument_list|,
name|createParent
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|,
name|progress
argument_list|,
name|bufferSize
argument_list|,
name|checksumOpt
argument_list|)
argument_list|,
name|getStatistics
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete (Path f, boolean recursive)
specifier|public
name|boolean
name|delete
parameter_list|(
name|Path
name|f
parameter_list|,
name|boolean
name|recursive
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
return|return
name|dfs
operator|.
name|delete
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|recursive
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileBlockLocations (Path p, long start, long len)
specifier|public
name|BlockLocation
index|[]
name|getFileBlockLocations
parameter_list|(
name|Path
name|p
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
return|return
name|dfs
operator|.
name|getBlockLocations
argument_list|(
name|getUriPath
argument_list|(
name|p
argument_list|)
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileChecksum (Path f)
specifier|public
name|FileChecksum
name|getFileChecksum
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
return|return
name|dfs
operator|.
name|getFileChecksum
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFileStatus (Path f)
specifier|public
name|FileStatus
name|getFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|HdfsFileStatus
name|fi
init|=
name|dfs
operator|.
name|getFileInfo
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|!=
literal|null
condition|)
block|{
return|return
name|makeQualified
argument_list|(
name|fi
argument_list|,
name|f
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File does not exist: "
operator|+
name|f
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFileLinkStatus (Path f)
specifier|public
name|FileStatus
name|getFileLinkStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|HdfsFileStatus
name|fi
init|=
name|dfs
operator|.
name|getFileLinkInfo
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fi
operator|!=
literal|null
condition|)
block|{
return|return
name|makeQualified
argument_list|(
name|fi
argument_list|,
name|f
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File does not exist: "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
DECL|method|makeQualified (HdfsFileStatus f, Path parent)
specifier|private
name|FileStatus
name|makeQualified
parameter_list|(
name|HdfsFileStatus
name|f
parameter_list|,
name|Path
name|parent
parameter_list|)
block|{
comment|// NB: symlink is made fully-qualified in FileContext.
return|return
operator|new
name|FileStatus
argument_list|(
name|f
operator|.
name|getLen
argument_list|()
argument_list|,
name|f
operator|.
name|isDir
argument_list|()
argument_list|,
name|f
operator|.
name|getReplication
argument_list|()
argument_list|,
name|f
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|f
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|f
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|f
operator|.
name|getPermission
argument_list|()
argument_list|,
name|f
operator|.
name|getOwner
argument_list|()
argument_list|,
name|f
operator|.
name|getGroup
argument_list|()
argument_list|,
name|f
operator|.
name|isSymlink
argument_list|()
condition|?
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getSymlink
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
operator|(
name|f
operator|.
name|getFullPath
argument_list|(
name|parent
argument_list|)
operator|)
operator|.
name|makeQualified
argument_list|(
name|getUri
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
comment|// fully-qualify path
block|}
DECL|method|makeQualifiedLocated ( HdfsLocatedFileStatus f, Path parent)
specifier|private
name|LocatedFileStatus
name|makeQualifiedLocated
parameter_list|(
name|HdfsLocatedFileStatus
name|f
parameter_list|,
name|Path
name|parent
parameter_list|)
block|{
return|return
operator|new
name|LocatedFileStatus
argument_list|(
name|f
operator|.
name|getLen
argument_list|()
argument_list|,
name|f
operator|.
name|isDir
argument_list|()
argument_list|,
name|f
operator|.
name|getReplication
argument_list|()
argument_list|,
name|f
operator|.
name|getBlockSize
argument_list|()
argument_list|,
name|f
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|f
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|f
operator|.
name|getPermission
argument_list|()
argument_list|,
name|f
operator|.
name|getOwner
argument_list|()
argument_list|,
name|f
operator|.
name|getGroup
argument_list|()
argument_list|,
name|f
operator|.
name|isSymlink
argument_list|()
condition|?
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getSymlink
argument_list|()
argument_list|)
else|:
literal|null
argument_list|,
operator|(
name|f
operator|.
name|getFullPath
argument_list|(
name|parent
argument_list|)
operator|)
operator|.
name|makeQualified
argument_list|(
name|getUri
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|,
comment|// fully-qualify path
name|DFSUtil
operator|.
name|locatedBlocks2Locations
argument_list|(
name|f
operator|.
name|getBlockLocations
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getFsStatus ()
specifier|public
name|FsStatus
name|getFsStatus
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getDiskStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getServerDefaults ()
specifier|public
name|FsServerDefaults
name|getServerDefaults
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|dfs
operator|.
name|getServerDefaults
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listLocatedStatus ( final Path p)
specifier|public
name|RemoteIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
name|listLocatedStatus
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
return|return
operator|new
name|DirListingIterator
argument_list|<
name|LocatedFileStatus
argument_list|>
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|LocatedFileStatus
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|makeQualifiedLocated
argument_list|(
operator|(
name|HdfsLocatedFileStatus
operator|)
name|getNext
argument_list|()
argument_list|,
name|p
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|listStatusIterator (final Path f)
specifier|public
name|RemoteIterator
argument_list|<
name|FileStatus
argument_list|>
name|listStatusIterator
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
throws|,
name|IOException
block|{
return|return
operator|new
name|DirListingIterator
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|f
argument_list|,
literal|false
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|FileStatus
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|makeQualified
argument_list|(
name|getNext
argument_list|()
argument_list|,
name|f
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * This class defines an iterator that returns    * the file status of each file/subdirectory of a directory    *     * if needLocation, status contains block location if it is a file    * throws a RuntimeException with the error as its cause.    *     * @param<T> the type of the file status    */
DECL|class|DirListingIterator
specifier|abstract
specifier|private
class|class
name|DirListingIterator
parameter_list|<
name|T
extends|extends
name|FileStatus
parameter_list|>
implements|implements
name|RemoteIterator
argument_list|<
name|T
argument_list|>
block|{
DECL|field|thisListing
specifier|private
name|DirectoryListing
name|thisListing
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|;
DECL|field|src
specifier|final
specifier|private
name|String
name|src
decl_stmt|;
DECL|field|needLocation
specifier|final
specifier|private
name|boolean
name|needLocation
decl_stmt|;
comment|// if status
DECL|method|DirListingIterator (Path p, boolean needLocation)
specifier|private
name|DirListingIterator
parameter_list|(
name|Path
name|p
parameter_list|,
name|boolean
name|needLocation
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|src
operator|=
name|Hdfs
operator|.
name|this
operator|.
name|getUriPath
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|this
operator|.
name|needLocation
operator|=
name|needLocation
expr_stmt|;
comment|// fetch the first batch of entries in the directory
name|thisListing
operator|=
name|dfs
operator|.
name|listPaths
argument_list|(
name|src
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|,
name|needLocation
argument_list|)
expr_stmt|;
if|if
condition|(
name|thisListing
operator|==
literal|null
condition|)
block|{
comment|// the directory does not exist
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File "
operator|+
name|src
operator|+
literal|" does not exist."
argument_list|)
throw|;
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
if|if
condition|(
name|thisListing
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|i
operator|>=
name|thisListing
operator|.
name|getPartialListing
argument_list|()
operator|.
name|length
operator|&&
name|thisListing
operator|.
name|hasMore
argument_list|()
condition|)
block|{
comment|// current listing is exhausted& fetch a new listing
name|thisListing
operator|=
name|dfs
operator|.
name|listPaths
argument_list|(
name|src
argument_list|,
name|thisListing
operator|.
name|getLastName
argument_list|()
argument_list|,
name|needLocation
argument_list|)
expr_stmt|;
if|if
condition|(
name|thisListing
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
comment|// the directory is deleted
block|}
name|i
operator|=
literal|0
expr_stmt|;
block|}
return|return
operator|(
name|i
operator|<
name|thisListing
operator|.
name|getPartialListing
argument_list|()
operator|.
name|length
operator|)
return|;
block|}
comment|/**      * Get the next item in the list      * @return the next item in the list      *       * @throws IOException if there is any error      * @throws NoSuchElmentException if no more entry is available      */
DECL|method|getNext ()
specifier|public
name|HdfsFileStatus
name|getNext
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|thisListing
operator|.
name|getPartialListing
argument_list|()
index|[
name|i
operator|++
index|]
return|;
block|}
throw|throw
operator|new
name|NoSuchElementException
argument_list|(
literal|"No more entry in "
operator|+
name|src
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listStatus (Path f)
specifier|public
name|FileStatus
index|[]
name|listStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|String
name|src
init|=
name|getUriPath
argument_list|(
name|f
argument_list|)
decl_stmt|;
comment|// fetch the first batch of entries in the directory
name|DirectoryListing
name|thisListing
init|=
name|dfs
operator|.
name|listPaths
argument_list|(
name|src
argument_list|,
name|HdfsFileStatus
operator|.
name|EMPTY_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisListing
operator|==
literal|null
condition|)
block|{
comment|// the directory does not exist
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File "
operator|+
name|f
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
name|HdfsFileStatus
index|[]
name|partialListing
init|=
name|thisListing
operator|.
name|getPartialListing
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|thisListing
operator|.
name|hasMore
argument_list|()
condition|)
block|{
comment|// got all entries of the directory
name|FileStatus
index|[]
name|stats
init|=
operator|new
name|FileStatus
index|[
name|partialListing
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|partialListing
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|stats
index|[
name|i
index|]
operator|=
name|makeQualified
argument_list|(
name|partialListing
index|[
name|i
index|]
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
comment|// The directory size is too big that it needs to fetch more
comment|// estimate the total number of entries in the directory
name|int
name|totalNumEntries
init|=
name|partialListing
operator|.
name|length
operator|+
name|thisListing
operator|.
name|getRemainingEntries
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
name|listing
init|=
operator|new
name|ArrayList
argument_list|<
name|FileStatus
argument_list|>
argument_list|(
name|totalNumEntries
argument_list|)
decl_stmt|;
comment|// add the first batch of entries to the array list
for|for
control|(
name|HdfsFileStatus
name|fileStatus
range|:
name|partialListing
control|)
block|{
name|listing
operator|.
name|add
argument_list|(
name|makeQualified
argument_list|(
name|fileStatus
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now fetch more entries
do|do
block|{
name|thisListing
operator|=
name|dfs
operator|.
name|listPaths
argument_list|(
name|src
argument_list|,
name|thisListing
operator|.
name|getLastName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|thisListing
operator|==
literal|null
condition|)
block|{
comment|// the directory is deleted
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File "
operator|+
name|f
operator|+
literal|" does not exist."
argument_list|)
throw|;
block|}
name|partialListing
operator|=
name|thisListing
operator|.
name|getPartialListing
argument_list|()
expr_stmt|;
for|for
control|(
name|HdfsFileStatus
name|fileStatus
range|:
name|partialListing
control|)
block|{
name|listing
operator|.
name|add
argument_list|(
name|makeQualified
argument_list|(
name|fileStatus
argument_list|,
name|f
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|thisListing
operator|.
name|hasMore
argument_list|()
condition|)
do|;
return|return
name|listing
operator|.
name|toArray
argument_list|(
operator|new
name|FileStatus
index|[
name|listing
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|listCorruptFileBlocks (Path path)
specifier|public
name|RemoteIterator
argument_list|<
name|Path
argument_list|>
name|listCorruptFileBlocks
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|CorruptFileBlockIterator
argument_list|(
name|dfs
argument_list|,
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|mkdir (Path dir, FsPermission permission, boolean createParent)
specifier|public
name|void
name|mkdir
parameter_list|(
name|Path
name|dir
parameter_list|,
name|FsPermission
name|permission
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|primitiveMkdir
argument_list|(
name|getUriPath
argument_list|(
name|dir
argument_list|)
argument_list|,
name|permission
argument_list|,
name|createParent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|open (Path f, int bufferSize)
specifier|public
name|HdfsDataInputStream
name|open
parameter_list|(
name|Path
name|f
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
return|return
operator|new
name|DFSClient
operator|.
name|DFSDataInputStream
argument_list|(
name|dfs
operator|.
name|open
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|bufferSize
argument_list|,
name|verifyChecksum
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|renameInternal (Path src, Path dst)
specifier|public
name|void
name|renameInternal
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|rename
argument_list|(
name|getUriPath
argument_list|(
name|src
argument_list|)
argument_list|,
name|getUriPath
argument_list|(
name|dst
argument_list|)
argument_list|,
name|Options
operator|.
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|renameInternal (Path src, Path dst, boolean overwrite)
specifier|public
name|void
name|renameInternal
parameter_list|(
name|Path
name|src
parameter_list|,
name|Path
name|dst
parameter_list|,
name|boolean
name|overwrite
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|rename
argument_list|(
name|getUriPath
argument_list|(
name|src
argument_list|)
argument_list|,
name|getUriPath
argument_list|(
name|dst
argument_list|)
argument_list|,
name|overwrite
condition|?
name|Options
operator|.
name|Rename
operator|.
name|OVERWRITE
else|:
name|Options
operator|.
name|Rename
operator|.
name|NONE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setOwner (Path f, String username, String groupname)
specifier|public
name|void
name|setOwner
parameter_list|(
name|Path
name|f
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|groupname
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|setOwner
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|username
argument_list|,
name|groupname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPermission (Path f, FsPermission permission)
specifier|public
name|void
name|setPermission
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|setPermission
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|permission
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setReplication (Path f, short replication)
specifier|public
name|boolean
name|setReplication
parameter_list|(
name|Path
name|f
parameter_list|,
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
return|return
name|dfs
operator|.
name|setReplication
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|replication
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTimes (Path f, long mtime, long atime)
specifier|public
name|void
name|setTimes
parameter_list|(
name|Path
name|f
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|setTimes
argument_list|(
name|getUriPath
argument_list|(
name|f
argument_list|)
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setVerifyChecksum (boolean verifyChecksum)
specifier|public
name|void
name|setVerifyChecksum
parameter_list|(
name|boolean
name|verifyChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|verifyChecksum
operator|=
name|verifyChecksum
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|supportsSymlinks ()
specifier|public
name|boolean
name|supportsSymlinks
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|createSymlink (Path target, Path link, boolean createParent)
specifier|public
name|void
name|createSymlink
parameter_list|(
name|Path
name|target
parameter_list|,
name|Path
name|link
parameter_list|,
name|boolean
name|createParent
parameter_list|)
throws|throws
name|IOException
throws|,
name|UnresolvedLinkException
block|{
name|dfs
operator|.
name|createSymlink
argument_list|(
name|target
operator|.
name|toString
argument_list|()
argument_list|,
name|getUriPath
argument_list|(
name|link
argument_list|)
argument_list|,
name|createParent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLinkTarget (Path p)
specifier|public
name|Path
name|getLinkTarget
parameter_list|(
name|Path
name|p
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Path
argument_list|(
name|dfs
operator|.
name|getLinkTarget
argument_list|(
name|getUriPath
argument_list|(
name|p
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getCanonicalServiceName ()
specifier|public
name|String
name|getCanonicalServiceName
parameter_list|()
block|{
return|return
name|dfs
operator|.
name|getCanonicalServiceName
argument_list|()
return|;
block|}
annotation|@
name|Override
comment|//AbstractFileSystem
DECL|method|getDelegationTokens (String renewer)
specifier|public
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|getDelegationTokens
parameter_list|(
name|String
name|renewer
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|result
init|=
name|dfs
operator|.
name|getDelegationToken
argument_list|(
name|renewer
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
name|tokenList
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|tokenList
operator|.
name|add
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|tokenList
return|;
block|}
comment|/**    * Renew an existing delegation token.    *     * @param token delegation token obtained earlier    * @return the new expiration time    * @throws InvalidToken    * @throws IOException    * @deprecated Use Token.renew instead.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|renewDelegationToken ( Token<? extends AbstractDelegationTokenIdentifier> token)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|?
extends|extends
name|AbstractDelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|InvalidToken
throws|,
name|IOException
block|{
return|return
name|dfs
operator|.
name|renewDelegationToken
argument_list|(
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|)
return|;
block|}
comment|/**    * Cancel an existing delegation token.    *     * @param token delegation token    * @throws InvalidToken    * @throws IOException    * @deprecated Use Token.cancel instead.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|cancelDelegationToken ( Token<? extends AbstractDelegationTokenIdentifier> token)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|?
extends|extends
name|AbstractDelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|InvalidToken
throws|,
name|IOException
block|{
name|dfs
operator|.
name|cancelDelegationToken
argument_list|(
operator|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
operator|)
name|token
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

