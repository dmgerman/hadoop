begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.speculate
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
operator|.
name|speculate
package|;
end_package

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
DECL|class|TestDataStatistics
specifier|public
class|class
name|TestDataStatistics
block|{
DECL|field|TOL
specifier|private
specifier|static
specifier|final
name|double
name|TOL
init|=
literal|0.001
decl_stmt|;
annotation|@
name|Test
DECL|method|testEmptyDataStatistics ()
specifier|public
name|void
name|testEmptyDataStatistics
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStatistics
name|statistics
init|=
operator|new
name|DataStatistics
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|count
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|mean
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|var
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|std
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|outlier
argument_list|(
literal|1.0f
argument_list|)
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSingleEntryDataStatistics ()
specifier|public
name|void
name|testSingleEntryDataStatistics
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStatistics
name|statistics
init|=
operator|new
name|DataStatistics
argument_list|(
literal|17.29
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|statistics
operator|.
name|count
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|17.29
argument_list|,
name|statistics
operator|.
name|mean
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|var
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|statistics
operator|.
name|std
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|17.29
argument_list|,
name|statistics
operator|.
name|outlier
argument_list|(
literal|1.0f
argument_list|)
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMutiEntryDataStatistics ()
specifier|public
name|void
name|testMutiEntryDataStatistics
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStatistics
name|statistics
init|=
operator|new
name|DataStatistics
argument_list|()
decl_stmt|;
name|statistics
operator|.
name|add
argument_list|(
literal|17
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|add
argument_list|(
literal|29
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|statistics
operator|.
name|count
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|23.0
argument_list|,
name|statistics
operator|.
name|mean
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|36.0
argument_list|,
name|statistics
operator|.
name|var
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|6.0
argument_list|,
name|statistics
operator|.
name|std
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|29.0
argument_list|,
name|statistics
operator|.
name|outlier
argument_list|(
literal|1.0f
argument_list|)
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUpdateStatistics ()
specifier|public
name|void
name|testUpdateStatistics
parameter_list|()
throws|throws
name|Exception
block|{
name|DataStatistics
name|statistics
init|=
operator|new
name|DataStatistics
argument_list|(
literal|17
argument_list|)
decl_stmt|;
name|statistics
operator|.
name|add
argument_list|(
literal|29
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|statistics
operator|.
name|count
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|23.0
argument_list|,
name|statistics
operator|.
name|mean
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|36.0
argument_list|,
name|statistics
operator|.
name|var
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|statistics
operator|.
name|updateStatistics
argument_list|(
literal|17
argument_list|,
literal|29
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|statistics
operator|.
name|count
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|29.0
argument_list|,
name|statistics
operator|.
name|mean
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0.0
argument_list|,
name|statistics
operator|.
name|var
argument_list|()
argument_list|,
name|TOL
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

