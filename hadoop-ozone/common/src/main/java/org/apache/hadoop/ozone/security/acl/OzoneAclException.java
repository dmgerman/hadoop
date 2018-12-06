begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.security.acl
package|package
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
package|;
end_package

begin_comment
comment|/**  * Timeout exception thrown by Ozone. Ex: When checking ACLs for an Object if  * security manager is not able to process the request in configured time than  * {@link OzoneAclException} should be thrown.  */
end_comment

begin_class
DECL|class|OzoneAclException
specifier|public
class|class
name|OzoneAclException
extends|extends
name|Exception
block|{
DECL|field|errorCode
specifier|private
name|ErrorCode
name|errorCode
decl_stmt|;
comment|/**    * Constructs a new exception with {@code null} as its detail message. The    * cause is not initialized, and may subsequently be initialized by a call to    * {@link #initCause}.    */
DECL|method|OzoneAclException ()
specifier|public
name|OzoneAclException
parameter_list|()
block|{
name|super
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructs a new exception with {@code null} as its detail message. The    * cause is not initialized, and may subsequently be initialized by a call to    * {@link #initCause}.    */
DECL|method|OzoneAclException (String errorMsg, ErrorCode code, Throwable ex)
specifier|public
name|OzoneAclException
parameter_list|(
name|String
name|errorMsg
parameter_list|,
name|ErrorCode
name|code
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{
name|super
argument_list|(
name|errorMsg
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorCode
operator|=
name|code
expr_stmt|;
block|}
DECL|enum|ErrorCode
enum|enum
name|ErrorCode
block|{
DECL|enumConstant|TIMEOUT
name|TIMEOUT
block|,
DECL|enumConstant|OTHER
name|OTHER
block|}
DECL|method|getErrorCode ()
specifier|public
name|ErrorCode
name|getErrorCode
parameter_list|()
block|{
return|return
name|errorCode
return|;
block|}
block|}
end_class

end_unit

