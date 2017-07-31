begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
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
name|KsmKeyInfo
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
name|KsmKeyLocationInfo
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
DECL|field|keyName
specifier|private
specifier|final
name|String
name|keyName
decl_stmt|;
comment|/**    * Size of the data.    */
DECL|field|dataSize
specifier|private
specifier|final
name|long
name|dataSize
decl_stmt|;
comment|/**    * All the locations of this key, in an ordered list.    */
DECL|field|keyLocations
specifier|private
specifier|final
name|List
argument_list|<
name|KsmKeyLocationInfo
argument_list|>
name|keyLocations
decl_stmt|;
comment|/**    * Constructs OzoneKey from KsmKeyInfo.    *    * @param ksmKeyInfo    */
DECL|method|OzoneKey (KsmKeyInfo ksmKeyInfo)
specifier|public
name|OzoneKey
parameter_list|(
name|KsmKeyInfo
name|ksmKeyInfo
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|ksmKeyInfo
operator|.
name|getVolumeName
argument_list|()
expr_stmt|;
name|this
operator|.
name|bucketName
operator|=
name|ksmKeyInfo
operator|.
name|getBucketName
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyName
operator|=
name|ksmKeyInfo
operator|.
name|getKeyName
argument_list|()
expr_stmt|;
name|this
operator|.
name|dataSize
operator|=
name|ksmKeyInfo
operator|.
name|getDataSize
argument_list|()
expr_stmt|;
name|this
operator|.
name|keyLocations
operator|=
name|ksmKeyInfo
operator|.
name|getKeyLocationList
argument_list|()
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
comment|/**    * Retruns the list of the key locations.    *    * @return key locations    */
DECL|method|getKeyLocations ()
specifier|public
name|List
argument_list|<
name|KsmKeyLocationInfo
argument_list|>
name|getKeyLocations
parameter_list|()
block|{
return|return
name|keyLocations
return|;
block|}
block|}
end_class

end_unit

