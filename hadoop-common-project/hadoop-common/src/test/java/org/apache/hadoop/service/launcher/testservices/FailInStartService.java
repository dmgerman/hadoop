begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.service.launcher.testservices
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|launcher
operator|.
name|testservices
package|;
end_package

begin_comment
comment|/**  * Service which fails in its start() operation.  */
end_comment

begin_class
DECL|class|FailInStartService
specifier|public
class|class
name|FailInStartService
extends|extends
name|FailureTestService
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"org.apache.hadoop.service.launcher.testservices.FailInStartService"
decl_stmt|;
DECL|field|EXIT_CODE
specifier|public
specifier|static
specifier|final
name|int
name|EXIT_CODE
init|=
operator|-
literal|2
decl_stmt|;
DECL|method|FailInStartService ()
specifier|public
name|FailInStartService
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExitCode ()
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|EXIT_CODE
return|;
block|}
block|}
end_class

end_unit

