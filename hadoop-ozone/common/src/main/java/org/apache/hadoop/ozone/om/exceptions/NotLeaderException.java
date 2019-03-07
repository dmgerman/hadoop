begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om.exceptions
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
name|exceptions
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Exception thrown by  * {@link org.apache.hadoop.ozone.om.protocolPB.OzoneManagerProtocolPB} when  * a read request is received by a non leader OM node.  */
end_comment

begin_class
DECL|class|NotLeaderException
specifier|public
class|class
name|NotLeaderException
extends|extends
name|IOException
block|{
DECL|field|currentPeerId
specifier|private
specifier|final
name|String
name|currentPeerId
decl_stmt|;
DECL|field|leaderPeerId
specifier|private
specifier|final
name|String
name|leaderPeerId
decl_stmt|;
DECL|method|NotLeaderException (String currentPeerIdStr)
specifier|public
name|NotLeaderException
parameter_list|(
name|String
name|currentPeerIdStr
parameter_list|)
block|{
name|super
argument_list|(
literal|"OM "
operator|+
name|currentPeerIdStr
operator|+
literal|" is not the leader. Could not "
operator|+
literal|"determine the leader node."
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentPeerId
operator|=
name|currentPeerIdStr
expr_stmt|;
name|this
operator|.
name|leaderPeerId
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|NotLeaderException (String currentPeerIdStr, String suggestedLeaderPeerIdStr)
specifier|public
name|NotLeaderException
parameter_list|(
name|String
name|currentPeerIdStr
parameter_list|,
name|String
name|suggestedLeaderPeerIdStr
parameter_list|)
block|{
name|super
argument_list|(
literal|"OM "
operator|+
name|currentPeerIdStr
operator|+
literal|" is not the leader. Suggested leader is "
operator|+
name|suggestedLeaderPeerIdStr
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentPeerId
operator|=
name|currentPeerIdStr
expr_stmt|;
name|this
operator|.
name|leaderPeerId
operator|=
name|suggestedLeaderPeerIdStr
expr_stmt|;
block|}
DECL|method|getSuggestedLeaderNodeId ()
specifier|public
name|String
name|getSuggestedLeaderNodeId
parameter_list|()
block|{
return|return
name|leaderPeerId
return|;
block|}
block|}
end_class

end_unit

