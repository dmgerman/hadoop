begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.helper
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
operator|.
name|helper
package|;
end_package

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientHandlerException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|ClientResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|jersey
operator|.
name|api
operator|.
name|client
operator|.
name|UniformInterfaceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This class is merely a wrapper for {@link ClientResponse}. Given that the  * entity input stream of {@link ClientResponse} can be read only once by  * default and for some tests it is convenient to read the input stream many  * times, this class hides the details of how to do that and prevents  * unnecessary code duplication in tests.  */
end_comment

begin_class
DECL|class|BufferedClientResponse
specifier|public
class|class
name|BufferedClientResponse
block|{
DECL|field|response
specifier|private
name|ClientResponse
name|response
decl_stmt|;
DECL|method|BufferedClientResponse (ClientResponse response)
specifier|public
name|BufferedClientResponse
parameter_list|(
name|ClientResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|bufferEntity
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
DECL|method|getEntity (Class<T> clazz)
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getEntity
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|ClientHandlerException
throws|,
name|UniformInterfaceException
block|{
try|try
block|{
name|response
operator|.
name|getEntityInputStream
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|response
operator|.
name|getEntity
argument_list|(
name|clazz
argument_list|)
return|;
block|}
DECL|method|getType ()
specifier|public
name|MediaType
name|getType
parameter_list|()
block|{
return|return
name|response
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
end_class

end_unit

