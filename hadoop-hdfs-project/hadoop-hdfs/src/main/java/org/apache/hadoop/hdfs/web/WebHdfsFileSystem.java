begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
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
name|net
operator|.
name|URL
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
name|FileAlreadyExistsException
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
name|Options
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
name|ParentNotDirectoryException
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
name|HftpFileSystem
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
name|DSQuotaExceededException
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
name|NSQuotaExceededException
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
name|UnresolvedPathException
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
name|SafeModeException
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
name|web
operator|.
name|resources
operator|.
name|AccessTimeParam
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
name|web
operator|.
name|resources
operator|.
name|BlockSizeParam
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
name|web
operator|.
name|resources
operator|.
name|BufferSizeParam
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
name|web
operator|.
name|resources
operator|.
name|DeleteOpParam
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
name|web
operator|.
name|resources
operator|.
name|DstPathParam
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
name|web
operator|.
name|resources
operator|.
name|GetOpParam
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
name|web
operator|.
name|resources
operator|.
name|GroupParam
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
name|web
operator|.
name|resources
operator|.
name|HttpOpParam
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
name|web
operator|.
name|resources
operator|.
name|ModificationTimeParam
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
name|web
operator|.
name|resources
operator|.
name|OverwriteParam
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
name|web
operator|.
name|resources
operator|.
name|OwnerParam
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
name|web
operator|.
name|resources
operator|.
name|Param
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
name|web
operator|.
name|resources
operator|.
name|PermissionParam
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
name|web
operator|.
name|resources
operator|.
name|PostOpParam
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
name|web
operator|.
name|resources
operator|.
name|PutOpParam
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
name|web
operator|.
name|resources
operator|.
name|RecursiveParam
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
name|web
operator|.
name|resources
operator|.
name|RenameOptionSetParam
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
name|web
operator|.
name|resources
operator|.
name|ReplicationParam
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
name|ipc
operator|.
name|RemoteException
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
name|UserGroupInformation
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

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
import|;
end_import

begin_comment
comment|/** A FileSystem for HDFS over the web. */
end_comment

