begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.permission
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|permission
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

begin_comment
comment|/**  * Specifies the scope or intended usage of an ACL entry.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|enum|AclEntryScope
specifier|public
enum|enum
name|AclEntryScope
block|{
comment|/**    * An ACL entry that is inspected during permission checks to enforce    * permissions.    */
DECL|enumConstant|ACCESS
name|ACCESS
block|,
comment|/**    * An ACL entry to be applied to a directory's children that do not otherwise    * have their own ACL defined.  Unlike an access ACL entry, a default ACL    * entry is not inspected as part of permission enforcement on the directory    * that owns it.    */
DECL|enumConstant|DEFAULT
name|DEFAULT
block|; }
end_enum

end_unit

