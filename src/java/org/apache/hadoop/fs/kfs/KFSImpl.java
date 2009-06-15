begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or  * implied. See the License for the specific language governing  * permissions and limitations under the License.  *  *   * Provide the implementation of KFS which turn into calls to KfsAccess.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.kfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|kfs
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|Path
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kosmix
operator|.
name|kosmosfs
operator|.
name|access
operator|.
name|KfsAccess
import|;
end_import

begin_import
import|import
name|org
operator|.
name|kosmix
operator|.
name|kosmosfs
operator|.
name|access
operator|.
name|KfsFileAttr
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
DECL|class|KFSImpl
class|class
name|KFSImpl
implements|implements
name|IFSImpl
block|{
DECL|field|kfsAccess
specifier|private
name|KfsAccess
name|kfsAccess
init|=
literal|null
decl_stmt|;
DECL|field|statistics
specifier|private
name|FileSystem
operator|.
name|Statistics
name|statistics
decl_stmt|;
annotation|@
name|Deprecated
DECL|method|KFSImpl (String metaServerHost, int metaServerPort )
specifier|public
name|KFSImpl
parameter_list|(
name|String
name|metaServerHost
parameter_list|,
name|int
name|metaServerPort
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|metaServerHost
argument_list|,
name|metaServerPort
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|KFSImpl (String metaServerHost, int metaServerPort, FileSystem.Statistics stats)
specifier|public
name|KFSImpl
parameter_list|(
name|String
name|metaServerHost
parameter_list|,
name|int
name|metaServerPort
parameter_list|,
name|FileSystem
operator|.
name|Statistics
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
name|kfsAccess
operator|=
operator|new
name|KfsAccess
argument_list|(
name|metaServerHost
argument_list|,
name|metaServerPort
argument_list|)
expr_stmt|;
name|statistics
operator|=
name|stats
expr_stmt|;
block|}
DECL|method|exists (String path)
specifier|public
name|boolean
name|exists
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_exists
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|isDirectory (String path)
specifier|public
name|boolean
name|isDirectory
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_isDirectory
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|isFile (String path)
specifier|public
name|boolean
name|isFile
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_isFile
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|readdir (String path)
specifier|public
name|String
index|[]
name|readdir
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_readdir
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|readdirplus (Path path)
specifier|public
name|FileStatus
index|[]
name|readdirplus
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|srep
init|=
name|path
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|KfsFileAttr
index|[]
name|fattr
init|=
name|kfsAccess
operator|.
name|kfs_readdirplus
argument_list|(
name|srep
argument_list|)
decl_stmt|;
if|if
condition|(
name|fattr
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|int
name|numEntries
init|=
literal|0
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
name|fattr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|fattr
index|[
name|i
index|]
operator|.
name|filename
operator|.
name|compareTo
argument_list|(
literal|"."
argument_list|)
operator|==
literal|0
operator|)
operator|||
operator|(
name|fattr
index|[
name|i
index|]
operator|.
name|filename
operator|.
name|compareTo
argument_list|(
literal|".."
argument_list|)
operator|==
literal|0
operator|)
condition|)
continue|continue;
name|numEntries
operator|++
expr_stmt|;
block|}
name|FileStatus
index|[]
name|fstatus
init|=
operator|new
name|FileStatus
index|[
name|numEntries
index|]
decl_stmt|;
name|int
name|j
init|=
literal|0
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
name|fattr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|fattr
index|[
name|i
index|]
operator|.
name|filename
operator|.
name|compareTo
argument_list|(
literal|"."
argument_list|)
operator|==
literal|0
operator|)
operator|||
operator|(
name|fattr
index|[
name|i
index|]
operator|.
name|filename
operator|.
name|compareTo
argument_list|(
literal|".."
argument_list|)
operator|==
literal|0
operator|)
condition|)
continue|continue;
name|Path
name|fn
init|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
name|fattr
index|[
name|i
index|]
operator|.
name|filename
argument_list|)
decl_stmt|;
if|if
condition|(
name|fattr
index|[
name|i
index|]
operator|.
name|isDirectory
condition|)
name|fstatus
index|[
name|j
index|]
operator|=
operator|new
name|FileStatus
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
name|fattr
index|[
name|i
index|]
operator|.
name|modificationTime
argument_list|,
name|fn
argument_list|)
expr_stmt|;
else|else
name|fstatus
index|[
name|j
index|]
operator|=
operator|new
name|FileStatus
argument_list|(
name|fattr
index|[
name|i
index|]
operator|.
name|filesize
argument_list|,
name|fattr
index|[
name|i
index|]
operator|.
name|isDirectory
argument_list|,
name|fattr
index|[
name|i
index|]
operator|.
name|replication
argument_list|,
call|(
name|long
call|)
argument_list|(
literal|1
operator|<<
literal|26
argument_list|)
argument_list|,
name|fattr
index|[
name|i
index|]
operator|.
name|modificationTime
argument_list|,
name|fn
argument_list|)
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
return|return
name|fstatus
return|;
block|}
DECL|method|mkdirs (String path)
specifier|public
name|int
name|mkdirs
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_mkdirs
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|rename (String source, String dest)
specifier|public
name|int
name|rename
parameter_list|(
name|String
name|source
parameter_list|,
name|String
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_rename
argument_list|(
name|source
argument_list|,
name|dest
argument_list|)
return|;
block|}
DECL|method|rmdir (String path)
specifier|public
name|int
name|rmdir
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_rmdir
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|remove (String path)
specifier|public
name|int
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_remove
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|filesize (String path)
specifier|public
name|long
name|filesize
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_filesize
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|getReplication (String path)
specifier|public
name|short
name|getReplication
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_getReplication
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|setReplication (String path, short replication)
specifier|public
name|short
name|setReplication
parameter_list|(
name|String
name|path
parameter_list|,
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_setReplication
argument_list|(
name|path
argument_list|,
name|replication
argument_list|)
return|;
block|}
DECL|method|getDataLocation (String path, long start, long len)
specifier|public
name|String
index|[]
index|[]
name|getDataLocation
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|start
parameter_list|,
name|long
name|len
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_getDataLocation
argument_list|(
name|path
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
return|;
block|}
DECL|method|getModificationTime (String path)
specifier|public
name|long
name|getModificationTime
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|kfsAccess
operator|.
name|kfs_getModificationTime
argument_list|(
name|path
argument_list|)
return|;
block|}
DECL|method|open (String path, int bufferSize)
specifier|public
name|FSDataInputStream
name|open
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FSDataInputStream
argument_list|(
operator|new
name|KFSInputStream
argument_list|(
name|kfsAccess
argument_list|,
name|path
argument_list|,
name|statistics
argument_list|)
argument_list|)
return|;
block|}
DECL|method|create (String path, short replication, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
name|String
name|path
parameter_list|,
name|short
name|replication
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FSDataOutputStream
argument_list|(
operator|new
name|KFSOutputStream
argument_list|(
name|kfsAccess
argument_list|,
name|path
argument_list|,
name|replication
argument_list|,
literal|false
argument_list|,
name|progress
argument_list|)
argument_list|,
name|statistics
argument_list|)
return|;
block|}
DECL|method|append (String path, int bufferSize, Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
comment|// when opening for append, # of replicas is ignored
return|return
operator|new
name|FSDataOutputStream
argument_list|(
operator|new
name|KFSOutputStream
argument_list|(
name|kfsAccess
argument_list|,
name|path
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|true
argument_list|,
name|progress
argument_list|)
argument_list|,
name|statistics
argument_list|)
return|;
block|}
block|}
end_class

end_unit

