begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.common.serialization
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|common
operator|.
name|serialization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|resourceestimator
operator|.
name|common
operator|.
name|api
operator|.
name|ResourceSkyline
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|reservation
operator|.
name|RLESparseResourceAllocation
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
name|reservation
operator|.
name|ReservationInterval
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
name|util
operator|.
name|resource
operator|.
name|DefaultResourceCalculator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|GsonBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|reflect
operator|.
name|TypeToken
import|;
end_import

begin_comment
comment|/**  * Test ResourceSkylineSerDe.  */
end_comment

begin_class
DECL|class|TestResourceSkylineSerDe
specifier|public
class|class
name|TestResourceSkylineSerDe
block|{
comment|/**    * Testing variables.    */
DECL|field|gson
specifier|private
name|Gson
name|gson
decl_stmt|;
DECL|field|resourceSkyline
specifier|private
name|ResourceSkyline
name|resourceSkyline
decl_stmt|;
DECL|field|resource
specifier|private
name|Resource
name|resource
decl_stmt|;
DECL|field|resource2
specifier|private
name|Resource
name|resource2
decl_stmt|;
DECL|field|resourceOverTime
specifier|private
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|Resource
argument_list|>
name|resourceOverTime
decl_stmt|;
DECL|field|skylineList
specifier|private
name|RLESparseResourceAllocation
name|skylineList
decl_stmt|;
DECL|method|setup ()
annotation|@
name|Before
specifier|public
specifier|final
name|void
name|setup
parameter_list|()
block|{
name|resourceOverTime
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
name|skylineList
operator|=
operator|new
name|RLESparseResourceAllocation
argument_list|(
name|resourceOverTime
argument_list|,
operator|new
name|DefaultResourceCalculator
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
literal|100
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|resource2
operator|=
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
operator|*
literal|200
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|gson
operator|=
operator|new
name|GsonBuilder
argument_list|()
operator|.
name|registerTypeAdapter
argument_list|(
name|Resource
operator|.
name|class
argument_list|,
operator|new
name|ResourceSerDe
argument_list|()
argument_list|)
operator|.
name|registerTypeAdapter
argument_list|(
name|RLESparseResourceAllocation
operator|.
name|class
argument_list|,
operator|new
name|RLESparseResourceAllocationSerDe
argument_list|()
argument_list|)
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
DECL|method|testSerialization ()
annotation|@
name|Test
specifier|public
specifier|final
name|void
name|testSerialization
parameter_list|()
block|{
name|ReservationInterval
name|riAdd
init|=
operator|new
name|ReservationInterval
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|skylineList
operator|.
name|addInterval
argument_list|(
name|riAdd
argument_list|,
name|resource
argument_list|)
expr_stmt|;
name|riAdd
operator|=
operator|new
name|ReservationInterval
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|skylineList
operator|.
name|addInterval
argument_list|(
name|riAdd
argument_list|,
name|resource2
argument_list|)
expr_stmt|;
name|resourceSkyline
operator|=
operator|new
name|ResourceSkyline
argument_list|(
literal|"1"
argument_list|,
literal|1024.5
argument_list|,
literal|0
argument_list|,
literal|20
argument_list|,
name|resource
argument_list|,
name|skylineList
argument_list|)
expr_stmt|;
specifier|final
name|String
name|json
init|=
name|gson
operator|.
name|toJson
argument_list|(
name|resourceSkyline
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|()
block|{         }
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ResourceSkyline
name|resourceSkylineDe
init|=
name|gson
operator|.
name|fromJson
argument_list|(
name|json
argument_list|,
operator|new
name|TypeToken
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|()
block|{         }
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceSkylineDe
operator|.
name|getJobId
argument_list|()
argument_list|,
name|resourceSkyline
operator|.
name|getJobId
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceSkylineDe
operator|.
name|getJobInputDataSize
argument_list|()
argument_list|,
name|resourceSkyline
operator|.
name|getJobInputDataSize
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceSkylineDe
operator|.
name|getJobSubmissionTime
argument_list|()
argument_list|,
name|resourceSkyline
operator|.
name|getJobSubmissionTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceSkylineDe
operator|.
name|getJobFinishTime
argument_list|()
argument_list|,
name|resourceSkyline
operator|.
name|getJobFinishTime
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceSkylineDe
operator|.
name|getContainerSpec
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|resourceSkyline
operator|.
name|getContainerSpec
argument_list|()
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resourceSkylineDe
operator|.
name|getContainerSpec
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|resourceSkyline
operator|.
name|getContainerSpec
argument_list|()
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RLESparseResourceAllocation
name|skylineList2
init|=
name|resourceSkyline
operator|.
name|getSkylineList
argument_list|()
decl_stmt|;
specifier|final
name|RLESparseResourceAllocation
name|skylineListDe
init|=
name|resourceSkylineDe
operator|.
name|getSkylineList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|skylineList2
operator|.
name|getCapacityAtTime
argument_list|(
name|i
argument_list|)
operator|.
name|getMemorySize
argument_list|()
argument_list|,
name|skylineListDe
operator|.
name|getCapacityAtTime
argument_list|(
name|i
argument_list|)
operator|.
name|getMemorySize
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|skylineList2
operator|.
name|getCapacityAtTime
argument_list|(
name|i
argument_list|)
operator|.
name|getVirtualCores
argument_list|()
argument_list|,
name|skylineListDe
operator|.
name|getCapacityAtTime
argument_list|(
name|i
argument_list|)
operator|.
name|getVirtualCores
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|cleanUp ()
annotation|@
name|After
specifier|public
specifier|final
name|void
name|cleanUp
parameter_list|()
block|{
name|gson
operator|=
literal|null
expr_stmt|;
name|resourceSkyline
operator|=
literal|null
expr_stmt|;
name|resourceOverTime
operator|.
name|clear
argument_list|()
expr_stmt|;
name|resourceOverTime
operator|=
literal|null
expr_stmt|;
name|resource
operator|=
literal|null
expr_stmt|;
name|resource2
operator|=
literal|null
expr_stmt|;
name|skylineList
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

