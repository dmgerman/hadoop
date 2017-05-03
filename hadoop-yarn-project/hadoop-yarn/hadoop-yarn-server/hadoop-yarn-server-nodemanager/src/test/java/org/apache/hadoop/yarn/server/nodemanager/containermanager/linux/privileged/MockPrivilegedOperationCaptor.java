begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.linux.privileged
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|linux
operator|.
name|privileged
package|;
end_package

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|times
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_comment
comment|/**  * Captures operations from mock {@link PrivilegedOperation} instances.  */
end_comment

begin_class
DECL|class|MockPrivilegedOperationCaptor
specifier|public
specifier|final
class|class
name|MockPrivilegedOperationCaptor
block|{
DECL|method|MockPrivilegedOperationCaptor ()
specifier|private
name|MockPrivilegedOperationCaptor
parameter_list|()
block|{}
comment|/**    * Capture the operation that should be performed by the    * PrivilegedOperationExecutor.    *    * @param mockExecutor    mock PrivilegedOperationExecutor.    * @param invocationCount number of invocations expected.    * @return a list of operations that were invoked.    * @throws PrivilegedOperationException when the operation fails to execute.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|capturePrivilegedOperations ( PrivilegedOperationExecutor mockExecutor, int invocationCount, boolean grabOutput)
specifier|public
specifier|static
name|List
argument_list|<
name|PrivilegedOperation
argument_list|>
name|capturePrivilegedOperations
parameter_list|(
name|PrivilegedOperationExecutor
name|mockExecutor
parameter_list|,
name|int
name|invocationCount
parameter_list|,
name|boolean
name|grabOutput
parameter_list|)
throws|throws
name|PrivilegedOperationException
block|{
name|ArgumentCaptor
argument_list|<
name|PrivilegedOperation
argument_list|>
name|opCaptor
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|PrivilegedOperation
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//one or more invocations expected
comment|//due to type erasure + mocking, this verification requires a suppress
comment|// warning annotation on the entire method
name|verify
argument_list|(
name|mockExecutor
argument_list|,
name|times
argument_list|(
name|invocationCount
argument_list|)
argument_list|)
operator|.
name|executePrivilegedOperation
argument_list|(
name|anyList
argument_list|()
argument_list|,
name|opCaptor
operator|.
name|capture
argument_list|()
argument_list|,
name|any
argument_list|(
name|File
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|,
name|eq
argument_list|(
name|grabOutput
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|//verification completed. we need to isolate specific invications.
comment|// hence, reset mock here
name|Mockito
operator|.
name|reset
argument_list|(
name|mockExecutor
argument_list|)
expr_stmt|;
return|return
name|opCaptor
operator|.
name|getAllValues
argument_list|()
return|;
block|}
block|}
end_class

end_unit

