begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
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
name|OzoneAcl
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
name|om
operator|.
name|exceptions
operator|.
name|OMException
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
name|security
operator|.
name|acl
operator|.
name|OzoneObj
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
name|security
operator|.
name|acl
operator|.
name|RequestContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Interface for Ozone Acl management.  */
end_comment

begin_interface
DECL|interface|IOzoneAcl
specifier|public
interface|interface
name|IOzoneAcl
block|{
comment|/**    * Add acl for Ozone object. Return true if acl is added successfully else    * false.    * @param obj Ozone object for which acl should be added.    * @param acl ozone acl top be added.    *    * @throws IOException if there is error.    * */
DECL|method|addAcl (OzoneObj obj, OzoneAcl acl)
name|boolean
name|addAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|,
name|OzoneAcl
name|acl
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove acl for Ozone object. Return true if acl is removed successfully    * else false.    * @param obj Ozone object.    * @param acl Ozone acl to be removed.    *    * @throws IOException if there is error.    * */
DECL|method|removeAcl (OzoneObj obj, OzoneAcl acl)
name|boolean
name|removeAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|,
name|OzoneAcl
name|acl
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Acls to be set for given Ozone object. This operations reset ACL for    * given object to list of ACLs provided in argument.    * @param obj Ozone object.    * @param acls List of acls.    *    * @throws IOException if there is error.    * */
DECL|method|setAcl (OzoneObj obj, List<OzoneAcl> acls)
name|boolean
name|setAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|,
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|acls
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns list of ACLs for given Ozone object.    * @param obj Ozone object.    *    * @throws IOException if there is error.    * */
DECL|method|getAcl (OzoneObj obj)
name|List
argument_list|<
name|OzoneAcl
argument_list|>
name|getAcl
parameter_list|(
name|OzoneObj
name|obj
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Check access for given ozoneObject.    *    * @param ozObject object for which access needs to be checked.    * @param context Context object encapsulating all user related information.    * @throws org.apache.hadoop.ozone.om.exceptions.OMException    * @return true if user has access else false.    */
DECL|method|checkAccess (OzoneObj ozObject, RequestContext context)
name|boolean
name|checkAccess
parameter_list|(
name|OzoneObj
name|ozObject
parameter_list|,
name|RequestContext
name|context
parameter_list|)
throws|throws
name|OMException
function_decl|;
block|}
end_interface

end_unit

