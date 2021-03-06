begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
DECL|class|RPCUtil
specifier|public
class|class
name|RPCUtil
block|{
comment|/**    * Returns an instance of {@link YarnException}    */
DECL|method|getRemoteException (Throwable t)
specifier|public
specifier|static
name|YarnException
name|getRemoteException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
operator|new
name|YarnException
argument_list|(
name|t
argument_list|)
return|;
block|}
comment|/**    * Returns an instance of {@link YarnException}    */
DECL|method|getRemoteException (String message)
specifier|public
specifier|static
name|YarnException
name|getRemoteException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|YarnException
argument_list|(
name|message
argument_list|)
return|;
block|}
DECL|method|instantiateException ( Class<? extends T> cls, RemoteException re)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Throwable
parameter_list|>
name|T
name|instantiateException
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|cls
parameter_list|,
name|RemoteException
name|re
parameter_list|)
throws|throws
name|RemoteException
block|{
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|cn
init|=
name|cls
operator|.
name|getConstructor
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|cn
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|T
name|ex
init|=
name|cn
operator|.
name|newInstance
argument_list|(
name|re
operator|.
name|getMessage
argument_list|()
argument_list|)
decl_stmt|;
name|ex
operator|.
name|initCause
argument_list|(
name|re
argument_list|)
expr_stmt|;
return|return
name|ex
return|;
comment|// RemoteException contains useful information as against the
comment|// java.lang.reflect exceptions.
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
name|re
throw|;
block|}
block|}
DECL|method|instantiateYarnException ( Class<? extends T> cls, RemoteException re)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|YarnException
parameter_list|>
name|T
name|instantiateYarnException
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|cls
parameter_list|,
name|RemoteException
name|re
parameter_list|)
throws|throws
name|RemoteException
block|{
return|return
name|instantiateException
argument_list|(
name|cls
argument_list|,
name|re
argument_list|)
return|;
block|}
DECL|method|instantiateIOException ( Class<? extends T> cls, RemoteException re)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|IOException
parameter_list|>
name|T
name|instantiateIOException
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|cls
parameter_list|,
name|RemoteException
name|re
parameter_list|)
throws|throws
name|RemoteException
block|{
return|return
name|instantiateException
argument_list|(
name|cls
argument_list|,
name|re
argument_list|)
return|;
block|}
DECL|method|instantiateRuntimeException ( Class<? extends T> cls, RemoteException re)
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|RuntimeException
parameter_list|>
name|T
name|instantiateRuntimeException
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|T
argument_list|>
name|cls
parameter_list|,
name|RemoteException
name|re
parameter_list|)
throws|throws
name|RemoteException
block|{
return|return
name|instantiateException
argument_list|(
name|cls
argument_list|,
name|re
argument_list|)
return|;
block|}
comment|/**    * Utility method that unwraps and returns appropriate exceptions.    *     * @param se    *          ServiceException    * @return An instance of the actual exception, which will be a subclass of    *         {@link YarnException} or {@link IOException}    */
DECL|method|unwrapAndThrowException (ServiceException se)
specifier|public
specifier|static
name|Void
name|unwrapAndThrowException
parameter_list|(
name|ServiceException
name|se
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
name|Throwable
name|cause
init|=
name|se
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|==
literal|null
condition|)
block|{
comment|// SE generated by the RPC layer itself.
throw|throw
operator|new
name|IOException
argument_list|(
name|se
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|cause
operator|instanceof
name|RemoteException
condition|)
block|{
name|RemoteException
name|re
init|=
operator|(
name|RemoteException
operator|)
name|cause
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|realClass
init|=
literal|null
decl_stmt|;
try|try
block|{
name|realClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|re
operator|.
name|getClassName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|cnf
parameter_list|)
block|{
comment|// Assume this to be a new exception type added to YARN. This isn't
comment|// absolutely correct since the RPC layer could add an exception as
comment|// well.
throw|throw
name|instantiateYarnException
argument_list|(
name|YarnException
operator|.
name|class
argument_list|,
name|re
argument_list|)
throw|;
block|}
if|if
condition|(
name|YarnException
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|realClass
argument_list|)
condition|)
block|{
throw|throw
name|instantiateYarnException
argument_list|(
name|realClass
operator|.
name|asSubclass
argument_list|(
name|YarnException
operator|.
name|class
argument_list|)
argument_list|,
name|re
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|IOException
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|realClass
argument_list|)
condition|)
block|{
throw|throw
name|instantiateIOException
argument_list|(
name|realClass
operator|.
name|asSubclass
argument_list|(
name|IOException
operator|.
name|class
argument_list|)
argument_list|,
name|re
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|RuntimeException
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|realClass
argument_list|)
condition|)
block|{
throw|throw
name|instantiateRuntimeException
argument_list|(
name|realClass
operator|.
name|asSubclass
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|)
argument_list|,
name|re
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|re
throw|;
block|}
comment|// RemoteException contains useful information as against the
comment|// java.lang.reflect exceptions.
block|}
elseif|else
if|if
condition|(
name|cause
operator|instanceof
name|IOException
condition|)
block|{
comment|// RPC Client exception.
throw|throw
operator|(
name|IOException
operator|)
name|cause
throw|;
block|}
elseif|else
if|if
condition|(
name|cause
operator|instanceof
name|RuntimeException
condition|)
block|{
comment|// RPC RuntimeException
throw|throw
operator|(
name|RuntimeException
operator|)
name|cause
throw|;
block|}
else|else
block|{
comment|// Should not be generated.
throw|throw
operator|new
name|IOException
argument_list|(
name|se
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

