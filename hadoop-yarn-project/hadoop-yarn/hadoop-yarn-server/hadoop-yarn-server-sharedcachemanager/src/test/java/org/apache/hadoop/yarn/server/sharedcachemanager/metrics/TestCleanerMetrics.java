begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.sharedcachemanager.metrics
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
name|sharedcachemanager
operator|.
name|metrics
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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

begin_class
DECL|class|TestCleanerMetrics
specifier|public
class|class
name|TestCleanerMetrics
block|{
DECL|field|conf
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cleanerMetrics
name|CleanerMetrics
name|cleanerMetrics
decl_stmt|;
annotation|@
name|Before
DECL|method|init ()
specifier|public
name|void
name|init
parameter_list|()
block|{
name|cleanerMetrics
operator|=
name|CleanerMetrics
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMetricsOverMultiplePeriods ()
specifier|public
name|void
name|testMetricsOverMultiplePeriods
parameter_list|()
block|{
name|simulateACleanerRun
argument_list|()
expr_stmt|;
name|assertMetrics
argument_list|(
literal|4
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|simulateACleanerRun
argument_list|()
expr_stmt|;
name|assertMetrics
argument_list|(
literal|4
argument_list|,
literal|8
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|simulateACleanerRun ()
specifier|public
name|void
name|simulateACleanerRun
parameter_list|()
block|{
name|cleanerMetrics
operator|.
name|reportCleaningStart
argument_list|()
expr_stmt|;
name|cleanerMetrics
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
name|cleanerMetrics
operator|.
name|reportAFileDelete
argument_list|()
expr_stmt|;
name|cleanerMetrics
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
name|cleanerMetrics
operator|.
name|reportAFileProcess
argument_list|()
expr_stmt|;
block|}
DECL|method|assertMetrics (int proc, int totalProc, int del, int totalDel)
name|void
name|assertMetrics
parameter_list|(
name|int
name|proc
parameter_list|,
name|int
name|totalProc
parameter_list|,
name|int
name|del
parameter_list|,
name|int
name|totalDel
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Processed files in the last period are not measured correctly"
argument_list|,
name|proc
argument_list|,
name|cleanerMetrics
operator|.
name|getProcessedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Total processed files are not measured correctly"
argument_list|,
name|totalProc
argument_list|,
name|cleanerMetrics
operator|.
name|getTotalProcessedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Deleted files in the last period are not measured correctly"
argument_list|,
name|del
argument_list|,
name|cleanerMetrics
operator|.
name|getDeletedFiles
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Total deleted files are not measured correctly"
argument_list|,
name|totalDel
argument_list|,
name|cleanerMetrics
operator|.
name|getTotalDeletedFiles
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

