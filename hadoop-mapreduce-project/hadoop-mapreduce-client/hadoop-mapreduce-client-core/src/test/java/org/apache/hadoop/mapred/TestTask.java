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
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
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
name|Mockito
operator|.
name|when
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|ExitUtil
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
name|util
operator|.
name|ExitUtil
operator|.
name|ExitException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|junit
operator|.
name|MockitoJUnitRunner
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|MockitoJUnitRunner
operator|.
name|class
argument_list|)
DECL|class|TestTask
specifier|public
class|class
name|TestTask
block|{
annotation|@
name|Mock
DECL|field|umbilical
specifier|private
name|TaskUmbilicalProtocol
name|umbilical
decl_stmt|;
annotation|@
name|Mock
DECL|field|feedback
specifier|private
name|AMFeedback
name|feedback
decl_stmt|;
DECL|field|task
specifier|private
name|Task
name|task
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|task
operator|=
operator|new
name|StubTask
argument_list|()
expr_stmt|;
name|ExitUtil
operator|.
name|disableSystemExit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStatusUpdateDoesNotExitInUberMode ()
specifier|public
name|void
name|testStatusUpdateDoesNotExitInUberMode
parameter_list|()
throws|throws
name|Exception
block|{
name|setupTest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|task
operator|.
name|statusUpdate
argument_list|(
name|umbilical
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ExitException
operator|.
name|class
argument_list|)
DECL|method|testStatusUpdateExitsInNonUberMode ()
specifier|public
name|void
name|testStatusUpdateExitsInNonUberMode
parameter_list|()
throws|throws
name|Exception
block|{
name|setupTest
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|task
operator|.
name|statusUpdate
argument_list|(
name|umbilical
argument_list|)
expr_stmt|;
block|}
DECL|method|setupTest (boolean uberized)
specifier|private
name|void
name|setupTest
parameter_list|(
name|boolean
name|uberized
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"mapreduce.task.uberized"
argument_list|,
name|uberized
argument_list|)
expr_stmt|;
name|task
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|umbilical
operator|.
name|statusUpdate
argument_list|(
name|any
argument_list|(
name|TaskAttemptID
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|TaskStatus
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|feedback
argument_list|)
expr_stmt|;
comment|// to avoid possible infinite loop
name|when
argument_list|(
name|feedback
operator|.
name|getTaskFound
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|class|StubTask
specifier|public
class|class
name|StubTask
extends|extends
name|Task
block|{
annotation|@
name|Override
DECL|method|run (JobConf job, TaskUmbilicalProtocol umbilical)
specifier|public
name|void
name|run
parameter_list|(
name|JobConf
name|job
parameter_list|,
name|TaskUmbilicalProtocol
name|umbilical
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
throws|,
name|InterruptedException
block|{
comment|// nop
block|}
annotation|@
name|Override
DECL|method|isMapTask ()
specifier|public
name|boolean
name|isMapTask
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

