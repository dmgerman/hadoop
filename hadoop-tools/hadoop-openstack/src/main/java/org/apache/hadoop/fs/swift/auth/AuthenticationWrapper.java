begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.auth
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|swift
operator|.
name|auth
package|;
end_package

begin_comment
comment|/**  * This class is used for correct hierarchy mapping of  * Keystone authentication model and java code  * THIS FILE IS MAPPED BY JACKSON TO AND FROM JSON.  * DO NOT RENAME OR MODIFY FIELDS AND THEIR ACCESSORS.  */
end_comment

begin_class
DECL|class|AuthenticationWrapper
specifier|public
class|class
name|AuthenticationWrapper
block|{
comment|/**    * authentication response field    */
DECL|field|access
specifier|private
name|AuthenticationResponse
name|access
decl_stmt|;
comment|/**    * @return authentication response    */
DECL|method|getAccess ()
specifier|public
name|AuthenticationResponse
name|getAccess
parameter_list|()
block|{
return|return
name|access
return|;
block|}
comment|/**    * @param access sets authentication response    */
DECL|method|setAccess (AuthenticationResponse access)
specifier|public
name|void
name|setAccess
parameter_list|(
name|AuthenticationResponse
name|access
parameter_list|)
block|{
name|this
operator|.
name|access
operator|=
name|access
expr_stmt|;
block|}
block|}
end_class

end_unit

