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
name|VolumeInfo
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
name|HashMap
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
name|OzoneClient
name|client
decl_stmt|;
comment|/**    * Constructor for OzoneVolume.    */
DECL|method|OzoneVolume (OzoneClient client)
specifier|public
name|OzoneVolume
parameter_list|(
name|OzoneClient
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
DECL|method|OzoneVolume (VolumeInfo volInfo, OzoneClient client)
specifier|public
name|OzoneVolume
parameter_list|(
name|VolumeInfo
name|volInfo
parameter_list|,
name|OzoneClient
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
name|OzoneClient
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

