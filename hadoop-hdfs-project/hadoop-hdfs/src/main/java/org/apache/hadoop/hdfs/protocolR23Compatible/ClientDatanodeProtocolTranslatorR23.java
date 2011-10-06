begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolR23Compatible
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolR23Compatible
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|SocketFactory
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
name|CommonConfigurationKeysPublic
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
name|ClientDatanodeProtocol
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
name|DatanodeID
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
name|ExtendedBlock
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
name|LocatedBlock
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
name|ProtocolSignature
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
name|net
operator|.
name|NetUtils
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

begin_comment
comment|/**  * This class forwards ClientDatanodeProtocol calls as RPC to the DN server  * while translating from the parameter types used in ClientDatanodeProtocol to  * those used in protocolR23Compatile.*.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|ClientDatanodeProtocolTranslatorR23
specifier|public
class|class
name|ClientDatanodeProtocolTranslatorR23
implements|implements
name|ClientDatanodeProtocol
block|{
DECL|field|rpcProxy
specifier|final
specifier|private
name|ClientDatanodeWireProtocol
name|rpcProxy
decl_stmt|;
DECL|method|ClientDatanodeProtocolTranslatorR23 (DatanodeID datanodeid, Configuration conf, int socketTimeout, LocatedBlock locatedBlock)
specifier|public
name|ClientDatanodeProtocolTranslatorR23
parameter_list|(
name|DatanodeID
name|datanodeid
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|LocatedBlock
name|locatedBlock
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcProxy
operator|=
name|createClientDatanodeProtocolProxy
argument_list|(
name|datanodeid
argument_list|,
name|conf
argument_list|,
name|socketTimeout
argument_list|,
name|locatedBlock
argument_list|)
expr_stmt|;
block|}
comment|/** used for testing */
DECL|method|ClientDatanodeProtocolTranslatorR23 (InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|public
name|ClientDatanodeProtocolTranslatorR23
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcProxy
operator|=
name|createClientDatanodeProtocolProxy
argument_list|(
name|addr
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
expr_stmt|;
block|}
DECL|method|createClientDatanodeProtocolProxy ( DatanodeID datanodeid, Configuration conf, int socketTimeout, LocatedBlock locatedBlock)
specifier|static
name|ClientDatanodeWireProtocol
name|createClientDatanodeProtocolProxy
parameter_list|(
name|DatanodeID
name|datanodeid
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|socketTimeout
parameter_list|,
name|LocatedBlock
name|locatedBlock
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|addr
init|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|datanodeid
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|datanodeid
operator|.
name|getIpcPort
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ClientDatanodeWireProtocol
operator|.
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|ClientDatanodeWireProtocol
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"ClientDatanodeProtocol addr="
operator|+
name|addr
argument_list|)
expr_stmt|;
block|}
comment|// Since we're creating a new UserGroupInformation here, we know that no
comment|// future RPC proxies will be able to re-use the same connection. And
comment|// usages of this proxy tend to be one-off calls.
comment|//
comment|// This is a temporary fix: callers should really achieve this by using
comment|// RPC.stopProxy() on the resulting object, but this is currently not
comment|// working in trunk. See the discussion on HDFS-1965.
name|Configuration
name|confWithNoIpcIdle
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|confWithNoIpcIdle
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECTION_MAXIDLETIME_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ticket
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|locatedBlock
operator|.
name|getBlock
argument_list|()
operator|.
name|getLocalBlock
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ticket
operator|.
name|addToken
argument_list|(
name|locatedBlock
operator|.
name|getBlockToken
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|ClientDatanodeWireProtocol
operator|.
name|class
argument_list|,
name|ClientDatanodeWireProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|ticket
argument_list|,
name|confWithNoIpcIdle
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|,
name|socketTimeout
argument_list|)
return|;
block|}
DECL|method|createClientDatanodeProtocolProxy ( InetSocketAddress addr, UserGroupInformation ticket, Configuration conf, SocketFactory factory)
specifier|static
name|ClientDatanodeWireProtocol
name|createClientDatanodeProtocolProxy
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|UserGroupInformation
name|ticket
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SocketFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|RPC
operator|.
name|getProxy
argument_list|(
name|ClientDatanodeWireProtocol
operator|.
name|class
argument_list|,
name|ClientDatanodeWireProtocol
operator|.
name|versionID
argument_list|,
name|addr
argument_list|,
name|ticket
argument_list|,
name|conf
argument_list|,
name|factory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature ( String protocolName, long clientVersion, int clientMethodHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocolName
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodHash
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ProtocolSignatureWritable
operator|.
name|convert
argument_list|(
name|rpcProxy
operator|.
name|getProtocolSignature2
argument_list|(
name|protocolName
argument_list|,
name|clientVersion
argument_list|,
name|clientMethodHash
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String protocolName, long clientVersion)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|protocolName
parameter_list|,
name|long
name|clientVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|rpcProxy
operator|.
name|getProtocolVersion
argument_list|(
name|protocolName
argument_list|,
name|clientVersion
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReplicaVisibleLength (ExtendedBlock b)
specifier|public
name|long
name|getReplicaVisibleLength
parameter_list|(
name|ExtendedBlock
name|b
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|rpcProxy
operator|.
name|getReplicaVisibleLength
argument_list|(
name|ExtendedBlockWritable
operator|.
name|convertExtendedBlock
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|refreshNamenodes ()
specifier|public
name|void
name|refreshNamenodes
parameter_list|()
throws|throws
name|IOException
block|{
name|rpcProxy
operator|.
name|refreshNamenodes
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteBlockPool (String bpid, boolean force)
specifier|public
name|void
name|deleteBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|rpcProxy
operator|.
name|deleteBlockPool
argument_list|(
name|bpid
argument_list|,
name|force
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

