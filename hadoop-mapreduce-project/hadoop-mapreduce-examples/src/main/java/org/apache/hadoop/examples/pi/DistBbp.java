begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.examples.pi
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|examples
operator|.
name|pi
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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
name|Configured
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
name|examples
operator|.
name|pi
operator|.
name|DistSum
operator|.
name|Computation
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
name|examples
operator|.
name|pi
operator|.
name|DistSum
operator|.
name|Parameters
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
name|examples
operator|.
name|pi
operator|.
name|math
operator|.
name|Bellard
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
name|examples
operator|.
name|pi
operator|.
name|math
operator|.
name|Summation
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
name|examples
operator|.
name|pi
operator|.
name|math
operator|.
name|Bellard
operator|.
name|Parameter
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
name|Tool
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
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * A map/reduce program that uses a BBP-type method to compute exact   * binary digits of Pi.  * This program is designed for computing the n th bit of Pi,  * for large n, say n&gt;= 10^8.  * For computing lower bits of Pi, consider using bbp.  *  * The actually computation is done by DistSum jobs.  * The steps for launching the jobs are:  *   * (1) Initialize parameters.  * (2) Create a list of sums.  * (3) Read computed values from the given local directory.  * (4) Remove the computed values from the sums.  * (5) Partition the remaining sums into computation jobs.  * (6) Submit the computation jobs to a cluster and then wait for the results.  * (7) Write job outputs to the given local directory.  * (8) Combine the job outputs and print the Pi bits.  */
end_comment

begin_comment
comment|/*  * The command line format is:  *> hadoop org.apache.hadoop.examples.pi.DistBbp \  *<b><nThreads><nJobs><type><nPart><remoteDir><localDir>  *   * And the parameters are:  *<b>         The number of bits to skip, i.e. compute the (b+1)th position.  *<nThreads>  The number of working threads.  *<nJobs>     The number of jobs per sum.  *<type>      'm' for map side job, 'r' for reduce side job, 'x' for mix type.  *<nPart>     The number of parts per job.  *<remoteDir> Remote directory for submitting jobs.  *<localDir>  Local directory for storing output files.  *  * Note that it may take a long time to finish all the jobs when<b> is large.  * If the program is killed in the middle of the execution, the same command with  * a different<remoteDir> can be used to resume the execution.  For example, suppose  * we use the following command to compute the (10^15+57)th bit of Pi.  *   *> hadoop org.apache.hadoop.examples.pi.DistBbp \  *          1,000,000,000,000,056 20 1000 x 500 remote/a local/output  *  * It uses 20 threads to summit jobs so that there are at most 20 concurrent jobs.  * Each sum (there are totally 14 sums) is partitioned into 1000 jobs.  * The jobs will be executed in map-side or reduce-side.  Each job has 500 parts.  * The remote directory for the jobs is remote/a and the local directory  * for storing output is local/output.  Depends on the cluster configuration,  * it may take many days to finish the entire execution.  If the execution is killed,  * we may resume it by  *   *> hadoop org.apache.hadoop.examples.pi.DistBbp \  *          1,000,000,000,000,056 20 1000 x 500 remote/b local/output  */
end_comment

