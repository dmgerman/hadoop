begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.test
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|AssumptionViolatedException
import|;
end_import

begin_comment
comment|/**  * JUnit assumptions for the environment (OS).  */
end_comment

begin_class
DECL|class|PlatformAssumptions
specifier|public
specifier|final
class|class
name|PlatformAssumptions
block|{
DECL|field|OS_NAME
specifier|public
specifier|static
specifier|final
name|String
name|OS_NAME
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
DECL|field|WINDOWS
specifier|public
specifier|static
specifier|final
name|boolean
name|WINDOWS
init|=
name|OS_NAME
operator|.
name|startsWith
argument_list|(
literal|"Windows"
argument_list|)
decl_stmt|;
DECL|method|PlatformAssumptions ()
specifier|private
name|PlatformAssumptions
parameter_list|()
block|{ }
DECL|method|assumeNotWindows ()
specifier|public
specifier|static
name|void
name|assumeNotWindows
parameter_list|()
block|{
name|assumeNotWindows
argument_list|(
literal|"Expected Unix-like platform but got "
operator|+
name|OS_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|assumeNotWindows (String message)
specifier|public
specifier|static
name|void
name|assumeNotWindows
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|WINDOWS
condition|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
name|message
argument_list|)
throw|;
block|}
block|}
DECL|method|assumeWindows ()
specifier|public
specifier|static
name|void
name|assumeWindows
parameter_list|()
block|{
if|if
condition|(
operator|!
name|WINDOWS
condition|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"Expected Windows platform but got "
operator|+
name|OS_NAME
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

