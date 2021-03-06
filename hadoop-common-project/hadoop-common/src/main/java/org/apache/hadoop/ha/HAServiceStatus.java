begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|ha
operator|.
name|HAServiceProtocol
operator|.
name|HAServiceState
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|HAServiceStatus
specifier|public
class|class
name|HAServiceStatus
block|{
DECL|field|state
specifier|private
name|HAServiceState
name|state
decl_stmt|;
DECL|field|readyToBecomeActive
specifier|private
name|boolean
name|readyToBecomeActive
decl_stmt|;
DECL|field|notReadyReason
specifier|private
name|String
name|notReadyReason
decl_stmt|;
DECL|method|HAServiceStatus (HAServiceState state)
specifier|public
name|HAServiceStatus
parameter_list|(
name|HAServiceState
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
DECL|method|getState ()
specifier|public
name|HAServiceState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|setReadyToBecomeActive ()
specifier|public
name|HAServiceStatus
name|setReadyToBecomeActive
parameter_list|()
block|{
name|this
operator|.
name|readyToBecomeActive
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|notReadyReason
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setNotReadyToBecomeActive (String reason)
specifier|public
name|HAServiceStatus
name|setNotReadyToBecomeActive
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|readyToBecomeActive
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|notReadyReason
operator|=
name|reason
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|isReadyToBecomeActive ()
specifier|public
name|boolean
name|isReadyToBecomeActive
parameter_list|()
block|{
return|return
name|readyToBecomeActive
return|;
block|}
DECL|method|getNotReadyReason ()
specifier|public
name|String
name|getNotReadyReason
parameter_list|()
block|{
return|return
name|notReadyReason
return|;
block|}
block|}
end_class

end_unit

