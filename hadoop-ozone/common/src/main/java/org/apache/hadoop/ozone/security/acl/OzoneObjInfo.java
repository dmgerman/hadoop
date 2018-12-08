begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security.acl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|security
operator|.
name|acl
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
name|OzoneConsts
import|;
end_import

begin_comment
comment|/**  * Class representing an ozone object.  */
end_comment

begin_class
DECL|class|OzoneObjInfo
specifier|public
specifier|final
class|class
name|OzoneObjInfo
extends|extends
name|OzoneObj
block|{
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
DECL|field|bucketName
specifier|private
specifier|final
name|String
name|bucketName
decl_stmt|;
DECL|field|keyName
specifier|private
specifier|final
name|String
name|keyName
decl_stmt|;
DECL|method|OzoneObjInfo (ResourceType resType, StoreType storeType, String volumeName, String bucketName, String keyName)
specifier|private
name|OzoneObjInfo
parameter_list|(
name|ResourceType
name|resType
parameter_list|,
name|StoreType
name|storeType
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|keyName
parameter_list|)
block|{
name|super
argument_list|(
name|resType
argument_list|,
name|storeType
argument_list|)
expr_stmt|;
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
name|keyName
operator|=
name|keyName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
switch|switch
condition|(
name|getResourceType
argument_list|()
condition|)
block|{
case|case
name|VOLUME
case|:
return|return
name|getVolumeName
argument_list|()
return|;
case|case
name|BUCKET
case|:
return|return
name|getVolumeName
argument_list|()
operator|+
name|OzoneConsts
operator|.
name|OZONE_URI_DELIMITER
operator|+
name|getBucketName
argument_list|()
return|;
case|case
name|KEY
case|:
return|return
name|getVolumeName
argument_list|()
operator|+
name|OzoneConsts
operator|.
name|OZONE_URI_DELIMITER
operator|+
name|getBucketName
argument_list|()
operator|+
name|OzoneConsts
operator|.
name|OZONE_URI_DELIMITER
operator|+
name|getKeyName
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown resource "
operator|+
literal|"type"
operator|+
name|getResourceType
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
comment|/**    * Inner builder class.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|resType
specifier|private
name|OzoneObj
operator|.
name|ResourceType
name|resType
decl_stmt|;
DECL|field|storeType
specifier|private
name|OzoneObj
operator|.
name|StoreType
name|storeType
decl_stmt|;
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
DECL|method|newBuilder ()
specifier|public
specifier|static
name|Builder
name|newBuilder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|method|setResType (OzoneObj.ResourceType res)
specifier|public
name|Builder
name|setResType
parameter_list|(
name|OzoneObj
operator|.
name|ResourceType
name|res
parameter_list|)
block|{
name|this
operator|.
name|resType
operator|=
name|res
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setStoreType (OzoneObj.StoreType store)
specifier|public
name|Builder
name|setStoreType
parameter_list|(
name|OzoneObj
operator|.
name|StoreType
name|store
parameter_list|)
block|{
name|this
operator|.
name|storeType
operator|=
name|store
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setVolumeName (String volume)
specifier|public
name|Builder
name|setVolumeName
parameter_list|(
name|String
name|volume
parameter_list|)
block|{
name|this
operator|.
name|volumeName
operator|=
name|volume
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setBucketName (String bucket)
specifier|public
name|Builder
name|setBucketName
parameter_list|(
name|String
name|bucket
parameter_list|)
block|{
name|this
operator|.
name|bucketName
operator|=
name|bucket
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setKeyName (String key)
specifier|public
name|Builder
name|setKeyName
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|this
operator|.
name|keyName
operator|=
name|key
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|OzoneObjInfo
name|build
parameter_list|()
block|{
return|return
operator|new
name|OzoneObjInfo
argument_list|(
name|resType
argument_list|,
name|storeType
argument_list|,
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

