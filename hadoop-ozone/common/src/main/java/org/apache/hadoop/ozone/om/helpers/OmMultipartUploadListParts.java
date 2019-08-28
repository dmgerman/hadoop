begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneManagerProtocolProtos
operator|.
name|PartInfo
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
name|List
import|;
end_import

begin_comment
comment|/**  * Class which is response for the list parts of a multipart upload key.  */
end_comment

begin_class
DECL|class|OmMultipartUploadListParts
specifier|public
class|class
name|OmMultipartUploadListParts
block|{
DECL|field|replicationType
specifier|private
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
decl_stmt|;
DECL|field|replicationFactor
specifier|private
name|HddsProtos
operator|.
name|ReplicationFactor
name|replicationFactor
decl_stmt|;
comment|//When a list is truncated, this element specifies the last part in the list,
comment|// as well as the value to use for the part-number-marker request parameter
comment|// in a subsequent request.
DECL|field|nextPartNumberMarker
specifier|private
name|int
name|nextPartNumberMarker
decl_stmt|;
comment|// Indicates whether the returned list of parts is truncated. A true value
comment|// indicates that the list was truncated.
comment|// A list can be truncated if the number of parts exceeds the limit
comment|// returned in the MaxParts element.
DECL|field|truncated
specifier|private
name|boolean
name|truncated
decl_stmt|;
DECL|field|partInfoList
specifier|private
specifier|final
name|List
argument_list|<
name|OmPartInfo
argument_list|>
name|partInfoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|OmMultipartUploadListParts (HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, int nextMarker, boolean truncate)
specifier|public
name|OmMultipartUploadListParts
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|int
name|nextMarker
parameter_list|,
name|boolean
name|truncate
parameter_list|)
block|{
name|this
operator|.
name|replicationType
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|replicationFactor
operator|=
name|factor
expr_stmt|;
name|this
operator|.
name|nextPartNumberMarker
operator|=
name|nextMarker
expr_stmt|;
name|this
operator|.
name|truncated
operator|=
name|truncate
expr_stmt|;
block|}
DECL|method|addPart (OmPartInfo partInfo)
specifier|public
name|void
name|addPart
parameter_list|(
name|OmPartInfo
name|partInfo
parameter_list|)
block|{
name|partInfoList
operator|.
name|add
argument_list|(
name|partInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|getReplicationType ()
specifier|public
name|HddsProtos
operator|.
name|ReplicationType
name|getReplicationType
parameter_list|()
block|{
return|return
name|replicationType
return|;
block|}
DECL|method|getNextPartNumberMarker ()
specifier|public
name|int
name|getNextPartNumberMarker
parameter_list|()
block|{
return|return
name|nextPartNumberMarker
return|;
block|}
DECL|method|isTruncated ()
specifier|public
name|boolean
name|isTruncated
parameter_list|()
block|{
return|return
name|truncated
return|;
block|}
DECL|method|setReplicationType (HddsProtos.ReplicationType replicationType)
specifier|public
name|void
name|setReplicationType
parameter_list|(
name|HddsProtos
operator|.
name|ReplicationType
name|replicationType
parameter_list|)
block|{
name|this
operator|.
name|replicationType
operator|=
name|replicationType
expr_stmt|;
block|}
DECL|method|getPartInfoList ()
specifier|public
name|List
argument_list|<
name|OmPartInfo
argument_list|>
name|getPartInfoList
parameter_list|()
block|{
return|return
name|partInfoList
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
DECL|method|addPartList (List<OmPartInfo> partInfos)
specifier|public
name|void
name|addPartList
parameter_list|(
name|List
argument_list|<
name|OmPartInfo
argument_list|>
name|partInfos
parameter_list|)
block|{
name|this
operator|.
name|partInfoList
operator|.
name|addAll
argument_list|(
name|partInfos
argument_list|)
expr_stmt|;
block|}
DECL|method|addProtoPartList (List<PartInfo> partInfos)
specifier|public
name|void
name|addProtoPartList
parameter_list|(
name|List
argument_list|<
name|PartInfo
argument_list|>
name|partInfos
parameter_list|)
block|{
name|partInfos
operator|.
name|forEach
argument_list|(
name|partInfo
lambda|->
name|partInfoList
operator|.
name|add
argument_list|(
operator|new
name|OmPartInfo
argument_list|(
name|partInfo
operator|.
name|getPartNumber
argument_list|()
argument_list|,
name|partInfo
operator|.
name|getPartName
argument_list|()
argument_list|,
name|partInfo
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|partInfo
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

