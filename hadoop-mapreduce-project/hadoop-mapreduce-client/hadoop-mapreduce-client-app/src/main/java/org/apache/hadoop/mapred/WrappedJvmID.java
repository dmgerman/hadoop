begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/**  * A simple wrapper for increasing the visibility.  */
end_comment

begin_class
DECL|class|WrappedJvmID
specifier|public
class|class
name|WrappedJvmID
extends|extends
name|JVMId
block|{
DECL|method|WrappedJvmID (JobID jobID, boolean mapTask, long nextLong)
specifier|public
name|WrappedJvmID
parameter_list|(
name|JobID
name|jobID
parameter_list|,
name|boolean
name|mapTask
parameter_list|,
name|long
name|nextLong
parameter_list|)
block|{
name|super
argument_list|(
name|jobID
argument_list|,
name|mapTask
argument_list|,
name|nextLong
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

