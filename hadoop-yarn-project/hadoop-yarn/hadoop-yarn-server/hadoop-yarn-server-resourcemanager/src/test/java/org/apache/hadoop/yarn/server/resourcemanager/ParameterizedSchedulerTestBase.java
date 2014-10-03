begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|yarn
operator|.
name|conf
operator|.
name|YarnConfiguration
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|capacity
operator|.
name|CapacityScheduler
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairScheduler
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
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fair
operator|.
name|FairSchedulerConfiguration
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
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|io
operator|.
name|FileWriter
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|ParameterizedSchedulerTestBase
specifier|public
specifier|abstract
class|class
name|ParameterizedSchedulerTestBase
block|{
DECL|field|TEST_DIR
specifier|protected
specifier|final
specifier|static
name|String
name|TEST_DIR
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|FS_ALLOC_FILE
specifier|private
specifier|final
specifier|static
name|String
name|FS_ALLOC_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"test-fs-queues.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|schedulerType
specifier|private
name|SchedulerType
name|schedulerType
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
init|=
literal|null
decl_stmt|;
DECL|enum|SchedulerType
specifier|public
enum|enum
name|SchedulerType
block|{
DECL|enumConstant|CAPACITY
DECL|enumConstant|FAIR
name|CAPACITY
block|,
name|FAIR
block|}
DECL|method|ParameterizedSchedulerTestBase (SchedulerType type)
specifier|public
name|ParameterizedSchedulerTestBase
parameter_list|(
name|SchedulerType
name|type
parameter_list|)
block|{
name|schedulerType
operator|=
name|type
expr_stmt|;
block|}
DECL|method|getConf ()
specifier|public
name|YarnConfiguration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Parameterized
operator|.
name|Parameters
DECL|method|getParameters ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|SchedulerType
index|[]
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SchedulerType
index|[]
index|[]
block|{
block|{
name|SchedulerType
operator|.
name|CAPACITY
block|}
block|,
block|{
name|SchedulerType
operator|.
name|FAIR
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|configureScheduler ()
specifier|public
name|void
name|configureScheduler
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|schedulerType
condition|)
block|{
case|case
name|CAPACITY
case|:
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|CapacityScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|FAIR
case|:
name|configureFairScheduler
argument_list|(
name|conf
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
DECL|method|configureFairScheduler (YarnConfiguration conf)
specifier|private
name|void
name|configureFairScheduler
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Disable queueMaxAMShare limitation for fair scheduler
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|FS_ALLOC_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queueMaxAMShareDefault>-1.0</queueMaxAMShareDefault>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FairScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FairSchedulerConfiguration
operator|.
name|ALLOCATION_FILE
argument_list|,
name|FS_ALLOC_FILE
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

