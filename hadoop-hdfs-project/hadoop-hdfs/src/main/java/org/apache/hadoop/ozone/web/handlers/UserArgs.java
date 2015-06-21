begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.handlers
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|web
operator|.
name|handlers
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|UriInfo
import|;
end_import

begin_comment
comment|/**  * UserArgs is used to package caller info  * and pass it down to file system.  */
end_comment

begin_class
DECL|class|UserArgs
specifier|public
class|class
name|UserArgs
block|{
DECL|field|userName
specifier|private
name|String
name|userName
decl_stmt|;
DECL|field|requestID
specifier|private
specifier|final
name|long
name|requestID
decl_stmt|;
DECL|field|hostName
specifier|private
specifier|final
name|String
name|hostName
decl_stmt|;
DECL|field|uri
specifier|private
specifier|final
name|UriInfo
name|uri
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|HttpHeaders
name|headers
decl_stmt|;
comment|/**    * Constructs  user args.    *    * @param userName - User name    * @param requestID _ Request ID    * @param hostName - Host Name    * @param req  - Request    * @param info - Uri Info    * @param httpHeaders - http headers    */
DECL|method|UserArgs (String userName, long requestID, String hostName, Request req, UriInfo info, HttpHeaders httpHeaders)
specifier|public
name|UserArgs
parameter_list|(
name|String
name|userName
parameter_list|,
name|long
name|requestID
parameter_list|,
name|String
name|hostName
parameter_list|,
name|Request
name|req
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|HttpHeaders
name|httpHeaders
parameter_list|)
block|{
name|this
operator|.
name|hostName
operator|=
name|hostName
expr_stmt|;
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
name|this
operator|.
name|requestID
operator|=
name|requestID
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|req
expr_stmt|;
name|this
operator|.
name|headers
operator|=
name|httpHeaders
expr_stmt|;
block|}
comment|/**    * Returns hostname.    *    * @return String    */
DECL|method|getHostName ()
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|hostName
return|;
block|}
comment|/**    * Returns RequestID.    *    * @return Long    */
DECL|method|getRequestID ()
specifier|public
name|long
name|getRequestID
parameter_list|()
block|{
return|return
name|requestID
return|;
block|}
comment|/**    * Returns User Name.    *    * @return String    */
DECL|method|getUserName ()
specifier|public
name|String
name|getUserName
parameter_list|()
block|{
return|return
name|userName
return|;
block|}
comment|/**    * Sets the user name.    *    * @param userName Name of the user    */
DECL|method|setUserName (String userName)
specifier|public
name|void
name|setUserName
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|userName
expr_stmt|;
block|}
comment|/**    * Returns the resource Name.    *    * @return String Resource.    */
DECL|method|getResourceName ()
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|getUserName
argument_list|()
return|;
block|}
comment|/**    * Returns Http Headers for this call.    *    * @return httpHeaders    */
DECL|method|getHeaders ()
specifier|public
name|HttpHeaders
name|getHeaders
parameter_list|()
block|{
return|return
name|headers
return|;
block|}
comment|/**    * Returns Request Object.    *    * @return Request    */
DECL|method|getRequest ()
specifier|public
name|Request
name|getRequest
parameter_list|()
block|{
return|return
name|request
return|;
block|}
comment|/**    * Returns UriInfo.    *    * @return UriInfo    */
DECL|method|getUri ()
specifier|public
name|UriInfo
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
block|}
end_class

end_unit

