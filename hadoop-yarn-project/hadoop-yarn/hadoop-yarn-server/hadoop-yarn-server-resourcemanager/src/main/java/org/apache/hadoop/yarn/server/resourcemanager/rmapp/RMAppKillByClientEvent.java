begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.rmapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|rmapp
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
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
name|security
operator|.
name|UserGroupInformation
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

begin_comment
comment|/**  * An event class that is used to help with logging information  * when an application KILL event is needed.  *  */
end_comment

begin_class
DECL|class|RMAppKillByClientEvent
specifier|public
class|class
name|RMAppKillByClientEvent
extends|extends
name|RMAppEvent
block|{
DECL|field|callerUGI
specifier|private
specifier|final
name|UserGroupInformation
name|callerUGI
decl_stmt|;
DECL|field|ip
specifier|private
specifier|final
name|InetAddress
name|ip
decl_stmt|;
comment|/**    * constructor to create an event used for logging during user driven kill    * invocations.    *    * @param appId application id    * @param diagnostics message about the kill event    * @param callerUGI caller's user and group information    * @param remoteIP ip address of the caller    */
DECL|method|RMAppKillByClientEvent (ApplicationId appId, String diagnostics, UserGroupInformation callerUGI, InetAddress remoteIP)
specifier|public
name|RMAppKillByClientEvent
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|String
name|diagnostics
parameter_list|,
name|UserGroupInformation
name|callerUGI
parameter_list|,
name|InetAddress
name|remoteIP
parameter_list|)
block|{
name|super
argument_list|(
name|appId
argument_list|,
name|RMAppEventType
operator|.
name|KILL
argument_list|,
name|diagnostics
argument_list|)
expr_stmt|;
name|this
operator|.
name|callerUGI
operator|=
name|callerUGI
expr_stmt|;
name|this
operator|.
name|ip
operator|=
name|remoteIP
expr_stmt|;
block|}
comment|/**    * returns the {@link UserGroupInformation} information.    * @return UserGroupInformation    */
DECL|method|getCallerUGI ()
specifier|public
specifier|final
name|UserGroupInformation
name|getCallerUGI
parameter_list|()
block|{
return|return
name|callerUGI
return|;
block|}
comment|/**    * returns the ip address stored in this event.    * @return remoteIP    */
DECL|method|getIp ()
specifier|public
specifier|final
name|InetAddress
name|getIp
parameter_list|()
block|{
return|return
name|ip
return|;
block|}
block|}
end_class

end_unit

