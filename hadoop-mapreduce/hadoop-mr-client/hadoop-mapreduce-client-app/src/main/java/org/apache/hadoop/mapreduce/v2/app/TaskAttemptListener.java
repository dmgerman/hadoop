begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|WrappedJvmID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
import|;
end_import

begin_interface
DECL|interface|TaskAttemptListener
specifier|public
interface|interface
name|TaskAttemptListener
block|{
DECL|method|getAddress ()
name|InetSocketAddress
name|getAddress
parameter_list|()
function_decl|;
DECL|method|register (TaskAttemptId attemptID, Task task, WrappedJvmID jvmID)
name|void
name|register
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|,
name|Task
name|task
parameter_list|,
name|WrappedJvmID
name|jvmID
parameter_list|)
function_decl|;
DECL|method|unregister (TaskAttemptId attemptID, WrappedJvmID jvmID)
name|void
name|unregister
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|,
name|WrappedJvmID
name|jvmID
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

