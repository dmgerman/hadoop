begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.aliasmap
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
name|aliasmap
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|BlockingService
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
name|ipc
operator|.
name|ProtobufRpcEngine
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
name|conf
operator|.
name|Configurable
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|Block
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
name|ProvidedStorageLocation
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
name|protocolPB
operator|.
name|AliasMapProtocolPB
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
name|protocolPB
operator|.
name|AliasMapProtocolServerSideTranslatorPB
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
name|RPC
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

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
name|Optional
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
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS_DEFAULT
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
name|hdfs
operator|.
name|protocol
operator|.
name|proto
operator|.
name|AliasMapProtocolProtos
operator|.
name|*
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
name|hdfs
operator|.
name|server
operator|.
name|aliasmap
operator|.
name|InMemoryAliasMap
operator|.
name|CheckedFunction
import|;
end_import

begin_comment
comment|/**  * InMemoryLevelDBAliasMapServer is the entry point from the Namenode into  * the {@link InMemoryAliasMap}.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|InMemoryLevelDBAliasMapServer
specifier|public
class|class
name|InMemoryLevelDBAliasMapServer
implements|implements
name|InMemoryAliasMapProtocol
implements|,
name|Configurable
implements|,
name|Closeable
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
name|InMemoryLevelDBAliasMapServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|initFun
specifier|private
specifier|final
name|CheckedFunction
argument_list|<
name|Configuration
argument_list|,
name|InMemoryAliasMap
argument_list|>
name|initFun
decl_stmt|;
DECL|field|aliasMapServer
specifier|private
name|RPC
operator|.
name|Server
name|aliasMapServer
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|aliasMap
specifier|private
name|InMemoryAliasMap
name|aliasMap
decl_stmt|;
DECL|field|blockPoolId
specifier|private
name|String
name|blockPoolId
decl_stmt|;
DECL|method|InMemoryLevelDBAliasMapServer ( CheckedFunction<Configuration, InMemoryAliasMap> initFun, String blockPoolId)
specifier|public
name|InMemoryLevelDBAliasMapServer
parameter_list|(
name|CheckedFunction
argument_list|<
name|Configuration
argument_list|,
name|InMemoryAliasMap
argument_list|>
name|initFun
parameter_list|,
name|String
name|blockPoolId
parameter_list|)
block|{
name|this
operator|.
name|initFun
operator|=
name|initFun
expr_stmt|;
name|this
operator|.
name|blockPoolId
operator|=
name|blockPoolId
expr_stmt|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unable to start "
operator|+
literal|"InMemoryLevelDBAliasMapServer as security is enabled"
argument_list|)
throw|;
block|}
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|getConf
argument_list|()
argument_list|,
name|AliasMapProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|AliasMapProtocolServerSideTranslatorPB
name|aliasMapProtocolXlator
init|=
operator|new
name|AliasMapProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|BlockingService
name|aliasMapProtocolService
init|=
name|AliasMapProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
name|aliasMapProtocolXlator
argument_list|)
decl_stmt|;
name|String
name|rpcAddress
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS
argument_list|,
name|DFS_PROVIDED_ALIASMAP_INMEMORY_RPC_ADDRESS_DEFAULT
argument_list|)
decl_stmt|;
name|String
index|[]
name|split
init|=
name|rpcAddress
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|String
name|bindHost
init|=
name|split
index|[
literal|0
index|]
decl_stmt|;
name|Integer
name|port
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|split
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|aliasMapServer
operator|=
operator|new
name|RPC
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|setProtocol
argument_list|(
name|AliasMapProtocolPB
operator|.
name|class
argument_list|)
operator|.
name|setInstance
argument_list|(
name|aliasMapProtocolService
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|bindHost
argument_list|)
operator|.
name|setPort
argument_list|(
name|port
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
literal|1
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting InMemoryLevelDBAliasMapServer on {}"
argument_list|,
name|rpcAddress
argument_list|)
expr_stmt|;
name|aliasMapServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|list (Optional<Block> marker)
specifier|public
name|InMemoryAliasMap
operator|.
name|IterationResult
name|list
parameter_list|(
name|Optional
argument_list|<
name|Block
argument_list|>
name|marker
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|aliasMap
operator|.
name|list
argument_list|(
name|marker
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
DECL|method|read (@onnull Block block)
specifier|public
name|Optional
argument_list|<
name|ProvidedStorageLocation
argument_list|>
name|read
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|aliasMap
operator|.
name|read
argument_list|(
name|block
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write (@onnull Block block, @Nonnull ProvidedStorageLocation providedStorageLocation)
specifier|public
name|void
name|write
parameter_list|(
annotation|@
name|Nonnull
name|Block
name|block
parameter_list|,
annotation|@
name|Nonnull
name|ProvidedStorageLocation
name|providedStorageLocation
parameter_list|)
throws|throws
name|IOException
block|{
name|aliasMap
operator|.
name|write
argument_list|(
name|block
argument_list|,
name|providedStorageLocation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBlockPoolId ()
specifier|public
name|String
name|getBlockPoolId
parameter_list|()
block|{
return|return
name|blockPoolId
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
try|try
block|{
name|this
operator|.
name|aliasMap
operator|=
name|initFun
operator|.
name|apply
argument_list|(
name|conf
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
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping InMemoryLevelDBAliasMapServer"
argument_list|)
expr_stmt|;
try|try
block|{
name|aliasMap
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|aliasMapServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

