begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.helpers
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
name|helpers
package|;
end_package

begin_comment
comment|/**  * Class which holds information about the response of initiate multipart  * upload request.  */
end_comment

begin_class
DECL|class|OmMultipartInfo
specifier|public
class|class
name|OmMultipartInfo
block|{
DECL|field|volumeName
specifier|private
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
name|String
name|bucketName
decl_stmt|;
DECL|field|keyName
specifier|private
name|String
name|keyName
decl_stmt|;
DECL|field|uploadID
specifier|private
name|String
name|uploadID
decl_stmt|;
comment|/**    * Construct OmMultipartInfo object which holds information about the    * response from initiate multipart upload request.    * @param volume    * @param bucket    * @param key    * @param id    */
DECL|method|OmMultipartInfo (String volume, String bucket, String key, String id)
specifier|public
name|OmMultipartInfo
parameter_list|(
name|String
name|volume
parameter_list|,
name|String
name|bucket
parameter_list|,
name|String
name|key
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volume
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|bucket
expr_stmt|;
name|this
operator|.
name|keyName
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|uploadID
operator|=
name|id
expr_stmt|;
block|}
comment|/**    * Return volume name.    * @return volumeName    */
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|volumeName
return|;
block|}
comment|/**    * Return bucket name.    * @return bucketName    */
DECL|method|getBucketName ()
specifier|public
name|String
name|getBucketName
parameter_list|()
block|{
return|return
name|bucketName
return|;
block|}
comment|/**    * Return key name.    * @return keyName    */
DECL|method|getKeyName ()
specifier|public
name|String
name|getKeyName
parameter_list|()
block|{
return|return
name|keyName
return|;
block|}
comment|/**    * Return uploadID.    * @return uploadID    */
DECL|method|getUploadID ()
specifier|public
name|String
name|getUploadID
parameter_list|()
block|{
return|return
name|uploadID
return|;
block|}
block|}
end_class

end_unit

