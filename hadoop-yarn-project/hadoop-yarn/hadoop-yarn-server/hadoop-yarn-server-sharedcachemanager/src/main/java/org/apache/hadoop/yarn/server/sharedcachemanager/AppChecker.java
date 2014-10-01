begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager
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
name|sharedcachemanager
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|InterfaceAudience
operator|.
name|Private
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
operator|.
name|Evolving
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
name|service
operator|.
name|CompositeService
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
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * An interface for checking whether an app is running so that the cleaner  * service may determine if it can safely remove a cached entry.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Evolving
DECL|class|AppChecker
specifier|public
specifier|abstract
class|class
name|AppChecker
extends|extends
name|CompositeService
block|{
DECL|method|AppChecker ()
specifier|public
name|AppChecker
parameter_list|()
block|{
name|super
argument_list|(
literal|"AppChecker"
argument_list|)
expr_stmt|;
block|}
DECL|method|AppChecker (String name)
specifier|public
name|AppChecker
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns whether the app is in an active state.    *     * @return true if the app is found and is not in one of the completed states;    *         false otherwise    * @throws YarnException if there is an error in determining the app state    */
annotation|@
name|Private
DECL|method|isApplicationActive (ApplicationId id)
specifier|public
specifier|abstract
name|boolean
name|isApplicationActive
parameter_list|(
name|ApplicationId
name|id
parameter_list|)
throws|throws
name|YarnException
function_decl|;
comment|/**    * Returns the list of all active apps at the given time.    *     * @return the list of active apps, or an empty list if there is none    * @throws YarnException if there is an error in obtaining the list    */
annotation|@
name|Private
DECL|method|getActiveApplications ()
specifier|public
specifier|abstract
name|Collection
argument_list|<
name|ApplicationId
argument_list|>
name|getActiveApplications
parameter_list|()
throws|throws
name|YarnException
function_decl|;
block|}
end_class

end_unit

