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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|RpcClientFactory
import|;
end_import

begin_class
annotation|@
name|Private
DECL|class|RpcClientFactoryPBImpl
specifier|public
class|class
name|RpcClientFactoryPBImpl
implements|implements
name|RpcClientFactory
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
name|RpcClientFactoryPBImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|PB_IMPL_PACKAGE_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PB_IMPL_PACKAGE_SUFFIX
init|=
literal|"impl.pb.client"
decl_stmt|;
DECL|field|PB_IMPL_CLASS_SUFFIX
specifier|private
specifier|static
specifier|final
name|String
name|PB_IMPL_CLASS_SUFFIX
init|=
literal|"PBClientImpl"
decl_stmt|;
DECL|field|self
specifier|private
specifier|static
specifier|final
name|RpcClientFactoryPBImpl
name|self
init|=
operator|new
name|RpcClientFactoryPBImpl
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
DECL|field|cache
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
name|cache
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
DECL|method|get ()
specifier|public
specifier|static
name|RpcClientFactoryPBImpl
name|get
parameter_list|()
block|{
return|return
name|RpcClientFactoryPBImpl
operator|.
name|self
return|;
block|}
DECL|method|RpcClientFactoryPBImpl ()
specifier|private
name|RpcClientFactoryPBImpl
parameter_list|()
block|{   }
DECL|method|getClient (Class<?> protocol, long clientVersion, InetSocketAddress addr, Configuration conf)
specifier|public
name|Object
name|getClient
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|InetSocketAddress
name|addr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
init|=
name|cache
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
name|pbClazz
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pbClazz
operator|=
name|localConf
operator|.
name|getClassByName
argument_list|(
name|getPBImplClassName
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
name|getPBImplClassName
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
name|pbClazz
operator|.
name|getConstructor
argument_list|(
name|Long
operator|.
name|TYPE
argument_list|,
name|InetSocketAddress
operator|.
name|class
argument_list|,
name|Configuration
operator|.
name|class
argument_list|)
expr_stmt|;
name|constructor
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cache
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
try|try
block|{
name|Object
name|retObject
init|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|clientVersion
argument_list|,
name|addr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|retObject
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
block|}
annotation|@
name|Override
DECL|method|stopClient (Object proxy)
specifier|public
name|void
name|stopClient
parameter_list|(
name|Object
name|proxy
parameter_list|)
block|{
try|try
block|{
name|Method
name|closeMethod
init|=
name|proxy
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"close"
argument_list|)
decl_stmt|;
name|closeMethod
operator|.
name|invoke
argument_list|(
name|proxy
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
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot call close method due to Exception. "
operator|+
literal|"Ignoring."
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getPBImplClassName (Class<?> clazz)
specifier|private
name|String
name|getPBImplClassName
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
block|}
end_class

end_unit

