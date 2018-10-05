begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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

begin_comment
comment|/**  * An interface that maps S3 buckets to Ozone  * volume/bucket.  */
end_comment

begin_interface
DECL|interface|S3BucketManager
specifier|public
interface|interface
name|S3BucketManager
block|{
comment|/**    * Creates an s3 bucket and maps it to Ozone volume/bucket.    * @param  userName - Name of the user who owns the bucket.    * @param bucketName - S3 Bucket Name.    * @throws  IOException in case the bucket cannot be created.    */
DECL|method|createS3Bucket (String userName, String bucketName)
name|void
name|createS3Bucket
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the Ozone volume/bucket where the S3 Bucket points to.    * @param s3BucketName - S3 Bucket Name    * @return String - Ozone volume/bucket    * @throws IOException in case of failure to retrieve mapping.    */
DECL|method|getOzoneBucketMapping (String s3BucketName)
name|String
name|getOzoneBucketMapping
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns Ozone volume name for a given S3Bucket.    * @param s3BucketName - S3 bucket name.    * @return String - Ozone volume name where is s3bucket resides.    * @throws IOException - in case of failure to retrieve mapping.    */
DECL|method|getOzoneVolumeName (String s3BucketName)
name|String
name|getOzoneVolumeName
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns Ozone bucket name for a given s3Bucket.    * @param s3BucketName  - S3 bucket Name.    * @return  Ozone bucket name for this given S3 bucket    * @throws IOException - in case of failure to retrieve mapping.    */
DECL|method|getOzoneBucketName (String s3BucketName)
name|String
name|getOzoneBucketName
parameter_list|(
name|String
name|s3BucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

