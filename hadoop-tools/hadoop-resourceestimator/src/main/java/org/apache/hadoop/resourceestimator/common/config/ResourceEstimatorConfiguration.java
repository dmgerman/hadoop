begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.resourceestimator.common.config
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
name|config
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
name|resourceestimator
operator|.
name|skylinestore
operator|.
name|impl
operator|.
name|InMemoryStore
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
name|impl
operator|.
name|LpSolver
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
name|translator
operator|.
name|impl
operator|.
name|BaseLogParser
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
name|translator
operator|.
name|impl
operator|.
name|NativeSingleLineParser
import|;
end_import

begin_comment
comment|/**  * Defines configuration keys for ResourceEstimatorServer.  */
end_comment

begin_class
DECL|class|ResourceEstimatorConfiguration
specifier|public
specifier|final
class|class
name|ResourceEstimatorConfiguration
block|{
comment|/**    * The location of the configuration file for ResourceEstimatorService.    */
DECL|field|CONFIG_FILE
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_FILE
init|=
literal|"resourceestimator-config.xml"
decl_stmt|;
comment|/**    * The URI for ResourceEstimatorService.    */
DECL|field|SERVICE_URI
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_URI
init|=
literal|"http://0.0.0.0/"
decl_stmt|;
comment|/**    * The port which ResourceEstimatorService listens to.    */
DECL|field|SERVICE_PORT
specifier|public
specifier|static
specifier|final
name|String
name|SERVICE_PORT
init|=
literal|"resourceestimator.service-port"
decl_stmt|;
comment|/**    * Default port number of ResourceEstimatorService.    */
DECL|field|DEFAULT_SERVICE_PORT
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_SERVICE_PORT
init|=
literal|9998
decl_stmt|;
comment|/**    * The class name of the skylinestore provider.    */
DECL|field|SKYLINESTORE_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|SKYLINESTORE_PROVIDER
init|=
literal|"resourceestimator.skylinestore.provider"
decl_stmt|;
comment|/**    * Default value for skylinestore provider, which is an in-memory implementation of skylinestore.    */
DECL|field|DEFAULT_SKYLINESTORE_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SKYLINESTORE_PROVIDER
init|=
name|InMemoryStore
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    * The class name of the translator provider.    */
DECL|field|TRANSLATOR_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|TRANSLATOR_PROVIDER
init|=
literal|"resourceestimator.translator.provider"
decl_stmt|;
comment|/**    * Default value for translator provider, which extracts resourceskylines from log streams.    */
DECL|field|DEFAULT_TRANSLATOR_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TRANSLATOR_PROVIDER
init|=
name|BaseLogParser
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    * The class name of the translator single-line parser, which parses a single line in the log.    */
DECL|field|TRANSLATOR_LINE_PARSER
specifier|public
specifier|static
specifier|final
name|String
name|TRANSLATOR_LINE_PARSER
init|=
literal|"resourceestimator.translator.line-parser"
decl_stmt|;
comment|/**    * Default value for translator single-line parser, which can parse one line in the sample log.    */
DECL|field|DEFAULT_TRANSLATOR_LINE_PARSER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_TRANSLATOR_LINE_PARSER
init|=
name|NativeSingleLineParser
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    * The class name of the solver provider.    */
DECL|field|SOLVER_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|SOLVER_PROVIDER
init|=
literal|"resourceestimator.solver.provider"
decl_stmt|;
comment|/**    * Default value for solver provider, which incorporates a Linear Programming model to make the prediction.    */
DECL|field|DEFAULT_SOLVER_PROVIDER
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_SOLVER_PROVIDER
init|=
name|LpSolver
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|/**    * The time length which is used to discretize job execution into intervals.    */
DECL|field|TIME_INTERVAL_KEY
specifier|public
specifier|static
specifier|final
name|String
name|TIME_INTERVAL_KEY
init|=
literal|"resourceestimator.timeInterval"
decl_stmt|;
comment|/**    * The parameter which tunes the tradeoff between resource over-allocation and under-allocation in the Linear Programming model.    */
DECL|field|SOLVER_ALPHA_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SOLVER_ALPHA_KEY
init|=
literal|"resourceestimator.solver.lp.alpha"
decl_stmt|;
comment|/**    * This parameter which controls the generalization of the Linear Programming model.    */
DECL|field|SOLVER_BETA_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SOLVER_BETA_KEY
init|=
literal|"resourceestimator.solver.lp.beta"
decl_stmt|;
comment|/**    * The minimum number of job runs required in order to make the prediction.    */
DECL|field|SOLVER_MIN_JOB_RUN_KEY
specifier|public
specifier|static
specifier|final
name|String
name|SOLVER_MIN_JOB_RUN_KEY
init|=
literal|"resourceestimator.solver.lp.minJobRuns"
decl_stmt|;
DECL|method|ResourceEstimatorConfiguration ()
specifier|private
name|ResourceEstimatorConfiguration
parameter_list|()
block|{}
block|}
end_class

end_unit

