begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|*
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
name|conf
operator|.
name|YarnConfiguration
import|;
end_import

begin_comment
comment|/**  * Application related ACLs  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|enum|ApplicationACL
specifier|public
enum|enum
name|ApplicationACL
block|{
comment|/**    * ACL for 'viewing' application. Dictates who can 'view' some or all of the application    * related details.    */
DECL|enumConstant|VIEW_APP
name|VIEW_APP
parameter_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_ACL_VIEW_APP
parameter_list|)
operator|,
comment|/**    * ACL for 'modifying' application. Dictates who can 'modify' the application for e.g., by    * killing the application    */
DECL|enumConstant|MODIFY_APP
constructor|MODIFY_APP(YarnConfiguration.APPLICATION_ACL_MODIFY_APP
block|)
enum|;
end_enum

begin_decl_stmt
DECL|field|aclName
name|String
name|aclName
decl_stmt|;
end_decl_stmt

begin_expr_stmt
DECL|method|ApplicationACL (String name)
name|ApplicationACL
argument_list|(
name|String
name|name
argument_list|)
block|{
name|this
operator|.
name|aclName
operator|=
name|name
block|;   }
comment|/**    * Get the name of the ACL. Here it is same as the name of the configuration    * property for specifying the ACL for the application.    *     * @return aclName    */
DECL|method|getAclName ()
specifier|public
name|String
name|getAclName
argument_list|()
block|{
return|return
name|aclName
return|;
block|}
end_expr_stmt

unit|}
end_unit

