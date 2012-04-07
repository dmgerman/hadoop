begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|protocol
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

begin_comment
comment|/**  * Response to a journal fence request. See {@link JournalProtocol#fence}  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FenceResponse
specifier|public
class|class
name|FenceResponse
block|{
DECL|field|previousEpoch
specifier|private
specifier|final
name|long
name|previousEpoch
decl_stmt|;
DECL|field|lastTransactionId
specifier|private
specifier|final
name|long
name|lastTransactionId
decl_stmt|;
DECL|field|isInSync
specifier|private
specifier|final
name|boolean
name|isInSync
decl_stmt|;
DECL|method|FenceResponse (long previousEpoch, long lastTransId, boolean inSync)
specifier|public
name|FenceResponse
parameter_list|(
name|long
name|previousEpoch
parameter_list|,
name|long
name|lastTransId
parameter_list|,
name|boolean
name|inSync
parameter_list|)
block|{
name|this
operator|.
name|previousEpoch
operator|=
name|previousEpoch
expr_stmt|;
name|this
operator|.
name|lastTransactionId
operator|=
name|lastTransId
expr_stmt|;
name|this
operator|.
name|isInSync
operator|=
name|inSync
expr_stmt|;
block|}
DECL|method|isInSync ()
specifier|public
name|boolean
name|isInSync
parameter_list|()
block|{
return|return
name|isInSync
return|;
block|}
DECL|method|getLastTransactionId ()
specifier|public
name|long
name|getLastTransactionId
parameter_list|()
block|{
return|return
name|lastTransactionId
return|;
block|}
DECL|method|getPreviousEpoch ()
specifier|public
name|long
name|getPreviousEpoch
parameter_list|()
block|{
return|return
name|previousEpoch
return|;
block|}
block|}
end_class

end_unit

