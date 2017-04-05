begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.exception
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
name|federation
operator|.
name|store
operator|.
name|exception
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
import|;
end_import

begin_comment
comment|/**  * Exception thrown by the<code>FederationStateStore</code>.  *  */
end_comment

begin_class
DECL|class|FederationStateStoreException
specifier|public
class|class
name|FederationStateStoreException
extends|extends
name|YarnException
block|{
comment|/**    * IDE auto-generated.    */
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|6453353714832159296L
decl_stmt|;
DECL|field|code
specifier|private
name|FederationStateStoreErrorCode
name|code
decl_stmt|;
DECL|method|FederationStateStoreException (FederationStateStoreErrorCode code)
specifier|public
name|FederationStateStoreException
parameter_list|(
name|FederationStateStoreErrorCode
name|code
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
DECL|method|getCode ()
specifier|public
name|FederationStateStoreErrorCode
name|getCode
parameter_list|()
block|{
return|return
name|code
return|;
block|}
block|}
end_class

end_unit

