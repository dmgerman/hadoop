begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|client
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|StorageType
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
name|ozone
operator|.
name|web
operator|.
name|request
operator|.
name|OzoneAcl
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
name|ozone
operator|.
name|web
operator|.
name|response
operator|.
name|BucketInfo
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneConsts
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpRequestInterceptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HTTP
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|protocol
operator|.
name|HttpContext
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A Bucket class the represents an Ozone Bucket.  */
end_comment

begin_class
DECL|class|OzoneBucket
specifier|public
class|class
name|OzoneBucket
block|{
DECL|field|bucketInfo
specifier|private
name|BucketInfo
name|bucketInfo
decl_stmt|;
DECL|field|volume
specifier|private
name|OzoneVolume
name|volume
decl_stmt|;
comment|/**    * Constructor for bucket.    *    * @param info   - BucketInfo    * @param volume - OzoneVolume Object that contains this bucket    */
DECL|method|OzoneBucket (BucketInfo info, OzoneVolume volume)
specifier|public
name|OzoneBucket
parameter_list|(
name|BucketInfo
name|info
parameter_list|,
name|OzoneVolume
name|volume
parameter_list|)
block|{
name|this
operator|.
name|bucketInfo
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|volume
operator|=
name|volume
expr_stmt|;
block|}
comment|/**    * Gets bucket Info.    *    * @return BucketInfo    */
DECL|method|getBucketInfo ()
specifier|public
name|BucketInfo
name|getBucketInfo
parameter_list|()
block|{
return|return
name|bucketInfo
return|;
block|}
comment|/**    * Sets Bucket Info.    *    * @param bucketInfo BucketInfo    */
DECL|method|setBucketInfo (BucketInfo bucketInfo)
specifier|public
name|void
name|setBucketInfo
parameter_list|(
name|BucketInfo
name|bucketInfo
parameter_list|)
block|{
name|this
operator|.
name|bucketInfo
operator|=
name|bucketInfo
expr_stmt|;
block|}
comment|/**    * Returns the parent volume class.    *    * @return - OzoneVolume    */
DECL|method|getVolume ()
name|OzoneVolume
name|getVolume
parameter_list|()
block|{
return|return
name|volume
return|;
block|}
comment|/**    * Returns bucket name.    *    * @return Bucket Name    */
DECL|method|getBucketName ()
specifier|public
name|String
name|getBucketName
parameter_list|()
block|{
return|return
name|bucketInfo
operator|.
name|getBucketName
argument_list|()
return|;
block|}
comment|/**    * Returns the Acls on the bucket.    *    * @return - Acls    */
DECL|method|getAcls ()
specifier|public
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcls
parameter_list|()
block|{
return|return
name|bucketInfo
operator|.
name|getAcls
argument_list|()
return|;
block|}
comment|/**    * Return versioning info on the bucket - Enabled or disabled.    *    * @return - Version Enum    */
DECL|method|getVersioning ()
specifier|public
name|OzoneConsts
operator|.
name|Versioning
name|getVersioning
parameter_list|()
block|{
return|return
name|bucketInfo
operator|.
name|getVersioning
argument_list|()
return|;
block|}
comment|/**    * Gets the Storage class for the bucket.    *    * @return Storage Class Enum    */
DECL|method|getStorageClass ()
specifier|public
name|StorageType
name|getStorageClass
parameter_list|()
block|{
return|return
name|bucketInfo
operator|.
name|getStorageType
argument_list|()
return|;
block|}
DECL|class|ContentLengthHeaderRemover
specifier|private
specifier|static
class|class
name|ContentLengthHeaderRemover
implements|implements
name|HttpRequestInterceptor
block|{
annotation|@
name|Override
DECL|method|process (HttpRequest request, HttpContext context)
specifier|public
name|void
name|process
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpContext
name|context
parameter_list|)
throws|throws
name|HttpException
throws|,
name|IOException
block|{
comment|// fighting org.apache.http.protocol
comment|// .RequestContent's ProtocolException("Content-Length header
comment|// already present");
name|request
operator|.
name|removeHeaders
argument_list|(
name|HTTP
operator|.
name|CONTENT_LEN
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

