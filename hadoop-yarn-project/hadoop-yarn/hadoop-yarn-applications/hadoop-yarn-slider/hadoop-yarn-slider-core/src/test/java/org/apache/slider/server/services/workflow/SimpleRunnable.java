begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
package|;
end_package

begin_comment
comment|/**  * Test runnable that can be made to exit, or throw an exception  * during its run  */
end_comment

begin_class
DECL|class|SimpleRunnable
class|class
name|SimpleRunnable
implements|implements
name|Runnable
block|{
DECL|field|throwException
name|boolean
name|throwException
init|=
literal|false
decl_stmt|;
DECL|method|SimpleRunnable ()
name|SimpleRunnable
parameter_list|()
block|{   }
DECL|method|SimpleRunnable (boolean throwException)
name|SimpleRunnable
parameter_list|(
name|boolean
name|throwException
parameter_list|)
block|{
name|this
operator|.
name|throwException
operator|=
name|throwException
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|throwException
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"SimpleRunnable"
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|this
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

