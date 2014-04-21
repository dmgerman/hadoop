begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestJobQueueClient
specifier|public
class|class
name|TestJobQueueClient
block|{
comment|/**    * Test that print job queue recursively prints child queues    */
annotation|@
name|Test
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|testPrintJobQueueInfo ()
specifier|public
name|void
name|testPrintJobQueueInfo
parameter_list|()
throws|throws
name|IOException
block|{
name|JobQueueClient
name|queueClient
init|=
operator|new
name|JobQueueClient
argument_list|()
decl_stmt|;
name|JobQueueInfo
name|parent
init|=
operator|new
name|JobQueueInfo
argument_list|()
decl_stmt|;
name|JobQueueInfo
name|child
init|=
operator|new
name|JobQueueInfo
argument_list|()
decl_stmt|;
name|JobQueueInfo
name|grandChild
init|=
operator|new
name|JobQueueInfo
argument_list|()
decl_stmt|;
name|child
operator|.
name|addChild
argument_list|(
name|grandChild
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addChild
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|grandChild
operator|.
name|setQueueName
argument_list|(
literal|"GrandChildQueue"
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|bbos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|bbos
argument_list|)
decl_stmt|;
name|queueClient
operator|.
name|printJobQueueInfo
argument_list|(
name|parent
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"printJobQueueInfo did not print grandchild's name"
argument_list|,
name|bbos
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"GrandChildQueue"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

