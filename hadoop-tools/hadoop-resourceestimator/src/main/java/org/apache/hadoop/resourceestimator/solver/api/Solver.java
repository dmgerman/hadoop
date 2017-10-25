begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.solver.api
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|resourceestimator
operator|.
name|solver
operator|.
name|api
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|resourceestimator
operator|.
name|common
operator|.
name|api
operator|.
name|RecurrenceId
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|api
operator|.
name|PredictionSkylineStore
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
name|skylinestore
operator|.
name|exceptions
operator|.
name|SkylineStoreException
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
name|solver
operator|.
name|exceptions
operator|.
name|SolverException
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

begin_comment
comment|/**  * Solver takes recurring pipeline's {@link ResourceSkyline} history as input,  * predicts its {@link Resource} requirement at each time t for the next run,  * and translate them into {@link ResourceSkyline} which will be used to make  * recurring resource reservations.  */
end_comment

begin_interface
DECL|interface|Solver
specifier|public
interface|interface
name|Solver
block|{
comment|/**    * Initializing the Solver, including loading solver parameters from    * configuration file.    *    * @param config       {@link Configuration} for the Solver.    * @param skylineStore the {@link PredictionSkylineStore} which stores    *                     predicted {@code Resource} allocations.    */
DECL|method|init (Configuration config, PredictionSkylineStore skylineStore)
name|void
name|init
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|PredictionSkylineStore
name|skylineStore
parameter_list|)
function_decl|;
comment|/**    * The Solver reads recurring pipeline's {@link ResourceSkyline} history, and    * precits its {@link ResourceSkyline} requirements for the next run.    *    * @param jobHistory the {@link ResourceSkyline}s of the recurring pipeline in    *     previous runs. The {@link RecurrenceId} identifies one run of the    *     recurring pipeline, and the list of {@link ResourceSkyline}s    *     records the {@link ResourceSkyline} of each job within the pipeline.    * @return the amount of {@link Resource} requested by the pipeline for the    * next run (discretized by timeInterval).    * @throws SolverException       if: (1) input is invalid; (2) the number of    *     instances in the jobHistory is smaller than the minimum    *     requirement; (3) solver runtime has unexpected behaviors;    * @throws SkylineStoreException if it fails to add predicted {@code Resource}    *     allocation to the {@link PredictionSkylineStore}.    */
DECL|method|solve ( Map<RecurrenceId, List<ResourceSkyline>> jobHistory)
name|RLESparseResourceAllocation
name|solve
parameter_list|(
name|Map
argument_list|<
name|RecurrenceId
argument_list|,
name|List
argument_list|<
name|ResourceSkyline
argument_list|>
argument_list|>
name|jobHistory
parameter_list|)
throws|throws
name|SolverException
throws|,
name|SkylineStoreException
function_decl|;
comment|/**    * Release the resource used by the Solver.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

