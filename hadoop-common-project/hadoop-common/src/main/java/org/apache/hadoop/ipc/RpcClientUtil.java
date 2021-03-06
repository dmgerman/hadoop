begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
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
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|ConcurrentHashMap
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
name|ipc
operator|.
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|GetProtocolSignatureRequestProto
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
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|GetProtocolSignatureResponseProto
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
name|protobuf
operator|.
name|ProtocolInfoProtos
operator|.
name|ProtocolSignatureProto
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
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
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
name|ServiceException
import|;
end_import

begin_comment
comment|/**  * This class maintains a cache of protocol versions and corresponding protocol  * signatures, keyed by server address, protocol and rpc kind.  * The cache is lazily populated.   */
end_comment

begin_class
DECL|class|RpcClientUtil
specifier|public
class|class
name|RpcClientUtil
block|{
DECL|field|NULL_CONTROLLER
specifier|private
specifier|static
name|RpcController
name|NULL_CONTROLLER
init|=
literal|null
decl_stmt|;
DECL|field|PRIME
specifier|private
specifier|static
specifier|final
name|int
name|PRIME
init|=
literal|16777619
decl_stmt|;
DECL|class|ProtoSigCacheKey
specifier|private
specifier|static
class|class
name|ProtoSigCacheKey
block|{
DECL|field|serverAddress
specifier|private
name|InetSocketAddress
name|serverAddress
decl_stmt|;
DECL|field|protocol
specifier|private
name|String
name|protocol
decl_stmt|;
DECL|field|rpcKind
specifier|private
name|String
name|rpcKind
decl_stmt|;
DECL|method|ProtoSigCacheKey (InetSocketAddress addr, String p, String rk)
name|ProtoSigCacheKey
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|p
parameter_list|,
name|String
name|rk
parameter_list|)
block|{
name|this
operator|.
name|serverAddress
operator|=
name|addr
expr_stmt|;
name|this
operator|.
name|protocol
operator|=
name|p
expr_stmt|;
name|this
operator|.
name|rpcKind
operator|=
name|rk
expr_stmt|;
block|}
annotation|@
name|Override
comment|//Object
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|PRIME
operator|*
name|result
operator|+
operator|(
operator|(
name|serverAddress
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|serverAddress
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|PRIME
operator|*
name|result
operator|+
operator|(
operator|(
name|protocol
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|protocol
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|PRIME
operator|*
name|result
operator|+
operator|(
operator|(
name|rpcKind
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|rpcKind
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
comment|//Object
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|ProtoSigCacheKey
condition|)
block|{
name|ProtoSigCacheKey
name|otherKey
init|=
operator|(
name|ProtoSigCacheKey
operator|)
name|other
decl_stmt|;
return|return
operator|(
name|serverAddress
operator|.
name|equals
argument_list|(
name|otherKey
operator|.
name|serverAddress
argument_list|)
operator|&&
name|protocol
operator|.
name|equals
argument_list|(
name|otherKey
operator|.
name|protocol
argument_list|)
operator|&&
name|rpcKind
operator|.
name|equals
argument_list|(
name|otherKey
operator|.
name|rpcKind
argument_list|)
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|ConcurrentHashMap
argument_list|<
name|ProtoSigCacheKey
argument_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
argument_list|>
DECL|field|signatureMap
name|signatureMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|ProtoSigCacheKey
argument_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|putVersionSignatureMap (InetSocketAddress addr, String protocol, String rpcKind, Map<Long, ProtocolSignature> map)
specifier|private
specifier|static
name|void
name|putVersionSignatureMap
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|protocol
parameter_list|,
name|String
name|rpcKind
parameter_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
name|map
parameter_list|)
block|{
name|signatureMap
operator|.
name|put
argument_list|(
operator|new
name|ProtoSigCacheKey
argument_list|(
name|addr
argument_list|,
name|protocol
argument_list|,
name|rpcKind
argument_list|)
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|getVersionSignatureMap ( InetSocketAddress addr, String protocol, String rpcKind)
specifier|private
specifier|static
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
name|getVersionSignatureMap
parameter_list|(
name|InetSocketAddress
name|addr
parameter_list|,
name|String
name|protocol
parameter_list|,
name|String
name|rpcKind
parameter_list|)
block|{
return|return
name|signatureMap
operator|.
name|get
argument_list|(
operator|new
name|ProtoSigCacheKey
argument_list|(
name|addr
argument_list|,
name|protocol
argument_list|,
name|rpcKind
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns whether the given method is supported or not.    * The protocol signatures are fetched and cached. The connection id for the    * proxy provided is re-used.    * @param rpcProxy Proxy which provides an existing connection id.    * @param protocol Protocol for which the method check is required.    * @param rpcKind The RpcKind for which the method check is required.    * @param version The version at the client.    * @param methodName Name of the method.    * @return true if the method is supported, false otherwise.    * @throws IOException    */
DECL|method|isMethodSupported (Object rpcProxy, Class<?> protocol, RPC.RpcKind rpcKind, long version, String methodName)
specifier|public
specifier|static
name|boolean
name|isMethodSupported
parameter_list|(
name|Object
name|rpcProxy
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|RPC
operator|.
name|RpcKind
name|rpcKind
parameter_list|,
name|long
name|version
parameter_list|,
name|String
name|methodName
parameter_list|)
throws|throws
name|IOException
block|{
name|InetSocketAddress
name|serverAddress
init|=
name|RPC
operator|.
name|getServerAddress
argument_list|(
name|rpcProxy
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
name|versionMap
init|=
name|getVersionSignatureMap
argument_list|(
name|serverAddress
argument_list|,
name|protocol
operator|.
name|getName
argument_list|()
argument_list|,
name|rpcKind
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|versionMap
operator|==
literal|null
condition|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|ProtocolMetaInfoPB
operator|.
name|class
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|ProtocolMetaInfoPB
name|protocolInfoProxy
init|=
name|getProtocolMetaInfoProxy
argument_list|(
name|rpcProxy
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|GetProtocolSignatureRequestProto
operator|.
name|Builder
name|builder
init|=
name|GetProtocolSignatureRequestProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|setProtocol
argument_list|(
name|protocol
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setRpcKind
argument_list|(
name|rpcKind
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|GetProtocolSignatureResponseProto
name|resp
decl_stmt|;
try|try
block|{
name|resp
operator|=
name|protocolInfoProxy
operator|.
name|getProtocolSignature
argument_list|(
name|NULL_CONTROLLER
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
throw|throw
name|ProtobufHelper
operator|.
name|getRemoteException
argument_list|(
name|se
argument_list|)
throw|;
block|}
name|versionMap
operator|=
name|convertProtocolSignatureProtos
argument_list|(
name|resp
operator|.
name|getProtocolSignatureList
argument_list|()
argument_list|)
expr_stmt|;
name|putVersionSignatureMap
argument_list|(
name|serverAddress
argument_list|,
name|protocol
operator|.
name|getName
argument_list|()
argument_list|,
name|rpcKind
operator|.
name|toString
argument_list|()
argument_list|,
name|versionMap
argument_list|)
expr_stmt|;
block|}
comment|// Assuming unique method names.
name|Method
name|desiredMethod
decl_stmt|;
name|Method
index|[]
name|allMethods
init|=
name|protocol
operator|.
name|getMethods
argument_list|()
decl_stmt|;
name|desiredMethod
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|Method
name|m
range|:
name|allMethods
control|)
block|{
if|if
condition|(
name|m
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|methodName
argument_list|)
condition|)
block|{
name|desiredMethod
operator|=
name|m
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|desiredMethod
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|methodHash
init|=
name|ProtocolSignature
operator|.
name|getFingerprint
argument_list|(
name|desiredMethod
argument_list|)
decl_stmt|;
return|return
name|methodExists
argument_list|(
name|methodHash
argument_list|,
name|version
argument_list|,
name|versionMap
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
DECL|method|convertProtocolSignatureProtos (List<ProtocolSignatureProto> protoList)
name|convertProtocolSignatureProtos
parameter_list|(
name|List
argument_list|<
name|ProtocolSignatureProto
argument_list|>
name|protoList
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ProtocolSignatureProto
name|p
range|:
name|protoList
control|)
block|{
name|int
index|[]
name|methods
init|=
operator|new
name|int
index|[
name|p
operator|.
name|getMethodsList
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|m
range|:
name|p
operator|.
name|getMethodsList
argument_list|()
control|)
block|{
name|methods
index|[
name|index
operator|++
index|]
operator|=
name|m
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
name|p
operator|.
name|getVersion
argument_list|()
argument_list|,
operator|new
name|ProtocolSignature
argument_list|(
name|p
operator|.
name|getVersion
argument_list|()
argument_list|,
name|methods
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
DECL|method|methodExists (int methodHash, long version, Map<Long, ProtocolSignature> versionMap)
specifier|private
specifier|static
name|boolean
name|methodExists
parameter_list|(
name|int
name|methodHash
parameter_list|,
name|long
name|version
parameter_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ProtocolSignature
argument_list|>
name|versionMap
parameter_list|)
block|{
name|ProtocolSignature
name|sig
init|=
name|versionMap
operator|.
name|get
argument_list|(
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|sig
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|m
range|:
name|sig
operator|.
name|getMethods
argument_list|()
control|)
block|{
if|if
condition|(
name|m
operator|==
name|methodHash
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|// The proxy returned re-uses the underlying connection. This is a special
comment|// mechanism for ProtocolMetaInfoPB.
comment|// Don't do this for any other protocol, it might cause a security hole.
DECL|method|getProtocolMetaInfoProxy (Object proxy, Configuration conf)
specifier|private
specifier|static
name|ProtocolMetaInfoPB
name|getProtocolMetaInfoProxy
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|RpcInvocationHandler
name|inv
init|=
operator|(
name|RpcInvocationHandler
operator|)
name|Proxy
operator|.
name|getInvocationHandler
argument_list|(
name|proxy
argument_list|)
decl_stmt|;
return|return
name|RPC
operator|.
name|getProtocolEngine
argument_list|(
name|ProtocolMetaInfoPB
operator|.
name|class
argument_list|,
name|conf
argument_list|)
operator|.
name|getProtocolMetaInfoProxy
argument_list|(
name|inv
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|conf
argument_list|,
name|NetUtils
operator|.
name|getDefaultSocketFactory
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|getProxy
argument_list|()
return|;
block|}
comment|/**    * Convert an RPC method to a string.    * The format we want is 'MethodOuterClassShortName#methodName'.    *    * For example, if the method is:    *   org.apache.hadoop.hdfs.protocol.proto.ClientNamenodeProtocolProtos.    *     ClientNamenodeProtocol.BlockingInterface.getServerDefaults    *    * the format we want is:    *   ClientNamenodeProtocol#getServerDefaults    */
DECL|method|methodToTraceString (Method method)
specifier|public
specifier|static
name|String
name|methodToTraceString
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|method
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|next
init|=
name|clazz
operator|.
name|getEnclosingClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
operator|||
name|next
operator|.
name|getEnclosingClass
argument_list|()
operator|==
literal|null
condition|)
break|break;
name|clazz
operator|=
name|next
expr_stmt|;
block|}
return|return
name|clazz
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"#"
operator|+
name|method
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**    * Convert an RPC class method to a string.    * The format we want is    * 'SecondOutermostClassShortName#OutermostClassShortName'.    *    * For example, if the full class name is:    *   org.apache.hadoop.hdfs.protocol.ClientProtocol.getBlockLocations    *    * the format we want is:    *   ClientProtocol#getBlockLocations    */
DECL|method|toTraceName (String fullName)
specifier|public
specifier|static
name|String
name|toTraceName
parameter_list|(
name|String
name|fullName
parameter_list|)
block|{
name|int
name|lastPeriod
init|=
name|fullName
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastPeriod
operator|<
literal|0
condition|)
block|{
return|return
name|fullName
return|;
block|}
name|int
name|secondLastPeriod
init|=
name|fullName
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|,
name|lastPeriod
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|secondLastPeriod
operator|<
literal|0
condition|)
block|{
return|return
name|fullName
return|;
block|}
return|return
name|fullName
operator|.
name|substring
argument_list|(
name|secondLastPeriod
operator|+
literal|1
argument_list|,
name|lastPeriod
argument_list|)
operator|+
literal|"#"
operator|+
name|fullName
operator|.
name|substring
argument_list|(
name|lastPeriod
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
end_class

end_unit

