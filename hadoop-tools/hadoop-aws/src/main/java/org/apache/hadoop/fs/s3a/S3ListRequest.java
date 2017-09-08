begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ListObjectsRequest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|ListObjectsV2Request
import|;
end_import

begin_comment
comment|/**  * API version-independent container for S3 List requests.  */
end_comment

begin_class
DECL|class|S3ListRequest
specifier|public
class|class
name|S3ListRequest
block|{
DECL|field|v1Request
specifier|private
name|ListObjectsRequest
name|v1Request
decl_stmt|;
DECL|field|v2Request
specifier|private
name|ListObjectsV2Request
name|v2Request
decl_stmt|;
DECL|method|S3ListRequest (ListObjectsRequest v1, ListObjectsV2Request v2)
specifier|protected
name|S3ListRequest
parameter_list|(
name|ListObjectsRequest
name|v1
parameter_list|,
name|ListObjectsV2Request
name|v2
parameter_list|)
block|{
name|v1Request
operator|=
name|v1
expr_stmt|;
name|v2Request
operator|=
name|v2
expr_stmt|;
block|}
comment|/**    * Restricted constructors to ensure v1 or v2, not both.    * @param request v1 request    * @return new list request container    */
DECL|method|v1 (ListObjectsRequest request)
specifier|public
specifier|static
name|S3ListRequest
name|v1
parameter_list|(
name|ListObjectsRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|S3ListRequest
argument_list|(
name|request
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**    * Restricted constructors to ensure v1 or v2, not both.    * @param request v2 request    * @return new list request container    */
DECL|method|v2 (ListObjectsV2Request request)
specifier|public
specifier|static
name|S3ListRequest
name|v2
parameter_list|(
name|ListObjectsV2Request
name|request
parameter_list|)
block|{
return|return
operator|new
name|S3ListRequest
argument_list|(
literal|null
argument_list|,
name|request
argument_list|)
return|;
block|}
comment|/**    * Is this a v1 API request or v2?    * @return true if v1, false if v2    */
DECL|method|isV1 ()
specifier|public
name|boolean
name|isV1
parameter_list|()
block|{
return|return
name|v1Request
operator|!=
literal|null
return|;
block|}
DECL|method|getV1 ()
specifier|public
name|ListObjectsRequest
name|getV1
parameter_list|()
block|{
return|return
name|v1Request
return|;
block|}
DECL|method|getV2 ()
specifier|public
name|ListObjectsV2Request
name|getV2
parameter_list|()
block|{
return|return
name|v2Request
return|;
block|}
block|}
end_class

end_unit

