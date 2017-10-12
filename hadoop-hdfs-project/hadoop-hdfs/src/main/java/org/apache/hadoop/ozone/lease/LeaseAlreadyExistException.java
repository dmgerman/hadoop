begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.lease
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|lease
package|;
end_package

begin_comment
comment|/**  * This exception represents that there is already a lease acquired on the  * same resource.  */
end_comment

begin_class
DECL|class|LeaseAlreadyExistException
specifier|public
class|class
name|LeaseAlreadyExistException
extends|extends
name|LeaseException
block|{
comment|/**    * Constructs an {@code LeaseAlreadyExistException} with {@code null}    * as its error detail message.    */
DECL|method|LeaseAlreadyExistException ()
specifier|public
name|LeaseAlreadyExistException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructs an {@code LeaseAlreadyExistException} with the specified    * detail message.    *    * @param message    *        The detail message (which is saved for later retrieval    *        by the {@link #getMessage()} method)    */
DECL|method|LeaseAlreadyExistException (String message)
specifier|public
name|LeaseAlreadyExistException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

