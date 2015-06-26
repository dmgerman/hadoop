begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.web.interfaces
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
name|interfaces
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
name|ozone
operator|.
name|web
operator|.
name|exceptions
operator|.
name|OzoneException
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
name|ozone
operator|.
name|web
operator|.
name|handlers
operator|.
name|UserArgs
import|;
end_import

begin_comment
comment|/**  * This interface is used by Ozone to determine user identity.  *  * Please see concrete implementations for more information  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|UserAuth
specifier|public
interface|interface
name|UserAuth
block|{
comment|/**    * Returns the user name as a string from the URI and HTTP headers.    *    * @param userArgs - userArgs    *    * @return String - User name    *    * @throws OzoneException    */
DECL|method|getUser (UserArgs userArgs)
name|String
name|getUser
parameter_list|(
name|UserArgs
name|userArgs
parameter_list|)
throws|throws
name|OzoneException
function_decl|;
comment|/**    * Returns all the Groups that user is a member of.    *    * @param userArgs - userArgs    *    * @return Array of Groups    *    * @throws OzoneException    */
DECL|method|getGroups (UserArgs userArgs)
name|String
index|[]
name|getGroups
parameter_list|(
name|UserArgs
name|userArgs
parameter_list|)
throws|throws
name|OzoneException
function_decl|;
comment|/**    * Returns true if a user is a Admin.    *    * @param userArgs - userArgs    *    * @return true if Admin , false otherwise    *    * @throws OzoneException -- Allows the underlying system    * to throw, that error will get propagated to clients    */
DECL|method|isAdmin (UserArgs userArgs)
name|boolean
name|isAdmin
parameter_list|(
name|UserArgs
name|userArgs
parameter_list|)
throws|throws
name|OzoneException
function_decl|;
comment|/**    * Returns true if the request is Anonymous.    *    * @param userArgs - userArgs    *    * @return true if the request is anonymous, false otherwise.    *    * @throws OzoneException - Will be propagated back to end user    */
DECL|method|isAnonymous (UserArgs userArgs)
name|boolean
name|isAnonymous
parameter_list|(
name|UserArgs
name|userArgs
parameter_list|)
throws|throws
name|OzoneException
function_decl|;
comment|/**    * Returns true if the name is a recognizable user in the system.    *    * @param userName - User Name to check    * @param userArgs - userArgs    *    * @return true if the username string is the name of a valid user.    *    * @throws OzoneException - Will be propagated back to end user    */
DECL|method|isUser (String userName, UserArgs userArgs)
name|boolean
name|isUser
parameter_list|(
name|String
name|userName
parameter_list|,
name|UserArgs
name|userArgs
parameter_list|)
throws|throws
name|OzoneException
function_decl|;
comment|/**    * Returns the x-ozone-user or the user on behalf of, This is    * used in Volume creation path.    *    * @param userArgs - userArgs    *    * @return a user name if it has x-ozone-user args in header.    *    * @throws OzoneException    */
DECL|method|getOzoneUser (UserArgs userArgs)
name|String
name|getOzoneUser
parameter_list|(
name|UserArgs
name|userArgs
parameter_list|)
throws|throws
name|OzoneException
function_decl|;
block|}
end_interface

end_unit

