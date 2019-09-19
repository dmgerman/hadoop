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

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
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
name|client
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
name|hdds
operator|.
name|client
operator|.
name|ReplicationType
import|;
end_import

begin_comment
comment|/**  * Information about one initialized upload.  */
end_comment

begin_class
DECL|class|OzoneMultipartUpload
specifier|public
class|class
name|OzoneMultipartUpload
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
DECL|field|uploadId
specifier|private
name|String
name|uploadId
decl_stmt|;
DECL|field|creationTime
specifier|private
name|Instant
name|creationTime
decl_stmt|;
DECL|field|replicationType
specifier|private
name|ReplicationType
name|replicationType
decl_stmt|;
DECL|field|replicationFactor
specifier|private
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
DECL|method|OzoneMultipartUpload (String volumeName, String bucketName, String keyName, String uploadId, Instant creationTime, ReplicationType replicationType, ReplicationFactor replicationFactor)
specifier|public
name|OzoneMultipartUpload
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
name|String
name|uploadId
parameter_list|,
name|Instant
name|creationTime
parameter_list|,
name|ReplicationType
name|replicationType
parameter_list|,
name|ReplicationFactor
name|replicationFactor
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
name|uploadId
operator|=
name|uploadId
expr_stmt|;
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
name|this
operator|.
name|replicationType
operator|=
name|replicationType
expr_stmt|;
name|this
operator|.
name|replicationFactor
operator|=
name|replicationFactor
expr_stmt|;
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
DECL|method|getUploadId ()
specifier|public
name|String
name|getUploadId
parameter_list|()
block|{
return|return
name|uploadId
return|;
block|}
DECL|method|getCreationTime ()
specifier|public
name|Instant
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
DECL|method|setCreationTime (Instant creationTime)
specifier|public
name|void
name|setCreationTime
parameter_list|(
name|Instant
name|creationTime
parameter_list|)
block|{
name|this
operator|.
name|creationTime
operator|=
name|creationTime
expr_stmt|;
block|}
DECL|method|getReplicationType ()
specifier|public
name|ReplicationType
name|getReplicationType
parameter_list|()
block|{
return|return
name|replicationType
return|;
block|}
DECL|method|getReplicationFactor ()
specifier|public
name|ReplicationFactor
name|getReplicationFactor
parameter_list|()
block|{
return|return
name|replicationFactor
return|;
block|}
block|}
end_class

end_unit

