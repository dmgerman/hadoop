begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|MockitoUtil
specifier|public
specifier|abstract
class|class
name|MockitoUtil
block|{
comment|/**    * Return a mock object for an IPC protocol. This special    * method is necessary, since the IPC proxies have to implement    * Closeable in addition to their protocol interface.    * @param clazz the protocol class    */
DECL|method|mockProtocol (Class<T> clazz)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|mockProtocol
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
name|Mockito
operator|.
name|mock
argument_list|(
name|clazz
argument_list|,
name|Mockito
operator|.
name|withSettings
argument_list|()
operator|.
name|extraInterfaces
argument_list|(
name|Closeable
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

