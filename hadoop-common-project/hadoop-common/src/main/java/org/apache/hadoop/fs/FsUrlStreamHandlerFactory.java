begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|URLStreamHandlerFactory
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

begin_comment
comment|/**  * Factory for URL stream handlers.  *   * There is only one handler whose job is to create UrlConnections. A  * FsUrlConnection relies on FileSystem to choose the appropriate FS  * implementation.  *   * Before returning our handler, we make sure that FileSystem knows an  * implementation for the requested scheme/protocol.  */
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
DECL|class|FsUrlStreamHandlerFactory
specifier|public
class|class
name|FsUrlStreamHandlerFactory
implements|implements
name|URLStreamHandlerFactory
block|{
comment|// The configuration holds supported FS implementation class names.
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
comment|// This map stores whether a protocol is know or not by FileSystem
DECL|field|protocols
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|protocols
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
decl_stmt|;
comment|// The URL Stream handler
DECL|field|handler
specifier|private
name|java
operator|.
name|net
operator|.
name|URLStreamHandler
name|handler
decl_stmt|;
DECL|method|FsUrlStreamHandlerFactory ()
specifier|public
name|FsUrlStreamHandlerFactory
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|FsUrlStreamHandlerFactory (Configuration conf)
specifier|public
name|FsUrlStreamHandlerFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// force init of FileSystem code to avoid HADOOP-9041
try|try
block|{
name|FileSystem
operator|.
name|getFileSystemClass
argument_list|(
literal|"file"
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|io
argument_list|)
throw|;
block|}
name|this
operator|.
name|handler
operator|=
operator|new
name|FsUrlStreamHandler
argument_list|(
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createURLStreamHandler (String protocol)
specifier|public
name|java
operator|.
name|net
operator|.
name|URLStreamHandler
name|createURLStreamHandler
parameter_list|(
name|String
name|protocol
parameter_list|)
block|{
if|if
condition|(
operator|!
name|protocols
operator|.
name|containsKey
argument_list|(
name|protocol
argument_list|)
condition|)
block|{
name|boolean
name|known
init|=
literal|true
decl_stmt|;
try|try
block|{
name|FileSystem
operator|.
name|getFileSystemClass
argument_list|(
name|protocol
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|known
operator|=
literal|false
expr_stmt|;
block|}
name|protocols
operator|.
name|put
argument_list|(
name|protocol
argument_list|,
name|known
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protocols
operator|.
name|get
argument_list|(
name|protocol
argument_list|)
condition|)
block|{
return|return
name|handler
return|;
block|}
else|else
block|{
comment|// FileSystem does not know the protocol, let the VM handle this
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

