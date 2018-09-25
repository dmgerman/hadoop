begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.s3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|s3
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|container
operator|.
name|ContainerRequestContext
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
name|container
operator|.
name|ContainerResponseContext
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
name|container
operator|.
name|ContainerResponseFilter
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
name|ext
operator|.
name|Provider
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
comment|/**  * This class adds common header responses for all the requests.  */
end_comment

begin_class
annotation|@
name|Provider
DECL|class|CommonHeadersContainerResponseFilter
specifier|public
class|class
name|CommonHeadersContainerResponseFilter
implements|implements
name|ContainerResponseFilter
block|{
annotation|@
name|Override
DECL|method|filter (ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext)
specifier|public
name|void
name|filter
parameter_list|(
name|ContainerRequestContext
name|containerRequestContext
parameter_list|,
name|ContainerResponseContext
name|containerResponseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|containerResponseContext
operator|.
name|getHeaders
argument_list|()
operator|.
name|add
argument_list|(
literal|"Server"
argument_list|,
literal|"Ozone"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

