begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ha
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ha
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
name|test
operator|.
name|MultithreadedTestUtil
import|;
end_import

begin_class
DECL|class|ZKFCTestUtil
specifier|public
class|class
name|ZKFCTestUtil
block|{
DECL|method|waitForHealthState (ZKFailoverController zkfc, HealthMonitor.State state, MultithreadedTestUtil.TestContext ctx)
specifier|public
specifier|static
name|void
name|waitForHealthState
parameter_list|(
name|ZKFailoverController
name|zkfc
parameter_list|,
name|HealthMonitor
operator|.
name|State
name|state
parameter_list|,
name|MultithreadedTestUtil
operator|.
name|TestContext
name|ctx
parameter_list|)
throws|throws
name|Exception
block|{
while|while
condition|(
name|zkfc
operator|.
name|getLastHealthState
argument_list|()
operator|!=
name|state
condition|)
block|{
if|if
condition|(
name|ctx
operator|!=
literal|null
condition|)
block|{
name|ctx
operator|.
name|checkException
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|50
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

