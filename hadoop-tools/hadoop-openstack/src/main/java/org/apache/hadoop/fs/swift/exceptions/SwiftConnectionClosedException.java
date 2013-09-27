begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.swift.exceptions
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
name|exceptions
package|;
end_package

begin_comment
comment|/**  * Exception raised when an attempt is made to use a closed stream  */
end_comment

begin_class
DECL|class|SwiftConnectionClosedException
specifier|public
class|class
name|SwiftConnectionClosedException
extends|extends
name|SwiftException
block|{
DECL|field|MESSAGE
specifier|public
specifier|static
specifier|final
name|String
name|MESSAGE
init|=
literal|"Connection to Swift service has been closed"
decl_stmt|;
DECL|method|SwiftConnectionClosedException ()
specifier|public
name|SwiftConnectionClosedException
parameter_list|()
block|{
name|super
argument_list|(
name|MESSAGE
argument_list|)
expr_stmt|;
block|}
DECL|method|SwiftConnectionClosedException (String reason)
specifier|public
name|SwiftConnectionClosedException
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|super
argument_list|(
name|MESSAGE
operator|+
literal|": "
operator|+
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

