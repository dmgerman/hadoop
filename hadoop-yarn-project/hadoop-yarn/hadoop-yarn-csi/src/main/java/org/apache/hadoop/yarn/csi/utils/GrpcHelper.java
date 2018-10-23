begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.csi.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|csi
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|unix
operator|.
name|DomainSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
import|;
end_import

begin_comment
comment|/**  * Helper classes for gRPC utility functions.  */
end_comment

begin_class
DECL|class|GrpcHelper
specifier|public
specifier|final
class|class
name|GrpcHelper
block|{
DECL|field|UNIX_DOMAIN_SOCKET_PREFIX
specifier|protected
specifier|static
specifier|final
name|String
name|UNIX_DOMAIN_SOCKET_PREFIX
init|=
literal|"unix://"
decl_stmt|;
DECL|method|GrpcHelper ()
specifier|private
name|GrpcHelper
parameter_list|()
block|{
comment|// hide constructor for utility class
block|}
DECL|method|getSocketAddress (String value)
specifier|public
specifier|static
name|SocketAddress
name|getSocketAddress
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
name|UNIX_DOMAIN_SOCKET_PREFIX
argument_list|)
condition|)
block|{
name|String
name|filePath
init|=
name|value
operator|.
name|substring
argument_list|(
name|UNIX_DOMAIN_SOCKET_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|filePath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unix domain socket file path must be absolute, file: "
operator|+
name|value
argument_list|)
throw|;
block|}
comment|// Create the SocketAddress referencing the file.
return|return
operator|new
name|DomainSocketAddress
argument_list|(
name|file
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Given address "
operator|+
name|value
operator|+
literal|" is not a valid unix domain socket path"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

