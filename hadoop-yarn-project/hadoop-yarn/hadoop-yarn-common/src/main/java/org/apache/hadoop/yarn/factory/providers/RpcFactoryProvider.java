begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.factory.providers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|factory
operator|.
name|providers
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
name|conf
operator|.
name|YarnConfiguration
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

begin_comment
comment|/**  * A public static get() method must be present in the Client/Server Factory implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"MapReduce"
block|,
literal|"YARN"
block|}
argument_list|)
DECL|class|RpcFactoryProvider
specifier|public
class|class
name|RpcFactoryProvider
block|{
DECL|method|RpcFactoryProvider ()
specifier|private
name|RpcFactoryProvider
parameter_list|()
block|{        }
DECL|method|getServerFactory (Configuration conf)
specifier|public
specifier|static
name|RpcServerFactory
name|getServerFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
block|}
name|String
name|serverFactoryClassName
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_SERVER_FACTORY_CLASS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_IPC_SERVER_FACTORY_CLASS
argument_list|)
decl_stmt|;
return|return
operator|(
name|RpcServerFactory
operator|)
name|getFactoryClassInstance
argument_list|(
name|serverFactoryClassName
argument_list|)
return|;
block|}
DECL|method|getClientFactory (Configuration conf)
specifier|public
specifier|static
name|RpcClientFactory
name|getClientFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|clientFactoryClassName
init|=
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|IPC_CLIENT_FACTORY_CLASS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_IPC_CLIENT_FACTORY_CLASS
argument_list|)
decl_stmt|;
return|return
operator|(
name|RpcClientFactory
operator|)
name|getFactoryClassInstance
argument_list|(
name|clientFactoryClassName
argument_list|)
return|;
block|}
DECL|method|getFactoryClassInstance (String factoryClassName)
specifier|private
specifier|static
name|Object
name|getFactoryClassInstance
parameter_list|(
name|String
name|factoryClassName
parameter_list|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|factoryClassName
argument_list|)
decl_stmt|;
name|Method
name|method
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"get"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|method
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
return|;
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
name|e
argument_list|)
throw|;
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
block|}
block|}
end_class

end_unit

