begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ApplicationsRequestScope
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|AMCommand
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationAccessType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationResourceUsageReport
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ContainerState
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|FinalApplicationStatus
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LogAggregationStatus
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|NodeState
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|QueueACL
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|QueueState
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ReservationRequestInterpreter
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|YarnApplicationAttemptState
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|YarnApplicationState
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|AMCommandProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ApplicationAccessTypeProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ApplicationResourceUsageReportProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ContainerStateProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|FinalApplicationStatusProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|LocalResourceTypeProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|LocalResourceVisibilityProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|LogAggregationStatusProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|NodeIdProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|NodeStateProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|QueueACLProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|QueueStateProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|ReservationRequestInterpreterProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|YarnApplicationAttemptStateProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnProtos
operator|.
name|YarnApplicationStateProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServiceProtos
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ProtoUtils
specifier|public
class|class
name|ProtoUtils
block|{
comment|/*    * ContainerState    */
DECL|field|CONTAINER_STATE_PREFIX
specifier|private
specifier|static
name|String
name|CONTAINER_STATE_PREFIX
init|=
literal|"C_"
decl_stmt|;
DECL|method|convertToProtoFormat (ContainerState e)
specifier|public
specifier|static
name|ContainerStateProto
name|convertToProtoFormat
parameter_list|(
name|ContainerState
name|e
parameter_list|)
block|{
return|return
name|ContainerStateProto
operator|.
name|valueOf
argument_list|(
name|CONTAINER_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (ContainerStateProto e)
specifier|public
specifier|static
name|ContainerState
name|convertFromProtoFormat
parameter_list|(
name|ContainerStateProto
name|e
parameter_list|)
block|{
return|return
name|ContainerState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|CONTAINER_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * NodeState    */
DECL|field|NODE_STATE_PREFIX
specifier|private
specifier|static
name|String
name|NODE_STATE_PREFIX
init|=
literal|"NS_"
decl_stmt|;
DECL|method|convertToProtoFormat (NodeState e)
specifier|public
specifier|static
name|NodeStateProto
name|convertToProtoFormat
parameter_list|(
name|NodeState
name|e
parameter_list|)
block|{
return|return
name|NodeStateProto
operator|.
name|valueOf
argument_list|(
name|NODE_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (NodeStateProto e)
specifier|public
specifier|static
name|NodeState
name|convertFromProtoFormat
parameter_list|(
name|NodeStateProto
name|e
parameter_list|)
block|{
return|return
name|NodeState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|NODE_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * NodeId    */
DECL|method|convertToProtoFormat (NodeId e)
specifier|public
specifier|static
name|NodeIdProto
name|convertToProtoFormat
parameter_list|(
name|NodeId
name|e
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeIdPBImpl
operator|)
name|e
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (NodeIdProto e)
specifier|public
specifier|static
name|NodeId
name|convertFromProtoFormat
parameter_list|(
name|NodeIdProto
name|e
parameter_list|)
block|{
return|return
operator|new
name|NodeIdPBImpl
argument_list|(
name|e
argument_list|)
return|;
block|}
comment|/*    * YarnApplicationState    */
DECL|method|convertToProtoFormat (YarnApplicationState e)
specifier|public
specifier|static
name|YarnApplicationStateProto
name|convertToProtoFormat
parameter_list|(
name|YarnApplicationState
name|e
parameter_list|)
block|{
return|return
name|YarnApplicationStateProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (YarnApplicationStateProto e)
specifier|public
specifier|static
name|YarnApplicationState
name|convertFromProtoFormat
parameter_list|(
name|YarnApplicationStateProto
name|e
parameter_list|)
block|{
return|return
name|YarnApplicationState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * YarnApplicationAttemptState    */
DECL|field|YARN_APPLICATION_ATTEMPT_STATE_PREFIX
specifier|private
specifier|static
name|String
name|YARN_APPLICATION_ATTEMPT_STATE_PREFIX
init|=
literal|"APP_ATTEMPT_"
decl_stmt|;
DECL|method|convertToProtoFormat ( YarnApplicationAttemptState e)
specifier|public
specifier|static
name|YarnApplicationAttemptStateProto
name|convertToProtoFormat
parameter_list|(
name|YarnApplicationAttemptState
name|e
parameter_list|)
block|{
return|return
name|YarnApplicationAttemptStateProto
operator|.
name|valueOf
argument_list|(
name|YARN_APPLICATION_ATTEMPT_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( YarnApplicationAttemptStateProto e)
specifier|public
specifier|static
name|YarnApplicationAttemptState
name|convertFromProtoFormat
parameter_list|(
name|YarnApplicationAttemptStateProto
name|e
parameter_list|)
block|{
return|return
name|YarnApplicationAttemptState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|YARN_APPLICATION_ATTEMPT_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * ApplicationsRequestScope    */
specifier|public
specifier|static
name|YarnServiceProtos
operator|.
name|ApplicationsRequestScopeProto
DECL|method|convertToProtoFormat (ApplicationsRequestScope e)
name|convertToProtoFormat
parameter_list|(
name|ApplicationsRequestScope
name|e
parameter_list|)
block|{
return|return
name|YarnServiceProtos
operator|.
name|ApplicationsRequestScopeProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (YarnServiceProtos.ApplicationsRequestScopeProto e)
specifier|public
specifier|static
name|ApplicationsRequestScope
name|convertFromProtoFormat
parameter_list|(
name|YarnServiceProtos
operator|.
name|ApplicationsRequestScopeProto
name|e
parameter_list|)
block|{
return|return
name|ApplicationsRequestScope
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * ApplicationResourceUsageReport    */
DECL|method|convertToProtoFormat (ApplicationResourceUsageReport e)
specifier|public
specifier|static
name|ApplicationResourceUsageReportProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationResourceUsageReport
name|e
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationResourceUsageReportPBImpl
operator|)
name|e
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (ApplicationResourceUsageReportProto e)
specifier|public
specifier|static
name|ApplicationResourceUsageReport
name|convertFromProtoFormat
parameter_list|(
name|ApplicationResourceUsageReportProto
name|e
parameter_list|)
block|{
return|return
operator|new
name|ApplicationResourceUsageReportPBImpl
argument_list|(
name|e
argument_list|)
return|;
block|}
comment|/*    * FinalApplicationStatus    */
DECL|field|FINAL_APPLICATION_STATUS_PREFIX
specifier|private
specifier|static
name|String
name|FINAL_APPLICATION_STATUS_PREFIX
init|=
literal|"APP_"
decl_stmt|;
DECL|method|convertToProtoFormat (FinalApplicationStatus e)
specifier|public
specifier|static
name|FinalApplicationStatusProto
name|convertToProtoFormat
parameter_list|(
name|FinalApplicationStatus
name|e
parameter_list|)
block|{
return|return
name|FinalApplicationStatusProto
operator|.
name|valueOf
argument_list|(
name|FINAL_APPLICATION_STATUS_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (FinalApplicationStatusProto e)
specifier|public
specifier|static
name|FinalApplicationStatus
name|convertFromProtoFormat
parameter_list|(
name|FinalApplicationStatusProto
name|e
parameter_list|)
block|{
return|return
name|FinalApplicationStatus
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|FINAL_APPLICATION_STATUS_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * LocalResourceType    */
DECL|method|convertToProtoFormat (LocalResourceType e)
specifier|public
specifier|static
name|LocalResourceTypeProto
name|convertToProtoFormat
parameter_list|(
name|LocalResourceType
name|e
parameter_list|)
block|{
return|return
name|LocalResourceTypeProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (LocalResourceTypeProto e)
specifier|public
specifier|static
name|LocalResourceType
name|convertFromProtoFormat
parameter_list|(
name|LocalResourceTypeProto
name|e
parameter_list|)
block|{
return|return
name|LocalResourceType
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * LocalResourceVisibility    */
DECL|method|convertToProtoFormat (LocalResourceVisibility e)
specifier|public
specifier|static
name|LocalResourceVisibilityProto
name|convertToProtoFormat
parameter_list|(
name|LocalResourceVisibility
name|e
parameter_list|)
block|{
return|return
name|LocalResourceVisibilityProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (LocalResourceVisibilityProto e)
specifier|public
specifier|static
name|LocalResourceVisibility
name|convertFromProtoFormat
parameter_list|(
name|LocalResourceVisibilityProto
name|e
parameter_list|)
block|{
return|return
name|LocalResourceVisibility
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * AMCommand    */
DECL|method|convertToProtoFormat (AMCommand e)
specifier|public
specifier|static
name|AMCommandProto
name|convertToProtoFormat
parameter_list|(
name|AMCommand
name|e
parameter_list|)
block|{
return|return
name|AMCommandProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (AMCommandProto e)
specifier|public
specifier|static
name|AMCommand
name|convertFromProtoFormat
parameter_list|(
name|AMCommandProto
name|e
parameter_list|)
block|{
return|return
name|AMCommand
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * ByteBuffer    */
DECL|method|convertFromProtoFormat (ByteString byteString)
specifier|public
specifier|static
name|ByteBuffer
name|convertFromProtoFormat
parameter_list|(
name|ByteString
name|byteString
parameter_list|)
block|{
name|int
name|capacity
init|=
name|byteString
operator|.
name|asReadOnlyByteBuffer
argument_list|()
operator|.
name|rewind
argument_list|()
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
name|capacity
index|]
decl_stmt|;
name|byteString
operator|.
name|asReadOnlyByteBuffer
argument_list|()
operator|.
name|get
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
return|return
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|b
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ByteBuffer byteBuffer)
specifier|public
specifier|static
name|ByteString
name|convertToProtoFormat
parameter_list|(
name|ByteBuffer
name|byteBuffer
parameter_list|)
block|{
comment|//    return ByteString.copyFrom((ByteBuffer)byteBuffer.duplicate().rewind());
name|int
name|oldPos
init|=
name|byteBuffer
operator|.
name|position
argument_list|()
decl_stmt|;
name|byteBuffer
operator|.
name|rewind
argument_list|()
expr_stmt|;
name|ByteString
name|bs
init|=
name|ByteString
operator|.
name|copyFrom
argument_list|(
name|byteBuffer
argument_list|)
decl_stmt|;
name|byteBuffer
operator|.
name|position
argument_list|(
name|oldPos
argument_list|)
expr_stmt|;
return|return
name|bs
return|;
block|}
comment|/*    * QueueState    */
DECL|field|QUEUE_STATE_PREFIX
specifier|private
specifier|static
name|String
name|QUEUE_STATE_PREFIX
init|=
literal|"Q_"
decl_stmt|;
DECL|method|convertToProtoFormat (QueueState e)
specifier|public
specifier|static
name|QueueStateProto
name|convertToProtoFormat
parameter_list|(
name|QueueState
name|e
parameter_list|)
block|{
return|return
name|QueueStateProto
operator|.
name|valueOf
argument_list|(
name|QUEUE_STATE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (QueueStateProto e)
specifier|public
specifier|static
name|QueueState
name|convertFromProtoFormat
parameter_list|(
name|QueueStateProto
name|e
parameter_list|)
block|{
return|return
name|QueueState
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|QUEUE_STATE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * QueueACL    */
DECL|field|QUEUE_ACL_PREFIX
specifier|private
specifier|static
name|String
name|QUEUE_ACL_PREFIX
init|=
literal|"QACL_"
decl_stmt|;
DECL|method|convertToProtoFormat (QueueACL e)
specifier|public
specifier|static
name|QueueACLProto
name|convertToProtoFormat
parameter_list|(
name|QueueACL
name|e
parameter_list|)
block|{
return|return
name|QueueACLProto
operator|.
name|valueOf
argument_list|(
name|QUEUE_ACL_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat (QueueACLProto e)
specifier|public
specifier|static
name|QueueACL
name|convertFromProtoFormat
parameter_list|(
name|QueueACLProto
name|e
parameter_list|)
block|{
return|return
name|QueueACL
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|QUEUE_ACL_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * ApplicationAccessType    */
DECL|field|APP_ACCESS_TYPE_PREFIX
specifier|private
specifier|static
name|String
name|APP_ACCESS_TYPE_PREFIX
init|=
literal|"APPACCESS_"
decl_stmt|;
DECL|method|convertToProtoFormat ( ApplicationAccessType e)
specifier|public
specifier|static
name|ApplicationAccessTypeProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationAccessType
name|e
parameter_list|)
block|{
return|return
name|ApplicationAccessTypeProto
operator|.
name|valueOf
argument_list|(
name|APP_ACCESS_TYPE_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( ApplicationAccessTypeProto e)
specifier|public
specifier|static
name|ApplicationAccessType
name|convertFromProtoFormat
parameter_list|(
name|ApplicationAccessTypeProto
name|e
parameter_list|)
block|{
return|return
name|ApplicationAccessType
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|APP_ACCESS_TYPE_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/*    * Reservation Request interpreter type    */
DECL|method|convertToProtoFormat ( ReservationRequestInterpreter e)
specifier|public
specifier|static
name|ReservationRequestInterpreterProto
name|convertToProtoFormat
parameter_list|(
name|ReservationRequestInterpreter
name|e
parameter_list|)
block|{
return|return
name|ReservationRequestInterpreterProto
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( ReservationRequestInterpreterProto e)
specifier|public
specifier|static
name|ReservationRequestInterpreter
name|convertFromProtoFormat
parameter_list|(
name|ReservationRequestInterpreterProto
name|e
parameter_list|)
block|{
return|return
name|ReservationRequestInterpreter
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/*    * Log Aggregation Status    */
DECL|field|LOG_AGGREGATION_STATUS_PREFIX
specifier|private
specifier|static
specifier|final
name|String
name|LOG_AGGREGATION_STATUS_PREFIX
init|=
literal|"LOG_"
decl_stmt|;
DECL|method|convertToProtoFormat ( LogAggregationStatus e)
specifier|public
specifier|static
name|LogAggregationStatusProto
name|convertToProtoFormat
parameter_list|(
name|LogAggregationStatus
name|e
parameter_list|)
block|{
return|return
name|LogAggregationStatusProto
operator|.
name|valueOf
argument_list|(
name|LOG_AGGREGATION_STATUS_PREFIX
operator|+
name|e
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertFromProtoFormat ( LogAggregationStatusProto e)
specifier|public
specifier|static
name|LogAggregationStatus
name|convertFromProtoFormat
parameter_list|(
name|LogAggregationStatusProto
name|e
parameter_list|)
block|{
return|return
name|LogAggregationStatus
operator|.
name|valueOf
argument_list|(
name|e
operator|.
name|name
argument_list|()
operator|.
name|replace
argument_list|(
name|LOG_AGGREGATION_STATUS_PREFIX
argument_list|,
literal|""
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

