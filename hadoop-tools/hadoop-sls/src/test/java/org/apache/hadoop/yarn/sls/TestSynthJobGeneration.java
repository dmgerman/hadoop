begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math3
operator|.
name|random
operator|.
name|JDKRandomGenerator
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
name|ExecutionType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|JsonMappingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|ObjectMapper
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
name|yarn
operator|.
name|sls
operator|.
name|synthetic
operator|.
name|SynthJob
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
name|sls
operator|.
name|synthetic
operator|.
name|SynthTraceJobProducer
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|util
operator|.
name|Arrays
import|;
end_import

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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
operator|.
name|Feature
operator|.
name|INTERN_FIELD_NAMES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|DeserializationConfig
operator|.
name|Feature
operator|.
name|FAIL_ON_UNKNOWN_PROPERTIES
import|;
end_import

begin_comment
comment|/**  * Simple test class driving the {@code SynthTraceJobProducer}, and validating  * jobs produce are within expected range.  */
end_comment

begin_class
DECL|class|TestSynthJobGeneration
specifier|public
class|class
name|TestSynthJobGeneration
block|{
DECL|field|LOG
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSynthJobGeneration
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testWorkloadGenerateTime ()
specifier|public
name|void
name|testWorkloadGenerateTime
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
name|String
name|workloadJson
init|=
literal|"{\"job_classes\": [], \"time_distribution\":["
operator|+
literal|"{\"time\": 0, \"weight\": 1}, "
operator|+
literal|"{\"time\": 30, \"weight\": 0},"
operator|+
literal|"{\"time\": 60, \"weight\": 2},"
operator|+
literal|"{\"time\": 90, \"weight\": 1}"
operator|+
literal|"]}"
decl_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|INTERN_FIELD_NAMES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|SynthTraceJobProducer
operator|.
name|Workload
name|wl
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|workloadJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Workload
operator|.
name|class
argument_list|)
decl_stmt|;
name|JDKRandomGenerator
name|rand
init|=
operator|new
name|JDKRandomGenerator
argument_list|()
decl_stmt|;
name|rand
operator|.
name|setSeed
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|wl
operator|.
name|init
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|int
name|bucket0
init|=
literal|0
decl_stmt|;
name|int
name|bucket1
init|=
literal|0
decl_stmt|;
name|int
name|bucket2
init|=
literal|0
decl_stmt|;
name|int
name|bucket3
init|=
literal|0
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
literal|1000
condition|;
operator|++
name|i
control|)
block|{
name|long
name|time
init|=
name|wl
operator|.
name|generateSubmissionTime
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Generated time "
operator|+
name|time
argument_list|)
expr_stmt|;
if|if
condition|(
name|time
operator|<
literal|30
condition|)
block|{
name|bucket0
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|time
operator|<
literal|60
condition|)
block|{
name|bucket1
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|time
operator|<
literal|90
condition|)
block|{
name|bucket2
operator|++
expr_stmt|;
block|}
else|else
block|{
name|bucket3
operator|++
expr_stmt|;
block|}
block|}
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucket0
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucket1
operator|==
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucket2
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucket3
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucket2
operator|>
name|bucket0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|bucket2
operator|>
name|bucket3
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"bucket0 {}, bucket1 {}, bucket2 {}, bucket3 {}"
argument_list|,
name|bucket0
argument_list|,
name|bucket1
argument_list|,
name|bucket2
argument_list|,
name|bucket3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testMapReduce ()
specifier|public
name|void
name|testMapReduce
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SynthTraceJobProducer
operator|.
name|SLS_SYNTHETIC_TRACE_FILE
argument_list|,
literal|"src/test/resources/syn.json"
argument_list|)
expr_stmt|;
name|SynthTraceJobProducer
name|stjp
init|=
operator|new
name|SynthTraceJobProducer
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|stjp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SynthJob
name|js
init|=
operator|(
name|SynthJob
operator|)
name|stjp
operator|.
name|getNextJob
argument_list|()
decl_stmt|;
name|int
name|jobCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|js
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|validateJob
argument_list|(
name|js
argument_list|)
expr_stmt|;
name|js
operator|=
operator|(
name|SynthJob
operator|)
name|stjp
operator|.
name|getNextJob
argument_list|()
expr_stmt|;
name|jobCount
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|stjp
operator|.
name|getNumJobs
argument_list|()
argument_list|,
name|jobCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGeneric ()
specifier|public
name|void
name|testGeneric
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SynthTraceJobProducer
operator|.
name|SLS_SYNTHETIC_TRACE_FILE
argument_list|,
literal|"src/test/resources/syn_generic.json"
argument_list|)
expr_stmt|;
name|SynthTraceJobProducer
name|stjp
init|=
operator|new
name|SynthTraceJobProducer
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|stjp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SynthJob
name|js
init|=
operator|(
name|SynthJob
operator|)
name|stjp
operator|.
name|getNextJob
argument_list|()
decl_stmt|;
name|int
name|jobCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|js
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|validateJob
argument_list|(
name|js
argument_list|)
expr_stmt|;
name|js
operator|=
operator|(
name|SynthJob
operator|)
name|stjp
operator|.
name|getNextJob
argument_list|()
expr_stmt|;
name|jobCount
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|stjp
operator|.
name|getNumJobs
argument_list|()
argument_list|,
name|jobCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStream ()
specifier|public
name|void
name|testStream
parameter_list|()
throws|throws
name|IllegalArgumentException
throws|,
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|SynthTraceJobProducer
operator|.
name|SLS_SYNTHETIC_TRACE_FILE
argument_list|,
literal|"src/test/resources/syn_stream.json"
argument_list|)
expr_stmt|;
name|SynthTraceJobProducer
name|stjp
init|=
operator|new
name|SynthTraceJobProducer
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|stjp
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|SynthJob
name|js
init|=
operator|(
name|SynthJob
operator|)
name|stjp
operator|.
name|getNextJob
argument_list|()
decl_stmt|;
name|int
name|jobCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|js
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|js
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|validateJob
argument_list|(
name|js
argument_list|)
expr_stmt|;
name|js
operator|=
operator|(
name|SynthJob
operator|)
name|stjp
operator|.
name|getNextJob
argument_list|()
expr_stmt|;
name|jobCount
operator|++
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|stjp
operator|.
name|getNumJobs
argument_list|()
argument_list|,
name|jobCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSample ()
specifier|public
name|void
name|testSample
parameter_list|()
throws|throws
name|IOException
block|{
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|INTERN_FIELD_NAMES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|configure
argument_list|(
name|FAIL_ON_UNKNOWN_PROPERTIES
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|JDKRandomGenerator
name|rand
init|=
operator|new
name|JDKRandomGenerator
argument_list|()
decl_stmt|;
name|rand
operator|.
name|setSeed
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|String
name|valJson
init|=
literal|"{\"val\" : 5 }"
decl_stmt|;
name|SynthTraceJobProducer
operator|.
name|Sample
name|valSample
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|valJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
decl_stmt|;
name|valSample
operator|.
name|init
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|int
name|val
init|=
name|valSample
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|val
argument_list|)
expr_stmt|;
name|String
name|distJson
init|=
literal|"{\"val\" : 5, \"std\" : 1 }"
decl_stmt|;
name|SynthTraceJobProducer
operator|.
name|Sample
name|distSample
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|distJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
decl_stmt|;
name|distSample
operator|.
name|init
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|double
name|dist
init|=
name|distSample
operator|.
name|getDouble
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|dist
operator|>
literal|2
operator|&&
name|dist
operator|<
literal|8
argument_list|)
expr_stmt|;
name|String
name|normdistJson
init|=
literal|"{\"val\" : 5, \"std\" : 1, \"dist\": \"NORM\" }"
decl_stmt|;
name|SynthTraceJobProducer
operator|.
name|Sample
name|normdistSample
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|normdistJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
decl_stmt|;
name|normdistSample
operator|.
name|init
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|double
name|normdist
init|=
name|normdistSample
operator|.
name|getDouble
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|normdist
operator|>
literal|2
operator|&&
name|normdist
operator|<
literal|8
argument_list|)
expr_stmt|;
name|String
name|discreteJson
init|=
literal|"{\"discrete\" : [2, 4, 6, 8]}"
decl_stmt|;
name|SynthTraceJobProducer
operator|.
name|Sample
name|discreteSample
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|discreteJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
decl_stmt|;
name|discreteSample
operator|.
name|init
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|int
name|discrete
init|=
name|discreteSample
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Integer
index|[]
block|{
literal|2
block|,
literal|4
block|,
literal|6
block|,
literal|8
block|}
argument_list|)
operator|.
name|contains
argument_list|(
name|discrete
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|discreteWeightsJson
init|=
literal|"{\"discrete\" : [2, 4, 6, 8], "
operator|+
literal|"\"weights\": [0, 0, 0, 1]}"
decl_stmt|;
name|SynthTraceJobProducer
operator|.
name|Sample
name|discreteWeightsSample
init|=
name|mapper
operator|.
name|readValue
argument_list|(
name|discreteWeightsJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
decl_stmt|;
name|discreteWeightsSample
operator|.
name|init
argument_list|(
name|rand
argument_list|)
expr_stmt|;
name|int
name|discreteWeights
init|=
name|discreteWeightsSample
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|discreteWeights
argument_list|)
expr_stmt|;
name|String
name|invalidJson
init|=
literal|"{\"val\" : 5, \"discrete\" : [2, 4, 6, 8], "
operator|+
literal|"\"weights\": [0, 0, 0, 1]}"
decl_stmt|;
try|try
block|{
name|mapper
operator|.
name|readValue
argument_list|(
name|invalidJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JsonMappingException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Instantiation of"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|invalidDistJson
init|=
literal|"{\"val\" : 5, \"std\" : 1, "
operator|+
literal|"\"dist\": \"INVALID\" }"
decl_stmt|;
try|try
block|{
name|mapper
operator|.
name|readValue
argument_list|(
name|invalidDistJson
argument_list|,
name|SynthTraceJobProducer
operator|.
name|Sample
operator|.
name|class
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JsonMappingException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Instantiation of"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateJob (SynthJob js)
specifier|private
name|void
name|validateJob
parameter_list|(
name|SynthJob
name|js
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|js
operator|.
name|getSubmissionTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|js
operator|.
name|getDuration
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|js
operator|.
name|getTotalSlotTime
argument_list|()
operator|>=
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|js
operator|.
name|hasDeadline
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
name|js
operator|.
name|getDeadline
argument_list|()
operator|>
name|js
operator|.
name|getSubmissionTime
argument_list|()
operator|+
name|js
operator|.
name|getDuration
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|js
operator|.
name|getTasks
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|SynthJob
operator|.
name|SynthTask
name|t
range|:
name|js
operator|.
name|getTasks
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|t
operator|.
name|getType
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|getTime
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|getMemory
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|getVcores
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ExecutionType
operator|.
name|GUARANTEED
argument_list|,
name|t
operator|.
name|getExecutionType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

