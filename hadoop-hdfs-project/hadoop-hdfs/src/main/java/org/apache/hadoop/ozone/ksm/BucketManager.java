begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketArgs
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
name|ksm
operator|.
name|helpers
operator|.
name|KsmBucketInfo
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
comment|/**  * BucketManager handles all the bucket level operations.  */
end_comment

begin_interface
DECL|interface|BucketManager
specifier|public
interface|interface
name|BucketManager
block|{
comment|/**    * Creates a bucket.    * @param bucketInfo - KsmBucketInfo for creating bucket.    */
DECL|method|createBucket (KsmBucketInfo bucketInfo)
name|void
name|createBucket
parameter_list|(
name|KsmBucketInfo
name|bucketInfo
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns Bucket Information.    * @param volumeName - Name of the Volume.    * @param bucketName - Name of the Bucket.    */
DECL|method|getBucketInfo (String volumeName, String bucketName)
name|KsmBucketInfo
name|getBucketInfo
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Sets bucket property from args.    * @param args - BucketArgs.    * @throws IOException    */
DECL|method|setBucketProperty (KsmBucketArgs args)
name|void
name|setBucketProperty
parameter_list|(
name|KsmBucketArgs
name|args
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes an existing empty bucket from volume.    * @param volumeName - Name of the volume.    * @param bucketName - Name of the bucket.    * @throws IOException    */
DECL|method|deleteBucket (String volumeName, String bucketName)
name|void
name|deleteBucket
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a list of buckets represented by {@link KsmBucketInfo}    * in the given volume.    *    * @param volumeName    *   Required parameter volume name determines buckets in which volume    *   to return.    * @param startBucket    *   Optional start bucket name parameter indicating where to start    *   the bucket listing from, this key is excluded from the result.    * @param bucketPrefix    *   Optional start key parameter, restricting the response to buckets    *   that begin with the specified name.    * @param maxNumOfBuckets    *   The maximum number of buckets to return. It ensures    *   the size of the result will not exceed this limit.    * @return a list of buckets.    * @throws IOException    */
DECL|method|listBuckets (String volumeName, String startBucket, String bucketPrefix, int maxNumOfBuckets)
name|List
argument_list|<
name|KsmBucketInfo
argument_list|>
name|listBuckets
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|startBucket
parameter_list|,
name|String
name|bucketPrefix
parameter_list|,
name|int
name|maxNumOfBuckets
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

