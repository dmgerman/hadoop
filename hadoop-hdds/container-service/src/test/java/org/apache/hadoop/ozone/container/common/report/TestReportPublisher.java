begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.report
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|report
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadFactoryBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|GeneratedMessage
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
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|statemachine
operator|.
name|StateContext
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
name|concurrent
operator|.
name|HadoopExecutors
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
name|util
operator|.
name|concurrent
operator|.
name|ScheduledExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
comment|/**  * Test cases to test {@link ReportPublisher}.  */
end_comment

begin_class
DECL|class|TestReportPublisher
specifier|public
class|class
name|TestReportPublisher
block|{
comment|/**    * Dummy report publisher for testing.    */
DECL|class|DummyReportPublisher
specifier|private
class|class
name|DummyReportPublisher
extends|extends
name|ReportPublisher
block|{
DECL|field|frequency
specifier|private
specifier|final
name|long
name|frequency
decl_stmt|;
DECL|field|getReportCount
specifier|private
name|int
name|getReportCount
init|=
literal|0
decl_stmt|;
DECL|method|DummyReportPublisher (long frequency)
name|DummyReportPublisher
parameter_list|(
name|long
name|frequency
parameter_list|)
block|{
name|this
operator|.
name|frequency
operator|=
name|frequency
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getReportFrequency ()
specifier|protected
name|long
name|getReportFrequency
parameter_list|()
block|{
return|return
name|frequency
return|;
block|}
annotation|@
name|Override
DECL|method|getReport ()
specifier|protected
name|GeneratedMessage
name|getReport
parameter_list|()
block|{
name|getReportCount
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testReportPublisherInit ()
specifier|public
name|void
name|testReportPublisherInit
parameter_list|()
block|{
name|ReportPublisher
name|publisher
init|=
operator|new
name|DummyReportPublisher
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|dummyExecutorService
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ScheduledExecutorService
operator|.
name|class
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|dummyExecutorService
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dummyExecutorService
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|schedule
argument_list|(
name|publisher
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testScheduledReport ()
specifier|public
name|void
name|testScheduledReport
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ReportPublisher
name|publisher
init|=
operator|new
name|DummyReportPublisher
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|executorService
init|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Unit test ReportManager Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|150
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|DummyReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReportCount
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|150
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|DummyReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReportCount
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPublishReport ()
specifier|public
name|void
name|testPublishReport
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ReportPublisher
name|publisher
init|=
operator|new
name|DummyReportPublisher
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|StateContext
name|dummyContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|StateContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|ScheduledExecutorService
name|executorService
init|=
name|HadoopExecutors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|,
operator|new
name|ThreadFactoryBuilder
argument_list|()
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
operator|.
name|setNameFormat
argument_list|(
literal|"Unit test ReportManager Thread - %d"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|init
argument_list|(
name|dummyContext
argument_list|,
name|executorService
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|150
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|DummyReportPublisher
operator|)
name|publisher
operator|)
operator|.
name|getReportCount
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|dummyContext
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|addReport
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

