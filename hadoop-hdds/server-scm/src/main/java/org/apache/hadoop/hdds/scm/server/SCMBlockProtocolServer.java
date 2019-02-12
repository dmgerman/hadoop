begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license  * agreements. See the NOTICE file distributed with this work for additional  * information regarding  * copyright ownership. The ASF licenses this file to you under the Apache  * License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the  * License. You may obtain a  * copy of the License at  *  *<p>http://www.apache.org/licenses/LICENSE-2.0  *  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR  * CONDITIONS OF ANY KIND, either  * express or implied. See the License for the specific language governing  * permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|server
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
name|collect
operator|.
name|Maps
import|;
end_import

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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|proto
operator|.
name|HddsProtos
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
name|proto
operator|.
name|ScmBlockLocationProtocolProtos
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
name|scm
operator|.
name|HddsServerUtil
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
name|scm
operator|.
name|ScmInfo
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|DeleteBlockResult
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
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|scm
operator|.
name|protocol
operator|.
name|ScmBlockLocationProtocol
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
name|scm
operator|.
name|protocolPB
operator|.
name|ScmBlockLocationProtocolPB
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
name|ipc
operator|.
name|RPC
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
name|Server
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
name|audit
operator|.
name|AuditAction
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
name|audit
operator|.
name|AuditEventStatus
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
name|audit
operator|.
name|AuditLogger
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
name|audit
operator|.
name|AuditLoggerType
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
name|audit
operator|.
name|AuditMessage
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
name|audit
operator|.
name|Auditor
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
name|audit
operator|.
name|SCMAction
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
name|common
operator|.
name|BlockGroup
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
name|client
operator|.
name|BlockID
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
name|common
operator|.
name|DeleteBlockGroupResult
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
name|protocolPB
operator|.
name|ScmBlockLocationProtocolServerSideTranslatorPB
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
name|InetSocketAddress
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
name|Map
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HANDLER_COUNT_DEFAULT
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_HANDLER_COUNT_KEY
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
name|hdds
operator|.
name|server
operator|.
name|ServerUtils
operator|.
name|updateRPCListenAddress
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
name|hdds
operator|.
name|scm
operator|.
name|server
operator|.
name|StorageContainerManager
operator|.
name|startRpcServer
import|;
end_import

begin_comment
comment|/**  * SCM block protocol is the protocol used by Namenode and OzoneManager to get  * blocks from the SCM.  */
end_comment

