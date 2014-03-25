begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_comment
comment|/**  * The UserIdentityProvider creates uses the username as the  * identity. All jobs launched by a user will be grouped together.  */
end_comment

begin_class
DECL|class|UserIdentityProvider
specifier|public
class|class
name|UserIdentityProvider
implements|implements
name|IdentityProvider
block|{
DECL|method|makeIdentity (Schedulable obj)
specifier|public
name|String
name|makeIdentity
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
block|{
name|UserGroupInformation
name|ugi
init|=
name|obj
operator|.
name|getUserGroupInformation
argument_list|()
decl_stmt|;
if|if
condition|(
name|ugi
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|ugi
operator|.
name|getUserName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

