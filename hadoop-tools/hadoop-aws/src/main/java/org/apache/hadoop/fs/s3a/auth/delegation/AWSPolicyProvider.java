begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.auth.delegation
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|delegation
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|fs
operator|.
name|s3a
operator|.
name|auth
operator|.
name|RoleModel
import|;
end_import

begin_comment
comment|/**  * Interface for providers of AWS policy for accessing data.  * This is used when building up the role permissions for a delegation  * token.  *  * The permissions requested are from the perspective of  * S3A filesystem operations on the data,<i>not</i> the simpler  * model of "permissions on the the remote service".  * As an example, to use S3Guard effectively, the client needs full CRUD  * access to the table, even for {@link AccessLevel#READ}.  */
end_comment

begin_interface
DECL|interface|AWSPolicyProvider
specifier|public
interface|interface
name|AWSPolicyProvider
block|{
comment|/**    * Get the AWS policy statements required for accessing this service.    *    * @param access access level desired.    * @return a possibly empty list of statements to grant access at that    * level.    */
DECL|method|listAWSPolicyRules (Set<AccessLevel> access)
name|List
argument_list|<
name|RoleModel
operator|.
name|Statement
argument_list|>
name|listAWSPolicyRules
parameter_list|(
name|Set
argument_list|<
name|AccessLevel
argument_list|>
name|access
parameter_list|)
function_decl|;
comment|/**    * Access levels.    */
DECL|enum|AccessLevel
enum|enum
name|AccessLevel
block|{
comment|/** Filesystem data read operations. */
DECL|enumConstant|READ
name|READ
block|,
comment|/** Data write, encryption, etc. */
DECL|enumConstant|WRITE
name|WRITE
block|,
comment|/** Administration of the data, tables, etc. */
DECL|enumConstant|ADMIN
name|ADMIN
block|,   }
block|}
end_interface

end_unit

