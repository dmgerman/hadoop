begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.replication
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|replication
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
name|FileOutputStream
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|CopyContainerRequestProto
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|ContainerProtos
operator|.
name|CopyContainerResponseProto
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|IntraDatanodeProtocolServiceGrpc
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
name|hdds
operator|.
name|protocol
operator|.
name|datanode
operator|.
name|proto
operator|.
name|IntraDatanodeProtocolServiceGrpc
operator|.
name|IntraDatanodeProtocolServiceStub
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|ratis
operator|.
name|shaded
operator|.
name|io
operator|.
name|grpc
operator|.
name|ManagedChannel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|io
operator|.
name|grpc
operator|.
name|netty
operator|.
name|NettyChannelBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|ratis
operator|.
name|shaded
operator|.
name|io
operator|.
name|grpc
operator|.
name|stub
operator|.
name|StreamObserver
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

begin_comment
comment|/**  * Client to read container data from Grpc.  */
end_comment

begin_class
DECL|class|GrpcReplicationClient
specifier|public
class|class
name|GrpcReplicationClient
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
name|GrpcReplicationClient
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|ManagedChannel
name|channel
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|IntraDatanodeProtocolServiceStub
name|client
decl_stmt|;
DECL|field|workingDirectory
specifier|private
specifier|final
name|Path
name|workingDirectory
decl_stmt|;
DECL|method|GrpcReplicationClient (String host, int port, Path workingDir)
specifier|public
name|GrpcReplicationClient
parameter_list|(
name|String
name|host
parameter_list|,
name|int
name|port
parameter_list|,
name|Path
name|workingDir
parameter_list|)
block|{
name|channel
operator|=
name|NettyChannelBuilder
operator|.
name|forAddress
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
operator|.
name|usePlaintext
argument_list|()
operator|.
name|maxInboundMessageSize
argument_list|(
name|OzoneConfigKeys
operator|.
name|DFS_CONTAINER_CHUNK_MAX_SIZE
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|client
operator|=
name|IntraDatanodeProtocolServiceGrpc
operator|.
name|newStub
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|this
operator|.
name|workingDirectory
operator|=
name|workingDir
expr_stmt|;
block|}
DECL|method|download (long containerId)
specifier|public
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
name|download
parameter_list|(
name|long
name|containerId
parameter_list|)
block|{
name|CopyContainerRequestProto
name|request
init|=
name|CopyContainerRequestProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|setContainerID
argument_list|(
name|containerId
argument_list|)
operator|.
name|setLen
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|setReadOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
name|response
init|=
operator|new
name|CompletableFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|Path
name|destinationPath
init|=
name|getWorkingDirectory
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"container-"
operator|+
name|containerId
operator|+
literal|".tar.gz"
argument_list|)
decl_stmt|;
name|client
operator|.
name|download
argument_list|(
name|request
argument_list|,
operator|new
name|StreamDownloader
argument_list|(
name|containerId
argument_list|,
name|response
argument_list|,
name|destinationPath
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|getWorkingDirectory ()
specifier|private
name|Path
name|getWorkingDirectory
parameter_list|()
block|{
return|return
name|workingDirectory
return|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|channel
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Grpc stream observer to ComletableFuture adapter.    */
DECL|class|StreamDownloader
specifier|public
specifier|static
class|class
name|StreamDownloader
implements|implements
name|StreamObserver
argument_list|<
name|CopyContainerResponseProto
argument_list|>
block|{
DECL|field|response
specifier|private
specifier|final
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
name|response
decl_stmt|;
DECL|field|containerId
specifier|private
specifier|final
name|long
name|containerId
decl_stmt|;
DECL|field|stream
specifier|private
name|BufferedOutputStream
name|stream
decl_stmt|;
DECL|field|outputPath
specifier|private
name|Path
name|outputPath
decl_stmt|;
DECL|method|StreamDownloader (long containerId, CompletableFuture<Path> response, Path outputPath)
specifier|public
name|StreamDownloader
parameter_list|(
name|long
name|containerId
parameter_list|,
name|CompletableFuture
argument_list|<
name|Path
argument_list|>
name|response
parameter_list|,
name|Path
name|outputPath
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
name|containerId
expr_stmt|;
name|this
operator|.
name|outputPath
operator|=
name|outputPath
expr_stmt|;
try|try
block|{
name|outputPath
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|outputPath
argument_list|)
expr_stmt|;
name|Path
name|parentPath
init|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|outputPath
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
name|Files
operator|.
name|createDirectories
argument_list|(
name|parentPath
argument_list|)
expr_stmt|;
name|stream
operator|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outputPath
operator|.
name|toFile
argument_list|()
argument_list|)
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
name|RuntimeException
argument_list|(
literal|"OutputPath can't be used: "
operator|+
name|outputPath
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|onNext (CopyContainerResponseProto chunk)
specifier|public
name|void
name|onNext
parameter_list|(
name|CopyContainerResponseProto
name|chunk
parameter_list|)
block|{
try|try
block|{
name|stream
operator|.
name|write
argument_list|(
name|chunk
operator|.
name|getData
argument_list|()
operator|.
name|toByteArray
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
name|response
operator|.
name|completeExceptionally
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onError (Throwable throwable)
specifier|public
name|void
name|onError
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Container download was unsuccessfull"
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error happened during the download but can't delete the "
operator|+
literal|"temporary destination."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|completeExceptionally
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|response
operator|.
name|completeExceptionally
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onCompleted ()
specifier|public
name|void
name|onCompleted
parameter_list|()
block|{
try|try
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Container is downloaded to {}"
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|response
operator|.
name|complete
argument_list|(
name|outputPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|response
operator|.
name|completeExceptionally
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

