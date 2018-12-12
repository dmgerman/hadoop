begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationType
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
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
operator|.
name|ReplicationFactor
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
name|audit
operator|.
name|Auditable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_comment
comment|/**  * Args for key. Client use this to specify key's attributes on  key creation  * (putKey()).  */
end_comment

begin_class
DECL|class|OmKeyArgs
specifier|public
specifier|final
class|class
name|OmKeyArgs
implements|implements
name|Auditable
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
DECL|field|dataSize
specifier|private
name|long
name|dataSize
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
specifier|final
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|field|locationInfoList
specifier|private
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
decl_stmt|;
DECL|field|isMultipartKey
specifier|private
specifier|final
name|boolean
name|isMultipartKey
decl_stmt|;
DECL|field|multipartUploadID
specifier|private
specifier|final
name|String
name|multipartUploadID
decl_stmt|;
DECL|field|multipartUploadPartNumber
specifier|private
specifier|final
name|int
name|multipartUploadPartNumber
decl_stmt|;
DECL|method|OmKeyArgs (String volumeName, String bucketName, String keyName, long dataSize, ReplicationType type, ReplicationFactor factor, List<OmKeyLocationInfo> locationInfoList, boolean isMultipart, String uploadID, int partNumber)
specifier|private
name|OmKeyArgs
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
name|dataSize
parameter_list|,
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|factor
parameter_list|,
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
parameter_list|,
name|boolean
name|isMultipart
parameter_list|,
name|String
name|uploadID
parameter_list|,
name|int
name|partNumber
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
name|keyName
operator|=
name|keyName
expr_stmt|;
name|this
operator|.
name|dataSize
operator|=
name|dataSize
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
name|this
operator|.
name|locationInfoList
operator|=
name|locationInfoList
expr_stmt|;
name|this
operator|.
name|isMultipartKey
operator|=
name|isMultipart
expr_stmt|;
name|this
operator|.
name|multipartUploadID
operator|=
name|uploadID
expr_stmt|;
name|this
operator|.
name|multipartUploadPartNumber
operator|=
name|partNumber
expr_stmt|;
block|}
DECL|method|getIsMultipartKey ()
specifier|public
name|boolean
name|getIsMultipartKey
parameter_list|()
block|{
return|return
name|isMultipartKey
return|;
block|}
DECL|method|getMultipartUploadID ()
specifier|public
name|String
name|getMultipartUploadID
parameter_list|()
block|{
return|return
name|multipartUploadID
return|;
block|}
DECL|method|getMultipartUploadPartNumber ()
specifier|public
name|int
name|getMultipartUploadPartNumber
parameter_list|()
block|{
return|return
name|multipartUploadPartNumber
return|;
block|}
DECL|method|getType ()
specifier|public
name|ReplicationType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|getFactor ()
specifier|public
name|ReplicationFactor
name|getFactor
parameter_list|()
block|{
return|return
name|factor
return|;
block|}
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
DECL|method|setDataSize (long size)
specifier|public
name|void
name|setDataSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|dataSize
operator|=
name|size
expr_stmt|;
block|}
DECL|method|setLocationInfoList (List<OmKeyLocationInfo> locationInfoList)
specifier|public
name|void
name|setLocationInfoList
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
parameter_list|)
block|{
name|this
operator|.
name|locationInfoList
operator|=
name|locationInfoList
expr_stmt|;
block|}
DECL|method|getLocationInfoList ()
specifier|public
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|getLocationInfoList
parameter_list|()
block|{
return|return
name|locationInfoList
return|;
block|}
annotation|@
name|Override
DECL|method|toAuditMap ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toAuditMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|auditMap
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|VOLUME
argument_list|,
name|this
operator|.
name|volumeName
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|BUCKET
argument_list|,
name|this
operator|.
name|bucketName
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|KEY
argument_list|,
name|this
operator|.
name|keyName
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|DATA_SIZE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|dataSize
argument_list|)
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|REPLICATION_TYPE
argument_list|,
operator|(
name|this
operator|.
name|type
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|type
operator|.
name|name
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|REPLICATION_FACTOR
argument_list|,
operator|(
name|this
operator|.
name|factor
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|factor
operator|.
name|name
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
name|auditMap
operator|.
name|put
argument_list|(
name|OzoneConsts
operator|.
name|KEY_LOCATION_INFO
argument_list|,
operator|(
name|this
operator|.
name|locationInfoList
operator|!=
literal|null
operator|)
condition|?
name|locationInfoList
operator|.
name|toString
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
return|return
name|auditMap
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|addLocationInfo (OmKeyLocationInfo locationInfo)
specifier|public
name|void
name|addLocationInfo
parameter_list|(
name|OmKeyLocationInfo
name|locationInfo
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|locationInfoList
operator|==
literal|null
condition|)
block|{
name|locationInfoList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|locationInfoList
operator|.
name|add
argument_list|(
name|locationInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * Builder class of OmKeyArgs.    */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
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
DECL|field|dataSize
specifier|private
name|long
name|dataSize
decl_stmt|;
DECL|field|type
specifier|private
name|ReplicationType
name|type
decl_stmt|;
DECL|field|factor
specifier|private
name|ReplicationFactor
name|factor
decl_stmt|;
DECL|field|locationInfoList
specifier|private
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfoList
decl_stmt|;
DECL|field|isMultipartKey
specifier|private
name|boolean
name|isMultipartKey
decl_stmt|;
DECL|field|multipartUploadID
specifier|private
name|String
name|multipartUploadID
decl_stmt|;
DECL|field|multipartUploadPartNumber
specifier|private
name|int
name|multipartUploadPartNumber
decl_stmt|;
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
DECL|method|setDataSize (long size)
specifier|public
name|Builder
name|setDataSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|dataSize
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setType (ReplicationType replicationType)
specifier|public
name|Builder
name|setType
parameter_list|(
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|replicationType
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFactor (ReplicationFactor replicationFactor)
specifier|public
name|Builder
name|setFactor
parameter_list|(
name|ReplicationFactor
name|replicationFactor
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|replicationFactor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setLocationInfoList (List<OmKeyLocationInfo> locationInfos)
specifier|public
name|Builder
name|setLocationInfoList
parameter_list|(
name|List
argument_list|<
name|OmKeyLocationInfo
argument_list|>
name|locationInfos
parameter_list|)
block|{
name|this
operator|.
name|locationInfoList
operator|=
name|locationInfos
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIsMultipartKey (boolean isMultipart)
specifier|public
name|Builder
name|setIsMultipartKey
parameter_list|(
name|boolean
name|isMultipart
parameter_list|)
block|{
name|this
operator|.
name|isMultipartKey
operator|=
name|isMultipart
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMultipartUploadID (String uploadID)
specifier|public
name|Builder
name|setMultipartUploadID
parameter_list|(
name|String
name|uploadID
parameter_list|)
block|{
name|this
operator|.
name|multipartUploadID
operator|=
name|uploadID
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setMultipartUploadPartNumber (int partNumber)
specifier|public
name|Builder
name|setMultipartUploadPartNumber
parameter_list|(
name|int
name|partNumber
parameter_list|)
block|{
name|this
operator|.
name|multipartUploadPartNumber
operator|=
name|partNumber
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|OmKeyArgs
name|build
parameter_list|()
block|{
return|return
operator|new
name|OmKeyArgs
argument_list|(
name|volumeName
argument_list|,
name|bucketName
argument_list|,
name|keyName
argument_list|,
name|dataSize
argument_list|,
name|type
argument_list|,
name|factor
argument_list|,
name|locationInfoList
argument_list|,
name|isMultipartKey
argument_list|,
name|multipartUploadID
argument_list|,
name|multipartUploadPartNumber
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

