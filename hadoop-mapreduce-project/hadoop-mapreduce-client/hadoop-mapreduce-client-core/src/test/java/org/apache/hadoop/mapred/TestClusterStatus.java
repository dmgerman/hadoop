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
DECL|class|TestClusterStatus
specifier|public
class|class
name|TestClusterStatus
block|{
DECL|field|clusterStatus
specifier|private
name|ClusterStatus
name|clusterStatus
init|=
operator|new
name|ClusterStatus
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testGraylistedTrackers ()
specifier|public
name|void
name|testGraylistedTrackers
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|clusterStatus
operator|.
name|getGraylistedTrackers
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|clusterStatus
operator|.
name|getGraylistedTrackerNames
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testJobTrackerState ()
specifier|public
name|void
name|testJobTrackerState
parameter_list|()
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|JobTracker
operator|.
name|State
operator|.
name|RUNNING
argument_list|,
name|clusterStatus
operator|.
name|getJobTrackerState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

