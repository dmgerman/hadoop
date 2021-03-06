begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.exceptions
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|exceptions
package|;
end_package

begin_comment
comment|/**  * Used to raise a usage exception ... this has the exit code  * {@link #EXIT_USAGE}  */
end_comment

begin_class
DECL|class|UsageException
specifier|public
class|class
name|UsageException
extends|extends
name|SliderException
block|{
DECL|method|UsageException (String s, Object... args)
specifier|public
name|UsageException
parameter_list|(
name|String
name|s
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|EXIT_USAGE
argument_list|,
name|s
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
DECL|method|UsageException (Throwable throwable, String message, Object... args)
specifier|public
name|UsageException
parameter_list|(
name|Throwable
name|throwable
parameter_list|,
name|String
name|message
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|EXIT_USAGE
argument_list|,
name|throwable
argument_list|,
name|message
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

