begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
name|records
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
name|util
operator|.
name|Records
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SerializedException
specifier|public
specifier|abstract
class|class
name|SerializedException
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (Throwable e)
specifier|public
specifier|static
name|SerializedException
name|newInstance
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|SerializedException
name|exception
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SerializedException
operator|.
name|class
argument_list|)
decl_stmt|;
name|exception
operator|.
name|init
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|exception
return|;
block|}
comment|/**    * Constructs a new<code>SerializedException</code> with the specified detail    * message and cause.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|init (String message, Throwable cause)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
comment|/**    * Constructs a new<code>SerializedException</code> with the specified detail    * message.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|init (String message)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
comment|/**    * Constructs a new<code>SerializedException</code> with the specified cause.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|init (Throwable cause)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|Throwable
name|cause
parameter_list|)
function_decl|;
comment|/**    * Get the detail message string of this exception.    * @return the detail message string of this exception.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getMessage ()
specifier|public
specifier|abstract
name|String
name|getMessage
parameter_list|()
function_decl|;
comment|/**    * Get the backtrace of this exception.     * @return the backtrace of this exception.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getRemoteTrace ()
specifier|public
specifier|abstract
name|String
name|getRemoteTrace
parameter_list|()
function_decl|;
comment|/**    * Get the cause of this exception or null if the cause is nonexistent or    * unknown.    * @return the cause of this exception.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|getCause ()
specifier|public
specifier|abstract
name|SerializedException
name|getCause
parameter_list|()
function_decl|;
comment|/**    * Deserialize the exception to a new Throwable.     * @return the Throwable form of this serialized exception.    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|deSerialize ()
specifier|public
specifier|abstract
name|Throwable
name|deSerialize
parameter_list|()
function_decl|;
block|}
end_class

end_unit

