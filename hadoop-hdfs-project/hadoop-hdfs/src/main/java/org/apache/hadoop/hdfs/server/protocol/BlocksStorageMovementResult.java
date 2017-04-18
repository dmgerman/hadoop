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

begin_comment
comment|/**  * This class represents, movement status of a set of blocks associated to a  * track Id.  */
end_comment

begin_class
DECL|class|BlocksStorageMovementResult
specifier|public
class|class
name|BlocksStorageMovementResult
block|{
DECL|field|trackId
specifier|private
specifier|final
name|long
name|trackId
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|Status
name|status
decl_stmt|;
comment|/**    * SUCCESS - If all the blocks associated to track id has moved successfully    * or maximum possible movements done.    *    *<p>    * FAILURE - If any of its(trackId) blocks movement failed and requires to    * retry these failed blocks movements. Example selected target node is no    * more running or no space. So, retrying by selecting new target node might    * work.    *    *<p>    * IN_PROGRESS - If all or some of the blocks associated to track id are    * still moving.    */
DECL|enum|Status
specifier|public
specifier|static
enum|enum
name|Status
block|{
DECL|enumConstant|SUCCESS
DECL|enumConstant|FAILURE
DECL|enumConstant|IN_PROGRESS
name|SUCCESS
block|,
name|FAILURE
block|,
name|IN_PROGRESS
block|;   }
comment|/**    * BlocksStorageMovementResult constructor.    *    * @param trackId    *          tracking identifier    * @param status    *          block movement status    */
DECL|method|BlocksStorageMovementResult (long trackId, Status status)
specifier|public
name|BlocksStorageMovementResult
parameter_list|(
name|long
name|trackId
parameter_list|,
name|Status
name|status
parameter_list|)
block|{
name|this
operator|.
name|trackId
operator|=
name|trackId
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
DECL|method|getTrackId ()
specifier|public
name|long
name|getTrackId
parameter_list|()
block|{
return|return
name|trackId
return|;
block|}
DECL|method|getStatus ()
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|()
operator|.
name|append
argument_list|(
literal|"BlocksStorageMovementResult(\n  "
argument_list|)
operator|.
name|append
argument_list|(
literal|"track id: "
argument_list|)
operator|.
name|append
argument_list|(
name|trackId
argument_list|)
operator|.
name|append
argument_list|(
literal|"  status: "
argument_list|)
operator|.
name|append
argument_list|(
name|status
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

