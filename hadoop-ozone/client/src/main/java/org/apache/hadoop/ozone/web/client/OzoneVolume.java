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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|hdds
operator|.
name|scm
operator|.
name|client
operator|.
name|HddsClientUtils
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
name|client
operator|.
name|rest
operator|.
name|OzoneException
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
name|client
operator|.
name|rest
operator|.
name|headers
operator|.
name|Header
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
name|OzoneQuota
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
name|response
operator|.
name|ListBuckets
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
name|VolumeInfo
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
name|OzoneConsts
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
name|OzoneUtils
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|server
operator|.
name|ServerUtils
operator|.
name|releaseConnection
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
name|HttpEntity
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
name|HttpResponse
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
name|client
operator|.
name|methods
operator|.
name|HttpDelete
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
name|client
operator|.
name|methods
operator|.
name|HttpGet
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
name|client
operator|.
name|methods
operator|.
name|HttpPost
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
name|client
operator|.
name|methods
operator|.
name|HttpPut
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
name|client
operator|.
name|utils
operator|.
name|URIBuilder
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
name|impl
operator|.
name|client
operator|.
name|CloseableHttpClient
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
name|util
operator|.
name|EntityUtils
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_CREATED
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
operator|.
name|HTTP_OK
import|;
end_import

begin_comment
comment|/**  * Ozone Volume Class.  */
end_comment

