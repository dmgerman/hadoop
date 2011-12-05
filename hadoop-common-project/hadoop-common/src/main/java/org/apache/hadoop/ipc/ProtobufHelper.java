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
comment|/**  * Helper methods for protobuf related RPC implementation  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|ProtobufHelper
specifier|public
class|class
name|ProtobufHelper
block|{
DECL|method|ProtobufHelper ()
specifier|private
name|ProtobufHelper
parameter_list|()
block|{
comment|// Hidden constructor for class with only static helper methods
block|}
comment|/**    * Return the RemoteException wrapped in ServiceException as cause.    * @param se ServiceException that wraps RemoteException    * @return RemoteException wrapped in ServiceException or    *         a new IOException that wraps unexpected ServiceException.    */
DECL|method|getRemoteException (ServiceException se)
specifier|public
specifier|static
name|IOException
name|getRemoteException
parameter_list|(
name|ServiceException
name|se
parameter_list|)
block|{
name|Throwable
name|e
init|=
name|se
operator|.
name|getCause
argument_list|()
decl_stmt|;
return|return
operator|(
operator|(
name|e
operator|instanceof
name|RemoteException
operator|)
condition|?
operator|(
name|IOException
operator|)
name|e
else|:
operator|new
name|IOException
argument_list|(
name|se
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

