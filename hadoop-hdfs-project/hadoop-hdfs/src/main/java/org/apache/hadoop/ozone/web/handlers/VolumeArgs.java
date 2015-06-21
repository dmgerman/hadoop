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
name|request
operator|.
name|OzoneQuota
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
comment|/**  * VolumeArgs is used to package all volume  * related arguments in the call to underlying  * file system.  */
end_comment

begin_class
DECL|class|VolumeArgs
specifier|public
class|class
name|VolumeArgs
extends|extends
name|UserArgs
block|{
DECL|field|adminName
specifier|private
name|String
name|adminName
decl_stmt|;
DECL|field|volumeName
specifier|private
specifier|final
name|String
name|volumeName
decl_stmt|;
DECL|field|quota
specifier|private
name|OzoneQuota
name|quota
decl_stmt|;
comment|/**    * Returns Quota Information.    *    * @return Quota    */
DECL|method|getQuota ()
specifier|public
name|OzoneQuota
name|getQuota
parameter_list|()
block|{
return|return
name|quota
return|;
block|}
comment|/**    * Returns volume name.    *    * @return String    */
DECL|method|getVolumeName ()
specifier|public
name|String
name|getVolumeName
parameter_list|()
block|{
return|return
name|volumeName
return|;
block|}
comment|/**    * Constructs  volume Args.    *    * @param userName - User name    * @param volumeName - volume Name    * @param requestID _ Request ID    * @param hostName - Host Name    * @param request  - Http Request    * @param info - URI info    * @param headers - http headers    */
DECL|method|VolumeArgs (String userName, String volumeName, long requestID, String hostName, Request request, UriInfo info, HttpHeaders headers)
specifier|public
name|VolumeArgs
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|volumeName
parameter_list|,
name|long
name|requestID
parameter_list|,
name|String
name|hostName
parameter_list|,
name|Request
name|request
parameter_list|,
name|UriInfo
name|info
parameter_list|,
name|HttpHeaders
name|headers
parameter_list|)
block|{
name|super
argument_list|(
name|userName
argument_list|,
name|requestID
argument_list|,
name|hostName
argument_list|,
name|request
argument_list|,
name|info
argument_list|,
name|headers
argument_list|)
expr_stmt|;
name|this
operator|.
name|volumeName
operator|=
name|volumeName
expr_stmt|;
block|}
comment|/**    * Sets Quota information.    *    * @param quota - Quota Sting    * @throws IllegalArgumentException    */
DECL|method|setQuota (String quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|String
name|quota
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|this
operator|.
name|quota
operator|=
name|OzoneQuota
operator|.
name|parseQuota
argument_list|(
name|quota
argument_list|)
expr_stmt|;
block|}
comment|/**    * Sets quota information.    *    * @param quota - OzoneQuota    */
DECL|method|setQuota (OzoneQuota quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|OzoneQuota
name|quota
parameter_list|)
block|{
name|this
operator|.
name|quota
operator|=
name|quota
expr_stmt|;
block|}
comment|/**    * Gets admin Name.    *    * @return - Admin Name    */
DECL|method|getAdminName ()
specifier|public
name|String
name|getAdminName
parameter_list|()
block|{
return|return
name|adminName
return|;
block|}
comment|/**    * Sets Admin Name.    *    * @param adminName - Admin Name    */
DECL|method|setAdminName (String adminName)
specifier|public
name|void
name|setAdminName
parameter_list|(
name|String
name|adminName
parameter_list|)
block|{
name|this
operator|.
name|adminName
operator|=
name|adminName
expr_stmt|;
block|}
comment|/**    * Returns UserName/VolumeName.    *    * @return String    */
annotation|@
name|Override
DECL|method|getResourceName ()
specifier|public
name|String
name|getResourceName
parameter_list|()
block|{
return|return
name|super
operator|.
name|getResourceName
argument_list|()
operator|+
literal|"/"
operator|+
name|getVolumeName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