begin_class
DECL|class|DistBbp
specifier|public
specifier|final
class|class
name|DistBbp
extends|extends
name|Configured
implements|implements
name|Tool
block|{
DECL|field|DESCRIPTION
specifier|public
specifier|static
specifier|final
name|String
name|DESCRIPTION
init|=
literal|"A map/reduce program that uses a BBP-type formula to compute exact bits of Pi."
decl_stmt|;
DECL|field|timer
specifier|private
specifier|final
name|Util
operator|.
name|Timer
name|timer
init|=
operator|new
name|Util
operator|.
name|Timer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
comment|/** {@inheritDoc} */
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
comment|//parse arguments
if|if
condition|(
name|args
operator|.
name|length
operator|!=
name|DistSum
operator|.
name|Parameters
operator|.
name|COUNT
operator|+
literal|1
condition|)
return|return
name|Util
operator|.
name|printUsage
argument_list|(
name|args
argument_list|,
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"<b> "
operator|+
name|Parameters
operator|.
name|LIST
operator|+
literal|"\n<b> The number of bits to skip, i.e. compute the (b+1)th position."
operator|+
name|Parameters
operator|.
name|DESCRIPTION
argument_list|)
return|;
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|long
name|b
init|=
name|Util
operator|.
name|string2long
argument_list|(
name|args
index|[
name|i
operator|++
index|]
argument_list|)
decl_stmt|;
specifier|final
name|DistSum
operator|.
name|Parameters
name|parameters
init|=
name|DistSum
operator|.
name|Parameters
operator|.
name|parse
argument_list|(
name|args
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"b = "
operator|+
name|b
operator|+
literal|"< 0"
argument_list|)
throw|;
name|Util
operator|.
name|printBitSkipped
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|Util
operator|.
name|out
operator|.
name|println
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|Util
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
comment|//initialize sums
specifier|final
name|DistSum
name|distsum
init|=
operator|new
name|DistSum
argument_list|()
decl_stmt|;
name|distsum
operator|.
name|setConf
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
name|distsum
operator|.
name|setParameters
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|isVerbose
init|=
name|getConf
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|Parser
operator|.
name|VERBOSE_PROPERTY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Parameter
argument_list|,
name|List
argument_list|<
name|TaskResult
argument_list|>
argument_list|>
name|existings
init|=
operator|new
name|Parser
argument_list|(
name|isVerbose
argument_list|)
operator|.
name|parse
argument_list|(
name|parameters
operator|.
name|localDir
operator|.
name|getPath
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Parser
operator|.
name|combine
argument_list|(
name|existings
argument_list|)
expr_stmt|;
for|for
control|(
name|List
argument_list|<
name|TaskResult
argument_list|>
name|tr
range|:
name|existings
operator|.
name|values
argument_list|()
control|)
name|Collections
operator|.
name|sort
argument_list|(
name|tr
argument_list|)
expr_stmt|;
name|Util
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|Bellard
operator|.
name|Parameter
argument_list|,
name|Bellard
operator|.
name|Sum
argument_list|>
name|sums
init|=
name|Bellard
operator|.
name|getSums
argument_list|(
name|b
argument_list|,
name|parameters
operator|.
name|nJobs
argument_list|,
name|existings
argument_list|)
decl_stmt|;
name|Util
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
comment|//execute the computations
name|execute
argument_list|(
name|distsum
argument_list|,
name|sums
argument_list|)
expr_stmt|;
comment|//compute Pi from the sums
specifier|final
name|double
name|pi
init|=
name|Bellard
operator|.
name|computePi
argument_list|(
name|b
argument_list|,
name|sums
argument_list|)
decl_stmt|;
name|Util
operator|.
name|printBitSkipped
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|Util
operator|.
name|out
operator|.
name|println
argument_list|(
name|Util
operator|.
name|pi2string
argument_list|(
name|pi
argument_list|,
name|Bellard
operator|.
name|bit2terms
argument_list|(
name|b
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
comment|/** Execute DistSum computations */
DECL|method|execute (DistSum distsum, final Map<Bellard.Parameter, Bellard.Sum> sums)
specifier|private
name|void
name|execute
parameter_list|(
name|DistSum
name|distsum
parameter_list|,
specifier|final
name|Map
argument_list|<
name|Bellard
operator|.
name|Parameter
argument_list|,
name|Bellard
operator|.
name|Sum
argument_list|>
name|sums
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|Computation
argument_list|>
name|computations
init|=
operator|new
name|ArrayList
argument_list|<
name|Computation
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Bellard
operator|.
name|Parameter
name|p
range|:
name|Bellard
operator|.
name|Parameter
operator|.
name|values
argument_list|()
control|)
for|for
control|(
name|Summation
name|s
range|:
name|sums
operator|.
name|get
argument_list|(
name|p
argument_list|)
control|)
if|if
condition|(
name|s
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
name|computations
operator|.
name|add
argument_list|(
name|distsum
operator|.
expr|new
name|Computation
argument_list|(
name|i
operator|++
argument_list|,
name|p
operator|.
name|toString
argument_list|()
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|computations
operator|.
name|isEmpty
argument_list|()
condition|)
name|Util
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"No computation"
argument_list|)
expr_stmt|;
else|else
block|{
name|timer
operator|.
name|tick
argument_list|(
literal|"execute "
operator|+
name|computations
operator|.
name|size
argument_list|()
operator|+
literal|" computation(s)"
argument_list|)
expr_stmt|;
name|Util
operator|.
name|execute
argument_list|(
name|distsum
operator|.
name|getParameters
argument_list|()
operator|.
name|nThreads
argument_list|,
name|computations
argument_list|)
expr_stmt|;
name|timer
operator|.
name|tick
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** main */
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|exit
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
literal|null
argument_list|,
operator|new
name|DistBbp
argument_list|()
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

