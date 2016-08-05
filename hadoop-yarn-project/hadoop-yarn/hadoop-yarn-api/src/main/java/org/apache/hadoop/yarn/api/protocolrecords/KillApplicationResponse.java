begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords
package|package
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
name|protocolrecords
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
name|InterfaceAudience
operator|.
name|Public
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
name|Stable
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
name|Unstable
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
name|ApplicationClientProtocol
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * The response sent by the<code>ResourceManager</code> to the client aborting  * a submitted application.  *<p>  * The response, includes:  *<ul>  *<li>  *     A flag which indicates that the process of killing the application is  *     completed or not.  *</li>  *</ul>  * Note: user is recommended to wait until this flag becomes true, otherwise if  * the<code>ResourceManager</code> crashes before the process of killing the  * application is completed, the<code>ResourceManager</code> may retry this  * application on recovery.  *   * @see ApplicationClientProtocol#forceKillApplication(KillApplicationRequest)  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|KillApplicationResponse
specifier|public
specifier|abstract
class|class
name|KillApplicationResponse
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (boolean isKillCompleted)
specifier|public
specifier|static
name|KillApplicationResponse
name|newInstance
parameter_list|(
name|boolean
name|isKillCompleted
parameter_list|)
block|{
name|KillApplicationResponse
name|response
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|KillApplicationResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setIsKillCompleted
argument_list|(
name|isKillCompleted
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|/**    * Get the flag which indicates that the process of killing application is completed or not.    * @return true if the process of killing application has completed,    *         false otherwise    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getIsKillCompleted ()
specifier|public
specifier|abstract
name|boolean
name|getIsKillCompleted
parameter_list|()
function_decl|;
comment|/**    * Set the flag which indicates that the process of killing application is completed or not.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setIsKillCompleted (boolean isKillCompleted)
specifier|public
specifier|abstract
name|void
name|setIsKillCompleted
parameter_list|(
name|boolean
name|isKillCompleted
parameter_list|)
function_decl|;
block|}
end_class

end_unit