begin_class
DECL|class|SCMBlockProtocolServer
specifier|public
class|class
name|SCMBlockProtocolServer
implements|implements
name|ScmBlockLocationProtocol
implements|,
name|Auditor
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
name|SCMBlockProtocolServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|AUDIT
specifier|private
specifier|static
specifier|final
name|AuditLogger
name|AUDIT
init|=
operator|new
name|AuditLogger
argument_list|(
name|AuditLoggerType
operator|.
name|SCMLOGGER
argument_list|)
decl_stmt|;
DECL|field|scm
specifier|private
specifier|final
name|StorageContainerManager
name|scm
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|OzoneConfiguration
name|conf
decl_stmt|;
DECL|field|blockRpcServer
specifier|private
specifier|final
name|RPC
operator|.
name|Server
name|blockRpcServer
decl_stmt|;
DECL|field|blockRpcAddress
specifier|private
specifier|final
name|InetSocketAddress
name|blockRpcAddress
decl_stmt|;
comment|/**    * The RPC server that listens to requests from block service clients.    */
DECL|method|SCMBlockProtocolServer (OzoneConfiguration conf, StorageContainerManager scm)
specifier|public
name|SCMBlockProtocolServer
parameter_list|(
name|OzoneConfiguration
name|conf
parameter_list|,
name|StorageContainerManager
name|scm
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scm
operator|=
name|scm
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
specifier|final
name|int
name|handlerCount
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_HANDLER_COUNT_KEY
argument_list|,
name|OZONE_SCM_HANDLER_COUNT_DEFAULT
argument_list|)
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|ScmBlockLocationProtocolPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// SCM Block Service RPC.
name|BlockingService
name|blockProtoPbService
init|=
name|ScmBlockLocationProtocolProtos
operator|.
name|ScmBlockLocationProtocolService
operator|.
name|newReflectiveBlockingService
argument_list|(
operator|new
name|ScmBlockLocationProtocolServerSideTranslatorPB
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|scmBlockAddress
init|=
name|HddsServerUtil
operator|.
name|getScmBlockClientBindAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|blockRpcServer
operator|=
name|startRpcServer
argument_list|(
name|conf
argument_list|,
name|scmBlockAddress
argument_list|,
name|ScmBlockLocationProtocolPB
operator|.
name|class
argument_list|,
name|blockProtoPbService
argument_list|,
name|handlerCount
argument_list|)
expr_stmt|;
name|blockRpcAddress
operator|=
name|updateRPCListenAddress
argument_list|(
name|conf
argument_list|,
name|OZONE_SCM_BLOCK_CLIENT_ADDRESS_KEY
argument_list|,
name|scmBlockAddress
argument_list|,
name|blockRpcServer
argument_list|)
expr_stmt|;
block|}
DECL|method|getBlockRpcServer ()
specifier|public
name|RPC
operator|.
name|Server
name|getBlockRpcServer
parameter_list|()
block|{
return|return
name|blockRpcServer
return|;
block|}
DECL|method|getBlockRpcAddress ()
specifier|public
name|InetSocketAddress
name|getBlockRpcAddress
parameter_list|()
block|{
return|return
name|blockRpcAddress
return|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
name|StorageContainerManager
operator|.
name|buildRpcServerStartMessage
argument_list|(
literal|"RPC server for Block Protocol"
argument_list|,
name|getBlockRpcAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|getBlockRpcServer
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping the RPC server for Block Protocol"
argument_list|)
expr_stmt|;
name|getBlockRpcServer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Block Protocol RPC stop failed."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|cleanupWithLogger
argument_list|(
name|LOG
argument_list|,
name|scm
operator|.
name|getScmNodeManager
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|join ()
specifier|public
name|void
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Join RPC server for Block Protocol"
argument_list|)
expr_stmt|;
name|getBlockRpcServer
argument_list|()
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|allocateBlock (long size, HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, String owner)
specifier|public
name|AllocatedBlock
name|allocateBlock
parameter_list|(
name|long
name|size
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|String
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
name|type
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
literal|"factor"
argument_list|,
name|factor
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
literal|"owner"
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|boolean
name|auditSuccess
init|=
literal|true
decl_stmt|;
try|try
block|{
return|return
name|scm
operator|.
name|getScmBlockManager
argument_list|()
operator|.
name|allocateBlock
argument_list|(
name|size
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|owner
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|auditSuccess
operator|=
literal|false
expr_stmt|;
name|AUDIT
operator|.
name|logWriteFailure
argument_list|(
name|buildAuditMessageForFailure
argument_list|(
name|SCMAction
operator|.
name|ALLOCATE_BLOCK
argument_list|,
name|auditMap
argument_list|,
name|ex
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|auditSuccess
condition|)
block|{
name|AUDIT
operator|.
name|logWriteSuccess
argument_list|(
name|buildAuditMessageForSuccess
argument_list|(
name|SCMAction
operator|.
name|ALLOCATE_BLOCK
argument_list|,
name|auditMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Delete blocks for a set of object keys.    *    * @param keyBlocksInfoList list of block keys with object keys to delete.    * @return deletion results.    */
annotation|@
name|Override
DECL|method|deleteKeyBlocks ( List<BlockGroup> keyBlocksInfoList)
specifier|public
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|deleteKeyBlocks
parameter_list|(
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|keyBlocksInfoList
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"SCM is informed by OM to delete {} blocks"
argument_list|,
name|keyBlocksInfoList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|BlockGroup
name|keyBlocks
range|:
name|keyBlocksInfoList
control|)
block|{
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
operator|.
name|Result
name|resultCode
decl_stmt|;
try|try
block|{
comment|// We delete blocks in an atomic operation to prevent getting
comment|// into state like only a partial of blocks are deleted,
comment|// which will leave key in an inconsistent state.
name|auditMap
operator|.
name|put
argument_list|(
literal|"keyBlockToDelete"
argument_list|,
name|keyBlocks
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|scm
operator|.
name|getScmBlockManager
argument_list|()
operator|.
name|deleteBlocks
argument_list|(
name|keyBlocks
operator|.
name|getBlockIDList
argument_list|()
argument_list|)
expr_stmt|;
name|resultCode
operator|=
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
operator|.
name|Result
operator|.
name|success
expr_stmt|;
name|AUDIT
operator|.
name|logWriteSuccess
argument_list|(
name|buildAuditMessageForSuccess
argument_list|(
name|SCMAction
operator|.
name|DELETE_KEY_BLOCK
argument_list|,
name|auditMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SCMException
name|scmEx
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Fail to delete block: {}"
argument_list|,
name|keyBlocks
operator|.
name|getGroupID
argument_list|()
argument_list|,
name|scmEx
argument_list|)
expr_stmt|;
name|AUDIT
operator|.
name|logWriteFailure
argument_list|(
name|buildAuditMessageForFailure
argument_list|(
name|SCMAction
operator|.
name|DELETE_KEY_BLOCK
argument_list|,
name|auditMap
argument_list|,
name|scmEx
argument_list|)
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|scmEx
operator|.
name|getResult
argument_list|()
condition|)
block|{
case|case
name|CHILL_MODE_EXCEPTION
case|:
name|resultCode
operator|=
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
operator|.
name|Result
operator|.
name|chillMode
expr_stmt|;
break|break;
case|case
name|FAILED_TO_FIND_BLOCK
case|:
name|resultCode
operator|=
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
operator|.
name|Result
operator|.
name|errorNotFound
expr_stmt|;
break|break;
default|default:
name|resultCode
operator|=
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
operator|.
name|Result
operator|.
name|unknownFailure
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Fail to delete blocks for object key: {}"
argument_list|,
name|keyBlocks
operator|.
name|getGroupID
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|AUDIT
operator|.
name|logWriteFailure
argument_list|(
name|buildAuditMessageForFailure
argument_list|(
name|SCMAction
operator|.
name|DELETE_KEY_BLOCK
argument_list|,
name|auditMap
argument_list|,
name|ex
argument_list|)
argument_list|)
expr_stmt|;
name|resultCode
operator|=
name|ScmBlockLocationProtocolProtos
operator|.
name|DeleteScmBlockResult
operator|.
name|Result
operator|.
name|unknownFailure
expr_stmt|;
block|}
name|List
argument_list|<
name|DeleteBlockResult
argument_list|>
name|blockResultList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|BlockID
name|blockKey
range|:
name|keyBlocks
operator|.
name|getBlockIDList
argument_list|()
control|)
block|{
name|blockResultList
operator|.
name|add
argument_list|(
operator|new
name|DeleteBlockResult
argument_list|(
name|blockKey
argument_list|,
name|resultCode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|results
operator|.
name|add
argument_list|(
operator|new
name|DeleteBlockGroupResult
argument_list|(
name|keyBlocks
operator|.
name|getGroupID
argument_list|()
argument_list|,
name|blockResultList
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
annotation|@
name|Override
DECL|method|getScmInfo ()
specifier|public
name|ScmInfo
name|getScmInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|auditSuccess
init|=
literal|true
decl_stmt|;
try|try
block|{
name|ScmInfo
operator|.
name|Builder
name|builder
init|=
operator|new
name|ScmInfo
operator|.
name|Builder
argument_list|()
operator|.
name|setClusterId
argument_list|(
name|scm
operator|.
name|getScmStorageConfig
argument_list|()
operator|.
name|getClusterID
argument_list|()
argument_list|)
operator|.
name|setScmId
argument_list|(
name|scm
operator|.
name|getScmStorageConfig
argument_list|()
operator|.
name|getScmId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|auditSuccess
operator|=
literal|false
expr_stmt|;
name|AUDIT
operator|.
name|logReadFailure
argument_list|(
name|buildAuditMessageForFailure
argument_list|(
name|SCMAction
operator|.
name|GET_SCM_INFO
argument_list|,
literal|null
argument_list|,
name|ex
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|auditSuccess
condition|)
block|{
name|AUDIT
operator|.
name|logReadSuccess
argument_list|(
name|buildAuditMessageForSuccess
argument_list|(
name|SCMAction
operator|.
name|GET_SCM_INFO
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|buildAuditMessageForSuccess ( AuditAction op, Map<String, String> auditMap)
specifier|public
name|AuditMessage
name|buildAuditMessageForSuccess
parameter_list|(
name|AuditAction
name|op
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
parameter_list|)
block|{
return|return
operator|new
name|AuditMessage
operator|.
name|Builder
argument_list|()
operator|.
name|setUser
argument_list|(
operator|(
name|Server
operator|.
name|getRemoteUser
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|Server
operator|.
name|getRemoteUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|atIp
argument_list|(
operator|(
name|Server
operator|.
name|getRemoteIp
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|Server
operator|.
name|getRemoteIp
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
operator|.
name|forOperation
argument_list|(
name|op
operator|.
name|getAction
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|auditMap
argument_list|)
operator|.
name|withResult
argument_list|(
name|AuditEventStatus
operator|.
name|SUCCESS
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withException
argument_list|(
literal|null
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|buildAuditMessageForFailure (AuditAction op, Map<String, String> auditMap, Throwable throwable)
specifier|public
name|AuditMessage
name|buildAuditMessageForFailure
parameter_list|(
name|AuditAction
name|op
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
return|return
operator|new
name|AuditMessage
operator|.
name|Builder
argument_list|()
operator|.
name|setUser
argument_list|(
operator|(
name|Server
operator|.
name|getRemoteUser
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|Server
operator|.
name|getRemoteUser
argument_list|()
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|.
name|atIp
argument_list|(
operator|(
name|Server
operator|.
name|getRemoteIp
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|Server
operator|.
name|getRemoteIp
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
operator|.
name|forOperation
argument_list|(
name|op
operator|.
name|getAction
argument_list|()
argument_list|)
operator|.
name|withParams
argument_list|(
name|auditMap
argument_list|)
operator|.
name|withResult
argument_list|(
name|AuditEventStatus
operator|.
name|FAILURE
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|withException
argument_list|(
name|throwable
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

