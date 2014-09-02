begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * Test class used by {@link TestRunJar} to verify that it is loaded by the  * {@link ApplicationClassLoader}.  */
end_comment

begin_class
DECL|class|ClassLoaderCheckMain
specifier|public
class|class
name|ClassLoaderCheckMain
block|{
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|// ClassLoaderCheckMain should be loaded by the application classloader
name|ClassLoaderCheck
operator|.
name|checkClassLoader
argument_list|(
name|ClassLoaderCheckMain
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// ClassLoaderCheckSecond should NOT be loaded by the application
comment|// classloader
name|ClassLoaderCheck
operator|.
name|checkClassLoader
argument_list|(
name|ClassLoaderCheckSecond
operator|.
name|class
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|// ClassLoaderCheckThird should be loaded by the application classloader
name|ClassLoaderCheck
operator|.
name|checkClassLoader
argument_list|(
name|ClassLoaderCheckThird
operator|.
name|class
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

