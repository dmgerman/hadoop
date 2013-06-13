begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.factories.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factories
operator|.
name|impl
operator|.
name|pb
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
name|Constructor
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
name|InvocationTargetException
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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|ConcurrentMap
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
name|security
operator|.
name|token
operator|.
name|SecretManager
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
name|TokenIdentifier
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnRuntimeException
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
name|yarn
operator|.
name|factories
operator|.
name|RpcServerFactory
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

begin_class
DECL|class|RpcServerFactoryPBImpl
specifier|public
class|class
name|RpcServerFactoryPBImpl
implements|implements
name|RpcServerFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RpcServerFactoryPBImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PROTO_GEN_PACKAGE_NAME
specifier|private
specifier|static
specifier|final
name|String
name|PROTO_GEN_PACKAGE_NAME
init|=
literal|"org.apache.hadoop.yarn.proto"
decl_stmt|;
DECL|field|PROTO_GEN_CLASS_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PROTO_GEN_CLASS_SUFFIX
init|=
literal|"Service"
decl_stmt|;
DECL|field|PB_IMPL_PACKAGE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PB_IMPL_PACKAGE_SUFFIX
init|=
literal|"impl.pb.service"
decl_stmt|;
DECL|field|PB_IMPL_CLASS_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PB_IMPL_CLASS_SUFFIX
init|=
literal|"PBServiceImpl"
decl_stmt|;
DECL|field|self
specifier|private
specifier|static
specifier|final
name|RpcServerFactoryPBImpl
name|self
init|=
operator|new
name|RpcServerFactoryPBImpl
argument_list|()
decl_stmt|;
DECL|field|localConf
specifier|private
name|Configuration
name|localConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|serviceCache
specifier|private
name|ConcurrentMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Constructor
argument_list|<
name|?
argument_list|>
argument_list|>
name|serviceCache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Constructor
argument_list|<
name|?
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|protoCache
specifier|private
name|ConcurrentMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Method
argument_list|>
name|protoCache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Method
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|get ()
specifier|public
specifier|static
name|RpcServerFactoryPBImpl
name|get
parameter_list|()
block|{
return|return
name|RpcServerFactoryPBImpl
operator|.
name|self
return|;
block|}
DECL|method|RpcServerFactoryPBImpl ()
specifier|private
name|RpcServerFactoryPBImpl
parameter_list|()
block|{   }
DECL|method|getServer (Class<?> protocol, Object instance, InetSocketAddress addr, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, int numHandlers)
specifier|public
name|Server
name|getServer
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Object
name|instance
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|int
name|numHandlers
parameter_list|)
block|{
return|return
name|getServer
argument_list|(
name|protocol
argument_list|,
name|instance
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
name|secretManager
argument_list|,
name|numHandlers
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getServer (Class<?> protocol, Object instance, InetSocketAddress addr, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, int numHandlers, String portRangeConfig)
specifier|public
name|Server
name|getServer
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Object
name|instance
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|int
name|numHandlers
parameter_list|,
name|String
name|portRangeConfig
parameter_list|)
block|{
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
init|=
name|serviceCache
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|constructor
operator|==
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|pbServiceImplClazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pbServiceImplClazz
operator|=
name|localConf
operator|.
name|getClassByName
argument_list|(
name|getPbServiceImplClassName
argument_list|(
name|protocol
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to load class: ["
operator|+
name|getPbServiceImplClassName
argument_list|(
name|protocol
argument_list|)
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|constructor
operator|=
name|pbServiceImplClazz
operator|.
name|getConstructor
argument_list|(
name|protocol
argument_list|)
expr_stmt|;
name|constructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|serviceCache
operator|.
name|putIfAbsent
argument_list|(
name|protocol
argument_list|,
name|constructor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Could not find constructor with params: "
operator|+
name|Long
operator|.
name|TYPE
operator|+
literal|", "
operator|+
name|InetSocketAddress
operator|.
name|class
operator|+
literal|", "
operator|+
name|Configuration
operator|.
name|class
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|Object
name|service
init|=
literal|null
decl_stmt|;
try|try
block|{
name|service
operator|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|Class
argument_list|<
name|?
argument_list|>
name|pbProtocol
init|=
name|service
operator|.
name|getClass
argument_list|()
operator|.
name|getInterfaces
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|Method
name|method
init|=
name|protoCache
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|protoClazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|protoClazz
operator|=
name|localConf
operator|.
name|getClassByName
argument_list|(
name|getProtoClassName
argument_list|(
name|protocol
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Failed to load class: ["
operator|+
name|getProtoClassName
argument_list|(
name|protocol
argument_list|)
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|method
operator|=
name|protoClazz
operator|.
name|getMethod
argument_list|(
literal|"newReflectiveBlockingService"
argument_list|,
name|pbProtocol
operator|.
name|getInterfaces
argument_list|()
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|protoCache
operator|.
name|putIfAbsent
argument_list|(
name|protocol
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
try|try
block|{
return|return
name|createServer
argument_list|(
name|pbProtocol
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|,
name|secretManager
argument_list|,
name|numHandlers
argument_list|,
operator|(
name|BlockingService
operator|)
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|service
argument_list|)
argument_list|,
name|portRangeConfig
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getProtoClassName (Class<?> clazz)
specifier|private
name|String
name|getProtoClassName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|srcClassName
init|=
name|getClassName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
return|return
name|PROTO_GEN_PACKAGE_NAME
operator|+
literal|"."
operator|+
name|srcClassName
operator|+
literal|"$"
operator|+
name|srcClassName
operator|+
name|PROTO_GEN_CLASS_SUFFIX
return|;
block|}
DECL|method|getPbServiceImplClassName (Class<?> clazz)
specifier|private
name|String
name|getPbServiceImplClassName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|srcPackagePart
init|=
name|getPackageName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|String
name|srcClassName
init|=
name|getClassName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|String
name|destPackagePart
init|=
name|srcPackagePart
operator|+
literal|"."
operator|+
name|PB_IMPL_PACKAGE_SUFFIX
decl_stmt|;
name|String
name|destClassPart
init|=
name|srcClassName
operator|+
name|PB_IMPL_CLASS_SUFFIX
decl_stmt|;
return|return
name|destPackagePart
operator|+
literal|"."
operator|+
name|destClassPart
return|;
block|}
DECL|method|getClassName (Class<?> clazz)
specifier|private
name|String
name|getClassName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
name|String
name|fqName
init|=
name|clazz
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
operator|(
name|fqName
operator|.
name|substring
argument_list|(
name|fqName
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
operator|+
literal|1
argument_list|,
name|fqName
operator|.
name|length
argument_list|()
argument_list|)
operator|)
return|;
block|}
DECL|method|getPackageName (Class<?> clazz)
specifier|private
name|String
name|getPackageName
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
DECL|method|createServer (Class<?> pbProtocol, InetSocketAddress addr, Configuration conf, SecretManager<? extends TokenIdentifier> secretManager, int numHandlers, BlockingService blockingService, String portRangeConfig)
specifier|private
name|Server
name|createServer
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|pbProtocol
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|SecretManager
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|secretManager
parameter_list|,
name|int
name|numHandlers
parameter_list|,
name|BlockingService
name|blockingService
parameter_list|,
name|String
name|portRangeConfig
parameter_list|)
throws|throws
name|IOException
block|{
name|RPC
operator|.
name|setProtocolEngine
argument_list|(
name|conf
argument_list|,
name|pbProtocol
argument_list|,
name|ProtobufRpcEngine
operator|.
name|class
argument_list|)
expr_stmt|;
name|RPC
operator|.
name|Server
name|server
init|=
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
name|pbProtocol
argument_list|)
operator|.
name|setInstance
argument_list|(
name|blockingService
argument_list|)
operator|.
name|setBindAddress
argument_list|(
name|addr
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|.
name|setPort
argument_list|(
name|addr
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|setNumHandlers
argument_list|(
name|numHandlers
argument_list|)
operator|.
name|setVerbose
argument_list|(
literal|false
argument_list|)
operator|.
name|setSecretManager
argument_list|(
name|secretManager
argument_list|)
operator|.
name|setPortRangeConfig
argument_list|(
name|portRangeConfig
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding protocol "
operator|+
name|pbProtocol
operator|.
name|getCanonicalName
argument_list|()
operator|+
literal|" to the server"
argument_list|)
expr_stmt|;
name|server
operator|.
name|addProtocol
argument_list|(
name|RPC
operator|.
name|RpcKind
operator|.
name|RPC_PROTOCOL_BUFFER
argument_list|,
name|pbProtocol
argument_list|,
name|blockingService
argument_list|)
expr_stmt|;
return|return
name|server
return|;
block|}
block|}
end_class

end_unit