begin_class
DECL|class|OzoneVolume
specifier|public
class|class
name|OzoneVolume
block|{
DECL|field|volumeInfo
specifier|private
name|VolumeInfo
name|volumeInfo
decl_stmt|;
DECL|field|headerMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headerMap
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|OzoneRestClient
name|client
decl_stmt|;
comment|/**    * Constructor for OzoneVolume.    */
DECL|method|OzoneVolume (OzoneRestClient client)
specifier|public
name|OzoneVolume
parameter_list|(
name|OzoneRestClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|headerMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructor for OzoneVolume.    *    * @param volInfo - volume Info.    * @param client  Client    */
DECL|method|OzoneVolume (VolumeInfo volInfo, OzoneRestClient client)
specifier|public
name|OzoneVolume
parameter_list|(
name|VolumeInfo
name|volInfo
parameter_list|,
name|OzoneRestClient
name|client
parameter_list|)
block|{
name|this
operator|.
name|volumeInfo
operator|=
name|volInfo
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
comment|/**    * Returns a Json String of this class.    * @return String    * @throws IOException    */
DECL|method|getJsonString ()
specifier|public
name|String
name|getJsonString
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|volumeInfo
operator|.
name|toJsonString
argument_list|()
return|;
block|}
comment|/**    * sets the Volume Info.    *    * @param volInfoString - Volume Info String    */
DECL|method|setVolumeInfo (String volInfoString)
specifier|public
name|void
name|setVolumeInfo
parameter_list|(
name|String
name|volInfoString
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|volumeInfo
operator|=
name|VolumeInfo
operator|.
name|parse
argument_list|(
name|volInfoString
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the volume info.    */
DECL|method|getVolumeInfo ()
specifier|public
name|VolumeInfo
name|getVolumeInfo
parameter_list|()
block|{
return|return
name|this
operator|.
name|volumeInfo
return|;
block|}
comment|/**    * Returns volume Name.    *    * @return Volume Name.    */
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|this
operator|.
name|volumeInfo
operator|.
name|getVolumeName
argument_list|()
return|;
block|}
comment|/**    * Get created by.    *    * @return String    */
DECL|method|getCreatedby ()
specifier|public
name|String
name|getCreatedby
parameter_list|()
block|{
return|return
name|this
operator|.
name|volumeInfo
operator|.
name|getCreatedBy
argument_list|()
return|;
block|}
comment|/**    * returns the Owner name.    *    * @return String    */
DECL|method|getOwnerName ()
specifier|public
name|String
name|getOwnerName
parameter_list|()
block|{
return|return
name|this
operator|.
name|volumeInfo
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/**    * Returns Quota Info.    *    * @return Quota    */
DECL|method|getQuota ()
specifier|public
name|OzoneQuota
name|getQuota
parameter_list|()
block|{
return|return
name|volumeInfo
operator|.
name|getQuota
argument_list|()
return|;
block|}
comment|/**    * Returns creation time of Volume.    *    * @return String    */
DECL|method|getCreatedOn ()
specifier|public
name|String
name|getCreatedOn
parameter_list|()
block|{
return|return
name|volumeInfo
operator|.
name|getCreatedOn
argument_list|()
return|;
block|}
comment|/**    * Returns a Http header from the Last Volume related call.    *    * @param headerName - Name of the header    * @return - Header Value    */
DECL|method|getHeader (String headerName)
specifier|public
name|String
name|getHeader
parameter_list|(
name|String
name|headerName
parameter_list|)
block|{
return|return
name|headerMap
operator|.
name|get
argument_list|(
name|headerName
argument_list|)
return|;
block|}
comment|/**    * Gets the Client, this is used by Bucket and Key Classes.    *    * @return - Ozone Client    */
DECL|method|getClient ()
name|OzoneRestClient
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
comment|/**    * Create Bucket - Creates a bucket under a given volume.    *    * @param bucketName - Bucket Name    * @param acls - Acls - User Acls    * @param storageType - Storage Class    * @param versioning - enable versioning support on a bucket.    *    *    * @return - a Ozone Bucket Object    */
DECL|method|createBucket (String bucketName, String[] acls, StorageType storageType, OzoneConsts.Versioning versioning)
specifier|public
name|OzoneBucket
name|createBucket
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
index|[]
name|acls
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|OzoneConsts
operator|.
name|Versioning
name|versioning
parameter_list|)
throws|throws
name|OzoneException
block|{
name|HttpPost
name|httpPost
init|=
literal|null
decl_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|newHttpClient
argument_list|()
init|)
block|{
name|OzoneUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|getClient
argument_list|()
operator|.
name|getEndPointURI
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|getVolumeName
argument_list|()
operator|+
literal|"/"
operator|+
name|bucketName
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|httpPost
operator|=
name|client
operator|.
name|getHttpPost
argument_list|(
literal|null
argument_list|,
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|acls
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|acl
range|:
name|acls
control|)
block|{
name|httpPost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_ACLS
argument_list|,
name|Header
operator|.
name|OZONE_ACL_ADD
operator|+
literal|" "
operator|+
name|acl
argument_list|)
expr_stmt|;
block|}
block|}
name|httpPost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_STORAGE_TYPE
argument_list|,
name|storageType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|httpPost
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_BUCKET_VERSIONING
argument_list|,
name|versioning
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|executeCreateBucket
argument_list|(
name|httpPost
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
return|return
name|getBucket
argument_list|(
name|bucketName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|URISyntaxException
decl||
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseConnection
argument_list|(
name|httpPost
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Create Bucket.    *    * @param bucketName - bucket name    * @param acls - acls    * @param storageType - storage class    *    * @throws OzoneException    */
DECL|method|createBucket (String bucketName, String[] acls, StorageType storageType)
specifier|public
name|OzoneBucket
name|createBucket
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
index|[]
name|acls
parameter_list|,
name|StorageType
name|storageType
parameter_list|)
throws|throws
name|OzoneException
block|{
return|return
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|,
name|storageType
argument_list|,
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|DISABLED
argument_list|)
return|;
block|}
comment|/**    * Create Bucket.    *    * @param bucketName - bucket name    * @param acls - acls    *    * @throws OzoneException    */
DECL|method|createBucket (String bucketName, String[] acls)
specifier|public
name|OzoneBucket
name|createBucket
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
index|[]
name|acls
parameter_list|)
throws|throws
name|OzoneException
block|{
return|return
name|createBucket
argument_list|(
name|bucketName
argument_list|,
name|acls
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|,
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|DISABLED
argument_list|)
return|;
block|}
comment|/**    * Create Bucket.    *    * @param bucketName - bucket name    *    * @throws OzoneException    */
DECL|method|createBucket (String bucketName)
specifier|public
name|OzoneBucket
name|createBucket
parameter_list|(
name|String
name|bucketName
parameter_list|)
throws|throws
name|OzoneException
block|{
return|return
name|createBucket
argument_list|(
name|bucketName
argument_list|,
literal|null
argument_list|,
name|StorageType
operator|.
name|DEFAULT
argument_list|,
name|OzoneConsts
operator|.
name|Versioning
operator|.
name|DISABLED
argument_list|)
return|;
block|}
comment|/**    * execute a Create Bucket Request against Ozone server.    *    * @param httppost - httpPost    *    * @throws IOException    * @throws OzoneException    */
DECL|method|executeCreateBucket (HttpPost httppost, CloseableHttpClient httpClient)
specifier|private
name|void
name|executeCreateBucket
parameter_list|(
name|HttpPost
name|httppost
parameter_list|,
name|CloseableHttpClient
name|httpClient
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|httppost
argument_list|)
decl_stmt|;
name|int
name|errorCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|entity
operator|=
name|response
operator|.
name|getEntity
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|errorCode
operator|==
name|HTTP_OK
operator|)
operator|||
operator|(
name|errorCode
operator|==
name|HTTP_CREATED
operator|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
throw|throw
name|OzoneException
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"Unexpected null in http payload"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Adds Acls to an existing bucket.    *    * @param bucketName - Name of the bucket    * @param acls - Acls    *    * @throws OzoneException    */
DECL|method|addAcls (String bucketName, String[] acls)
specifier|public
name|void
name|addAcls
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
index|[]
name|acls
parameter_list|)
throws|throws
name|OzoneException
block|{
name|HttpPut
name|putRequest
init|=
literal|null
decl_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|newHttpClient
argument_list|()
init|)
block|{
name|OzoneUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|getClient
argument_list|()
operator|.
name|getEndPointURI
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|getVolumeName
argument_list|()
operator|+
literal|"/"
operator|+
name|bucketName
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|putRequest
operator|=
name|client
operator|.
name|getHttpPut
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|acl
range|:
name|acls
control|)
block|{
name|putRequest
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_ACLS
argument_list|,
name|Header
operator|.
name|OZONE_ACL_ADD
operator|+
literal|" "
operator|+
name|acl
argument_list|)
expr_stmt|;
block|}
name|executePutBucket
argument_list|(
name|putRequest
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseConnection
argument_list|(
name|putRequest
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Removes ACLs from a bucket.    *    * @param bucketName - Bucket Name    * @param acls - Acls to be removed    *    * @throws OzoneException    */
DECL|method|removeAcls (String bucketName, String[] acls)
specifier|public
name|void
name|removeAcls
parameter_list|(
name|String
name|bucketName
parameter_list|,
name|String
index|[]
name|acls
parameter_list|)
throws|throws
name|OzoneException
block|{
name|HttpPut
name|putRequest
init|=
literal|null
decl_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|newHttpClient
argument_list|()
init|)
block|{
name|OzoneUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|getClient
argument_list|()
operator|.
name|getEndPointURI
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|getVolumeName
argument_list|()
operator|+
literal|"/"
operator|+
name|bucketName
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|putRequest
operator|=
name|client
operator|.
name|getHttpPut
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|acl
range|:
name|acls
control|)
block|{
name|putRequest
operator|.
name|addHeader
argument_list|(
name|Header
operator|.
name|OZONE_ACLS
argument_list|,
name|Header
operator|.
name|OZONE_ACL_REMOVE
operator|+
literal|" "
operator|+
name|acl
argument_list|)
expr_stmt|;
block|}
name|executePutBucket
argument_list|(
name|putRequest
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseConnection
argument_list|(
name|putRequest
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Returns information about an existing bucket.    *    * @param bucketName - BucketName    *    * @return OZoneBucket    */
DECL|method|getBucket (String bucketName)
specifier|public
name|OzoneBucket
name|getBucket
parameter_list|(
name|String
name|bucketName
parameter_list|)
throws|throws
name|OzoneException
block|{
name|HttpGet
name|getRequest
init|=
literal|null
decl_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|newHttpClient
argument_list|()
init|)
block|{
name|OzoneUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|getClient
argument_list|()
operator|.
name|getEndPointURI
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|getVolumeName
argument_list|()
operator|+
literal|"/"
operator|+
name|bucketName
argument_list|)
operator|.
name|setParameter
argument_list|(
name|Header
operator|.
name|OZONE_INFO_QUERY_TAG
argument_list|,
name|Header
operator|.
name|OZONE_INFO_QUERY_BUCKET
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|getRequest
operator|=
name|client
operator|.
name|getHttpGet
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|executeInfoBucket
argument_list|(
name|getRequest
argument_list|,
name|httpClient
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|URISyntaxException
decl||
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseConnection
argument_list|(
name|getRequest
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Execute the info bucket call.    *    * @param getRequest - httpGet Request    * @param httpClient - Http Client    *    * @return OzoneBucket    *    * @throws IOException    * @throws OzoneException    */
DECL|method|executeInfoBucket (HttpGet getRequest, CloseableHttpClient httpClient)
specifier|private
name|OzoneBucket
name|executeInfoBucket
parameter_list|(
name|HttpGet
name|getRequest
parameter_list|,
name|CloseableHttpClient
name|httpClient
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|getRequest
argument_list|)
decl_stmt|;
name|int
name|errorCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|entity
operator|=
name|response
operator|.
name|getEntity
argument_list|()
expr_stmt|;
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"Unexpected null in http payload"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|(
name|errorCode
operator|==
name|HTTP_OK
operator|)
operator|||
operator|(
name|errorCode
operator|==
name|HTTP_CREATED
operator|)
condition|)
block|{
name|OzoneBucket
name|bucket
init|=
operator|new
name|OzoneBucket
argument_list|(
name|BucketInfo
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
name|bucket
return|;
block|}
throw|throw
name|OzoneException
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Execute the put bucket call.    *    * @param putRequest - http put request    * @param httpClient - Http Client    *    * @return OzoneBucket    *    * @throws IOException    * @throws OzoneException    */
DECL|method|executePutBucket (HttpPut putRequest, CloseableHttpClient httpClient)
specifier|private
name|void
name|executePutBucket
parameter_list|(
name|HttpPut
name|putRequest
parameter_list|,
name|CloseableHttpClient
name|httpClient
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|putRequest
argument_list|)
decl_stmt|;
name|int
name|errorCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|entity
operator|=
name|response
operator|.
name|getEntity
argument_list|()
expr_stmt|;
if|if
condition|(
name|errorCode
operator|==
name|HTTP_OK
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
throw|throw
name|OzoneException
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"Unexpected null in http result"
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Gets a list of buckets on this volume.    *    * @return - List of buckets    *    * @throws OzoneException    */
DECL|method|listBuckets (String resultLength, String previousBucket, String prefix)
specifier|public
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|resultLength
parameter_list|,
name|String
name|previousBucket
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|OzoneException
block|{
name|HttpGet
name|getRequest
init|=
literal|null
decl_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|newHttpClient
argument_list|()
init|)
block|{
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|getClient
argument_list|()
operator|.
name|getEndPointURI
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|getVolumeName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|resultLength
argument_list|)
condition|)
block|{
name|builder
operator|.
name|addParameter
argument_list|(
name|Header
operator|.
name|OZONE_LIST_QUERY_MAXKEYS
argument_list|,
name|resultLength
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|previousBucket
argument_list|)
condition|)
block|{
name|builder
operator|.
name|addParameter
argument_list|(
name|Header
operator|.
name|OZONE_LIST_QUERY_PREVKEY
argument_list|,
name|previousBucket
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|builder
operator|.
name|addParameter
argument_list|(
name|Header
operator|.
name|OZONE_LIST_QUERY_PREFIX
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
name|getRequest
operator|=
name|client
operator|.
name|getHttpGet
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|executeListBuckets
argument_list|(
name|getRequest
argument_list|,
name|httpClient
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseConnection
argument_list|(
name|getRequest
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * executes the List Bucket Call.    *    * @param getRequest - http Request    * @param httpClient - http Client    *    * @return List of OzoneBuckets    *    * @throws IOException    * @throws OzoneException    */
DECL|method|executeListBuckets (HttpGet getRequest, CloseableHttpClient httpClient)
specifier|private
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|executeListBuckets
parameter_list|(
name|HttpGet
name|getRequest
parameter_list|,
name|CloseableHttpClient
name|httpClient
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|ozoneBucketList
init|=
operator|new
name|LinkedList
argument_list|<
name|OzoneBucket
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|getRequest
argument_list|)
decl_stmt|;
name|int
name|errorCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|entity
operator|=
name|response
operator|.
name|getEntity
argument_list|()
expr_stmt|;
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"Unexpected null in http payload"
argument_list|)
throw|;
block|}
if|if
condition|(
name|errorCode
operator|==
name|HTTP_OK
condition|)
block|{
name|ListBuckets
name|bucketList
init|=
name|ListBuckets
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|BucketInfo
name|info
range|:
name|bucketList
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|ozoneBucketList
operator|.
name|add
argument_list|(
operator|new
name|OzoneBucket
argument_list|(
name|info
argument_list|,
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ozoneBucketList
return|;
block|}
else|else
block|{
throw|throw
name|OzoneException
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Delete an empty bucket.    *    * @param bucketName - Name of the bucket to delete    *    * @throws OzoneException    */
DECL|method|deleteBucket (String bucketName)
specifier|public
name|void
name|deleteBucket
parameter_list|(
name|String
name|bucketName
parameter_list|)
throws|throws
name|OzoneException
block|{
name|HttpDelete
name|delRequest
init|=
literal|null
decl_stmt|;
try|try
init|(
name|CloseableHttpClient
name|httpClient
init|=
name|newHttpClient
argument_list|()
init|)
block|{
name|OzoneUtils
operator|.
name|verifyResourceName
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|URIBuilder
name|builder
init|=
operator|new
name|URIBuilder
argument_list|(
name|getClient
argument_list|()
operator|.
name|getEndPointURI
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setPath
argument_list|(
literal|"/"
operator|+
name|getVolumeName
argument_list|()
operator|+
literal|"/"
operator|+
name|bucketName
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|delRequest
operator|=
name|client
operator|.
name|getHttpDelete
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|executeDeleteBucket
argument_list|(
name|delRequest
argument_list|,
name|httpClient
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|URISyntaxException
decl||
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
name|releaseConnection
argument_list|(
name|delRequest
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Executes delete bucket call.    *    * @param delRequest - Delete Request    * @param httpClient - Http Client 7   *    * @throws IOException    * @throws OzoneException    */
DECL|method|executeDeleteBucket (HttpDelete delRequest, CloseableHttpClient httpClient)
specifier|private
name|void
name|executeDeleteBucket
parameter_list|(
name|HttpDelete
name|delRequest
parameter_list|,
name|CloseableHttpClient
name|httpClient
parameter_list|)
throws|throws
name|IOException
throws|,
name|OzoneException
block|{
name|HttpEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|HttpResponse
name|response
init|=
name|httpClient
operator|.
name|execute
argument_list|(
name|delRequest
argument_list|)
decl_stmt|;
name|int
name|errorCode
init|=
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
decl_stmt|;
name|entity
operator|=
name|response
operator|.
name|getEntity
argument_list|()
expr_stmt|;
if|if
condition|(
name|errorCode
operator|==
name|HTTP_OK
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|OzoneRestClientException
argument_list|(
literal|"Unexpected null in http payload."
argument_list|)
throw|;
block|}
throw|throw
name|OzoneException
operator|.
name|parse
argument_list|(
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|entity
operator|!=
literal|null
condition|)
block|{
name|EntityUtils
operator|.
name|consumeQuietly
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|newHttpClient ()
specifier|public
name|CloseableHttpClient
name|newHttpClient
parameter_list|()
block|{
return|return
name|HddsClientUtils
operator|.
name|newHttpClient
argument_list|()
return|;
block|}
block|}
end_class

end_unit

