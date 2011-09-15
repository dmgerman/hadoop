begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.web.resources
package|package
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
name|datanode
operator|.
name|web
operator|.
name|resources
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
name|EnumSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Consumes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|DefaultValue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|GET
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|POST
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PUT
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|PathParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|Produces
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|QueryParam
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|StreamingOutput
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
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|CreateFlag
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
name|DFSClient
operator|.
name|DFSDataInputStream
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
name|datanode
operator|.
name|DataNode
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
name|hdfs
operator|.
name|web
operator|.
name|WebHdfsFileSystem
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
name|LengthParam
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
name|OffsetParam
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
name|hdfs
operator|.
name|web
operator|.
name|resources
operator|.
name|UriFsPathParam
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

begin_comment
comment|/** Web-hdfs DataNode implementation. */
end_comment

begin_class
annotation|@
name|Path
argument_list|(
literal|""
argument_list|)
DECL|class|DatanodeWebHdfsMethods
specifier|public
class|class
name|DatanodeWebHdfsMethods
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DatanodeWebHdfsMethods
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
annotation|@
name|Context
name|ServletContext
name|context
decl_stmt|;
comment|/** Handle HTTP PUT request. */
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
literal|"{"
operator|+
name|UriFsPathParam
operator|.
name|NAME
operator|+
literal|":.*}"
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
literal|"*/*"
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|put ( final InputStream in, @PathParam(UriFsPathParam.NAME) final UriFsPathParam path, @QueryParam(PutOpParam.NAME) @DefaultValue(PutOpParam.DEFAULT) final PutOpParam op, @QueryParam(PermissionParam.NAME) @DefaultValue(PermissionParam.DEFAULT) final PermissionParam permission, @QueryParam(OverwriteParam.NAME) @DefaultValue(OverwriteParam.DEFAULT) final OverwriteParam overwrite, @QueryParam(BufferSizeParam.NAME) @DefaultValue(BufferSizeParam.DEFAULT) final BufferSizeParam bufferSize, @QueryParam(ReplicationParam.NAME) @DefaultValue(ReplicationParam.DEFAULT) final ReplicationParam replication, @QueryParam(BlockSizeParam.NAME) @DefaultValue(BlockSizeParam.DEFAULT) final BlockSizeParam blockSize )
specifier|public
name|Response
name|put
parameter_list|(
specifier|final
name|InputStream
name|in
parameter_list|,
annotation|@
name|PathParam
argument_list|(
name|UriFsPathParam
operator|.
name|NAME
argument_list|)
specifier|final
name|UriFsPathParam
name|path
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|PutOpParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|PutOpParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|PutOpParam
name|op
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|PermissionParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|PermissionParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|PermissionParam
name|permission
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|OverwriteParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|OverwriteParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|OverwriteParam
name|overwrite
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|BufferSizeParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|BufferSizeParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|BufferSizeParam
name|bufferSize
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|ReplicationParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|ReplicationParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|ReplicationParam
name|replication
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|BlockSizeParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|BlockSizeParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|BlockSizeParam
name|blockSize
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|op
operator|+
literal|": "
operator|+
name|path
operator|+
name|Param
operator|.
name|toSortedString
argument_list|(
literal|", "
argument_list|,
name|permission
argument_list|,
name|overwrite
argument_list|,
name|bufferSize
argument_list|,
name|replication
argument_list|,
name|blockSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|fullpath
init|=
name|path
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|final
name|DataNode
name|datanode
init|=
operator|(
name|DataNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|op
operator|.
name|getValue
argument_list|()
condition|)
block|{
case|case
name|CREATE
case|:
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|datanode
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|nnRpcAddr
init|=
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|DFSClient
name|dfsclient
init|=
operator|new
name|DFSClient
argument_list|(
name|nnRpcAddr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
operator|new
name|FSDataOutputStream
argument_list|(
name|dfsclient
operator|.
name|create
argument_list|(
name|fullpath
argument_list|,
name|permission
operator|.
name|getFsPermission
argument_list|()
argument_list|,
name|overwrite
operator|.
name|getValue
argument_list|()
condition|?
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|,
name|CreateFlag
operator|.
name|OVERWRITE
argument_list|)
else|:
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|,
name|replication
operator|.
name|getValue
argument_list|()
argument_list|,
name|blockSize
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|,
name|bufferSize
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|bufferSize
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|InetSocketAddress
name|nnHttpAddr
init|=
name|NameNode
operator|.
name|getHttpAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|WebHdfsFileSystem
operator|.
name|SCHEME
argument_list|,
literal|null
argument_list|,
name|nnHttpAddr
operator|.
name|getHostName
argument_list|()
argument_list|,
name|nnHttpAddr
operator|.
name|getPort
argument_list|()
argument_list|,
name|fullpath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|created
argument_list|(
name|uri
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|op
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
comment|/** Handle HTTP POST request. */
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
literal|"{"
operator|+
name|UriFsPathParam
operator|.
name|NAME
operator|+
literal|":.*}"
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
literal|"*/*"
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|post ( final InputStream in, @PathParam(UriFsPathParam.NAME) final UriFsPathParam path, @QueryParam(PostOpParam.NAME) @DefaultValue(PostOpParam.DEFAULT) final PostOpParam op, @QueryParam(BufferSizeParam.NAME) @DefaultValue(BufferSizeParam.DEFAULT) final BufferSizeParam bufferSize )
specifier|public
name|Response
name|post
parameter_list|(
specifier|final
name|InputStream
name|in
parameter_list|,
annotation|@
name|PathParam
argument_list|(
name|UriFsPathParam
operator|.
name|NAME
argument_list|)
specifier|final
name|UriFsPathParam
name|path
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|PostOpParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|PostOpParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|PostOpParam
name|op
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|BufferSizeParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|BufferSizeParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|BufferSizeParam
name|bufferSize
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|op
operator|+
literal|": "
operator|+
name|path
operator|+
name|Param
operator|.
name|toSortedString
argument_list|(
literal|", "
argument_list|,
name|bufferSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|fullpath
init|=
name|path
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|final
name|DataNode
name|datanode
init|=
operator|(
name|DataNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|op
operator|.
name|getValue
argument_list|()
condition|)
block|{
case|case
name|APPEND
case|:
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|datanode
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|nnRpcAddr
init|=
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|DFSClient
name|dfsclient
init|=
operator|new
name|DFSClient
argument_list|(
name|nnRpcAddr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|out
init|=
name|dfsclient
operator|.
name|append
argument_list|(
name|fullpath
argument_list|,
name|bufferSize
operator|.
name|getValue
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|bufferSize
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|ok
argument_list|()
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_JSON
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|op
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
comment|/** Handle HTTP GET request. */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"{"
operator|+
name|UriFsPathParam
operator|.
name|NAME
operator|+
literal|":.*}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_OCTET_STREAM
block|,
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|get ( @athParamUriFsPathParam.NAME) final UriFsPathParam path, @QueryParam(GetOpParam.NAME) @DefaultValue(GetOpParam.DEFAULT) final GetOpParam op, @QueryParam(OffsetParam.NAME) @DefaultValue(OffsetParam.DEFAULT) final OffsetParam offset, @QueryParam(LengthParam.NAME) @DefaultValue(LengthParam.DEFAULT) final LengthParam length, @QueryParam(BufferSizeParam.NAME) @DefaultValue(BufferSizeParam.DEFAULT) final BufferSizeParam bufferSize )
specifier|public
name|Response
name|get
parameter_list|(
annotation|@
name|PathParam
argument_list|(
name|UriFsPathParam
operator|.
name|NAME
argument_list|)
specifier|final
name|UriFsPathParam
name|path
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|GetOpParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|GetOpParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|GetOpParam
name|op
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|OffsetParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|OffsetParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|OffsetParam
name|offset
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|LengthParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|LengthParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|LengthParam
name|length
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
name|BufferSizeParam
operator|.
name|NAME
argument_list|)
annotation|@
name|DefaultValue
argument_list|(
name|BufferSizeParam
operator|.
name|DEFAULT
argument_list|)
specifier|final
name|BufferSizeParam
name|bufferSize
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
name|op
operator|+
literal|": "
operator|+
name|path
operator|+
name|Param
operator|.
name|toSortedString
argument_list|(
literal|", "
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
name|bufferSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|fullpath
init|=
name|path
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|final
name|DataNode
name|datanode
init|=
operator|(
name|DataNode
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
literal|"datanode"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|op
operator|.
name|getValue
argument_list|()
condition|)
block|{
case|case
name|OPEN
case|:
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|datanode
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|nnRpcAddr
init|=
name|NameNode
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|DFSClient
name|dfsclient
init|=
operator|new
name|DFSClient
argument_list|(
name|nnRpcAddr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|DFSDataInputStream
name|in
init|=
operator|new
name|DFSClient
operator|.
name|DFSDataInputStream
argument_list|(
name|dfsclient
operator|.
name|open
argument_list|(
name|fullpath
argument_list|,
name|bufferSize
operator|.
name|getValue
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|in
operator|.
name|seek
argument_list|(
name|offset
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|StreamingOutput
name|streaming
init|=
operator|new
name|StreamingOutput
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Long
name|n
init|=
name|length
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|bufferSize
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|copyBytes
argument_list|(
name|in
argument_list|,
name|out
argument_list|,
name|n
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|streaming
argument_list|)
operator|.
name|type
argument_list|(
name|MediaType
operator|.
name|APPLICATION_OCTET_STREAM
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|op
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

