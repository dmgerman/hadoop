begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.lock
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
operator|.
name|lock
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_KEY_PREFIX
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_PREFIX
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_S3_PREFIX
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_S3_SECRET
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
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OM_USER_PREFIX
import|;
end_import

begin_comment
comment|/**  * Utility class contains helper functions required for OM lock.  */
end_comment

begin_class
DECL|class|OzoneManagerLockUtil
specifier|final
class|class
name|OzoneManagerLockUtil
block|{
DECL|method|OzoneManagerLockUtil ()
specifier|private
name|OzoneManagerLockUtil
parameter_list|()
block|{   }
comment|/**    * Generate resource lock name for the given resource name.    *    * @param resource    * @param resourceName    */
DECL|method|generateResourceLockName ( OzoneManagerLock.Resource resource, String resourceName)
specifier|public
specifier|static
name|String
name|generateResourceLockName
parameter_list|(
name|OzoneManagerLock
operator|.
name|Resource
name|resource
parameter_list|,
name|String
name|resourceName
parameter_list|)
block|{
if|if
condition|(
name|resource
operator|==
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|S3_BUCKET
condition|)
block|{
return|return
name|OM_S3_PREFIX
operator|+
name|resourceName
return|;
block|}
elseif|else
if|if
condition|(
name|resource
operator|==
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|VOLUME
condition|)
block|{
return|return
name|OM_KEY_PREFIX
operator|+
name|resourceName
return|;
block|}
elseif|else
if|if
condition|(
name|resource
operator|==
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|USER
condition|)
block|{
return|return
name|OM_USER_PREFIX
operator|+
name|resourceName
return|;
block|}
elseif|else
if|if
condition|(
name|resource
operator|==
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|S3_SECRET
condition|)
block|{
return|return
name|OM_S3_SECRET
operator|+
name|resourceName
return|;
block|}
elseif|else
if|if
condition|(
name|resource
operator|==
name|OzoneManagerLock
operator|.
name|Resource
operator|.
name|PREFIX
condition|)
block|{
return|return
name|OM_PREFIX
operator|+
name|resourceName
return|;
block|}
else|else
block|{
comment|// This is for developers who mistakenly call this method with resource
comment|// bucket type, as for bucket type we need bucket and volumeName.
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Bucket resource type is passed, "
operator|+
literal|"to get BucketResourceLockName, use generateBucketLockName method"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Generate bucket lock name.    * @param volumeName    * @param bucketName    */
DECL|method|generateBucketLockName (String volumeName, String bucketName)
specifier|public
specifier|static
name|String
name|generateBucketLockName
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|)
block|{
return|return
name|OM_KEY_PREFIX
operator|+
name|volumeName
operator|+
name|OM_KEY_PREFIX
operator|+
name|bucketName
return|;
block|}
block|}
end_class

end_unit

