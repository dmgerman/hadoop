begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ContainerRequestFilter
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
name|PreMatching
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
comment|/**  * Filter to adjust request headers for compatible reasons.  */
end_comment

begin_class
annotation|@
name|Provider
annotation|@
name|PreMatching
DECL|class|HeaderPreprocessor
specifier|public
class|class
name|HeaderPreprocessor
implements|implements
name|ContainerRequestFilter
block|{
annotation|@
name|Override
DECL|method|filter (ContainerRequestContext requestContext)
specifier|public
name|void
name|filter
parameter_list|(
name|ContainerRequestContext
name|requestContext
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|requestContext
operator|.
name|getUriInfo
argument_list|()
operator|.
name|getQueryParameters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"delete"
argument_list|)
condition|)
block|{
comment|//aws cli doesn't send proper Content-Type and by default POST requests
comment|//processed as form-url-encoded. Here we can fix this.
name|requestContext
operator|.
name|getHeaders
argument_list|()
operator|.
name|putSingle
argument_list|(
literal|"Content-Type"
argument_list|,
name|MediaType
operator|.
name|APPLICATION_XML
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

