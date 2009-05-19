begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**********************************************************  * This class runs FailMon in a continuous mode on the local  * node.  *   **********************************************************/
end_comment

begin_class
DECL|class|Continuous
specifier|public
class|class
name|Continuous
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
name|Environment
operator|.
name|prepare
argument_list|(
literal|"failmon.properties"
argument_list|)
expr_stmt|;
name|Executor
name|ex
init|=
operator|new
name|Executor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
operator|new
name|Thread
argument_list|(
name|ex
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

