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
name|records
operator|.
name|ApplicationAttemptId
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
name|ApplicationId
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
name|ApplicationAttemptIdProto
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
name|ApplicationIdProto
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|ApplicationAttemptIdPBImpl
specifier|public
class|class
name|ApplicationAttemptIdPBImpl
extends|extends
name|ApplicationAttemptId
block|{
DECL|field|proto
name|ApplicationAttemptIdProto
name|proto
init|=
literal|null
decl_stmt|;
DECL|field|builder
name|ApplicationAttemptIdProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
init|=
literal|null
decl_stmt|;
DECL|method|ApplicationAttemptIdPBImpl ()
specifier|public
name|ApplicationAttemptIdPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|ApplicationAttemptIdProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|ApplicationAttemptIdPBImpl (ApplicationAttemptIdProto proto)
specifier|public
name|ApplicationAttemptIdPBImpl
parameter_list|(
name|ApplicationAttemptIdProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|convertFromProtoFormat
argument_list|(
name|proto
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|ApplicationAttemptIdProto
name|getProto
parameter_list|()
block|{
return|return
name|proto
return|;
block|}
annotation|@
name|Override
DECL|method|getAttemptId ()
specifier|public
name|int
name|getAttemptId
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|proto
argument_list|)
expr_stmt|;
return|return
name|proto
operator|.
name|getAttemptId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setAttemptId (int attemptId)
specifier|protected
name|void
name|setAttemptId
parameter_list|(
name|int
name|attemptId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setAttemptId
argument_list|(
name|attemptId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationId
return|;
block|}
annotation|@
name|Override
DECL|method|setApplicationId (ApplicationId appId)
specifier|public
name|void
name|setApplicationId
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
if|if
condition|(
name|appId
operator|!=
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setApplicationId
argument_list|(
name|convertToProtoFormat
argument_list|(
name|appId
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|applicationId
operator|=
name|appId
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (ApplicationIdProto p)
specifier|private
name|ApplicationIdPBImpl
name|convertFromProtoFormat
parameter_list|(
name|ApplicationIdProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|ApplicationIdPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (ApplicationId t)
specifier|private
name|ApplicationIdProto
name|convertToProtoFormat
parameter_list|(
name|ApplicationId
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|ApplicationIdPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|build ()
specifier|protected
name|void
name|build
parameter_list|()
block|{
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|builder
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

