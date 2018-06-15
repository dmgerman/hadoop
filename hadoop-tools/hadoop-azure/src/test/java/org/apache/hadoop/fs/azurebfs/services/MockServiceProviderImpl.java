begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_comment
comment|/**  * Mock ABFS ServiceProviderImpl.  */
end_comment

begin_class
DECL|class|MockServiceProviderImpl
specifier|public
specifier|final
class|class
name|MockServiceProviderImpl
block|{
DECL|method|create (MockAbfsServiceInjectorImpl abfsServiceInjector)
specifier|public
specifier|static
name|void
name|create
parameter_list|(
name|MockAbfsServiceInjectorImpl
name|abfsServiceInjector
parameter_list|)
block|{
name|Injector
name|injector
init|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|abfsServiceInjector
argument_list|)
decl_stmt|;
name|AbfsServiceProviderImpl
operator|.
name|create
argument_list|(
name|injector
argument_list|)
expr_stmt|;
block|}
DECL|method|MockServiceProviderImpl ()
specifier|private
name|MockServiceProviderImpl
parameter_list|()
block|{
comment|// no-op
block|}
block|}
end_class

end_unit

