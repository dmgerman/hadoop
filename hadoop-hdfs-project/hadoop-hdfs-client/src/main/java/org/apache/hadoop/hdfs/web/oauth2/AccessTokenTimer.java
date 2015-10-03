begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.web.oauth2
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|web
operator|.
name|oauth2
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
name|classification
operator|.
name|InterfaceStability
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
name|util
operator|.
name|Timer
import|;
end_import

begin_comment
comment|/**  * Access tokens generally expire.  This timer helps keep track of that.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|AccessTokenTimer
specifier|public
class|class
name|AccessTokenTimer
block|{
DECL|field|EXPIRE_BUFFER_MS
specifier|public
specifier|static
specifier|final
name|long
name|EXPIRE_BUFFER_MS
init|=
literal|30
operator|*
literal|1000L
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Timer
name|timer
decl_stmt|;
comment|/**    * When the current access token will expire in milliseconds since    * epoch.    */
DECL|field|nextRefreshMSSinceEpoch
specifier|private
name|long
name|nextRefreshMSSinceEpoch
decl_stmt|;
DECL|method|AccessTokenTimer ()
specifier|public
name|AccessTokenTimer
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|Timer
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    *    * @param timer Timer instance for unit testing    */
DECL|method|AccessTokenTimer (Timer timer)
specifier|public
name|AccessTokenTimer
parameter_list|(
name|Timer
name|timer
parameter_list|)
block|{
name|this
operator|.
name|timer
operator|=
name|timer
expr_stmt|;
name|this
operator|.
name|nextRefreshMSSinceEpoch
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Set when the access token will expire as reported by the oauth server,    * ie in seconds from now.    * @param expiresIn Access time expiration as reported by OAuth server    */
DECL|method|setExpiresIn (String expiresIn)
specifier|public
name|void
name|setExpiresIn
parameter_list|(
name|String
name|expiresIn
parameter_list|)
block|{
name|this
operator|.
name|nextRefreshMSSinceEpoch
operator|=
name|convertExpiresIn
argument_list|(
name|timer
argument_list|,
name|expiresIn
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set when the access token will expire in milliseconds from epoch,    * as required by the WebHDFS configuration.  This is a bit hacky and lame.    *    * @param expiresInMSSinceEpoch Access time expiration in ms since epoch.    */
DECL|method|setExpiresInMSSinceEpoch (String expiresInMSSinceEpoch)
specifier|public
name|void
name|setExpiresInMSSinceEpoch
parameter_list|(
name|String
name|expiresInMSSinceEpoch
parameter_list|)
block|{
name|this
operator|.
name|nextRefreshMSSinceEpoch
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|expiresInMSSinceEpoch
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get next time we should refresh the token.    *    * @return Next time since epoch we'll need to refresh the token.    */
DECL|method|getNextRefreshMSSinceEpoch ()
specifier|public
name|long
name|getNextRefreshMSSinceEpoch
parameter_list|()
block|{
return|return
name|nextRefreshMSSinceEpoch
return|;
block|}
comment|/**    * Return true if the current token has expired or will expire within the    * EXPIRE_BUFFER_MS (to give ample wiggle room for the call to be made to    * the server).    */
DECL|method|shouldRefresh ()
specifier|public
name|boolean
name|shouldRefresh
parameter_list|()
block|{
name|long
name|lowerLimit
init|=
name|nextRefreshMSSinceEpoch
operator|-
name|EXPIRE_BUFFER_MS
decl_stmt|;
name|long
name|currTime
init|=
name|timer
operator|.
name|now
argument_list|()
decl_stmt|;
return|return
name|currTime
operator|>
name|lowerLimit
return|;
block|}
comment|/**    * The expires_in param from OAuth is in seconds-from-now.  Convert to    * milliseconds-from-epoch    */
DECL|method|convertExpiresIn (Timer timer, String expiresInSecs)
specifier|static
name|Long
name|convertExpiresIn
parameter_list|(
name|Timer
name|timer
parameter_list|,
name|String
name|expiresInSecs
parameter_list|)
block|{
name|long
name|expiresSecs
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|expiresInSecs
argument_list|)
decl_stmt|;
name|long
name|expiresMs
init|=
name|expiresSecs
operator|*
literal|1000
decl_stmt|;
return|return
name|timer
operator|.
name|now
argument_list|()
operator|+
name|expiresMs
return|;
block|}
block|}
end_class

end_unit

