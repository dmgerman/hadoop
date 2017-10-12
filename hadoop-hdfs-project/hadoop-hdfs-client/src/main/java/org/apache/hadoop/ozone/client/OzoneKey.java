begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * A class that encapsulates OzoneKey.  */
end_comment

begin_class
DECL|class|OzoneKey
specifier|public
class|class
name|OzoneKey
block|{
comment|/**    * Name of the Volume the Key belongs to.    */
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
comment|/**    * Name of the Bucket the Key belongs to.    */
DECL|field|bucketName
specifier|private
specifier|final
name|String
name|bucketName
decl_stmt|;
comment|/**    * Name of the Key.    */
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * Size of the data.    */
DECL|field|dataSize
specifier|private
specifier|final
name|long
name|dataSize
decl_stmt|;
comment|/**    * Creation time of the key.    */
DECL|field|creationTime
specifier|private
name|long
name|creationTime
decl_stmt|;
comment|/**    * Modification time of the key.    */
DECL|field|modificationTime
specifier|private
name|long
name|modificationTime
decl_stmt|;
comment|/**    * Constructs OzoneKey from KsmKeyInfo.    *    */
DECL|method|OzoneKey (String volumeName, String bucketName, String keyName, long size, long creationTime, long modificationTime)
specifier|public
name|OzoneKey
parameter_list|(
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|,
name|long
name|size
parameter_list|,
name|long
name|creationTime
parameter_list|,
name|long
name|modificationTime
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|bucketName
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|dataSize
operator|=
name|size
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|modificationTime
operator|=
name|modificationTime
expr_stmt|;
block|}
comment|/**    * Returns Volume Name associated with the Key.    *    * @return volumeName    */
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
comment|/**    * Returns Bucket Name associated with the Key.    *    * @return bucketName    */
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
comment|/**    * Returns the Key Name.    *    * @return keyName    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**    * Returns the size of the data.    *    * @return dataSize    */
DECL|method|getDataSize ()
specifier|public
name|long
name|getDataSize
parameter_list|()
block|{
return|return
name|dataSize
return|;
block|}
comment|/**    * Returns the creation time of the key.    *    * @return creation time    */
DECL|method|getCreationTime ()
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
comment|/**    * Returns the modification time of the key.    *    * @return modification time    */
DECL|method|getModificationTime ()
specifier|public
name|long
name|getModificationTime
parameter_list|()
block|{
return|return
name|modificationTime
return|;
block|}
block|}
end_class

end_unit