begin_class
DECL|class|WebHdfsFileSystem
specifier|public
class|class
name|WebHdfsFileSystem
extends|extends
name|HftpFileSystem
block|{
comment|/** File System URI: {SCHEME}://namenode:port/path/to/file */
DECL|field|SCHEME
specifier|public
specifier|static
specifier|final
name|String
name|SCHEME
init|=
literal|"webhdfs"
decl_stmt|;
comment|/** Http URI: http://namenode:port/{PATH_PREFIX}/path/to/file */
DECL|field|PATH_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|PATH_PREFIX
init|=
name|SCHEME
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|workingDir
specifier|protected
name|Path
name|workingDir
decl_stmt|;
annotation|@
name|Override
DECL|method|initialize (URI uri, Configuration conf)
specifier|public
name|void
name|initialize
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
name|super
operator|.
name|initialize
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|this
operator|.
name|workingDir
operator|=
name|getHomeDirectory
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUri ()
specifier|public
name|URI
name|getUri
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|SCHEME
argument_list|,
literal|null
argument_list|,
name|nnAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nnAddr
operator|.
name|getPort
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getHomeDirectory ()
specifier|public
name|Path
name|getHomeDirectory
parameter_list|()
block|{
return|return
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/user/"
operator|+
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getWorkingDirectory ()
specifier|public
specifier|synchronized
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|workingDir
return|;
block|}
annotation|@
name|Override
DECL|method|setWorkingDirectory (final Path dir)
specifier|public
specifier|synchronized
name|void
name|setWorkingDirectory
parameter_list|(
specifier|final
name|Path
name|dir
parameter_list|)
block|{
name|String
name|result
init|=
name|makeAbsolute
argument_list|(
name|dir
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|DFSUtil
operator|.
name|isValidName
argument_list|(
name|result
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid DFS directory name "
operator|+
name|result
argument_list|)
throw|;
block|}
name|workingDir
operator|=
name|makeAbsolute
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|method|makeAbsolute (Path f)
specifier|private
name|Path
name|makeAbsolute
parameter_list|(
name|Path
name|f
parameter_list|)
block|{
return|return
name|f
operator|.
name|isAbsolute
argument_list|()
condition|?
name|f
else|:
operator|new
name|Path
argument_list|(
name|workingDir
argument_list|,
name|f
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|jsonParse (final InputStream in )
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|jsonParse
parameter_list|(
specifier|final
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The input stream is null."
argument_list|)
throw|;
block|}
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|JSON
operator|.
name|parse
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
DECL|method|validateResponse (final HttpOpParam.Op op, final HttpURLConnection conn)
specifier|private
specifier|static
name|void
name|validateResponse
parameter_list|(
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
parameter_list|,
specifier|final
name|HttpURLConnection
name|conn
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|code
init|=
name|conn
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|code
operator|!=
name|op
operator|.
name|getExpectedHttpResponseCode
argument_list|()
condition|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|m
decl_stmt|;
try|try
block|{
name|m
operator|=
name|jsonParse
argument_list|(
name|conn
operator|.
name|getErrorStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected HTTP response: code = "
operator|+
name|code
operator|+
literal|" != "
operator|+
name|op
operator|.
name|getExpectedHttpResponseCode
argument_list|()
operator|+
literal|", "
operator|+
name|op
operator|.
name|toQueryString
argument_list|()
operator|+
literal|", message="
operator|+
name|conn
operator|.
name|getResponseMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|RemoteException
name|re
init|=
name|JsonUtil
operator|.
name|toRemoteException
argument_list|(
name|m
argument_list|)
decl_stmt|;
throw|throw
name|re
operator|.
name|unwrapRemoteException
argument_list|(
name|AccessControlException
operator|.
name|class
argument_list|,
name|DSQuotaExceededException
operator|.
name|class
argument_list|,
name|FileAlreadyExistsException
operator|.
name|class
argument_list|,
name|FileNotFoundException
operator|.
name|class
argument_list|,
name|ParentNotDirectoryException
operator|.
name|class
argument_list|,
name|SafeModeException
operator|.
name|class
argument_list|,
name|NSQuotaExceededException
operator|.
name|class
argument_list|,
name|UnresolvedPathException
operator|.
name|class
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|openConnection (String path, String query)
specifier|protected
name|HttpURLConnection
name|openConnection
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|IOException
block|{
name|query
operator|=
name|addDelegationTokenParam
argument_list|(
name|query
argument_list|)
expr_stmt|;
specifier|final
name|URL
name|url
init|=
name|getNamenodeURL
argument_list|(
name|path
argument_list|,
name|query
argument_list|)
decl_stmt|;
return|return
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
return|;
block|}
DECL|method|httpConnect (final HttpOpParam.Op op, final Path fspath, final Param<?,?>... parameters)
specifier|private
name|HttpURLConnection
name|httpConnect
parameter_list|(
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
parameter_list|,
specifier|final
name|Path
name|fspath
parameter_list|,
specifier|final
name|Param
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
modifier|...
name|parameters
parameter_list|)
throws|throws
name|IOException
block|{
comment|//initialize URI path and query
specifier|final
name|String
name|uripath
init|=
literal|"/"
operator|+
name|PATH_PREFIX
operator|+
name|makeQualified
argument_list|(
name|fspath
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
specifier|final
name|String
name|query
init|=
name|op
operator|.
name|toQueryString
argument_list|()
operator|+
name|Param
operator|.
name|toSortedString
argument_list|(
literal|"&"
argument_list|,
name|parameters
argument_list|)
decl_stmt|;
comment|//connect and get response
specifier|final
name|HttpURLConnection
name|conn
init|=
name|openConnection
argument_list|(
name|uripath
argument_list|,
name|query
argument_list|)
decl_stmt|;
try|try
block|{
name|conn
operator|.
name|setRequestMethod
argument_list|(
name|op
operator|.
name|getType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setDoOutput
argument_list|(
name|op
operator|.
name|getDoOutput
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|.
name|getDoOutput
argument_list|()
condition|)
block|{
name|conn
operator|.
name|setRequestProperty
argument_list|(
literal|"Expect"
argument_list|,
literal|"100-Continue"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|setInstanceFollowRedirects
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|conn
operator|.
name|connect
argument_list|()
expr_stmt|;
return|return
name|conn
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
DECL|method|run (final HttpOpParam.Op op, final Path fspath, final Param<?,?>... parameters)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|run
parameter_list|(
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
parameter_list|,
specifier|final
name|Path
name|fspath
parameter_list|,
specifier|final
name|Param
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
modifier|...
name|parameters
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HttpURLConnection
name|conn
init|=
name|httpConnect
argument_list|(
name|op
argument_list|,
name|fspath
argument_list|,
name|parameters
argument_list|)
decl_stmt|;
name|validateResponse
argument_list|(
name|op
argument_list|,
name|conn
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|jsonParse
argument_list|(
name|conn
operator|.
name|getInputStream
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|conn
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|applyUMask (FsPermission permission)
specifier|private
name|FsPermission
name|applyUMask
parameter_list|(
name|FsPermission
name|permission
parameter_list|)
block|{
if|if
condition|(
name|permission
operator|==
literal|null
condition|)
block|{
name|permission
operator|=
name|FsPermission
operator|.
name|getDefault
argument_list|()
expr_stmt|;
block|}
return|return
name|permission
operator|.
name|applyUMask
argument_list|(
name|FsPermission
operator|.
name|getUMask
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getHdfsFileStatus (Path f)
specifier|private
name|HdfsFileStatus
name|getHdfsFileStatus
parameter_list|(
name|Path
name|f
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|GetOpParam
operator|.
name|Op
operator|.
name|GETFILESTATUS
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|run
argument_list|(
name|op
argument_list|,
name|f
argument_list|)
decl_stmt|;
specifier|final
name|HdfsFileStatus
name|status
init|=
name|JsonUtil
operator|.
name|toFileStatus
argument_list|(
name|json
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
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
return|return
name|status
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
block|{
name|statistics
operator|.
name|incrementReadOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
name|makeQualified
argument_list|(
name|getHdfsFileStatus
argument_list|(
name|f
argument_list|)
argument_list|,
name|f
argument_list|)
return|;
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
name|getFullPath
argument_list|(
name|parent
argument_list|)
operator|.
name|makeQualified
argument_list|(
name|getUri
argument_list|()
argument_list|,
name|getWorkingDirectory
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|mkdirs (Path f, FsPermission permission)
specifier|public
name|boolean
name|mkdirs
parameter_list|(
name|Path
name|f
parameter_list|,
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|MKDIRS
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|run
argument_list|(
name|op
argument_list|,
name|f
argument_list|,
operator|new
name|PermissionParam
argument_list|(
name|applyUMask
argument_list|(
name|permission
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|json
operator|.
name|get
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rename (final Path src, final Path dst)
specifier|public
name|boolean
name|rename
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|,
specifier|final
name|Path
name|dst
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|RENAME
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|run
argument_list|(
name|op
argument_list|,
name|src
argument_list|,
operator|new
name|DstPathParam
argument_list|(
name|makeQualified
argument_list|(
name|dst
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|json
operator|.
name|get
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
DECL|method|rename (final Path src, final Path dst, final Options.Rename... options)
specifier|public
name|void
name|rename
parameter_list|(
specifier|final
name|Path
name|src
parameter_list|,
specifier|final
name|Path
name|dst
parameter_list|,
specifier|final
name|Options
operator|.
name|Rename
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|RENAME
decl_stmt|;
name|run
argument_list|(
name|op
argument_list|,
name|src
argument_list|,
operator|new
name|DstPathParam
argument_list|(
name|makeQualified
argument_list|(
name|dst
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|,
operator|new
name|RenameOptionSetParam
argument_list|(
name|options
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setOwner (final Path p, final String owner, final String group )
specifier|public
name|void
name|setOwner
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|,
specifier|final
name|String
name|owner
parameter_list|,
specifier|final
name|String
name|group
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|owner
operator|==
literal|null
operator|&&
name|group
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"owner == null&& group == null"
argument_list|)
throw|;
block|}
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|SETOWNER
decl_stmt|;
name|run
argument_list|(
name|op
argument_list|,
name|p
argument_list|,
operator|new
name|OwnerParam
argument_list|(
name|owner
argument_list|)
argument_list|,
operator|new
name|GroupParam
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setPermission (final Path p, final FsPermission permission )
specifier|public
name|void
name|setPermission
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|SETPERMISSION
decl_stmt|;
name|run
argument_list|(
name|op
argument_list|,
name|p
argument_list|,
operator|new
name|PermissionParam
argument_list|(
name|permission
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setReplication (final Path p, final short replication )
specifier|public
name|boolean
name|setReplication
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|,
specifier|final
name|short
name|replication
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|SETREPLICATION
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|run
argument_list|(
name|op
argument_list|,
name|p
argument_list|,
operator|new
name|ReplicationParam
argument_list|(
name|replication
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|json
operator|.
name|get
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setTimes (final Path p, final long mtime, final long atime )
specifier|public
name|void
name|setTimes
parameter_list|(
specifier|final
name|Path
name|p
parameter_list|,
specifier|final
name|long
name|mtime
parameter_list|,
specifier|final
name|long
name|atime
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|SETTIMES
decl_stmt|;
name|run
argument_list|(
name|op
argument_list|,
name|p
argument_list|,
operator|new
name|ModificationTimeParam
argument_list|(
name|mtime
argument_list|)
argument_list|,
operator|new
name|AccessTimeParam
argument_list|(
name|atime
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|write (final HttpOpParam.Op op, final HttpURLConnection conn, final int bufferSize)
specifier|private
name|FSDataOutputStream
name|write
parameter_list|(
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
parameter_list|,
specifier|final
name|HttpURLConnection
name|conn
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FSDataOutputStream
argument_list|(
operator|new
name|BufferedOutputStream
argument_list|(
name|conn
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|bufferSize
argument_list|)
argument_list|,
name|statistics
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|validateResponse
argument_list|(
name|op
argument_list|,
name|conn
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|create (final Path f, final FsPermission permission, final boolean overwrite, final int bufferSize, final short replication, final long blockSize, final Progressable progress)
specifier|public
name|FSDataOutputStream
name|create
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|FsPermission
name|permission
parameter_list|,
specifier|final
name|boolean
name|overwrite
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|,
specifier|final
name|short
name|replication
parameter_list|,
specifier|final
name|long
name|blockSize
parameter_list|,
specifier|final
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PutOpParam
operator|.
name|Op
operator|.
name|CREATE
decl_stmt|;
specifier|final
name|HttpURLConnection
name|conn
init|=
name|httpConnect
argument_list|(
name|op
argument_list|,
name|f
argument_list|,
operator|new
name|PermissionParam
argument_list|(
name|applyUMask
argument_list|(
name|permission
argument_list|)
argument_list|)
argument_list|,
operator|new
name|OverwriteParam
argument_list|(
name|overwrite
argument_list|)
argument_list|,
operator|new
name|BufferSizeParam
argument_list|(
name|bufferSize
argument_list|)
argument_list|,
operator|new
name|ReplicationParam
argument_list|(
name|replication
argument_list|)
argument_list|,
operator|new
name|BlockSizeParam
argument_list|(
name|blockSize
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|write
argument_list|(
name|op
argument_list|,
name|conn
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|append (final Path f, final int bufferSize, final Progressable progress)
specifier|public
name|FSDataOutputStream
name|append
parameter_list|(
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|int
name|bufferSize
parameter_list|,
specifier|final
name|Progressable
name|progress
parameter_list|)
throws|throws
name|IOException
block|{
name|statistics
operator|.
name|incrementWriteOps
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|PostOpParam
operator|.
name|Op
operator|.
name|APPEND
decl_stmt|;
specifier|final
name|HttpURLConnection
name|conn
init|=
name|httpConnect
argument_list|(
name|op
argument_list|,
name|f
argument_list|,
operator|new
name|BufferSizeParam
argument_list|(
name|bufferSize
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|write
argument_list|(
name|op
argument_list|,
name|conn
argument_list|,
name|bufferSize
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
block|{
specifier|final
name|HttpOpParam
operator|.
name|Op
name|op
init|=
name|DeleteOpParam
operator|.
name|Op
operator|.
name|DELETE
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|json
init|=
name|run
argument_list|(
name|op
argument_list|,
name|f
argument_list|,
operator|new
name|RecursiveParam
argument_list|(
name|recursive
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|Boolean
operator|)
name|json
operator|.
name|get
argument_list|(
name|op
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

