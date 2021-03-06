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
name|service
operator|.
name|conf
operator|.
name|SliderExitCodes
import|;
end_import

begin_class
DECL|class|SliderException
specifier|public
class|class
name|SliderException
extends|extends
name|ServiceLaunchException
implements|implements
name|SliderExitCodes
block|{
DECL|method|SliderException ()
specifier|public
name|SliderException
parameter_list|()
block|{
name|super
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
literal|"SliderException"
argument_list|)
expr_stmt|;
block|}
DECL|method|SliderException (int code, String message)
specifier|public
name|SliderException
parameter_list|(
name|int
name|code
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|code
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|SliderException (String s)
specifier|public
name|SliderException
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|super
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
DECL|method|SliderException (String s, Throwable throwable)
specifier|public
name|SliderException
parameter_list|(
name|String
name|s
parameter_list|,
name|Throwable
name|throwable
parameter_list|)
block|{
name|super
argument_list|(
name|EXIT_EXCEPTION_THROWN
argument_list|,
name|s
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
comment|/**    * Format the exception as you create it    * @param code exit code    * @param message exception message -sprintf formatted    * @param args arguments for the formatting    */
DECL|method|SliderException (int code, String message, Object... args)
specifier|public
name|SliderException
parameter_list|(
name|int
name|code
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
name|code
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Format the exception, include a throwable.     * The throwable comes before the message so that it is out of the varargs    * @param code exit code    * @param throwable thrown    * @param message message    * @param args arguments    */
DECL|method|SliderException (int code, Throwable throwable, String message, Object... args)
specifier|public
name|SliderException
parameter_list|(
name|int
name|code
parameter_list|,
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
name|code
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
argument_list|,
name|throwable
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

